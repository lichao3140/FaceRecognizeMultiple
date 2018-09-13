package com.face.sv;

public class FaceLiveNative {
    private static FaceLiveNative mNative = null;
    static {
    	System.loadLibrary("THFacialPos");
        System.loadLibrary("THFaceLive");
        System.loadLibrary("FaceLive");
    }

    public static FaceLiveNative getInstance() {
        if (mNative == null) {
            mNative = new FaceLiveNative();
        }
        return mNative;
    }
    
    /**
     * ��ȡ�㷨����KEY
     *  @return 8�ֽ��ֽ�����;
     */
    public native byte[] getLiveSN();

    /**
     * ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
     * @sncode ���ܺ���ֽ�����
     * @return �ɹ�����1��ʧ�ܷ���0����
     */
    public native int checkLiveSN(byte[] sncode);

    /**
     * ��ʼ���������㷨��
     * @ libDir �㷨���·��
     * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
     * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
     */
    public native int InitFaceLive(String libDir, String tempDir);

    /**
     * �ͷŻ������㷨��
     */
    public native void ReleaseFaceLive();

    /**
     * ����Ƿ����
     * @param BGR24KJ �ɼ�������ͼƬBGR24���ݡ�
     * @param BGR24HW ��������ͼƬBGR24���ݡ�
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param posKJ �ɼ�������������Ϣ
     * @param posHW ��������������Ϣ
     * @param nThreshold ��������(sugguest value is 30)
     * @return �ɹ�����0���ǻ��壩 ��  1�����壩�� ʧ�ܷ���������
     */
    public synchronized native int getFaceLive(byte[] BGR24KJ, byte[] BGR24HW, int width, int height, byte[] posKJ, byte[] posHW, int nThreshold);
}