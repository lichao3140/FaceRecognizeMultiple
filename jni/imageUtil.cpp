#include <stdio.h>
#include <fcntl.h>
#include <sys/stat.h>

#include "yuv420.h"
#include "jpeg_if.h"
#include "imageUtil.h"

#ifdef __cplusplus
extern "C"
{
#endif
/**
 * ������ת��BRλ��
 */
void reverseBGR2RGB(unsigned char* bgr24, int width, int height) {
	int hHeight = height / 2;
	char b = 0;
	int index = 0;
	int size = width * height * 3;
    for (int i = 0; i < hHeight; i++) {
    	for (int j = 0; j < width; j++) {
            index = (i * width + j) * 3;
            b = bgr24[index];
            bgr24[index + 0] = bgr24[index + 2];
            bgr24[index + 2] = b;

            b = bgr24[size - index];
            bgr24[size - index] = bgr24[size - index + 2];
            bgr24[size - index + 2] = b;
    	}
    }
}

/**
 * Yuv420תBGR24
 * @param yuv420p ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @return ���ر�����bgr24����
 * errorCode:
*/
JNIEXPORT jbyteArray Java_com_face_sv_ImageUtilNative_encodeYuv420pToBGR(JNIEnv* env,jobject obj,jbyteArray yuv420p, jint width, jint height)
{
	//LOGD("encodeYuv420pToBGR() start");
	int ret = 0;
	jbyteArray retArr = NULL;
	unsigned char* yuv420 = (unsigned char*)env->GetByteArrayElements(yuv420p, 0);
	unsigned int   yuvLen = env->GetArrayLength(yuv420p);
	//LOGD("encodeYuv420pToBGR() yuvlen:%d, width:%d, height:%d", yuvLen, width, height);
    unsigned int size = width * height * 3;
    unsigned int mWidth = width;
    unsigned int mHeight = height;
    unsigned int bitsPerPixel = 24;
	unsigned char* bgr24 = (unsigned char*)malloc(size);
	if (bgr24 != NULL)
	{
		LOGD("encodeYuv420pToBGR() yuv420p_to_bgr(yuv420, yuvLen, bgr24, mWidth, mHeight, bitsPerPixel)");
		ret = NV12_To_RGB(mWidth, mHeight, yuv420, bgr24);
		//ret = NV12_To_RGB_Ext(mWidth, mHeight, yuv420, bgr24);
		LOGD("encodeYuv420pToBGR() NV12_To_RGB(mWidth, mHeight, yuv420, bgr24) ret:%d", ret);
		if (ret == 0) {
		    retArr = env->NewByteArray(size);
		    env->SetByteArrayRegion(retArr, 0, size, (jbyte*)bgr24);
		}
		free(bgr24);
	}
	//LOGD("encodeYuv420pToBGR() end");
	env->ReleaseByteArrayElements(yuv420p, (jbyte*)yuv420, 0);
    return retArr;
}

/**
 * BGR24תJPG
 * @param yuv420p ��Ҫ����������Ƭ�ĻҶ�ͼ����
 * @param width ͼƬ���
 * @param height ͼƬ�߶�
 * @param imgPath ͼƬ�洢��ַ
 * @return �ɹ�����0�� ʧ�ܷ��ظ�����
 * errorCode: -1 malloc fail; -2 jpg compress fail; -3 jpgBufLen is error; -4 open file fail; -5 file write fail.
 */
JNIEXPORT jint Java_com_face_sv_ImageUtilNative_encodeBGR24toJpg(JNIEnv* env,jobject obj,jbyteArray bgr24, jint width, jint height, jstring imgPath)
{
    int ret = -1;
    int fd = 0;
    int n = 0;
    jbyteArray retArr = NULL;
    unsigned char* rgbBuf = (unsigned char*)env->GetByteArrayElements(bgr24, 0);
    unsigned int   len = env->GetArrayLength(bgr24);
    const char* filePath = (char*)env->GetStringUTFChars(imgPath, 0);
    int sLen = env->GetStringUTFLength(imgPath);
    //LOGD("encodeBGR24toJpg() sLen:%d filePath %s", sLen, filePath);
    char* pJpgBuf = NULL;
    int size = width * height * 4;
    int*  jpgBufLen = &size;
    pJpgBuf = (char*)malloc(size);
	if(pJpgBuf != NULL)
	{
		reverseBGR2RGB(rgbBuf, width, height);
		ret = (jpeg_compress_ex(pJpgBuf, jpgBufLen, rgbBuf, width, height, (int)true, 90)==0);
		if(!ret) {
			LOGE("jpeg_compress_ex fail\n");
			ret  = -2;
		}
		if(ret && jpgBufLen <= 0) {
			LOGE("jpeg_compress_ex ok, but jpgBufLen:%d is error\n",*jpgBufLen);
            ret = -3;
        }
		else
		{
			//LOGD("encodeBGR24toJpg() filePath: %s", filePath);
			remove(filePath);

			fd = open(filePath, O_CREAT | O_WRONLY, 0777);
			if (fd <= 0)
			{
				LOGE("can not open the image file (%s)", filePath);
				ret = -4;
				goto error;
			}
			//LOGD("encodeBGR24toJpg() pJpgBuf jpgBufLen:%d", *jpgBufLen);
			n = write(fd, pJpgBuf, *jpgBufLen);
			//LOGD("write(featureData) ret:%d featureSize:%d", n, featureSize);
			if (n != *jpgBufLen)
			{
			     ret = -5;
				 LOGE("write feature file fail. (%s)", filePath);
				 goto error;
			}
			ret = 0;
			error:
			if (fd > 0)
			{
				close(fd);
				fd = 0;
			}

		}
		free(pJpgBuf);
	}
	else
	{
		ret = -1;
	}
	env->ReleaseByteArrayElements(bgr24, (jbyte*)rgbBuf, 0);
	env->ReleaseStringUTFChars(imgPath, filePath);
	return ret;
}


/**
 * JNI������
 */
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void *reserved) //����JNI_OnLoad�����������밴�������ķ�ʽ����
{
	LOGD("JNI_OnLoad()");
	JNIEnv* env = NULL; //ע��ʱ��JNIEnv��ʵ�ֵģ����Ա������Ȼ�ȡ��
	jint result = -1;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) //��JavaVM��ȡJNIEnv��һ��ʹ��1.4�İ汾
		return -1;

	jclass clazz;
	static const char* const kClassName = "com/face/sv/ImageUtilNative";

	clazz = env->FindClass(kClassName); //��������ҵ�Ҫע����࣬ǰ����������Ѿ����ص�java������С� ����˵������̬�����native��������֮�䣬û���κζ�Ӧ��ϵ��

	if (clazz == NULL) {
		LOGD("cannot get class:%s\n", kClassName);
		return -1;
	}

	return JNI_VERSION_1_4; //�������Ҫ�����뷵�ذ汾��������ػ�ʧ�ܡ�
}

#ifdef __cplusplus
}
#endif
