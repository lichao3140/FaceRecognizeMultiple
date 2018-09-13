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
	unsigned long id;                         // 用户编号
	unsigned char* pFeature;                  // 用户模型加载内存起始地址
	int numMBLBP;                             // 用户模型数量
	bool status;                              // 用户模型状态 (0 为默认状态 , 1 加载模型状态)
} USERINFO, *LPUSERINFO, **LLPUSERINFO;

/**
 * 获取算法加密KEY
 *  @return 8字节字节数组;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceRecognizeNative_getFeatureSN( JNIEnv* env,jobject obj);

/**
 *  使用Dm2016加密后的秘钥进行算法鉴权
 * @sncode 解密后的字节数组
 * @return 成功返回1，失败返回0或负数
 */
JNIEXPORT  jint JNICALL Java_com_face_sv_FaceRecognizeNative_checkFeatureSN( JNIEnv* env,jobject obj, jbyteArray sncode);

/**
 * 初始化人脸检测算法库
 * @ libDir 算法库包路径
 * @ tempDir 临时目录地址，当前应用必须拥有操作权限
 * @ modelNum 每个用户的模型数量
 * @return 成功返回通道数 0, 失败返回 <0 ;
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_initFaceLibrary(JNIEnv* env, jobject obj, jstring libDir, jstring tempDir, jstring userDataPath, jint modelNum);

/**
 * 释放人脸检测算法库
 */
JNIEXPORT void Java_com_face_sv_FaceRecognizeNative_releaseFaceLibrary(JNIEnv* env,jobject obj);

/**
 * 获取已知人脸坐标的人脸图片中人脸模板
 * @param rgb24 人脸图片BGR24
 * @param faceData  图片人脸信息
 * @param width 图片宽度
 * @param height 图片高度
 * @return 人脸模板数据，成功数组长度2008字节, 失败数组长度为1，byte[0]返回0或负数。
 * (-101表示传入数据为空;-102表示malloc模型失败;103表示传入的faceInfo信息错误;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceFeatureNative_getFaceFeature( JNIEnv* env,jobject obj, jbyteArray rgb24, jbyteArray faceData, int width, int height);

/**
 * 比对两个人脸模板相似度
 * @param feature1 人脸模板
 * @param feature2 人脸模板
 * @return 相识度（分值范围0 ~ 100之间）
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceFeatureNative_compareFeature( JNIEnv* env,jobject obj, jbyteArray feature1, jbyteArray feature2);

/**
 * 注册人脸模型
 * @ faceId 注册用户模型编号
 * @param BGR24 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @facePos 人脸检测获得的人脸信息
 * @return 大于等于0注册成功，表示注册模型编号（0-N),小于0表示失败。
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_registerFaceFeature(JNIEnv* env,jobject obj,jint faceId, jbyteArray BGR24, jint width, jint height, jbyteArray facePos);

/**
 * 注册人脸模型
 * @ faceId 注册用户模型编号
 * @param BGR24 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @facePos 人脸检测获得的人脸信息
 * @return 大于等于0更新成功，表示更新模型编号（0-N),小于0表示失败。
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_updateFaceFeature(JNIEnv* env,jobject obj,jint faceId, jbyteArray BGR24, jint width, jint height, jbyteArray facePos);

/**
 * 删除人脸模型
 * @ faceId 注册用户模型编号
 * @return 0为成功，小于0 表示失败
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_deleteFaceFeature(JNIEnv* env,jobject obj,jint faceId);

/**
 * 清空所有人脸模型
 * @return 0为成功，小于0 表示失败
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_clearAllFaceFeature(JNIEnv* env,jobject obj);

/**
 * 重新加载人脸模型
 * @ faceId 注册用户模型编号
 * @return 0为成功，小于0 表示失败。
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_reloadFaceFeature(JNIEnv* env,jobject obj);

/**
 * 注册人脸模型
 * @ faceId 注册用户模型编号
 * @param BGR24 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @facePos 人脸检测获得的人脸信息
 * @return 返回识别相似度，大于等于0识别成功，返回识别相似度（0-100),小于0表示失败。
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_recognizeFaceOne(JNIEnv* env,jobject obj,jint faceId, jbyteArray BGR24, jint width, jint height, jbyteArray facePos);

/**
 * 注册人脸模型
 * @param BGR24 需要检测的人脸照片的灰度图数据
 * @param width 图片宽度
 * @param height 图片高度
 * @facePos 人脸检测获得的人脸信息
 * @return int[0]大于等于0注册成功,返回用户编号,小于0表示失败。int[1]成功时表示识别分数
 */
JNIEXPORT jintArray Java_com_face_sv_FaceRecognizeNative_recognizeFaceMore(JNIEnv* env,jobject obj,jbyteArray BGR24, jint width, jint height, jbyteArray facePos);

/**
 * 读取DM2016上的设备序列号
 * @return  (长度4表示失败，返回整数失败状态， 长度13表示成功。)
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceRecognizeNative_readDeviceSerial(JNIEnv* env, jobject obj);

/**
 * 写入设备序列号到DM2016
 * @param devSerial 序列号
 * @return  (0表示成功，其他表示失败。)
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceRecognizeNative_writeDeviceSerial(JNIEnv* env, jobject obj, jbyteArray devSerial);
#ifdef __cplusplus
}
#endif

#endif /* LIBRECOGNIZEFACE_RECOGNIZEFACE_H_ */
