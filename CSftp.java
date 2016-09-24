
import java.io.*;
import java.net.*;


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
        	BufferedReader fromFtpServer = new BufferedReader(                               // socketInputStream	(from FTP server
                    new InputStreamReader(mySocket.getInputStream()));
                	
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));  // consoleImputStream   
            
                String fromServer;
                Command userCommand;
                String userString;
                Boolean continueLoop = true;
                Boolean readServerResponse = true;
                
                while (continueLoop) {
                	if (readServerResponse) {
                		fromServer = fromFtpServer.readLine();  //this will not be sufficient when server's response is more than one line.
                		System.out.println("<-- " + fromServer);     //According to TA, need to iterate through lines, read response codes, then exit.
                	}
                	System.out.println("csftp> ");

                    userString = consoleInput.readLine();
                         if (userString != null){  	
                         	userCommand = new Command(userString.getBytes());
                         	String commandString;
                             if (userCommand.getCommand().equals("user")){
                             	if (userCommand.getArguments() == null){
                             		System.out.println("901 Incorrect number of arguments");
                             		readServerResponse = false;
                             	}    else {
                             	commandString = "USER " + userCommand.getArguments();
                             	toFtpServer.println(commandString);
                             	System.out.println("--> " + commandString);
                             	readServerResponse = true;
                             	}	
                             } else if (userCommand.getCommand().equals("pw")){
                             	if (userCommand.getArguments() == null){
                             		System.out.println("901 Incorrect number of arguments");
                             		readServerResponse = false;
                             	}    else {
                             	commandString = "PASS " + userCommand.getArguments();
                             	toFtpServer.println(commandString);
                             	System.out.println("--> " + commandString);
                             	readServerResponse = true;
                             	}
                             } else if (userCommand.getCommand().equals("quit")) {
                             	System.out.println("Bye bye!! :)");
                             	continueLoop = false;
                             } else if (userCommand.isSilentReturn()) {
                             	readServerResponse = false;
                             }    else {
                             	System.out.println("900 Invalid command");
                             	readServerResponse = false;
                             }
                         }
                     }
    
            
        }  catch (UnknownHostException exception) {
        	System.err.println("Unknown host!!!");
        }  catch (IOException exception) {
            System.err.println("998 Input error while reading commands, terminating.");
        }
    }   

    
    
    
}


