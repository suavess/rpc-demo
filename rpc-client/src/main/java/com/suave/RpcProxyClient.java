package com.suave;

import java.lang.reflect.Proxy;

/**
 * @author Suave
 * @since 2023/07/18 18:35
 */
public class RpcProxyClient {
    public static <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new RpcInvocationHandler());
    }
}
