package com.face.sv;

public class FaceLive {
    private final static String TAG = "FaceDetect";
    private FaceLiveNative mLiveNative;
    private byte[]  mutexFace = new byte[0];
    
    public FaceLive() {
        mLiveNative = FaceLiveNative.getInstance();
        //mLiveNative = new FaceLiveNative();
    }

    /**
     * ��ȡ�㷨����KEY
     *  @return 8�ֽ��ֽ�����;
     */
    public byte[] getLiveSN() {
        return mLiveNative.getLiveSN();
    }

    /**
     * ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
     * @sncode ���ܺ���ֽ�����
     * @return �ɹ�����1��ʧ�ܷ���0����
     */
    public int checkLiveSN(byte[] sncode) {
        return mLiveNative.checkLiveSN(sncode);
    }

    /**
     * ��ʼ���������㷨��
     * @ libDir �㷨���·��
     * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
     * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
     */
    public int InitFaceLive(String libDir, String tempDir) {
    	return mLiveNative.InitFaceLive(libDir, tempDir);
    }

    /**
     * �ͷŻ������㷨��
     */
    public void ReleaseFaceLive() {
    	mLiveNative.ReleaseFaceLive();
    }

    /**
     * ����Ƿ����
     * @param BGR24KJ �ɼ�������ͼƬBGR24���ݡ�
     * @param BGR24HW ��������ͼƬBGR24���ݡ�
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param posKJ �ɼ�������������Ϣ
     * @param posHW ��������������Ϣ
     * @param nThreshold ��������(sugguest value is 30, 0 ~ 50)
     * @return �ɹ�����0���ǻ��壩 ��  1�����壩�� ʧ�ܷ���������
     */
    public int getFaceLive(byte[] BGR24KJ, byte[] BGR24HW, int width, int height, byte[] posKJ, byte[] posHW, int nThreshold) {
    	int ret = -1;
    	synchronized (mutexFace) {
    	    ret = mLiveNative.getFaceLive(BGR24KJ, BGR24HW, width, height, posKJ, posHW, nThreshold);
    	}
    	return ret;
    }
}