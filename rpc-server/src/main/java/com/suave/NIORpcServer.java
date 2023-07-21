package com.suave;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Suave
 * @since 2023/07/21 12:25
 */
public class NIORpcServer implements Runnable {
    private Selector selector;

    private Object service;

    private int port;

    public NIORpcServer(Object service, int port) {
        try {
            this.selector = Selector.open();
            this.service = service;
            this.port = port;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress("localhost", port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    try {
                        if (next.isValid()) {
                            if (next.isAcceptable()) {
                                handleAccept(next);
                            } else if (next.isReadable()) {
                                handleRead(next);
                            }
                        }
                    } catch (Exception e) {
                        next.cancel();
                        next.channel().close();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        System.out.println("接收到连接请求啦！");
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void handleRead(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        StringBuilder sb = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (socketChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            sb.append(new String(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.limit()));
            byteBuffer.clear();
        }
        RpcRequest rpcRequest = JSON.parseObject(sb.toString(), RpcRequest.class, JSONReader.Feature.SupportClassForName);
        Class<?> clazz = Class.forName(rpcRequest.getClassName());
        Method method = clazz.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        Object result = method.invoke(service, rpcRequest.getArgs());
        socketChannel.write(ByteBuffer.wrap(result.toString().getBytes()));
        System.out.println("发送RPC结果完成");
    }
}
