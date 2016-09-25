import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Laura on 2016-09-24.
 */
public class FtpHandler {

    Socket socket;
    PrintWriter toFtpServer;
    BufferedReader fromFtpServer;

    public FtpHandler(String host, int port) throws IOException {
        socket = new Socket(host, port);
        toFtpServer = new PrintWriter(socket.getOutputStream(), true);
        fromFtpServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // confirm connection and clear input buffer
        // Todo: this will not be sufficient when server's response is more than one line.
        // According to TA, need to iterate through lines, read response codes, then exit.
        System.out.println(fromFtpServer.readLine());
    }

    public void executeCommand(Command command) throws IOException {
        String userInputCommand = command.getCommand();
        String argument = command.getArgument();
        String commandString;
        switch (userInputCommand) {
            case "user":
                if (argument == null) {
                    System.out.println("901 Incorrect number of arguments"); // Todo: check if number of arguments != 1
                    return;
                }
                commandString = "USER " + argument;
                break;
            case "pw":
                if (argument == null) {
                    System.out.println("901 Incorrect number of arguments"); // Todo: check if number of arguments != 1
                    return;
                }
                commandString = "PASS " + argument;
                break;
            case "quit":
                commandString = "QUIT";
                break;
            // TODO implement rest of commands
//            case "get":
//                break;
//            case "cd":
//                break;
//            case "dir":
//                break;
// TODO implement "silently ignored case" (new prompt displayed)                
            default:
                System.out.println("900 Invalid command.");
                // return without contacting server
                return;
        }
        // send command to server
        System.out.println("--> " + commandString);
        toFtpServer.println(commandString);

        // handle response from server
        System.out.println("<-- " + fromFtpServer.readLine());
        // TODO act on response from server, handle codes, etc
    }

    public void closeSocket() throws IOException {
        socket.close();
    }
}
