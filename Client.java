package common.networking;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class Client extends Connection{
	
	private Thread runningThread;

	public Client() {
		super();
	}
	
	
	public boolean connect(String host, int port) {
		try {
			initialize(new Socket(host, port));
			runningThread = new Thread(this, "Client");
			runningThread.setDaemon(true);
			runningThread.start();
			
			return true;

		}catch(ConnectException e) {
			//System.out.println("Unable to connect to the server");
			close();
			return false;
		}catch(IOException e) {
			e.printStackTrace();
			close();
		}
		return false;
	}
	
	public Thread getThread() {
		return runningThread;
	}

}