/**
 * Parse user input command and arguments into String(s) and handle the result
 */
public class Command {

    private String fullCommand;
    private String command;
    private String arguments;

    public Command(byte[] byteArray) {
        this.fullCommand = parseByteArray(byteArray);
    }

    public void echoToTerminal() {
        if (!isSilentReturn()) {
            System.out.println(fullCommand);
        }
    }

    private String parseByteArray(byte[] byteArray) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            // get all characters up to newline
            if (byteArray[i] == ('\n')) {
                break;
            }
            buffer.append((char) byteArray[i]);
        }
        return buffer.toString();
    }

    private boolean isSilentReturn() {
        return (fullCommand.length() == 0 || fullCommand.charAt(0) == '#');
    }

    // *********** Getters and Setters **********
    public String getFullCommand() {
        return fullCommand;
    }

    public void setFullCommand(byte[] byteArr) {
        this.fullCommand = parseByteArray(byteArr);
    }

    public String getCommand() {
        // TODO
        return null;
    }

    public String getArguments() {
        // TODO
        return null;
    }
}
