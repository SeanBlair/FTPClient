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

        // borrowed from
        // http://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoClient.java
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo: " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }

        // end borrowed code

        try {
            for (int len = 1; len > 0;) {
                System.out.print("csftp> ");
                len = System.in.read(cmdString);
                if (len <= 0)
                    break;
                // Start processing the command here.
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < cmdString.length; i++) {
                    // get all characters up to newline
                    if (cmdString[i] == ('\n')) {
                        break;
                    }
                    buffer.append((char) cmdString[i]);
                }
                if (!(buffer.length() == 0 || buffer.charAt(0) == '#')) {
                    System.out.println(buffer.toString());
                }

//				System.out.println("900 Invalid command.");
            }
        } catch (IOException exception) {
            System.err.println("998 Input error while reading commands, terminating.");
        }
    }
}
