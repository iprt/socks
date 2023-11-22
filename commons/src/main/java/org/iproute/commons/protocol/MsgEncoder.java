package org.iproute.commons.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * MsgEncoder 编码器
 *
 * @author zhuzhenjie
 * @since 2022/8/7
 */
public class MsgEncoder extends MessageToByteEncoder<Msg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Msg msg, ByteBuf out) throws Exception {

        String json = msg.toJson();

        byte[] bytes = json.getBytes();

        out.writeInt(bytes.length);
        out.writeBytes(bytes);

    }
}
