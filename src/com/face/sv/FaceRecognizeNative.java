package com.face.sv;

/**
 * 人脸识别算法库的人脸检测JNI接口。
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
     * 获取算法加密KEY
     *  @return 8字节字节数组;
     */
    public native byte[] getFeatureSN();

    /**
     *  使用Dm2016加密后的秘钥进行算法鉴权
     * @sncode 解密后的字节数组
     * @return 成功返回1，失败返回0或负数
     */
    public native int checkFeatureSN(byte[] sncode);

    
    /**
     * 初始化人脸检测算法库
     * @ libDir 算法库包路径
     * @ tempDir 临时目录地址，当前应用必须拥有操作权限
     * @ userDataPath 用户模型数据存储路径
     * @ modelNum 每个用户的模型数量
     * @return 成功返回通道数 > 0, 失败返回 <=0 ;
     */
    public native int initFaceLibrary(String libDir, String tempDir, String userDataPath, int modelNum);

    /**
     * 释放人脸检测算法库
     */
    public native void releaseFaceLibrary();
    
    /**
     * 获取已知人脸坐标的人脸图片中人脸模板
     * @param BGR24 人脸图片BGR24
     * @param faceInfo  图片人脸信息, 数组长度580(有多个人脸则 截取需要检测的人脸信息)
     * @param width 图片宽度
     * @param height 图片高度
     * @return 人脸模板数据，成功数组长度2008字节, 失败数组长度为1，byte[0]返回0或负数。
     * (-101表示传入数据为空;-102表示malloc模型失败;103表示传入的faceInfo信息错误;
     */
    public native byte[] getFaceFeature(byte[] BGR24, byte[] faceInfo, int width, int height);

    /**
     * 比对两个人脸模板相似度
     * @param feature1 人脸模板
     * @param feature2 人脸模板
     * @return 相识度（分值范围0 ~ 100之间）， -1 表示参数feature1或feature2为null.
     */
    public native int compareFeature(byte[] feature1, byte[] feature2);
    
    /**
     * 注册人脸模型
     * @ userId 注册用户模型编号
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return 大于等于0注册成功，表示注册模型编号（0-N),小于0表示失败。
     */
    public native int registerFaceFeature(int userId, byte[] BGR24, int width, int height, byte[] facePos);

    /**
     * 删除人脸模型
     * @ userId 注册用户模型编号
     * @return 0为成功，小于0 表示失败
     */
    public native int deleteFaceFeature(int userId);
    
    /**
     * 清空所有人脸模型
     * @return 0为成功，小于0 表示失败
     */
    public native int clearAllFaceFeature();

    /**
     * 注册人脸模型
     * @ userId 注册用户模型编号
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return 大于等于0更新成功，表示更新模型编号（0-N),小于0表示失败。
     */
    public native int updateFaceFeature(int userId, byte[] BGR24, int width, int height, byte[] facePos);

    /**
     * 重新加载人脸模型
     * @ userId 注册用户模型编号
     * @return 0为成功，小于0 表示失败。
     */
    public native int reloadFaceFeature();

    /**
     * 注册人脸模型
     * @ userId 注册用户模型编号
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return 大于等于0识别成功，返回识别相似度（0-100),小于0表示失败。
     */
    public native int recognizeFaceOne(int userId, byte[] BGR24, int width, int height, byte[] facePos);

    
    /**
     * 注册人脸模型
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @facePos 人脸检测获得的人脸信息
     * @return int[0]大于等于0注册成功,返回用户编号,小于0表示失败。int[1]成功时表示识别分数
     */
    public native int[] recognizeFaceMore(byte[] BGR24, int width, int height, byte[] facePos);
}
