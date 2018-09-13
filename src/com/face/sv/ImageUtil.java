package com.face.sv;

public class ImageUtil {
    //private final static String TAG = "ImageUtil";
    private ImageUtilNative mImageNative;
    private byte[]  mutexFace = new byte[0];
    
    public ImageUtil() {
    	mImageNative = ImageUtilNative.getInstance();
    }
    
    /**
     * Yuv420转BGR24
     * @param yuv420p 需要检测的人脸照片的灰度图数据(nv12)
     * @param width 图片宽度
     * @param height 图片高度
     * @return 返回编码后的bgr24数据
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
     * BGR24转JPG
     * @param bgr24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param imgPath 图片存储地址
     * @return 成功返回0， 失败返回负数。
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
