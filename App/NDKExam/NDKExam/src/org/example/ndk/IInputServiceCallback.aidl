package org.example.ndk;

interface IInputServiceCallback{
	void onFpgaChanged(in char[] changedFpgaBtns);
	void onGpioChanged(in int changedGpioBtns);
}