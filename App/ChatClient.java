import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.net.*;
class ChatClient extends Client{
	Thread listeningThread;
	private Socket mSocket;
	private JPanel chatArea;
	static final int serverPort = 6792;
	public ChatClient(){
		//
	}
	public boolean connect(InetAddress ipAddress, JPanel chatArea){
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
	public void setSocket(Socket connectionSocket, JPanel chatArea){
		mSocket = connectionSocket;
		listeningThread = new Thread(new ListeningRunnable());
		listeningThread.start();
		this.chatArea = chatArea;
	}
	public void sendMessage(String text){
		try{
			DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());
			byte[] textBytes = text.getBytes();
			System.out.println("Bytes sent: " + textBytes.length);
			outputStream.writeInt(1);
			outputStream.writeInt(textBytes.length);
			outputStream.write(textBytes, 0, textBytes.length);
		}
		catch(SocketException e){
			if(!isClosed())
				closeConnection();
		}
		catch(IOException e){
			//
		}
	}
	public void sendAudio(byte[] audioBytes){
		try{
			DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());
			System.out.println("Bytes sent: " + audioBytes.length);
			outputStream.writeInt(2);
			outputStream.writeInt(audioBytes.length);
			outputStream.write(audioBytes, 0, audioBytes.length);
		}
		catch(SocketException e){
			if(!isClosed())
				closeConnection();
		}
		catch(IOException e){
			//
		}
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
				//BufferedReader listenStream = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
				DataInputStream listenStream = new DataInputStream(mSocket.getInputStream());

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