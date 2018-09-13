package com.face.sv;

/**
 * ����ʶ���㷨����������JNI�ӿڡ�
 * @author �޷�
 * @datetime 2016-05-03
 */
public class FaceDetectNative {
    private static FaceDetectNative mNative = null;
    static {
    	System.loadLibrary("THFacialPos");
        System.loadLibrary("THFaceImage");
        System.loadLibrary("FaceDetect");
    }

    public static FaceDetectNative getInstance() {
        if (mNative == null) {
            mNative = new FaceDetectNative();
        }
        return mNative;
    }
    
    /**
     * ��ȡ�㷨����KEY
     *  @return 8�ֽ��ֽ�����;
     */
    public native byte[] getDetectSN();

    /**
     *  ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
     * @sncode ���ܺ���ֽ�����
     * @return �ɹ�����1��ʧ�ܷ���0����
     */
    public native int checkDetectSN(byte[] sncode);

    /**
     * ��ʼ����������㷨��
     * @ libDir �㷨���·��
     * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
     * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
     */
    public native int InitFaceDetect(String libDir, String tempDir);

    /**
     * �ͷ���������㷨��
     */
    public native void ReleaseFaceDetect();

    /**
     * ���������Ϣ
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param padding ���߽�
     * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
        ret[0] ~ ret[4]; init��������ɹ���������������ʧ�ܷ���0����, -1001��ʾgrayΪ�գ�
        errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
     */
    public native byte[] getFacePosition(byte[] BGR24, int width, int height, int padding);
    
    /**
     * ���������Ϣ(ѹ��ͼ��360��Ⱥ���)
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param padding ���߽�
     * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
        ret[0] ~ ret[4]; init��������ɹ���������������ʧ�ܷ���0����, -1001��ʾgrayΪ�գ�
        errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
     */
    public native byte[] getFacePositionScale(byte[] BGR24, int width, int height, int padding);

    /**
     * ���������Ϣ
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param type   �������(0��ʾʶ��1��ʾע��)
     * @paran padding ���߾� (����ʶ�������������ͼ����ߴ��ڵ���padding���������Ч��������)
     * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
     * errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
     */
    public native byte[] faceDetectMaster(byte[] BGR24, int width, int height, int type, int padding);
    
    /**
     * ���������Ϣ�����ʱ�㷨�Զ����ŵ����320�����м�⣩
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param type   �������(0��ʾʶ��1��ʾע��)
     * @paran padding ���߾� (����ʶ�������������ͼ����ߴ��ڵ���padding���������Ч��������)
     * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
     * errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
     */
    public native byte[] faceDetectScale(byte[] BGR24, int width, int height, int type, int padding);
}
