package org.njupt.videostreamer.SessionManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.njupt.videostreamer.utils.AudioProfile;

import android.util.Log;

public class SessionManager implements Runnable {
	// 鍙橀噺
	String TAG = "session manager";
	Hashtable<SocketChannel, AudioProfile> aplist = null;
	AudioSession as = null;
	ServerSocketChannel asServer = null;
	SocketChannel asClient = null, client = null;
	Selector server_selector = null;
	protected Vector<SocketChannel> clients = null, newClients = null,
			leavingClients = null;
	Hashtable<InetAddress, Integer> ipBook = null;
	Vector<InetAddress> ipkey = null;
	AudioProfile local_audioProfile = null;
	Thread wt = null;
	InetSocketAddress host = null;
	int port = 0;
	int m_mode = 0;

	public static int SETUP_MODE = 0, JOIN_MODE = 1;

	public SessionManager(int p) {
		port = p;
		as = new AudioSession(null);
		this.local_audioProfile = as.getAudioConfig();

		try {
			server_selector = Selector.open();
			this.host = new InetSocketAddress(port);
			asServer = ServerSocketChannel.open();
			asServer.socket().bind(host);
			Log.d(TAG, "SESSION AT IP :"
					+ asServer.socket().getLocalSocketAddress());
			asServer.configureBlocking(false);
			asServer.register(server_selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
		}
	}

	public void setupSession() {

		m_mode = this.SETUP_MODE;
		Log.d(TAG,
				"session OK,config is : " + this.local_audioProfile.toString());
		Log.d(TAG, "waiting for client");

		if (ipBook == null && ipkey == null) {
			ipBook = new Hashtable<InetAddress, Integer>();
			ipkey = new Vector<InetAddress>();
		}
	}

	public boolean joinSession(InetAddress host, int port) {

		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		AudioProfile nap = null;
		m_mode = this.JOIN_MODE;

		try {
			asClient = SocketChannel.open();
			asClient.connect(new InetSocketAddress(host.getHostAddress(), port));
			
			if (asClient == null)
				Log.d(TAG, "client failed ");
			if (!asClient.isConnected())
				return false;
			
			asClient.configureBlocking(true);
			ois = new ObjectInputStream(asClient.socket().getInputStream());
			oos = new ObjectOutputStream(asClient.socket().getOutputStream());
			nap = (AudioProfile) ois.readObject();
			Log.d(TAG, "receive server audioProfile  " + nap.toString());
			oos.writeObject(as.getAudioConfig());
			Log.d(TAG, "send server my profile" + nap.toString());
			ipBook = (Hashtable<InetAddress, Integer>) ois.readObject();
			this.ipkey = (Vector<InetAddress>) ois.readObject();
			asClient.configureBlocking(false);
			as.addClient(asClient, nap);
			asClient = null;

			for (int i = 0; i < this.ipkey.size(); i++) {

				asClient = SocketChannel.open();
				asClient.connect(new InetSocketAddress(ipkey.get(i), ipBook
						.get(ipkey.get(i))));
				asClient.configureBlocking(true);
				ois = new ObjectInputStream(asClient.socket().getInputStream());
				oos = new ObjectOutputStream(asClient.socket()
						.getOutputStream());

				asClient.configureBlocking(false);
				
				nap = (AudioProfile) ois.readObject();
				oos.writeObject(as.getAudioConfig());
				as.addClient(asClient, nap);
				asClient = null;

			}
			return true;
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		return false;

	}

	public void initRealTimeVideoSession() {

	}

	public void cancelSession() {

	}

	public Vector<SocketChannel> showClients() {
		return as.getClientList();
	}

	public void startManagering() {

		wt = new Thread(this);
		wt.start();
		as.sessionStrat();

	}

	private void serverhandleAccept() {
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		while (true) {
			try {

				this.server_selector.select();
				Log.d(TAG, "have connection request");
				Iterator<SelectionKey> keyIterator = this.server_selector
						.selectedKeys().iterator();

				while (keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();
					client = ((ServerSocketChannel) key.channel()).accept();
					client.configureBlocking(true);

					oos = new ObjectOutputStream(client.socket()
							.getOutputStream());

					oos.writeObject(as.getAudioConfig());

					ois = new ObjectInputStream(client.socket()
							.getInputStream());
					Log.d(TAG, "accepted client "
							+ client.socket().getInetAddress().toString());

					AudioProfile nap = null;

					nap = (AudioProfile) ois.readObject();

					if (this.m_mode == this.SETUP_MODE) {
						oos.writeObject(this.ipBook);
						oos.writeObject(this.ipkey);
						ipBook.put(client.socket().getInetAddress(),
								Integer.valueOf(client.socket().getLocalPort()));
						Log.d(TAG, "sent IPBOOK to new client");
					}

					client.configureBlocking(false);
					if (as != null || nap != null)
						as.addClient(client, nap);
					// 涓轰笅娆″鐞嗘柊鍔犲叆鐢ㄦ埛鍋氬噯澶�

					client = null;
					keyIterator.remove();
				}
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void handleJoin() {

	}

	@Override
	public void run() {
		this.serverhandleAccept();
	}

	public InetAddress getHOST() {
		return this.host.getAddress();
	}
}
