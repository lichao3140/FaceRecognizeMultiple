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
     * ʹ��DM2016��������
     * @param keyCode Ҫ���ܵ�����
     * @return  ���ܺ�����ݽ��(����4��ʾʧ�ܣ���������ʧ��״̬�� ����8��ʾ�ɹ���)
     */
    public byte[] encodeKeyCode(byte[] keyCode) {
    	byte[] ret = null;
    	synchronized (mutexFace) {
    		ret = mDM2016Native.encodeKeyCode(keyCode);
    	}
    	return ret;
    }
	
    /**
     * ��ȡDM2016�ϵ��豸���к�
     * @return  (����4��ʾʧ�ܣ���������ʧ��״̬�� ����13��ʾ�ɹ���)
     */
	public byte[] readDeviceSerial() {
		return mDM2016Native.readDeviceSerial();
	}
	
	/**
	 * д���豸���кŵ�DM2016
	 * @param devSerial ���к�
	 * @return  (0��ʾ�ɹ���������ʾʧ�ܡ�)
	 */
	public int writeDeviceSerial(byte[] devSerial) {
		if (devSerial == null || devSerial.length == 0) {
			return -1;
		}
		return mDM2016Native.writeDeviceSerial(devSerial);
	}
}
