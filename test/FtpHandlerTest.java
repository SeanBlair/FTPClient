import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Sean on 2016-09-25.
 */

public class FtpHandlerTest {
	
	private String UBC = "ftp.cs.ubc.ca";
	private String GNU = "ftp.gnu.org";
	private String CSC = "ftp.cisco.com";
	private String DLL = "ftp.dell.com";
	private String MCS = "ftp.microsoft.com";
	
	private String host = UBC;
	private int port = 21;
	
	FtpHandler ftpHandler;
	
	private byte[] USER_ANONYMOUS = {'u', 's', 'e', 'r', ' ', 'a', 'n', 'o', 'n', 'y', 'm', 'o', 'u', 's'};
	private byte[] USER_LKJ = {'u', 's', 'e', 'r', ' ', 'l', 'k', 'j'};
	private byte[] USER = {'u', 's', 'e', 'r'};
  
	private byte[] PW_LKJ = {'p', 'w', ' ', 'l', 'k', 'j'};
	private byte[] PW = {'p', 'w'};
	
    private byte[] CD_LKJ = {'c', 'd', ' ', 'l', 'k', 'j'}; 
    private byte[] DIR = {'d', 'i', 'r'}; 
    private byte[] COMMENT = {'#', 'T', 'O', 'D', 'O'};
    private byte[] EMPTY_STRING = {};
    private byte[] INVALID_COMMAND = {'l', 'k', 'j'};
    private byte[] QUIT = {'q', 'u', 'i', 't'};
    
	Command command;
	
	

    @Before
    public void setUp() {
        //ftpHandler = new FtpHandler(host, port);
    }
	
    


    @Test 
    public void testExecuteCommand_FtpUbc_USER() {
    	String response;
    	try
    	{
    		ftpHandler = new FtpHandler("ftp.cs.ubc.ca", 21);
    		
    		command = new Command(USER_LKJ);
    		ftpHandler.executeCommand(command);
    		response = ftpHandler.getServerResponseString();
    		assert(response.substring(0, 3).equals("331"));
    		
    		command = new Command(USER_ANONYMOUS);
    		ftpHandler.executeCommand(command);
    		response = ftpHandler.getServerResponseString();
    		assert(response.substring(0, 3).equals("331"));	
    	}
    	
    	catch (IOException e)
    	{
			fail("executeCommand(" + command.getFullCommand() + ") threw an IOException \n" + e.toString());
		}
    }
    
    @Test 
    public void testExecuteCommand_FtpUbc_PW() {
    	String response;
    	try
    	{
    		ftpHandler = new FtpHandler("ftp.cs.ubc.ca", 21);
    		
    		command = new Command(USER_ANONYMOUS);
    		ftpHandler.executeCommand(command);
    		response = ftpHandler.getServerResponseString();
    		assert(response.substring(0, 3).equals("331"));	
    		
    		command = new Command(PW_LKJ);
    		ftpHandler.executeCommand(command);
    		response = ftpHandler.getServerResponseString();
    		assert(response.substring(0, 3).equals("331"));
    	}
    	
    	catch (IOException e)
    	{
			fail("executeCommand(" + command.getFullCommand() + ") threw an IOException \n" + e.toString());
		}
    }
    
    /**
     * Sanity test for all commands.
     * A semi-random runthrough of a sequence of commands.
     * 
     * Flakey - Designed to be run with ftp.cs.ubc.ca
     */
    
	@Test
	public void testExecuteCommand_AllCommandsOnce_FtpUbc() {
		String response;
		try
		{
			ftpHandler = new FtpHandler("ftp.cs.ubc.ca", 21);
			
			command = new Command(USER_ANONYMOUS);
			ftpHandler.executeCommand(command);	
			response = ftpHandler.getServerResponseString();
			assert(response.substring(0, 3).equals("331"));
			
			command = new Command(PW_LKJ);
			ftpHandler.executeCommand(command);	
			response = ftpHandler.getServerResponseString();
			assert(response.substring(0, 3).equals("230"));
			
			command = new Command(CD_LKJ);
			ftpHandler.executeCommand(command);
			response = ftpHandler.getServerResponseString();
			assert(response.substring(0, 3).equals("550"));
			
			command = new Command(DIR);
			ftpHandler.executeCommand(command);
			response = ftpHandler.getServerResponseString();
			assert(response.substring(0, 3).equals("227"));
			
			command = new Command(COMMENT);
			ftpHandler.executeCommand(command);
			response = ftpHandler.getServerResponseString();
			// no change from previous state (server not called)
			assert(response.substring(0, 3).equals("227"));
			
			command = new Command(INVALID_COMMAND);
			ftpHandler.executeCommand(command);
			response = ftpHandler.getServerResponseString();
			// no change from previous state (server not called)
			assert(response.substring(0, 3).equals("227"));		
			
			command = new Command(EMPTY_STRING);
			ftpHandler.executeCommand(command);
			response = ftpHandler.getServerResponseString();
			// no change from previous state (server not called)
			assert(response.substring(0, 3).equals("227"));		
			
			command = new Command(QUIT);
			ftpHandler.executeCommand(command);
			response = ftpHandler.getServerResponseString();
			assert(response.substring(0, 3).equals("221"));
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
