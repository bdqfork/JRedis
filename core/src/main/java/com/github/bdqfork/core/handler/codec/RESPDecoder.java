package com.github.bdqfork.core.handler.codec;

import com.github.bdqfork.core.util.RespUtils;
import com.github.bdqfork.core.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * RESP协议解码器
 *
 * @author Trey
 * @since 2020/10/22
 */

public class RESPDecoder extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        out.add(RespUtils.parserArray(msg).get("res"));
    }
}
