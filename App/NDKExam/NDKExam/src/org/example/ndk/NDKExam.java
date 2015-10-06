package org.example.ndk;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

public class NDKExam implements Parcelable {

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

	public void gpioStopThread() {
		Constant.isGpioStarted = false;
		if (GT != null)
			GT.terminate();

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}
}
