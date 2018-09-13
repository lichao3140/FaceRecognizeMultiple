package com.face.sv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.Point;
import android.graphics.Rect;

public class FacePos {
/**
 * struct THFI_FacePos
{
    RECT		rcFace;//coordinate of face
   	POINT		ptLeftEye;//coordinate of left eye
	POINT		ptRightEye;//coordinate of right eye
	POINT		ptMouth;//coordinate of mouth
	POINT		ptNose;//coordinate of nose								
	FaceAngle	fAngle;//value of face angle
	int			nQuality;//quality of face(from 0 to 100)
	BYTE   		pFacialData[512];//facial data
	THFI_FacePos()
	{
		memset(&rcFace,0,sizeof(RECT));
		memset(&ptLeftEye,0,sizeof(POINT));
		memset(&ptRightEye,0,sizeof(POINT));
		memset(&ptMouth,0,sizeof(POINT));
		memset(&ptNose,0,sizeof(POINT));
		memset(&fAngle,0,sizeof(FaceAngle));
		nQuality=0;
		memset(pFacialData, 0, 512);
	}
};
 */
	public final static int SIZE = 580;
	private byte[] data;
    private Rect face = new Rect();
    private Point lEye = new Point();
    private Point rEye = new Point();
    private Point mouth = new Point();
    private Point nose = new Point();
    private FaceAngle angle = new FaceAngle();
    private int quality;

    public FacePos() {
    	
    }

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Rect getFace() {
		return face;
	}

	public void setFace(Rect face) {
		this.face = face;
	}

	public Point getlEye() {
		return lEye;
	}

	public void setlEye(Point lEye) {
		this.lEye = lEye;
	}

	public Point getrEye() {
		return rEye;
	}

	public void setrEye(Point rEye) {
		this.rEye = rEye;
	}

	public Point getMouth() {
		return mouth;
	}

	public void setMouth(Point mouth) {
		this.mouth = mouth;
	}

	public Point getNose() {
		return nose;
	}

	public void setNose(Point nose) {
		this.nose = nose;
	}

	public FaceAngle getAngle() {
		return angle;
	}

	public void setAngle(FaceAngle angle) {
		this.angle = angle;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}
    
	public boolean praseFromByteArray(byte[] bArr) {
		if (null != bArr && SIZE == bArr.length) {
			data = bArr;
			ByteBuffer buf = ByteBuffer.wrap(bArr);
			buf.order(ByteOrder.nativeOrder());
			face.left = buf.getInt();
			face.top = buf.getInt();
			face.right = buf.getInt();
			face.bottom = buf.getInt();
			lEye.x = buf.getInt();
			lEye.y = buf.getInt();
			rEye.x = buf.getInt();
			rEye.y = buf.getInt();
			mouth.x = buf.getInt();
			mouth.y = buf.getInt();
			nose.x = buf.getInt();
			nose.y = buf.getInt();
			angle.setFaceAngle(buf.getInt(), buf.getInt(), buf.getInt(), buf.getFloat());
			quality = buf.getInt();
			return true;
		} else {
			data = null;
			return false;
		}
	}
}
