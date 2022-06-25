package common.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import common.networking.FrameworkPacket.Register;

public class Connection implements Runnable{
	
	protected Socket socket;
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	
	private Listener listener;
	
	protected int id = -1;
	protected boolean running = false;
	protected boolean init = false;
	
	public Connection() {
		
	}
	
	public void  initialize(Socket socket) {
		this.socket = socket;
		
		initIO();
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean getRunning() {
		return running;
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}

		
	private void initIO() {
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			init = true;
		}catch(IOException e) {
			e.printStackTrace();
			close();
		}
	}

	@Override
	public void run() {
		running = true;
		while(running) {
			if(init) {
				try {
					Object data = in.readObject();
					if(data instanceof FrameworkPacket) {
						if(data instanceof Register) {
							id = ((Register)data).id;
							
							if(listener != null) {
								listener.onConnect(this);
							} else {
								logOut("Recieved a Register packet from the server on Connection " + id + " with no listener");
							}
						}
					} else {
						if(listener != null) {
							listener.onReceived(this, data);
						} else {
							logOut("Recieved non FrameworkPacket on Connection " + id + " with no listner");
						}
					}
				}catch(ClassNotFoundException e) {
					e.printStackTrace();
					close();
				}catch(SocketException e) {
					close();
				}catch(IOException e) {
					close();
				}
			}
		}
	}
	
	public void close() {
		try {
			if(!running) {
				return;
			}
			running = false;
			
			if(socket != null) {
				in.close();
				out.close();
				socket.close();
				
				if(listener != null) {
					listener.onDisconnect(this);
				}else {
					logOut("Connection " + id + " has closed");
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void  sendObject(Object o) {
		try {
			out.writeObject(o);
			out.flush();
		}catch(IOException e) {
			//failed to send. Close the connection
			logOut("Connection " + id + " couldn't write object");
			close();
		}
	}
	
	private static void logOut(String str) {
		System.out.println(str);
	}
}