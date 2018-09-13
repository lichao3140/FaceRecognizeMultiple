package com.face.sv;

public class ImageUtil {
    //private final static String TAG = "ImageUtil";
    private ImageUtilNative mImageNative;
    private byte[]  mutexFace = new byte[0];
    
    public ImageUtil() {
    	mImageNative = ImageUtilNative.getInstance();
    }
    
    /**
     * Yuv420תBGR24
     * @param yuv420p ��Ҫ����������Ƭ�ĻҶ�ͼ����(nv12)
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @return ���ر�����bgr24����
     * errorCode:
     */
    public byte[] encodeYuv420pToBGR(byte[] yuv420, int width, int height) {
    	byte[] ret = null;
    	synchronized (mutexFace) {
    	    ret = mImageNative.encodeYuv420pToBGR(yuv420, width, height);
    	}
    	return ret;
    }

    /**
     * BGR24תJPG
     * @param bgr24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param imgPath ͼƬ�洢��ַ
     * @return �ɹ�����0�� ʧ�ܷ��ظ�����
     * errorCode: -1 malloc fail; -2 jpg compress fail; -3 jpgBufLen is error; -4 open file fail; -5 file write fail.
     */
    public int encodeBGR24toJpg(byte[] bgr24, int width, int height, String imgPath) {
    	int ret = 0;
    	synchronized (mutexFace) {
    	    ret = mImageNative.encodeBGR24toJpg(bgr24, width, height, imgPath);
    	}
    	return ret;
    }
}
