/*
 * faceFeature.cpp
 *
 *  Created on: 2016-4-22
 *      Author: zoufeng
 */
#include <unistd.h>
#include <Math.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/timeb.h>
#include "faceDetect.h"
#include "FiStdDefEx.h"
#include "THFaceImage_i.h"
#include "THFaceImageEx_i.h"
#include "params.h"

#ifdef __cplusplus
extern "C" {
#endif

static int MAX_LEN = 32;
static int INVALID_LEN = 8;

bool isValidFacePosition(int width, int height, RECT face,FaceAngle angle, int padding, int type = TYPE_FACE_RECOGNIZE)
{
	//LOGD("isValidFacePosition() padding:%d face left:%d top:%d right:%d bottom:%d", padding, face.left, face.top, face.right, face.bottom);
    if (face.left < padding || face.top < padding || (width - face.right) < padding || (height - face.bottom) < padding)
    {
    	LOGD("isValidFacePosition() fail. padding:%d face left:%d top:%d right:%d bottom:%d", padding, face.left, face.top, face.right, face.bottom);
    	return false;
    }
    //LOGD("isValidFacePosition() type:%d angle.confidence:%d angle.pitch:%d angle.roll:%d angle.yaw:%d", type, angle.confidence, angle.pitch, angle.roll, angle.yaw);
    if (type == TYPE_FACE_RECOGNIZE)
    {
        if (angle.confidence >= RECOGNIZE_FACE_ANGLE && abs(angle.pitch) <= RECOGNIZE_ANGLE_THRESHOLD && abs(angle.roll)  <= RECOGNIZE_ANGLE_THRESHOLD && abs(angle.yaw) <= RECOGNIZE_ANGLE_THRESHOLD)
        {
    	    return true;
        }
    } else if(type == TYPE_FACE_REGISTER) {
        if (angle.confidence >= REGISTER_FACE_ANGLE && abs(angle.pitch) <= REGISTER_ANGLE_THRESHOLD && abs(angle.roll)  <= REGISTER_ANGLE_THRESHOLD && abs(angle.yaw)  <= REGISTER_ANGLE_THRESHOLD)
        {
    	    return true;
        }
    }
		LOGD("isValidFacePosition() fail. type:%d angle.confidence:%f angle.pitch:%d angle.roll:%d angle.yaw:%d", type, angle.confidence, angle.pitch, angle.roll, angle.yaw);
    return false;
}

/**
 * 获取算法加密KEY
 *  @return 8字节字节数组;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getDetectSN( JNIEnv* env,jobject obj)
{
	unsigned char outSn[32] = {0};
	THFI_OutputSN((unsigned char*)&outSn);
	jbyteArray results = env->NewByteArray(FACE_AUTH_SN_LENGTH);
	env->SetByteArrayRegion(results, 0, FACE_AUTH_SN_LENGTH, (jbyte*)&outSn);
	return results;
}

/**
 * 使用Dm2016加密后的秘钥进行算法鉴权
 * @sncode 解密后的字节数组
 * @return 成功返回1，失败返回0或负数
 */
JNIEXPORT  jint JNICALL Java_com_face_sv_FaceDetectNative_checkDetectSN( JNIEnv* env,jobject obj, jbyteArray sncode)
{
	int ret = -1;
	unsigned char inputSn[32] = {0};
	unsigned char* input = (unsigned char*)env->GetByteArrayElements(sncode, 0);
	int size = env->GetArrayLength(sncode);
	if (size >= FACE_AUTH_SN_LENGTH) {
		memcpy(&inputSn, input, 8);
		ret = THFI_InputSN((unsigned char*)&inputSn);
	}
	return ret;
}

/**
 * 初始化人脸检测算法库
 * @ libDir 算法库包路径
 * @ tempDir 临时目录地址，当前应用必须拥有操作权限
 * @return 成功返回通道数 > 0, 失败返回 <=0 ;
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceDetectNative_InitFaceDetect(JNIEnv* env,jobject obj, jstring libDir, jstring tempDir) {
	LOGD("InitFaceDetect()");
	int ret = -1;
    const char* libPath = env->GetStringUTFChars(libDir, 0);
    const char* tempPath = env->GetStringUTFChars(tempDir, 0);
    LOGD("libPath:%s tempPath:%s", libPath, tempPath);
    THFI_SetDir(libPath, tempPath);
    env->ReleaseStringUTFChars(libDir, libPath);
    env->ReleaseStringUTFChars(tempDir, tempPath);

	THFI_Param param;
	ret = THFI_Create(DEFAULT_CHANNEL_NUMBER, &param);
	LOGD("InitFaceDetect() ret:%d", ret);
	if (ret > 0) {
		return ret;
	} else {
		LOGD("Open face detect channel failed!\r\n");
		return ret;
	}
}

/**
 * 释放人脸检测算法库
 */
JNIEXPORT void JNICALL Java_com_face_sv_FaceDetectNative_ReleaseFaceDetect( JNIEnv* env,jobject obj) {
	LOGD("ReleaseFaceDetect()");
	THFI_Release();
}

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
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getFacePosition( JNIEnv* env,jobject obj, jbyteArray rgb24, jint width, jint height, jint padding) {
	int ret = -1;
	jbyteArray arr = NULL;
	unsigned char* img = (unsigned char*)env->GetByteArrayElements(rgb24, 0);
	long sz = env->GetArrayLength(rgb24);
	LOGD("DetectFace() width:%d height:%d sz:%ld", width, height, sz);
	if (img != NULL) {
        THFI_FacePos fps;
        // 调用人脸检测算法接口
	    ret = THFI_DetectFace(DEFAULT_CHANNEL_INDEX, img, 24, width, height, (THFI_FacePos*)&fps, 1, 0);
	    if (ret > 0) {
	    	LOGD("ret:%d face[1] left:%d top:%d right:%d bottom:%d", ret, fps.rcFace.left, fps.rcFace.top, fps.rcFace.right, fps.rcFace.bottom);
	    	// 人脸坐标是否有效，过滤不完整人脸 或偏转角度过大人脸
	    	if (isValidFacePosition(width, height,fps.rcFace, fps.fAngle, padding)) {
	    	    int size = sizeof(THFI_FacePos) + sizeof(ret);
                arr = env->NewByteArray(size);
                env->SetByteArrayRegion(arr, 0, 4, (const signed char*)&ret);
                env->SetByteArrayRegion(arr, 4, size - 4, (const signed char*)&fps);
	    	} else {
		    	LOGD("faceDetectMaster() uninvalid face. ret:%d", ret);
		    	ret = -102;
	    	}
	    } else {
	    	LOGD("getFaceFeature() none face. ret:%d", ret);
	    	ret = -103;
	    }
	} else {
		LOGD("getFaceFeature() rgb24 is null.");
		ret = -101;
	}
	if (arr == NULL) {
		arr = env->NewByteArray(4);
		env->SetByteArrayRegion(arr, 0, 4, (const signed char*)&ret);
	}
	env->ReleaseByteArrayElements(rgb24, (signed char*)img, 0);
	return arr;
}

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
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getFacePositionScale( JNIEnv* env,jobject obj, jbyteArray rgb24, jint width, jint height, jint padding) {
	int ret = -1;
	jbyteArray arr = NULL;
	unsigned char* img = (unsigned char*)env->GetByteArrayElements(rgb24, 0);
	long sz = env->GetArrayLength(rgb24);
	LOGD("DetectFace() width:%d height:%d sz:%ld", width, height, sz);
	if (img != NULL) {
        THFI_FacePos fps;
        // 调用人脸检测算法接口
	    ret = THFI_DetectFace(DEFAULT_CHANNEL_INDEX, img, 24, width, height, (THFI_FacePos*)&fps, 1);
	    if (ret > 0) {
	    	LOGD("ret:%d face[1] left:%d top:%d right:%d bottom:%d", ret, fps.rcFace.left, fps.rcFace.top, fps.rcFace.right, fps.rcFace.bottom);
	    	// 人脸坐标是否有效，过滤不完整人脸 或偏转角度过大人脸
	    	if (isValidFacePosition(width, height,fps.rcFace, fps.fAngle, padding)) {
	    	    int size = sizeof(THFI_FacePos) + sizeof(ret);
                arr = env->NewByteArray(size);
                env->SetByteArrayRegion(arr, 0, 4, (const signed char*)&ret);
                env->SetByteArrayRegion(arr, 4, size - 4, (const signed char*)&fps);
	    	} else {
		    	LOGD("faceDetectMaster() uninvalid face. ret:%d", ret);
		    	ret = -102;
	    	}
	    } else {
	    	LOGD("getFaceFeature() none face. ret:%d", ret);
	    	ret = -103;
	    }
	} else {
		LOGD("getFaceFeature() rgb24 is null.");
		ret = -101;
	}
	if (arr == NULL) {
		arr = env->NewByteArray(4);
		env->SetByteArrayRegion(arr, 0, 4, (const signed char*)&ret);
	}
	env->ReleaseByteArrayElements(rgb24, (signed char*)img, 0);
	return arr;
}

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
JNIEXPORT jbyteArray Java_com_face_sv_FaceDetectNative_faceDetectMaster(JNIEnv* env,jobject obj,jbyteArray BGR24, jint width, jint height, jint type, jint padding) {
	int ret = -1;
	jbyteArray arr = NULL;
	unsigned char* img = (unsigned char*)env->GetByteArrayElements(BGR24, 0);
	//long sz = env->GetArrayLength(BGR24);
	//LOGD("faceDetect() width:%d height:%d sz:%ld", width, height, sz);
	if (img != NULL) {
        THFI_FacePos fps;
        //LOGD("faceDetectMaster() start");
        // 调用人脸检测算法接口
	    ret = THFI_DetectFace(DEFAULT_CHANNEL_INDEX, img, 24, width, height, (THFI_FacePos*)&fps, 1, 640);
	    //LOGD("faceDetectMaster() THFI_DetectFace(320) ret:%d", ret);
	    if (ret > 0) {
	    	// 人脸坐标是否有效，过滤不完整人脸 或偏转角度过大人脸
	    	if (isValidFacePosition(width, height,fps.rcFace, fps.fAngle, padding, type)) {
	    	    int size = sizeof(THFI_FacePos) + sizeof(ret);
                arr = env->NewByteArray(size);
                env->SetByteArrayRegion(arr, 0, 4, (const signed char*)&ret);
                env->SetByteArrayRegion(arr, 4, size - 4, (const signed char*)&fps);
	    	} else {
		    	//LOGD("faceDetectMaster() uninvalid face. ret:%d", ret);
		    	ret = -102;
	    	}
	    } else {
	    	//LOGD("faceDetect() none face. ret:%d", ret);
	    	ret = -103;
	    }
	} else {
		//LOGE("faceDetect() isInitSuccess is false or rgb24 is null.");
		ret = -101;
	}
	//LOGE("faceDetectMaster() ret:%d" , ret);
	if (arr == NULL) {
		arr = env->NewByteArray(4);
		env->SetByteArrayRegion(arr, 0, 4, (const signed char*)&ret);
	}
	env->ReleaseByteArrayElements(BGR24, (signed char*)img, 0);
	return arr;
}

/**
 * 检测人脸信息
 * @param BGR24 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @param type   检测类型 (0表示识别，1表示注册)
 * @paran padding 检测边距
 * @return 返回人脸信息，数组长度4(检测结果(int)，失败或无人脸)或(4 + n*580)(检测结果 + n*所有人脸信息)
 * errorCode: -101 为算法初始化失败； -102 为检测到的人脸边界或角度判断为无效； -103 为没有检测到人脸；
 */
JNIEXPORT jbyteArray Java_com_face_sv_FaceDetectNative_faceDetectScale(JNIEnv* env,jobject obj,jbyteArray BGR24, jint width, jint height, jint type, jint padding) {
	int ret = -1;
	jbyteArray arr = NULL;
	unsigned char* img = (unsigned char*)env->GetByteArrayElements(BGR24, 0);
	long sz = env->GetArrayLength(BGR24);
	LOGD("faceDetectScale() width:%d height:%d sz:%ld", width, height, sz);
	if (img != NULL) {
        THFI_FacePos fps;
        //LOGD("faceDetectScale() start");
        // 调用人脸检测算法接口
	    ret = THFI_DetectFace(DEFAULT_CHANNEL_INDEX, img, 24, width, height, (THFI_FacePos*)&fps, 1, 320);
	    LOGD("faceDetectScale() THFI_DetectFace(320) ret:%d", ret);
	    if (ret > 0) {
	    	//LOGD("ret:%d face[1] left:%d top:%d right:%d bottom:%d", ret, fps[0].rcFace.left, fps[0].rcFace.top, fps[0].rcFace.right, fps[0].rcFace.bottom);
	    	// 人脸坐标是否有效，过滤不完整人脸 或偏转角度过大人脸
	    	if (isValidFacePosition(width, height,fps.rcFace, fps.fAngle, padding, type))
	    	{
	    	    int size = sizeof(THFI_FacePos) + sizeof(ret);
                arr = env->NewByteArray(size);
                env->SetByteArrayRegion(arr, 0, 4, (const signed char*)&ret);
                env->SetByteArrayRegion(arr, 4, size - 4, (const signed char*)&fps);
	    	} else {
		    	//LOGD("faceDetectScale() uninvalid face. ret:%d", ret);
		    	ret = -102;
	    	}
	    } else {
	    	//LOGD("faceDetectScale() none face. ret:%d", ret);
	    	ret = -103;
	    }
	} else {
		//LOGE("faceDetectScale() isInitSuccess is false or rgb24 is null.");
		ret = -101;
	}
	//LOGE("faceDetectScale() ret:%d" , ret);
	if (arr == NULL) {
		arr = env->NewByteArray(4);
		env->SetByteArrayRegion(arr, 0, 4, (const signed char*)&ret);
	}
	env->ReleaseByteArrayElements(BGR24, (signed char*)img, 0);
	return arr;
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
	static const char* const kClassName = "com/face/sv/FaceDetectNative";

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
