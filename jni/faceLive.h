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

#define LIVE_SN_LENGTH  8           /* �����ȶ��㷨��Ȩ��Կ��Ч���� */

/**
 * ��ȡ�㷨����KEY
 *  @return 8�ֽ��ֽ�����;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceLiveNative_getLiveSN( JNIEnv* env,jobject obj);

/**
 * ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
 * @sncode ���ܺ���ֽ�����
 * @return �ɹ�����1��ʧ�ܷ���0����
 */
JNIEXPORT  jint JNICALL Java_com_face_sv_FaceLiveNative_checkLiveSN( JNIEnv* env,jobject obj, jbyteArray sncode);

/**
 * ��ʼ���������㷨��
 * @ libDir �㷨���·��
 * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
 * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceLiveNative_InitFaceLive(JNIEnv* env, jobject obj, jstring libDir, jstring tempDir);

/**
 * �ͷŻ������㷨��
 */
JNIEXPORT void JNICALL Java_com_face_sv_FaceLiveNative_ReleaseFaceLive(JNIEnv* env, jobject obj);

/**
 * ����Ƿ����
 * @param BGR24KJ �ɼ�������ͼƬBGR24���ݡ�
 * @param BGR24HW ��������ͼƬBGR24���ݡ�
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @param posKJ �ɼ�������������Ϣ
 * @param posHW ��������������Ϣ
 * @param nThreshold ��������(sugguest value is 30)
 * @return �ɹ�����0���ǻ��壩 ��  1�����壩�� ʧ�ܷ���������
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceLiveNative_getFaceLive(JNIEnv* env, jobject obj, jbyteArray BGR24KJ, jbyteArray BGR24HW, jint width, jint height, jbyteArray posKJ, jbyteArray posHW, jint nThreshold);

#ifdef __cplusplus
}
#endif
#endif /* __FACE_LIVE_H__ */
