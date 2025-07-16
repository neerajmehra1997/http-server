import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int PORT = 4221;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        System.out.println("Server starting on port " + PORT);
        AtomicInteger threadCount = new AtomicInteger(0);

        // Ensure the server can reuse the address

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                 //   System.out.println("Accepted new connection");
                    pool.submit(() -> {
                        System.out.println("Accepted new connection by thread" + threadCount.getAndIncrement());
                        handleClient(clientSocket);
                    });
                } catch (IOException e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT + ": " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (clientSocket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream output = clientSocket.getOutputStream()) {


            String requestLine = reader.readLine();
            System.out.println("Received request: " + requestLine);

            if (requestLine != null) {
                String response = buildResponse(requestLine);
                output.write(response.getBytes());
                output.flush();
            }
        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
        }
    }

    private static String buildResponse(String requestLine) {
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 2 || !requestParts[1].startsWith("/")) {
            return buildErrorResponse();
        }

        String path = requestParts[1];
        String[] pathParts = path.split("/");

        if (pathParts.length < 3) {
            return buildErrorResponse();
        }

        String content = pathParts[2];
        return buildSuccessResponse(content);
    }

    private static String buildSuccessResponse(String content) {
        return String.format("HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: %d\r\n" +
                "\r\n" +
                "%s", content.length(), content);
    }

    private static String buildErrorResponse() {
        return "HTTP/1.1 404 Not Found\r\n\r\n";
    }
}