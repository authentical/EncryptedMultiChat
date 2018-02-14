/* Adapted from code by Siva Naganjaneyulu Polam */

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/* TODO
Add System.exit after /quit
Enforce unique username
Reply when no @recipient exists
 */

// A class that defines streams, message routing and
// housekeeping for each connected client
public class ClientThread extends Thread{

    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private int maxClientsCount;

    private String clientName = null;
    private DataInputStream inputStream = null;
    private PrintStream outputStream = null;


    // CONSTRUCTOR ///////////////////////////////////////////////////////////
    public ClientThread(Socket clientSocket, ClientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        ClientThread[] threads = this.threads;


        // Setup streams, Get username from user, tell other's about new user
        try {
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new PrintStream(clientSocket.getOutputStream());


            // Get username and echo
            String name;
            while (true) {
                outputStream.println("Enter your name.");
                name = inputStream.readLine().trim();
                if (name.indexOf('@') == -1) {
                    break;
                } else {
                    outputStream.println("The name should not contain '@' character.");
                }
            }


            // Walk through client list
            // and report new user's connection to everyone else
            outputStream.println("Welcome " + name
                    + " to our chat room.\nTo leave enter /quit in a new line.");
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        clientName = "@" + name;
                        break;
                    }
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].outputStream.println("*** A new user " + name
                                + " entered the chat room !!! ***");
                    }
                }
            }


            // Main chat loop ////////////////////////////////////////////////
            // Read the network in from the client's socket
            // and send it to everyone else
            while (true) {
                String line = inputStream.readLine();
                if (line.startsWith("/quit")) {
                    break;
                }

                // Send message to @"recipient" or
                // Broadcast message to all users and
                // Notify originator that private message was delivered
                if (line.startsWith("@")) {
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty()) {
                            synchronized (this) {
                                for (int i = 0; i < maxClientsCount; i++) {
                                    if (threads[i] != null && threads[i] != this
                                            && threads[i].clientName != null
                                            && threads[i].clientName.equals(words[0])) {
                                        threads[i].outputStream.println("<" + name + "> " + words[1]);
                                        // Notify originator that private message was delivered
                                        this.outputStream.println(">" + name + "> " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                } else {

                    //Broadcast message to all users
                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].outputStream.println("<" + name + "> " + line);
                            }
                        }
                    }
                }
            }

            // After the user says "/quit"
            // Tell everyone and say goodbye to the user
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null) {
                        threads[i].outputStream.println("*** The user " + name
                                + " is leaving the chat room !!! ***");
                    }
                }
            }
            outputStream.println("*** Bye " + name + " ***");

            // Nullify user in the ClientThreads array
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }

            // Close client's streams and socket
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}
