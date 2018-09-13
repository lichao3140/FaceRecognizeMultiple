package com.face.sv;

public class FaceDM2016Native {
    private static FaceDM2016Native mNative = null;
    static {
    	System.loadLibrary("FaceDM2016");
    }

    public static FaceDM2016Native getInstance() {
        if (mNative == null) {
            mNative = new FaceDM2016Native();
        }
        return mNative;
    }
    
    /**
     * 使用DM2016加密数据
     * @param keyCode 要加密的数据
     * @return  加密后的数据结果(长度4表示失败，返回整数失败状态， 长度8表示成功。)
     */
    public native byte[] encodeKeyCode(byte[] keyCode);
    
    /**
     * 读取DM2016上的设备序列号
     * @return  (长度4表示失败，返回整数失败状态， 长度13表示成功。)
     */
	public native byte[] readDeviceSerial();
	
	/**
	 * 写入设备序列号到DM2016
	 * @param devSerial 序列号
	 * @return  (0表示成功，其他表示失败。)
	 */
	public native int writeDeviceSerial(byte[] devSerial);
}
