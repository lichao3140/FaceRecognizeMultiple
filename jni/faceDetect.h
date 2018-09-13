/*
 * faceDetect.h
 *
 *  Created on: 2016-4-22
 *      Author: zoufeng
 */

#ifndef FACEDETECT_H_
#define FACEDETECT_H_
#include <jni.h>
#include <string.h>
#include <errno.h>
#include <android/log.h>
#include "params.h"

static const char *D_TAG="faceDetect";
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, D_TAG, fmt, ##args)
#define LOGW(fmt, args...) __android_log_print(ANDROID_LOG_WARN, D_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, D_TAG, fmt, ##args)

#ifdef __cplusplus
extern "C" {
#endif

/**
 * 获取算法加密KEY
 *  @return 8字节字节数组;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getDetectSN( JNIEnv* env,jobject obj);

/**
 *  使用Dm2016加密后的秘钥进行算法鉴权
 * @sncode 解密后的字节数组
 * @return 成功返回1，失败返回0或负数
 */
JNIEXPORT  jint JNICALL Java_com_face_sv_FaceDetectNative_checkDetectSN( JNIEnv* env,jobject obj, jbyteArray sncode);

/**
 * 初始化人脸检测算法库
 * @ libDir 算法库包路径
 * @ tempDir 临时目录地址，当前应用必须拥有操作权限
 * @return 成功返回通道数 > 0, 失败返回 <=0 ;
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceDetectNative_InitFaceDetect(JNIEnv* env, jobject obj, jstring libDir, jstring tempDir);

/**
 * 释放人脸检测算法库
 */
JNIEXPORT void JNICALL Java_com_face_sv_FaceDetectNative_ReleaseFaceDetect(JNIEnv* env, jobject obj);

/**
 * 检测人脸信息
 * @param rgb24 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @param padding 检测边界
 * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
    ret[0] ~ ret[4]; init检测结果，成功返回人脸数量，失败返回0或负数, -1001表示gray为空，
    errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getFacePosition(JNIEnv* env, jobject obj, jbyteArray rgb24, jint width, jint height, jint padding);

/**
 * 检测人脸信息(压缩图像到360宽度后检测)
 * @param rgb24 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @param padding 检测边界
 * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
    ret[0] ~ ret[4]; init检测结果，成功返回人脸数量，失败返回0或负数, -1001表示gray为空，
    errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getFacePositionScale(JNIEnv* env, jobject obj, jbyteArray rgb24, jint width, jint height, jint padding);

/**
 * 检测人脸信息
 * @param BGR24 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @param type   检测类型
 * @paran padding 检测边距
 * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
 * errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
 */
JNIEXPORT jbyteArray Java_com_face_sv_FaceDetectNative_faceDetectMaster(JNIEnv* env,jobject obj,jbyteArray BGR24, jint width, jint height, jint type, jint padding);

/**
 * 检测人脸信息
 * @param BGR24 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @param type   检测类型   (0表示识别，1表示注册)
 * @paran padding 检测边距
 * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
 * errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
 */
JNIEXPORT jbyteArray Java_com_face_sv_FaceDetectNative_faceDetectScale(JNIEnv* env,jobject obj,jbyteArray BGR24, jint width, jint height, jint type, jint padding);

#ifdef __cplusplus
}
#endif
#endif /* FACEDETECT_H_ */
