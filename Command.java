/**
 * Parse user input command and arguments into String(s)
 */
public class Command {

    static final String USER = "USER";
    static final String PASS = "PASS";
    static final String CWD = "CWD";
    static final String QUIT = "QUIT";

    static final String PASV = "PASV";
    static final String LIST = "LIST";
    static final String RETR = "RETR";

    private String trimmedUserInput;
    private String command;
    private String argument;
    private String dataCommand;

    private String dataArgument;

    private boolean isDataConnection;

    public Command(byte[] byteArray) throws WrongNumberOfArgumentsException, InvalidCommandException {
        setTrimmedUserInput(byteArray);
        if (isSilentReturn()) {
            return;
        } else {
            String[] userInputArray = splitUserInput();
            setCommandAndArgument(userInputArray);
            if ((userInputArray.length > 2) || !validInput()) {
                throw new WrongNumberOfArgumentsException();
            }
        }
    }

    /**
     * Given an array of characters from the command line, read as a String,
     * trim leading and trailing white space, and cast to lower case
     */
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
        return (trimmedUserInput.length() == 0 || trimmedUserInput.charAt(0) == '#');
    }

    /**
     * Split command into an array of Strings and trim each part
     */
    public String[] splitUserInput() {
        String[] commandParts = trimmedUserInput.split("\\s+");

        for (String str : commandParts) {
            trimAll(str);
        }
        return commandParts;
    }

    /**
     * Given an array of Strings of user input, set the FTP command and the argument
     */
    public void setCommandAndArgument(String[] commandArray) throws InvalidCommandException {
        parseAndSetCommand(commandArray[0]);
        this.argument = "";
        this.dataArgument = "";
        if (commandArray.length > 1) {
            if (isDataCommand()) {
                this.dataArgument = commandArray[1];
            } else {
                this.argument = commandArray[1];
            }
        }
    }

    /**
     * Set the FTP command based on the user's input
     * @param command User input command
     * @throws InvalidCommandException if the user input a command that is not implemented
     */
    public void parseAndSetCommand(String command) throws InvalidCommandException {
        this.isDataConnection = false;
        this.dataCommand = "";
        switch (command) {
            case ("user"):
                this.command = USER;
                break;
            case ("pw"):
                this.command = PASS;
                break;
            case ("cd"):
                this.command = CWD;
                break;
            case ("dir"):
                this.command = PASV;
                this.dataCommand = LIST;
                this.isDataConnection = true;
                break;
            case ("get"):
                this.command = PASV;
                this.dataCommand = RETR;
                this.isDataConnection = true;
                break;
            case ("quit"):
                this.command = QUIT;
                break;
            default:
                throw new InvalidCommandException();
        }
    }

    /**
     * Removes tabs and leading/trailing whitespace from the given string
     */
    private String trimAll(String string) {
        return string.replaceAll("\t", "").trim();
    }

    /**
     * @return True if the user input has an argument when it should, or doesn't when it shouldn't
     */
    public boolean validInput() {
        if (requiresArgument() && argument.equals("")) {
            return false;
        } else if (!requiresArgument() && !argument.equals("")) {
            return false;
        } else if (requiresDataArgument() && dataArgument.equals("")) {
            return false;
        } else if (!requiresDataArgument() && !dataArgument.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return True if the input command requires an argument
     */
    public boolean requiresArgument() {
        return (command.equals(USER) || command.equals(PASS) || command.equals(CWD));
    }

    /**
     * @return True if user input is a data command
     */
    public boolean isDataCommand() {
        return (command.equals(PASV));
    }


    /**
     * @return True if this is a data command and it requires an argument
     */
    public boolean requiresDataArgument() {
        return dataCommand.equals(RETR);
    }


    /**
     * If command should not silently return, print it to the terminal
     * Convenience method for development
     */
    public void echoToTerminal() {
        if (!isSilentReturn()) {
            System.out.println(trimmedUserInput);
        }
    }

    // *********** Getters and Setters **********

    public String getTrimmedUserInput() {
        return trimmedUserInput;
    }

    public String getCommand() {
        return this.command;
    }

    public String getArgument() {
        return argument;
    }

    public String getDataCommand() {
        return dataCommand;
    }

    public String getDataArgument() {
        return dataArgument;
    }

    public String getFtpControlCommand() {
        if (getArgument().equals("")) {
            return getCommand();
        } else {
            return getCommand() + " " + getArgument();
        }
    }

    public String getFtpDataCommand() {
        if (getDataArgument().equals("")) {
            return getDataCommand();
        } else {
            return getDataCommand() + " " + getDataArgument();
        }
    }

    public void setTrimmedUserInput(byte[] byteArray) {
        this.trimmedUserInput = parseByteArray(byteArray);
    }

    public boolean isDataConnection() {
        return isDataConnection;
    }

    public boolean isQuit() {
        return (command.equals(QUIT));
    }
}
