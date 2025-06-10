package io.shrouded;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.shrouded.data.entity.player.PlayerId;
import io.shrouded.recievers.BaseResponse;
import io.shrouded.recievers.ResponseMessageType;
import io.shrouded.util.ConnectionManager;
import io.shrouded.util.RegisteredClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
@RequiredArgsConstructor
public class UdpConnectionHelper {
    private final ChannelHandlerContext ctx;
    private final ConnectionManager connectionManager;

    @Getter
    private final InetSocketAddress socketAddress;
    private final ObjectMapper mapper;

    public boolean isConnected() {
        return connectionManager.getUserByAddress(socketAddress) != null;
    }

    public RegisteredClient getUser() {
        return connectionManager.getUserByAddress(socketAddress);
    }

    @SneakyThrows(JsonProcessingException.class)
    public void respondSender(final BaseResponse<?> baseResponse) {
        final String json = mapper.writeValueAsString(baseResponse);
        if (!log.isDebugEnabled() && ResponseMessageType.debug.equals(baseResponse.getType())) {
            return;
        }
        ctx.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer(json, CharsetUtil.UTF_8),
                socketAddress
        ));
    }

    public void broadcast(final BaseResponse<?> baseResponse) throws JsonProcessingException {
        final String json = mapper.writeValueAsString(baseResponse);
        if (!log.isDebugEnabled() && ResponseMessageType.debug.equals(baseResponse.getType())) {
            return;
        }
        connectionManager.forEach(((playerId, registeredClient) -> {
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(json, CharsetUtil.UTF_8),
                    registeredClient.address()
            ));
        }));
    }

    public void toUser(PlayerId playerId, final BaseResponse<?> baseResponse) throws JsonProcessingException {
        final RegisteredClient user = connectionManager.getUser(playerId);
        final String json = mapper.writeValueAsString(baseResponse);
        if (!log.isDebugEnabled() && ResponseMessageType.debug.equals(baseResponse.getType())) {
            return;
        }
        ctx.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer(json, CharsetUtil.UTF_8),
                user.address()
        ));
    }
}
