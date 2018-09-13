package com.frm.cn;

public class FaceImage {
    private byte[] faceInfo;
    private byte[] bgr24;
    private int width;
    private int height;
    private long time = 0;
    
    public FaceImage(int width, int height) {
    	this.width = width;
    	this.height = height;
    }

    public byte[] getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(byte[] faceInfo) {
        this.faceInfo = faceInfo;
    }

	public byte[] getBgr24() {
		return bgr24;
	}
	public void setBgr24(byte[] bgr24) {
		this.bgr24 = bgr24;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	public void loadFromOther(FaceImage img) {
		if (img == null) {
			return;
		}
		this.faceInfo = img.getFaceInfo();
		this.bgr24 = img.getBgr24();
		this.width = img.getWidth();
		this.height = img.getHeight();
		this.time = img.getTime();
	}
}
