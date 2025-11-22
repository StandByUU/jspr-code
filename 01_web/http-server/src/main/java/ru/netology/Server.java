package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final List<String> validPaths;
    private final int port;
    private final ExecutorService threadPool;
    private volatile boolean isRunning;

    public Server(List<String> validPaths, int port, int threadPoolSize) {
        this.validPaths = validPaths;
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
        this.isRunning = true;
    }

    public void start() {
        try (final var serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            
            while (isRunning) {
                try {
                    final var socket = serverSocket.accept();
                    threadPool.submit(new ConnectionHandler(socket, validPaths));
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        isRunning = false;
        threadPool.shutdown();
        System.out.println("Server stopped");
    }
}
