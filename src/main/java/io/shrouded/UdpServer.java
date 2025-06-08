package io.shrouded;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UdpServer {

    @Autowired
    private UdpHandler udpHandler;

    @Value("${netty.server.port:9999}")
    private int port;

    @Value("${netty.server.broadcast:true}")
    private boolean broadcast;

    @Value("${netty.server.workerThreads:4}")
    private int workerThreads;

    private NioEventLoopGroup group;

    @PostConstruct
    public void start() {
        group = new NioEventLoopGroup(workerThreads);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, broadcast)
                    .handler(udpHandler);

            b.bind(port).sync();
            log.info("UDP Server started on port {}", port);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start UDP server", e);
        }
    }

    @PreDestroy
    public void stop() {
        if (group != null) {
            group.shutdownGracefully();
            log.info("UDP Server stopped.");
        }
    }
}
