package com.face.sv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FaceInfo {
    private int ret;
    private FacePos[] facePos;
    
    public FaceInfo() {
    	ret = 0;
    	facePos = null;
    }
	
	public void parseFromByteArray(byte[] data) {
		if (data != null && data.length >= 4) {
			ByteBuffer buf = ByteBuffer.wrap(data);
			buf.order(ByteOrder.nativeOrder());
			ret = buf.getInt();
			int length  = data.length;
			if (length >= 4 + FacePos.SIZE) {
			    int size = (data.length - 4) / FacePos.SIZE;
			    facePos = new FacePos[size];
			    byte[] bts;
			    for (int i = 0; i < size; i++) {
			    	bts = new byte[FacePos.SIZE];
			    	buf.get(bts);
			    	facePos[i] = new FacePos();
				    facePos[i].praseFromByteArray(bts);
			    }
			}
		} else {
			ret = 0;
		    facePos = null;
		}
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public FacePos[] getFacePos() {
		return facePos;
	}
	
	public FacePos getFacePos(int index) {
		return facePos[index];
	}
	
	public byte[] getFacePosData(int index) {
		FacePos pos = facePos[index];
		if (pos != null) {
	        return pos.getData();
		} else {
			return null;
		}
	}
}
