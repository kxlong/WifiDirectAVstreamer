package org.njupt.videostreamer.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;

import org.njupt.videostreamer.utils.AudioProfile;

import android.util.Log;

public class Server implements Runnable{
	
	InetAddress hostip=null;
	int port=0;
	ServerSocketChannel server=null;
	int limit=0;
	Buffer mes=null;
	String TAG="Server";
	boolean inserve=false;
	SocketChannel client = null;
	Thread st = null;
	Selector selector = null,hselector = null;
	Handler handler = null;
	Pipe p = null;
	Hashtable<InetAddress,Integer> ip = null;
	
	public Server(){
		
		try {
			ip = new Hashtable<InetAddress,Integer>();
			
			initServer();
		} catch (IOException e) {
			Log.e(TAG,"initialize Failed!");
		}
	}
	
	private void initServer() throws IOException{
		
		selector = Selector.open();
		hselector = Selector.open();
		server=ServerSocketChannel.open();
		
		server.socket().bind(new InetSocketAddress(8002));server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_ACCEPT);
		
		this.port = server.socket().getLocalPort();
		this.hostip = server.socket().getInetAddress();
		ip.put(hostip, Integer.valueOf(port));
		p = Pipe.open();

		
		if(server.isOpen())			
			Log.d(TAG, "serverchannel OK.IP :"+this.hostip.toString());
		
	}

	@Override
	public void run() {
		
			try {
				selector.select();
			} catch (IOException e) {
			}
			SocketChannel clientChannel = null;
			
			Iterator<SelectionKey> si = selector.selectedKeys().iterator();
		//	Log.i(TAG, "selected keys has :"+selector.selectedKeys().size());
				SelectionKey sk = si.next();
				if(sk.isAcceptable()){
					
					try{
				    
						clientChannel=((ServerSocketChannel)sk.channel()).accept();
						
						clientChannel.configureBlocking(true);
					//	clientChannel.register(sk.selector(), SelectionKey.OP_READ);
						ObjectOutputStream oos = new ObjectOutputStream(clientChannel.socket().getOutputStream());
						oos.writeObject(ip);
					//	Thread.sleep(2000);
					//	handler = new Handler(clientChannel,p);
				//		handler.startService();
						clientChannel.configureBlocking(false);
						}catch(IOException e){
						
						} 
						
				}
			
		

	}
	
	private void Stop(){
		//send very client "DISCONNECT" and close stream.
	}
	
	public String getIP(){
		return server.socket().getInetAddress().getHostAddress();
	}
	
	public boolean ok(){
		if(client ==null)
			return false;
		return true;
	}
	
	public void start(){
		st = new Thread(this);
		st.start();
	}
	
	public AudioProfile getAP(){
		while(handler==null);
		return handler.getAP();
	}
}
