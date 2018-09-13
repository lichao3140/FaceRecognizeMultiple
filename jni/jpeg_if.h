
#ifndef __JPEG_IF__
#define __JPEG_IF__

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

int jpeg_compress(char* pFileName, unsigned char* pData, int nWidth, int nHeight, int bColor, int nQuality);
int jpeg_decompress(char* pFileName, char* pBuffer, int* pnWidth, int* pnHeight, int* pnComponents, int nDepth, int bYuvMode);
int jpeg_compress_ex(char* pJpgBuf, int *jpgBufLen, unsigned char* pData, int nWidth, int nHeight, int bColor, int nQuality);
int jpeg_decompress_ex(char* indata, int iSize, char* pBuffer, int* pnWidth, int* pnHeight, int* pnComponents, int nDepth, int bYuvMode);

#ifdef __cplusplus
}
#endif // __cplusplus

#endif // __JPEG_IF__

