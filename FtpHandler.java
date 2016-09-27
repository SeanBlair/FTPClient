import java.io.*;
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
        boolean isDataConnection = false;
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
                    isDataConnection = true;
                    commandString = "PASV";
                    break;
                default:
                    System.out.println("900 Invalid command.");
                    // return without contacting server
                    return;
            }
            // send command to server
            sendCommandToServer(commandString);

            // handle response from server
            String response = getCompleteResponseString();
            System.out.println("<-- " + response);

            if (isDataConnection) {
                DataConnection dataConnection = new DataConnection(response, command);
                dataConnection.receiveTransfer();
                dataConnection.closeSocket();
            }
            // TODO act on response from server, handle codes, etc
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
                serverResponse += line.concat("\n");
            }    while (!(line.contains(code)));      // the last line starts with the same 3 digits but followed by a space
        }
        return serverResponse;
    }

    public void closeSocket() throws IOException {
        socket.close();
    }


    /**
     * Private class for data connections, opened only in conjunction with a control connection
     * and when user commands dictate
     */
    private class DataConnection {

        private BufferedReader dataInFromServer;

        private Command command;

        private String dataIP;
        private int dataPort;
        private Socket dataSocket;

        public DataConnection(String dataResponse, Command command) {
            this.command = command;
            parseResponse(dataResponse);

            try {
                dataSocket = new Socket(dataIP, dataPort);
                dataInFromServer = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            } catch (IOException e) {
                // the socket could not be created
                // TODO timeout on attempt to create connection
                System.out.format("930 Data transfer connection to %s on port %d failed to open", dataIP, dataPort);
            }
        }

        /**
         * Parse out the IP and port for the data connection
         * @param dataResponse the full response string from the server
         */
        private void parseResponse(String dataResponse) {
            Pattern pattern = Pattern.compile("(\\d{1,3},){5}\\d{1,3}");
            Matcher matcher = pattern.matcher(dataResponse);

            if (matcher.find()) {
                String[] responseArray = matcher.group().split(",");
                dataPort = (Integer.parseInt(responseArray[4]) << 8) + Integer.parseInt(responseArray[5]);
                dataIP = String.join(".", Arrays.copyOfRange(responseArray, 0, 4));
                System.out.println("data IP: " + dataIP);
                System.out.println("data port: " + dataPort);
            } else {
                // TODO handle case appropriately when no regex match found
            }
        }

        /**
         * Receive either the directory listings, or a file transfer
         */
        public void receiveTransfer() throws IOException {
            String commandString = command.getCommand();
            System.out.println("command string is: " + commandString);
            if (commandString.equals("dir")) {

                sendCommandToServer("LIST");
                System.out.println("<-- " + getCompleteResponseString());

                while (dataInFromServer.readLine() != null) {
                    System.out.println(dataInFromServer.readLine());
                }

                // transfer complete, close data connection
//                dataSocket.close();
            } else if (command.getCommand().equals("GET")) {
                // TODO implement get command
            } else {
                System.out.println("900 Invalid command.");
                return;
            }
            System.out.println("<-- " + getCompleteResponseString());
        }

        /**
         * Close the data connection
         */
        public void closeSocket() throws IOException {
            dataSocket.close();
        }
    }
}
