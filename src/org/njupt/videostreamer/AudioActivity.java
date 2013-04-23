package org.njupt.videostreamer;

//import com.view.R;
//import com.view.ViewStart;
//import com.view.Voice;

import java.nio.channels.Pipe;

import org.njupt.videostreamer.SessionManager.SessionManager;
import org.njupt.videostreamer.utils.PeerListAdapter;
import org.njupt.videostreamer.utils.WifiDirector;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class AudioActivity extends Activity implements OnClickListener,
		Runnable {

	// private TextView textview=null;
	public ProgressDialog mydialog = null;
	ImageButton setup = null, search = null, session = null;
	ListView peers = null;
	Pipe p = null;
	WifiDirector director = null;
	PeerListAdapter padapter = null;
	SessionManager sm = null;
	Thread sessionThread = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio);
		// session = (Button) this.findViewById(R.id.session);
		setup = (ImageButton) this.findViewById(R.id.setup);
		search = (ImageButton) this.findViewById(R.id.search);
		peers = (ListView) this.findViewById(R.id.peers);
		search.setOnClickListener(this);
		setup.setOnClickListener(this);
		// session.setOnClickListener(this);
		director = new WifiDirector(this);
		director.initiallize();
		if (peers == null)
			Toast.makeText(this, "listview non", Toast.LENGTH_SHORT).show();
		peers.setAdapter(director.getPadapter());
		peers.setOnItemClickListener(director);
		director.register();

		/*
		 * imagebutton2.setOnClickListener(new OnClickListener(){ public void
		 * onClick(android.view.View v){ showDialog(AudioActivity.this); }
		 * private void showDialog(Context context) { // TODO Auto-generated
		 * method stub LayoutInflater
		 * factory=LayoutInflater.from(AudioActivity.this); View
		 * view=factory.inflate(R.layout.newgroup, null); AlertDialog.Builder
		 * dialog=new AlertDialog.Builder(AudioActivity.this);
		 * dialog.setTitle("create a new group"); dialog.setView(view);
		 * dialog.setPositiveButton("cancle", null);
		 * dialog.setNegativeButton("OK",null); dialog.show(); } });
		 */
		sessionThread = new Thread(this);
		sessionThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.audio, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.setup) {
			this.onSetupClick();
		}
		if (v.getId() == R.id.search) {
			this.onSearchClick();
		}

	}

	@Override
	protected void onPause() {
		unregisterReceiver(director);
		if(sessionThread != null)
			sessionThread.interrupt();
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		director.register();

	}

	private void onSearchClick() {
		// Toast.makeText(this, "searching up", Toast.LENGTH_LONG).show();
		director.searchPeers();
		mydialog = ProgressDialog.show(AudioActivity.this,
				"Searching Peers...", "Wait...", true);
		new Thread() {
			public void run() {
				try {
					sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mydialog.dismiss();
				}
			}
		}.start();
	}

	private void onSetupClick() {
		Toast.makeText(this, "setting up", Toast.LENGTH_SHORT).show();
		director.setup();
	}

	@Override
	public void run() {
		// 查询是否成功建立连接，随后判断是否是groupowner，再决定sessionmanager是选择加入还是建立。
		WifiP2pInfo info = director.getConnectionInfo();
		while (info == null) {
			info = director.getConnectionInfo();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	
		sm = new SessionManager(8001);
		if (info.isGroupOwner) {
			sm.setupSession();
			sm.startManagering();
		} else {
			Log.d("ACTIVITY", "join session");
			sm.joinSession(info.groupOwnerAddress, 8001);
			sm.startManagering();
		}
	}
}
