#ifndef THFEATURE_I_H
#define THFEATURE_I_H

#include "THFaceImage_i.h"

/*
* ============================================================================
*  Name     : THFeature_i.h
*  Part of  : Face Feature (THFeature) SDK
*  Created  : 10.18.2016 by xxx
*  Description:
*     THFeature_i.h -  Face Feature(THFeature) SDK header file
*  Version  : 5.0.0
*  Copyright: All Rights Reserved by XXX
*  Revision:
* ============================================================================
*/

#define THFEATURE_API extern "C"

struct TH_Image_Data
{
	BYTE* bgr;//MUST BE bgr format buffer,the size is width*height*3 bytes
	int width;//image width
	int height;//image height
};

struct EF_Param
{
	int nDeviceID;//device id for GPU device.eg:0,1,2,3.....
	EF_Param()
	{
		nDeviceID = 0;
	}
};
//////API define//////
THFEATURE_API void EF_SetDir(const char* modelDir, const char* tmpDir);
/*
 The EF_SetDir function will set face feature environment directory,it must be called before EF_Init
 Parameters:
	modelDir[input],存放 libTHFeature_db20a.so文件的目录，如果modelDir为NULL，则使用默认路径“/mnt/sdcard/SysConfig/model/”
	tmpDir[input],临时读写目录，供算法初始化使用，算法必须具有临时目录下的读写权限,如果tmpDir为NULL，则使用默认路径“"/mnt/sdcard/SysConfig/tmp/"”			
 Return Values:
	No return value.
 Remarks：         
	It must be called before EF_Init.
*/

THFEATURE_API short EF_Init(int nChannelNum);
/*
The EF_Init function will initialize the Face Feature(THFeature) algorithm module

Parameters:
nChannelNum,the channel number,support for muti-thread,one channel stand for one thread.max value is 32.
Return Values:
If the function succeeds, the return value is valid channel number.
If the function fails, the return value is 0 or nagative;
error code:
-99,invalid license.
-1,open file "feadb.db*" error
-2,check  file "feadb.db*" error
-3,read  file "feadb.db*" error
Remarks:
This function can be called one time at program initialization.
*/

THFEATURE_API int EF_Size();
/*
The EF_Size function will return face feature size.

Parameters:
No parameter.
Return Values:
If the function succeeds, the return value is face feature size.
If the function fails, the return value is 0 or nagative;
error code:
-99,invalid license.
Remarks:
No remark.
*/

THFEATURE_API int EF_Extract(short nChannelID, BYTE* pBuf, int nWidth, int nHeight, int nChannel, THFI_FacePos* ptfp, BYTE* pFeature);
/*
The EF_Extract function execute face feature extraction from one photo

Parameters:
nChannelID[input],channel ID(from 0 to nChannelNum-1)
pBuf[input],point to an image buffer,BGR format.
nWidth[input],the image width.
nHeight[input],the image height.
nChannel[input],image buffer channel,must be 3
ptfp[input],the facial data of a face.
pFeature[output],the face feature buffer
Return Values:
If the function succeeds, the return value is 1.
If the function fails, the return value is nagative.
error code:
-99,invalid license.
-1,pBuf,ptfp,pFeature is NULL
-2,nChannelID is invalid or SDK is not initialized
Remarks:
No remark.
*/

THFEATURE_API int EF_Extract_M(short nChannelID, BYTE* pBuf, int nWidth, int nHeight, int nChannel, THFI_FacePos* ptfps, BYTE* pFeatures, int nFaceNum);
/*
The EF_Extract_M function execute face feature extraction for muti-faces from one photo

Parameters:
nChannelID[input],channel ID(from 0 to nChannelNum-1)
pBuf[input],point to an image buffer,BGR format.
nWidth[input],the image width.
nHeight[input],the image height.
nChannel[input],image buffer channel,must be 3
ptfps[input],the facial data of muti-faces
pFeatures[output],the face feature buffer for muti-faces
nFaceNum[input],the face number
Return Values:
If the function succeeds, the return value is 1.
If the function fails, the return value is 0 or nagative.
error code:
-99,invalid license.
-1,pBuf,ptfps,pFeatures is NULL
-2,nChannelID is invalid or SDK is not initialized
Remarks:
No remark.
*/

THFEATURE_API int EF_Extracts(short nChannelID, TH_Image_Data* ptids, THFI_FacePos* ptfps, BYTE* pFeatures, int nNum);
/*
The EF_Extracts function execute face feature extraction for muti-faces from muti-photos

Parameters:
nChannelID[input],channel ID(from 0 to nChannelNum-1)
ptids[input],the image data list of muti-photos
ptfps[input],the facial data list of muti-photos(one image data-one facial data)
pFeatures[output],the face feature buffer for muti-faces
nNum[input],the image data number
Return Values:
If the function succeeds, the return value is 1.
If the function fails, the return value is 0 or nagative.
error code:
-99,invalid license.
-1,ptids,ptfp,pFeature is NULL
-2,nChannelID is invalid or SDK is not initialized
Remarks:
No remark.
*/

THFEATURE_API float EF_Compare(BYTE* pFeature1, BYTE* pFeature2);
/*
The EF_Compare function execute two face features compare.

Parameters:
pFeature1[input],point to one face feature buffer.
pFeature2[input],point to another face feature buffer.
Return Values:
the return value is the two face features's similarity.
Remarks:
No remark.
*/

THFEATURE_API void EF_Release();
/*
The EF_Release function will release the Face Feature (THFeature) algorithm module

Parameters:
No parameter.
Return Values:
No return value.
Remarks:
This function can be called one time at program Un-Initialization.
*/

THFEATURE_API short EF_Init_Ex(int nChannelNum, EF_Param* pParam = NULL);
/*
The EF_Init_Ex function will initialize the Face Feature(THFeature) algorithm module,only for GPU version

Parameters:
nChannelNum,the channel number,support for muti-thread,one channel stand for one thread.max value is 32.
pParam,initialize parameter
Return Values:
If the function succeeds, the return value is valid channel number.
If the function fails, the return value is 0 or nagative;
error code:
-99,invalid license.
-1,open file "feadb.db*" error
-2,check  file "feadb.db*" error
-3,read  file "feadb.db*" error
Remarks:
This function can be called one time at program initialization.
*/

#endif
