/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /root/Downloads/1506292234/NDKExam/src/org/example/ndk/IInputService.aidl
 */
package org.example.ndk;
public interface IInputService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.example.ndk.IInputService
{
private static final java.lang.String DESCRIPTOR = "org.example.ndk.IInputService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.example.ndk.IInputService interface,
 * generating a proxy if needed.
 */
public static org.example.ndk.IInputService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.example.ndk.IInputService))) {
return ((org.example.ndk.IInputService)iin);
}
return new org.example.ndk.IInputService.Stub.Proxy(obj);
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
case TRANSACTION_getFpgaButtons:
{
data.enforceInterface(DESCRIPTOR);
char[] _result = this.getFpgaButtons();
reply.writeNoException();
reply.writeCharArray(_result);
return true;
}
case TRANSACTION_getGpioButtons:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getGpioButtons();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stopFpgaThread:
{
data.enforceInterface(DESCRIPTOR);
this.stopFpgaThread();
reply.writeNoException();
return true;
}
case TRANSACTION_stopGpioThread:
{
data.enforceInterface(DESCRIPTOR);
this.stopGpioThread();
reply.writeNoException();
return true;
}
case TRANSACTION_registerInputCallback:
{
data.enforceInterface(DESCRIPTOR);
org.example.ndk.IInputServiceCallback _arg0;
_arg0 = org.example.ndk.IInputServiceCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerInputCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterInputCallback:
{
data.enforceInterface(DESCRIPTOR);
org.example.ndk.IInputServiceCallback _arg0;
_arg0 = org.example.ndk.IInputServiceCallback.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterInputCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.example.ndk.IInputService
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
@Override public char[] getFpgaButtons() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
char[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getFpgaButtons, _data, _reply, 0);
_reply.readException();
_result = _reply.createCharArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getGpioButtons() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getGpioButtons, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void stopFpgaThread() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopFpgaThread, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopGpioThread() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopGpioThread, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//void changeEnabled(boolean changed);

@Override public boolean registerInputCallback(org.example.ndk.IInputServiceCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerInputCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean unregisterInputCallback(org.example.ndk.IInputServiceCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterInputCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getFpgaButtons = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getGpioButtons = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopFpgaThread = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_stopGpioThread = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_registerInputCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_unregisterInputCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public char[] getFpgaButtons() throws android.os.RemoteException;
public int getGpioButtons() throws android.os.RemoteException;
public void stopFpgaThread() throws android.os.RemoteException;
public void stopGpioThread() throws android.os.RemoteException;
//void changeEnabled(boolean changed);

public boolean registerInputCallback(org.example.ndk.IInputServiceCallback callback) throws android.os.RemoteException;
public boolean unregisterInputCallback(org.example.ndk.IInputServiceCallback callback) throws android.os.RemoteException;
}
