/*
 * RecognizeFaceNative.h
 *
 *  Created on: 2015��9��17��
 *      Author: binsys
 */

#ifndef PUBLIC_PARAMS_H_
#define PUBLIC_PARAMS_H_

#ifdef __cplusplus
extern "C"
{
#endif

#define DEFAULT_CHANNEL_NUMBER    1      /* Ĭ���㷨ͨ������*/
#define DEFAULT_CHANNEL_INDEX     0       /* Ĭ��ͨ�� */

#define RECOGNIZE_PADDING_SIZE  30          /* ����ʶ�������������ͼ����ߴ��ڵ���padding���������Ϊע��ʶ����Чͼ�� */

#define RECOGNIZE_ANGLE_THRESHOLD  15       /* ����ƫת�Ƕ�  */

#define FACE_AUTH_SN_LENGTH  8                   /* �����㷨��Ȩ��Կ��Ч���� */

#define RECOGNIZE_FACE_ANGLE  0.7f          /* �������Ŷ�  */
#define REGISTER_FACE_ANGLE  0.7f           /* �������Ŷ�  */

#define RECOGNIZE_ANGLE_THRESHOLD  20             /* ����ƫת�Ƕ�  */
#define REGISTER_ANGLE_THRESHOLD  15             /* ����ƫת�Ƕ�  */

#define TYPE_FACE_RECOGNIZE  0             /* ����ʶ�������Ǹ�  */
#define TYPE_FACE_REGISTER  1             /* ����ע������ */


#define GENERAL_PATH_LEN        256                //·���ĳ���
//#define MAX_DIR_USER_NUM        2000               //ÿ��Ŀ¼�����Ŀ¼��  ��ֹÿ��Ŀ¼���û�������
#define FEATURE_SIZE_GENERAL     2560               /* ����ֵ���� ��λ byte */
#define MAX_FEATURE_NUM_PER_USER         1        /* ģ���� */
#define MAX_USER_NUM            10000               /* ������� */
#define FACEDETECT_FIND_NO_FACE        -255        /* ��ⲻ����������ʱ���صĴ����� */
#define _THRESHOLD_ENROL_MIN            35         /*��ֹһ��ģ��ע������*/

#define ONE_FEATURE_BUFFER_SIZE   FEATURE_SIZE_GENERAL                                                    /* ��ʼ����ǰ�û�ģ�ʹ洢�ռ� */
#define MAX_USER_FEATURE_BUFFER_SIZE   FEATURE_SIZE_GENERAL * MAX_USER_NUM * MAX_FEATURE_NUM_PER_USER     /* ��ʼ���û�ģ�ʹ洢�ռ� */

#define CLEAR(x) memset(&(x), 0, sizeof(x))

#define Malloc(s) malloc(s)
#define Free(p) \
if(p != NULL)   \
{               \
    free(p);    \
    p = NULL;   \
}

#ifdef __cplusplus
}
#endif

#endif /* PUBLIC_PARAMS_H_ */
