import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


public class FtpHandlerTest {
	
	String host = "ftp.cs.ubc.ca";
	int port = 21;
	
	FtpHandler ftpHandler;
	
	private byte[] USER_ANONYMOUS = {'u', 's', 'e', 'r', ' ', 'a', 'n', 'o', 'n', 'y', 'm', 'o', 'u', 's'};
    private byte[] PW_LKJ = {'p', 'w', ' ', 'l', 'k', 'j'};
    private byte[] QUIT = {'q', 'u', 'i', 't'};
    
	Command command;
	
	

    @Before
    public void setUp() {
        ftpHandler = new FtpHandler(host, port);
    }
	
	@Test
	public void testExecuteCommand() {
		try
		{
			
			command = new Command(USER_ANONYMOUS);
			ftpHandler.executeCommand(command);
			
			String after = ftpHandler.lastServerResponseForTesting;
			assert(after.substring(0, 3).equals("331"));
			
			command = new Command(PW_LKJ);
			ftpHandler.executeCommand(command);
			
			after = ftpHandler.lastServerResponseForTesting;
			assert(after.substring(0, 3).equals("230"));
			
			command = new Command(QUIT);
			ftpHandler.executeCommand(command);
			
			after = ftpHandler.lastServerResponseForTesting;
			assert(after.substring(0, 3).equals("221"));
			
		}
		catch (IOException e)
		{
			fail("executeCommand(" + command.getFullCommand() + "threw an IOException \n" + e.toString());
		}
	}
	
	@After
	public void tearDown(){
		try {
			ftpHandler.closeSocket();
			
		} catch (IOException e) {
			System.out.println("Unable to close Socket while testing " + command.getFullCommand() + " because of a thrown IOException");
			e.printStackTrace();
		}
	}

}
