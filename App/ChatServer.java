import java.io.*;
import javax.swing.*;
import java.net.*;
class ChatServer{
	Thread serverThread;
	UserInterface ui;
	static final int serverPort = 6792;
	public ChatServer(UserInterface obj){
		serverThread = new Thread(new ServerRunnable());
		serverThread.start();
		ui = obj;
	}
	class ServerRunnable implements Runnable{
		public void run(){
			try{
				ServerSocket serverSocket = new ServerSocket(serverPort);
				System.out.println("Chat Server started.");
				int count = 0;
				while(true){
					Socket connectionSocket = serverSocket.accept();
					String ip = connectionSocket.getInetAddress().toString();
					System.out.println("Connection accepted from "+ip);
					boolean val = Connection.getUserName(ip);
					if(val){
						ui.statusField.setText("Connection accepted from " + Connection.response);
						ui.addTab(Connection.response, connectionSocket);
					}
				}
			}
			catch(IOException e){
				//
			}
		}
	}
}