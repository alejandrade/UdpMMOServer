package io.shrouded;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.shrouded.data.player.PlayerId;
import io.shrouded.recievers.response.BaseResponse;
import io.shrouded.util.ConnectionManager;
import io.shrouded.util.RegisteredClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.InetSocketAddress;

@RequiredArgsConstructor
public class udpConnectionHelper {
    private final ChannelHandlerContext ctx;
    private final ConnectionManager connectionManager;

    @Getter
    private final InetSocketAddress sender;
    private final ObjectMapper mapper;

    public boolean isRegistered() {
        return connectionManager.getUserByAddress(sender) != null;
    }

    public void respondSender(final BaseResponse<?> baseResponse) throws JsonProcessingException {
        final String json = mapper.writeValueAsString(baseResponse);
        ctx.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer(json, CharsetUtil.UTF_8),
                sender
        ));
    }

    public void broadcast(final BaseResponse<?> baseResponse) throws JsonProcessingException {
        final String json = mapper.writeValueAsString(baseResponse);
        connectionManager.forEach(((playerId, registeredClient) -> {
            ctx.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(json, CharsetUtil.UTF_8),
                    registeredClient.address()
            ));
        }));
    }

    public void toUser(PlayerId playerId, final BaseResponse<?> baseResponse) throws JsonProcessingException {
        RegisteredClient user = connectionManager.getUser(playerId);
        final String json = mapper.writeValueAsString(baseResponse);
        ctx.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer(json, CharsetUtil.UTF_8),
                user.address()
        ));
    }
}
