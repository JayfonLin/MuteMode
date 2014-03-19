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
	 * �绰������
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
	 * ����״̬����
	 */
	class TeleListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: {
				/**
				 * ����绰������������������
				 */
				System.out.println("�绰����");
				/*if(amp!=null){
					music_play(context);
				}*/
				break;
			}
			case TelephonyManager.CALL_STATE_OFFHOOK:
			case TelephonyManager.CALL_STATE_RINGING: {
				/**
				 * ���ͻȻ�绰������ֹͣ��������
				 */

				System.out.println("�绰����");
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
							msg = "�ѹص��������𶯣�";
							//tView.setText(getString(R.string.tip)+"����");
							
						}else {
							audioM.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							msg = "�Ѵ�����";
							//tView.setText(getString(R.string.tip)+"��ͨ");
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
