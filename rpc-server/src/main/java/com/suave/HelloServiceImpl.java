package com.suave;

/**
 * @author Suave
 * @since 2023/07/18 18:22
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public HelloBean sayHello(String content) {
        return new HelloBean("hello " + content);
    }
}
