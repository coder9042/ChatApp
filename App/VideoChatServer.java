import java.io.*;
import java.net.*;
class VideoChatServer{
	VideoChatWindow videoGUI;
	static final int myPort = 9876;
	static final int myPort2 = 9877;
	InetAddress receivingIP;
	public VideoChatServer(VideoChatWindow window, InetAddress addr){
		videoGUI = window;
		receivingIP = addr;
	}
	public void start(){
		videoGUI.show();
		Thread server = new Thread(new VideoRunnable());
		server.start();
		Thread server2 = new Thread(new AudioRunnable());
		server2.start();
	}

	class VideoRunnable implements Runnable{
		public void run(){
			try{
				DatagramSocket serverSocket = new DatagramSocket(myPort);
				byte[] receiveData = new byte[40000];
				while(true){
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					InetAddress ip = receivePacket.getAddress();
					if(ip!= null && ip.toString().compareTo(receivingIP.toString()) != 0)
						continue;
					serverSocket.receive(receivePacket);
					System.out.println("Receiving video");
					byte[] arr = receivePacket.getData();
					videoGUI.updateVideo(arr);
				}
			}
			catch(IOException e){
				//
			}
		}
	}
	class AudioRunnable implements Runnable{
		public void run(){
			try{
				DatagramSocket serverSocket = new DatagramSocket(myPort2);
				byte[] receiveData = new byte[40000];
				while(true){
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					InetAddress ip = receivePacket.getAddress();
					if(ip!= null && ip.toString().compareTo(receivingIP.toString()) != 0)
						continue;
					serverSocket.receive(receivePacket);
					System.out.println("Receiving audio");
					byte[] arr = receivePacket.getData();
					AudioHelper.play(arr);
				}
			}
			catch(IOException e){
				//
			}
		}
	}
}