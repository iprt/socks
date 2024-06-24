package org.iproute.dns.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.DnsQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * Dns114
 *
 * @author devops@kubectl.net
 */
@Slf4j
public class Dns114 {

    private static final String dns_server = "114.114.114.114";

    void clientQuery(DnsQuery query, ChannelHandlerContext rtCtx) throws Exception {
        // todo
    }

}
