package org.iproute.server.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.iproute.commons.protocol.Msg;
import org.iproute.commons.protocol.MsgDecoder;
import org.iproute.commons.protocol.MsgEncoder;
import org.iproute.commons.utils.SocksServerUtils;
import org.iproute.server.handler.relay.ReadClientRelayWebHandler;
import org.iproute.server.handler.relay.ReadWebRelayClientHandler;

/**
 * DirectClientConnectHandler
 *
 * @author zhuzhenjie
 */
@Slf4j
public class ConnectHandler extends SimpleChannelInboundHandler<Msg> {

    private final Bootstrap b = new Bootstrap();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener(new FutureListener<Channel>() {

            @Override
            public void operationComplete(Future<Channel> future) throws Exception {
                final Channel websiteChannel = future.getNow();
                if (future.isSuccess()) {
                    ChannelFuture responseFuture = ctx.channel().writeAndFlush(
                            Msg.builder().connectTag(Msg.ConnectTag.SUCCESS).build()
                    );

                    responseFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            ctx.pipeline().remove(ConnectHandler.class);
                            ctx.pipeline().remove(MsgDecoder.class);
                            ctx.pipeline().remove(MsgEncoder.class);

                            websiteChannel.pipeline().addLast(
                                    new ReadWebRelayClientHandler(ctx.channel())
                            );
                            // 客户端收取到的数据直接write到网站
                            ctx.pipeline().addLast(
                                    new ReadClientRelayWebHandler(websiteChannel)
                            );
                        }
                    });
                } else {
                    ctx.channel().writeAndFlush(
                            Msg.builder().connectTag(Msg.ConnectTag.FAILURE).build()
                    );

                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
            }
        });

        final Channel inboundChannel = ctx.channel();
        b.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                             @Override
                             protected void initChannel(SocketChannel ch) throws Exception {
                                 ch.pipeline().addLast(new DirectClientHandler(promise));
                             }
                         }
                );


        log.info("receive from client | msg = {}", msg.toJson());

        String webHost = msg.getHostPort().getHost();
        int webPort = msg.getHostPort().getPort();

        b.connect(webHost, webPort).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // Connection established use handler provided results
                    log.info("Connection established use handler provided results");
                } else {
                    // Close the connection if the connection attempt has failed.
                    log.info("Close the connection if the connection attempt has failed.");
                    ctx.channel().writeAndFlush(
                            Msg.builder().connectTag(Msg.ConnectTag.FAILURE).build()
                    );
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
            }
        });
    }

}
