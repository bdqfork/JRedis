package com.github.bdqfork.client.handler.codec;

import com.github.bdqfork.client.util.RespUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author Trey
 * @since 2020/10/31
 */

public class RESPDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        String str = new String(bytes);
        out.add(RespUtil.parseResponse(str));
    }
}
