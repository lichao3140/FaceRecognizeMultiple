#ifndef THFACEIMAGEEX_I_H
#define THFACEIMAGEEX_I_H

/*
* ============================================================================
*  Name     : THFaceImage_i.h
*  Part of  : Face Recognition (THFaceImage) SDK
*  Created  : 9.18.2015 by XXX
*  Description:
*     THFaceImage_i.h -  Face Recognition (THFaceImage) SDK header file
*  Version  : 1.0.0
*  Copyright: All Rights Reserved by XXXX
*  Revision:
* ============================================================================
*/

#include "FiStdDefEx.h"
#define THFACEIMAGE_API

//###############################################################################################
// 																加密鉴权

// 返回加密后随机码（经过DM2016逻辑软件加密后的随机码，DM2016使用前8个字节）
// 调用时间：在 THFI_InputSN 和 THFI_Create 前调用
THFACEIMAGE_API void THFI_OutputSN(unsigned char encryptSN[32]);

// 输入解密后随机码（经过DM2016芯片硬件解密后的随机码，DM2016使用前8个字节）
// 调用时间：在 THFI_Create 前调用，在 THFI_OutputSN 后调用
// 返回值：==1 已经加密验证通过；==0 尚未加密验证通过
THFACEIMAGE_API int THFI_InputSN(const unsigned char decryptSN[32]);

// 																加密鉴权
//###############################################################################################

#endif // THFACEIMAGEEX_I_H