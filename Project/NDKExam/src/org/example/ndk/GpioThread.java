package org.example.ndk;

import android.os.Handler;
import android.os.Message;

public class GpioThread extends Thread {

	int fd, adrs;
	NDKExam jniObject;
	Handler mHandler;
	private volatile boolean terminated = false;

	public GpioThread(NDKExam jniObject, Handler mHandler) {
		// TODO Auto-generated constructor stub
		this.fd = jniObject.gpioFd;
		this.adrs = jniObject.address;
		this.jniObject = jniObject;
		this.mHandler = mHandler;
	}

	@Override
	public void run() {

		int whichItr;
		Message msg;

		while (true) {
			msg = Message.obtain();
			msg.what = Constant.GPIOMSG;
			whichItr = jniObject.getCmd();
			System.out.println(whichItr);
			msg.arg1 = whichItr;
			synchronized (jniObject) {
				mHandler.sendMessage(msg);
			}
			if (terminated)
				break;
		}
		System.out.println("Gpio is Terminated");
	}

	public void terminate() {
		terminated = true;
	}

}
