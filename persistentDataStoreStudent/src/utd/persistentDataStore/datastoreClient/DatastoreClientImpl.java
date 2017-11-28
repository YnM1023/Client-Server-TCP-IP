package utd.persistentDataStore.datastoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.StreamUtil;

public class DatastoreClientImpl implements DatastoreClient
{
	private static Logger logger = Logger.getLogger(DatastoreClientImpl.class);

	private InetAddress address;
	private int port;

	public DatastoreClientImpl(InetAddress address, int port)
	{
		this.address = address;
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#write(java.lang.String, byte[])
	 */
	@Override
    public void write(String name, byte data[]) throws ClientException, ConnectionException
	{
		try {
			logger.debug("Opening Socket");
			Socket socket = new Socket();
			SocketAddress saddr = new InetSocketAddress(address, port);
			socket.connect(saddr);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			logger.debug("Executing Write Operation");
			StreamUtil.writeLine("write", outputStream);
			StreamUtil.writeLine(name, outputStream);
			StreamUtil.writeLine(data.length+"", outputStream);
			StreamUtil.writeData(data, outputStream);
			
			logger.debug("Reading Response");
			String result = StreamUtil.readLine(inputStream);
			logger.debug("Response " + result);
		} catch (IOException e) {
			throw new ConnectionException(e.getMessage(),e);
		}

		
		
	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#read(java.lang.String)
	 */
	@Override
    public byte[] read(String name) throws ClientException, ConnectionException
	{
		byte[] result = null;
		try {
			logger.debug("Opening Socket");
			Socket socket = new Socket();
			SocketAddress saddr = new InetSocketAddress(address, port);
			socket.connect(saddr);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			logger.debug("Executing Read Operation");
			// StreamUtil.writeLine can detect the end of String and add newline command for it if needed
			StreamUtil.writeLine("read", outputStream);
			StreamUtil.writeLine(name, outputStream);
			
			logger.debug("Reading Response");
			String responseCode = StreamUtil.readLine(inputStream);
			if("ok".equalsIgnoreCase(responseCode)) {
				String length = StreamUtil.readLine(inputStream);
				result = StreamUtil.readData(Integer.parseInt(length), inputStream);
				logger.debug("Response: "+responseCode+", the length of bytes is "+length);
				String dataBytes = "";
				
				logger.debug("Read Data: "+Arrays.toString(result));
			}else {
				throw new ClientException(responseCode);
			}				
		} catch (IOException e) {
			throw new ConnectionException(e.getMessage(),e);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#delete(java.lang.String)
	 */
	@Override
    public void delete(String name) throws ClientException, ConnectionException
	{
		try {
			logger.debug("Opening Socket");
			Socket socket = new Socket();
			SocketAddress saddr = new InetSocketAddress(address, port);
			socket.connect(saddr);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			logger.debug("Executing Delete Operation");
			// StreamUtil.writeLine can detect the end of String and add newline command for it if needed
			StreamUtil.writeLine("delete", outputStream);
			StreamUtil.writeLine(name, outputStream);
			
			logger.debug("Reading Response");
			String result = StreamUtil.readLine(inputStream);
			
			if("ok".equalsIgnoreCase(result)) {
				logger.debug("Response " + result);
			}else {
				throw new ClientException(result);
			}		
		} catch (IOException e) {
			throw new ConnectionException(e.getMessage(),e);
		}

	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#directory()
	 */
	@Override
    public List<String> directory() throws ClientException, ConnectionException
	{
		LinkedList<String> result = null;
		try {
			logger.debug("Opening Socket");
			Socket socket = new Socket();
			SocketAddress saddr = new InetSocketAddress(address, port);
			socket.connect(saddr);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			logger.debug("Executing Directory Operation");
			// StreamUtil.writeLine can detect the end of String and add newline command for it if needed
			StreamUtil.writeLine("directory", outputStream);
			
			logger.debug("Reading Respose");
			String responseCode = StreamUtil.readLine(inputStream);
			if("ok".equalsIgnoreCase(responseCode)) {
				String number = StreamUtil.readLine(inputStream);
				result = new LinkedList<>();
				logger.debug("Response: "+responseCode+", the number of files is "+number);
				for(int i=0;i<Integer.parseInt(number);i++) {
					result.addLast(StreamUtil.readLine(inputStream));
					logger.debug("File Name: "+result.get(i));
				}	
			}else {
				throw new ClientException(responseCode);
			}			
		} catch (IOException e) {
			throw new ConnectionException(e.getMessage(),e);
		}
		return result;
	}

}
