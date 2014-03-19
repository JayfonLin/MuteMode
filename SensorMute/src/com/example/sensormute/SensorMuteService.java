package com.example.sensormute;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SensorMuteService extends Service{
	private final IBinder binder = new SensorBinder();
	SensorManager mSensorManager;
	AudioManager audioM;
	Sensor sensor;
	String msg;
	int modeStr;
	OnMySensorChangeListener onMySensorChangeListener;
	/**
	 * 电话管理器
	 */
	private TelephonyManager mTelephonyMgr;
	
	public void setOnMySensorChangeListener(OnMySensorChangeListener onMySensorChangeListener){
		this.onMySensorChangeListener = onMySensorChangeListener;
	}
	public String getChangeMessage(){
		return msg;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	
	

	@Override
	public void onRebind(Intent intent) {
		audioM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		super.onRebind(intent);
	}
	@Override
	public IBinder onBind(Intent intent) {
		mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyMgr.listen(new TeleListener(),
				PhoneStateListener.LISTEN_CALL_STATE);
		Notification notification = new Notification(R.drawable.ic_launcher,
	               "my_service_name",
	                System.currentTimeMillis());
	    PendingIntent p_intent = PendingIntent.getActivity(this, 0,
	            new Intent(this, MainActivity.class), 0);
	    notification.setLatestEventInfo(this, "MyServiceNotification", 
	    		"MyServiceNotification is Running!",   p_intent);
	    Log.d("tag", String.format("notification = %s", notification));
	    startForeground(0x1982, notification);   // notification ID: 0x1982, you can name it as you will.
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		audioM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		modeStr = audioM.getRingerMode();
		sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		return binder;
	}
	public class SensorBinder extends Binder{
		
		SensorMuteService getService(){
			return SensorMuteService.this;
		}
	}
	
	/**
	 * 来电状态监听
	 */
	class TeleListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: {
				/**
				 * 如果电话结束，继续播放音乐
				 */
				System.out.println("电话挂了");
				/*if(amp!=null){
					music_play(context);
				}*/
				break;
			}
			case TelephonyManager.CALL_STATE_OFFHOOK:
			case TelephonyManager.CALL_STATE_RINGING: {
				/**
				 * 如果突然电话到来，停止播放音乐
				 */

				System.out.println("电话来了");
				//music_pause(context);
				break;
			}
			default:
				break;
			}
		}
	}
	
	public final SensorEventListener mSensorEventListener = 
			new SensorEventListener() {
				
				@Override
				public void onSensorChanged(SensorEvent event) {
					if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
						float fPitchAngle = event.values[SensorManager.DATA_Y];
						float x = fPitchAngle;
						float y = event.values[SensorManager.DATA_Z];
						
						msg = new String();
						if (fPitchAngle<-120 || fPitchAngle>120){
							audioM.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
							msg = "已关掉铃声（震动）";
							//tView.setText(getString(R.string.tip)+"静音");
							
						}else {
							audioM.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							msg = "已打开铃声";
							//tView.setText(getString(R.string.tip)+"普通");
						}
						msg += "\nx: "+x;
						msg += "\ny: "+y;
						if (onMySensorChangeListener != null)
							onMySensorChangeListener.onChange(msg);
					}
				}
				
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					
				}
			};
}
