package utd.persistentDataStore.datastoreServer;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.apache.log4j.Logger;

import utd.persistentDataStore.datastoreServer.commands.*;
import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class DatastoreServer
{
	private static Logger logger = Logger.getLogger(DatastoreServer.class);

	static public final int port = 10023;

	public void startup() throws IOException
	{
		logger.debug("Starting Service at port " + port);

		ServerSocket serverSocket = new ServerSocket(port);

		InputStream inputStream = null;
		OutputStream outputStream = null;
		while (true) {
			try {
				logger.debug("Waiting for request");
				// The following accept() will block until a client connection 
				// request is received at the configured port number
				Socket clientSocket = serverSocket.accept();
				logger.debug("Request received");

				inputStream = clientSocket.getInputStream();
				outputStream = clientSocket.getOutputStream();

				ServerCommand command = dispatchCommand(inputStream);
				logger.debug("Processing Request: " + command);
				command.setInputStream(inputStream);
				command.setOutputStream(outputStream);
				command.run();
				
				StreamUtil.closeSocket(inputStream);
			}
			catch (ServerException ex) {
				String msg = ex.getMessage();
				logger.error("Exception while processing request. " + msg);
				StreamUtil.sendError(msg, outputStream);
				StreamUtil.closeSocket(inputStream);
			}
			catch (Exception ex) {
				logger.error("Exception while processing request. " + ex.getMessage());
				ex.printStackTrace();
				StreamUtil.closeSocket(inputStream);
			}
		}
	}

	// Need to implement
	private ServerCommand dispatchCommand(InputStream inputStream) throws ServerException, IOException
	{
		String commandString = StreamUtil.readLine(inputStream);
		
		if("write".equalsIgnoreCase(commandString)) {
			ServerCommand command = new WriteCommand();
			return command;
		}else if("read".equalsIgnoreCase(commandString)) {
			ServerCommand command = new ReadCommand();
			return command;
		}else if("delete".equalsIgnoreCase(commandString)) {
			ServerCommand command = new DeleteCommand();
			return command;
		}else if("directory".equalsIgnoreCase(commandString)) {
			ServerCommand command = new DirectoryCommand();
			return command;
		}else {
			throw new ServerException("Unknown Request: "+commandString);
		}
	}
	
	public static void main(String args[])
	{
		DatastoreServer server = new DatastoreServer();
		try {
			server.startup();
		}
		catch (IOException ex) {
			logger.error("Unable to start server. " + ex.getMessage());
			ex.printStackTrace();
		}
	}


}
