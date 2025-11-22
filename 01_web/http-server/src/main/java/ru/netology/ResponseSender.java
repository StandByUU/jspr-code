package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

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

    public static void sendResponse(String path, BufferedOutputStream out) throws IOException {
        final var filePath = Path.of(".", "public", path);
        
        if (!Files.exists(filePath)) {
            sendNotFoundResponse(out);
            return;
        }

        if (path.equals("/classic.html")) {
            sendClassicHtmlResponse(filePath, out);
        } else {
            sendFileResponse(filePath, out);
        }
    }

    private static void sendClassicHtmlResponse(Path filePath, BufferedOutputStream out) throws IOException {
        final var mimeType = Files.probeContentType(filePath);
        final var template = Files.readString(filePath);
        final var content = template.replace("{time}", LocalDateTime.now().toString()).getBytes();
        
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
