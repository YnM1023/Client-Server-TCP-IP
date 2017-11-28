package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class DirectoryCommand extends ServerCommand{
	private static Logger logger = Logger.getLogger(DirectoryCommand.class);
	
	@Override
	public void run() throws IOException, ServerException {
		logger.debug("Directory Operation Starts on Server");
		
		logger.debug("Producing Response");
		StreamUtil.writeLine("ok", outputStream);
		List<String> directory = FileUtil.directory();
		int number = directory.size();
		StreamUtil.writeLine(String.valueOf(number), outputStream);
		for(int i=0;i<number;i++) StreamUtil.writeLine(directory.get(i), outputStream);
	}
}