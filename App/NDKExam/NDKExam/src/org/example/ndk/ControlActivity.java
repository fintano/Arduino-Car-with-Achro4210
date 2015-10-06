package org.example.ndk;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

public class ControlActivity extends Activity {

	NDKExam inputControl;
	ArduinoInputThread AIT;
	InputToBoard ITB;
	Handler inputHandler;

	int highbyte, lowbyte;
	int completebyte;
	int count = 0;
	int dist_flag;
	int left_dist;
	int front_dist;
	int right_dist;
	
	int total_dist;
	int left_count;
	int front_count;
	int right_count;
	
	boolean isComplete = false;

	IInputService mBinder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.control);

		Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				synchronized (inputControl) {
					switch (msg.what) {
					/*
					 * Send gpio pressed button to Arduino GPIO is menu
					 */
					case Constant.GPIOMSG:
						System.out.println(msg.arg1);
						sendGpioInfo(msg.arg1);
						break;
					/*
					 * Send fpga pressed button to Arduino FPGA is control of
					 * Car
					 */
					case Constant.FPGAMSG:
						char[] tmpCh = (char[]) msg.obj;
						sendFpgaInfo(tmpCh);
					}
				}
			}
		};

		/*
		 * ArduinoInputThread Handler
		 */

		ITB = new InputToBoard();

		inputHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					
					/*
					 * Combine 2 bytes to int(short)
					 */
					
					if (!isComplete) {
						highbyte = msg.arg1;
						dist_flag = (highbyte&0x30)>>4;
						highbyte=highbyte&0x0f;
						isComplete = true;
					} else {
						lowbyte = msg.arg1;
						completebyte = ((highbyte & 0x7f) << 7)
								| (lowbyte & 0x7f);
						/*
						 *  flag
						 *  1 : right side
						 *  2 : front side 
						 *  3 : left side
						 */
						System.out.printf("flag : %d / complete byte : %d\n",dist_flag, completebyte);
						
						if(dist_flag==1){
							right_dist = completebyte;
						}
						else if(dist_flag==2){
							front_dist = completebyte;
						}
						else if(dist_flag==3){
							left_dist = completebyte;
						}
						else if(dist_flag==0){		//mode 2 is end, calculate total distance
							right_count = (completebyte>>8);
							front_count = (completebyte&0xff)>>4;
							left_count = completebyte&0x0f;
							System.out.printf("r:%d / f:%d / l:%d\n",right_count,front_count,left_count);
							total_dist = 120*(right_count+left_count)+ 300*front_count;
						}
						
						if(dist_flag==2){
							ITB.setDistanceToFnd(front_dist);
							ITB.setBuzzer(front_dist);
						}
						else if(dist_flag==3){
							ITB.setDot(left_dist, front_dist, right_dist);
						}
						
						
						ITB.setLCD(total_dist);						
						

						isComplete = false;
					}
				}
			}
		};
		
		
		/*
		 * Add Constant.jniObject, but it's hard to 
		 * revise all codes
		 */

		Constant.jniObject = new NDKExam(mHandler);
		inputControl = Constant.jniObject;
		//inputControl.gpioStartThread();
		startInputService();
		showDialog();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("Control Activity", "onResume()");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("Control Activity", "onPause()");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("Control Activity", "onStop()");
	}

	/*
	 * Show "Control using board .." Dialog !
	 */

	private void showDialog() {

		AlertDialog.Builder alert = new AlertDialog.Builder(
				ControlActivity.this);
		alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); // �떕湲�
			}
		});
		alert.setMessage("Control using board ..");
		alert.show();

		Dialog d = alert.show(); // �븣由쇱갹 �쓣�슦湲�
		// Change Background color of title
		int textViewId = d.getContext().getResources()
				.getIdentifier("android:id/alertTitle", null, null);
		TextView tv = (TextView) d.findViewById(textViewId);
		tv.setTextSize(24);
	}

	/*
	 * Input Thread Control (Arduino to App)
	 */

	void startThread(Handler mHandler) {
		if (!Constant.isInputStarted) {
			AIT = new ArduinoInputThread(mHandler);
			AIT.setDaemon(true);
			AIT.start();
			Constant.isInputStarted = true;
		}
	}

	void stopThread() {
		Constant.isInputStarted = false;
		if (AIT != null)
			AIT.terminate();
	}

	private void sendGpioInfo(int btn) {

		/*
		 * <Mode> a : Exit b : Driving Mode C : Free Mode , Gpio is always
		 * started
		 */

		try {
			switch (btn) {

			case 'a':

				System.out.println("A");
				Constant.out.write('a');

				/*
				 * Stop service 
				 */

				stopInputService();
				break;

			case 'b':

				System.out.println("B");
				Constant.out.write('b');
				Constant.out.flush();
				// Sleep for geting arduino data
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * Start and bind thread sending fpga buttons.
				 * stop TimerThread to reset timer
				 */

				startInputService();
				stopThread();
				//mBinder.changeEnabled(true);
				
				break;

			case 'c':

				System.out.println("C");
				Constant.out.write('c');
				Constant.out.flush();
				/*
				 * Stop threads and start autodriving mode
				 * It's controlled by arduino
				 * and start timer 
				 */ 
				startThread(inputHandler);
				mBinder.stopFpgaThread();
				//mBinder.changeEnabled(false);
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	/******************************************************************/
	/********************* Client request service *********************/
	/******************************************************************/

	IInputServiceCallback mInputCallback = new IInputServiceCallback.Stub() {

		@Override
		public void onFpgaChanged(char[] changedInputBtns)
				throws RemoteException {
			// TODO Auto-generated method stub

			// Log.i("Final Project", "onFpgaChanged()");
			System.out.println("OnFpgaChanged " + Integer.toString((int)changedInputBtns[3]));
			sendFpgaInfo(changedInputBtns);
		}

		@Override
		public void onGpioChanged(int changedGpioBtns) throws RemoteException {
			// TODO Auto-generated method stub
			System.out.println("OnGpioChanged " + Integer.toString(changedGpioBtns));
			sendGpioInfo(changedGpioBtns);
		}
	};

	private final ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBinder = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.i("Final Project", "Service Binding");

			mBinder = IInputService.Stub.asInterface(service);

			try {
				mBinder.registerInputCallback(mInputCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private void startInputService() {

		Intent intent = new Intent("org.example.service.FPGA");
		startService(intent);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private void stopInputService() {

		Intent intent = new Intent("org.example.service.FPGA");

		if (mBinder != null) {
			try {
				mBinder.unregisterInputCallback(mInputCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			unbindService(mConnection);
		}
		stopService(intent);
	}

	/****************************************************************************/

	private void sendFpgaInfo(char[] btns) {

		/*
		 * 1 : forward 2 : backward 3 : left 4 : right 0 : stop
		 */

		try {
			char[] btn = mBinder.getFpgaButtons();
			if (btn[1] == 1) {
				Constant.out.write(1);
			} else if (btn[3] == 1) {
				Constant.out.write(3);
			} else if (btn[4] == 1) {
				Constant.out.write(0);
			} else if (btn[5] == 1) {
				Constant.out.write(4);
			} else if (btn[7] == 1) {
				Constant.out.write(2);
			}
			Constant.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
