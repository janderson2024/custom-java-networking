package common.networking.test;

import java.io.Serializable;

public class TestNetwork {

	public static class Message implements Serializable{
		private static final long serialVersionUID = 1L;
		String name;
		String message;
	}
	
	public static class ClientJoined implements Serializable {
		private static final long serialVersionUID = 1L;
		String name;
	}
	
	public static class ClientLeft implements Serializable {
		private static final long serialVersionUID = 1L;
		String name;
	}
}
