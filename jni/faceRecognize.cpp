/*
 * RecognizeFaceNative.cpp
 *
 *  Created on: 2015��9��17��
 *      Author: binsys
 */
#include <unistd.h>
#include <Math.h>
#include <sys/stat.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <sys/timeb.h>
#include <omp.h>
#include <dirent.h>
#include "FiStdDefEx.h"
#include "THFaceImage_i.h"
#include "THFaceImageEx_i.h"
#include "THFaceLive_i.h"
#include "THFaceLiveEx_i.h"
#include "THFeature_i.h"
#include "THFeatureEx_i.h"
#include "params.h"
#include "faceRecognize.h"



#ifdef __cplusplus
extern "C"
{
#endif

int nModelNum = MAX_FEATURE_NUM_PER_USER;

int nMaxUserNum = MAX_USER_NUM;

/* /data/data/com.face.cn/files/ */
char* sUserDataPath;

/* ��¼ȫ���û������Ա����Ϣ */
USERINFO gstrUserList[MAX_USER_NUM];

/* ȫ�ֵĴ����㷨������ָ�� */
unsigned char* pOneFeatureBuffer = NULL;

/* �û����� */
int nUserNum;

/* Ӧ�ó������㷨֮��Ĺ����ڴ棬20M�ֽ� */
unsigned char* pMaxUserFeatureBuffer;

timeval getSystemTime() {
	struct timeval begin;
	gettimeofday(&begin, NULL);
	return begin;
}


/**
 *
 * error code:
 * -100 faceFeature malloc fail.
   -99,invalid license.
   -1,pBuf,ptfp,pFeature is NULL
   -2,nChannelID is invalid or SDK is not initialized
 */
int getFaceFeature(BYTE* bgr24, int width, int height, BYTE* facePos, BYTE* pFeature)
{
	int ret = -255;
    if (pFeature != NULL) {
	    THFI_FacePos fps;
	    memcpy(&fps, facePos, sizeof(THFI_FacePos));
	    // ��ȡ����ģ������
	    ret = EF_Extract(DEFAULT_CHANNEL_INDEX, bgr24, width, height, 3, (THFI_FacePos*)&fps, pFeature);
	    //LOGD("EF_Extract() ret:%d", ret);
	    if (ret != 1){
		    LOGE("EF_Extract() fail, ret:%d .", ret);
		    free(pFeature);
	    }
    }
    else
    {
	    LOGE("malloc(size) fail, pFeature == NULL.");
	    ret = -100;
    }
    return ret;
}

/*********************************************************************
 * �����û�������ֵ
   userId: �û�userid
   pFeatureData ����������
   featureSize��ÿ���������ݴ�С
   pPath ���������ݴ��·��
 * ����ֵ: 0:�ɹ�; С��0:ʧ��
 *********************************************************************/
int saveUserFeatureToFile(unsigned int userId, unsigned char* pFeatureData, int featureSize, char* pPath)
{
	int n = 0;
	int fd = 0;
	int ret = 0;
	char filePath[GENERAL_PATH_LEN] = { 0 };

	if ((pPath == NULL) || (strlen(pPath) == 0))
	{
		sprintf(filePath, "%s/%08u", sUserDataPath, userId);
	}
	else
	{
		sprintf(filePath, "%s/%08u", pPath, userId);
	}
	mkdir(filePath, 0777);
	//LOGD("filePath %s", filePath);

	sprintf(filePath, "%s/feature.dat", filePath);

	//LOGD("filePath %s", filePath);
	remove(filePath);

	fd = open(filePath, O_CREAT | O_WRONLY, 0777);
	if (fd <= 0)
	{
		LOGE("can not open user feature file (%s)", filePath);
		ret = -1;
		goto error;
	}

	//LOGD("featureSize:%d", featureSize);
	n = write(fd, pFeatureData, featureSize);
	//LOGD("write(featureData) ret:%d featureSize:%d", n, featureSize);
	if (n != featureSize)
	{
	     ret = -2;
		 LOGE("write feature file fail. (%s)", filePath);
		 goto error;
	}

	error:
	if (fd > 0)
	{
		close(fd);
		fd = 0;
	}

	return ret;
}

/**
 * ����û�ģ��
 */
void initUserInfo(USERINFO* pUserInfo, int i) {
	pUserInfo->id = 0;
	pUserInfo->pFeature = pMaxUserFeatureBuffer + i * FEATURE_SIZE_GENERAL;
	///LOGD("addUserFeatureToMem() pUserInfo->pFeature:%x",pUserInfo->pFeature);
    memset(pUserInfo->pFeature, 0, FEATURE_SIZE_GENERAL);
    pUserInfo->numMBLBP = MAX_FEATURE_NUM_PER_USER;
    pUserInfo->status = false;
}

void initUserInfoList() {
	int size = 0;
	for (int i = 0; i < MAX_USER_NUM; i++) {
    	initUserInfo(&gstrUserList[i], i);
    	size++;
	}
	LOGD("initSDKBuffer size:%d", size);
}


/**
 * ����û�ģ����Ϣ
 * pUserInfo �û�ģ����Ϣָ��
 */
void clearUserInfo(USERINFO* pUserInfo) {
	pUserInfo->id = 0;
	memset(pUserInfo->pFeature, 0, FEATURE_SIZE_GENERAL);
    pUserInfo->numMBLBP = MAX_FEATURE_NUM_PER_USER;
    pUserInfo->status = false;
}


/******************************************************************************
 * �������ƣ� getUserInfoByUserId
 * ���ܣ� �����û�id�õ��û��±�
 * ������ iUserId �û�id
 * ���أ� �û������±�
 ******************************************************************************/
USERINFO* getUserInfoByUserId(int nUserId)
{
	USERINFO* userInfo;
	USERINFO* pUserInfo = NULL;
	for (int nIndex = 0; nIndex < MAX_USER_NUM; nIndex++)
	{
		userInfo = &gstrUserList[nIndex];
		if (userInfo->status && userInfo->id == nUserId)
		{
			pUserInfo = &gstrUserList[nIndex];
			break;
		}
	}
	return pUserInfo;
}

/******************************************************************************
 * �������ƣ� getFirstEmptyUserInfo
 * ���ܣ� �����û�id�õ��û��±�
 * ������ iUserId �û�id
 * ���أ� �û������±�
 ******************************************************************************/
USERINFO* getEmptyUserInfoToRegister()
{
	USERINFO* pUserInfo = NULL;
	int ret = 0;
	for (int nIndex = 0; nIndex < MAX_USER_NUM; nIndex++)
	{
		if (!gstrUserList[nIndex].status) {
			pUserInfo = &gstrUserList[nIndex];
			initUserInfo(pUserInfo, nIndex);
			ret = 1;
			break;
		}
	}
	return pUserInfo;
}

int loadFileList(char *basePath, USERINFO* pUserList)
{
    DIR *dir;
    struct dirent *ptr;

    if ((dir=opendir(basePath)) == NULL)
    {
        LOGE("Open dir error... basePath:%s", basePath);
        return -1;
    }
    LOGD("basePath:%s", basePath);
    int size = 0;
    while ((ptr=readdir(dir)) != NULL)
    {
        if(strcmp(ptr->d_name,".")==0 || strcmp(ptr->d_name,"..")==0)    ///current dir OR parrent dir
        {
        	LOGD("d_name:%s/%s\n",basePath,ptr->d_name);
            continue;
        }
        else if(ptr->d_type == 8)    ///file
        {
        	LOGD("d_name:%s/%s\n",basePath,ptr->d_name);
        }
        else if(ptr->d_type == 10)    ///link file
        {
        	LOGD("d_name:%s/%s\n",basePath,ptr->d_name);
        }
        else if(ptr->d_type == 4)    ///dir
        {
        	gstrUserList[size].id = (unsigned long)atol(ptr->d_name);
        	LOGD("gstrUserList[%d].id = %s", size, ptr->d_name);
        	size++;
        }
        if (size >= nMaxUserNum) {
        	LOGD("loadFileList() break; size >= nMaxUserNum(%d).", nMaxUserNum);
        	break;
        }
    }
    LOGD("loadFileList() size:%d", size);
    closedir(dir);
    return size;
}

/*********************************************************************\
 ** ����: �����û���userid�����û�������ֵ
 ** ����:
   pUserInfo�� �û�ģ����Ϣ
 ** ����:
 \********************************************************************/
int LoadOneUserFeature(USERINFO* pUserInfo)
{
	int n = 0;
	int ret = JNI_OK;
	int fd = 0;
	char filePath[GENERAL_PATH_LEN] ={ 0 };

	if (pUserInfo == NULL)
	{
		ret = JNI_ERR;
		goto error;
	}

	sprintf(
		filePath,                    /*  */
		"%s/%08u/feature.dat",  /*  */
		sUserDataPath,       /*  */
		pUserInfo->id);                     /*  */

	fd = open(filePath, O_RDONLY | O_NOATIME, 0666);
	if (fd <= 0)
	{
		LOGE("can not find user feature file (%s)", filePath);
		ret = JNI_ERR;
		goto error;
	}

	//LOGD("read() filePath:%s featureSize:%d", filePath, FEATURE_SIZE_GENERAL);
	n = read(fd, pUserInfo->pFeature, FEATURE_SIZE_GENERAL);
	//LOGD("read() ret:%d size:%d", n, FEATURE_SIZE_GENERAL);
	if (n != FEATURE_SIZE_GENERAL)
	{
		LOGW("read(fd, pUserInfo->pFeature, size) is not success.");
	}

error:
	if (fd > 0)
	{
		close(fd);
		fd = 0;
	}

	return ret;
}

/*********************************************************************\
 ** ����: �����û�������ģ�嵽�ڴ�
 ** ����: �ɹ�����0  ʧ�ܷ���-1
 \********************************************************************/
int LoadAllUserFeature()
{
	int ret = 0;
	int i = 0;
	USERINFO* pUserInfo;
    //LOGD("initUserInfoList()");
	// ����û�ģ����Ϣ�б�
    initUserInfoList();

	//LOGD("loadFileList()");
	nUserNum = loadFileList(sUserDataPath, gstrUserList);
	LOGD("loadFileList() nUserNum:%d", nUserNum);
	for (i = 0; i < nUserNum; i++)
	{
		pUserInfo = &gstrUserList[i];
		pUserInfo->numMBLBP = MAX_FEATURE_NUM_PER_USER;
		//pUserInfo->pFeature = pMaxUserFeatureBuffer + i * FEATURE_SIZE_GENERAL;
		//LOGD("LoadOneUserFeature() i:%d", i);
		ret = LoadOneUserFeature(pUserInfo);
		//LOGD("LoadOneUserFeature() ret:%d", ret);
		if (ret == JNI_OK)
		{
			pUserInfo->status = true;
		} else {
			pUserInfo->status = false;
		}
	}
	return JNI_OK;
}

/*********************************************************************\
 ** ����: �����û���useridɾ���û�������
 ** ����:
 ** userId �û����id
 ** ����:
 \********************************************************************/
int deleteUserFeature(int userId)
{
	int ret = 0;
    int nIndex = 0;
    char filePath[GENERAL_PATH_LEN] ={ 0 };
    char dirPath[GENERAL_PATH_LEN] ={ 0 };

    USERINFO* pUserInfo = getUserInfoByUserId(userId);
    if (pUserInfo != NULL) {
        clearUserInfo(pUserInfo);
    }

	sprintf(
		dirPath,                    /*  */
		"%s/%08u/",  /*  */
		sUserDataPath,       /*  */
		userId);

	sprintf(
		filePath,                    /*  */
		"%s/%08u/feature.dat",  /*  */
		sUserDataPath,       /*  */
		userId);                     /*  */

	ret = remove(filePath);
    if (ret != 0) {
    	LOGE("deleteUserFeature(%d) remove filePath fail, filePath:", userId, filePath);
    	ret = -1;
    	return ret;
    }

	remove(dirPath);
    if (ret != 0) {
    	LOGE("deleteUserFeature(%d) remove dirPath fail, dirPath:", userId, dirPath);
    	ret = -2;
    } else {
        nUserNum--;
    }
    return ret;
}

int addUserFeatureToMem(
		int userId,
		unsigned char* pFeatureData,  //
		int nFeatureSize  //
		)
{
	int ret = 0;
	int nIndex = 0;
	if (nFeatureSize == 0 || (nFeatureSize > FEATURE_SIZE_GENERAL) || (pFeatureData == NULL))
	{
	    LOGE("Feature To Mem Fail");
		ret = -1;
	}
	else
	{
		//LOGD("getUserIndexByUserId(%d)", userId);
	    USERINFO* pUserInfo = getUserInfoByUserId(userId);
	    if (pUserInfo != NULL) {
		    //LOGD("getUserIndexByUserId() nIndex:%d", nIndex);
		   // LOGD("addUserFeatureToMem() 2");
		    memset(pUserInfo->pFeature, 0, FEATURE_SIZE_GENERAL);
	        //LOGD("addUserFeatureToMem() 3");
	        memcpy(pUserInfo->pFeature, pFeatureData, nFeatureSize);
	        //LOGD("addUserFeatureToMem() 4");
	        pUserInfo->status = true;
		} else {
			LOGE("getUserInfoByUserId() is fail, pUserInfo is NULL.");
		}
	}
	return ret;
}

/*********************************************************************\
 ** ����: �����û���userid�����û�������
 ** ����:
 ** userId �û����id
    nFeatureSize�� �û�ģ�ʹ�С
    pFeatureData �û�ģ������
 ** ����: 0 �ɹ���С��0 ʧ��
 \********************************************************************/
int updateUserFeature(
	int userId,
	unsigned char* pFeatureData,  //
	int nFeatureSize  //
	)
{
	int ret = 0;
	int nIndex = 0;


	if (nFeatureSize == 0 || (nFeatureSize > FEATURE_SIZE_GENERAL) || (pFeatureData == NULL))
	{
	    LOGE("Feature To Mem Fail");
		ret = -1;
	}
	else
	{
	    USERINFO* pUserInfo = getUserInfoByUserId(userId);
	    if (pUserInfo != NULL) {
		    memset(pUserInfo->pFeature, 0, FEATURE_SIZE_GENERAL);
            memcpy(pUserInfo->pFeature, pFeatureData, nFeatureSize);
		}

        ret = saveUserFeatureToFile(userId, pFeatureData, nFeatureSize, NULL);
        if (ret < 0) {
        	LOGE("saveUserFeatureToFile() fail. userId:%d", userId);
        }
	}
	return ret;
}

/******************************************************************************
 * �������ƣ� getUserIndexToRegister
 * ���ܣ� �����û�id�õ��û��±�
 * ���أ� �û������±�
 ******************************************************************************/
int getUserIndexToRegister()
{
	int nIndex = 0;

	for (nIndex = 0; nIndex < MAX_USER_NUM; nIndex++)
	{
		if (!gstrUserList[nIndex].status)
		{
			break;
		}
	}

	if (nIndex >= nUserNum)
	{
		nIndex = -1;
	}

	return nIndex;
}

void initSdk(char* libPath, char* tempPath) {
	//LOGD("THFI_SetDir()");
    THFI_SetDir(libPath, tempPath);
    //LOGD("THFL_SDK_SetDir()");
    THFL_SDK_SetDir(libPath, tempPath);
    //LOGD("EF_SetDir()");
    EF_SetDir(libPath, tempPath);
    //LOGD("initSdk() end");
}

int initSDKBuffer() {
	int ret = 0;
	//LOGD("initSDKBuffer Malloc(ONE_FEATURE_BUFFER_SIZE)");
	pOneFeatureBuffer = (unsigned char*)Malloc(ONE_FEATURE_BUFFER_SIZE);
    if (pOneFeatureBuffer != NULL) {
    	memset(pOneFeatureBuffer, 0, ONE_FEATURE_BUFFER_SIZE);
    } else {
    	LOGE("initSDKBuffer() Malloc(ONE_FEATURE_BUFFER_SIZE) fail.");
    	ret = -1;
    	return ret;
    }
    //LOGD("initSDKBuffer Malloc(MAX_USER_FEATURE_BUFFER_SIZE)");
	pMaxUserFeatureBuffer = (unsigned char*)Malloc(MAX_USER_FEATURE_BUFFER_SIZE);
    if (!pMaxUserFeatureBuffer) {
    	LOGE("initSDKBuffer() Malloc(MAX_USER_FEATURE_BUFFER_SIZE) fail.");
    	ret = -2;
    	return ret;
    } else {
    	//LOGD("initSDKBuffer memset()");
    	memset(pMaxUserFeatureBuffer, 0, MAX_USER_FEATURE_BUFFER_SIZE);
    }
    //LOGD("initSDKBuffer() end");
    return ret;
}

void releaseSDKBuffer()
{
    if (pOneFeatureBuffer) {
    	Free(pOneFeatureBuffer);
    }
    if (pMaxUserFeatureBuffer) {
    	Free(pMaxUserFeatureBuffer);
    }
}
/**
 * ��ȡ�㷨����KEY
 *  @return 8�ֽ��ֽ�����;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceRecognizeNative_getFeatureSN( JNIEnv* env,jobject obj)
{
	unsigned char outSn[32] = {0};
	EF_OutputSN((unsigned char*)&outSn);
	jbyteArray results = env->NewByteArray(FACE_AUTH_SN_LENGTH);
	env->SetByteArrayRegion(results, 0, FACE_AUTH_SN_LENGTH, (jbyte*)&outSn);
	return results;
}

/**
 * ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
 * @sncode ���ܺ���ֽ�����
 * @return �ɹ�����1��ʧ�ܷ���0����
 */
JNIEXPORT  jint JNICALL Java_com_face_sv_FaceRecognizeNative_checkFeatureSN( JNIEnv* env,jobject obj, jbyteArray sncode)
{
	int ret = -1;
	unsigned char inputSn[32] = {0};
	unsigned char* input = (unsigned char*)env->GetByteArrayElements(sncode, 0);
	int size = env->GetArrayLength(sncode);
	if (size >= FACE_AUTH_SN_LENGTH) {
		memcpy(&inputSn, input, 8);
		ret = EF_InputSN((unsigned char*)&inputSn);
	}
	return ret;
}

/**
 * ��ʼ����������㷨��
 * @ libDir �㷨���·��
 * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
 * @ maxUserNum ����û�����
 * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_initFaceLibrary(JNIEnv* env,jobject obj,jstring libDir, jstring tempDir, jstring userDataPath, jint maxUserNum)
{
    int ret = -1;
	const char* libPath = env->GetStringUTFChars(libDir, 0);
    const char* tempPath = env->GetStringUTFChars(tempDir, 0);
    const char* uDataPath = env->GetStringUTFChars(userDataPath, JNI_FALSE);
    nModelNum = MAX_FEATURE_NUM_PER_USER;
    nMaxUserNum = maxUserNum;
    LOGD("initFaceLibrary() libPath:%s tempPath:%s userDataPath:%s nModelNum:%d", libPath, tempPath, uDataPath, nModelNum);

    sUserDataPath = strdup(uDataPath);

    //LOGD("initSDKBuffer()");
    ret = initSDKBuffer();
    //LOGD("initSDKBuffer() ret:%d", ret);
    if (0 != ret) {
    	LOGE("initSDKBuffer() is fail. ret:%d", ret);
    	return ret;
    }

    //LOGD("LoadAllUserFeature()");
    ret = LoadAllUserFeature();
    if (ret != 0) {
    	LOGD("LoadAllUserFeature() fail. ret:%d", ret);
    }

    //LOGD("initSdk()");
    initSdk((char*)libPath, (char*)tempPath);

    env->ReleaseStringUTFChars(libDir, libPath);
    env->ReleaseStringUTFChars(tempDir, tempPath);


    //LOGD("EF_Init()");
	ret = EF_Init(DEFAULT_CHANNEL_NUMBER);
	LOGD("EF_Init() ret:%d", ret);
	if (ret <= 0) {
		LOGE("Create feature extract channel failed!\r\n");
		return ret;
	}
    //LOGD("authSdk() ret:%d", ret);
	return ret;
}

/**
 * �ͷ���������㷨��
 */
JNIEXPORT void Java_com_face_sv_FaceRecognizeNative_releaseFaceLibrary(JNIEnv* env,jobject obj) {
	LOGD("releaseFaceLibrary()");
	THFI_Release();
	THFL_Release();
	EF_Release();
	releaseSDKBuffer();
}


/**
 * ��ȡ��֪�������������ͼƬ������ģ��
 * @param rgb24 ����ͼƬBGR24
 * @param faceData  ͼƬ������Ϣ
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @return ����ģ�����ݣ��ɹ����鳤��2008�ֽ�, ʧ�����鳤��Ϊ1��byte[0]����0������
 * (-101��ʾ��������Ϊ��;-102��ʾmallocģ��ʧ��;103��ʾ�����faceInfo��Ϣ����;
 */
JNIEXPORT jbyteArray JNICALL Java_com_face_sv_FaceFeatureNative_getFaceFeature( JNIEnv* env,jobject obj, jbyteArray rgb24, jbyteArray faceData, int width, int height) {
	LOGD("getFaceFeature()");
	//long tm = getSystemTime();
	int ret = -1;
	signed char retChar = -1;
	jbyteArray arr = NULL;
	int pSize = sizeof(THFI_FacePos);
	int length = env->GetArrayLength(faceData);
	BYTE* face = (BYTE*) env->GetByteArrayElements(faceData, 0);
	LOGD("faceData size:%d, length:%d", face[0], length);
	if (face[0] > 0 && length >= pSize)
	{
		unsigned char* rgb = (unsigned char*) env->GetByteArrayElements(rgb24, 0);
	    if (rgb != NULL && face != NULL) {
		    // �ֽ����鳤��
		    long size = EF_Size();
		    LOGD("EF_Size() size:%ld", size);
		    unsigned char * pFeature = (unsigned char *) malloc(size);
		    memset(pFeature,0,size);
		    if (pFeature != NULL) {
			    THFI_FacePos fps;
			    memcpy(&fps, face, pSize);
			    // ��ȡ����ģ������
			    ret = EF_Extract(DEFAULT_CHANNEL_INDEX, (unsigned char*) rgb, width, height, 3, (THFI_FacePos*)&fps, (BYTE*) pFeature);
			    LOGD("EF_Extract() ret:%d", ret);
			    if (1 == ret)
			    {
				    arr = env->NewByteArray(size);
				    env->SetByteArrayRegion(arr, 0, size,(signed char*)pFeature);
			    }
			    else
			    {
				    LOGD("EF_Extract() fail, ret:%d .", ret);
				    retChar = 0;
			    }
			    free(pFeature);
		    }
		    else
		    {
			    LOGD("malloc(size) fail, pFeature == NULL.");
			    retChar = -102;
		    }
	    }
	    else
	    {
		    LOGD("getFaceFeature() rgb24 is null ||  faceInfo is null.");
		    retChar = -101;
	    }
		env->ReleaseByteArrayElements(rgb24, (signed char*) rgb, 0);
	}
	else
	{
		LOGD("getFaceFeature() faceInfo is no face or error.");
		retChar = -103;
	}

	if (arr == NULL) {
		arr = env->NewByteArray(1);
		env->SetByteArrayRegion(arr, 0, 1, (signed char*) &retChar);
	}
	env->ReleaseByteArrayElements(faceData, (jbyte*) face, 0);
	LOGD("getFaceFeature() ret:%d ", ret);
	return arr;
}


/**
 * �ȶ���������ģ�����ƶ�
 * @param feature1 ����ģ��
 * @param feature2 ����ģ��
 * @return ��ʶ�ȣ���ֵ��Χ0 ~ 100֮�䣩
 */
JNIEXPORT jint JNICALL Java_com_face_sv_FaceFeatureNative_compareFeature( JNIEnv* env,jobject obj, jbyteArray feature1, jbyteArray feature2) {
	LOGD("compareFeature()");
	int ret = -1;
    jsize size1 = env->GetArrayLength(feature1);
    jbyte* f1 = (jbyte*)env->GetByteArrayElements(feature1, 0);
    jsize size2 = env->GetArrayLength(feature2);
    jbyte* f2 = (jbyte*)env->GetByteArrayElements(feature2, 0);
    int s1 = size1 * sizeof(jbyte);
    int s2 = size2 * sizeof(jbyte);
    unsigned char* fData1 = (unsigned char*)malloc(s1);
    unsigned char* fData2 = (unsigned char*)malloc(s2);
    if (fData1 != NULL && fData2 != NULL) {
    	memcpy(fData1, f1, s1);
    	memcpy(fData2, f2, s2);
    	//timeval tv_begin, tv_end;
        //LOGD("EF_Compare()");
        //gettimeofday(&tv_begin, NULL);
    	float result = EF_Compare(fData1, fData2);
    	//gettimeofday(&tv_end, NULL);
    	//LOGD("EF_Compare() result:%f ,begin time:%ld end time:%ld time:%ld", result, tv_begin.tv_usec, tv_end.tv_usec, (tv_end.tv_usec - tv_begin.tv_usec));
    	LOGD("EF_Compare() result:%f", result);
    	ret = (int)(result * (float) 100.0f);
    }
    free(fData1);
    free(fData2);
    env->ReleaseByteArrayElements(feature1, f1, 0);
    env->ReleaseByteArrayElements(feature2, f2, 0);
    LOGD("compareFeature() ret:%d", ret);
	return (int)ret;
}

/**
 * ע������ģ��
 * @ userId ע���û�ģ�ͱ��
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @facePos ��������õ�������Ϣ
 * @return ���ڵ���0ע��ɹ�����ʾע��ģ�ͱ�ţ�0-N),С��0��ʾʧ�ܡ�(-1��ʾģ�ͱ���ʧ��)
 *  * error code:
 * -100 faceFeature malloc fail.
   -99,invalid license.
   -1,pBuf,ptfp,pFeature is NULL
   -2,nChannelID is invalid or SDK is not initialized
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_registerFaceFeature(JNIEnv* env,jobject obj,jint userId, jbyteArray BGR24, jint width, jint height, jbyteArray facePos) {
	//LOGD("registerFaceFeature()");
	int ret = -1;
	BYTE* pBGR24 = (BYTE*)env->GetByteArrayElements(BGR24, 0);
	long sz = env->GetArrayLength(BGR24);
	int pSize = sizeof(THFI_FacePos);
	int length = env->GetArrayLength(facePos);
	BYTE* pFacePos = (BYTE*) env->GetByteArrayElements(facePos, 0);
	LOGD("faceData size:%d, length:%d", pFacePos[0], length);
	if (pFacePos != NULL && length >= pSize)
	{
		BYTE* pFeature;
		long fSize = EF_Size();
		pFeature = (BYTE*) malloc(fSize);
		memset(pFeature, 0, fSize);
		if (pFeature != NULL) {
		    ret = getFaceFeature(pBGR24, width, height, pFacePos, pFeature);
		    if (1 == ret) {
		        ret = saveUserFeatureToFile(userId, pFeature, fSize, NULL);
		        LOGE("saveUserFeatureToFile() ret:%d", ret);
		        if (0 == ret) {
			    	USERINFO* pUserInfo = NULL;
			    	pUserInfo = getEmptyUserInfoToRegister();
			    	if (pUserInfo!=NULL) {
				        pUserInfo->id = userId;
			            memcpy(pUserInfo->pFeature, pFeature, fSize);
			            pUserInfo->status = true;
			            LOGE("addUserFeatureToMem() success userId:%d", userId);
			            nUserNum++;
			    	} else {
			    		LOGE("addUserFeatureToMem() is fail, none mem.");
			    	}
                    ret = 1;
		        } else {
		        	ret = -103;
		        	LOGE("saveUserFeatureToFile() userId:%d is fail, result:%d.", userId, ret);
		        }
		    } else {
		    	ret = -102;
		    	LOGD("getFaceFeature() fail,  return NULL. result:%d", ret);
		    }
		    free(pFeature);
		} else {
			LOGD("malloc(fSize),  return NULL. result:%d", ret);
		}
	}
	else
	{
		//LOGD("registerFaceFeature()isInitSuccess is false or  faceInfo is null or data is error.");
		ret = -101;
	}
	//LOGD("getFaceFeature() ret:%d ", ret);
	env->ReleaseByteArrayElements(BGR24, (signed char*)pBGR24, 0);
	env->ReleaseByteArrayElements(facePos, (signed char*)pFacePos, 0);
	return ret;
}

/**
 * ע������ģ��
 * @ userId ע���û�ģ�ͱ��
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @facePos ��������õ�������Ϣ
 * @return ���ڵ���0���³ɹ�����ʾ����ģ�ͱ�ţ�0-N),С��0��ʾʧ�ܡ�
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_updateFaceFeature(JNIEnv* env,jobject obj,jint userId, jbyteArray BGR24, jint width, jint height, jbyteArray facePos) {
	//LOGD("registerFaceFeature()");
	int ret = -1;
	int nUserId = userId;
	BYTE* img = (BYTE*)env->GetByteArrayElements(BGR24, 0);
	long sz = env->GetArrayLength(BGR24);
	int pSize = sizeof(THFI_FacePos);
	int length = env->GetArrayLength(facePos);
	BYTE* face = (BYTE*) env->GetByteArrayElements(facePos, 0);
	//LOGD("faceData size:%d, length:%d", face[0], length);
	if (face != NULL && length >= pSize)
	{
		BYTE* faceFeature;
		ret = getFaceFeature(img, width, height, face, faceFeature);
		if (1 != ret) {
		    ret = updateUserFeature(nUserId, faceFeature, sizeof(faceFeature));
		    if (ret < 0) {
                LOGE("saveUserFeatureToFile() userId:%d is fail.", userId);
		    }
		} else {
			LOGE("getFaceFeature() fail,  return NULL.");
		}
	}
	else
	{
		LOGE("registerFaceFeature() faceInfo is null or data is error.");
		ret = -102;
	}
	//LOGD("getFaceFeature() ret:%d ", ret);
	return ret;
}

/**
 * ɾ������ģ��
 * @ userId ע���û�ģ�ͱ��
 * @return 0Ϊ�ɹ���С��0 ��ʾʧ��
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_deleteFaceFeature(JNIEnv* env,jobject obj,jint userId) {
    int nUserId = userId;
    int ret = deleteUserFeature(nUserId);
    return ret;
}

/**
 * �����������ģ��
 * @return 0Ϊ�ɹ���С��0 ��ʾʧ��
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_clearAllFaceFeature(JNIEnv* env,jobject obj) {
    //LOGD("initUserInfoList()");
	// ����û�ģ����Ϣ�б�
	nUserNum = 0;
    initUserInfoList();
}

/**
 * ���¼�������ģ��
 * @ userId ע���û�ģ�ͱ��
 * @return 0Ϊ�ɹ���С��0 ��ʾʧ�ܡ�
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_reloadAllFaceFeature(JNIEnv* env,jobject obj) {
	return LoadAllUserFeature();
}

/**
 * ע������ģ��
 * @ userId �ȶ��û�ģ�ͱ��
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @facePos ��������õ�������Ϣ
 * @return ���ڵ���0ʶ��ɹ�������ʶ�����ƶȣ�0-100),С��0��ʾʧ�ܡ�
 */
JNIEXPORT jint Java_com_face_sv_FaceRecognizeNative_recognizeFaceOne(JNIEnv* env,jobject obj,jint userId, jbyteArray BGR24, jint width, jint height, jbyteArray facePos) {
	//LOGD("recognizeFaceOne()");
	int ret = -1;
	int nUserId = userId;
	BYTE* pBGR24 = (BYTE*)env->GetByteArrayElements(BGR24, 0);
	long sz = env->GetArrayLength(BGR24);
	int pSize = sizeof(THFI_FacePos);
	int length = env->GetArrayLength(facePos);
	BYTE* pFacePos = (BYTE*) env->GetByteArrayElements(facePos, 0);
	//LOGD("faceData size:%d, length:%d", pFacePos[0], length);
	if (pFacePos != NULL && length >= pSize)
	{
		//LOGD("recognizeFaceOne()");
		memset(pOneFeatureBuffer, 0, FEATURE_SIZE_GENERAL);
		//LOGD("recognizeFaceOne() start");
		ret = getFaceFeature(pBGR24, width, height, pFacePos, pOneFeatureBuffer);
		//LOGD("recognizeFaceOne() getFaceFeature() ret:%d", ret);
		if (1 == ret && nUserNum > 0) {
			USERINFO* pUserInfo = getUserInfoByUserId(nUserId);
			//LOGD("recognizeFaceOne()");
		    if (ret) {
		    	float result = EF_Compare(pOneFeatureBuffer, pUserInfo->pFeature);
		    	ret = (int)(result * (float) 100.0f);
		    } else {
		    	LOGE("getUserIndexByUserId() userId:%d is fail.", userId);
		    	ret = -101;
		    }
		} else {
			LOGE("getFaceFeature() fail,  return NULL.");
		}
	}
	else
	{
		LOGE("registerFaceFeature() faceInfo is null or data is error.");
		ret = -102;
	}
	//LOGD("getFaceFeature() ret:%d ", ret);
	return ret;
}

/**
 * ע������ģ��
 * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @facePos ��������õ�������Ϣ
 * @return int[0]���ڵ���0ע��ɹ�,�����û����,С��0��ʾʧ�ܡ�int[1]�ɹ�ʱ��ʾʶ�����
 */
JNIEXPORT jintArray Java_com_face_sv_FaceRecognizeNative_recognizeFaceMore(JNIEnv* env,jobject obj,jbyteArray BGR24, jint width, jint height, jbyteArray facePos) {
	//LOGD("recognizeFaceMore()");
	long ret = -1;
	int pRet[2] = {0};
	BYTE* pBGR24 = (BYTE*)env->GetByteArrayElements(BGR24, 0);
	long sz = env->GetArrayLength(BGR24);
	int pSize = sizeof(THFI_FacePos);
	int length = env->GetArrayLength(facePos);
	BYTE* pFacePos = (BYTE*) env->GetByteArrayElements(facePos, 0);
	//LOGD("faceData size:%d, length:%d", pSize, length);
	if (pFacePos != NULL && length >= pSize)
	{
		memset(pOneFeatureBuffer, 0, FEATURE_SIZE_GENERAL);
		//LOGD("recognizeFaceMore() getFaceFeature()");
		ret = getFaceFeature(pBGR24, width, height, pFacePos, pOneFeatureBuffer);
		//LOGD("recognizeFaceMore() end");
		//LOGD("recognizeFaceMore() getFaceFeature() ret:%d", ret);
		if (1 == ret && nUserNum > 0) {
		    USERINFO* pUserInfo;
		    float result = 0.0f;
		    int maxResult = 0;
		    int comResult = 0;
		    //LOGD("recognizeFaceMore() nUserNum:%d", nUserNum);
		    ret = -1;
		    //timeval tm;
		    for(int i = 0; i < nUserNum; i++) {
		        pUserInfo= &gstrUserList[i];
		        //LOGD("gstrUserList[%d].status:%d userId:%d", i, pUserInfo->status, pUserInfo->id);
		        if (pUserInfo->status) {
		        	//tm = getSystemTime();
		        	//LOGD("EF_Compare() start");
                    //#pragma omp parallel for
		        	//for(int k = 0; k < 10000; k++) {
		                result = EF_Compare(pOneFeatureBuffer, pUserInfo->pFeature);
		        	//}
		            //LOGD("EF_Compare() end time:%ld" , getSystemTime().tv_usec - tm.tv_usec);
		            //LOGD("EF_Compare() userId:%d result:%f", pUserInfo->id, result);
		            comResult = (int)(result * 100);
		            //LOGD("comResult:%d", comResult);
		            if (comResult > maxResult && comResult > 0) {
		               //LOGD("comResult > maxResult  maxResult:%d", maxResult);
		            	maxResult = comResult;
			            pRet[0] = pUserInfo->id;
			            pRet[1] = comResult;
			            ret = 0;
		            }
		        }
		    }
		} else {
			LOGE("getFaceFeature() fail or nUserNum = 0. ret:%d, nUserNum:%d", ret, nUserNum);
			ret = -102;
		}
	}
	else
	{
		LOGD("registerFaceFeature() isInitSuccess is false or faceInfo is null or data is error.");
		ret = -103;
	}
	env->ReleaseByteArrayElements(BGR24, (signed char*)pBGR24, 0);
	env->ReleaseByteArrayElements(facePos, (signed char*)pFacePos, 0);

	jintArray pArr = env->NewIntArray(2);
	if (ret < 0 ) {
         pRet[0] = ret;
         pRet[1] = 0;
	}
	//LOGD("getFaceFeature() ret:%d pRet[0]:%d pRet[1]:%d", ret, pRet[0], pRet[1]);
	env->SetIntArrayRegion(pArr, 0, 2, pRet);
	return pArr;
}

#ifdef __cplusplus
}
#endif
