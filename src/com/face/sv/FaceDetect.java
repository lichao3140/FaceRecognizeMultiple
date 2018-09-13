package com.face.sv;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * ����ʶ���㷨���������⡣
 * @author �޷�
 * @datetime 2016-05-03
 */
public class FaceDetect {
    private final static String TAG = "FaceDetect";
    private FaceDetectNative mDetectNative;
    private byte[]  mutexFace = new byte[0];
    
    public FaceDetect() {
        mDetectNative = FaceDetectNative.getInstance();
        //mDetectNative = new FaceDetectNative();
    }
    
    /**
     * ��ȡ�㷨����KEY
     *  @return 8�ֽ��ֽ�����;
     */
    public byte[] getDetectSN() {
    	return mDetectNative.getDetectSN();
    }

    /**
     *  ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
     * @sncode ���ܺ���ֽ�����
     * @return �ɹ�����1��ʧ�ܷ���0����
     */
    public int checkDetectSN(byte[] sncode) {
    	return mDetectNative.checkDetectSN(sncode);
    }

    /**
     * ��ʼ����������㷨��
     * @ libDir �㷨���·��
     * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
     * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
     */
    public int initFaceDetectLib(String libDir, String tempDir) {
        int ret = mDetectNative.InitFaceDetect(libDir, tempDir);
        return ret;
    }

    /**
     * �ͷ���������㷨��
     */
    public void releaseFaceDetectLib() {
        mDetectNative.ReleaseFaceDetect();
    }

    /**
     * ��ȡRGB24ͼƬ������������Ϣ
     * ���������Ϣ
     * @param rgb24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param padding ���߽�
     * @return ����������Ϣ��FaceInfo��ʧ��FacePos=null ret= 0��
     */
    public FaceInfo getFacePositionFromGray(byte[] rgb24, int width, int height, int padding) {
        log("getFacePositionFromGray(byte[] gray, int width, int height)");
        FaceInfo faceInfo = new FaceInfo();
        if (rgb24 == null || rgb24.length == 0) {
        	return faceInfo;
        }
        byte[] value = null;
        synchronized (mutexFace) {
            value = mDetectNative.getFacePosition(rgb24, width, height, padding);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }

    /**
     * ��ȡͼƬ��������Ϣ
     * @param bmp ��Ҫ����������Ƭ
     * @param padding ���߽�
     * @return ����������Ϣ��FaceInfo retΪ������ FacePosΪ������Ϣ��ʧ��FacePos=null ret= 0��
     */
    public FaceInfo getFacePositionFromBitmap(Bitmap bmp, int padding) {
        log("getFacePositionFromBitmap(Bitmap bmp)");
        FaceInfo faceInfo = new FaceInfo();
        if (bmp == null) {
        	return faceInfo;
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte[] value = null;
        if (mDetectNative != null) {
            int[] pixels = new int[width * height];
            // ��ȡRGB32����
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            byte[] BGR24 = new byte[width * height * 3];
            //byte[] gray = new byte[width * height];
            // ��ȡͼƬ��RGB24���ݺͻҶ�ͼ����
            for (int i = 0; i < width * height; i++) {
                int r = (pixels[i] >> 16) & 0x000000FF;
                int g = (pixels[i] >> 8) & 0x000000FF;
                int b = pixels[i] & 0x000000FF;
                BGR24[i * 3] = (byte)(b & 0xFF);
                BGR24[i * 3 + 1] = (byte)(g & 0xFF);
                BGR24[i * 3 + 2] = (byte)(r & 0xFF);
                //gray[i] = (byte) ((306 * r + 601 * g + 117 * b) >> 10);
            }
            synchronized (mutexFace) {
                value = mDetectNative.getFacePosition(BGR24, width, height, padding);
            }
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }
    
    /**
     * ��ȡRGB24ͼƬ������������Ϣ(ѹ��ͼ��360��Ⱥ���)
     * ���������Ϣ
     * @param bgr24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param padding ���߽�
     * @return ����������Ϣ��FaceInfo��ʧ��FacePos=null ret= 0��
     */
    public FaceInfo getFacePositionScaleFromGray(byte[] bgr24, int width, int height, int padding) {
        log("getFacePositionFromGray(byte[] gray, int width, int height)");
        FaceInfo faceInfo = new FaceInfo();
        if (bgr24 == null || bgr24.length == 0) {
        	return faceInfo;
        }
        byte[] value = null;
        synchronized (mutexFace) {
            value = mDetectNative.getFacePositionScale(bgr24, width, height, padding);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }

    /**
     * ��ȡͼƬ��������Ϣ(ѹ��ͼ��360��Ⱥ���)
     * @param bmp ��Ҫ����������Ƭ
     * @param padding ���߽�
     * @return ����������Ϣ��FaceInfo retΪ������ FacePosΪ������Ϣ��ʧ��FacePos=null ret= 0��
     */
    public FaceInfo getFacePositionScaleFromBitmap(Bitmap bmp, int padding) {
        log("getFacePositionFromBitmap(Bitmap bmp)");
        FaceInfo faceInfo = new FaceInfo();
        if (bmp == null) {
        	return faceInfo;
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte[] value = null;
        if (mDetectNative != null) {
            int[] pixels = new int[width * height];
            // ��ȡRGB32����
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            byte[] BGR24 = new byte[width * height * 3];
            //byte[] gray = new byte[width * height];
            // ��ȡͼƬ��RGB24���ݺͻҶ�ͼ����
            for (int i = 0; i < width * height; i++) {
                int r = (pixels[i] >> 16) & 0x000000FF;
                int g = (pixels[i] >> 8) & 0x000000FF;
                int b = pixels[i] & 0x000000FF;
                BGR24[i * 3] = (byte)(b & 0xFF);
                BGR24[i * 3 + 1] = (byte)(g & 0xFF);
                BGR24[i * 3 + 2] = (byte)(r & 0xFF);
                //gray[i] = (byte) ((306 * r + 601 * g + 117 * b) >> 10);
            }
            synchronized (mutexFace) {
                value = mDetectNative.getFacePositionScale(BGR24, width, height, padding);
            }
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }
    

    /**
     * ���������Ϣ
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param type   �������(0��ʾʶ��1��ʾע��)
     * @paran padding ���߾�(����ʶ�������������ͼ����ߴ��ڵ���padding���������Ч��������)
     * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
     * errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
     */
    public FaceInfo faceDetectMaster(byte[] BGR24, int width, int height, int type, int padding) {
        //log("faceDetect(byte[] gray, int width, int height)");
        byte[] value = null;
        FaceInfo faceInfo = new FaceInfo();
        if (BGR24 == null) {
        	return faceInfo;
        }
        synchronized (mutexFace) {
            value = mDetectNative.faceDetectMaster(BGR24, width, height, type, padding);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }
    
    /**
     * ���������Ϣ�����ʱ�㷨�Զ����ŵ����320�����м�⣩
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @param type   �������(0��ʾʶ��1��ʾע��)
     * @paran padding ���߾�(����ʶ�������������ͼ����ߴ��ڵ���padding���������Ч��������)
     * @return ����������Ϣ�����鳤��4(�����(int)��ʧ�ܻ�������)��(4 + n*580)(����� + n*����������Ϣ)
     * errorCode: -101 Ϊ�㷨��ʼ��ʧ�ܣ� -102 Ϊ��⵽�������߽��Ƕ��ж�Ϊ��Ч�� -103 Ϊû�м�⵽������
     */
    public FaceInfo faceDetectScale(byte[] BGR24, int width, int height, int type, int padding) {
        //log("faceDetect(byte[] gray, int width, int height)");
        byte[] value = null;
        FaceInfo faceInfo = new FaceInfo();
        if (BGR24 == null) {
        	return faceInfo;
        }
        synchronized (mutexFace) {
            value = mDetectNative.faceDetectScale(BGR24, width, height, type, padding);
        }
        faceInfo.parseFromByteArray(value);
        return faceInfo;
    }
    

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
