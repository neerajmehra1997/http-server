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
            String responseMessage;
            System.out.println("Received request: " + requestMessage);

            if (requestMessage != null && requestMessage.split(" ")[1].equals("/")) {
                System.out.println("Extracted URL Path: " + requestMessage.split(" ")[1]);
                responseMessage = "HTTP/1.1 200 OK\r\n\r\n";
            } else {
                responseMessage = "HTTP/1.1 404 Not Found\r\n\r\n";
            }
            printWriter.println(responseMessage);


            serverSocket.accept(); // Wait for connection from client.
            System.out.println("accepted new connection");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
