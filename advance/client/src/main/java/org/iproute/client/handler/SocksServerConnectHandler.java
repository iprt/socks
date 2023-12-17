package org.iproute.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.SocksMessage;
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandResponse;
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest;
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import org.iproute.client.handler.advance.DirectServerHandler;
import org.iproute.client.handler.relay.ReadServerRelayUserHandler;
import org.iproute.client.handler.relay.ReadUserRelayServerHandler;
import org.iproute.commons.config.HostPortConfigLoader;
import org.iproute.commons.protocol.HostPort;
import org.iproute.commons.protocol.MsgDecoder;
import org.iproute.commons.protocol.MsgEncoder;
import org.iproute.commons.utils.SocksServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public final class SocksServerConnectHandler extends SimpleChannelInboundHandler<SocksMessage> {

    public static final Logger log = LoggerFactory.getLogger(SocksServerConnectHandler.class);

    private final Bootstrap b = new Bootstrap();

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final SocksMessage message) throws Exception {
        if (message instanceof Socks4CommandRequest request) {
            Promise<Channel> promise = ctx.executor().newPromise();
            promise.addListener(
                    new FutureListener<Channel>() {
                        @Override
                        public void operationComplete(final Future<Channel> future) throws Exception {
                            final Channel serverChannel = future.getNow();
                            if (future.isSuccess()) {
                                ChannelFuture responseFuture = ctx.channel().writeAndFlush(
                                        new DefaultSocks4CommandResponse(Socks4CommandStatus.SUCCESS));

                                responseFuture.addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture channelFuture) {
                                        ctx.pipeline().remove(SocksServerConnectHandler.this);

                                        // read的 server的数据直接通过 ctx.channel() write出去到用户
                                        serverChannel.pipeline().addLast(
                                                new ReadServerRelayUserHandler(ctx.channel())
                                        );

                                        // read的 用户的数据直接通过 serverChannel write 出去到 server
                                        ctx.pipeline().addLast(
                                                new ReadUserRelayServerHandler(serverChannel)
                                        );
                                    }
                                });
                            } else {
                                ctx.channel().writeAndFlush(
                                        new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED));
                                SocksServerUtils.closeOnFlush(ctx.channel());
                            }
                        }
                    });
            doConnect(ctx, promise,
                    request.dstAddr(), request.dstPort(),
                    new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED)
            );

        } else if (message instanceof Socks5CommandRequest request) {
            Promise<Channel> promise = ctx.executor().newPromise();
            promise.addListener(
                    new FutureListener<Channel>() {
                        @Override
                        public void operationComplete(final Future<Channel> future) throws Exception {
                            final Channel serverChannel = future.getNow();
                            if (future.isSuccess()) {
                                ChannelFuture responseFuture =
                                        ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                                                Socks5CommandStatus.SUCCESS,
                                                request.dstAddrType(),
                                                request.dstAddr(),
                                                request.dstPort()));

                                responseFuture.addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture channelFuture) {
                                        ctx.pipeline().remove(SocksServerConnectHandler.this);

                                        // read的 server的数据直接通过 ctx.channel() write出去到用户
                                        serverChannel.pipeline().addLast(
                                                new ReadServerRelayUserHandler(ctx.channel())
                                        );

                                        // read的 用户的数据直接通过 serverChannel write 出去到 server
                                        ctx.pipeline().addLast(
                                                new ReadUserRelayServerHandler(serverChannel)
                                        );
                                    }
                                });
                            } else {
                                ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                                        Socks5CommandStatus.FAILURE, request.dstAddrType()));
                                SocksServerUtils.closeOnFlush(ctx.channel());
                            }
                        }
                    });

            doConnect(ctx, promise,
                    request.dstAddr(), request.dstPort(),
                    new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType())
            );

        } else {
            ctx.close();
        }
    }


    private void doConnect(ChannelHandlerContext ctx, Promise<Channel> promise,
                           String dstAddr, int dstPort,
                           Object failedResponse) {

        final Channel inboundChannel = ctx.channel();
        b.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MsgDecoder());
                        ch.pipeline().addLast(new MsgEncoder());
                        ch.pipeline().addLast(
                                new DirectServerHandler(promise, dstAddr, dstPort)
                        );
                    }
                });

        HostPort serverConfig = HostPortConfigLoader.instance().getHostPort();

        b.connect(serverConfig.getHost(), serverConfig.getPort()).addListener(
                new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            // Connection established use handler provided results
                            log.info("Connection established use handler provided results");
                        } else {
                            // Close the connection if the connection attempt has failed.
                            log.info("Close the connection if the connection attempt has failed.");
                            // ctx.channel().writeAndFlush(
                            //         new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED)
                            // );
                            ctx.writeAndFlush(failedResponse);
                            SocksServerUtils.closeOnFlush(ctx.channel());
                        }
                    }
                });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocksServerUtils.closeOnFlush(ctx.channel());
    }
}
