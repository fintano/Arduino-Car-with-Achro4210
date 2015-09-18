package org.example.ndk;

import android.os.Handler;
import android.widget.TextView;

public class NDKExam {

	public native void getFD();

	public native int getCmd();

	public native char[] getFpgaBtn(int fd);

	public int gpioFd;
	public int fpgaFd;
	public int address;
	TextView fpgaText;
	TextView gpioText;
	NDKExam test;
	GpioThread GT;
	FpgaThread FT;

	Handler mHandler;

	public NDKExam(Handler mHandler) {

		System.loadLibrary("ndk-exam");
		// char[] fpgaBtn = new char[9];

		this.mHandler = mHandler;

		test = this;

		/*
		 * Get fd and address of device for interrupt
		 */

		test.getFD();

	}

	/*
	 * From board to app
	 */

	public void gpioStartThread() {

		if (!Constant.isGpioStarted) {
			GT = new GpioThread(test, mHandler);
			GT.setDaemon(true);
			GT.start();
			Constant.isGpioStarted = true;
		}
	}

	public void fpgaStartThread() {

		if (!Constant.isFpgaStarted) {
			FT = new FpgaThread(test, mHandler);
			FT.setDaemon(true);
			FT.start();
			Constant.isFpgaStarted = true;
		}
	}

	public void gpioStopThread() {
		Constant.isGpioStarted = false;
		if (GT != null)
			GT.terminate();

	}

	public void fpgaStopThread() {
		Constant.isFpgaStarted = false;
		if (FT != null)
			FT.terminate();
	}
}
