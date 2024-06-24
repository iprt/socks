package org.iproute.dns.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DefaultDnsResponse;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.handler.codec.dns.TcpDnsQueryDecoder;
import io.netty.handler.codec.dns.TcpDnsResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * NettyTcpDnsServer
 *
 * @author devops@kubectl.net
 */
public class NettyTcpDnsServer {
    private static final int DNS_SERVER_PORT = 53;
    private static final byte[] QUERY_RESULT = new byte[]{(byte) 192, (byte) 168, 1, 1};

    public static void main(String[] args) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap().group(new NioEventLoopGroup(1),
                        new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new TcpDnsQueryDecoder(), new TcpDnsResponseEncoder());
                        p.addLast(new SimpleChannelInboundHandler<DnsQuery>() {

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx,
                                                        DnsQuery msg) throws Exception {
                                DnsQuestion question = msg.recordAt(DnsSection.QUESTION);
                                System.out.println("Query domain: " + question);
                                // always return 192.168.1.1
                                ctx.writeAndFlush(newResponse(msg, question, 600, QUERY_RESULT));
                            }

                            private DefaultDnsResponse newResponse(DnsQuery query,
                                                                   DnsQuestion question,
                                                                   long ttl, byte[]... addresses) {
                                DefaultDnsResponse response = new DefaultDnsResponse(query.id());
                                response.addRecord(DnsSection.QUESTION, question);

                                for (byte[] address : addresses) {
                                    System.out.println("Question name: " + question.name());
                                    DefaultDnsRawRecord queryAnswer = new DefaultDnsRawRecord(
                                            question.name(),
                                            DnsRecordType.A, ttl, Unpooled.wrappedBuffer(address));
                                    response.addRecord(DnsSection.ANSWER, queryAnswer);
                                }
                                return response;
                            }
                        });
                    }
                });

        final Channel channel = bootstrap.bind(DNS_SERVER_PORT).channel();
        channel.closeFuture().sync();

    }

}
