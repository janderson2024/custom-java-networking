package common.networking;

public interface Listener {
	public void onConnect(Connection connection);
	public void onDisconnect(Connection connection);
	public void onReceived(Connection connection, Object object);
}
