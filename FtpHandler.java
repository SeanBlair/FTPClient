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

    private String serverResponse;

    public FtpHandler(String host, int port) {
        try {
            socket = new Socket(host, port);
            toFtpServer = new PrintWriter(socket.getOutputStream(), true);
            fromFtpServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            serverResponse = getCompleteResponseString();
            System.out.println("<-- " + serverResponse);

            // Set type to binary
            sendCommandToServer("TYPE I");
            System.out.println("<-- " + getCompleteResponseString());
        } catch (IOException e) {
            // the socket could not be created
            // TODO timeout on attempt to create connection
            System.out.format("920 Control connection to %s on port %d failed to open.\n", host, port);
            System.exit(0);
        }
    }


    public void executeCommand(Command command) throws IOException, ProcessingException {
        String response;

        if (!command.isSilentReturn()) {
            // send command to server
            sendCommandToServer(command.getFtpControlCommand());

            // handle response from server
            response = getCompleteResponseString();
            System.out.println("<-- " + response);
            serverResponse = response;

            if (command.isDataConnection()) {
                // TODO ensure that server responds positively to data connection request
                DataConnection dataConnection = new DataConnection(response, command);
                dataConnection.receiveTransfer();
                dataConnection.closeSocket();
            }
            if (command.isQuit()) {
                closeSocket();
                System.exit(0);
            }
        }
    }

    /**
     * This is used by FtpHandlerTest.java for validation.
     * Maybe should be called getLastServerResponseString()
     * because there can be other server responses before this one.
     * for example: dir receives at least two responses, this would get the last one...
     */
    public String getServerResponseString() {
        return serverResponse;
    }

    /**
     * Send a command to the server via the control connection
     */
    private void sendCommandToServer(String command) {
        System.out.println("--> " + command);
        toFtpServer.println(command);
    }

    // TODO if readLine throws IOException, catch and print:
    // "925 Control connection I/O error, closing control connection."
    // and exit program.
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

        public DataConnection(String dataResponse, Command command) throws ProcessingException {
            this.command = command;

            try {
                parseResponse(dataResponse);
                dataSocket = new Socket(dataIP, dataPort);
                dataInFromServer = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
                dataSocket.setSoTimeout(30000); // 30 second timeout
            } catch (IOException e) {
                // the socket could not be created or timed out
                System.out.format("930 Data transfer connection to %s on port %d failed to open", dataIP, dataPort);
            }
        }



        /**
         * Parse out the IP and port for the data connection
         * @param dataResponse the full response string from the server
         */
        private void parseResponse(String dataResponse) throws ProcessingException {
            Pattern pattern = Pattern.compile("(\\d{1,3},){5}\\d{1,3}");
            Matcher matcher = pattern.matcher(dataResponse);

            if (matcher.find()) {
                String[] responseArray = matcher.group().split(",");
                dataPort = (Integer.parseInt(responseArray[4]) << 8) + Integer.parseInt(responseArray[5]);
                dataIP = String.join(".", Arrays.copyOfRange(responseArray, 0, 4));
            } else {
                throw new ProcessingException("Could not parse the data IP or port.");
            }
        }

        /**
         * Receive either the directory listings, or a file transfer
         */
        public void receiveTransfer(){
            String dataCommand = command.getDataCommand();
            String transferResponse;
            try {
                sendCommandToServer(command.getFtpDataCommand());
                transferResponse = getCompleteResponseString();
                System.out.println("<-- " + transferResponse);

                if (dataCommand.equals("LIST")) {
                    while (dataInFromServer.readLine() != null) {
                        System.out.println(dataInFromServer.readLine());
                    }
                } else if (dataCommand.equals("RETR")) {
                    // This checks if exists file to read from
                	if (fileStatusOK(transferResponse)) {
                		FileOutputStream fileOut = new FileOutputStream(command.getDataArgument());
                        int next;
                        
                        while ((next = dataInFromServer.read()) != -1) {
                            fileOut.write(next);
                        }
                	} else {
                		// here can check for relevant error messages that
                		// might require specific output by our program.
                		// note that in all cases the user will see the server response message which might
                		// be sufficient to indicate possible errors that our program is not required
                		// to deal with. 
                		
                		System.out.println("935 Data transfer connection I/O error, closing data connection.");
                    	// exit data connection because no file to read
                    	return;
                	}	
                } else {
                    throw new InvalidCommandException();
                }

                System.out.println("<-- " + getCompleteResponseString());
                
            } catch (FileNotFoundException fnfe) {
                System.out.format("910 Access to local file %s denied.", command.getDataArgument());
            } catch (IOException ioe) {
                System.out.println("935 Data transfer connection I/O error, closing data connection.");
            }
        }
        
        
        /**
         * 
         * @param transferResponse  response from RETR "filename". 
         * @return  True if can transfer requested file. 
         */
        private boolean fileStatusOK(String transferResponse) {
        	String code = transferResponse.substring(0, 3);
        	
			return code.equals("150") || code.equals("125");
		}



		/**
         * Close the data connection
         */
        public void closeSocket() throws IOException {
            dataSocket.close();
        }
    }
}
