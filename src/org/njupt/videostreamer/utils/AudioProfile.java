package org.njupt.videostreamer.utils;

public class AudioProfile implements java.io.Serializable{
	
	public int fre = 0;
	public int channel = 0;
	public int format = 0;
	
	public AudioProfile(int fre,int chn,int form) {
		this.fre = fre;
		this.channel = chn;
		this.format = form;
	}

    public static byte[] intToByte(int i) {

        byte[] abyte0 = new byte[4];

        abyte0[0] = (byte) (0xff & i);

        abyte0[1] = (byte) ((0xff00 & i) >> 8);

        abyte0[2] = (byte) ((0xff0000 & i) >> 16);

        abyte0[3] = (byte) ((0xff000000 & i) >> 24);

        return abyte0;

    }

    public  static int bytesToInt(byte[] bytes) {

        int addr = bytes[0] & 0xFF;

        addr |= ((bytes[1] << 8) & 0xFF00);

        addr |= ((bytes[2] << 16) & 0xFF0000);

        addr |= ((bytes[3] << 24) & 0xFF000000);

        return addr;

    }
    
    public String toString(){
		return new String(this.fre+" "+this.channel+" "+this.format);
    	
    }
}

