/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /root/Downloads/9week/NDKExam/src/org/example/ndk/IFpgaService.aidl
 */
package org.example.ndk;
public interface IFpgaService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.example.ndk.IFpgaService
{
private static final java.lang.String DESCRIPTOR = "org.example.ndk.IFpgaService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.example.ndk.IFpgaService interface,
 * generating a proxy if needed.
 */
public static org.example.ndk.IFpgaService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.example.ndk.IFpgaService))) {
return ((org.example.ndk.IFpgaService)iin);
}
return new org.example.ndk.IFpgaService.Stub.Proxy(obj);
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
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.example.ndk.IFpgaService
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
}
static final int TRANSACTION_getFpgaButtons = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public char[] getFpgaButtons() throws android.os.RemoteException;
}
