package io.shrouded;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.shrouded.recievers.request.BaseRequest;
import io.shrouded.recievers.request.PayloadMessageRequest;
import io.shrouded.recievers.RequestMessageType;
import io.shrouded.recievers.MessageReceiver;
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
public class UdpHandler  extends SimpleChannelInboundHandler<DatagramPacket> {
    private final ApplicationContext applicationContext;
    private final ConnectionManager connectionManager;

    private final ObjectMapper mapper;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String msg = packet.content().toString(CharsetUtil.UTF_8);
        InetSocketAddress sender = packet.sender();
        MDCUtil.addClient(sender);

        try {
            final BaseRequest base = mapper.readValue(msg, BaseRequest.class);
            MDCUtil.addRequestId(base.getRequestId());
            final RequestMessageType type = base.getType();
            final MessageReceiver<PayloadMessageRequest, ?> receiver = (MessageReceiver<PayloadMessageRequest, ?>) applicationContext.getBean(type.getAClass());;
            receiver.handle(base.getRequestId(), base.getPayload(),
                    new udpConnectionHelper(ctx, connectionManager, sender, mapper));

        } catch (Exception e) {
            log.warn("Invalid JSON received: {}", msg, e);
        }finally {
            MDCUtil.clear();
        }
    }
}
