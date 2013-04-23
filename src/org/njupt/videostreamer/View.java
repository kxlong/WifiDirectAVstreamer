package org.njupt.videostreamer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class View extends Activity {
	//Button start=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        
        final int nWelcomeScreenDisplay=3000;
        new Handler().postDelayed(new Runnable(){
        
        	public void run(){
        		Intent mainIntent=new Intent(View.this,ViewStartActivity.class);
        		startActivity(mainIntent);
        		View.this.finish();
        	}
        },nWelcomeScreenDisplay);
        //start=(Button)findViewById(R.id.start);
        //start.setOnClickListener(new OnClickListener(){
        	
		/*	@Override
			public void onClick(android.view.View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(View.this, ViewStart.class);
				startActivity(intent);
//				View.this.finish();
			}
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
