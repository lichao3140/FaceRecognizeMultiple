#ifndef THFEATUREEX_I_H
#define THFEATUREEX_I_H

/*
* ============================================================================
*  Name     : THFeature_i.h
*  Part of  : Face Feature (THFeature) SDK
*  Created  : 9.18.2015 by xxx
*  Description:
*     THFeature_i.h -  Face Feature(THFeature) SDK header file
*  Version  : 1.0.0
*  Copyright: All Rights Reserved by XXX
*  Revision:
* ============================================================================
*/

#include "FiStdDefEx.h"
#define THEATUREE_API

//###############################################################################################
// 																加密鉴权

// 返回加密后随机码（经过DM2016逻辑软件加密后的随机码，DM2016使用前8个字节）
// 调用时间：在 EF_Init 和 EF_InputSN 前调用
THEATUREE_API void EF_OutputSN(unsigned char encryptSN[32]);

// 输入解密后随机码（经过DM2016芯片硬件解密后的随机码，DM2016使用前8个字节）
// 调用时间：在 EF_Init 前调用，在 EF_OutputSN 后调用
// 返回值：==1 已经加密验证通过；==0 尚未加密验证通过
THEATUREE_API int EF_InputSN(const unsigned char decryptSN[32]);

// 																加密鉴权
//###############################################################################################

#endif // THFEATUREEX_I_H