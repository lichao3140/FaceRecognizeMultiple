#include <stdio.h>
#include <fcntl.h>
#include <sys/stat.h>

//#include "debug.h"
#include "yuv420.h"
#include "malloc.h"

bool yuv420p_to_rgb(unsigned char *outRgbBuf, unsigned int outRgbLen, unsigned char *srcYuv420p, unsigned int width, unsigned int height, unsigned int bitsPerPixel, bool bUV)
{
	unsigned int line, col, half_width;
	int y=0x10, u=0x80, v=0x80, yy, uu, vv, vr=0, ug=0, vg=0, ub=0;
	int r, g, b;
	unsigned char *ySrc, *uSrc, *vSrc;
	unsigned char* pDst;
	RGB16* pDst16;

	if(!srcYuv420p || !width || !height || !outRgbBuf || !outRgbLen) 
		return false;

	
	unsigned int needOutSize = width * height * (bitsPerPixel/8);

	if(outRgbLen < needOutSize)
		return false;

	half_width=width>>1;

	ySrc = srcYuv420p;
	uSrc = ySrc + width * height;
	vSrc = uSrc + width * height/4;

	pDst = outRgbBuf;

	for (line = 0; line < height; line++) 
	{
		for (col = 0; col < width; col++) 
		{
			y = *ySrc++;
			if(bUV && !(col%2)) 
			{
				u = *uSrc++;
				v = *vSrc++;
			}
#if 1
			if(y<16) 
				y=16;
			else if(y>235) 
				y=235;
#endif
			yy = y<<8;
			if(!(col%2)) 
			{
				uu = u-128;
				ug = 88 * uu;
				ub = 454 * uu;
				vv = v - 128;
				vg = 183 * vv;
				vr = 359 * vv;
			}
			
			r = ( yy + vr ) >> 8;
			g = ( yy - ug - vg ) >> 8;
			b = ( yy + ub ) >> 8;

			if(r<0) r=0;
			else if(r>0xff) r=0xff;
			if(g<0) g=0;
			else if(g>0xff) g=0xff;
			if(b<0) b=0;
			else if(b>0xff) b=0xff;

			if(bitsPerPixel==16) 
				*pDst16++ = MAKERGB565(r, g, b);
			else if(bitsPerPixel==24 || bitsPerPixel==32)
			{
#if 0  // bgr
				*pDst++=b;   
				*pDst++=g;
				*pDst++=r;
#else  // rgb
				*pDst++=r;
				*pDst++=g;
				*pDst++=b;
#endif

				if(bitsPerPixel==32) 
					*pDst++=0;
			} 
			else 
				*pDst++ = 0;
		}
		if(bUV && !(line%2)) 
		{
			uSrc -= half_width;
			vSrc -= half_width;
		}
	}
	return true;
}
/*
bool Yuv420pToJpgBuf(char *pYuv420PBuf, int width, int height, char *pJpgBuf, int *jpgBufLen)
{
	bool rc = false;

	int bitsPerPixel = 24;
	int rgbLen = width * height * bitsPerPixel / 8;
#ifndef MEM_TRACE
	unsigned char *rgbBuf = (unsigned char *)malloc(rgbLen);
#else
	unsigned char *rgbBuf = NULL;
	Malloc(rgbBuf, rgbLen);
#endif
	if(!rgbBuf)
	{
		printf("rgbBuf == NULL, out of mem\n");
		return false;
	}

	if(yuv420p_to_rgb(rgbBuf, rgbLen, (unsigned char *)pYuv420PBuf, width, height, bitsPerPixel))
	{			
		rc = (jpeg_compress_ex(pJpgBuf, jpgBufLen, rgbBuf, width, height, true, 90)==0);
		
		if(!rc)
			printf("jpeg_compress_ex fail\n");
		if(rc && jpgBufLen <= 0)
			printf("jpeg_compress_ex ok, but jpgBufLen:%d is error\n",*jpgBufLen);
	}
	free(rgbBuf);
	
	return rc;
}
*/

#if 1
//调用此函数会造成内存泄露，原因不明

bool cutYuv420pPic(YUV_SRC *src, DST_CUT_YUV *dst)
{
	if(dst->startPoint.x >= src->width || dst->startPoint.y >= src->height
	|| !dst->yuvInfo.yuvBuf || !src->yuvBuf)
	{
		printf("param is invalid\n");
		return false;
	}
	if(dst->startPoint.x < 0)
		dst->startPoint.x = 0;
	if(dst->startPoint.y < 0)
		dst->startPoint.y = 0;
	
	if(dst->startPoint.x + dst->yuvInfo.width > src->width)
	{
		 dst->yuvInfo.width = src->width - dst->startPoint.x;
	}
	if(dst->startPoint.y + dst->yuvInfo.height> src->height)
	{
		 dst->yuvInfo.height = src->height - dst->startPoint.y;
	}
	if(dst->yuvInfo.width%2)
		dst->yuvInfo.width -= 1;
	if(dst->yuvInfo.height%2)
		dst->yuvInfo.height -= 1;

	int dstYuvLen = (dst->yuvInfo.width * dst->yuvInfo.height * 3) >> 1; 
	if(dstYuvLen > dst->yuvInfo.bufLen)
	{
		printf("dstYuvLen:%d > dst->yuvInfo.bufLen:%d\n", dstYuvLen, dst->yuvInfo.bufLen);
		return false;
	}
	dst->yuvInfo.bufLen = dstYuvLen;

	char *ySrc = src->yuvBuf;
	char *uSrc = ySrc + src->width * src->height;
	char *vSrc = uSrc + (src->width>>1) * (src->height>>1);

	char *yuvSrc = ySrc + dst->startPoint.y * src->width + dst->startPoint.x;
	char *yuvDst = dst->yuvInfo.yuvBuf;
	// copy y
	for(int i = 0; i < dst->yuvInfo.height; i++)
	{
		memcpy(yuvDst, yuvSrc, dst->yuvInfo.width);
		yuvSrc += src->width;
		yuvDst += dst->yuvInfo.width;
	}
	// copy u
	yuvSrc = uSrc + dst->startPoint.y/2 * src->width/2 + dst->startPoint.x/2;;
	for(int i = 0; i < dst->yuvInfo.height/2; i++)
	{
		memcpy(yuvDst, yuvSrc, dst->yuvInfo.width/2);
		yuvSrc += src->width/2;
		yuvDst += dst->yuvInfo.width/2;
	}
	// copy v
	yuvSrc = vSrc + dst->startPoint.y/2 * src->width/2 + dst->startPoint.x/2;
	for(int i = 0; i < dst->yuvInfo.height/2; i++)
	{
		memcpy(yuvDst, yuvSrc, dst->yuvInfo.width/2);
		yuvSrc += src->width/2;
		yuvDst += dst->yuvInfo.width/2;
	}
	return true;
}


#else
//调用此函数会造成内存泄露，原因不明

bool cutYuv420pPic(YUV_SRC &src, DST_CUT_YUV &dst)
{
	if(dst.startPoint.x >= src.width || dst.startPoint.y >= src.height
	|| !dst.yuvInfo.yuvBuf || !src.yuvBuf)
	{
		printf("param is invalid\n");
		return false;
	}
	if(dst.startPoint.x < 0)
		dst.startPoint.x = 0;
	if(dst.startPoint.y < 0)
		dst.startPoint.y = 0;
	
	if(dst.startPoint.x + dst.yuvInfo.width > src.width)
	{
		 dst.yuvInfo.width = src.width - dst.startPoint.x;
	}
	if(dst.startPoint.y + dst.yuvInfo.height> src.height)
	{
		 dst.yuvInfo.height = src.height - dst.startPoint.y;
	}
	if(dst.yuvInfo.width%2)
		dst.yuvInfo.width -= 1;
	if(dst.yuvInfo.height%2)
		dst.yuvInfo.height -= 1;

	int dstYuvLen = (dst.yuvInfo.width * dst.yuvInfo.height * 3) >> 1; 
	if(dstYuvLen > dst.yuvInfo.bufLen)
	{
		printf("dstYuvLen:%d > dst.yuvInfo.bufLen:%d\n", dstYuvLen, dst.yuvInfo.bufLen);
		return false;
	}
	dst.yuvInfo.bufLen = dstYuvLen;

	char *ySrc = src.yuvBuf;
	char *uSrc = ySrc + src.width * src.height;
	char *vSrc = uSrc + (src.width>>1) * (src.height>>1);

	char *yuvSrc = ySrc + dst.startPoint.y * src.width + dst.startPoint.x;
	char *yuvDst = dst.yuvInfo.yuvBuf;
	// copy y
	for(int i = 0; i < dst.yuvInfo.height; i++)
	{
		memcpy(yuvDst, yuvSrc, dst.yuvInfo.width);
		yuvSrc += src.width;
		yuvDst += dst.yuvInfo.width;
	}
	// copy u
	yuvSrc = uSrc + dst.startPoint.y/2 * src.width/2 + dst.startPoint.x/2;;
	for(int i = 0; i < dst.yuvInfo.height/2; i++)
	{
		memcpy(yuvDst, yuvSrc, dst.yuvInfo.width/2);
		yuvSrc += src.width/2;
		yuvDst += dst.yuvInfo.width/2;
	}
	// copy v
	yuvSrc = vSrc + dst.startPoint.y/2 * src.width/2 + dst.startPoint.x/2;
	for(int i = 0; i < dst.yuvInfo.height/2; i++)
	{
		memcpy(yuvDst, yuvSrc, dst.yuvInfo.width/2);
		yuvSrc += src.width/2;
		yuvDst += dst.yuvInfo.width/2;
	}
	return true;
}
#endif

#define MY(a,b,c) (( a*  0.2989  + b*  0.5866  + c*  0.1145))
#define MU(a,b,c) (( a*(-0.1688) + b*(-0.3312) + c*  0.5000 + 128))
#define MV(a,b,c) (( a*  0.5000  + b*(-0.4184) + c*(-0.0816) + 128))

#define DY(a,b,c) (MY(a,b,c) > 255 ? 255 : (MY(a,b,c) < 0 ? 0 : MY(a,b,c)))
#define DU(a,b,c) (MU(a,b,c) > 255 ? 255 : (MU(a,b,c) < 0 ? 0 : MU(a,b,c)))
#define DV(a,b,c) (MV(a,b,c) > 255 ? 255 : (MV(a,b,c) < 0 ? 0 : MV(a,b,c)))

// cost time 400ms per frame when solution is 640*480 
void Rgb24ToYuv420p(unsigned char *yuvDst, unsigned char *rgbSrc, unsigned int width, unsigned int height)
{

	unsigned int i,x,y,j;
	unsigned char *Y = NULL;
	unsigned char *U = NULL;
	unsigned char *V = NULL;
	char temp;

	unsigned char *tmp_buf;
	int line_width = width * 3;
#ifndef MEM_TRACE
	tmp_buf = (unsigned char *)malloc(line_width);
#else
	 Malloc(tmp_buf, line_width);
#endif
#if 0
	for(i = 0, j = height - 1; i < j; i++, j--){
		memcpy(tmp_buf, rgbSrc + i * line_width, line_width);
		memcpy(rgbSrc + i * line_width, rgbSrc + j * line_width, line_width);
		memcpy(rgbSrc + j * line_width, tmp_buf, line_width);
	}
#endif

	for(i=0; (unsigned int)i < width*height*3; i+=3)
	{
		temp = rgbSrc[i];
		rgbSrc[i] = rgbSrc[i+2];
		rgbSrc[i+2] = temp;
	}
	i = j = 0;

	Y = yuvDst;
	U = yuvDst + width*height;
	V = U + ((width*height)>>2);

	for(y=0; y < height; y++)
		for(x=0; x < width; x++)
		{
			j = y*width + x;
			i = j*3;
			Y[j] = (unsigned char)(DY(rgbSrc[i], rgbSrc[i+1], rgbSrc[i+2]));

			if(x%2 == 1 && y%2 == 1)
			{
				j = (width>>1) * (y>>1) + (x>>1);

				U[j] = (unsigned char)
					((DU(rgbSrc[i  ], rgbSrc[i+1], rgbSrc[i+2]) +
					  DU(rgbSrc[i-3], rgbSrc[i-2], rgbSrc[i-1]) +
					  DU(rgbSrc[i  -width*3], rgbSrc[i+1-width*3], rgbSrc[i+2-width*3]) +
					  DU(rgbSrc[i-3-width*3], rgbSrc[i-2-width*3], rgbSrc[i-1-width*3]))/4);

				V[j] = (unsigned char)
					((DV(rgbSrc[i  ], rgbSrc[i+1], rgbSrc[i+2]) +
					  DV(rgbSrc[i-3], rgbSrc[i-2], rgbSrc[i-1]) +
					  DV(rgbSrc[i  -width*3], rgbSrc[i+1-width*3], rgbSrc[i+2-width*3]) +
					  DV(rgbSrc[i-3-width*3], rgbSrc[i-2-width*3], rgbSrc[i-1-width*3]))/4);
			}

		}
	free(tmp_buf);
}


// @param IsZoomOut:  true - 缩小  false - 放大
// @param rate [1 - n]

bool YUV420Pzoom_InOut(const unsigned char *ySrc, const unsigned char *uSrc,const unsigned char *vSrc,
                        unsigned char *yDst, unsigned char *uDst, unsigned char *vDst,
                        int src_w, int src_h, int rate, bool IsZoomOut)
{
	if(!ySrc || !uSrc || !vSrc || !yDst || !uDst || !vDst)
	{
		printf("invalid param \n");
		return false;
	}

    int dest_w,dest_h;
    int i , j;

    if(IsZoomOut)  // 缩小
	{
        dest_w = src_w/rate;
        dest_h = src_h/rate;
        for (j=0; j<dest_h; j++)
        {
            int dstLine = j*dest_w;
            int srcLine = j*rate*src_w;
            for(i=0; i<dest_w; i++)
            {
               *(yDst+dstLine+i) = *(ySrc+srcLine+i*rate) ;
            }
        }
        for (j=0; j<(dest_h>>1); j++)
        {
            int dstLine = j*(dest_w>>1);
            int srcLine = j*rate*(src_w>>1);
            for(i=0; i<(dest_w>>1); i++)
            {
               *(uDst+dstLine+i) = *(uSrc+srcLine+i*rate) ;
               *(vDst+dstLine+i) = *(vSrc+srcLine+i*rate) ;
            }
        }
    }
    else  // 放大
	{
        dest_w = src_w*rate;
        dest_h = src_h*rate;
        for (j=0; j<dest_h; j++)
        {
            int dstLine = j*dest_w;
            int srcLine = j/rate*src_w;
            for(i=0; i<dest_w; i++)
            {
               *(yDst+dstLine+i) = *(ySrc+srcLine+i/rate) ;
            }
        }
        for (j=0; j<(dest_h>>1); j++)
        {
            int dstLine = j*(dest_w>>1);
            int srcLine = j/rate*(src_w>>1);
            for(i=0; i<(dest_w>>1); i++)
            {
               *(uDst+dstLine+i) = *(uSrc+srcLine+i/rate) ;
               *(vDst+dstLine+i) = *(vSrc+srcLine+i/rate) ;
            }
        }
    }
    return true;
}

int NV21_To_RGB(unsigned int width , unsigned int height , unsigned char *yuyv , unsigned char *rgb)
{
const int nv_start = width * height ;
    unsigned int  i, j, index = 0, rgb_index = 0;
    BYTE y, u, v;
    int r, g, b, nv_index = 0;


    for(i = 0; i <  height ; i++)
    {
        for(j = 0; j < width; j ++){
            //nv_index = (rgb_index / 2 - width / 2 * ((i + 1) / 2)) * 2;
            nv_index = i / 2  * width + j - j % 2;

            y = yuyv[rgb_index];
            v = yuyv[nv_start + nv_index ];
            u = yuyv[nv_start + nv_index + 1];


            r = y + (140 * (v-128))/100;  //r
            g = y - (34 * (u-128))/100 - (71 * (v-128))/100; //g
            b = y + (177 * (u-128))/100; //b

            if(r > 255)   r = 255;
            if(g > 255)   g = 255;
            if(b > 255)   b = 255;
            if(r < 0)     r = 0;
            if(g < 0)     g = 0;
            if(b < 0)     b = 0;

            index = rgb_index % width + (height - i - 1) * width;
            rgb[index * 3+0] = b;
            rgb[index * 3+1] = g;
            rgb[index * 3+2] = r;
            rgb_index++;
        }
    }
    i++;
    j++;
    if ((i== height) && (j = width)) {
        return 0;
    } else {
    	return -1;
    }
}

int NV12_To_RGB(unsigned int width , unsigned int height , unsigned char *yuyv , unsigned char *rgb)
{
const int nv_start = width * height ;
    unsigned int  i, j, index = 0, rgb_index = 0;
    int y=0x10, u=0x80, v=0x80, yy, uu, vv, vr=0, ug=0, vg=0, ub=0;
    int r, g, b, nv_index = 0;


    for(i = 0; i <  height ; i++)
    {
        for(j = 0; j < width; j ++){
            //nv_index = (rgb_index / 2 - width / 2 * ((i + 1) / 2)) * 2;
            nv_index = i / 2  * width + j - j % 2;

            y = yuyv[rgb_index];
            u = yuyv[nv_start + nv_index ];
            v = yuyv[nv_start + nv_index + 1];


#if 1
			if(y<16)
				y=16;
			else if(y>235)
				y=235;
#endif
			yy = y<<8;
			if(!(width%2))
			{
				uu = u-128;
				ug = 88 * uu;
				ub = 454 * uu;
				vv = v - 128;
				vg = 183 * vv;
				vr = 359 * vv;
			}

			r = ( yy + vr ) >> 8;
			g = ( yy - ug - vg ) >> 8;
			b = ( yy + ub ) >> 8;

            if(r > 255)   r = 255;
            if(g > 255)   g = 255;
            if(b > 255)   b = 255;
            if(r < 0)     r = 0;
            if(g < 0)     g = 0;
            if(b < 0)     b = 0;

//            index = rgb_index % width + (height - i - 1) * width;
#if 0
            rgb[rgb_index * 3+0] = b;
            rgb[rgb_index * 3+1] = g;
            rgb[rgb_index * 3+2] = r;
#else
            rgb[rgb_index * 3+0] = r;
            rgb[rgb_index * 3+1] = g;
            rgb[rgb_index * 3+2] = b;
#endif
            rgb_index++;
        }
    }
    return 0;
}
