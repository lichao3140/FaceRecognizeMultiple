#ifndef THFACELIVEEX_I_H
#define THFACELIVEEX_I_H

/*
* ============================================================================
*  Name     : THFaceLive_i.h
*  Part of  : Face Live (THFaceLive) SDK
*  Created  : 9.18.2015 by xxx
*  Description:
*     THFeature_i.h -  Face Live(THFaceLive) SDK header file
*  Version  : 1.0.0
*  Copyright: All Rights Reserved by XXX
*  Revision:
* ============================================================================
*/

#include "FiStdDefEx.h"

#define THFACELIVE_API extern "C"

//###############################################################################################
// 																加密鉴权

// 返回加密后随机码（经过DM2016逻辑软件加密后的随机码，DM2016使用前8个字节）
// 调用时间：在 THFL_Create 和 THFL_InputSN 前调用
THFACELIVE_API void THFL_OutputSN(unsigned char encryptSN[32]);

// 输入解密后随机码（经过DM2016芯片硬件解密后的随机码，DM2016使用前8个字节）
// 调用时间：在 THFL_Create 前调用，在 THFL_OutputSN 后调用
// 返回值：==1 已经加密验证通过；==0 尚未加密验证通过
THFACELIVE_API int THFL_InputSN(const unsigned char decryptSN[32]);

// 																加密鉴权
//###############################################################################################

#endif // THFACELIVEEX_I_H