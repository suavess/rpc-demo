package com.suave;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Suave
 * @since 2023/07/18 20:09
 */
public class RpcServer {
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static void publish(Object service, int port) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try (Socket socket = serverSocket.accept()) {
                EXECUTOR.execute(new ProcessorHandler(socket, service));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
