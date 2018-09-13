#ifndef THFACELIVE_I_H
#define THFACELIVE_I_H

/*
* ============================================================================
*  Name     : THFaceLive_i.h
*  Part of  : Face Liveness Detect (THFaceLive) SDK
*  Created  : 9.1.2017 by XXX
*  Description:
*     THFaceLive_i.h -  Face Liveness Detect (THFaceLive) SDK header file
*  Version  : 2.0.0
*  Copyright: All Rights Reserved by XXXX
*  Revision:
* ============================================================================
*/
#include "THFaceImage_i.h"

#define THFACELIVE_API extern "C"

THFACELIVE_API void THFL_SDK_SetDir(const char* modelDir, const char* tmpDir);
/*
 The THFL_SDK_SetDir function will set SDK environment directory,it must be called before THFL_Create
 Parameters:
	modelDir[input],存放 libTHFacelive_*.so文件的目录，如果modelDir为NULL，则使用libTHFaceLive.so所在的目录
	tmpDir[input],临时读写目录，供算法初始化使用，算法必须具有临时目录下的读写权限,如果tmpDir为NULL，则使用默认路径"/tmp/"		
 Return Values:
	No return value.
 Remarks：         
	It must be called before THFL_Create.
*/

THFACELIVE_API int	THFL_Create();
/*
The THFL_Create function will initialize the algorithm engine module

Parameters:
	No parameter.
Return Values:
	If the function succeeds, the return value is 1.
	If the function fails, the return value is negative;
Remarks:
	This function only can be called one time at program initialization.
*/

THFACELIVE_API int	THFL_Detect(unsigned char* pBuf_color, unsigned char* pBuf_bw, int nWidth, int nHeight, THFI_FacePos*  ptfp_color, THFI_FacePos* ptfp_bw, int nThreshold=30);
/*
The THFL_Detect function execute face liveness detection

Parameters:
	pBuf_color[input],color camera image data buffer,bgr format.
	pBuf_bw[input],black-white camera image data buffer,bgr format.
	nWidth[input],image width.
	nHeight[input],image height.
	ptfp_color[input],face data of color camera image.(THFI_FacePos format,return by THFI_DetectFace of THFaceImage SDK)
	ptfp_bw[input],face data of black-white camera image.(THFI_FacePos format,return by THFI_DetectFace of THFaceImage SDK)
	nThreshold[input],score threshold(sugguest value is 30)
Return Values:
	If the function succeeds, the return value is 0 or 1.(0->fake face,1->live face)
	If the function fails, the return value is negative.
Remarks:
*/
THFACELIVE_API void	THFL_Release();
/*
The THFL_Release function will release the algorithm engine module

Parameters:
	No parameter.
Return Values:
	No return value.
Remarks:
	This function only can be called one time at program exit.
*/

#endif