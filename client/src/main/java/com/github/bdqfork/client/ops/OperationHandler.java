package com.github.bdqfork.client.ops;

import com.github.bdqfork.client.netty.NettyChannel;
import com.github.bdqfork.core.CommandFuture;
import com.github.bdqfork.core.exception.FailedDeserializeException;
import com.github.bdqfork.core.exception.FailedSerializeException;
import com.github.bdqfork.core.exception.JRedisException;
import com.github.bdqfork.core.operation.OperationContext;
import com.github.bdqfork.core.protocol.LiteralWrapper;
import com.github.bdqfork.core.protocol.Type;
import com.github.bdqfork.core.serializtion.JdkSerializer;
import com.github.bdqfork.core.serializtion.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

/**
 * @author bdq
 * @since 2020/11/10
 */
public class OperationHandler implements InvocationHandler {
    private int databaseId;
    private NettyChannel nettyChannel;
    private BlockingQueue<OperationContext> queue;
    private Serializer serializer;

    public OperationHandler(int databaseId, NettyChannel nettyChannel, BlockingQueue<OperationContext> queue) {
        this(databaseId, nettyChannel, queue, new JdkSerializer());
    }

    public OperationHandler(int databaseId, NettyChannel nettyChannel, BlockingQueue<OperationContext> queue, Serializer serializer) {
        this.databaseId = databaseId;
        this.nettyChannel = nettyChannel;
        this.queue = queue;
        this.serializer = serializer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // todo: 根据args对命令进行解析，将参数转换为对应的类型，包括对命令中value参数进行序列化以及对返回值进行反序列化
        String methodName = method.getName();
        OperationContext operationContext = new OperationContext(databaseId, methodName, serialize(methodName,args));

        CommandFuture commandFuture = new CommandFuture();
        operationContext.setResultFuture(commandFuture);

        try {
            queue.put(operationContext);
        } catch (InterruptedException e) {
            throw new JRedisException(e);
        }

        String cmd = encode(operationContext);

        nettyChannel.send(cmd);

        try {
            LiteralWrapper literalWrapper = (LiteralWrapper) commandFuture.get();
            if (literalWrapper.getType() == Type.BULK) {
                return serializer.deserialize(literalWrapper.getData(), Object.class);
            }
            return literalWrapper.getData();
        } catch (InterruptedException | ExecutionException | FailedDeserializeException e) {
            throw new JRedisException(e);
        }
    }

    private Object[] serialize(String method, Object[] args) throws FailedSerializeException {
        if ("set".equals(method)) {
            args[1] = serializer.serialize(args[1]);
        }
        return args;
    }

    private String encode(OperationContext operationContext) {
        LiteralWrapper literalWrapper = encodeArgs(operationContext.getArgs());

        List<LiteralWrapper> literalWrappers = literalWrapper.getData();
        LiteralWrapper cmdLiteralWrapper = LiteralWrapper.singleWrapper();
        cmdLiteralWrapper.setData(operationContext.getCmd());
        literalWrappers.add(0, cmdLiteralWrapper);

        return literalWrapper.encode();
    }

    private LiteralWrapper encodeArgs(Object[] args) {
        List<LiteralWrapper> literalWrappers = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof String) {
                LiteralWrapper singleWrapper = LiteralWrapper.singleWrapper();
                singleWrapper.setData(arg);
                literalWrappers.add(singleWrapper);
            }
            if (arg instanceof Number) {
                LiteralWrapper integerWrapper = LiteralWrapper.integerWrapper();
                integerWrapper.setData(arg);
                literalWrappers.add(integerWrapper);
            }
            if (arg instanceof byte[]) {
                LiteralWrapper bulkWrapper = LiteralWrapper.bulkWrapper();
                bulkWrapper.setData(arg);
                literalWrappers.add(bulkWrapper);
            }
            if (arg instanceof List) {
                List<?> items = (List<?>) arg;
                literalWrappers.add(encodeArgs(items.toArray()));
            }
        }
        LiteralWrapper wrapper = LiteralWrapper.multiWrapper();
        wrapper.setData(literalWrappers);
        return wrapper;
    }

}
