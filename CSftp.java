import java.io.*;
import java.net.*;

//
// This is an implementation of a simplified version of a command 
// line ftp client. The program always takes two arguments
//

public class CSftp {
	static final int MAX_LEN = 255;
	static final int ARG_CNT = 2;

	public static void main(String[] args) throws IOException {
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
			PrintWriter toFtpServer = new PrintWriter(
					mySocket.getOutputStream(), true);
			BufferedReader fromFtpServer = new BufferedReader(
					new InputStreamReader(mySocket.getInputStream()));

			String fromServer;
			Command userCommand;
			Boolean continueLoop = true;
			Boolean readServerResponse = true;
			int len = 1;

			while (continueLoop) {
				if (readServerResponse) {
					// Todo: this will not be sufficient when server's response is more than one line.
					// According to TA, need to iterate through lines, read response codes, then exit.
					fromServer = fromFtpServer.readLine(); 
					System.out.println("<-- " + fromServer); 
				}
				
				System.out.println("csftp> ");

				len = System.in.read(cmdString); // adds user input bytes to cmdString
				if (len <= 0) {                  // null check for user input  
					System.out.println("Null input????");
					break;							
				}

				userCommand = new Command(cmdString);
				String argument = userCommand.getArguments();
				String command = userCommand.getCommand();
				String commandString;
	
				if (command.equals("user")) {
					if (argument == null) { 
						System.out.println("901 Incorrect number of arguments"); // Todo: check if number of arguments != 1
						readServerResponse = false;
					}     else {
						commandString = "USER " + argument;
						toFtpServer.println(commandString);
						System.out.println("--> " + commandString);
						readServerResponse = true;
					}
				} else if (command.equals("pw")) {
					if (argument == null) {
						System.out.println("901 Incorrect number of arguments");
						readServerResponse = false;
					}    else {
						commandString = "PASS " + argument;
						toFtpServer.println(commandString);
						System.out.println("--> " + commandString);
						readServerResponse = true;
					}
				} else if (command.equals("quit")) {
					System.out.println("Bye bye!! :)");
					continueLoop = false;
				} else if (userCommand.isSilentReturn()) {
					readServerResponse = false;
				} else {
					System.out.println("900 Invalid command");
					readServerResponse = false;
				}
			}

			mySocket.close();

		} catch (UnknownHostException exception) {
			System.err.println("Unknown host!!!");
		} catch (IOException exception) {
			System.err
					.println("998 Input error while reading commands, terminating.");
		}
	}

}
