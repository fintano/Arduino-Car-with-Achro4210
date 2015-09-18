package org.example.ndk;

public class InputToBoard {
	/*
	 * From Arduino to app
	 */
	public native void getFD();

	public native void outFnd(int fd, int data);

	public native void outDot(int fd, int data1, int data2, int data3);

	public native void outBuzzer(int fd, int data);

	/*
	 * 
	 * 
	 * 
	 * 
	 */
	InputToBoard test;

	private int lcdFd;
	private int dotFd;
	private int fndFd;
	private int buzzerFd;

	public InputToBoard() {

		System.loadLibrary("ndk-exam");

		test = this;

		/*
		 * Get fd of device
		 */

		test.getFD();
	}

	public void setDistanceToFnd(int data) {
		test.outFnd(fndFd, data);

	}

	public void setDot(int left_dist, int front_dist, int right_dist) {
		test.outDot(dotFd, left_dist, front_dist, right_dist);
	}

	public void setBuzzer(int front_dist) {
		test.outBuzzer(buzzerFd, front_dist);
	}

}
