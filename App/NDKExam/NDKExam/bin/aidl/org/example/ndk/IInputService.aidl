package org.example.ndk;

import org.example.ndk.IInputServiceCallback;


interface IInputService{
	char[] getFpgaButtons();
	int getGpioButtons();
	void stopFpgaThread();
	void stopGpioThread();
	//void changeEnabled(boolean changed);
	boolean registerInputCallback( IInputServiceCallback callback );
	boolean unregisterInputCallback ( IInputServiceCallback callback );
}