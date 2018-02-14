/* Adapted from code by Siva Naganjaneyulu Polam */
import javax.swing.*;


//////////////////////////////////////////////////////////////////////////
// JOptionPane providing Server or Client

public class ServerClientSelection {


    public static void main(String [] args){

        Object[] clientOrServer = { "Server","Client"};
        String defaultSelection = "Server";


        Object selection = JOptionPane.showInputDialog(
                null,
                "Login as : ",
                "MyChatApp",
                JOptionPane.QUESTION_MESSAGE,
                null,
                clientOrServer,
                defaultSelection);


        if(selection.equals("Server")){
            String[] arguments = new String[] {};
            new ChatServer().main(arguments);
        }else if(selection.equals("Client")){
            String IPServer = JOptionPane.showInputDialog("Enter the Server ip adress");
            String[] arguments = new String[] {IPServer};
            new ChatClient().main(arguments);
        }

    }

}
