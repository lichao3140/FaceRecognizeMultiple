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
     * ʹ��DM2016��������
     * @param keyCode Ҫ���ܵ�����
     * @return  ���ܺ�����ݽ��(����4��ʾʧ�ܣ���������ʧ��״̬�� ����8��ʾ�ɹ���)
     */
    public native byte[] encodeKeyCode(byte[] keyCode);
    
    /**
     * ��ȡDM2016�ϵ��豸���к�
     * @return  (����4��ʾʧ�ܣ���������ʧ��״̬�� ����13��ʾ�ɹ���)
     */
	public native byte[] readDeviceSerial();
	
	/**
	 * д���豸���кŵ�DM2016
	 * @param devSerial ���к�
	 * @return  (0��ʾ�ɹ���������ʾʧ�ܡ�)
	 */
	public native int writeDeviceSerial(byte[] devSerial);
}
