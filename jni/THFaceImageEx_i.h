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
// 																���ܼ�Ȩ

// ���ؼ��ܺ�����루����DM2016�߼�������ܺ������룬DM2016ʹ��ǰ8���ֽڣ�
// ����ʱ�䣺�� THFI_InputSN �� THFI_Create ǰ����
THFACEIMAGE_API void THFI_OutputSN(unsigned char encryptSN[32]);

// ������ܺ�����루����DM2016оƬӲ�����ܺ������룬DM2016ʹ��ǰ8���ֽڣ�
// ����ʱ�䣺�� THFI_Create ǰ���ã��� THFI_OutputSN �����
// ����ֵ��==1 �Ѿ�������֤ͨ����==0 ��δ������֤ͨ��
THFACEIMAGE_API int THFI_InputSN(const unsigned char decryptSN[32]);

// 																���ܼ�Ȩ
//###############################################################################################

#endif // THFACEIMAGEEX_I_H