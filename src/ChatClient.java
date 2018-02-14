/* Adapted from code by Siva Naganjaneyulu Polam */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;


// Class to manage Client chat Box.
public class ChatClient {


    // MAIN //////////////////////////////////////////////////////////////////
    //
    public static void main(String[] args) {
        String server = args[0];
        int port =2222;
        SendAndReceive sendAndReceive = new SendAndReceive();

        JFrame frame = new ChatFrame(sendAndReceive);
        frame.setTitle("MultiChat - connected to " + server + ":" + port);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        try {
            sendAndReceive.initSocket(server,port);
        } catch (IOException ex) {
            System.out.println("Cannot connect to " + server + ":" + port);
            ex.printStackTrace();
            System.exit(0);
        }
    }


    // SendAndReceive //////////////////////////////////////////////////////////////////
    // Set up socket, streams, create thread to Receive messages and method to Send messages
    //
    static class SendAndReceive extends Observable {
        private Socket socket;
        private OutputStream outputStream;


        /* JAVA DOCS REFERENCE
        After an observable instance changes, an application calling the Observable's
        notifyObservers method causes all of its observers to be notified of the change
        by a call to their update method.
         */
        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        // Create socket, output stream and Thread for receiving messages
        public void initSocket(String server, int port) throws IOException {
            socket = new Socket(server, port);
            outputStream = socket.getOutputStream();

            Thread receivingThread = new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader textInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        String message;

                        // Continually take text off of the input stream and notifyObservers of the text
                        while ((message = textInput.readLine()) != null)
                            notifyObservers(message);

                    } catch (IOException e) {
                        notifyObservers(e);
                    }
                }
            };
            receivingThread.start();
        }

        private static final String CRLF = "\r\n"; // newline


        // Send message
        public void sendMessage(String text) {
            try {
                outputStream.write((text + CRLF).getBytes());
                outputStream.flush();

            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }

        // Close socket
        public void close() {
            try {
                socket.close();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }

    // Chat client UI
    static class ChatFrame extends JFrame implements Observer {

        private JTextArea textArea;
        private JTextField inputTextField;
        private JButton sendButton;
        private SendAndReceive sendAndReceive;  // Has notifyObservers

        public ChatFrame(SendAndReceive sendAndReceive) {
            this.sendAndReceive = sendAndReceive;
            sendAndReceive.addObserver(this);
            buildGUI();
        }


        // UI Definition
        private void buildGUI() {
            textArea = new JTextArea(20, 50);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            add(new JScrollPane(textArea), BorderLayout.CENTER);

            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            inputTextField = new JTextField();
            sendButton = new JButton("Send");
            box.add(inputTextField);
            box.add(sendButton);


            // Action for the inputTextField and the goButton
            ActionListener sendListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String str = inputTextField.getText();
                    if (str != null && str.trim().length() > 0)
                        sendAndReceive.sendMessage(str);
                    inputTextField.selectAll();
                    inputTextField.requestFocus();
                    inputTextField.setText("");
                }
            };
            inputTextField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    sendAndReceive.close();
                }
            });
        }


        /** Updates the UI depending on the Object argument */
        public void update(Observable o, Object arg) {
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(finalArg.toString());
                    textArea.append("\n");
                }
            });
        }
    }
}