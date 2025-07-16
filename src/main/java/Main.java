import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.HttpClient;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");


        try {
            ServerSocket serverSocket = new ServerSocket(4221);


            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            Socket clientSocket = serverSocket.accept();
            System.out.println("accepted new connection");

            BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(clientSocket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            String requestMessage = bufferedReader.readLine();
            String responseMessage = null;
            System.out.println("Received request: " + requestMessage);


            // Extract URL Path
            /*if (requestMessage != null && requestMessage.split(" ")[1].startsWith("/")) {
                System.out.println("Extracted URL Path: " + requestMessage.split(" ")[1]);
                responseMessage = "HTTP/1.1 200 OK\r\n\r\n";
            } else {
                responseMessage = "HTTP/1.1 404 Not Found\r\n\r\n";
            }*/

            /**
             * Respond with body: It accept the request, extract the string from url adn return in response body
             */
            if (requestMessage != null && requestMessage.split(" ")[1].startsWith("/")) {
                String url = requestMessage.split(" ")[1];
                String extractedString = url.split("/")[2];
                System.out.println("Extracted String: " + extractedString);
                String contentType = "Content-Type: text/plain\r\n";
                String contentLength = "Content-Length: " + extractedString.length() + "\r\n";
                responseMessage = "HTTP/1.1 200 OK\r\n" + contentType + contentLength + "\r\n" + extractedString;

                /**
                 * // Status line
                 * HTTP/1.1 200 OK
                 * \r\n                          // CRLF that marks the end of the status line
                 *
                 * // Headers
                 * Content-Type: text/plain\r\n  // Header that specifies the format of the response body
                 * Content-Length: 3\r\n         // Header that specifies the size of the response body, in bytes
                 * \r\n                          // CRLF that marks the end of the headers
                 *
                 * // Response body
                 * abc                           // The string from the request
                 */
            }


            System.out.println("Response message: " + responseMessage);
            printWriter.println(responseMessage);


            serverSocket.accept(); // Wait for connection from client.
            System.out.println("accepted new connection");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
