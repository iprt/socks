package org.iproute.client.handler.advance;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.iproute.commons.protocol.HostPort;
import org.iproute.commons.protocol.Msg;
import org.iproute.commons.protocol.MsgDecoder;
import org.iproute.commons.protocol.MsgEncoder;

@Slf4j
public final class DirectServerHandler extends SimpleChannelInboundHandler<Msg> {

    private final Promise<Channel> promise;

    private final String dstAddr;
    private final int dstPort;

    public DirectServerHandler(Promise<Channel> promise, String dstAddr, int dstPort) {
        this.promise = promise;
        this.dstAddr = dstAddr;
        this.dstPort = dstPort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Msg msg = Msg.builder()
                .connectTag(Msg.ConnectTag.TRY)
                .hostPort(
                        HostPort.builder().host(dstAddr).port(dstPort).build()
                ).build();
        log.info("connect to socks-server success,then writeAndFlush msg|msg is {}", msg.toJson());
        ctx.writeAndFlush(msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        log.info("receive from server |msg = {}", msg.toJson());
        if (Msg.ConnectTag.SUCCESS.equals(msg.getConnectTag())) {
            ctx.pipeline().remove(this);
            ctx.pipeline().remove(MsgDecoder.class);
            ctx.pipeline().remove(MsgEncoder.class);

            // success and set channel then reuse
            promise.setSuccess(ctx.channel());
        } else {
            promise.setFailure(new RuntimeException(msg.toJson()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        log.info("promise set failure");
        promise.setFailure(throwable);
    }

}
