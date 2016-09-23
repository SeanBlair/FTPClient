import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static void main(String [] args) {
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
        	PrintWriter toFtpServer = new PrintWriter(mySocket.getOutputStream(), true);  
        	BufferedReader fromFtpServer = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
        	
            for (int len = 1; len > 0;) {
                System.out.print("csftp> ");
                len = System.in.read(cmdString);
                if (len <= 0)
                    break;
                // Start processing the command here.
                Command command = new Command(cmdString);
                command.echoToTerminal();

            }
            
        }  catch (UnknownHostException exception) {
        	System.err.println("Unknown host!!!");
        }  catch (IOException exception) {
            System.err.println("998 Input error while reading commands, terminating.");
        } catch (IllegalArgumentException exception) {
				System.out.println("900 Invalid command.");
        }
    }   
}


