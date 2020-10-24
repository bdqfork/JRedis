package com.github.bdqfork.core.handler.codec;

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
        StringBuffer msgBuf = new StringBuffer();
        msgBuf.append(msg);
        //获取字符串数组长度
        int arrayLength = Integer.parseInt(
                StringUtils.getSubstringBetweenStrings("*","\r\n",msgBuf));

        String[] command = new String[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            //获取字符串长度，将字符串中对应的字符加入命令数组
            int strLength = Integer.parseInt(
                   StringUtils.getSubstringBetweenStrings("$","\r\n",msgBuf));
            String partOfCommand = msgBuf.substring(0, strLength);
            command[i] = partOfCommand;
            msgBuf.delete(0, strLength + "\r\n".length());
        }
        out.add(command);
    }
}
