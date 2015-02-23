import java.io.*;
import java.util.*;
import javax.swing.*;
import java.net.*;
class GroupChatServer{
	Thread serverThread;
	UserInterface ui;
	static final int serverPort = 6793;
	public GroupChatServer(UserInterface obj){
		serverThread = new Thread(new ServerRunnable());
		serverThread.start();
		ui = obj;
	}
	class ServerRunnable implements Runnable{
		public void run(){
			try{
				ServerSocket serverSocket = new ServerSocket(serverPort);
				System.out.println("Group Chat Server started.");
				int count = 0;
				while(true){
					Socket connectionSocket = serverSocket.accept();
					Thread helper = new Thread(new HelperRunnable(connectionSocket));
					helper.start();
				}
			}
			catch(IOException e){
				//
			}
		}
	}
	class HelperRunnable implements Runnable{
		Socket connectionSocket;
		public HelperRunnable(Socket connectionSocket){
			this.connectionSocket = connectionSocket;
		}
		public void run(){
			try{
				String ip = connectionSocket.getInetAddress().toString();
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				String groupName = inFromClient.readLine();
				System.out.println(groupName);
				int num = Integer.parseInt(inFromClient.readLine());
				System.out.println(num);
				ArrayList<InetAddress> ipAddresses = new ArrayList<InetAddress>();
				if(num > 0){
					for(int i=0;i<num;i++){
						String ipAddr = inFromClient.readLine();
						System.out.println(ipAddr);
						ipAddresses.add(InetAddress.getByName(ipAddr));
					}
				}
				ui.addGroupTab(groupName, connectionSocket, ipAddresses);
				outToClient.writeBytes("Success\n");
			}
			catch(IOException e){

			}
		}
	}
}