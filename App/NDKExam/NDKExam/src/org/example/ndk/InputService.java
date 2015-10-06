package org.example.ndk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class InputService extends Service {

	char[] fpgaBtn;
	int gpioBtn;
	private NDKExam jniObject;
	public Thread mFpgaThread;
	public Thread mGpioThread;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.jniObject = Constant.jniObject;
		Log.i("Final Project", "OnCreate()");
		/*
		 * Thread athread = new Thread(this); athread.start();
		 */
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);

		Log.i("Final Project", "onStartCommand()");

		if (mFpgaThread == null) {
			mFpgaThread = new Thread() {
				@Override
				public void run() {
					
					System.out.println("Outside");
					
					try {
						while (!Thread.currentThread().isInterrupted()
								) {
							System.out.println("In mFpgaThread");
							fpgaBtn = jniObject.getFpgaBtn(jniObject.fpgaFd);
							int count = mFpgaCallbackList.beginBroadcast();

							for (int i = 0; i < count; i++) {

								try {
									IInputServiceCallback callback = (mFpgaCallbackList
											.getBroadcastItem(i));

									if (callback != null) {
										callback.onFpgaChanged(fpgaBtn);
									}

								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}

							mFpgaCallbackList.finishBroadcast();
							Thread.sleep(150);

						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
			};
			mFpgaThread.start();
		}

		if (mGpioThread == null) {
			/*
			 * mGpioThread = new InputServiceGPIOThread(this);
			 * ((InputServiceGPIOThread)mGpioThread).isEnabled = false;
			 */
			mGpioThread = new Thread() {
				@Override
				public void run() {
					try {
						while (!Thread.currentThread().isInterrupted()) {

							gpioBtn = jniObject.getCmd();
							int count = mGpioCallbackList.beginBroadcast();

							for (int i = 0; i < count; i++) {

								try {
									IInputServiceCallback callback = (mGpioCallbackList
											.getBroadcastItem(i));

									if (callback != null) {
										callback.onGpioChanged(gpioBtn);
									}

								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}

							mGpioCallbackList.finishBroadcast();
							Thread.sleep(100);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
			};
			mGpioThread.start();
		}

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("Final Project", "onDestroy()");

		if (mFpgaThread != null) {
			mFpgaThread.interrupt();
			mFpgaThread = null;
		}

		if (mGpioThread != null) {
			mGpioThread.interrupt();
			mGpioThread = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("Final Project", "onBind()");
		return mBinder;
	}

	RemoteCallbackList<IInputServiceCallback> mFpgaCallbackList = new RemoteCallbackList<IInputServiceCallback>();
	RemoteCallbackList<IInputServiceCallback> mGpioCallbackList = new RemoteCallbackList<IInputServiceCallback>();
	public boolean callbackOnce = false; 
	
	IInputService.Stub mBinder = new IInputService.Stub() {

		@Override
		public char[] getFpgaButtons() throws RemoteException {
			// TODO Auto-generated method stub
			return fpgaBtn;
		}

		@Override
		public int getGpioButtons() throws RemoteException {
			// TODO Auto-generated method stub
			return gpioBtn;
		}

		@Override
		public boolean registerInputCallback(IInputServiceCallback callback)
				throws RemoteException {
			// TODO Auto-generated method stub

			if (callback != null) {
				Log.i("Final Project", "callbacktrue");
				//if(!callbackOnce){
				mGpioCallbackList.register(callback);
				
				mFpgaCallbackList.register(callback);
				//callbackOnce = true;
				//}
				return true;

			} else {

				// Log.i("Final Project", "callbackfalse");
				return false;

			}
		}

		@Override
		public boolean unregisterInputCallback(IInputServiceCallback callback)
				throws RemoteException {
			// TODO Auto-generated method stub

			if (callback != null) {
				mGpioCallbackList.unregister(callback);
				mFpgaCallbackList.unregister(callback);
				return true;

			} else {
				return false;
			}
		}

		@Override
		public void stopFpgaThread() throws RemoteException {
			// TODO Auto-generated method stub
			if (mFpgaThread != null) {
				mFpgaThread.interrupt();
				mFpgaThread = null;
			}
		}

		@Override
		public void stopGpioThread() throws RemoteException {
			// TODO Auto-generated method stub
			if (mGpioThread != null) {
				mGpioThread.interrupt();
				mGpioThread = null;
			}
		}


	};

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("Final Project", "onUnbind()");
		return true;
	};

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("Final Project", "onRebind()");
	}

}
