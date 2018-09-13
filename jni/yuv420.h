
#ifndef __YUV420P__H_
#define __YUV420P__H_

#include "common.h"
#include <stdio.h>
#include <stdlib.h>

#ifndef RGB16
#define RGB16 unsigned short
#endif
#ifndef MAKERGB565
#define MAKERGB565(r,g,b) (RGB16)( (((r)>>3)<<11) | (((g)>>2)<<5) | ((b)>>3) )
#endif

bool yuv420p_to_rgb(unsigned char *outRgbBuf, unsigned int outRgbLen, unsigned char *srcYuv420p, unsigned int width, unsigned int height, unsigned int bitsPerPixel, bool bUV=true);
bool Yuv420pToJpgBuf(char *pYuvBuf, int width, int height, char* pJpgBuf, int *jpgBufLen);

#if 0
bool cutYuv420pPic(YUV_SRC &src, DST_CUT_YUV &dst);
#else
bool cutYuv420pPic(YUV_SRC *src, DST_CUT_YUV *dst);
#endif
void Rgb24ToYuv420p(unsigned char *yuvDst, unsigned char *rgbSrc, unsigned int width, unsigned int height);

bool YUV420Pzoom_InOut(const unsigned char *ySrc, const unsigned char *uSrc,const unsigned char *vSrc,
                        unsigned char *yDst, unsigned char *uDst, unsigned char *vDst,
                        int src_w, int src_h, int rate, bool IsZoomOut);

int NV21_To_RGB(unsigned int width , unsigned int height , unsigned char *yuyv , unsigned char *rgb);

int NV12_To_RGB(unsigned int width , unsigned int height , unsigned char *yuyv , unsigned char *rgb);

int NV12_To_RGB_Ext(unsigned int width , unsigned int height , unsigned char *yuyv , unsigned char *rgb);

int jpeginit(int image_width,int image_height,int quality);



int rgb2jpeg(char * filename, unsigned char* rgbData);

int jpeguninit();

#endif //yuv420p.h


