package com.frm.cn;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;  
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements Callback, PreviewCallback {
	private String mTag = "CameraView";
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private ImageStack imgStack = new ImageStack(FaceApp.CAMERA_IMAGE_WIDTH, FaceApp.CAMERA_IMAGE_HEIGHT);
    private boolean isPreview = false;
    private boolean isLoading = false;
    private boolean isCallback = true;
    
    public CameraView(Context context) {
    	super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void initView() {
        isPreview = false;
        isLoading = false;
        isCallback = true;
    }
    
    @SuppressLint("NewApi")
    public void openCamera(int cameraIndex){
    	log("openCamera() Camera:" + mTag);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        if (cameraIndex >= 0) {
        	log("Camera.open(cameraIndex) cameraIndex:" + cameraIndex);
        	try {
        	    mCamera = Camera.open(cameraIndex);
        	} catch(RuntimeException ex) {
        		log(ex.getLocalizedMessage());
        		if (mCamera != null) {
        		    mCamera.release();
        		}
        		try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            	try {
                	mCamera = Camera.open(cameraIndex);
                	} catch(RuntimeException exce) {
                		log(exce.getLocalizedMessage());
                		if (mCamera != null) {
                		    mCamera.release();
                		}
                		mCamera = null;
                	}
        	}
        } else {
        	mCamera = null;
        }
        isPreview = false;
        isLoading = false;
    }
    
	// 释放摄像头图像显�?
    public void releaseCamera() {
    	log("releaseCamera() Camera:" + mTag);
        // 释放摄像�?
        if (mCamera != null) {
        	mCamera.setPreviewCallback(null);
            if (isPreview) {
            	log("camera.stopPreview()");
            	mCamera.stopPreview();
            }
            mCamera.release();
            mCamera = null;
        }
        isPreview = false;
        isLoading = false;
    }
    
    /**
     * 初始化SurfaceView时调用一次，另外更改surface或�?onpause->onresume时调�?
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	log("surfaceChanged()");
        if(holder.getSurface() == null || mCamera == null){
            return;
        }
        mCamera.stopPreview();
        isPreview = false;
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();
        isPreview = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	log("surfaceCreated()");
        if(mCamera == null){
            return;
        }
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();
        isPreview = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	log("surfaceDestroyed()");
    }

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		//log("onPreviewFrame(byte[] data, Camera camera) isLoading:" + isLoading);
		if (isCallback && data != null) {
			isLoading = true;
			//log("imgStack.pushImageInfo(data, System.currentTimeMillis()) start");
			imgStack.pushImageInfo(data, System.currentTimeMillis());
			//log("imgStack.pushImageInfo(data, System.currentTimeMillis()) end");
		}
	}

	public ImageStack getImgStack() {
		return imgStack;
	}

	public void setTag(String tag) {
		mTag = tag;
	}

	public boolean isPreview() {
		return isPreview;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public boolean isCallback() {
		return isCallback;
	}

	public void setCallback(boolean isCallback) {
		this.isCallback = isCallback;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public void log(String msg) {
		Log.e(mTag, msg);
	}
}
