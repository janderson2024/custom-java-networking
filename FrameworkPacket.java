package common.networking;

import java.io.Serializable;

public abstract class FrameworkPacket implements Serializable{
	private static final long serialVersionUID = 1L;

	/** Internal message to give the client the server assigned connection ID. */
	static public class Register extends FrameworkPacket {
		protected static final long serialVersionUID = 1L;
		public int id;
	}

	/** Internal message to keep connections alive. */
	static public class KeepAlive extends FrameworkPacket {
		protected static final long serialVersionUID = 1L;
	}

	/** Internal message to determine round trip time. */
	static public class Ping extends FrameworkPacket {
		protected static final long serialVersionUID = 1L;
		public int id;
		public boolean isReply;
	}
}
