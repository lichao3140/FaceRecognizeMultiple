#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <pthread.h>

#include <fcntl.h>
#include <errno.h>
#include <sys/mman.h>
#include <asm/types.h>
#include <linux/types.h>
#include <linux/videodev2.h>
#include "Mutex.h"
#include "i2c-dev.h"
#include "dm2016.h"

#ifdef LOG_TAG
#undef LOG_TAG
#endif
#define LOG_TAG "dm2016"

#define USE_LOGALL
#define USE_LOGV
#define USE_LOGD
#define USE_LOGI
#define USE_LOGW
#define USE_LOGE
#define USE_LOGF

#define DM2016_DEVICE "/dev/dm2016"

int gbG1J = 1;

#if 0
typedef struct
{
	char code[8];
} dm2016_auth;

typedef struct
{
	unsigned int offset;
	unsigned int len;
	char buf[128];
} dm2016_eeprom;

#define DM2016_AUTH 		_IOWR('M', 101, dm2016_auth)
#define DM2016_READ_EEPROM	_IOR('M', 102, dm2016_eeprom)
#define DM2016_WRITE_EEPROM	_IOWR('M', 103, dm2016_eeprom)
#endif

#define I2C_DEVICE	"/dev/i2c-1"//"/dev/i2c-0"

#define DM2016_ADDR 0x50

int s_nDirectOpOnI2C = 1;

CMutexLock s_lockI2C;

void LockI2C()
{
	s_lockI2C.Lock();
}

void UnlockI2C()
{
	s_lockI2C.Unlock();
}

int DM2016_Open()
{
	int hDev = open(DM2016_DEVICE, O_RDWR);
	if (hDev != -1)
	{
		LOGD(" open DM2016_DEVICE ok");
		s_nDirectOpOnI2C = 0;
	}
	else
	{
		LOGE("open DM2016_DEVICE fail, open I2C_DEVICE");
#if 0
		hDev = open(I2C_DEVICE, O_RDWR);
		if (hDev != -1)
		{
			int i = 3;
			int ret = -1;
			while (i--)
			{
				if (gbG1J)
				{
					ret = ioctl(hDev, I2C_SLAVE_FORCE, DM2016_ADDR);
				}
				else
				{
					ret = ioctl(hDev, I2C_SLAVE, DM2016_ADDR);
				}
				if (ret != -1)
				{
					break;
				}

				usleep(10 * 1000);
			}

			if (ret == -1)
			{
				close(hDev);
				hDev = -1;
			}
		}
#endif
	}

	if (hDev == -1)
	{
		LOGE("open DM2016 failed.");
	}
	return hDev;
}

void DM2016_Close(int hDev)
{
	if (hDev != -1)
		close(hDev);
}

void MSLEEP(int ms)
{
	usleep(ms * 1000);
}


int DM2016_Authentication(char* pEncryptedCode, char* pPlanCode)
{
	int ret = 0;
	//return ret;

	if (pEncryptedCode == NULL || pPlanCode == NULL)
	{
		ret = -1;
	}
	else
	{
		int hDev = DM2016_Open();
		if (hDev == -1)
		{
			ret = -2;
		}
		else
		{
			if (s_nDirectOpOnI2C)
			{
				char buf[256];
				buf[0] = 0x90;
				memcpy(buf + 1, pEncryptedCode, 8);
				LockI2C();
				ret = write(hDev, buf, 9);
				if (ret != 9)
					ret = -3;
				else
				{
					MSLEEP(100);
					ret = write(hDev, buf, 1);
					if (ret != 1)
						ret = -4;
					else
					{
						MSLEEP(100);
						ret = read(hDev, pPlanCode, 8);
						if (ret != 8)
							ret = -4;
					}
				}
				UnlockI2C();
			}
			else
			{
#if 0
				dm2016_auth auth;
				memcpy(auth.code, pEncryptedCode, 8);
				if (ioctl(hDev, DM2016_AUTH, &auth))
				{
					LOGE("CMD_AUTHENTICATION failed");
					ret = -3;
				}
				else
				{
					memcpy(pPlanCode, auth.code, 8);
					ret = 8;
				}
#endif
				unsigned char Buffer[9] = {0};
				Buffer[0] = 0x90;

				memcpy(&Buffer[1], pEncryptedCode, 8);

	            LOGE("writeBuf: %02x %02x-%02x-%02x-%02x %02x-%02x-%02x-%02x", Buffer[0],Buffer[1],Buffer[2],Buffer[3], Buffer[4],Buffer[5],Buffer[6],Buffer[7],Buffer[8]);

	            //第一个字节是地址，第二个字节开始是数据，写数据长度必须是8
				ret = write(hDev, (void *)Buffer , 8);
				if (ret == 8)
				{
					Buffer[0] = 0x90;
					//第一个字节是地址，第二个字节开始是数据，读数据长度必须是8
					ret = read(hDev, (void *)Buffer, 8);
					if (ret == 8)
					{
						LOGE("readBuf: %02x %02x-%02x-%02x-%02x %02x-%02x-%02x-%02x", Buffer[0],Buffer[1],Buffer[2],Buffer[3],Buffer[4],Buffer[5],Buffer[6],Buffer[7],Buffer[8]);
						memcpy(pPlanCode, &Buffer[1], 8);
					}
					else
					{
						LOGE("read ret = %d", ret);
						ret = -5;
					}
				}
				else
				{
				    LOGE("write ret: %d", ret);
				    ret = -6;
				}
			}

			DM2016_Close(hDev);
		}
	}
	return (ret > 0 ? 0 : ret);
}

int DM2016_ReadEeprom(int offset, char* pOutBuf, int nRequestLen)
{
	int ret = 0;

	if (pOutBuf == NULL || offset < 0 || nRequestLen < 1
			|| (offset + nRequestLen) > 128)
	{
		ret = -1;
	}
	else
	{
		int hDev = DM2016_Open();
		if (hDev == -1)
		{
			ret = -2;
		}
		else
		{
			LOGE("s_nDirectOpOnI2C %d", s_nDirectOpOnI2C);

			if (s_nDirectOpOnI2C)
			{
				pOutBuf[0] = (char) offset;
				LockI2C();

				ret = write(hDev, pOutBuf, 1);

				LOGE("ret %d ", ret);
				if (ret != 1)
				{
					ret = -4;
				}
				else
				{
					MSLEEP(100);

					ret = read(hDev, pOutBuf, nRequestLen);

					LOGE(" ret %d", ret);
					if (ret != nRequestLen)
					{
						ret = -4;
					}
				}

				UnlockI2C();
			}
			else
			{
#if 0
				dm2016_eeprom eeprom;
				eeprom.offset = offset;
				eeprom.len = nRequestLen;

				if (ioctl(hDev, DM2016_READ_EEPROM, &eeprom) == -1)
				{
					LOGD("\r\n");

					ret = -3;
				}
				else
				{
					LOGD("\r\n");

					memcpy(pOutBuf, eeprom.buf, eeprom.len);
					ret = eeprom.len;
				}
#endif
				int i = 0;
				unsigned char Buffer[2] = {0};
                                for (i = 0; i < nRequestLen; i++)
                                {
                                    Buffer[0] = (char) (offset + i);
                                    //第一个字节是地址，第二个字节开始是数据，一次最多读入8个
                                    ret = read(hDev, Buffer, 1);
                                    if (ret != 1)
                                    {
                                       break;
                                    }
                                    pOutBuf[i] = Buffer[1];

                                }
                                if (ret != 1)
                                {
                                    ret = -3;
                                }
                                LOGE("read len : %d", i);
			}

			DM2016_Close(hDev);
		}
	}

	return (ret > 0 ? 0 : ret);
}

int DM2016_WriteEeprom(int offset, char* pInData, int nDataLen)
{
	int ret = 0;
	if (pInData == NULL || offset < 0 || nDataLen < 1
			|| (offset + nDataLen) > 128)
		ret = -1;
	else
	{
		int hDev = DM2016_Open();
		if (hDev == -1)
			ret = -2;
		else
		{
			if (s_nDirectOpOnI2C)
			{
				int i;
				char buf[256];
				for (i = 0; i < nDataLen; i++)
				{
					buf[0] = (char) (offset + i);
					buf[1] = pInData[i];
					LockI2C();
					ret = write(hDev, buf, 2);
					UnlockI2C();
					MSLEEP(10);
					if (ret != 2)
						break;
				}
				if (ret != 2)
					ret = -3;
			}
			else
			{
#if 0
				dm2016_eeprom eeprom;
				eeprom.offset = offset;
				eeprom.len = nDataLen;
				memcpy(eeprom.buf, pInData, nDataLen);
				if (ioctl(hDev, DM2016_WRITE_EEPROM, &eeprom) == -1)
					ret = -4;
				else
					ret = eeprom.len;
#endif
				int i = 0;
				unsigned char Buffer[2] = {0};
                                for (i = 0; i < nDataLen; i++)
                                {
                                    Buffer[0] = (char) (offset + i);
                                    Buffer[1] = pInData[i];
                                    //第一个字节是地址，第二个字节开始是数据，一次最多写入8个
                                    ret = write(hDev, Buffer, 1);
                                    if (ret != 1)
                                    {
                                       break;
                                    }
                                }
                                if (ret != 1)
                                {
                                    ret = -4;
                                }
                                LOGE("write len : %d", i);
			}

			DM2016_Close(hDev);
		}
	}
	return (ret > 0 ? 0 : ret);
}

int DM2016_ReadSN(char *buf)
{
	int ret = -1;

	if (NULL != buf)
	{
		ret = DM2016_ReadEeprom(DM2016_EPPROM_SN_OFFSET, buf,
				DM2016_EPPROM_SN_SIZE);
	}

	return ret;
}

int DM2016_WriteSN(char *buf)
{
	int ret = -1;

	if (NULL != buf)
	{
		ret = DM2016_WriteEeprom(DM2016_EPPROM_SN_OFFSET, buf,
				DM2016_EPPROM_SN_SIZE);

	}

	return ret;
}


#ifdef __cplusplus
extern "C" {
#endif

/**
 * 使用DM2016加密数据
 * @param keyCode 要加密的数据
 * @return  加密后的数据结果(长度4表示失败，返回整数失败状态， 长度8表示成功。)
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDM2016Native_encodeKeyCode( JNIEnv* env,jobject obj, jbyteArray keyCode)
{
	int ret = -1;
	jbyteArray results;
	unsigned char* input = (unsigned char*)env->GetByteArrayElements(keyCode, 0);
	int size = env->GetArrayLength(keyCode);
	if (size < 8) {
        ret = -102;
	}
	unsigned char outSn[32] = {0};
	unsigned char inputSn[32] = {0};
	memcpy(&inputSn, input, 8);
	//LOGE("outSn: %02x-%02x-%02x-%02x %02x-%02x-%02x-%02x", outSn[0],outSn[1],outSn[2],outSn[3], outSn[4],outSn[5],outSn[6],outSn[7]);
	ret = DM2016_Authentication((char*)&inputSn, (char*)&outSn);
	LOGE("ret = %d", ret);
    if(ret == 0) {
    	results = env->NewByteArray(8);
        env->SetByteArrayRegion(results, 0, 8, (jbyte*)&outSn);

    } else {
    	size = sizeof(ret);
    	results = env->NewByteArray(size);
    	env->SetByteArrayRegion(results, 0, size, (jbyte*)&ret);
    }
    env->ReleaseByteArrayElements(keyCode, (jbyte*)input, 0);
    return results;
}

/**
 * 读取DM2016上的设备序列号
 * @return (长度4表示失败，返回整数失败状态， 长度13表示成功。)
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceDM2016Native_readDeviceSerial(JNIEnv* env, jobject obj) {
	int ret = 0;
	jbyteArray results;
	int len = DM2016_EPPROM_SN_SIZE;
    jbyte * array = new jbyte[len];
    ret = DM2016_ReadSN((char*)array);
    if(ret != 0) {
    	len = sizeof(ret);
    	results = env->NewByteArray(len);
    	env->SetByteArrayRegion(results, 0, len, (jbyte*)&ret);
    } else {
    	results = env->NewByteArray(len);
        env->SetByteArrayRegion(results, 0, len, array);
    }
    delete[] array;
    return results;
}

/**
 * 写入设备序列号到DM2016
 * @param devSerial 序列号
 * @return (0表示成功，其他表示失败。)
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceDM2016Native_writeDeviceSerial(JNIEnv* env, jobject obj, jbyteArray devSerial) {
	jint ret = 0;
	int len = env->GetArrayLength(devSerial);
    jbyte * array = env->GetByteArrayElements(devSerial, 0);
    ///for (int i = 0; i < len; i++) {
    ///	LOGD("array[%d]:%d", i, array[i]);
    ///}
    char* buf = new char[DM2016_EPPROM_SN_SIZE];
    memset(buf, 0, DM2016_EPPROM_SN_SIZE);
    if (len > DM2016_EPPROM_SN_SIZE) {
    	len = DM2016_EPPROM_SN_SIZE;
    }
    memcpy(buf, array, len);
    //for (int i = 0; i < DM2016_EPPROM_SN_SIZE; i++) {
    //	LOGD("devSerial[%d]:%0xd", i, buf[i]);
    //}
    ret = DM2016_WriteSN(buf);
    LOGD("DM2016_WriteSN(buf) ret:%d", ret);
    env->ReleaseByteArrayElements(devSerial, array, 0);
    //LOGD("writeDeviceSerial() end");
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
	static const char* const kClassName = "com/face/sv/FaceDM2016Native";

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
