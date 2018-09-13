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
// 																���ܼ�Ȩ

// ���ؼ��ܺ�����루����DM2016�߼�������ܺ������룬DM2016ʹ��ǰ8���ֽڣ�
// ����ʱ�䣺�� THFL_Create �� THFL_InputSN ǰ����
THFACELIVE_API void THFL_OutputSN(unsigned char encryptSN[32]);

// ������ܺ�����루����DM2016оƬӲ�����ܺ������룬DM2016ʹ��ǰ8���ֽڣ�
// ����ʱ�䣺�� THFL_Create ǰ���ã��� THFL_OutputSN �����
// ����ֵ��==1 �Ѿ�������֤ͨ����==0 ��δ������֤ͨ��
THFACELIVE_API int THFL_InputSN(const unsigned char decryptSN[32]);

// 																���ܼ�Ȩ
//###############################################################################################

#endif // THFACELIVEEX_I_H