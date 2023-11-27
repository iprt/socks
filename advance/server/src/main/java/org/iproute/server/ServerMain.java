package org.iproute.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.iproute.commons.config.HostPortConfigLoader;
import org.iproute.server.handler.ServerInitializer;

/**
 * ServerMain
 *
 * @author zhuzhenjie
 */
@Slf4j
public class ServerMain {
    static final int PORT = Integer.parseInt(System.getProperty("port",
            String.valueOf(
                    HostPortConfigLoader.instance().getHostPort().getPort()
            )));

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();


            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer());

            // bind port
            ChannelFuture sync = b.bind(PORT).sync();

            sync.addListener((ChannelFutureListener) future -> {
                log.info("ServerMain listen on port {}", PORT);
            });

            sync.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
