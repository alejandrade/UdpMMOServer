package io.shrouded;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.shrouded.exceptions.MmoException;
import io.shrouded.recievers.BaseRequest;
import io.shrouded.recievers.PayloadMessageRequest;
import io.shrouded.recievers.RequestMessageType;
import io.shrouded.recievers.MessageReceiver;
import io.shrouded.recievers.BaseResponse;
import io.shrouded.recievers.DefaultMessageResponse;
import io.shrouded.recievers.PayloadMessageResponse;
import io.shrouded.recievers.ResponseMessageType;
import io.shrouded.util.ConnectionManager;
import io.shrouded.util.MDCUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private final ApplicationContext applicationContext;
    private final ConnectionManager connectionManager;
    private final ObjectMapper mapper;
    private final Validator validator;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        final String msg = packet.content().toString(CharsetUtil.UTF_8);
        final InetSocketAddress sender = packet.sender();
        MDCUtil.addClient(sender);
        final UdpConnectionHelper publisherHelper = new UdpConnectionHelper(ctx, connectionManager, sender, mapper);
        try {
            final List<BaseRequest> baseRequests = readAsList(mapper, msg, BaseRequest.class);
            final Set<ConstraintViolation<List<BaseRequest>>> violations = validator.validate(baseRequests);
            if (!violations.isEmpty()) {
                throw new MmoException("Invalid request: " + violations, 400);
            }
            for (BaseRequest base : baseRequests) {
                handleMessage(base, sender, publisherHelper, msg);
            }
        } catch (MmoException iae) {
            log.warn("mmo error: {}", msg, iae);
            publisherHelper.respondSender(new BaseResponse<PayloadMessageResponse>(iae.getRequestId(),
                    Instant.now(),
                    ResponseMessageType.error, iae.getRespondCode(), iae.getMessage(), new DefaultMessageResponse()));
        } catch (JsonProcessingException jp) {
            if (jp.getMessage().startsWith("Unexpected character")) {
                return;
            }
            log.error("Server Error: {}", msg, jp);
            publisherHelper.respondSender(new BaseResponse<PayloadMessageResponse>("no-request-id",
                    Instant.now(),
                    ResponseMessageType.error, 400, jp.getMessage(), new DefaultMessageResponse()));
        } finally {
            MDCUtil.clear();
        }

    }

    private void handleMessage(final BaseRequest base, final InetSocketAddress sender,
                               final UdpConnectionHelper publisherHelper,
                               final String msg) {
        try {
            MDCUtil.addRequestId(base.getRequestId());
            final RequestMessageType type = base.getType();
            final MessageReceiver<PayloadMessageRequest, ?> receiver = (MessageReceiver<PayloadMessageRequest, ?>) applicationContext.getBean(type.getAClass());
            if (receiver.isPrivate() && !connectionManager.userLoggedIn(sender)) {
                throw new MmoException(base.getRequestId(), "access denied", 401);
            }

            receiver.handle(base.getRequestId(), base.getPayload(), publisherHelper);
        } catch (MmoException iae) {
            log.warn("MMO ERROR: {}", msg, iae);
            publisherHelper.respondSender(new BaseResponse<PayloadMessageResponse>(iae.getRequestId(),
                    Instant.now(),
                    ResponseMessageType.error, iae.getRespondCode(), iae.getMessage(), new DefaultMessageResponse()));
        } catch (Exception e) {
            log.error("Server Error: {}", msg, e);
            publisherHelper.respondSender(new BaseResponse<PayloadMessageResponse>("requestFailed",
                    Instant.now(),
                    ResponseMessageType.error, 500, e.getMessage(), new DefaultMessageResponse()));
        }
    }

    private static <T> List<T> readAsList(ObjectMapper mapper, String json, Class<T> clazz) throws JsonProcessingException {
        final JsonNode node = mapper.readTree(json);

        if (node.isArray()) {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } else {
            T single = mapper.treeToValue(node, clazz);
            return List.of(single);
        }
    }
}
