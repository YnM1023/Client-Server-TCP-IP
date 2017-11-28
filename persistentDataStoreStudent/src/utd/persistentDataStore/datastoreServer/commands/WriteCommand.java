package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class WriteCommand extends ServerCommand{
	private static Logger logger = Logger.getLogger(WriteCommand.class);
	
	@Override
	public void run() throws IOException, ServerException {
		logger.debug("Writing Operation Starts on Server");
		String name = StreamUtil.readLine(inputStream);
		String length = StreamUtil.readLine(inputStream);
		byte[] data = StreamUtil.readData(Integer.parseInt(length), inputStream);
		FileUtil.writeData(name, data);
		
		logger.debug("Producing Response");
		StreamUtil.writeLine("ok", outputStream);
		logger.debug("Writing Operation Finished on Server");
	}
}
