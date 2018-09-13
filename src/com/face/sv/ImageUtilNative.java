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
     * Yuv420转BGR24
     * @param yuv420p 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @return 返回编码后的bgr24数据
     * errorCode:
     */
    public native byte[] encodeYuv420pToBGR(byte[] yuv420p, int width, int height);

    /**
     * BGR24转JPG
     * @param yuv420p 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param imgPath 图片存储地址
     * @return 成功返回0， 失败返回负数。
     * errorCode: -1 malloc fail; -2 jpg compress fail; -3 jpgBufLen is error; -4 open file fail; -5 file write fail.
     */
    public native int encodeBGR24toJpg(byte[] bgr24, int width, int height, String imgPath);
}
