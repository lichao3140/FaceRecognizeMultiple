#ifndef _FI_STD_DEF_EX_H_
#define _FI_STD_DEF_EX_H_

#ifndef WIN32

typedef struct tagPOINT
{
	int x, y;
}POINT;

typedef struct tagSIZE
{
	int cx, cy;
}SIZE;

typedef struct tagRECT
{
	int left, top, right, bottom;
}RECT;

typedef unsigned char BYTE;
typedef unsigned short WORD;
typedef unsigned int DWORD;

#endif


/*
typedef struct tagPointF {
	float x;
	float y;
} TPointF;
*/
#endif // _FI_STD_DEF_EX_H_
