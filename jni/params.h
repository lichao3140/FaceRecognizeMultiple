/*
 * RecognizeFaceNative.h
 *
 *  Created on: 2015年9月17日
 *      Author: binsys
 */

#ifndef PUBLIC_PARAMS_H_
#define PUBLIC_PARAMS_H_

#ifdef __cplusplus
extern "C"
{
#endif

#define DEFAULT_CHANNEL_NUMBER    1      /* 默认算法通道数量*/
#define DEFAULT_CHANNEL_INDEX     0       /* 默认通道 */

#define RECOGNIZE_PADDING_SIZE  30          /* 人脸识别人脸坐标距离图像边线大于等于padding距离才能做为注册识别有效图像 */

#define RECOGNIZE_ANGLE_THRESHOLD  15       /* 人脸偏转角度  */

#define FACE_AUTH_SN_LENGTH  8                   /* 人脸算法鉴权秘钥有效长度 */

#define RECOGNIZE_FACE_ANGLE  0.7f          /* 人脸可信度  */
#define REGISTER_FACE_ANGLE  0.7f           /* 人脸可信度  */

#define RECOGNIZE_ANGLE_THRESHOLD  20             /* 人脸偏转角度  */
#define REGISTER_ANGLE_THRESHOLD  15             /* 人脸偏转角度  */

#define TYPE_FACE_RECOGNIZE  0             /* 人脸识别类型那个  */
#define TYPE_FACE_REGISTER  1             /* 人脸注册类型 */


#define GENERAL_PATH_LEN        256                //路径的长度
//#define MAX_DIR_USER_NUM        2000               //每个目录下最大目录数  防止每个目录下用户数过多
#define FEATURE_SIZE_GENERAL     2560               /* 特征值长度 单位 byte */
#define MAX_FEATURE_NUM_PER_USER         1        /* 模板数 */
#define MAX_USER_NUM            10000               /* 最大人数 */
#define FACEDETECT_FIND_NO_FACE        -255        /* 检测不到人脸场景时返回的错误码 */
#define _THRESHOLD_ENROL_MIN            35         /*防止一个模板注册多个人*/

#define ONE_FEATURE_BUFFER_SIZE   FEATURE_SIZE_GENERAL                                                    /* 初始化当前用户模型存储空间 */
#define MAX_USER_FEATURE_BUFFER_SIZE   FEATURE_SIZE_GENERAL * MAX_USER_NUM * MAX_FEATURE_NUM_PER_USER     /* 初始化用户模型存储空间 */

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
