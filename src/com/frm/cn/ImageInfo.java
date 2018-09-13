package com.frm.cn;

import java.nio.ByteBuffer;

import android.util.Log;

public class ImageInfo {
	private final static String TAG = ImageInfo.class.getSimpleName();
	private int size = 0;
	private int width;
	private int height;
    private byte[] data;
    private long   time;
    private boolean isNew;

    public ImageInfo(int width, int height) {
    	this.width = width;
    	this.height = height;
    	size = width * height * 3 / 2;
    	data = new byte[size];
    	isNew = false;
    }

    public int getWidth() {
    	return width;
    }
    
    public int getHeight() {
    	return height;
    }
    
	public byte[] getData() {
		return data;
	}
	public void setImage(byte[] imgData) {
		//log("ImageInfo.setImage(byte[] imgData)");
		System.arraycopy(imgData, 0, data, 0, imgData.length);
	}
	public void setImage(ByteBuffer buffer) {
		//log("ImageInfo.setImage(byte[] imgData)");
		buffer.get(data, 0, buffer.remaining());
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}

	public boolean isNew() {
///		Log.e("ImageInfo","isNew() isNew:" + isNew);
		return isNew;
	}

	public void setNew(boolean isNew) {
//		Log.e("ImageInfo","setNew() isNew:" + isNew);
		this.isNew = isNew;
	}
	
	public void loadFromOther(ImageInfo info) {
		if (info == null) {
			return;
		}
		this.data = info.getData();
		this.time = info.getTime();
		this.isNew = info.isNew();
	}

	// ¥Ú”°log
	public static void log(String msg) {
		Log.e(TAG, msg);
	}
}
