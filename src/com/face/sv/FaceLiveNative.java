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
     * 获取算法加密KEY
     *  @return 8字节字节数组;
     */
    public native byte[] getLiveSN();

    /**
     * 使用Dm2016加密后的秘钥进行算法鉴权
     * @sncode 解密后的字节数组
     * @return 成功返回1，失败返回0或负数
     */
    public native int checkLiveSN(byte[] sncode);

    /**
     * 初始化活体检测算法库
     * @ libDir 算法库包路径
     * @ tempDir 临时目录地址，当前应用必须拥有操作权限
     * @return 成功返回通道数 > 0, 失败返回 <=0 ;
     */
    public native int InitFaceLive(String libDir, String tempDir);

    /**
     * 释放活体检测算法库
     */
    public native void ReleaseFaceLive();

    /**
     * 检测是否活体
     * @param BGR24KJ 可见光人脸图片BGR24数据。
     * @param BGR24HW 红外人脸图片BGR24数据。
     * @param width 图片宽度
     * @param height 图片高度
     * @param posKJ 可见光人脸坐标信息
     * @param posHW 红外人脸坐标信息
     * @param nThreshold 活体门限(sugguest value is 30)
     * @return 成功返回0（非活体） 或  1（活体）， 失败返回其他。
     */
    public synchronized native int getFaceLive(byte[] BGR24KJ, byte[] BGR24HW, int width, int height, byte[] posKJ, byte[] posHW, int nThreshold);
}