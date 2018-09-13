package com.face.sv;

public class ImageUtilNative {
    private static ImageUtilNative mNative = null;
    static {
    	System.loadLibrary("JPG");
    	System.loadLibrary("ImageUtil");
    }

    public static ImageUtilNative getInstance() {
        if (mNative == null) {
            mNative = new ImageUtilNative();
        }
        return mNative;
    }
    
    /**
     * Yuv420תBGR24
     * @param yuv420p ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @return ���ر�����bgr24����
     * errorCode:
     */
    public native byte[] encodeYuv420pToBGR(byte[] yuv420p, int width, int height);

    /**
     * BGR24תJPG
     * @param yuv420p ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param imgPath ͼƬ�洢��ַ
     * @return �ɹ�����0�� ʧ�ܷ��ظ�����
     * errorCode: -1 malloc fail; -2 jpg compress fail; -3 jpgBufLen is error; -4 open file fail; -5 file write fail.
     */
    public native int encodeBGR24toJpg(byte[] bgr24, int width, int height, String imgPath);
}
