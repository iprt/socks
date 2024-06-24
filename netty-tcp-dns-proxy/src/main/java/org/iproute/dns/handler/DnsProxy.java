package org.iproute.dns.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsSection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DnsProxy
 *
 * @author devops@kubectl.net
 */
@RequiredArgsConstructor
@Slf4j
public class DnsProxy extends SimpleChannelInboundHandler<DnsQuery> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DnsQuery msg) throws Exception {
        DnsQuestion question = msg.recordAt(DnsSection.QUESTION);
        log.info("Query domain: {}", question);
        // todo proxy
    }

}
