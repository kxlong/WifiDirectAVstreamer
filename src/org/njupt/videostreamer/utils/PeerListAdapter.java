package org.njupt.videostreamer.utils;

import java.util.ArrayList;
import java.util.List;

import org.njupt.videostreamer.R;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PeerListAdapter extends ArrayAdapter<WifiP2pDevice> implements
		PeerListListener {

	Context mContext = null;
	List<WifiP2pDevice> items = new ArrayList<WifiP2pDevice>();

	public PeerListAdapter(Context context, int textViewResourceId,
			List<WifiP2pDevice> objects) {
		super(context, textViewResourceId, objects);
		mContext = context;
		items = objects;
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList arg0) {
		
		items.clear();
		items.addAll(arg0.getDeviceList());
		this.notifyDataSetChanged();
		Toast.makeText(this.mContext, "found peers", Toast.LENGTH_SHORT).show();
		if (items.size() == 0) {
			Log.d("director", "no device available");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_devices, null);
		}
		WifiP2pDevice device = items.get(position);
		if (device != null) {
			TextView top = (TextView) v.findViewById(R.id.device_name);
			TextView bottom = (TextView) v.findViewById(R.id.device_details);
			if (top != null) {
				top.setText(device.deviceName);
			}
			if (bottom != null) {
				bottom.setText(getDeviceStatus(device.status));
			}
		}

		return v;

	}

	private static String getDeviceStatus(int deviceStatus) {
		Log.d("PeerListAdapter", "Peer status :" + deviceStatus);
		switch (deviceStatus) {
		case WifiP2pDevice.AVAILABLE:
			return "Available";
		case WifiP2pDevice.INVITED:
			return "Invited";
		case WifiP2pDevice.CONNECTED:
			return "Connected";
		case WifiP2pDevice.FAILED:
			return "Failed";
		case WifiP2pDevice.UNAVAILABLE:
			return "Unavailable";
		default:
			return "Unknown";

		}
	}

}
