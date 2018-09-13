/*
 * RecognizeFaceNative.h
 *
 *  Created on: 2015��9��17��
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
 * Yuv420תBGR24
 * @param yuv420p ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @return ���ر�����bgr24����
 * errorCode:
 */
JNIEXPORT jbyteArray Java_com_face_sv_ImageUtilNative_encodeYuv420pToBGR(JNIEnv* env,jobject obj,jbyteArray yuv420p, jint width, jint height);

/**
 * BGR24תJPG
 * @param yuv420p ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @param imgPath ͼƬ�洢��ַ
 * @return �ɹ�����0�� ʧ�ܷ��ظ�����
 * errorCode: -1 malloc fail; -2 jpg compress fail; -3 jpgBufLen is error; -4 open file fail; -5 file write fail.
 */
JNIEXPORT jint Java_com_face_sv_ImageUtilNative_encodeBGR24toJpg(JNIEnv* env,jobject obj,jbyteArray bgr24, jint width, jint height, jstring imgPath);

#ifdef __cplusplus
}
#endif

#endif /* LIBRECOGNIZEFACE_RECOGNIZEFACE_H_ */
