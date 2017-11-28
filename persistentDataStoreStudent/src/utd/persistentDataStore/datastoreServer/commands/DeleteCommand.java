package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class DeleteCommand extends ServerCommand{
	private static Logger logger = Logger.getLogger(DeleteCommand.class);
	
	@Override
	public void run() throws IOException, ServerException {
		logger.debug("Deleting Operation Starts on Server");
		String name = StreamUtil.readLine(inputStream);
		try {
			FileUtil.deleteData(name);	
			logger.debug("Producing Response");
			StreamUtil.writeLine("ok", outputStream);	
		}catch(Exception e) {
			StreamUtil.writeLine("error: "+e.getMessage(), outputStream);
			throw new ServerException(e.getMessage(),e);
		}
		logger.debug("Reading Operation Finished on Server");		
	}
}
