package org.example.ndk;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class ControlActivity extends Activity {

	NDKExam inputControl;
	ArduinoInputThread AIT;
	InputToBoard ITB;
	Handler inputHandler;

	int highbyte, lowbyte;
	int completebyte;
	int count = 0;
	int left_dist;
	int front_dist;
	int right_dist;

	boolean isComplete = false;

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
						isComplete = true;
					} else {
						lowbyte = msg.arg1;
						completebyte = ((highbyte & 0x7f) << 7)
								| (lowbyte & 0x7f);
						System.out.println(completebyte);
						if (count % 3 == 0) {
							right_dist = completebyte;
						} else if (count % 3 == 1) {
							front_dist = completebyte;
						} else if (count % 3 == 2) {
							left_dist = completebyte;
						}
						count++;

						ITB.setDistanceToFnd(front_dist);
						ITB.setDot(left_dist, front_dist, right_dist);
						ITB.setBuzzer(front_dist);

						/*
						 * 
						 * 
						 * 
						 * 
						 */
						isComplete = false;
					}
				}
			}
		};

		inputControl = new NDKExam(mHandler);
		inputControl.gpioStartThread();
		showDialog();
	}

	/*
	 * Show Dialog !
	 */
	private void showDialog() {

		AlertDialog.Builder alert = new AlertDialog.Builder(
				ControlActivity.this);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); // 닫기
			}
		});
		alert.setMessage("Control using board ..");
		alert.show();

		Dialog d = alert.show(); // 알림창 띄우기
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

		System.out.println(btn);

		try {
			switch (btn) {

			case 'a':

				System.out.println("A");
				Constant.out.write('a');
				inputControl.fpgaStopThread();
				break;

			case 'b':

				System.out.println("B");
				Constant.out.write('b');
				// Sleep for arduino
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				inputControl.fpgaStartThread();
				break;

			case 'c':

				System.out.println("C");
				Constant.out.write('c');

				// Stop threads and start free mode
				// It's controlled by arduino

				inputControl.fpgaStopThread();
				// Start input Thread receiving data of car
				startThread(inputHandler);
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendFpgaInfo(char[] btn) {

		/*
		 * StringBuilder str = new StringBuilder(); for (int i = 0; i < 9; i++)
		 * { str.append((int) btn[i] + " "); } System.out.println(str);
		 */

		/*
		 * 1 : forward 2 : backward 3 : left 4 : right 0 : stop
		 */
		try {
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
		}
	}

}
