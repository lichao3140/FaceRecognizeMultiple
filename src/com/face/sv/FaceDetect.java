package com.face.sv;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * 人脸识别算法库的人脸检测。
 * @author 邹丰
 * @datetime 2016-05-03
 */
public class FaceDetect {
    private final static String TAG = "FaceDetect";
    private FaceDetectNative mDetectNative;
    private byte[]  mutexFace = new byte[0];
    
    public FaceDetect() {
        mDetectNative = FaceDetectNative.getInstance();
        //mDetectNative = new FaceDetectNative();
    }
    
    /**
     * 获取算法加密KEY
     *  @return 8字节字节数组;
     */
    public byte[] getDetectSN() {
    	return mDetectNative.getDetectSN();
    }

    /**
     *  使用Dm2016加密后的秘钥进行算法鉴权
     * @sncode 解密后的字节数组
     * @return 成功返回1，失败返回0或负数
     */
    public int checkDetectSN(byte[] sncode) {
    	return mDetectNative.checkDetectSN(sncode);
    }

    /**
     * 初始化人脸检测算法库
     * @ libDir 算法库包路径
     * @ tempDir 临时目录地址，当前应用必须拥有操作权限
     * @return 成功返回通道数 > 0, 失败返回 <=0 ;
     */
    public int initFaceDetectLib(String libDir, String tempDir) {
        int ret = mDetectNative.InitFaceDetect(libDir, tempDir);
        return ret;
    }

    /**
     * 释放人脸检测算法库
     */
    public void releaseFaceDetectLib() {
        mDetectNative.ReleaseFaceDetect();
    }

    /**
     * 获取RGB24图片数据中人脸信息
     * 检测人脸信息
     * @param rgb24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param padding 检测边界
     * @return 返回人脸信息，FaceInfo，失败FacePos=null ret= 0，
     */
    public FaceInfo getFacePositionFromGray(byte[] rgb24, int width, int height, int padding) {
        log("getFacePositionFromGray(byte[] gray, int width, int height)");
        FaceInfo faceInfo = new FaceInfo();
        if (rgb24 == null || rgb24.length == 0) {
        	return faceInfo;
        }
        byte[] value = null;
        synchronized (mutexFace) {
            value = mDetectNative.getFacePosition(rgb24, width, height, padding);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }

    /**
     * 获取图片中人脸信息
     * @param bmp 需要检测的人脸照片
     * @param padding 检测边界
     * @return 返回人脸信息，FaceInfo ret为人脸数 FacePos为人脸信息，失败FacePos=null ret= 0，
     */
    public FaceInfo getFacePositionFromBitmap(Bitmap bmp, int padding) {
        log("getFacePositionFromBitmap(Bitmap bmp)");
        FaceInfo faceInfo = new FaceInfo();
        if (bmp == null) {
        	return faceInfo;
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte[] value = null;
        if (mDetectNative != null) {
            int[] pixels = new int[width * height];
            // 获取RGB32数据
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            byte[] BGR24 = new byte[width * height * 3];
            //byte[] gray = new byte[width * height];
            // 获取图片的RGB24数据和灰度图数据
            for (int i = 0; i < width * height; i++) {
                int r = (pixels[i] >> 16) & 0x000000FF;
                int g = (pixels[i] >> 8) & 0x000000FF;
                int b = pixels[i] & 0x000000FF;
                BGR24[i * 3] = (byte)(b & 0xFF);
                BGR24[i * 3 + 1] = (byte)(g & 0xFF);
                BGR24[i * 3 + 2] = (byte)(r & 0xFF);
                //gray[i] = (byte) ((306 * r + 601 * g + 117 * b) >> 10);
            }
            synchronized (mutexFace) {
                value = mDetectNative.getFacePosition(BGR24, width, height, padding);
            }
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }
    
    /**
     * 获取RGB24图片数据中人脸信息(压缩图像到360宽度后检测)
     * 检测人脸信息
     * @param bgr24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param padding 检测边界
     * @return 返回人脸信息，FaceInfo，失败FacePos=null ret= 0，
     */
    public FaceInfo getFacePositionScaleFromGray(byte[] bgr24, int width, int height, int padding) {
        log("getFacePositionFromGray(byte[] gray, int width, int height)");
        FaceInfo faceInfo = new FaceInfo();
        if (bgr24 == null || bgr24.length == 0) {
        	return faceInfo;
        }
        byte[] value = null;
        synchronized (mutexFace) {
            value = mDetectNative.getFacePositionScale(bgr24, width, height, padding);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }

    /**
     * 获取图片中人脸信息(压缩图像到360宽度后检测)
     * @param bmp 需要检测的人脸照片
     * @param padding 检测边界
     * @return 返回人脸信息，FaceInfo ret为人脸数 FacePos为人脸信息，失败FacePos=null ret= 0，
     */
    public FaceInfo getFacePositionScaleFromBitmap(Bitmap bmp, int padding) {
        log("getFacePositionFromBitmap(Bitmap bmp)");
        FaceInfo faceInfo = new FaceInfo();
        if (bmp == null) {
        	return faceInfo;
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte[] value = null;
        if (mDetectNative != null) {
            int[] pixels = new int[width * height];
            // 获取RGB32数据
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            byte[] BGR24 = new byte[width * height * 3];
            //byte[] gray = new byte[width * height];
            // 获取图片的RGB24数据和灰度图数据
            for (int i = 0; i < width * height; i++) {
                int r = (pixels[i] >> 16) & 0x000000FF;
                int g = (pixels[i] >> 8) & 0x000000FF;
                int b = pixels[i] & 0x000000FF;
                BGR24[i * 3] = (byte)(b & 0xFF);
                BGR24[i * 3 + 1] = (byte)(g & 0xFF);
                BGR24[i * 3 + 2] = (byte)(r & 0xFF);
                //gray[i] = (byte) ((306 * r + 601 * g + 117 * b) >> 10);
            }
            synchronized (mutexFace) {
                value = mDetectNative.getFacePositionScale(BGR24, width, height, padding);
            }
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }
    

    /**
     * 检测人脸信息
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param type   检测类型(0表示识别，1表示注册)
     * @paran padding 检测边距(人脸识别人脸坐标距离图像边线大于等于padding距离才是有效人脸坐标)
     * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
     * errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
     */
    public FaceInfo faceDetectMaster(byte[] BGR24, int width, int height, int type, int padding) {
        //log("faceDetect(byte[] gray, int width, int height)");
        byte[] value = null;
        FaceInfo faceInfo = new FaceInfo();
        if (BGR24 == null) {
        	return faceInfo;
        }
        synchronized (mutexFace) {
            value = mDetectNative.faceDetectMaster(BGR24, width, height, type, padding);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }
    
    /**
     * 检测人脸信息（检测时算法自动缩放到宽度320来进行检测）
     * @param BGR24 需要检测的人脸照片的灰度图数据
     * @param width 图片宽度
     * @param height 图片高度
     * @param type   检测类型(0表示识别，1表示注册)
     * @paran padding 检测边距(人脸识别人脸坐标距离图像边线大于等于padding距离才是有效人脸坐标)
     * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
     * errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
     */
    public FaceInfo faceDetectScale(byte[] BGR24, int width, int height, int type, int padding) {
        //log("faceDetect(byte[] gray, int width, int height)");
        byte[] value = null;
        FaceInfo faceInfo = new FaceInfo();
        if (BGR24 == null) {
        	return faceInfo;
        }
        synchronized (mutexFace) {
            value = mDetectNative.faceDetectScale(BGR24, width, height, type, padding);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }
    

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
