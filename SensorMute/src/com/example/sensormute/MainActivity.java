package com.example.sensormute;

import java.util.logging.LogRecord;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	Button buttonOpen, buttonClose;
	TextView tView;
	int modeStr;
	SensorMuteService sms;
	Context context = this;
	ServiceConnection sc = new ServiceConnection() {
        
        public void onServiceDisconnected(ComponentName name) {
            sms = null;
        }
        
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			sms = ((SensorMuteService.SensorBinder)arg1).getService();
			sms.setOnMySensorChangeListener(new OnMySensorChangeListener() {
				
				@Override
				public void onChange(String str) {
					tView.setText(str);
				}
			});
		}
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		buttonOpen = (Button) findViewById(R.id.button1);
		buttonClose = (Button) findViewById(R.id.button2);
		tView = (TextView) findViewById(R.id.textView1);
		Intent intent = new Intent(context, SensorMuteService.class);
        startService(intent);
		
		
		buttonOpen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sms.mSensorManager.registerListener(sms.mSensorEventListener, sms.sensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
		});
		buttonClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sms.mSensorManager.unregisterListener(sms.mSensorEventListener);
			}
		});
		
	}
	@Override
	protected void onResume(){
		Intent bindent = new Intent(context, SensorMuteService.class);
		bindService(bindent, sc, BIND_ADJUST_WITH_ACTIVITY);//bug
		super.onResume();
		//tView.setText(getString(R.string.tip)+modeStr);
	}
	@Override
	protected void onPause(){
		super.onPause();
		unbindService(sc);
		MainActivity.this.onDestroy();
		MainActivity.this.finish();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
