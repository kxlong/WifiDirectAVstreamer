package org.njupt.videostreamer.audio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

import org.njupt.videostreamer.utils.AudioProfile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder implements Runnable{
	private static String TAG="AudioRecorder";
	private static int audioSource = MediaRecorder.AudioSource.MIC;
	private static int _sampleRate = 0 ;
	private static int _channelConfig  = 0;
	private static int _audioFormat = 0;
	private static int _bufferSize = 0;//Size(bytes) got from getMinBufferSize();
	private AudioRecord recorder = null;	
	private boolean recording = false;
	private Pipe.SinkChannel sink = null;
	private Thread recordThread = null;
	private AudioProfile ap = null;
	
 	public AudioRecorder( ){		
		recorder = findAudioRecord();
		ap = new AudioProfile(this._sampleRate,this._channelConfig,this._audioFormat);
	}
	
	public boolean startRecording(){
		
		if(!recording){
			
			recorder.startRecording();
			recording = true;
			
			if(recorder.getState()== AudioRecord.STATE_UNINITIALIZED){
				recording = false;Log.d(TAG,"ARecorder start failed");
			}
			else {
				recordThread = new Thread(this);
				recordThread.start();		Log.d(TAG,"ARecorder start");
			}
			
		}

		return recording;
	}
	
	public int getData( ){
		//get data from hardware.
		byte[] data = new byte[_bufferSize/2];
		int bufferRead = recorder.read(data, 0, data.length);
		
		//Write read bytes to Pipe.
		try {
			sink.write(ByteBuffer.wrap(data));
		} catch (IOException e) {
			Log.e(TAG,"ByteBuffer wrap failed"+e.getMessage());
		}
		return bufferRead;
	}
	
	public int getBufferSize(){
		return _bufferSize/2;		
	}
	
	public int getFormat(){
		return this._audioFormat;
	}
	
	public int getFrequen(){
		return this._sampleRate;
	}
	
	public int getChannel(){
		return this._channelConfig;
	}
	
	public boolean isRecording(){
		return recording;
	}
	
	public void stopRecording(){
		recording = false;
	}
	
	public boolean isInit(){
		return recorder.getState()==recorder.STATE_INITIALIZED;
	}
	
	private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
	
	public AudioRecord findAudioRecord() {
		
		for (int rate : mSampleRates) {
			for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
	            for (short channelConfig : new short[] { AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.CHANNEL_CONFIGURATION_STEREO }) {
	                try {
	                    Log.d("AUDIORECORDER", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
	                            + channelConfig);
	                    _bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

	                    if (_bufferSize != AudioRecord.ERROR_BAD_VALUE) {
	                        // check if we can instantiate and have a success
	                        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, _bufferSize);

	                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
	                        	_audioFormat = recorder.getAudioFormat();
	                        	_channelConfig = recorder.getChannelConfiguration();
	                        	_sampleRate = recorder.getSampleRate();
	                            return recorder;
	                        }
	                    }
	                } catch (Exception e) {
	                    Log.e("AUDIORECORDER", rate + "Exception, keep trying.",e);
	                }
	            }
	        }
	    }  
	    Log.d("AUDIORECORDER", "failed!!!!!!!");
	    return null;	    
	}
	
	public void releaseRecorder(){
		recorder.release();
	}

	@Override
	public void run() {
		if(this.getSink()==null) return;
		
		while(recording){
			getData();
		}
		recorder.stop();
		recorder.release();
		recordThread.interrupt();
	}

	public Pipe.SinkChannel getSink() {
		return sink;
	}

	public void setSink(Pipe.SinkChannel sink) {
		this.sink = sink;
	}
	
	public AudioProfile getAP(){
		return this.ap;
	}
}

