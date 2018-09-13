package com.face.sv;

public class FaceAngle {
/**
 * struct FaceAngle
{
	int   yaw;//angle of yaw,from -90 to +90,left is negative,right is postive
	int   pitch;//angle of pitch,from -90 to +90,up is negative,down is postive
	int   roll;//angle of roll,from -90 to +90,left is negative,right is postive
	float confidence;//confidence of face pose(from 0 to 1,0.6 is suggested threshold)
};
 */
	
	private int yaw;
	private int pitch;
	private int roll;
	private float confidence;
	
	public FaceAngle() {
		
	}
	
	public FaceAngle(int yaw, int pitch, int roll, float confidence) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		this.confidence = confidence;
	}
	
	public void setFaceAngle(int yaw, int pitch, int roll, float confidence) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		this.confidence = confidence;
	}

	public int getYaw() {
		return yaw;
	}

	public void setYaw(int yaw) {
		this.yaw = yaw;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public int getRoll() {
		return roll;
	}

	public void setRoll(int roll) {
		this.roll = roll;
	}

	public float getConfidence() {
		return confidence;
	}

	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}

	
}
