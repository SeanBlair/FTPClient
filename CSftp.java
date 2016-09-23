import java.io.IOException;

//
// This is an implementation of a simplified version of a command 
// line ftp client. The program always takes two arguments
//




public class CSftp
{
    static final int MAX_LEN = 255;
    static final int ARG_CNT = 2;

    static final String PW = "pw";

    public static void main(String [] args) {
        byte cmdString[] = new byte[MAX_LEN];

        // Get command line arguments and connect to FTP
        // If the arguments are invalid or there aren't enough of them
        // then exit.

        if (args.length != ARG_CNT) {
            System.out.print("Usage: cmd ServerAddress ServerPort\n");
            return;
        }

        try {
            for (int len = 1; len > 0;) {
                System.out.print("csftp> ");
                len = System.in.read(cmdString);
                if (len <= 0)
                    break;
                // Start processing the command here.
                StringBuffer buff = new StringBuffer();
                for (int i = 0; i < cmdString.length; i++) {
                    // get all characters up to newline
                    if (cmdString[i] == ('\n')) {
                        break;
                    }
                    buff.append((char) cmdString[i]);
                }
                if (!(buff.length() == 0 || buff.charAt(0) == '#')) {
                    System.out.println(buff.toString());
                }

//				System.out.println("900 Invalid command.");
            }
        } catch (IOException exception) {
            System.err.println("998 Input error while reading commands, terminating.");
        }
    }
}
