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
            FtpHandler ftpHandler = new FtpHandler(hostName, portNumber);

//			Boolean readServerResponse = true;


            for (int len = 1; len > 0;) {
                System.out.print("csftp> ");

                len = System.in.read(cmdString); // adds user input bytes to cmdString
                if (len <= 0) {                  // null check for user input
                    break;
                }

				Command userCommand = new Command(cmdString);
                ftpHandler.executeCommand(userCommand);
			}
				
			ftpHandler.closeSocket();
			

		} catch (UnknownHostException exception) {
			System.err.println("Unknown host");
		} catch (IOException exception) {
			System.err
					.println("998 Input error while reading commands, terminating.");
		}
	}

}
