package com.face.sv;

/**
 * ����ʶ���㷨����������JNI�ӿڡ�
 * @author zoufeng
 * @datetime 2018-01-30
 */
public class FaceRecognizeNative {
    private static FaceRecognizeNative mNative = null;
    static {
    	System.loadLibrary("JPG");
        System.loadLibrary("THFacialPos");
        System.loadLibrary("THFeature");
        System.loadLibrary("FaceRecognize");
    }

    public static FaceRecognizeNative getInstance() {
        if (mNative == null) {
            mNative = new FaceRecognizeNative();
        }
        return mNative;
    }
    
    /**
     * ��ȡ�㷨����KEY
     *  @return 8�ֽ��ֽ�����;
     */
    public native byte[] getFeatureSN();

    /**
     *  ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
     * @sncode ���ܺ���ֽ�����
     * @return �ɹ�����1��ʧ�ܷ���0����
     */
    public native int checkFeatureSN(byte[] sncode);

    
    /**
     * ��ʼ����������㷨��
     * @ libDir �㷨���·��
     * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
     * @ userDataPath �û�ģ�����ݴ洢·��
     * @ modelNum ÿ���û���ģ������
     * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
     */
    public native int initFaceLibrary(String libDir, String tempDir, String userDataPath, int modelNum);

    /**
     * �ͷ���������㷨��
     */
    public native void releaseFaceLibrary();
    
    /**
     * ��ȡ��֪�������������ͼƬ������ģ��
     * @param BGR24 ����ͼƬBGR24
     * @param faceInfo  ͼƬ������Ϣ, ���鳤��580(�ж�������� ��ȡ��Ҫ����������Ϣ)
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @return ����ģ�����ݣ��ɹ����鳤��2008�ֽ�, ʧ�����鳤��Ϊ1��byte[0]����0������
     * (-101��ʾ��������Ϊ��;-102��ʾmallocģ��ʧ��;103��ʾ�����faceInfo��Ϣ����;
     */
    public native byte[] getFaceFeature(byte[] BGR24, byte[] faceInfo, int width, int height);

    /**
     * �ȶ���������ģ�����ƶ�
     * @param feature1 ����ģ��
     * @param feature2 ����ģ��
     * @return ��ʶ�ȣ���ֵ��Χ0 ~ 100֮�䣩�� -1 ��ʾ����feature1��feature2Ϊnull.
     */
    public native int compareFeature(byte[] feature1, byte[] feature2);
    
    /**
     * ע������ģ��
     * @ userId ע���û�ģ�ͱ��
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @facePos ��������õ�������Ϣ
     * @return ���ڵ���0ע��ɹ�����ʾע��ģ�ͱ�ţ�0-N),С��0��ʾʧ�ܡ�
     */
    public native int registerFaceFeature(int userId, byte[] BGR24, int width, int height, byte[] facePos);

    /**
     * ɾ������ģ��
     * @ userId ע���û�ģ�ͱ��
     * @return 0Ϊ�ɹ���С��0 ��ʾʧ��
     */
    public native int deleteFaceFeature(int userId);
    
    /**
     * �����������ģ��
     * @return 0Ϊ�ɹ���С��0 ��ʾʧ��
     */
    public native int clearAllFaceFeature();

    /**
     * ע������ģ��
     * @ userId ע���û�ģ�ͱ��
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @facePos ��������õ�������Ϣ
     * @return ���ڵ���0���³ɹ�����ʾ����ģ�ͱ�ţ�0-N),С��0��ʾʧ�ܡ�
     */
    public native int updateFaceFeature(int userId, byte[] BGR24, int width, int height, byte[] facePos);

    /**
     * ���¼�������ģ��
     * @ userId ע���û�ģ�ͱ��
     * @return 0Ϊ�ɹ���С��0 ��ʾʧ�ܡ�
     */
    public native int reloadFaceFeature();

    /**
     * ע������ģ��
     * @ userId ע���û�ģ�ͱ��
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @facePos ��������õ�������Ϣ
     * @return ���ڵ���0ʶ��ɹ�������ʶ�����ƶȣ�0-100),С��0��ʾʧ�ܡ�
     */
    public native int recognizeFaceOne(int userId, byte[] BGR24, int width, int height, byte[] facePos);

    
    /**
     * ע������ģ��
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @facePos ��������õ�������Ϣ
     * @return int[0]���ڵ���0ע��ɹ�,�����û����,С��0��ʾʧ�ܡ�int[1]�ɹ�ʱ��ʾʶ�����
     */
    public native int[] recognizeFaceMore(byte[] BGR24, int width, int height, byte[] facePos);
}
