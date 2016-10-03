import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.fail;

/**
 * Created by Laura on 2016-09-23.
 */
public class CommandTest {

    private byte[] SINGLE_COMMAND_ARRAY = {'q', 'u', 'i', 't'};
    private byte[] COMMAND_WITH_ARGS_ARRAY = {'u', 's', 'e', 'r', ' ', 'n', 'a', 'm', 'e'};
    private byte[] COMMAND_TOO_FEW_ARGS = {'u', 's', 'e', 'r'};
    private byte[] COMMAND_TOO_MANY_ARGS = {'u', 's', 'e', 'r', ' ', 'n', 'a', 'm', 'e', ' ', 'e', 'x', 't', 'r', 'a'};
//    private byte[] INVALID_COMMAND = {'w', 'r', 'o', 'n', 'g'};

    private String SINGLE_COMMAND_STRING = new String(SINGLE_COMMAND_ARRAY);
    private String COMMAND_WITH_ARGS_STRING = new String(COMMAND_WITH_ARGS_ARRAY);
    private String COMMAND_ALONE = "USER";
    private String ARGUMENT_ALONE = "name";

    Command command;

    @Before
    public void setUp() throws WrongNumberOfArgumentsException, InvalidCommandException {
        command = new Command(SINGLE_COMMAND_ARRAY);
    }

    @Test
    public void testSingleCommand() {
        assert(command.getTrimmedUserInput().equals(SINGLE_COMMAND_STRING));
    }

    @Test
    public void testCommandWithArgs() throws WrongNumberOfArgumentsException, InvalidCommandException {
        Command command = new Command(COMMAND_WITH_ARGS_ARRAY);
        assert(command.getTrimmedUserInput().equals(COMMAND_WITH_ARGS_STRING));
        assert(command.getCommand().equals(COMMAND_ALONE));
        assert(command.getArgument().equals(ARGUMENT_ALONE));
    }

    @Test(expected=WrongNumberOfArgumentsException.class)
    public void testTooFewArgs() throws WrongNumberOfArgumentsException {
        try {
            new Command(COMMAND_TOO_FEW_ARGS);
        } catch (InvalidCommandException e) {
            fail();
        }
    }

    @Test(expected=WrongNumberOfArgumentsException.class)
    public void testTooManyArgs() throws WrongNumberOfArgumentsException {
        try {
            new Command(COMMAND_TOO_MANY_ARGS);
        } catch (InvalidCommandException e) {
            fail();
        }
    }

    @Test
    public void testParseAndSetCommand() throws InvalidCommandException, WrongNumberOfArgumentsException {
        Command command = new Command(COMMAND_WITH_ARGS_ARRAY);
        command.parseAndSetCommand("user");
        assert(command.getCommand().equals("USER"));
        command.parseAndSetCommand("pw");
        assert(command.getCommand().equals("PASS"));
        command.parseAndSetCommand("cd");
        assert(command.getCommand().equals("CWD"));
        command.parseAndSetCommand("dir");
        assert(command.getCommand().equals("PASV"));
        assert(command.getDataCommand().equals("LIST"));
        command.parseAndSetCommand("get");
        assert(command.getCommand().equals("PASV"));
        assert(command.getDataCommand().equals("RETR"));
        command.parseAndSetCommand("quit");
        assert(command.getCommand().equals("QUIT"));

    }

    @Test(expected = InvalidCommandException.class)
    public void testParseAndSetInvalidCommand() throws InvalidCommandException {
        command.parseAndSetCommand("wrong");
    }

    @Test
    public void testValidInputTrue() {
        try {
            // command originall set to 'quit'
            assert(command.validInput());

            Command userCommand = new Command(COMMAND_WITH_ARGS_ARRAY);
            assert(userCommand.validInput());

            Command passCommand = new Command("pw password".getBytes());
            assert(passCommand.validInput());

            Command cdCommand = new Command("cd ..".getBytes());
            assert(cdCommand.validInput());

            Command dirCommand = new Command("dir".getBytes());
            assert(dirCommand.validInput());

            Command getCommand = new Command("get file.txt".getBytes());
            assert(getCommand.validInput());
        } catch (InvalidCommandException e) {
            e.printStackTrace();
            fail();
        } catch (WrongNumberOfArgumentsException e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testIsDataCommand() {
        try {
            assert(!command.isDataCommand()); // quit

            Command dataCommand = new Command("dir".getBytes());
            assert(dataCommand.isDataCommand());

            dataCommand = new Command("get file.txt".getBytes());
            assert(dataCommand.isDataCommand());

        } catch (WrongNumberOfArgumentsException e) {
            e.printStackTrace();
            fail();
        } catch (InvalidCommandException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testRequiresArgument() {
        try {
            // original command in @Before set up with command 'quit'
            assert(!command.requiresArgument());
            command.parseAndSetCommand("user");
            assert(command.requiresArgument());
            command.parseAndSetCommand("pw");
            assert(command.requiresArgument());
            command.parseAndSetCommand("cd");
            assert(command.requiresArgument());
        } catch (InvalidCommandException e) {
            e.printStackTrace();
            fail();
        }
    }
}