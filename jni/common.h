
#ifndef __YUV_COMMON__H_
#define __YUV_COMMON__H_

#include "FIStdDefEx.h"

#ifndef PACK_ALIGN
#define PACK_ALIGN	__attribute__((packed))
#endif

#define OFFSET_OF(type, member)   ((unsigned int)&((type *)0)->member)


typedef struct
{
	int width; 
	int height;
	int format;
	int bufLen;
	char *yuvBuf; 
}YUV_INFO;

typedef YUV_INFO YUV_SRC;

typedef struct
{
	  POINT startPoint;             //��ͼ����ʼ����
	  YUV_INFO yuvInfo;            //Ŀ��yuv��Ϣ
}DST_CUT_YUV;

typedef struct
{
	char *y;
	char *u;
	char *v;
}YUV_BUF;



#endif //yuvCommon.h





