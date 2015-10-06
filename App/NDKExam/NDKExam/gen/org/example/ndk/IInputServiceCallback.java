/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /root/Downloads/1506292234/NDKExam/src/org/example/ndk/IInputServiceCallback.aidl
 */
package org.example.ndk;
public interface IInputServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.example.ndk.IInputServiceCallback
{
private static final java.lang.String DESCRIPTOR = "org.example.ndk.IInputServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.example.ndk.IInputServiceCallback interface,
 * generating a proxy if needed.
 */
public static org.example.ndk.IInputServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.example.ndk.IInputServiceCallback))) {
return ((org.example.ndk.IInputServiceCallback)iin);
}
return new org.example.ndk.IInputServiceCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onFpgaChanged:
{
data.enforceInterface(DESCRIPTOR);
char[] _arg0;
_arg0 = data.createCharArray();
this.onFpgaChanged(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onGpioChanged:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onGpioChanged(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.example.ndk.IInputServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void onFpgaChanged(char[] changedFpgaBtns) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeCharArray(changedFpgaBtns);
mRemote.transact(Stub.TRANSACTION_onFpgaChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onGpioChanged(int changedGpioBtns) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(changedGpioBtns);
mRemote.transact(Stub.TRANSACTION_onGpioChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onFpgaChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onGpioChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void onFpgaChanged(char[] changedFpgaBtns) throws android.os.RemoteException;
public void onGpioChanged(int changedGpioBtns) throws android.os.RemoteException;
}
