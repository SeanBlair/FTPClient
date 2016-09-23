import org.junit.Before;
import org.junit.Test;


import static junit.framework.Assert.assertTrue;

/**
 * Created by Laura on 2016-09-22.
 */
public class CommandTest {

    static final String COMMAND_NO_ARGS = "quit";

    private Command cmd;

    @Before
    public void initialize() {
        byte[] byteArr = {'q', 'u', 'i', 't', '\n'};
        this.cmd = new Command(byteArr);
    }

    @Test
    public void testNewCommandNoArgs() {
        assertTrue(cmd.getCmd().equals(COMMAND_NO_ARGS));
    }
}