import java.io.*;
import javax.swing.*;
import java.net.*;
class ChatClient{
	Thread listeningThread;
	private Socket mSocket;
	private JTextArea chatArea;
	static final int serverPort = 6792;
	public ChatClient(){
		//
	}
	public boolean connect(InetAddress ipAddress, JTextArea chatArea){
		try{
			System.out.println("Connecting to "+ipAddress+":"+serverPort);
			mSocket = new Socket(ipAddress, serverPort);
			listeningThread = new Thread(new ListeningRunnable());
			listeningThread.start();
			this.chatArea = chatArea;
			return true;
		}
		catch(IOException e){
			return false;
		}
	}
	public void setSocket(Socket connectionSocket, JTextArea chatArea){
		mSocket = connectionSocket;
		listeningThread = new Thread(new ListeningRunnable());
		listeningThread.start();
		this.chatArea = chatArea;
	}
	public void sendMessage(String text){
		try{
			DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());
			outputStream.writeBytes(text);
		}
		catch(SocketException e){
			e.printStackTrace();
			if(!isClosed())
				closeConnection();
		}
		catch(IOException e){
			//
		}
	}
	public void updateChatWindow(String text){
		if(text == null || text.equals(""))
			return;
		if(isClosed())
			return;
		String current = chatArea.getText();
		if(current.equals(""))
			chatArea.setText(text);
		else{
			if(current.charAt(current.length()-1) == '\n')
				chatArea.setText(current + text);
			else
				chatArea.setText(current + "\n" + text);
		}
	}
	public void closeConnection(){
		try{
			if(mSocket != null){
				mSocket.close();
			}
		}
		catch(IOException e){
			//
		}
	}
	public boolean isClosed(){
		if(mSocket != null && mSocket.isClosed())
			return true;
		else if(mSocket == null)
			return true;
		else
			return false;
	}
	class ListeningRunnable implements Runnable{
		public void run(){
			try{
				BufferedReader listenStream = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
				while(true){
					updateChatWindow(listenStream.readLine());
				}
			}
			catch(SocketException e){
				closeConnection();
			}
			catch(IOException e){
				//
			}
		}
	}
}