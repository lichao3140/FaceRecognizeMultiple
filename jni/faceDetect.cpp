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
 * ��ȡ�㷨����KEY
 *  @return 8�ֽ��ֽ�����;
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
 * ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
 * @sncode ���ܺ���ֽ�����
 * @return �ɹ�����1��ʧ�ܷ���0����
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
 * ��ʼ����������㷨��
 * @ libDir �㷨���·��
 * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
 * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
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
 * �ͷ���������㷨��
 */
JNIEXPORT void JNICALL Java_com_face_sv_FaceDetectNative_ReleaseFaceDetect( JNIEnv* env,jobject obj) {
	LOGD("ReleaseFaceDetect()");
	THFI_Release();
}

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
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getFacePosition( JNIEnv* env,jobject obj, jbyteArray rgb24, jint width, jint height, jint padding) {
	int ret = -1;
	jbyteArray arr = NULL;
	unsigned char* img = (unsigned char*)env->GetByteArrayElements(rgb24, 0);
	long sz = env->GetArrayLength(rgb24);
	LOGD("DetectFace() width:%d height:%d sz:%ld", width, height, sz);
	if (img != NULL) {
        THFI_FacePos fps;
        // ������������㷨�ӿ�
	    ret = THFI_DetectFace(DEFAULT_CHANNEL_INDEX, img, 24, width, height, (THFI_FacePos*)&fps, 1, 0);
	    if (ret > 0) {
	    	LOGD("ret:%d face[1] left:%d top:%d right:%d bottom:%d", ret, fps.rcFace.left, fps.rcFace.top, fps.rcFace.right, fps.rcFace.bottom);
	    	// ���������Ƿ���Ч�����˲��������� ��ƫת�Ƕȹ�������
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
 * ���������Ϣ(ѹ��ͼ��360��Ⱥ���)
 * @param rgb24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @param padding ���߽�
 * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
    ret[0] ~ ret[4]; init��������ɹ���������������ʧ�ܷ���0����, -1001��ʾgrayΪ�գ�
    errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDetectNative_getFacePositionScale( JNIEnv* env,jobject obj, jbyteArray rgb24, jint width, jint height, jint padding) {
	int ret = -1;
	jbyteArray arr = NULL;
	unsigned char* img = (unsigned char*)env->GetByteArrayElements(rgb24, 0);
	long sz = env->GetArrayLength(rgb24);
	LOGD("DetectFace() width:%d height:%d sz:%ld", width, height, sz);
	if (img != NULL) {
        THFI_FacePos fps;
        // ������������㷨�ӿ�
	    ret = THFI_DetectFace(DEFAULT_CHANNEL_INDEX, img, 24, width, height, (THFI_FacePos*)&fps, 1);
	    if (ret > 0) {
	    	LOGD("ret:%d face[1] left:%d top:%d right:%d bottom:%d", ret, fps.rcFace.left, fps.rcFace.top, fps.rcFace.right, fps.rcFace.bottom);
	    	// ���������Ƿ���Ч�����˲��������� ��ƫת�Ƕȹ�������
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
 * ���������Ϣ
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @param type   �������
 * @paran padding ���߾�
 * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
 * errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
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
        // ������������㷨�ӿ�
	    ret = THFI_DetectFace(DEFAULT_CHANNEL_INDEX, img, 24, width, height, (THFI_FacePos*)&fps, 1, 640);
	    //LOGD("faceDetectMaster() THFI_DetectFace(320) ret:%d", ret);
	    if (ret > 0) {
	    	// ���������Ƿ���Ч�����˲��������� ��ƫת�Ƕȹ�������
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
 * ���������Ϣ
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @param type   ������� (0��ʾʶ��1��ʾע��)
 * @paran padding ���߾�
 * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
 * errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
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
        // ������������㷨�ӿ�
	    ret = THFI_DetectFace(DEFAULT_CHANNEL_INDEX, img, 24, width, height, (THFI_FacePos*)&fps, 1, 320);
	    LOGD("faceDetectScale() THFI_DetectFace(320) ret:%d", ret);
	    if (ret > 0) {
	    	//LOGD("ret:%d face[1] left:%d top:%d right:%d bottom:%d", ret, fps[0].rcFace.left, fps[0].rcFace.top, fps[0].rcFace.right, fps[0].rcFace.bottom);
	    	// ���������Ƿ���Ч�����˲��������� ��ƫת�Ƕȹ�������
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
	static const char* const kClassName = "com/face/sv/FaceDetectNative";

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
