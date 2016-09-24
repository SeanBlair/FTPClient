import org.junit.Before;
import org.junit.Test;

/**
 * Created by Laura on 2016-09-23.
 */
public class CommandTest {

    private byte[] SINGLE_COMMAND_ARRAY = {'q', 'u', 'i', 't'};
    private byte[] COMMAND_WITH_ARGS_ARRAY = {'u', 's', 'e', 'r', ' ', 'n', 'a', 'm', 'e'};

    private String SINGLE_COMMAND_STRING = new String(SINGLE_COMMAND_ARRAY);
    private String COMMAND_WITH_ARGS_STRING = new String(COMMAND_WITH_ARGS_ARRAY);
    private String COMMAND_ALONE = "user";
    private String ARGUMENT_ALONE = "name";

    Command command;

    @Before
    public void setUp() {
        command = new Command(SINGLE_COMMAND_ARRAY);
    }

    @Test
    public void testSingleCommand() {
        assert(command.getFullCommand().equals(SINGLE_COMMAND_STRING));
    }

    @Test
    public void testCommandWithArgs() {
        command.setFullCommand(COMMAND_WITH_ARGS_ARRAY);
        assert(command.getFullCommand().equals(COMMAND_WITH_ARGS_STRING));
        assert(command.getCommand().equals(COMMAND_ALONE));
        assert(command.getArgument().equals(ARGUMENT_ALONE));
    }
}