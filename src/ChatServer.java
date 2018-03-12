/* Adapted from code by Siva Naganjaneyulu Polam */
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;



public class ChatServer {
    // Server and Client socket
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    // Create empty ClientThread array with size maxClientsCount
    private static final int maxClientsCount = 10;
    private static final ClientThread[] threads = new ClientThread[maxClientsCount];


    //////////////////////////////////////////////////////////////////////////
    public static void main(String args[]) {

        int portNumber = 2222;


        // Handle command line arguments
        if (args.length < 1) {
            System.out.println("Usage: java ChatServer <portNumber>\n"
                    + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }


        // Set up server socket
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }


        // Give the connection to the ClientThread[] array
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new ClientThread(clientSocket, threads)).start();
                        break;
                    }
                }


                // If max clients has been reached, tell the client and drop them
                if (i == maxClientsCount) {
                    PrintWriter output = new PrintWriter(clientSocket.getOutputStream());
                    output.println("Server full.");
                    output.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
