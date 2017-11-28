package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class ReadCommand extends ServerCommand{
	private static Logger logger = Logger.getLogger(ReadCommand.class);
	
	@Override
	public void run() throws IOException, ServerException {
		logger.debug("Reading Operation Starts on Server");
		String name = StreamUtil.readLine(inputStream);
		
		logger.debug("Producing Response");
		try {
			byte[] data = FileUtil.readData(name);
			StreamUtil.writeLine("ok", outputStream);
			StreamUtil.writeLine(String.valueOf(data.length), outputStream);
			StreamUtil.writeData(data, outputStream);
		}catch(Exception e) {
			StreamUtil.writeLine("error: "+e.getMessage(), outputStream);
			throw new ServerException(e.getMessage(),e);
		}
		logger.debug("Reading Operation Finished on Server");
	}
}
