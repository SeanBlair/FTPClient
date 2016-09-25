/**
 * Parse user input command and arguments into String(s)
 */
public class Command {

    private String fullCommand;
    private String command;
    private String argument;

    public Command(byte[] byteArray) {
        setFullCommand(byteArray);
        setCommandAndArgument();
    }

    /**
     * Split command into an array on whitespace and trim each part
     */
    public String[] splitFullCommand() {
        String[] commandParts = fullCommand.split("\\s+");

        for (String str : commandParts) {
            trimAll(str);
        }
        return commandParts;
    }

    /**
     * If command should not silently return, print it to the terminal
     * Convenience method for development
     */
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
        return trimAll(buffer.toString().toLowerCase());
    }

    /**
     * @return true if the command is the newline char or begins with '#'
     */
    public boolean isSilentReturn() {
        return (fullCommand.length() == 0 || fullCommand.charAt(0) == '#');
    }

    /**
     * Removes tabs and leading/trailing whitespace from the given string
     */
    private String trimAll(String string) {
        return string.replaceAll("\t", "").trim();
    }

    // *********** Getters and Setters **********

    public String getFullCommand() {
        return fullCommand;
    }

    public void setFullCommand(byte[] byteArr) {
        this.fullCommand = parseByteArray(byteArr);
        setCommandAndArgument();
    }
    
    private void setCommandAndArgument() {
        String[] commandParts = splitFullCommand();
        // TODO validate the command format and notify user if there were too many arguments, or other problems
        this.command = commandParts[0];
        if (commandParts.length > 1) {
            this.argument = commandParts[1];
        } else {
            this.argument = null;
        }
    }

    public String getCommand() {
        return command;
    }

    public String getArgument() {
        return argument;
    }
}
