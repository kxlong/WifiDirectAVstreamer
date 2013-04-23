package org.njupt.videostreamer.transpoter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Vector;

import android.util.Log;

public class Receiver implements Runnable{
	
	String TAG = "RECEIVER";
	Hashtable<SocketChannel,Pipe> tracks = null;
	Vector<SocketChannel> clients = null;
	public static int  RECEIVE_AUDIO = 1;
	public static int  RECEIVE_VIDEO = 0;
	int rmode = 0;
	int bsize = 512;//鎺ユ敹缂撳啿鍖哄ぇ灏�
	Thread receivingThread = null;
	boolean working = false;
	
	public Receiver(Vector<SocketChannel> s,int mode){
		clients = s;
		rmode = mode;
	}
	
	public void receive(){
		
		if(tracks != null && clients != null) {
			working = true;
			receivingThread = new Thread(this);
			receivingThread.start();
		}
			
	}
	
	public void setPipeMap(Hashtable<SocketChannel,Pipe> pmap){
		tracks = pmap;
	}

	@Override
	public void run() {
		//寮�嚎绋嬪仛鎺ユ敹宸ヤ綔.
		ByteBuffer target = ByteBuffer.allocate(bsize);
		
		while(working){			
			SocketChannel op = null;
			for(int i = 0;i<clients.size();i++){
				op=clients.get(i);
				//浠庝竴涓敤鎴风殑channel涓幏鍙栧埌鏁版嵁鍒癰uffer,闅忓悗浼犻�鍒伴摼鎺ョ浉搴擜udioPlayer鐨凱ipe涓�
				try {				
					
					int a = op.read(target);
				//	Log.d(TAG, "receive data from socket :"+a);
					target.flip();
					tracks.get(op).sink().write(target);
					target.clear();
    				} catch (IOException e) {
    					Log.d(TAG, "receive data failed");
    				}
			}
		}
		if(!working && receivingThread != null)
			this.receivingThread.interrupt();
	}

	public void stopWorking(){
		this.working = false;
	}
}
