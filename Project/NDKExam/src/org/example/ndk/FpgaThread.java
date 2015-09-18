package org.example.ndk;

import android.os.Handler;
import android.os.Message;

public class FpgaThread extends Thread {

	Handler mHandler;
	NDKExam jniObject;

	private volatile boolean terminated = false;

	public void terminate() {
		terminated = true;
	}

	public FpgaThread(NDKExam jniObject, Handler mHandler) {
		// TODO Auto-generated constructor stub
		this.jniObject = jniObject;
		this.mHandler = mHandler;
	}

	@Override
	public void run() {

		char[] fpgaBtn;

		// Message msg = Message.obtain();
		// msg.what = Constant.FPGAMSG;
		Message msg;

		while (true) {
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// TODO Auto-generated catch

			msg = Message.obtain();
			msg.what = Constant.FPGAMSG;

			fpgaBtn = jniObject.getFpgaBtn(jniObject.fpgaFd);

			// System.out.println("IN THREAD" + (int)fpgaBtn[1]);

			msg.obj = fpgaBtn;

			synchronized (jniObject) {
				mHandler.sendMessage(msg);
			}

			if (terminated)
				break;

		}

		System.out.println("Fpga is Terminated");
	}

}
