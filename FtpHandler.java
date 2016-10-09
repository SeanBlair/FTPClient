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
            socket.setSoTimeout(30000); // 30 second timeout

            serverResponse = getControlConnectionResponse();
            System.out.println("<-- " + serverResponse);

            // Set type to binary
            sendCommandToServer("TYPE I");
            System.out.println("<-- " + getControlConnectionResponse());
        } catch (IOException e) {
            // the socket could not be created in 30 seconds
            System.out.format("920 Control connection to %s on port %d failed to open.\n", host, port);
            System.exit(0);
        }
    }

    /**
     * Executes the command by sending it to the server, and prints response; opens a data connection if necessary.
     * @param command the Command object that contains data about what to send to the server
     * @throws IOException if there was a problem communicating with the server
     * @throws ProcessingException if the command required a data connection, the creation of which failed
     */
    public void executeCommand(Command command) throws IOException, ProcessingException {
        String response;

        if (!command.isSilentReturn()) {
            // send command to server
            sendCommandToServer(command.getFtpControlCommand());

            // handle response from server
            response = getControlConnectionResponse();
            System.out.println("<-- " + response);
            serverResponse = response;

            if (command.isDataConnection()) {
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
     * @return the server's response string
     */
    public String getServerResponseString() {
        return serverResponse;
    }

    /**
     * Send a command to the server via the control connection
     * @param command the string representing the full command (plus args if required) to be sent to the server
     */
    private void sendCommandToServer(String command) {
        System.out.println("--> " + command);
        toFtpServer.print(command + "\r\n");
        toFtpServer.flush();
    }

    
    /**
     * Get the full response from the server after sending a command; could be single or multi-line
     * @return serverResponse message string (single or multi line)
     */
    private String getControlConnectionResponse() {
    	String serverResponse = null;
    	String line = null;
    	
    	try {
			line = fromFtpServer.readLine();
			serverResponse = line;
			
			if (line.substring(3, 4).equals("-")){
				String code = line.substring(0, 3) + " ";
				do {
					line = fromFtpServer.readLine();
					serverResponse += line.concat("\n");
				}    while (!(line.contains(code)));
			}
			
		} catch (IOException e) {
			System.out.println("925 Control connection I/O error, closing control connection.");
			try {
				closeSocket();
			} catch (IOException e1) {
				// ignore because exiting program next.
			}
            System.exit(0);
		}	
    	return serverResponse;
    }

    /**
     * Close the control connection
     * @throws IOException if there was a problem closing the socket
     */
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
         * Parse out the IP and port for the data connection from the server's response
         * @param dataResponse the full response string from the server
         * @throws ProcessingException if the regex patterns for IP and port could not be matched in the server response
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
         * Receive either the directory listings, or a file transfer, via the data connection
         */
        public void receiveTransfer(){
            String dataCommand = command.getDataCommand();
            String transferResponse;
            try {
                sendCommandToServer(command.getFtpDataCommand());
                transferResponse = getControlConnectionResponse();
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
                		System.out.println("935 Data transfer connection I/O error, closing data connection.");
                    	// exit data connection because no file to read
                    	return;
                	}	
                } else {
                    throw new InvalidCommandException();
                }

                System.out.println("<-- " + getControlConnectionResponse());           
                 
            } catch (FileNotFoundException fnfe) {
                System.out.format("910 Access to local file %s denied.", command.getDataArgument());
            } catch (IOException ioe) {
                System.out.println("935 Data transfer connection I/O error, closing data connection.");
            }
        }
        

		/**
         * Return true if the file was accessible and will be transferred via the data connection
         * @param transferResponse  response from RETR "filename". 
         * @return  True if can transfer requested file. 
         */
        private boolean fileStatusOK(String transferResponse) {
        	String code = transferResponse.substring(0, 3);
        	
			return code.equals("150") || code.equals("125");
		}

		/**
         * Close the data connection
         * @throws IOException if there was a problem closing the data socket
         */
        public void closeSocket() throws IOException {
            dataSocket.close();
        }
    }
}
