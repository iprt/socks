package org.iproute.dns;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.dns.TcpDnsQueryDecoder;
import org.iproute.dns.handler.DnsProxy;

/**
 * DnsProxyInitializer
 *
 * @author devops@kubectl.net
 */
public class DnsProxyInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new TcpDnsQueryDecoder());
        pipeline.addLast(new TcpDnsQueryDecoder());

        pipeline.addLast(new DnsProxy());

    }

}
