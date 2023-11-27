package org.iproute.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.iproute.commons.protocol.MsgDecoder;
import org.iproute.commons.protocol.MsgEncoder;

/**
 * ServerInitializer
 *
 * @author zhuzhenjie
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

        ch.pipeline().addLast(new MsgDecoder());
        ch.pipeline().addLast(new MsgEncoder());
        ch.pipeline().addLast(new ConnectHandler());
    }
}
