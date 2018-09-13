package com.frm.cn;

import java.nio.ByteBuffer;

import android.util.Log;

public class ImageStack {
	private final static String TAG = ImageStack.class.getSimpleName();
	private boolean   isReading = false;
    private ImageInfo imageOne = null;
    private ImageInfo imageTwo = null;
    
    public ImageStack(int width, int height) {
    	imageOne = new ImageInfo(width, height);
    	imageTwo = new ImageInfo(width, height);
    }
    
    /**
     * ����
     * @return
     */
    public ImageInfo pullImageInfo() {
    	//log("pullImageInfo() isReading:" + isReading);
    	isReading = true;
        log("pullImageInfo() imageOne.isNew:" + imageOne.isNew());
        if (imageOne.isNew()) {
        	imageTwo.setImage(imageOne.getData());
        	imageTwo.setTime(imageOne.getTime());
        	imageTwo.setNew(true);
        	imageOne.setNew(false);
        } else {
        	imageTwo.setNew(false);
        }
        isReading = false;
        return imageTwo;
    }

    /**
     * ���
     * @param img
     */
    public void pushImageInfo(byte[] imgData, long time) {
    	log("pushImageInfo(byte[]) isReading:" + isReading);
        if (!isReading) {
        	imageOne.setImage(imgData);
        	imageOne.setTime(time);
        	imageOne.setNew(true);
        }
    }
    
    /**
     * ���
     * @param img
     */
    public void pushImageInfo(ByteBuffer buffer, long time) {
    	//log("pushImageInfo(ByteBuffer) isReading:" + isReading);
    	if (!isReading) {
        	imageOne.setImage(buffer);
        	imageOne.setTime(time);
        	imageOne.setNew(true);
        	//log("pullImageInfo() imageOne.setNew(true)");
        }
    }
    
    public void clearAll() {
    	imageOne.setNew(false);
    	imageTwo.setNew(false);
    }
    
	// ��ӡlog
	public static void log(String msg) {
		Log.e(TAG, msg);
	}
}
