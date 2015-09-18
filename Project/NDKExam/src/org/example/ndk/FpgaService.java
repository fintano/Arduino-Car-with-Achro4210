package org.example.ndk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class FpgaService extends Service implements Runnable {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Thread athread = new Thread(this);
		athread.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("Final Project", "onBind()");
		return null;
	}

	IFpgaService.Stub mBinder = new IFpgaService.Stub() {

		@Override
		public char[] getFpgaButtons() throws RemoteException {
			// TODO Auto-generated method stub

			return null;
		}

	};

}
