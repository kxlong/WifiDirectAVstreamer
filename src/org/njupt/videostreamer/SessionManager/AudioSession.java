package org.njupt.videostreamer.SessionManager;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Vector;

import org.njupt.videostreamer.audio.AudioPlayer;
import org.njupt.videostreamer.audio.AudioRecorder;
import org.njupt.videostreamer.transpoter.Receiver;
import org.njupt.videostreamer.transpoter.Sender;
import org.njupt.videostreamer.utils.AudioProfile;

import android.util.Log;

public class AudioSession extends Session implements Runnable {

	String TAG = "AudioSession";
	Sender aSender = null;
	Receiver aReceiver = null;
	Thread record = null, st = null;
	AudioRecorder sampler = null;
	Hashtable<SocketChannel, AudioProfile> profiles = null;
	private AudioProfile sampler_config = null;
	Pipe sendPipe = null;
	Hashtable<SocketChannel, Pipe> receivePipe = null;
	Hashtable<SocketChannel, AudioPlayer> players = null;
	boolean ready = false;
	boolean stop = true;
	int clientNum = 0;

	int streamFre = 0;
	int streamFormat = 0;
	int streamChannel = 0;

	public AudioSession(Vector<SocketChannel> list) {

		if (list != null) {
			clients = list;
			clientNum = clients.size();
		} else {
			clients = new Vector<SocketChannel>();
			clientNum = 0;
		}

		newClients = new Vector<SocketChannel>();
		leavingClients = new Vector<SocketChannel>();

		try {
			this.profiles = new Hashtable<SocketChannel, AudioProfile>();
			sendPipe = Pipe.open();
			receivePipe = new Hashtable<SocketChannel, Pipe>();
			aSender = new Sender(clients, Sender.SEND_AUDIO);
			aReceiver = new Receiver(clients, Receiver.RECEIVE_AUDIO);
			sampler = new AudioRecorder();
			sampler_config = new AudioProfile(sampler.getFrequen(),
					sampler.getChannel(), sampler.getFormat());
			players = new Hashtable<SocketChannel, AudioPlayer>();

		} catch (IOException e) {
			Log.d(TAG, e.getMessage() + "Pipe Open Failed");
		}

	}

	public AudioProfile getAudioConfig() {
		if (sampler_config != null)
			return this.sampler_config;
		return null;
	}

	public void setPfMap(Hashtable<SocketChannel, AudioProfile> aplist) {
		if (aplist != null)
			this.profiles = aplist;
	}

	@Override
	public void send() {
		sampleAudio(sendPipe.sink());
		aSender.setPacketSize(sampler.getBufferSize());
		aSender.setAudioSource(sendPipe.source());
		aSender.sendAudio();
	}

	@Override
	public void receive() {
		aReceiver.setPipeMap(receivePipe);
		aReceiver.receive();
		this.playAudio();

	}

	public void sampleAudio(Pipe.SinkChannel buffer) {
		sampler.setSink(buffer);
		sampler.startRecording();
	}

	public void playAudio() {

		for (SocketChannel tmp : clients) {
			players.get(tmp).play();
			Log.d(TAG, "start player"+players.get(tmp).isPlaying());
		}
	}

	@Override
	public void init() {

		this.aReceiver.setPipeMap(receivePipe);

		while (!this.isReady()) {
			this.checkClientList();
			Log.d(TAG, "waiting for client in initiallizing");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}

	}

	@Override
	public void over() {
		sampler.stopRecording();
		for (AudioPlayer p : players.values()) {
			p.stop();
		}
		players = null;
		sampler = null;

		st.interrupt();

	}

	@Override
	public void run() {

		this.init();
		this.send();
		this.receive();

		while (!stop){
			this.checkClientList();
			this.checkInterval();
		}

		this.over();
	}

	public synchronized boolean isReady() {
		if (clientNum > 0)
			ready = true;
		else
			ready = false;
		return ready;

	}

	@Override
	protected void checkClientList() {
		

		if (newClients.size() > 0) {
			Log.d(TAG, "adding new client : ");
			for (SocketChannel s : newClients) {
				clients.add(s);
				clientNum++;
				try {
					receivePipe.put(s, Pipe.open());
					AudioPlayer np = new AudioPlayer();
					np.initPlayer(profiles.get(s));
					np.setInputStream(receivePipe.get(s).source());
					players.put(s, np);
				} catch (IOException e) {
					Log.d(TAG, "Pipe in Adding new Clients failed");
				}
				newClients.remove(s);
			}
			this.isReady();
		}

		// 澶勭悊绂诲紑鐢ㄦ埛.
		if (leavingClients.size() > 0) {
			clientNum--;
		}

		// 寮�鎾斁鏂板姞鍏ョ敤鎴风殑闊抽.
		for (AudioPlayer a : players.values()) {
			if (a.isPlaying())
				continue;
			else
				a.play();
		}

	}

	public void addClient(SocketChannel nClient, AudioProfile nap) {
		newClients.add(nClient);
		profiles.put(nClient, nap);
	}

	public void sessionStrat() {
		this.stop = false;
		st = new Thread(this);
		st.start();
	}
	
	private void checkInterval() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
