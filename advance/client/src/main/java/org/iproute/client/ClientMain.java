package org.iproute.client;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.iproute.client.handler.SocksServerInitializer;

/**
 * ClientMain
 *
 * @author zhuzhenjie
 */
@Slf4j
public class ClientMain {

    static final int PORT = Integer.parseInt(System.getProperty("port", "1080"));

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup;
        EventLoopGroup workerGroup;

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new SocksServerInitializer());

            // bind port
            ChannelFuture sync = b.bind(PORT).sync();

            sync.addListener((ChannelFutureListener) future -> {
                log.info("Client started on port(s): {}", PORT);
            });

            sync.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
