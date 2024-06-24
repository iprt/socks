package org.iproute.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.dns.DefaultDnsQuery;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DefaultDnsResponse;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.handler.codec.dns.TcpDnsQueryEncoder;
import io.netty.handler.codec.dns.TcpDnsResponseDecoder;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * DnsQueryTest
 *
 * @author devops@kubectl.net
 */
@Slf4j
public class DnsQueryTest {
    private static final String DNS_SERVER_HOST = "114.114.114.114";
    private static final String QUERY_DOMAIN = "www.baidu.com";

    @Test
    public void testDnsQuery() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new TcpDnsQueryEncoder(), new TcpDnsResponseDecoder());
                            p.addLast(new SimpleChannelInboundHandler<DefaultDnsResponse>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, DefaultDnsResponse msg) {
                                    try {
                                        handleQueryResp(msg);
                                    } finally {
                                        ctx.close();
                                    }
                                }

                                private void handleQueryResp(DefaultDnsResponse msg) {
                                    if (msg.count(DnsSection.QUESTION) > 0) {
                                        DnsQuestion question = msg.recordAt(DnsSection.QUESTION, 0);
                                        log.info("name: {}", question.name());
                                    }
                                    for (int i = 0, count = msg.count(DnsSection.ANSWER); i < count; i++) {
                                        DnsRecord record = msg.recordAt(DnsSection.ANSWER, i);
                                        if (record.type() == DnsRecordType.A) {
                                            // just print the IP after query
                                            DnsRawRecord raw = (DnsRawRecord) record;
                                            log.info("Record A|{}", NetUtil.bytesToIpAddress(ByteBufUtil.getBytes(raw.content())));
                                        }
                                    }
                                }

                            });
                        }
                    });

            final Channel ch = b.connect(DNS_SERVER_HOST, 53).sync().channel();

            int randomID = new Random().nextInt(60000 - 1000) + 1000;

            DnsQuery query = new DefaultDnsQuery(randomID, DnsOpCode.QUERY)
                    .setRecord(DnsSection.QUESTION, new DefaultDnsQuestion(QUERY_DOMAIN, DnsRecordType.A));

            ch.writeAndFlush(query).sync();

            boolean success = ch.closeFuture().await(10, TimeUnit.SECONDS);
            if (!success) {
                log.error("dns query timeout!");
                ch.close().sync();
            }
        } finally {
            group.shutdownGracefully();
        }
    }


}
