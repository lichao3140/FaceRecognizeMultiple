/*
 * faceLive.h
 *
 *  Created on: 2017-12-15
 *      Author: zoufeng
 */

#ifndef __FACE_LIVE_H__
#define __FACE_LIVE_H__
#include <jni.h>
#include <string.h>
#include <errno.h>
#include <android/log.h>

static const char *D_TAG="faceLive";
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, D_TAG, fmt, ##args)
#define LOGW(fmt, args...) __android_log_print(ANDROID_LOG_WARN, D_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, D_TAG, fmt, ##args)

#ifdef __cplusplus
extern "C" {
#endif

#define LIVE_SN_LENGTH  8           /* 人脸比对算法鉴权秘钥有效长度 */

/**
 * 获取算法加密KEY
 *  @return 8字节字节数组;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceLiveNative_getLiveSN( JNIEnv* env,jobject obj);

/**
 * 使用Dm2016加密后的秘钥进行算法鉴权
 * @sncode 解密后的字节数组
 * @return 成功返回1，失败返回0或负数
 */
JNIEXPORT  jint JNICALL Java_com_face_sv_FaceLiveNative_checkLiveSN( JNIEnv* env,jobject obj, jbyteArray sncode);

/**
 * 初始化活体检测算法库
 * @ libDir 算法库包路径
 * @ tempDir 临时目录地址，当前应用必须拥有操作权限
 * @return 成功返回通道数 > 0, 失败返回 <=0 ;
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceLiveNative_InitFaceLive(JNIEnv* env, jobject obj, jstring libDir, jstring tempDir);

/**
 * 释放活体检测算法库
 */
JNIEXPORT void JNICALL Java_com_face_sv_FaceLiveNative_ReleaseFaceLive(JNIEnv* env, jobject obj);

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
JNIEXPORT jint JNICALL Java_com_face_sv_FaceLiveNative_getFaceLive(JNIEnv* env, jobject obj, jbyteArray BGR24KJ, jbyteArray BGR24HW, jint width, jint height, jbyteArray posKJ, jbyteArray posHW, jint nThreshold);

#ifdef __cplusplus
}
#endif
#endif /* __FACE_LIVE_H__ */
