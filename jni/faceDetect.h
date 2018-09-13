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
 * ��ȡ�㷨����KEY
 *  @return 8�ֽ��ֽ�����;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getDetectSN( JNIEnv* env,jobject obj);

/**
 *  ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
 * @sncode ���ܺ���ֽ�����
 * @return �ɹ�����1��ʧ�ܷ���0����
 */
JNIEXPORT  jint JNICALL Java_com_face_sv_FaceDetectNative_checkDetectSN( JNIEnv* env,jobject obj, jbyteArray sncode);

/**
 * ��ʼ����������㷨��
 * @ libDir �㷨���·��
 * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
 * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceDetectNative_InitFaceDetect(JNIEnv* env, jobject obj, jstring libDir, jstring tempDir);

/**
 * �ͷ���������㷨��
 */
JNIEXPORT void JNICALL Java_com_face_sv_FaceDetectNative_ReleaseFaceDetect(JNIEnv* env, jobject obj);

/**
 * ���������Ϣ
 * @param rgb24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @param padding ���߽�
 * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
    ret[0] ~ ret[4]; init��������ɹ���������������ʧ�ܷ���0����, -1001��ʾgrayΪ�գ�
    errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getFacePosition(JNIEnv* env, jobject obj, jbyteArray rgb24, jint width, jint height, jint padding);

/**
 * ���������Ϣ(ѹ��ͼ��360��Ⱥ���)
 * @param rgb24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @param padding ���߽�
 * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
    ret[0] ~ ret[4]; init��������ɹ���������������ʧ�ܷ���0����, -1001��ʾgrayΪ�գ�
    errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getFacePositionScale(JNIEnv* env, jobject obj, jbyteArray rgb24, jint width, jint height, jint padding);

/**
 * ���������Ϣ
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @param type   �������
 * @paran padding ���߾�
 * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
 * errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
 */
JNIEXPORT jbyteArray Java_com_face_sv_FaceDetectNative_faceDetectMaster(JNIEnv* env,jobject obj,jbyteArray BGR24, jint width, jint height, jint type, jint padding);

/**
 * ���������Ϣ
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @param type   �������   (0��ʾʶ��1��ʾע��)
 * @paran padding ���߾�
 * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
 * errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
 */
JNIEXPORT jbyteArray Java_com_face_sv_FaceDetectNative_faceDetectScale(JNIEnv* env,jobject obj,jbyteArray BGR24, jint width, jint height, jint type, jint padding);

#ifdef __cplusplus
}
#endif
#endif /* FACEDETECT_H_ */
