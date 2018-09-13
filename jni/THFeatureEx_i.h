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
// 																���ܼ�Ȩ

// ���ؼ��ܺ�����루����DM2016�߼�������ܺ������룬DM2016ʹ��ǰ8���ֽڣ�
// ����ʱ�䣺�� EF_Init �� EF_InputSN ǰ����
THEATUREE_API void EF_OutputSN(unsigned char encryptSN[32]);

// ������ܺ�����루����DM2016оƬӲ�����ܺ������룬DM2016ʹ��ǰ8���ֽڣ�
// ����ʱ�䣺�� EF_Init ǰ���ã��� EF_OutputSN �����
// ����ֵ��==1 �Ѿ�������֤ͨ����==0 ��δ������֤ͨ��
THEATUREE_API int EF_InputSN(const unsigned char decryptSN[32]);

// 																���ܼ�Ȩ
//###############################################################################################

#endif // THFEATUREEX_I_H