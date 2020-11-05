package com.github.bdqfork.core.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * RESP协议编码器
 *
 * @author Trey
 * @since 2020/10/22
 */

public class RESPEncoder extends MessageToMessageEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        //todo 编码
        out.add(msg.getBytes());
    }
}
