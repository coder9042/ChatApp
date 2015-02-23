// Simply a super class so that the subclasses can be placed together in a list
abstract class Client{
	public abstract void sendMessage(String text);
	public abstract void sendAudio(byte[] audioBytes);
	public abstract void closeConnection();
}