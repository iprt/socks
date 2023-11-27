package org.iproute.server.handler.relay;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.iproute.commons.utils.SocksServerUtils;

@Slf4j
public final class ReadClientRelayWebHandler extends ChannelInboundHandlerAdapter {

    private final Channel relayChannel;

    public ReadClientRelayWebHandler(Channel relayChannel) {
        // put client<--->server channel
        this.relayChannel = relayChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (relayChannel.isActive()) {
            // TODO: decrypt msg then writeAndFlush to website
            relayChannel.writeAndFlush(msg);
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (relayChannel.isActive()) {
            SocksServerUtils.closeOnFlush(relayChannel, "ReadClientRelayWebHandler.channelInactive");
        } else {
            log.info("relayChannel is not active: ReadClientRelayWebHandler.channelInactive");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // cause.printStackTrace();
        log.error("exceptionCaught", cause);
        ctx.close();
    }
}
