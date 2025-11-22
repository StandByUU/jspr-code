package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ResponseSender {
    
    public static void sendNotFoundResponse(BufferedOutputStream out) throws IOException {
        out.write((
            "HTTP/1.1 404 Not Found\r\n" +
            "Content-Length: 0\r\n" +
            "Connection: close\r\n" +
            "\r\n"
        ).getBytes());
        out.flush();
    }

    public static void sendResponse(Request request, BufferedOutputStream out) throws IOException {
        final var filePath = Path.of(".", "public", request.getPath());
        
        if (!Files.exists(filePath)) {
            sendNotFoundResponse(out);
            return;
        }

        // Демонстрация работы с query parameters
        if (request.getPath().equals("/classic.html")) {
            sendClassicHtmlResponse(filePath, request, out);
        } else if (request.getPath().equals("/messages")) {
            sendMessagesResponse(request, out);
        } else {
            sendFileResponse(filePath, out);
        }
    }

    private static void sendClassicHtmlResponse(Path filePath, Request request, BufferedOutputStream out) throws IOException {
        final var mimeType = Files.probeContentType(filePath);
        final var template = Files.readString(filePath);
        
        // Добавляем информацию о query parameters в ответ для демонстрации
        String timeReplacement = LocalDateTime.now().toString();
        Map<String, List<String>> queryParams = request.getQueryParams();
        
        if (!queryParams.isEmpty()) {
            timeReplacement += " | Query params: " + queryParams.toString();
        }
        
        final var content = template.replace("{time}", timeReplacement).getBytes();
        
        out.write((
            "HTTP/1.1 200 OK\r\n" +
            "Content-Type: " + mimeType + "\r\n" +
            "Content-Length: " + content.length + "\r\n" +
            "Connection: close\r\n" +
            "\r\n"
        ).getBytes());
        out.write(content);
        out.flush();
    }

    private static void sendMessagesResponse(Request request, BufferedOutputStream out) throws IOException {
        // Демонстрация работы с query параметрами
        String lastParam = request.getQueryParam("last");
        String limitParam = request.getQueryParam("limit");
        
        StringBuilder responseContent = new StringBuilder();
        responseContent.append("<html><body>");
        responseContent.append("<h1>Messages</h1>");
        responseContent.append("<p>Query Parameters:</p>");
        responseContent.append("<ul>");
        
        Map<String, List<String>> allParams = request.getQueryParams();
        for (Map.Entry<String, List<String>> entry : allParams.entrySet()) {
            for (String value : entry.getValue()) {
                responseContent.append("<li>").append(entry.getKey()).append(": ").append(value).append("</li>");
            }
        }
        
        responseContent.append("</ul>");
        
        // Пример обработки конкретных параметров
        if (lastParam != null) {
            responseContent.append("<p>Last ").append(lastParam).append(" messages requested</p>");
        }
        if (limitParam != null) {
            responseContent.append("<p>Limit: ").append(limitParam).append(" messages per page</p>");
        }
        
        responseContent.append("</body></html>");
        
        byte[] content = responseContent.toString().getBytes();
        
        out.write((
            "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html\r\n" +
            "Content-Length: " + content.length + "\r\n" +
            "Connection: close\r\n" +
            "\r\n"
        ).getBytes());
        out.write(content);
        out.flush();
    }

    private static void sendFileResponse(Path filePath, BufferedOutputStream out) throws IOException {
        final var mimeType = Files.probeContentType(filePath);
        final var length = Files.size(filePath);
        
        out.write((
            "HTTP/1.1 200 OK\r\n" +
            "Content-Type: " + mimeType + "\r\n" +
            "Content-Length: " + length + "\r\n" +
            "Connection: close\r\n" +
            "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }
}
