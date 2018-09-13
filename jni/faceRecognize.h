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

static const char *D_TAG="FaceRecognize";
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, D_TAG, fmt, ##args)
#define LOGW(fmt, args...) __android_log_print(ANDROID_LOG_WARN, D_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, D_TAG, fmt, ##args)

#ifdef __cplusplus
extern "C"
{
#endif

typedef struct
{
	unsigned long id;                         // �û����
	unsigned char* pFeature;                  // �û�ģ�ͼ����ڴ���ʼ��ַ
	int numMBLBP;                             // �û�ģ������
	bool status;                              // �û�ģ��״̬ (0 ΪĬ��״̬ , 1 ����ģ��״̬)
} USERINFO, *LPUSERINFO, **LLPUSERINFO;

/**
 * ��ȡ�㷨����KEY
 *  @return 8�ֽ��ֽ�����;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceRecognizeNative_getFeatureSN( JNIEnv* env,jobject obj);

/**
 *  ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
 * @sncode ���ܺ���ֽ�����
 * @return �ɹ�����1��ʧ�ܷ���0����
 */
JNIEXPORT  jint JNICALL Java_com_face_sv_FaceRecognizeNative_checkFeatureSN( JNIEnv* env,jobject obj, jbyteArray sncode);

/**
 * ��ʼ����������㷨��
 * @ libDir �㷨���·��
 * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
 * @ modelNum ÿ���û���ģ������
 * @return �ɹ�����ͨ���� 0, ʧ�ܷ��� <0 ;
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_initFaceLibrary(JNIEnv* env, jobject obj, jstring libDir, jstring tempDir, jstring userDataPath, jint modelNum);

/**
 * �ͷ���������㷨��
 */
JNIEXPORT void Java_com_face_sv_FaceRecognizeNative_releaseFaceLibrary(JNIEnv* env,jobject obj);

/**
 * ��ȡ��֪�������������ͼƬ������ģ��
 * @param rgb24 ����ͼƬBGR24
 * @param faceData  ͼƬ������Ϣ
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @return ����ģ�����ݣ��ɹ����鳤��2008�ֽ�, ʧ�����鳤��Ϊ1��byte[0]����0������
 * (-101��ʾ��������Ϊ��;-102��ʾmallocģ��ʧ��;103��ʾ�����faceInfo��Ϣ����;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceFeatureNative_getFaceFeature( JNIEnv* env,jobject obj, jbyteArray rgb24, jbyteArray faceData, int width, int height);

/**
 * �ȶ���������ģ�����ƶ�
 * @param feature1 ����ģ��
 * @param feature2 ����ģ��
 * @return ��ʶ�ȣ���ֵ��Χ0 ~ 100֮�䣩
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceFeatureNative_compareFeature( JNIEnv* env,jobject obj, jbyteArray feature1, jbyteArray feature2);

/**
 * ע������ģ��
 * @ faceId ע���û�ģ�ͱ��
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @facePos ��������õ�������Ϣ
 * @return ���ڵ���0ע��ɹ�����ʾע��ģ�ͱ�ţ�0-N),С��0��ʾʧ�ܡ�
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_registerFaceFeature(JNIEnv* env,jobject obj,jint faceId, jbyteArray BGR24, jint width, jint height, jbyteArray facePos);

/**
 * ע������ģ��
 * @ faceId ע���û�ģ�ͱ��
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @facePos ��������õ�������Ϣ
 * @return ���ڵ���0���³ɹ�����ʾ����ģ�ͱ�ţ�0-N),С��0��ʾʧ�ܡ�
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_updateFaceFeature(JNIEnv* env,jobject obj,jint faceId, jbyteArray BGR24, jint width, jint height, jbyteArray facePos);

/**
 * ɾ������ģ��
 * @ faceId ע���û�ģ�ͱ��
 * @return 0Ϊ�ɹ���С��0 ��ʾʧ��
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_deleteFaceFeature(JNIEnv* env,jobject obj,jint faceId);

/**
 * �����������ģ��
 * @return 0Ϊ�ɹ���С��0 ��ʾʧ��
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_clearAllFaceFeature(JNIEnv* env,jobject obj);

/**
 * ���¼�������ģ��
 * @ faceId ע���û�ģ�ͱ��
 * @return 0Ϊ�ɹ���С��0 ��ʾʧ�ܡ�
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_reloadFaceFeature(JNIEnv* env,jobject obj);

/**
 * ע������ģ��
 * @ faceId ע���û�ģ�ͱ��
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @facePos ��������õ�������Ϣ
 * @return ����ʶ�����ƶȣ����ڵ���0ʶ��ɹ�������ʶ�����ƶȣ�0-100),С��0��ʾʧ�ܡ�
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_recognizeFaceOne(JNIEnv* env,jobject obj,jint faceId, jbyteArray BGR24, jint width, jint height, jbyteArray facePos);

/**
 * ע������ģ��
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @facePos ��������õ�������Ϣ
 * @return int[0]���ڵ���0ע��ɹ�,�����û����,С��0��ʾʧ�ܡ�int[1]�ɹ�ʱ��ʾʶ�����
 */
JNIEXPORT jintArray Java_com_face_sv_FaceRecognizeNative_recognizeFaceMore(JNIEnv* env,jobject obj,jbyteArray BGR24, jint width, jint height, jbyteArray facePos);

/**
 * ��ȡDM2016�ϵ��豸���к�
 * @return  (����4��ʾʧ�ܣ���������ʧ��״̬�� ����13��ʾ�ɹ���)
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceRecognizeNative_readDeviceSerial(JNIEnv* env, jobject obj);

/**
 * д���豸���кŵ�DM2016
 * @param devSerial ���к�
 * @return  (0��ʾ�ɹ���������ʾʧ�ܡ�)
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceRecognizeNative_writeDeviceSerial(JNIEnv* env, jobject obj, jbyteArray devSerial);
#ifdef __cplusplus
}
#endif

#endif /* LIBRECOGNIZEFACE_RECOGNIZEFACE_H_ */
