package org.iproute;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.iproute.handler.HexDumpProxyInitializer;

/**
 * HexDumpProxy
 *
 * @author winterfell
 * @since 2022/7/10
 */
public final class HexDumpProxy {

    static final String LOCAL_HOST = System.getProperty("localHost", "127.0.0.1");
    static final int LOCAL_PORT = Integer.parseInt(System.getProperty("localPort", "3306"));
    static final String REMOTE_HOST = System.getProperty("remoteHost", "172.100.1.100");
    static final int REMOTE_PORT = Integer.parseInt(System.getProperty("remotePort", "3306"));

    public static void main(String[] args) throws Exception {
        // InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        System.err.println("Proxying *:" + LOCAL_PORT + " to " + REMOTE_HOST + ':' + REMOTE_PORT + " ...");

        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HexDumpProxyInitializer(REMOTE_HOST, REMOTE_PORT))
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(LOCAL_HOST, LOCAL_PORT).sync().channel().closeFuture().sync();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
