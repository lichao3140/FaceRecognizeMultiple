#ifndef THFACEIMAGE_I_H
#define THFACEIMAGE_I_H

#include "FiStdDefEx.h"

/*
* ============================================================================
*  Name     : THFaceImage_i.h
*  Part of  : Face Recognition (THFaceImage) SDK
*  Created  : 9.1.2016 by XXX
*  Description:
*     THFaceImage_i.h -  Face Recognition (THFaceImage) SDK header file
*  Version  : 4.0.0
*  Copyright: All Rights Reserved by XXXX
*  Revision:
* ============================================================================
*/

#define THFACEIMAGE_API extern "C"

//////Struct define//////

struct FaceAngle
{
	int   yaw;//angle of yaw,from -90 to +90,left is negative,right is postive
	int   pitch;//angle of pitch,from -90 to +90,up is negative,down is postive
	int   roll;//angle of roll,from -90 to +90,left is negative,right is postive
	float confidence;//confidence of face pose(from 0 to 1,0.6 is suggested threshold)
};

struct THFI_FacePos
{
    RECT		rcFace;//coordinate of face
   	POINT		ptLeftEye;//coordinate of left eye
	POINT		ptRightEye;//coordinate of right eye
	POINT		ptMouth;//coordinate of mouth
	POINT		ptNose;//coordinate of nose								
	FaceAngle	fAngle;//value of face angle
	int			nQuality;//quality of face(from 0 to 100)
	BYTE   		pFacialData[512];//facial data
	THFI_FacePos()
	{
		memset(&rcFace,0,sizeof(RECT));
		memset(&ptLeftEye,0,sizeof(POINT));
		memset(&ptRightEye,0,sizeof(POINT));
		memset(&ptMouth,0,sizeof(POINT));
		memset(&ptNose,0,sizeof(POINT));
		memset(&fAngle,0,sizeof(FaceAngle));
		nQuality=0;
		memset(pFacialData, 0, 512);
	}
};

typedef long long DWORD_PTR;
struct THFI_Param
{
	int nMinFaceSize;//min face width size can be detected,default is 50 pixels
	int nRollAngle;//max face roll angle,default is 30(degree)
	bool bOnlyDetect;//ingored
	DWORD_PTR dwReserved;//reserved value,must be NULL
	THFI_Param()
	{
		nMinFaceSize=50;
		nRollAngle=30;
		bOnlyDetect=false;
		dwReserved=NULL;
	}
};

struct THFI_Param_Ex
{
	THFI_Param tp;
	int nDeviceID;//device id for GPU device.eg:0,1,2,3.....
	THFI_Param_Ex()
	{
		nDeviceID = 0;
	}
};


//////API define//////
THFACEIMAGE_API void THFI_SetDir(const char* modelDir, const char* tmpDir);
/*
 The THFI_SetDir function will set face detection environment directory,it must be called before THFI_Create
 Parameters:
	modelDir[input],存放 libTHDetect_dpbin.so文件的目录，如果modelDir为NULL，则使用默认路径“/mnt/sdcard/SysConfig/model/”
	tmpDir[input],临时读写目录，供算法初始化使用，算法必须具有临时目录下的读写权限,如果tmpDir为NULL，则使用默认路径“"/mnt/sdcard/SysConfig/tmp/"”			
 Return Values:
	No return value.
 Remarks：         
	It must be called before THFI_Create.
*/

THFACEIMAGE_API int		THFI_Create(short nChannelNum,THFI_Param* pParam);
/*
 The THFI_Create function will initialize the algorithm engine module

 Parameters:
	nChannelNum[intput],algorithm channel num,for multi-thread mode,one thread uses one channel
	pParam[input],algorithm engine parameter.
 Return Values:
	If the function succeeds, the return value is valid channel number.
	If the function fails, the return value is zero or negative;
	error code:
		-99,invalid license.
 Remarks: 
	This function only can be called one time at program initialization.
*/

THFACEIMAGE_API int		THFI_DetectFace(short nChannelID, BYTE* pImage, int bpp, int nWidth, int nHeight, THFI_FacePos* pfps, int nMaxFaceNums, int nSampleSize=360);
/*
 The THFI_DetectFace function execute face detection only.

 Parameters:
	nChannelID[input],channel ID(from 0 to nChannelNum-1)
	pImage[input],image data buffer,RGB24 format.
	bpp[input],bits per pixel(24-RGB24 image),must be 24
	nWidth[input],image width.
	nHeight[input],image height.
	pfps[output],the facial position information.
	nMaxFaceNums[input],max face nums that you want
	nSampleSize[input],down sample size(image down sample) for detect image,if it is 0,will detect by original image.
 Return Values:
	If the function succeeds, the return value is face number.
	If the function fails, the return value is negative.
	error code:
		-99,invalid license.
		-1,nChannelID is invalid or SDK is not initialized
		-2,image data is invalid,please check function parameter:pImage,bpp,nWidth,nHeight
		-3,pfps or nMaxFaceNums is invalid.
 Remarks:
	1.image data buffer(pImage)	size must be nWidth*(bpp/8)*nHeight.
	2.pfps must be allocated by caller,the memory size is nMaxFaceNums*sizeof(THFI_FacePos).
	3.if image has face(s),face number less than or equal to nMaxFaceNums
*/

THFACEIMAGE_API int THFI_DetectFaceByEye(short nChannelID, BYTE* pImage, int nWidth, int nHeight, POINT ptLeft, POINT ptRight, THFI_FacePos* pfps);
/*
The THFI_DetectFaceByEye function detect facial data by eye position

Parameters:
	pImage[input],image data buffer,rgb24 format,pImage data size must be nWidth*nHeight*3 bytes
	nWidth[input],image width.
	nHeight[input],image height.
	ptLeft[input],left eye position
	ptRight[input],right eye position
	pfps[output],the facial position information.
Return Values:
	If the function succeeds, the return value is 1.
	If the function fails, the return value is negative.
	error code:
		-99,invalid license.
		-1,nChannelID is invalid or SDK is not initialize
		-2,image data is invalid,please check function parameter:pImage,bpp,nWidth,nHeight
		-3,pfps or nMaxFaceNums is invalid.
*/

THFACEIMAGE_API void	THFI_Release();
/*
 The THFI_Release function will release the algorithm engine module

 Parameters:
	No parameter.
 Return Values:
	No return value.
 Remarks:
	This function only can be called one time at program exit.
*/

THFACEIMAGE_API int		THFI_Create_Ex(short nChannelNum, THFI_Param_Ex* pParam);
/*
The THFI_Create_Ex function will initialize the algorithm engine module,,only for GPU version

Parameters:
nChannelNum[intput],algorithm channel num,for multi-thread mode,one thread uses one channel
pParam[input],algorithm engine parameter.
Return Values:
If the function succeeds, the return value is valid channel number.
If the function fails, the return value is zero or negative;
error code:
-99,invalid license.
Remarks:
This function only can be called one time at program initialization.
*/

#endif
