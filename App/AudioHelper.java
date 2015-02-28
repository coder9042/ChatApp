import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.*;
import java.net.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioHelper {

	static final float sampleRate = 80000.0f;
	static final int sampleSizeInBits = 16;
	static final int channels = 1;
	static final boolean signed = true;
	static final boolean bigEndian = true;

	static AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

	static final int maxBytes = 5 * 1024 * 1024;

    Queue<ByteArrayOutputStream> outputs = new LinkedList<ByteArrayOutputStream>();

	boolean stopped = false;

    public byte[] record() {
        TargetDataLine microphone;
        try {
            microphone = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();

            int bytesRead = 0;

            try {
                while (bytesRead < maxBytes && !stopped) {
                    numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                    bytesRead = bytesRead + numBytesRead;
                    out.write(data, 0, numBytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            microphone.close();
            return out.toByteArray();
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void recordAndSend(InetAddress addr, int port){
        TargetDataLine microphone;
        try {
            microphone = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            
            int numBytesRead;
            int CHUNK_SIZE = 1024;
            microphone.start();

            final InetAddress ipAddress = addr;
            final int portNum = port;
            new Thread(new Runnable(){
                    public void run(){
                        while(true){
                            try{
                                DatagramSocket socket = new DatagramSocket();
                                ByteArrayOutputStream out = outputs.poll();
                                if(out == null){
                                    Thread.sleep(2000);
                                    continue;
                                }
                                byte[] arr = out.toByteArray();
                                DatagramPacket sendPacket = new DatagramPacket(arr, arr.length, ipAddress, portNum);
                                socket.send(sendPacket);
                                System.out.println("Audio sent");
                            }
                            catch(Exception e){
                                //e.printStackTrace();
                            }
                        }
                    }
                }).start();

            while(!stopped){
                int bytesRead = 0;
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] data = new byte[microphone.getBufferSize() / 5];
                try{
                    while(bytesRead < 40000) {
                        numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                        bytesRead = bytesRead + numBytesRead;
                        out.write(data, 0, numBytesRead);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                outputs.add(out);
                //final byte arr[] = out.toByteArray();
                //System.out.println("size of audio="+arr.length);
                
            }
            microphone.close();
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public static void play(byte[] audioData){
		AudioInputStream audioInputStream;
        SourceDataLine sourceDataLine;

        try{
			InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);

        	audioInputStream = new AudioInputStream(byteArrayInputStream, format, audioData.length / format.getFrameSize());
        	DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        	sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        	sourceDataLine.open(format);
        	sourceDataLine.start();
        	int cnt = 0;
        	byte tempBuffer[] = new byte[10000];
        	try {
        	    while ((cnt = audioInputStream.read(tempBuffer, 0,tempBuffer.length)) != -1) {
        	        if (cnt > 0) {
        	            sourceDataLine.write(tempBuffer, 0, cnt);
        	        }
        	    }
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}

        	sourceDataLine.drain();
        	sourceDataLine.close();
        }
        catch(LineUnavailableException e){
        	//
        }
    }
}