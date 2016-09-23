//import java.io.IOException;
import java.io.*;
import java.net.*;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

//
// This is an implementation of a simplified version of a command 
// line ftp client. The program always takes two arguments
//




public class CSftp
{
    static final int MAX_LEN = 255;
    static final int ARG_CNT = 2;

    public static void main(String [] args) throws IOException {
        byte cmdString[] = new byte[MAX_LEN];

        // Get command line arguments and connect to FTP
        // If the arguments are invalid or there aren't enough of them
        // then exit.

        if (args.length != ARG_CNT) {
            System.out.print("Usage: cmd ServerAddress ServerPort\n");
            return;
        }
        
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try {
        	Socket mySocket = new Socket(hostName, portNumber);
        	PrintWriter toServer = new PrintWriter(mySocket.getOutputStream(), true);  //socketOutputStream   (to FTP server)
        	BufferedReader toMe = new BufferedReader(                               // socketInputStream	(from FTP server
                    new InputStreamReader(mySocket.getInputStream()));
        
        	
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));  // consoleImputStream   
            
                String fromServer;
                String fromUser;

                while ((fromServer = in.readLine()) != null) {       // not sufficient , sometimes responses are more than one line.
                    System.out.println("Server: " + fromServer);     // need to iterate through lines, read response codes, then exit.
                    if (fromServer.equals("Bye."))
                        break;
                    
                    fromUser = stdIn.readLine();
                    if (fromUser != null) {
                        System.out.println("Client: " + fromUser);
                        
                        if (fromUser.contains("user"))
                        {
                        	out.println("USER " + fromUser.substring(5));
                        }else if (fromUser.contains("pw"))
                        {
                        	out.println("PASS " + fromUser.substring(5));
                        }else if (fromUser.contains("quit"))
                        {
                        	return;
                        }
                        //out.println(fromUser);
                    }
                }
        	
            for (int len = 1; len > 0;) {
                System.out.print("csftp> ");
                len = System.in.read(cmdString);
                if (len <= 0)
                    break;
                // Start processing the command here.
                Command command = new Command(cmdString);
                command.echoToTerminal();
                

				System.out.println("900 Invalid command.");
            }
            
        }  catch (UnknownHostException exception) {
        	System.err.println("Unknown host!!!");
        }  catch (IOException exception) {
            System.err.println("998 Input error while reading commands, terminating.");
        }
    }   

    
    
    
}


