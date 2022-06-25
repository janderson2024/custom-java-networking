package common.networking.test;

import common.networking.Connection;
import common.networking.Listener;
import common.networking.Server;
import common.networking.test.TestNetwork.*;

public class TestServer {
	Server server;
	
	public TestServer() {
		server = new Server(5000);

		// For consistency, the classes to be sent over the network are
		// registered by the same method for both the client and server.

		server.setListener(new Listener() {
			public void onConnect (Connection connection) {
				System.out.println("Client joined");
				
				ClientJoined cj = new ClientJoined();
				cj.name = "client " + connection.getID();
				server.sendToAllExcept(connection.getID(), cj);
			}

			public void onReceived(Connection connection, Object object) {
				if(object instanceof Message) {
					System.out.println("Got message from " + connection.getID());
					server.sendToAllExcept(connection.getID(), object);
						
				}
			}

			public void onDisconnect (Connection connection) {
				System.out.println("Client left");
				ClientLeft cl = new ClientLeft();
				cl.name = "client " + connection.getID();
				server.sendToAllExcept(connection.getID(), cl);
			}
		});
	
		server.start();
	}

	public static void main (String[] args) {
		new TestServer();
	}
}
