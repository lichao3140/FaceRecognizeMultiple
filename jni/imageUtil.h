/*
 * RecognizeFaceNative.h
 *
 *  Created on: 2015年9月17日
 *      Author: binsys
 */

#ifndef LIBRECOGNIZEFACE_RECOGNIZEFACE_H_
#define LIBRECOGNIZEFACE_RECOGNIZEFACE_H_

#include <jni.h>
#include <string.h>
#include <errno.h>
#include <android/log.h>

static const char *D_TAG="ImageUtil";
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, D_TAG, fmt, ##args)
#define LOGW(fmt, args...) __android_log_print(ANDROID_LOG_WARN, D_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, D_TAG, fmt, ##args)

#ifdef __cplusplus
extern "C"
{
#endif

/**
 * Yuv420转BGR24
 * @param yuv420p 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @return 返回编码后的bgr24数据
 * errorCode:
 */
JNIEXPORT jbyteArray Java_com_face_sv_ImageUtilNative_encodeYuv420pToBGR(JNIEnv* env,jobject obj,jbyteArray yuv420p, jint width, jint height);

/**
 * BGR24转JPG
 * @param yuv420p 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @param imgPath 图片存储地址
 * @return 成功返回0， 失败返回负数。
 * errorCode: -1 malloc fail; -2 jpg compress fail; -3 jpgBufLen is error; -4 open file fail; -5 file write fail.
 */
JNIEXPORT jint Java_com_face_sv_ImageUtilNative_encodeBGR24toJpg(JNIEnv* env,jobject obj,jbyteArray bgr24, jint width, jint height, jstring imgPath);

#ifdef __cplusplus
}
#endif

#endif /* LIBRECOGNIZEFACE_RECOGNIZEFACE_H_ */
