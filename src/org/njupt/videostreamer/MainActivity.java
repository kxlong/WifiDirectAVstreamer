package org.njupt.videostreamer;

import java.nio.channels.Pipe;

import org.njupt.videostreamer.server.Server;
import org.njupt.videostreamer.utils.PeerListAdapter;
import org.njupt.videostreamer.utils.WifiDirector;
import org.njupt.videostreamer.video.*;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, Runnable {

	Button setup =null;
	ListView peers = null;
	Pipe p = null;
	WifiDirector director = null;
	PeerListAdapter padapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setup = (Button) this.findViewById(R.id.setup);
		peers = (ListView) this.findViewById(R.id.peers);

		setup.setOnClickListener(this);
		director = new WifiDirector(this);
		director.initiallize();
		peers.setAdapter(director.getPadapter());
		
		setup.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {

		Toast.makeText(this, "setting up", Toast.LENGTH_LONG).show();
		director.setup();
	}

	@Override
	public void run() {

	}

	@Override
	protected void onPause() {
		super.onPause();
   //     unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}

// sm = new Server();
// sm.start();

/*
 * new Thread(new Runnable(){
 * 
 * @Override public void run() { //SessionManager client = new
 * SessionManager(8003); //client.joinSession(m.getHOST(), 8002);
 * //client.startManagering();
 * 
 * try { Selector sl = Selector.open(); SocketChannel client =
 * SocketChannel.open(); client.connect(new
 * InetSocketAddress(m.getHOST(),8002)); if(client.isConnected())
 * Log.d("main","server accepted");
 * 
 * client.configureBlocking(true); ObjectInputStream ois = new
 * ObjectInputStream(client.socket().getInputStream());
 * ObjectOutputStream oos = new
 * ObjectOutputStream(client.socket().getOutputStream());
 * 
 * AudioProfile nap = (AudioProfile) ois.readObject(); Log.d("main",
 * "client receive profile"+nap.toString()); oos.writeObject(new
 * AudioProfile(1,30,30)); Hashtable<InetAddress,Integer> ipBook =
 * (Hashtable<InetAddress, Integer>) ois.readObject();
 * Vector<InetAddress> ipkey = (Vector<InetAddress>) ois.readObject();
 * client.configureBlocking(false); } catch (IOException e) { } catch
 * (ClassNotFoundException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); }
 * 
 * }
 * 
 * }).start();
 */
