package ru.netology;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ConnectionHandler implements Runnable {
    private final Socket socket;
    private final List<String> validPaths;

    public ConnectionHandler(Socket socket, List<String> validPaths) {
        this.socket = socket;
        this.validPaths = validPaths;
    }

    @Override
    public void run() {
        try (
            socket;
            final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            processRequest(in, out);
        } catch (IOException e) {
            System.err.println("Error handling connection: " + e.getMessage());
        }
    }

    private void processRequest(BufferedReader in, BufferedOutputStream out) throws IOException {
        final var requestLine = in.readLine();
        if (requestLine == null) {
            return;
        }

        final var parts = requestLine.split(" ");
        if (parts.length != 3) {
            return;
        }

        final var method = parts[0];
        final var fullPath = parts[1];
        
        // Читаем остальные заголовки (пока не используем, но читаем для корректной работы)
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            // Пропускаем заголовки
        }
        
        // Читаем тело запроса если есть (для POST запросов)
        StringBuilder body = new StringBuilder();
        while (in.ready()) {
            body.append((char) in.read());
        }

        Request request = new Request(method, fullPath, body.toString());
        
        // Логируем запрос для отладки
        System.out.println("Processing request: " + request);
        
        // Проверяем путь без query parameters
        if (!validPaths.contains(request.getPath())) {
            ResponseSender.sendNotFoundResponse(out);
            return;
        }

        ResponseSender.sendResponse(request, out);
    }
}
