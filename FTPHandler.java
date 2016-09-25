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
        System.out.println(getCompleteResponseString());
    }


	public void executeCommand(Command command) throws IOException {
        String userInputCommand = command.getCommand();
        String argument = command.getArgument();
        String commandString;
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
//            case "cd":
//                break;
//                case "dir":
//                    break;
                default:
                    System.out.println("900 Invalid command.");
                    // return without contacting server
                    return;
            }
            // send command to server
            System.out.println("--> " + commandString);
            toFtpServer.println(commandString);

            // handle response from server
            System.out.println("<-- " + getCompleteResponseString());
            // TODO act on response from server, handle codes, etc
        }
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
