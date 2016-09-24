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
        //byte cmdString[] = new byte[MAX_LEN];

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
        	PrintWriter toFtpServer = new PrintWriter(mySocket.getOutputStream(), true);  //socketOutputStream   (to FTP server)
        	BufferedReader formFtpServer = new BufferedReader(                               // socketInputStream	(from FTP server
                    new InputStreamReader(mySocket.getInputStream()));
                	
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));  // consoleImputStream   
            
                String fromServer;
                String fromUser;

                while ((fromServer = formFtpServer.readLine()) != null) {       // not sufficient , sometimes responses are more than one line.
                    System.out.println("<-- " + fromServer);                    // need to iterate through lines, read response codes, then exit.
                    System.out.println("csftp> ");
                    
                    fromUser = consoleInput.readLine();
                    if (fromUser != null) {
                        
                        if (fromUser.contains("user"))
                        {
                        	String userCommand = "USER " + fromUser.substring(5);
                        	toFtpServer.println(userCommand);
                        	System.out.println("--> " + userCommand);
                        	
                        }else if (fromUser.contains("pw"))
                        {
                        	String pwCommand = "PASS " + fromUser.substring(3);
                        	toFtpServer.println(pwCommand);
                        	System.out.println("--> " + pwCommand);
                        	
                        }else if (fromUser.contains("quit"))
                        {
                        	System.out.println("Bye bye!! :)");
                        	return;
                        }
                    }
                }
        	
                Command command = new Command(cmdString);
                command.echoToTerminal();

            
        }  catch (UnknownHostException exception) {
        	System.err.println("Unknown host!!!");
        }  catch (IOException exception) {
            System.err.println("998 Input error while reading commands, terminating.");
        }
    }   

    
    
    
}


