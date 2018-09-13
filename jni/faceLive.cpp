/*
 * faceFeature.cpp
 *
 *  Created on: 2016-4-22
 *      Author: zoufeng
 */
#include <unistd.h>
#include <Math.h>
#include <stdio.h>
#include <sys/timeb.h>
#include "FiStdDefEx.h"
#include "THFaceLive_i.h"
#include "THFaceLiveEx_i.h"
#include "faceLive.h"

#ifdef __cplusplus
extern "C" {
#endif

/**
 * ��ȡ�㷨����KEY
 *  @return 8�ֽ��ֽ�����;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceLiveNative_getLiveSN( JNIEnv* env,jobject obj)
{
	unsigned char outSn[32] = {0};
	THFL_OutputSN((unsigned char*)&outSn);
	jbyteArray results = env->NewByteArray(LIVE_SN_LENGTH);
	env->SetByteArrayRegion(results, 0, LIVE_SN_LENGTH, (jbyte*)&outSn);
	return results;
}

/**
 * ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
 * @sncode ���ܺ���ֽ�����
 * @return �ɹ�����1��ʧ�ܷ���0����
 */
JNIEXPORT  jint JNICALL Java_com_face_sv_FaceLiveNative_checkLiveSN( JNIEnv* env,jobject obj, jbyteArray sncode)
{
	int ret = -1;
	unsigned char inputSn[32] = {0};
	unsigned char* input = (unsigned char*)env->GetByteArrayElements(sncode, 0);
	int size = env->GetArrayLength(sncode);
	if (size >= LIVE_SN_LENGTH) {
		memcpy(&inputSn, input, 8);
		ret = THFL_InputSN((unsigned char*)&inputSn);
	}
	return ret;
}

/**
 * ��ʼ���������㷨��
 * @ libDir �㷨���·��
 * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
 * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceLiveNative_InitFaceLive(JNIEnv* env, jobject obj, jstring libDir, jstring tempDir) {
	LOGD("InitFaceDetect()");
	int ret = -1;
    const char* libPath = env->GetStringUTFChars(libDir, 0);
    const char* tempPath = env->GetStringUTFChars(tempDir, 0);
    LOGD("libPath:%s tempPath:%s", libPath, tempPath);
    THFL_SDK_SetDir(libPath, tempPath);
    env->ReleaseStringUTFChars(libDir, libPath);
    env->ReleaseStringUTFChars(tempDir, tempPath);

	ret = THFL_Create();
	LOGD("InitFaceDetect() ret:%d", ret);
	if (ret > 0) {
		return ret;
	} else {
		LOGD("Open face detect channel failed!\r\n");
		return ret;
	}
}

/**
 * �ͷŻ������㷨��
 */
JNIEXPORT void JNICALL Java_com_face_sv_FaceLiveNative_ReleaseFaceLive(JNIEnv* env, jobject obj) {
	LOGD("ReleaseFaceDetect()");
	THFL_Release();
}

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
JNIEXPORT jint JNICALL Java_com_face_sv_FaceLiveNative_getFaceLive(JNIEnv* env, jobject obj, jbyteArray BGR24KJ, jbyteArray BGR24HW, jint width, jint height, jbyteArray posKJ, jbyteArray posHW, jint nThreshold) {
	LOGD("getFaceLive()");
	int ret = -1;
	unsigned char* imgKJ = (unsigned char*)env->GetByteArrayElements(BGR24KJ, 0);
	unsigned char* imgHW = (unsigned char*)env->GetByteArrayElements(BGR24HW, 0);
	unsigned char* psKJ = (unsigned char*)env->GetByteArrayElements(posKJ, 0);
	unsigned char* psHW = (unsigned char*)env->GetByteArrayElements(posHW, 0);
	int pSize = sizeof(THFI_FacePos);
    THFI_FacePos fpsKJ;
    memcpy(&fpsKJ, psKJ, pSize);
    THFI_FacePos fpsHW;
    memcpy(&fpsHW, psHW, pSize);
	ret = THFL_Detect(imgKJ, imgHW, width, height, (THFI_FacePos*)&fpsKJ, (THFI_FacePos*)&fpsHW, nThreshold);
	env->ReleaseByteArrayElements(BGR24KJ, (signed char*)imgKJ, 0);
	env->ReleaseByteArrayElements(BGR24HW, (signed char*)imgHW, 0);
	env->ReleaseByteArrayElements(posKJ, (signed char*)psKJ, 0);
	env->ReleaseByteArrayElements(posHW, (signed char*)psHW, 0);
	LOGD("getFaceLive() ret:%d", ret);
	return ret;
}





/**
 * JNI������
 */
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void *reserved) //����JNI_OnLoad�����������밴�������ķ�ʽ����
		{
	LOGD("JNI_OnLoad()");
	JNIEnv* env = NULL; //ע��ʱ��JNIEnv��ʵ�ֵģ����Ա������Ȼ�ȡ��
	jint result = -1;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) //��JavaVM��ȡJNIEnv��һ��ʹ��1.4�İ汾
		return -1;

	jclass clazz;
	static const char* const kClassName = "com/face/sv/FaceLiveNative";

	clazz = env->FindClass(kClassName); //��������ҵ�Ҫע����࣬ǰ����������Ѿ����ص�java������С� ����˵������̬�����native��������֮�䣬û���κζ�Ӧ��ϵ��

	if (clazz == NULL) {
		LOGD("cannot get class:%s\n", kClassName);
		return -1;
	}

	return JNI_VERSION_1_4; //�������Ҫ�����뷵�ذ汾��������ػ�ʧ�ܡ�
}
#ifdef __cplusplus
}
#endif
