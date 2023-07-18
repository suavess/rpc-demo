package com.suave;

/**
 * Hello world!
 */
public class App {


    public static void main(String[] args) {
        RpcServer.publish(new HelloServiceImpl(), 8080);
    }
}
