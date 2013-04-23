package org.njupt.videostreamer.audio;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

import org.njupt.videostreamer.utils.AudioProfile;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioPlayer implements Runnable {

	private static String TAG = "audioplayer";
	private int stpye = -1;
	private int sfre = -1;
	private int chan = -1;
	private int code = -1;
	private int bsize = -1;
	private int mod = -1;
	private AudioTrack at = null;
	private byte[] buffer = null;
	private boolean playing = false;
	private Thread hardwork = null;
	private Pipe.SourceChannel spipe = null;
	ByteBuffer pbuffer = null;

	public void setInputStream(Pipe.SourceChannel is) {
		spipe = is;

	}

	public AudioPlayer() {

	}

	public void initPlayer(AudioProfile ap) {
		this.stpye = AudioManager.STREAM_MUSIC;
		this.mod = AudioTrack.MODE_STREAM;
		this.chan = ap.channel;
		this.sfre = ap.fre;
		this.code = ap.format;
		bsize = 2 * AudioTrack.getMinBufferSize(sfre, chan, code);
		// Log.d(TAG,
		// "profile is fre :"+this.sfre+" chan :"+this.chan+" format :"+this.code);
		at = new AudioTrack(stpye, sfre, chan, code, bsize, mod);
		if (at != null)
			Log.i("audioplayer",
					"Initial AudioTrack success,with buffer size :" + bsize);
		pbuffer = ByteBuffer.allocate(bsize);
	}

	public int getDataFromSource() {

		if (spipe.isOpen()) {
			try {
				Log.d(TAG, "getting data!");
				int n = spipe.read(pbuffer);
				Log.d(TAG, "player get from pipe " + n);
				buffer = pbuffer.array();
				pbuffer.clear();
				return n;
			} catch (IOException e) {
				Log.e(TAG, "get data frome pipe failed");
			}
		}
		return -1;
	}

	@Override
	public void run() {
		at.play();
		while (playing) {
			 this.getDataFromSource();
			 int n = at.write(buffer, 0, buffer.length);
			//Log.d(TAG, "player get data " + n);
		}

		if (at != null) {
			Log.d(TAG, "is playing ? "+this.playing);
			if (at.getState() == at.STATE_INITIALIZED){
				at.stop();
				at.release();
			hardwork.interrupt();
			}
		}

	}

	public void play() {
		playing = true;
		hardwork = new Thread(this);
		hardwork.start();
	}

	public boolean isPlaying() {
		return playing;
	}

	public void stop() {
		this.playing = false;
	}

}
