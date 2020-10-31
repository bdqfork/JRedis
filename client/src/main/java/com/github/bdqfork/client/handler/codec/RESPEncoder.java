package com.github.bdqfork.client.handler.codec;

import com.github.bdqfork.client.util.RespUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RESP编码器
 *
 * @author Trey
 * @since 2020/10/31
 */

public class RESPEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        String command = RespUtil.parseString(msg);
        out.writeBytes(command.getBytes());
    }
}
