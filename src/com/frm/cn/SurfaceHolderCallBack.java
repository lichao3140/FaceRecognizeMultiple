package com.frm.cn;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class SurfaceHolderCallBack implements Callback, SurfaceHolder, PreviewCallback {
	private final static String TAG = "SurfaceHolderCallBack";
	public int rotate = 90;
	private boolean isPreview = false;
	private int cameraIndex = 0;
	private Camera camera;
	private int width;
	private int height;
	
	public SurfaceHolderCallBack(int cameraIndex, int rotate) {
		this.cameraIndex = cameraIndex;
		this.rotate = rotate;
	}

	public int getRotate() {
		return rotate;
	}

	public void setRotate(int rotate) {
		this.rotate = rotate;
	}

	public int getCameraIndex() {
		return cameraIndex;
	}

	public void setCameraIndex(int cameraIndex) {
		this.cameraIndex = cameraIndex;
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

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
//		log("surfaceCreated()");
		try {
			// 设置显示目标界面
			if (camera != null) {
				camera.setPreviewDisplay(this);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		log("surfaceChanged() width:" + width + " height:" + height);
		if (camera != null) {
			camera.stopPreview();
			isPreview = false;
			camera.setPreviewCallback(this);
			//camera.setPreviewCallbackWithBuffer(this);
			//camera.setAutoFocusMoveCallback(null);
			camera.startPreview();
			isPreview = true;
		}
		// 打开摄像头
		initCamera();
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
//		log("surfaceDestroyed()");
		// 释放摄像头
		releaseCamera();
	}

	@SuppressLint("NewApi")
	private void initCamera() {
		log("initCamera() size:" + Camera.getNumberOfCameras());
		try {
			if (!isPreview) {
				log("Camera.open(cameraIndex) cameraIndex:" + cameraIndex);
				camera = Camera.open(cameraIndex);
				// 设置旋转90度
				camera.setDisplayOrientation(rotate);
				if (camera != null) {
					Camera.Parameters parameters = camera.getParameters();
					// 设置分辨率
					List<Camera.Size> list = parameters
							.getSupportedPreviewSizes();
					Iterator<Camera.Size> its = list.iterator();
					int minWidth = 0;
					Camera.Size size = null;
					while (its.hasNext()) {
						size = (Camera.Size) its.next();
						log("width=" + size.width + " height=" + size.height);
						if (size.width / 4 != size.height / 3) {
							continue;
						}
						if (minWidth != 0 && minWidth < size.width) {
							continue;
						}
						minWidth = size.width;
						if (320 == size.width && 240 == size.height) {
							width = 320;
							height = 240;
						} else if (640 == size.width && 480 == size.height) {
							width = 640;
							height = 480;
						}
					}

					log("width=" + width + " height=" + height);
					parameters.setPictureSize(width, height);
					parameters.setPreviewSize(width, height);
					camera.setParameters(parameters);
                    //if (frameBuf == null) {
                    //	frameBuf = new byte[width * height * 3 / 2];
                    //}
					// 通过SurfaceView显示取景画面
					camera.setPreviewDisplay(this);
					// camera.addCallbackBuffer(camBuf);
					//camera.setPreviewCallbackWithBuffer(this);
					camera.setPreviewCallback(this);
					// 开始预览
					camera.startPreview();
					isPreview = true;
				}
			}
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			// 释放摄像头
			releaseCamera();
		} catch (Exception e) {
			e.printStackTrace();
			// 释放摄像头
			releaseCamera();
		}
	}

	// 释放摄像头图像显示
	public void releaseCamera() {
		// 释放摄像头
		if (camera != null) {
			//camera.setPreviewCallbackWithBuffer(null);
			camera.setPreviewCallback(null);
			if (isPreview) {
				camera.stopPreview();
				isPreview = false;
			}
			camera.release();
			camera = null;
		}
	}
	

	@Override
	public void addCallback(Callback arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Surface getSurface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rect getSurfaceFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCreating() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Canvas lockCanvas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas lockCanvas(Rect arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeCallback(Callback arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFixedSize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFormat(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setKeepScreenOn(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSizeFromLayout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Deprecated
	public void setType(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlockCanvasAndPost(Canvas arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub
		
	}

	public void log(String msg) {
		Log.d(TAG, msg);
	}
}
