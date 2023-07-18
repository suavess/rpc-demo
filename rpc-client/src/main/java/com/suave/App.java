package com.suave;

import java.io.IOException;
import java.net.Socket;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        HelloService helloService = RpcProxyClient.getProxy(HelloService.class);
        System.out.println(helloService.sayHello("Suave"));
    }
}
