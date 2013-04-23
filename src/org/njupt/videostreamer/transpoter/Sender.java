package org.njupt.videostreamer.transpoter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SocketChannel;
import java.util.Vector;

import android.util.Log;


public class Sender implements Runnable{
	
	String TAG = "SENDER";
	Pipe.SourceChannel  audiodata = null;
	Pipe.SourceChannel videodata = null;
	Vector<SocketChannel> aClients = null;
	int packetSize = 0;
	int sType ;
	public static int SEND_AUDIO = 1;
	public static int SEND_VIDEO = 2;
	boolean SENDING = false;
	Thread sendingThread = null;
	
	public Sender(Vector<SocketChannel> list,int t){
		aClients = list;
		sType = t;
	}
	
	public void setAudioSource(Pipe.SourceChannel  source){
		this.audiodata	=	source;
	}
	
	public void sendAudio( ){
		this.SENDING = true;
		sendingThread = new Thread(this);
		sendingThread.start();
	}
	
	public void sendVideo(byte[] data){
		
	}
	
	public void setPacketSize(int size){
		packetSize = size;
	}

	@Override
	public void run() {
		
		ByteBuffer sourceData = ByteBuffer.allocate(packetSize);
		while(SENDING && packetSize > 0  ){
			try {
				//璇诲彇PIPE涓殑閲囨牱鏁版嵁鍒癇UFFER锛屽噯澶囧彂閫�				
				int a = audiodata.read(sourceData);
				Log.d(TAG,"read recorded data "+a );
				sourceData.flip();
				//鍚屼竴浠芥暟鎹竴娆″彂缁欓摼鎺ョ潃鐨勭敤鎴�
				for(int i = 0;i<this.aClients.size();i++){
					aClients.get(i).write(sourceData); 
				}
				//鍙戦�瀹屾瘯鍚庢竻绌築UFFER鍑嗗涓嬩竴璁哄彂閫�
				sourceData.clear();
			 	} catch (IOException e) {
				Log.e(TAG,"send Buffer failed"+e.getMessage());
			}	
		}
		Log.e(TAG, "PacketSize not appointed!");
	}
	
	
	public void stopSending(){
		this.SENDING = false;
		if(sendingThread!=null)
		sendingThread.interrupt();
	}
}
