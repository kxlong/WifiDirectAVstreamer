package org.njupt.videostreamer.server;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Vector;

import org.njupt.videostreamer.audio.AudioRecorder;
import org.njupt.videostreamer.transpoter.Sender;
import org.njupt.videostreamer.utils.AudioProfile;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

//Appointed by Server in a thread.It handles request of each client.
public class Handler implements Runnable, OnClickListener {

	SocketChannel aclient = null;
	String acname = null;
	boolean serving = false;
	Pipe.SourceChannel ps = null;
	Pipe.SinkChannel sink = null;
	Thread m = null;
	AudioRecorder ar = null;
	Sender sender = null;

	public Handler(SocketChannel client, Pipe p) {
		ar = new AudioRecorder();
		aclient = client;
		ps = p.source();
		sink = p.sink();
		Vector<SocketChannel> vc = new Vector<SocketChannel>();
		vc.add(client);
		sender = new Sender(vc, 1);
		sender.setAudioSource(ps);
		sender.setPacketSize(1024);
		ar.setSink(sink);
	}

	@Override
	public void run() {
		SelectionKey next = null;
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		Log.d("handler", "handler start");
		ar.startRecording();
		sender.sendAudio();
		/*
		while (serving) {

			if (aclient == null) {
				// Log.d("handler",
				// "got no client"+aclient.selectedKeys().size());
				continue;
			}

			// Log.d("handler", "got client");

			try {
			//	int a = ps.read(buffer);
			//	Log.d("handler", "recording data " + a);
			//	buffer.flip();
				
			//	int ab = aclient.write(buffer);
		//		Log.d("handler", "handler writing to channel " + ab);
			} catch (IOException e) {
			}
			buffer.clear();
		}*/
	}

	private void handlePlay(Socket client) {
		// handle the on-demand play request.
		// pass socket to ServerPlayHandler.

	}

	private void handleConnect() {
		// Accept connection if not meeting the limit.
		// put client socket into socketlist.

	}

	public void stopService() {
		serving = false;
	}

	public void startService() {
		serving = true;
		m = new Thread(this);
		m.start();
	}

	@Override
	public void onClick(View arg0) {
		this.startService();

	}

	public AudioProfile getAP() {
		return ar.getAP();
	}
}
