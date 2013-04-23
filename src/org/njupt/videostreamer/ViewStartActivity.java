package org.njupt.videostreamer;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class ViewStartActivity extends Activity {
	ImageButton imagebutton1=null;
	ImageButton imagebutton2=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_start);
		  imagebutton1=(ImageButton)findViewById(R.id.imageButton1);
	       imagebutton1.setOnClickListener(new OnClickListener(){
	       
				@Override
				public void onClick(android.view.View v) {
					// TODO Auto-generated method stub
					Intent intent =new Intent();
					intent.setClass(ViewStartActivity.this,AudioActivity.class);
					ViewStartActivity.this.startActivity(intent);	
					
				}
	        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_start, menu);
		return true;
	}

}
