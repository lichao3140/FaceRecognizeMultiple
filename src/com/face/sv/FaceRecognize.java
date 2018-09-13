package com.face.sv;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * ����ʶ���㷨���������⡣
 * @author �޷�
 * @datetime 2018-02-05
 */
public class FaceRecognize {
    private final static String TAG = "FaceRecognize";
    private FaceRecognizeNative mRecognizeNative;
    private byte[] mutexFeature = new byte[0];
    private byte[] mutexCompare = new byte[0];
    
    public FaceRecognize() {
    	mRecognizeNative = FaceRecognizeNative.getInstance();
    }

    /**
     * ��ȡ�㷨����KEY
     *  @return 8�ֽ��ֽ�����;
     */
    public byte[] getFeatureSN() {
    	return mRecognizeNative.getFeatureSN();
    }

    /**
     *  ʹ��Dm2016���ܺ����Կ�����㷨��Ȩ
     * @sncode ���ܺ���ֽ�����
     * @return �ɹ�����1��ʧ�ܷ���0����
     */
    public int checkFeatureSN(byte[] sncode) {
    	return mRecognizeNative.checkFeatureSN(sncode);
    }
    
    /**
     * ��ʼ����������㷨��
     * @ libDir �㷨���·��
     * @ tempDir ��ʱĿ¼��ַ����ǰӦ�ñ���ӵ�в���Ȩ��
     * @ modelNum ÿ���û���ģ������
     * @return �ɹ�����ͨ���� > 0, ʧ�ܷ��� <=0 ;
     */
    public int initFaceLibrary(String libDir, String tempDir, String userDataPath, int modelNum) {
    	return mRecognizeNative.initFaceLibrary(libDir, tempDir, userDataPath, modelNum);
    }
    
    /**
     * �ͷ���������㷨��
     */
    public void releaseFaceLibrary() {
    	mRecognizeNative.releaseFaceLibrary();
    }
    
    /**
     * ��ȡ����ͼƬ������ģ��
     * @param BGR24 ����ͼƬ
     * @param faceData  ͼƬ����������Ϣ(FacePos.data), ���鳤��580(�ж�������� ��ȡ��Ҫ����������Ϣ)
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @return ����ģ�����ݣ��ɹ����鳤��2008�ֽ�, ʧ�����鳤��Ϊ1��byte[0]����0������
     * (-101��ʾ��������Ϊ��;-102��ʾmallocģ��ʧ��;103��ʾ�����faceInfo��Ϣ����;
     */
    public byte[] getFaceFeatureFromRGB(byte[] BGR24, byte[] faceData, int width, int height) {
        byte[] feature = null;
        if (faceData != null && faceData.length == FacePos.SIZE) {
            // ��ȡ����ģ��
            synchronized (mutexFeature) {
                 feature = mRecognizeNative.getFaceFeature(BGR24, faceData, width, height);
            }
        }
        return feature;
    }

    /**
     * ��ȡ����ͼƬ������ģ��
     * @param bmp ����ͼƬ
     * @param faceData  ͼƬ����������Ϣ(FacePos.data), ���鳤��580(�ж�������� ��ȡ��Ҫ����������Ϣ)
     * @return ����ģ�����ݣ��ɹ����鳤��2008�ֽ�, ʧ�����鳤��Ϊ1��byte[0]����0������
     * (-101��ʾ��������Ϊ��;-102��ʾmallocģ��ʧ��;103��ʾ�����faceInfo��Ϣ����;
     */
    public byte[] getFaceFeatureFromBitmap(Bitmap bmp, byte[] faceData) {
        log("getFaceFeatureFromBitmap(Bitmap bmp)");
        final int width = bmp.getWidth();
        final int height = bmp.getHeight();
        byte[] feature = null;
        if (faceData != null && faceData.length == FacePos.SIZE && mRecognizeNative != null) {
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

            // ��ȡ����ģ��
            synchronized (mutexFeature) {
                feature = mRecognizeNative.getFaceFeature(BGR24, faceData, width, height);
            }
        }
        return feature;
    }

    /**
     * �ȶ���������ģ�����ƶ�
     * @param bmp1 ����ͼƬ1
     * @param bmp2 ����ͼƬ2
     * @param faceInfo1  ͼƬ����������Ϣ(FacePos.data), ���鳤��580(�ж�������� ��ȡ��Ҫ����������Ϣ)
     * @param faceInfo2  ͼƬ����������Ϣ(FacePos.data), ���鳤��580(�ж�������� ��ȡ��Ҫ����������Ϣ)
     * @return ��ʶ�ȣ���ֵ��Χ0 ~ 100֮�䣩
     */
    public int compareFaces(Bitmap bmp1, byte[] faceData1, Bitmap bmp2, byte[] faceData2) {
        log("compareFaces(Bitmap bmp1, Bitmap bmp2)");
        int ret = -1;
        byte[] feature1 = null;
        byte[] feature2 = null;
        // ��ȡģ��
        feature1 = getFaceFeatureFromBitmap(bmp1, faceData1);
        // ��ȡģ��ɹ�
        if (feature1 != null && feature1.length > 1) {
            // ��ȡģ��
            feature2 = getFaceFeatureFromBitmap(bmp2, faceData2);
            if (feature2 != null && feature2.length > 1) {
                // �ȶ�����ģ�����ƶ�
                synchronized (mutexCompare) {
                    ret = mRecognizeNative.compareFeature(feature1, feature2);
                }
            } else {
                log("feature2 == null && feature2.length <= 1");
            }
        } else {
            log("feature1 == null && feature1.length <= 1");
        }
        return ret;
    }

    /**
     * �ȶ���������ģ�����ƶ�
     * @param feature1 ����ģ��
     * @param feature2 ����ģ��
     * @return ��ʶ�ȣ���ֵ��Χ0 ~ 100֮�䣩�� -1 ��ʾ����feature1��feature2Ϊnull.
     */
    public int compareFeatures(final byte[] feature1, final byte[] feature2) {
        log("compareFeatures(final float[] feature1, final float[] feature2)");
        int ret = -1;
        if (mRecognizeNative != null) {
            if (feature1 != null && feature1.length > 1) {
                if (feature2 != null && feature2.length > 1) {
                    // �ȶ�����ģ�����ƶ�
                    synchronized (mutexCompare) {
                        ret = mRecognizeNative.compareFeature(feature1, feature2);
                    }
                } else {
                    log("feature2 == null && feature2.length <= 1");
                }
            } else {
                log("feature1 == null && feature1.length <= 1");
            }
        }
        return ret;
    }

    /**
     * ע������ģ��
     * @ userId ע���û�ģ�ͱ��
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @facePos ��������õ�������Ϣ
     * @return ���ڵ���0ע��ɹ�����ʾע��ģ�ͱ�ţ�0-N),С��0��ʾʧ�ܡ�
     */
    public int registerFaceFeature(int userId, byte[] BGR24, int width, int height, byte[] facePos) {
    	return mRecognizeNative.registerFaceFeature(userId, BGR24, width, height, facePos);
    }

    /**
     * ɾ������ģ��
     * @ userId ע���û�ģ�ͱ��
     * @return 0Ϊ�ɹ���С��0 ��ʾʧ��
     */
    public int deleteFaceFeature(int userId) {
    	return mRecognizeNative.deleteFaceFeature(userId);
    }
    
    /**
     * �����������ģ��
     * @return 0Ϊ�ɹ���С��0 ��ʾʧ��
     */
    public int clearAllFaceFeature() {
    	return mRecognizeNative.clearAllFaceFeature();
    }

    /**
     * ע������ģ��
     * @ userId ע���û�ģ�ͱ��
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @facePos ��������õ�������Ϣ
     * @return ���ڵ���0���³ɹ�����ʾ����ģ�ͱ�ţ�0-N),С��0��ʾʧ�ܡ�
     */
    public int updateFaceFeature(int userId, byte[] BGR24, int width, int height, byte[] facePos) {
    	return mRecognizeNative.updateFaceFeature(userId, BGR24, width, height, facePos);
    }

    /**
     * ���¼�������ģ��
     * @ userId ע���û�ģ�ͱ��
     * @return 0Ϊ�ɹ���С��0 ��ʾʧ�ܡ�
     */
    public int reloadFaceFeature() {
    	return mRecognizeNative.reloadFaceFeature();
    }

    /**
     * ע������ģ��
     * @ userId ע���û�ģ�ͱ��
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @facePos ��������õ�������Ϣ
     * @return ���ڵ���0ʶ��ɹ�������ʶ�����ƶȣ�0-100),С��0��ʾʧ�ܡ�
     */
    public int recognizeFaceOne(int userId, byte[] BGR24, int width, int height, byte[] facePos) {
    	return mRecognizeNative.recognizeFaceOne(userId, BGR24, width, height, facePos);
    }

    
    /**
     * ע������ģ��
     * @param BGR24 ��Ҫ����������Ƭ�ĻҶ�ͼ����
     * @param width ͼƬ���
     * @param height ͼƬ�߶�
     * @facePos ��������õ�������Ϣ
     * @return int[0]���ڵ���0ע��ɹ�,�����û����,С��0��ʾʧ�ܡ�int[1]�ɹ�ʱ��ʾʶ�����
     */
    public int[] recognizeFaceMore(byte[] BGR24, int width, int height, byte[] facePos) {
    	return mRecognizeNative.recognizeFaceMore(BGR24, width, height, facePos);
    }
    
    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
