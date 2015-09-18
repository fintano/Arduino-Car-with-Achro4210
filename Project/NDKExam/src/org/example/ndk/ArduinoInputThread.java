package org.example.ndk;

import java.io.IOException;

import android.os.Handler;
import android.os.Message;

public class ArduinoInputThread extends Thread {

	Handler mHandler;
	Message msg;

	private volatile boolean terminated = false;

	public void terminate() {
		terminated = true;
	}

	public ArduinoInputThread(Handler mHandler) {
		this.mHandler = mHandler;
	}

	@Override
	public void run() {

		int inputData = 0;

		while (true) {

			// @@@@@@@@@@@@@@ revision
			try {
				Thread.sleep(200); // delay 0.2sec
				System.out.println("Ready to input ...");
				msg = Message.obtain();

				/*
				 * 1 : distance
				 */

				msg.what = 1;
				if ((inputData = Constant.in.read()) != -1) {

					/* except Carriage return && line Feed */
					if (inputData != 10 && inputData != 13) {
						System.out.println("Input  " + inputData);
						msg.arg1 = inputData;
						mHandler.sendMessage(msg);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (terminated)
				break;
		}
	}
}
