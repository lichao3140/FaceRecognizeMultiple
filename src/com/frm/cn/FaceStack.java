package com.frm.cn;

import android.graphics.Bitmap;
import android.util.Log;

public class FaceStack {
	private Object   mLock = new Object();
    private FaceImage faceOne = null;
    private FaceImage faceTwo = null;
    
    /**
     * 出堆
     * @return
     */
    public FaceImage pullFaceImage() {
    	//log("pullFaceImage() isReading:" + isReading);
    	faceTwo = null;
        synchronized (this) {
            //log("pullFaceImage() faceOne.isNew():" + faceOne.isNew());
        	if (faceOne != null && faceOne.getBgr24() != null) {
                faceTwo = faceOne;
                faceOne = null;
        	}
		}
        return faceTwo;
    }
    
    /**
     * 入堆
     * @param img
     */
    public void pushFaceImage(FaceImage img) {
    	//log("pushFaceImage() isReading:" + isReading);
    	synchronized (this) {
            if (faceOne != null) {
            	faceOne.setBgr24(null);
            	faceOne.setFaceInfo(null);
                faceOne = null;
            }
        	faceOne = img;
        }
    }
 
    /**
     * 清空数据
     */
    public void clearAll() {
        Bitmap bmp = null;
        if (faceOne != null) {
        	faceOne.setBgr24(null);
        	faceOne.setFaceInfo(null);
            faceOne = null;
        }
        if (faceTwo != null) {
        	faceTwo.setBgr24(null);
        	faceTwo.setFaceInfo(null);
            faceTwo = null;;
        }
        
    }
    
    private void log(String msg) {
    	Log.d("FaceStack", msg);
    }
}
