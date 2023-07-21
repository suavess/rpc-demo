package com.suave;

import com.alibaba.fastjson2.JSON;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Suave
 * @since 2023/07/18 18:37
 */
public class RpcInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setArgs(args);
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress("localhost", 8080));
            socketChannel.write(ByteBuffer.wrap(JSON.toJSONString(rpcRequest).getBytes()));
            StringBuilder sb = new StringBuilder();
            ByteBuffer byteBuffer = ByteBuffer.allocate(10240);
            socketChannel.read(byteBuffer);
            byteBuffer.flip();
            sb.append(new String(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.limit()));
            byteBuffer.clear();
            return sb.toString();
        }
    }
}
