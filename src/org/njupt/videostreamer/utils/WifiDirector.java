package org.njupt.videostreamer.utils;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.njupt.videostreamer.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Toast;

public class WifiDirector extends BroadcastReceiver implements
		OnItemClickListener, ConnectionInfoListener, ChannelListener,GroupInfoListener {

	private final IntentFilter intentFilter = new IntentFilter();
	private WifiP2pManager manager = null;
	private Channel mChannel;
	private Context sContext = null;
	private Looper mLooper = null;
	private Activity mActivity = null;
	private List<WifiP2pDevice> plist = new ArrayList<WifiP2pDevice>();
	private PeerListAdapter padapter = null;
	private WifiP2pInfo c_info = null;
	private WifiP2pGroup groupinfo =null;
	private boolean isGroupOwner = false, alreadyFormed = false;
	private InetAddress groupOwnerAddress = null;

	public WifiDirector(Activity activity) {
		this.mActivity = activity;
		manager = (WifiP2pManager) mActivity
				.getSystemService(Context.WIFI_P2P_SERVICE);
		sContext = mActivity;
		mLooper = mActivity.getMainLooper();
	}

	public void initiallize() {
		padapter = new PeerListAdapter(sContext, R.layout.row_devices, plist);
		// Indicates a change in the Wi-Fi Peer-to-Peer status.
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

		// Indicates a change in the list of available peers.
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

		// Indicates the state of Wi-Fi P2P connectivity has changed.
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

		// Indicates this device's details have changed.
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		if (padapter == null)
			Log.d("director", "adapter failed");

		mChannel = manager.initialize(this.sContext, this.mLooper, this);

	}

	public void searchPeers() {

		manager.discoverPeers(mChannel, new ActionListener() {

			@Override
			public void onFailure(int arg0) {
				Log.d("director", "peers discovery failed ");

			}

			@Override
			public void onSuccess() {
				Log.d("director", "peers discovery success ");
			}

		});
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(intent
				.getAction())) {
			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {
				this.alreadyFormed = true;
				manager.requestConnectionInfo(mChannel, this);
			} else
				Toast.makeText(mActivity, "No connection", Toast.LENGTH_SHORT)
						.show();
		}
		//发现了新的peers，通知adpter更新.
		if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(intent
				.getAction())) {
			manager.requestPeers(mChannel, padapter);
		}
		
	}

	public void setup() {
		if (mChannel != null)
			manager.createGroup(mChannel, new ActionListener() {

				@Override
				public void onFailure(int arg0) {

				}

				@Override
				public void onSuccess() {
				}

			});
	}

	public List<WifiP2pDevice> getPlist() {
		return plist;
	}

	public void setPlist(List<WifiP2pDevice> plist) {
		this.plist = plist;
	}

	public PeerListAdapter getPadapter() {
		return padapter;
	}

	public void setPadapter(PeerListAdapter padapter) {
		this.padapter = padapter;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		ListView list = (ListView) arg0;
		WifiP2pDevice device = (WifiP2pDevice) list.getItemAtPosition(position);
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;

		manager.connect(mChannel, config, new ActionListener() {

			@Override
			public void onFailure(int arg0) {
				Log.d("director", "connect failed");

			}

			@Override
			public void onSuccess() {
				Log.d("director", "connect success ");

			}

		});

	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		this.c_info = info;
	
		if (this.isGroupOwner = info.isGroupOwner) {
			Toast.makeText(sContext, "create Group success!",
					Toast.LENGTH_SHORT).show();
		}

		this.groupOwnerAddress = info.groupOwnerAddress;
		Log.d("director", "is owner? " + info.isGroupOwner
				+ " Owner Address is :" + info.groupOwnerAddress);

	}

	public void disconnect() {
		manager.cancelConnect(mChannel, new ActionListener() {

			@Override
			public void onFailure(int arg0) {

			}

			@Override
			public void onSuccess() {
			}

		});
		manager.removeGroup(mChannel, new ActionListener() {

			@Override
			public void onFailure(int arg0) {
			}

			@Override
			public void onSuccess() {
			}

		});
	}

	public void register() {
		this.mActivity.registerReceiver(this, intentFilter);
	}

	@Override
	public void onChannelDisconnected() {
		// Channel失效则重新初始化.
		manager.initialize(sContext, mLooper, this);
		Log.d("director", "channel 失效,重新初始化");

	}

	@Override
	public void onGroupInfoAvailable(WifiP2pGroup arg0) {
		//获取组信息.
		groupinfo = arg0;	
	}
	
	public boolean isConnected(){
		return alreadyFormed;
	}
	
	public WifiP2pInfo getConnectionInfo(){
		return this.c_info;
	}
}
