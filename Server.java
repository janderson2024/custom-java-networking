package common.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import common.networking.FrameworkPacket.Register;

public class Server implements Runnable{
	
	private int port;
	private ServerSocket serverSocket;
	private boolean running = false;
	
	private ArrayList<Connection> connections;
	
	private Listener listener;
	
	private Listener dispatchListener = new Listener() {
		public void onConnect(Connection connection) {
			if(listener != null) {
				listener.onConnect(connection);
			}
		}

		public void onDisconnect(Connection connection) {
			connections.remove(connection);
			//System.out.println("SERVER: conn #" + connection.id + " disconnected");
			if(listener != null) {
				listener.onDisconnect(connection);
			}
		}

		public void onReceived(Connection connection, Object object) {
			if(listener != null) {
				listener.onReceived(connection, object);
			}
		}
	};

	public Server(int port) {
		this.port = port;
		connections = new ArrayList<Connection>();
		listener = null;
		
		try {
			serverSocket = new ServerSocket(port);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		running = true;
		System.out.println("Server started on port: " + port);
		
		while(running) {
			try {
				Socket socket = serverSocket.accept();
				initSocket(socket);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		shutdown();
	}
	
	private void initSocket(Socket socket) {
		int id = connections.size();
		//System.out.println("New Client. Id: " + id);
		Connection connection = new Connection();
		connection.initialize(socket);
		
		connection.setListener(dispatchListener);
		connection.setId(id);
		connections.add(connection);
		
		Register register = new Register();
		register.id = id;
		
		new Thread(connection, "connection #" + id).start();
		connection.sendObject(register);
		dispatchListener.onConnect(connection);
	}
	
	public void shutdown() {
		running = false;
		
		try {
			serverSocket.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
		for(Connection conn : connections) {
			conn.setListener(listener);
		}
	}
	
	public void sendTo(int connectionId, Object o) {
		Connection[] temp = this.connections.toArray(new Connection[connections.size()]);
		for(Connection conn : temp) {
			if(conn.id == connectionId) {
				conn.sendObject(o);
			}
		}
	}
	public void sendToAll(Object o) {
		Connection[] temp = this.connections.toArray(new Connection[connections.size()]);
		for(Connection conn : temp) {
				conn.sendObject(o);
		}
	}
	
	public void sendToAllExcept(int connectionId, Object o) {
		Connection[] temp = this.connections.toArray(new Connection[connections.size()]);
		for(Connection conn : temp) {
			if(conn.id != connectionId) {
				conn.sendObject(o);
			}
		}
	}
	
}