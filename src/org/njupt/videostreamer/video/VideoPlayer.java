package org.njupt.videostreamer.video;

import java.io.FileDescriptor;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.View;

public class VideoPlayer extends View implements Runnable{
	MediaMetadataRetriever mt=null;
	
	//似乎extend View类的话绘制动画可以override onDraw函数，请验证.
	public VideoPlayer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void getBitmap(InputStream is){
		//获取视频文件的帧图像。		
	}
	
	public void drawBitmap(Bitmap draw){
		//将获取的bitmap绘制在View上.
	}

	@Override
	public void run() {
		// 调用getBitmap(),drawBitmap()就可以进行连续画面的显示.
		
	}
	
	public void setSource(FileDescriptor fd){
		//用来设定MetaDataRetriever的DataSource,测试下是否能接收流来获取frame.
		if(mt!=null) mt.setDataSource(fd);
	}
}
