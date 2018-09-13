/*
*******************************************************************************
**  Copyright (c) 2010, 深圳市飞瑞斯科技有限公司
**  All rights reserved.
**	文件名: dm2016.h
**  文件说明: 封转dm2016接口
**  创建日期: 2010.12.03
**
**  当前版本：1.0
**  作者：oscar(oscar@firscom.cn)
*******************************************************************************
*/

#ifndef __DM2016_H__
#define __DM2016_H__

#include <jni.h>
#include <string.h>
#include <errno.h>
#include <android/log.h>

static const char *TAG="dm2016";
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGW(fmt, args...) __android_log_print(ANDROID_LOG_WARN, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#define DM2016_EPPROM_SN_OFFSET		0x00	//存放序列号
#define DM2016_EPPROM_SN_SIZE		13

/* DM2016芯片接口 */

/**************************************************************\
** 函数名称： DM2016_Authentication
** 功能： DM2016鉴权函数
** 参数： pEncryptedCode : 加密码内容
  		  pPlanCode		 : 解密后的结果
** 返回： 0-成功，其它-失败
** 创建作者： 颜廷军
** 创建日期： 2012-06-18
** 修改作者：
** 修改日期：
\**************************************************************/
int DM2016_Authentication(char* pEncryptedCode, char* pPlanCode);

/**************************************************************\
** 函数名称： DM2016_ReadEeprom
** 功能： DM2016读E2PROM
** 参数： offset   : 偏移地址
		  pInData  : 数据内容
		  nDataLen : 数据长度
** 返回： 0-成功，其它-失败
** 创建作者： 颜廷军
** 创建日期： 2012-06-18
** 修改作者：
** 修改日期：
\**************************************************************/
int DM2016_ReadEeprom(int offset, char* pOutBuf, int nRequestLen);

/**************************************************************\
** 函数名称： DM2016_WriteEeprom
** 功能： DM2016写E2PROM
** 参数： offset   : 偏移地址
		  pInData  : 数据内容
		  nDataLen : 数据长度
** 返回： 0-成功，其它-失败
** 创建作者： 颜廷军
** 创建日期： 2012-06-18
** 修改作者：
** 修改日期：
\**************************************************************/
int DM2016_WriteEeprom(int offset, char* pInData, int nDataLen);

int DM2016_ReadSN(char *buf);
int DM2016_WriteSN(char *buf);

#ifdef __cplusplus
extern "C" {
#endif

/**
 * 使用DM2016加密数据
 * @param keyCode 要加密的数据
 * @return  加密后的数据结果(长度4表示失败，返回整数失败状态， 长度8表示成功。)
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDM2016Native_encodeKeyCode( JNIEnv* env,jobject obj, jbyteArray keyCode);

/**
 * 读取DM2016上的设备序列号
 * @return  (长度4表示失败，返回整数失败状态， 长度13表示成功。)
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDM2016Native_readDeviceSerial(JNIEnv* env, jobject obj);

/**
 * 写入设备序列号到DM2016
 * @param devSerial 序列号
 * @return  (0表示成功，其他表示失败。)
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceDM2016Native_writeDeviceSerial(JNIEnv* env, jobject obj, jbyteArray devSerial);


#ifdef __cplusplus
}
#endif

#endif //__DM2016_H__

