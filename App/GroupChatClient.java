import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.net.*;
class GroupChatClient extends Client{
	ArrayList<Thread> listeningThreads;
	ArrayList<Socket> mSockets;
	JPanel chatArea;
	String groupName;
	static final int serverPort = 6793;
	public GroupChatClient(String gName){
		groupName = gName;
		listeningThreads = new ArrayList<Thread>();
		mSockets = new ArrayList<Socket>();
	}
	public boolean connect(ArrayList<InetAddress> ipAddresses, JPanel chatArea){
		System.out.println(ipAddresses.size());
		for(InetAddress ipAddress: ipAddresses){
			if(connectionExists(ipAddress))
				continue;
			try{
				System.out.println("Connecting to "+ipAddress+":"+serverPort);
				Socket mSocket = new Socket(ipAddress, serverPort);
				mSockets.add(mSocket);
				DataOutputStream outToServer = new DataOutputStream(mSocket.getOutputStream());
				outToServer.writeBytes(groupName+"\n");
				outToServer.writeBytes(String.valueOf(ipAddresses.size()-1)+"\n");
				//outToServer.writeBytes(ipAddresses.get(1).toString().substring(1)+"\n");

				for(InetAddress ipAddress2: ipAddresses){
					if(ipAddress.toString().compareTo(ipAddress2.toString()) == 0)
						continue;
					outToServer.writeBytes(ipAddress2.toString().substring(1)+"\n");
				}

				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
				String response = inFromServer.readLine();
				if(!response.equals("Success")){
					break;
				}
				// Add listening threads
				Thread listeningThread = new Thread(new ListeningRunnable(mSocket));
				listeningThread.start();
				listeningThreads.add(listeningThread);
			}
			catch(IOException e){
				//
			}
		}
		System.out.println("Made connections");
		if(this.chatArea == null)
			this.chatArea = chatArea;
		if(mSockets.size() != 0)
			return true;
		else
			return false;
	}
	public void setSocket(Socket connectionSocket, JPanel chatArea, ArrayList<InetAddress> ipAddresses){
		System.out.println("Inside setSocket");
		if(connectionExists(connectionSocket.getInetAddress())){
			//
		}
		else {
			mSockets.add(connectionSocket);
			Thread listeningThread = new Thread(new ListeningRunnable(connectionSocket));
			listeningThread.start();
			listeningThreads.add(listeningThread);
			if(this.chatArea == null)
				this.chatArea = chatArea;
		}
		if(ipAddresses != null && ipAddresses.size() > 0)
			connect(ipAddresses, chatArea);
	}
	private boolean connectionExists(InetAddress ipAddress){
		String ipAddr = ipAddress.toString();
		for(Socket s: mSockets){
			if(s == null || s.isClosed())
				continue;
			String ip = s.getInetAddress().toString();
			if(ip.equals(ipAddr))
				return true;
		}
		return false;
	}
	public void sendMessage(String text){
		System.out.println(mSockets.size()); 
		for(Socket mSocket: mSockets){
			try{
				DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());
				byte[] textBytes = text.getBytes();
				System.out.println("Bytes sent: " + textBytes.length);
				outputStream.writeInt(1);
				outputStream.writeInt(textBytes.length);
				outputStream.write(textBytes, 0, textBytes.length);
			}
			catch(SocketException e){
				//
			}
			catch(IOException e){
				//
			}
		}
	}
	public void sendAudio(byte[] audioBytes){
		for(Socket mSocket: mSockets){
			try{
				DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());
				System.out.println("Bytes sent: " + audioBytes.length);
				outputStream.writeInt(2);
				outputStream.writeInt(audioBytes.length);
				outputStream.write(audioBytes, 0, audioBytes.length);
			}
			catch(SocketException e){
				//
			}
			catch(IOException e){
				//
			}
		}
	}
	public void closeConnection(){
		for(Socket mSocket: mSockets){
			try{
				if(mSocket != null){
					mSocket.close();
				}
			}
			catch(IOException e){
				//
			}
		}
	}
	public boolean isClosed(){
		int count = 0;
		for(int i=0;i<mSockets.size();i++){
			Socket mSocket = mSockets.get(i);
			if(mSocket != null && mSocket.isClosed()){
				count++;
			}
			else if(mSocket == null){
				count++;
			}
		}
		if(count == mSockets.size())
			return true;
		else
			return false;
	}
	public void updateChatWindow(String sendText){
		if(sendText == null || sendText.equals(""))
			return;
		if(isClosed())
			return;
		JTextField newTextArea = new JTextField(40);
		//int pixels = newTextArea.getFontMetrics(new Font("Cambria", Font.PLAIN, 15)).stringWidth("ME: " + sendText + "    ");
		//int cols = (int)(pixels / (newTextArea.getPreferredSize().getWidth() / 40)) + 5;
		//newTextArea.setColumns(cols);
		newTextArea.setText(sendText);
		chatArea.add(newTextArea);
		newTextArea.setMaximumSize(new Dimension(1000, 20));
		newTextArea.setFont(new Font("Cambria", Font.BOLD, 15));
		newTextArea.setBorder(BorderFactory.createEmptyBorder());
		newTextArea.setEditable(false);
		chatArea.validate();
	}
	public void updateChatWindow(byte[] audioBytes){
		AudioControlUI.addAudioButton(chatArea, audioBytes, "Audio File Received");
	}
	class ListeningRunnable implements Runnable{
		Socket listenSocket;
		public ListeningRunnable(Socket socket){
			listenSocket = socket;
		}
		public void run(){
			try{
				//BufferedReader listenStream = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
				DataInputStream listenStream = new DataInputStream(listenSocket.getInputStream());

				while(true){
					//updateChatWindow(listenStream.readLine());
					int len = -1;
					int type = -1;
					byte[] readData;
					try{
						type = listenStream.readInt();

						len = listenStream.readInt();
						if (len < 0) {
							continue;
						}

						readData = new byte[len];
						listenStream.readFully(readData, 0, len);

						if(type == 1){
							String str = new String(readData);
							if(str.length() > 70){
								while(str.length() > 0){
									if(str.length() > 70){
										updateChatWindow(str.substring(0, 70));
										str = str.substring(70);
									}
									else{
										updateChatWindow(str);
										str = "";
									}
								}
							}
							else{
								updateChatWindow(str);
							}
						}
						else if(type == 2){
							updateChatWindow(readData);
						}
					}
					catch(EOFException e){
						//e.printStackTrace();
						continue;
					}
					catch(SocketException e){
						//e.printStackTrace();
						closeConnection();
						break;
					}
					catch(IOException e){
						//e.printStackTrace();
						continue;
					}
					
				}
			}
			catch(IOException e){
				closeConnection();
			}
		}
	}
}