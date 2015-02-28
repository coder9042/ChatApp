import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
class VideoChatClient{
	VideoChatWindow videoGUI;
	InetAddress toIP;
	private DaemonThread myThread = null;;
    VideoCapture webSource = null;
    Mat videoframe;
    MatOfByte mem;
    AudioHelper jack;

	public VideoChatClient(VideoChatWindow videoGUI, InetAddress toIP){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		this.videoGUI = videoGUI;
		this.toIP = toIP;
		videoframe = new Mat();
    	mem = new MatOfByte();
	}
	public void start(){
		webSource = new VideoCapture(0);
		webSource.set(3, 300.0);
		webSource.set(4, 300.0);
		System.out.println("capture size" + webSource.get(3) + "*" + webSource.get(4));
		myThread = new DaemonThread();
		Thread t = new Thread(myThread);
		myThread.runnable = true;
		t.start();
		jack = new AudioHelper();
		new Thread(new Runnable(){
			public void run(){
				jack.recordAndSend(toIP, 9877);
			}
		}).start();
	}
	public void pause(){
		if(myThread != null){
			myThread.runnable = false;
		}
		if(webSource != null)
			webSource.release();
		if(jack != null)
			jack.stopped = true;
		
	}
	    class DaemonThread implements Runnable {

	        protected volatile boolean runnable = false;

	        @Override
	        public void run() {
	            synchronized (this) {
	                while (runnable) {
	                    DatagramSocket socket = null;
	                    try {
	                        socket = new DatagramSocket();
	                    } catch (SocketException ex) {
	                        //
	                    }
	                    if (webSource.grab()) {
	                        try {
	                            webSource.retrieve(videoframe);
	                            Highgui.imencode(".jpg", videoframe, mem);
	                            byte[] arr = new byte[40000];
	                            arr = mem.toArray();
	                            DatagramPacket sendPacket = new DatagramPacket(arr, arr.length, toIP, 9876);
	                            socket.send(sendPacket);

	                            Image im = ImageIO.read(new ByteArrayInputStream(arr));

	                            BufferedImage buff = (BufferedImage) im;
	                            Graphics g = videoGUI.myVideoPanel.getGraphics();

	                            if (g.drawImage(buff, videoGUI.myVideoPanel.getX(), videoGUI.myVideoPanel.getX(), videoGUI.myVideoPanel.getWidth(), videoGUI.myVideoPanel.getHeight(), 0, 0, buff.getWidth(), buff.getHeight(), null)) {
	                                if (runnable == false) {
	                                    System.out.println("Going to wait()");
	                                    this.wait();
	                                }
	                            }
	                        } catch (Exception ex) {
	                            ex.printStackTrace();
	                        }
	                    }
	                }
	            }
	        }
	    }
}