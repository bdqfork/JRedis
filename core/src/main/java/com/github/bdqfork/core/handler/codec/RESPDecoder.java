package com.github.bdqfork.core.handler.codec;

import com.github.bdqfork.core.util.RespUtils;
import com.github.bdqfork.core.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * RESP协议解码器
 *
 * @author Trey
 * @since 2020/10/22
 */

public class RESPDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        String str = new String(bytes);
        out.add(RespUtils.parserArray(str).get("res"));
    }
}
