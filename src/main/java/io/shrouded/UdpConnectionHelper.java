package io.shrouded;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.shrouded.data.entity.player.PlayerId;
import io.shrouded.recievers.BaseResponse;
import io.shrouded.recievers.ResponseMessageType;
import io.shrouded.util.ConnectionManager;
import io.shrouded.util.RegisteredClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
@RequiredArgsConstructor
public class UdpConnectionHelper {

    private final ChannelHandlerContext ctx;
    private final ConnectionManager connectionManager;

    @Getter
    private final InetSocketAddress socketAddress;

    public boolean isConnected() {
        return connectionManager.getUserByAddress(socketAddress) != null;
    }

    public RegisteredClient getUser() {
        return connectionManager.getUserByAddress(socketAddress);
    }

    public void respondSender(final BaseResponse<?> baseResponse) {
        if (!log.isDebugEnabled() && ResponseMessageType.debug.equals(baseResponse.getType())) {
            return;
        }
        final byte[] data = baseResponse.toBytes();
        ctx.writeAndFlush(new DatagramPacket(
                Unpooled.wrappedBuffer(data),
                socketAddress
        ));
    }

    public void broadcast(final BaseResponse<?> baseResponse) {
        if (!log.isDebugEnabled() && ResponseMessageType.debug.equals(baseResponse.getType())) {
            return;
        }
        final byte[] data = baseResponse.toBytes();
        connectionManager.forEach(((playerId, registeredClient) -> {
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.wrappedBuffer(data),
                    registeredClient.address()
            ));
        }));
    }

    public void toUser(PlayerId playerId, final BaseResponse<?> baseResponse) {
        if (!log.isDebugEnabled() && ResponseMessageType.debug.equals(baseResponse.getType())) {
            return;
        }
        final RegisteredClient user = connectionManager.getUser(playerId);
        if (user == null) {
            log.warn("Attempted to send message to unknown user: {}", playerId);
            return;
        }
        final byte[] data = baseResponse.toBytes();
        ctx.writeAndFlush(new DatagramPacket(
                Unpooled.wrappedBuffer(data),
                user.address()
        ));
    }
}
