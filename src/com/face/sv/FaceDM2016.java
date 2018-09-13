package com.face.sv;

public class FaceDM2016 {
    //private final static String TAG = "FaceDM2016";
    private FaceDM2016Native mDM2016Native;
    private byte[]  mutexFace = new byte[0];
    
    public FaceDM2016() {
    	mDM2016Native = FaceDM2016Native.getInstance();
        //mDetectNative = new FaceDetectNative();
    }
    
    /**
     * 使用DM2016加密数据
     * @param keyCode 要加密的数据
     * @return  加密后的数据结果(长度4表示失败，返回整数失败状态， 长度8表示成功。)
     */
    public byte[] encodeKeyCode(byte[] keyCode) {
    	byte[] ret = null;
    	synchronized (mutexFace) {
    		ret = mDM2016Native.encodeKeyCode(keyCode);
    	}
    	return ret;
    }
	
    /**
     * 读取DM2016上的设备序列号
     * @return  (长度4表示失败，返回整数失败状态， 长度13表示成功。)
     */
	public byte[] readDeviceSerial() {
		return mDM2016Native.readDeviceSerial();
	}
	
	/**
	 * 写入设备序列号到DM2016
	 * @param devSerial 序列号
	 * @return  (0表示成功，其他表示失败。)
	 */
	public int writeDeviceSerial(byte[] devSerial) {
		if (devSerial == null || devSerial.length == 0) {
			return -1;
		}
		return mDM2016Native.writeDeviceSerial(devSerial);
	}
}
