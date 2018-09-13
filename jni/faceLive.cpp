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
 * 获取算法加密KEY
 *  @return 8字节字节数组;
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
 * 使用Dm2016加密后的秘钥进行算法鉴权
 * @sncode 解密后的字节数组
 * @return 成功返回1，失败返回0或负数
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
 * 初始化活体检测算法库
 * @ libDir 算法库包路径
 * @ tempDir 临时目录地址，当前应用必须拥有操作权限
 * @return 成功返回通道数 > 0, 失败返回 <=0 ;
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
 * 释放活体检测算法库
 */
JNIEXPORT void JNICALL Java_com_face_sv_FaceLiveNative_ReleaseFaceLive(JNIEnv* env, jobject obj) {
	LOGD("ReleaseFaceDetect()");
	THFL_Release();
}

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
 * JNI加载类
 */
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void *reserved) //这是JNI_OnLoad的声明，必须按照这样的方式声明
		{
	LOGD("JNI_OnLoad()");
	JNIEnv* env = NULL; //注册时在JNIEnv中实现的，所以必须首先获取它
	jint result = -1;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) //从JavaVM获取JNIEnv，一般使用1.4的版本
		return -1;

	jclass clazz;
	static const char* const kClassName = "com/face/sv/FaceLiveNative";

	clazz = env->FindClass(kClassName); //这里可以找到要注册的类，前提是这个类已经加载到java虚拟机中。 这里说明，动态库和有native方法的类之间，没有任何对应关系。

	if (clazz == NULL) {
		LOGD("cannot get class:%s\n", kClassName);
		return -1;
	}

	return JNI_VERSION_1_4; //这里很重要，必须返回版本，否则加载会失败。
}
#ifdef __cplusplus
}
#endif
