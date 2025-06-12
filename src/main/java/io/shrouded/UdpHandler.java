package io.shrouded;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.shrouded.exceptions.MmoException;
import io.shrouded.recievers.*;
import io.shrouded.util.ConnectionManager;
import io.shrouded.util.MDCUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final ApplicationContext applicationContext;
    private final ConnectionManager connectionManager;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        final InetSocketAddress sender = packet.sender();
        MDCUtil.addClient(sender);
        final UdpConnectionHelper publisherHelper = new UdpConnectionHelper(ctx, connectionManager, sender);

        try {
            // Grab raw bytes directly
            final byte[] data = new byte[packet.content().readableBytes()];
            packet.content().readBytes(data);

            // Parse single BaseRequest from bytes
            final BaseRequest baseRequest = BaseRequest.fromBytes(data);

            handleMessage(baseRequest, sender, publisherHelper);

        } catch (MmoException iae) {
            log.warn("MMO error: ", iae);
            publisherHelper.respondSender(new BaseResponse<>(ResponseMessageType.error, new ErrorMessageResponse(ErrorCode.invalidRequest)));
        } catch (Exception e) {
            log.error("Server Error: ", e);
            publisherHelper.respondSender(new BaseResponse<>(ResponseMessageType.error,  new ErrorMessageResponse(ErrorCode.serverError)));
        } finally {
            MDCUtil.clear();
        }
    }

    private void handleMessage(final BaseRequest base, final InetSocketAddress sender,
                               final UdpConnectionHelper publisherHelper) {
        try {
            MDCUtil.addRequestId(base.getHash());
            final RequestMessageType type = base.getType();
            final MessageReceiver<PayloadMessageRequest, ?> receiver =
                    (MessageReceiver<PayloadMessageRequest, ?>) applicationContext.getBean(type.getAClass());

            if (receiver.isPrivate() && !connectionManager.userLoggedIn(sender)) {
                throw new MmoException(base.getHash(), "access denied", 401);
            }

            receiver.handle(base.getHash(), base.getPayload(), publisherHelper);
        } catch (MmoException iae) {
            log.warn("MMO error: ", iae);
            publisherHelper.respondSender(new BaseResponse<>(ResponseMessageType.error,
                    new ErrorMessageResponse(ErrorCode.invalidRequest)));
        } catch (Exception e) {
            log.error("Server Error: ", e);
            publisherHelper.respondSender(new BaseResponse<>(ResponseMessageType.error,
                    new ErrorMessageResponse(ErrorCode.serverError)));

        }
    }
}
