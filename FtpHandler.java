import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Laura on 2016-09-24.
 */
public class FtpHandler {

    Socket socket;
    PrintWriter toFtpServer;
    BufferedReader fromFtpServer;

    public FtpHandler(String host, int port) {
        try {
            socket = new Socket(host, port);
            toFtpServer = new PrintWriter(socket.getOutputStream(), true);
            fromFtpServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(getCompleteResponseString());
        } catch (IOException e) {
            // the socket could not be created
            // TODO timeout on attempt to create connection
            System.out.format("920 Control connection to %s on port %d failed to open", host, port);
        }
    }


    public void executeCommand(Command command) throws IOException {
        String userInputCommand = command.getCommand();
        String argument = command.getArgument();
        String commandString;
        Socket dataSocket = null;
        boolean dataConnection = false;
        if (!command.isSilentReturn()) {
            switch (userInputCommand) {
                case "user":
                    if (argument == null) {
                        System.out.println("901 Incorrect number of arguments"); // TODO: check if number of arguments != 1
                        return;
                    }
                    commandString = "USER " + argument;
                    break;
                case "pw":
                    if (argument == null) {
                        System.out.println("901 Incorrect number of arguments"); // TODO: check if number of arguments != 1
                        return;
                    }
                    commandString = "PASS " + argument;
                    break;
                case "quit":
                    commandString = "QUIT";  // TODO in this case need to close socket and exit program, after talking to server
                    break;
                // TODO implement rest of commands
//            case "get":
//                break;
                case "cd":
                    if (argument == null) {
                        System.out.println("901 Incorrect number of arguments"); // TODO: check if number of arguments != 1
                        return;
                    }
                    commandString = "CWD " + argument;
                    break;
                case "dir":
                    dataConnection = true;
                    commandString = "LIST";
                    dataSocket = openPassiveDataConnection();
                    break;
                default:
                    System.out.println("900 Invalid command.");
                    // return without contacting server
                    return;
            }
            // send command to server
            sendCommandToServer(commandString);

            // handle response from server
            System.out.println("<-- " + getCompleteResponseString());

            if (dataConnection) {
                BufferedReader fromDataFtpServer = new BufferedReader(
                        new InputStreamReader(dataSocket.getInputStream()));

                // TODO this covers the dir case but will likely be different for get
                while (fromDataFtpServer.readLine() != null) {
                    System.out.println(fromDataFtpServer.readLine());
                }

                System.out.println("<-- " + getCompleteResponseString());
                // transfer complete, close data connection
                dataSocket.close();
            }
            // TODO act on response from server, handle codes, etc
        }
    }

    /**
     * Open a separate data connection with the server
     */
    private Socket openPassiveDataConnection() throws IOException {
        sendCommandToServer("PASV");
        String response = getCompleteResponseString();
        System.out.println("<-- " + response);

        // calculate IP and port to connect to for data connection
        Pattern pattern = Pattern.compile("(\\d{1,3},){5}\\d{1,3}");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            String[] responseArray = matcher.group().split(",");
            int dataPort = (Integer.parseInt(responseArray[4]) << 8) + Integer.parseInt(responseArray[5]);
            String dataIP = String.join(".", Arrays.copyOfRange(responseArray, 0, 4));

            // open new socket for data connection
            Socket dataSocket = new Socket(dataIP, dataPort);
            return dataSocket;
        }
        else {
            // TODO handle case appropriately when no regex match found
            return null;
        }
    }

    /**
     * Send a command to the server via the control connection
     */
    private void sendCommandToServer(String command) {
        System.out.println("--> " + command);
        toFtpServer.println(command);
    }

    private String getCompleteResponseString() throws IOException {
        String serverResponse = null;
        String line = fromFtpServer.readLine();
        serverResponse = line;                         // a response always starts with a 3 digit number. if it is followed
        if (line.substring(3, 4).equals("-")){         // by a dash, it means that there are multiple lines in response
            String code = line.substring(0, 3) + " ";
            do {
                line = fromFtpServer.readLine();
                serverResponse += line;
            }    while (!(line.contains(code)));      // the last line starts with the same 3 digits but followed by a space
        }
        return serverResponse;
    }

    public void closeSocket() throws IOException {
        socket.close();
    }
}
