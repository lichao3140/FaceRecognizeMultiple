package com.face.sv;

/**
 * 人脸识别算法库的人脸检测JNI接口。
 * @author 邹丰
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
     * 获取算法加密KEY
     *  @return 8字节字节数组;
     */
    public native byte[] getDetectSN();

    /**
     *  使用Dm2016加密后的秘钥进行算法鉴权
     * @sncode 解密后的字节数组
     * @return 成功返回1，失败返回0或负数
     */
    public native int checkDetectSN(byte[] sncode);

    /**
     * 初始化人脸检测算法库
     * @ libDir 算法库包路径
     * @ tempDir 临时目录地址，当前应用必须拥有操作权限
     * @return 成功返回通道数 > 0, 失败返回 <=0 ;
     */
    public native int InitFaceDetect(String libDir, String tempDir);

    /**
     * 释放人脸检测算法库
     */
    public native void ReleaseFaceDetect();

    /**
     * 检测人脸信息
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param padding 检测边界
     * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
        ret[0] ~ ret[4]; init检测结果，成功返回人脸数量，失败返回0或负数, -1001表示gray为空，
        errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
     */
    public native byte[] getFacePosition(byte[] BGR24, int width, int height, int padding);
    
    /**
     * 检测人脸信息(压缩图像到360宽度后检测)
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param padding 检测边界
     * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
        ret[0] ~ ret[4]; init检测结果，成功返回人脸数量，失败返回0或负数, -1001表示gray为空，
        errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
     */
    public native byte[] getFacePositionScale(byte[] BGR24, int width, int height, int padding);

    /**
     * 检测人脸信息
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param type   检测类型(0表示识别，1表示注册)
     * @paran padding 检测边距 (人脸识别人脸坐标距离图像边线大于等于padding距离才是有效人脸坐标)
     * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
     * errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
     */
    public native byte[] faceDetectMaster(byte[] BGR24, int width, int height, int type, int padding);
    
    /**
     * 检测人脸信息（检测时算法自动缩放到宽度320来进行检测）
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param type   检测类型(0表示识别，1表示注册)
     * @paran padding 检测边距 (人脸识别人脸坐标距离图像边线大于等于padding距离才是有效人脸坐标)
     * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
     * errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
     */
    public native byte[] faceDetectScale(byte[] BGR24, int width, int height, int type, int padding);
}
