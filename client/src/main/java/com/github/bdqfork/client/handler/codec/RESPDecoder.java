package com.github.bdqfork.client.handler.codec;

import com.github.bdqfork.client.util.RespUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author Trey
 * @since 2020/10/31
 */

public class RESPDecoder extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        out.add(RespUtil.parseResponse(msg));
    }
}
