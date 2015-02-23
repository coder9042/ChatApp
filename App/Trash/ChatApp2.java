import java.io.*;
import java.net.*;
class ChatApp{
	ChatServer server;
	ChatClient client;
	Socket mSocket;
	//UserInterface ui;
	public ChatApp(){
		System.out.println("Server running...");
		server = new ChatServer();
		client = null;
	}
	public void createClient(int port){
		client = new ChatClient(port);
	}
	/*public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ChatApp app = new ChatApp();
		System.out.println("Do you want to connect?\n1.Yes\n2.No. Wait for connection.");
		int inp = Integer.parseInt(br.readLine());
		if(inp == 1){
			System.out.println("Enter port:");
			int p = Integer.parseInt(br.readLine());
			app.createClient(p);
		}
	}*/
	class ChatServer{
		Thread serverThread;
		int serverPort = 0;
		public ChatServer(){
			serverThread = new Thread(new ServerThread());
			serverThread.start();
		}
		class ServerThread implements Runnable{
			public void run(){
				try{
					System.out.println("Awaiting Connection!");
					serverPort = 6789 + (int)(Math.random()*10);
					ServerSocket serverSocket = new ServerSocket(serverPort);
					System.out.println("My Port:" + serverPort);
					Socket connectionSocket = serverSocket.accept();
					if(mSocket == null){
						//
					}
					else if(mSocket.isConnected()){
						mSocket.close();
					}
					mSocket = connectionSocket;
					System.out.println("Connection Accepted.");
					if(client == null){
						//ui = new UserInterface();
						client = new ChatClient(mSocket.getPort());
					}
					
				}
				catch(IOException e){}
			}
		}
	}
	class ChatClient{
		Thread sThread;
		Thread rThread;
		int connectingPort;
		public ChatClient(int p){
			connectingPort = p;
			sThread = new Thread(new SendingThread());
			sThread.start();
		}
		class SendingThread implements Runnable{
			public void run(){
				try{
					if(mSocket == null){
						mSocket = new Socket("localhost", connectingPort);
						//if(ui == null)
							//ui = new UserInterface();
					}
					System.out.println("Connected!");
					//BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
					DataOutputStream outToServer = new DataOutputStream(mSocket.getOutputStream());
					rThread = new Thread(new ReceivingThread());
					rThread.start();
					//System.out.println("Enter text to send:");
					//ui.show();
					//ui.setOutputStream(outToServer);
					/*while(true){
						String input = inFromUser.readLine();
						outToServer.writeBytes(input+"\n");
					}*/
				}
				catch(IOException e){}
			}
		}
		class ReceivingThread implements Runnable{
			public void run(){
				try{
					BufferedReader inFromServer = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
					while(true){
						String input = inFromServer.readLine();
						//ui.chatArea.setText(ui.chatArea.getText() + "HE/SHE: " + input);
					}
				}
				catch(IOException e){}
			}
		}
	}
}