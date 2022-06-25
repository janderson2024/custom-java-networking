package common.networking.test;

import java.util.Scanner;

import common.networking.Client;
import common.networking.Connection;
import common.networking.Listener;
import common.networking.test.TestNetwork.*;

public class TestClient {
	Client client;
	boolean runCatch;
	
	public TestClient() {
		client = new Client();

		// For consistency, the classes to be sent over the network are
		// registered by the same method for both the client and server.

		client.setListener(new Listener() {
			public void onConnect (Connection connection) {
				System.out.println("Connected");
			}

			public void onReceived(Connection connection, Object object) {
				if(object instanceof Message) {
					Message m = (Message)object;
					System.out.println(m.name + ": " + m.message);
				}
				
				if(object instanceof ClientJoined) {
					String name = ((ClientJoined)object).name;
					System.out.println(name + " has joined the chat");
				}
				
				if(object instanceof ClientLeft) {
					String name = ((ClientLeft)object).name;
					System.out.println(name + " has left the chat");
				}
			}

			public void onDisconnect (Connection connection) {
				System.out.println("Disconnected");
				System.exit(0);
			}
		});
		
		boolean connected = client.connect("localhost",5000);
		
		
		
		if(!connected) {
			System.out.println("Failed to Connect");
			
		} else {
			Scanner scan = new Scanner(System.in);
			
			boolean runCatch = client.getRunning();
			while(runCatch) {
				String input = scan.nextLine();
				if(input == "") {
					runCatch = false;
				} else {
					Message m = new Message();
					m.name = "client " + client.getID();
					m.message = input;
					client.sendObject(m);
				}
			}
			scan.close();
			client.close();
		}
		
		
		System.out.println("End of main thread");
	}

	public static void main (String[] args) {
		new TestClient();
	}
}
