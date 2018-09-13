package com.frm.cn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import com.face.db.Record;
import com.face.db.User;
import com.face.sv.FaceInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
			private final static String TAG = "MainActivity";
			private final static int MSG_DISPLAY = 0x1006;
			private final static int MSG_RECOGNIZE_SUCCESS  = 0x1012;
			private final static int MSG_INIT_STATUS =   0x1013;
			private final static int FACE_PADDING_WARNNING = 0x1014;
			private final static int MSG_RECOGNIZE_RESULT = 0x1015;
			private final static int CAMERA_VIEW_STATUS = 0x1018;
			private final static int MSG_VIEW_TIMEOUT = 0x1019;

			// 超时时间
			private final static int INIT_DELAY_TIME = 1000;
			private final static int CAMERA_STATUS_DELAY = 2000;
			private final static int DISPLAY_INTERVAL = 2000;
			public final static int REQUEST_CODE = 10010;
			public final static int REQUEST_RESULT = 10011;
			public final static int DISPLAY_MESSAGE = 10012;

			private final static int READ_INTERVAL = 100;
			
			// 控件
			private TextView txtPlease;
			private TextView txtMsg;
			private TextView txtInfo;
			
			private ImageView ivMenu;
			private LinearLayout lLayoutMenu;
			private Boolean isViewMenu = false;
			
			// 图像相关参数
			private CameraView surfaceViewKJ;

			private float wRate = 0.0f;
			private float hRate = 0.0f;

			private int mWidth = 0;
			private int mHeight = 0;
			private OverlayerView overlayerView = null;
			
			private Toast mToask = null;
			
			//人脸检测
			private ImageStack imgKjStack = null;
			// 活体检测
			private static FaceStack faceKjStack = new FaceStack();
			
			private FeatureThread featureThread = null;
			private FaceDetectTask faceDetectTask = null;
			
			private int    mLastUserId = -1;
			private long    mLastTime = 0;
			
			private boolean isRunning = false;
			
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				
				WindowManager winManager=(WindowManager)getSystemService(this.WINDOW_SERVICE);
				log("screen width:" + winManager.getDefaultDisplay().getWidth()+" height:"+winManager.getDefaultDisplay().getHeight());
				
				DisplayMetrics metric = new DisplayMetrics();  
				getWindowManager().getDefaultDisplay().getMetrics(metric);  
				int width = metric.widthPixels;     // 屏幕宽度（像素）  
				int height = metric.heightPixels;   // 屏幕高度（像素）  ;
				log("screen width:" + width + " height:" + height);
				initView();
			}

			private void initView() {
				//log("==initView()");
				txtPlease = (TextView)this.findViewById(R.id.txtPlease);
				txtMsg = (TextView)this.findViewById(R.id.txtMsg);
				txtInfo = (TextView)this.findViewById(R.id.txtInfo);
				
				lLayoutMenu = (LinearLayout) this.findViewById(R.id.lLayoutMenu);
				ivMenu = (ImageView) this.findViewById(R.id.ivMenu);
				ivMenu.setOnClickListener(this);

				overlayerView = (OverlayerView)this.findViewById(R.id.overlayerView);
				overlayerView.setOnClickListener(this);

				surfaceViewKJ = (CameraView) findViewById(R.id.surfaceViewKJ);
				surfaceViewKJ.initView();
		        surfaceViewKJ.setTag("surfaceViewKJ");
				surfaceViewKJ.setVisibility(View.INVISIBLE);
		        surfaceViewKJ.setOnClickListener(this);
				
				imgKjStack = surfaceViewKJ.getImgStack();
				
				Button btnFaceRec = (Button) this.findViewById(R.id.btnFaceRec);
				btnFaceRec.setOnClickListener(this);
				Button btnUserManage = (Button) this.findViewById(R.id.btnUserManage);
				btnUserManage.setOnClickListener(this);
				Button btnRecordManage = (Button) this.findViewById(R.id.btnRecordManage);
				btnRecordManage.setOnClickListener(this);
				Button btnSystemConfig = (Button)this.findViewById(R.id.btnSystemConfig);
				btnSystemConfig.setOnClickListener(this);
				Button btnSystemInfo = (Button)this.findViewById(R.id.btnSystemInfo);
				btnSystemInfo.setOnClickListener(this);
				// 停用当前页按钮
				btnFaceRec.setEnabled(false);
				
				loadConfig();
			}

			@Override
			protected void onResume() {
				super.onResume();
				log("==onResume()");
				
				initCameraViewKj();

				isRunning = false; 
				mLastUserId = -1; 

				txtMsg.setText("");
				txtInfo.setText("");
				txtPlease.setText(R.string.txtPleaseMsg);

				mHandler.sendEmptyMessageDelayed(MSG_INIT_STATUS, INIT_DELAY_TIME);
		        mHandler.sendEmptyMessageDelayed(CAMERA_VIEW_STATUS, 3 * CAMERA_STATUS_DELAY);
			}

			@Override
			protected void onPause() {
				super.onPause();
				log("onPause()");
				isRunning = false;
				mLastUserId = -1; 
				
				mHandler.removeMessages(CAMERA_VIEW_STATUS);
				mHandler.removeMessages(MSG_VIEW_TIMEOUT);
				mHandler.removeMessages(MSG_RECOGNIZE_SUCCESS);
		    	mHandler.removeMessages(MSG_DISPLAY);

				releaseCameraViewKj();
				
				if (mToask != null) {
					mToask.cancel();
					mToask = null;
				}
				if (featureThread != null) {
					featureThread.setStop(true);
					featureThread = null;
				}
				if (faceDetectTask != null) {
					faceDetectTask.setStop(true);
					faceDetectTask.cancel(false);
					faceDetectTask = null;
				}
			}

			@Override
			protected void onDestroy() {
				super.onDestroy();
				log("onDestroy()");
			}
			
			private void initCameraViewKj() {
		        surfaceViewKJ.openCamera(FaceApp.cameraIndexKJ);
			}
			
			private void releaseCameraViewKj() {
				surfaceViewKJ.releaseCamera();
			}

		    // 获取配置参数
		   public void loadConfig() {
			   log("loadConfig()");
		       SharedPreferences pre = this.getSharedPreferences("config", MODE_PRIVATE);
		       FaceApp.mDeviceSerial = pre.getString(FaceApp.KEY_DEVICE_SERIAL, FaceApp.DEFAULT_DEVICE_SERIAL);
		       FaceApp.mRecognizeLimit = pre.getInt(FaceApp.KEY_RECOGNIZE_LIMIT, FaceApp.DEFAULT_RECOGNIZE_LIMIT);
		    }

			@Override
			public void onClick(View v) {
				Intent mIntent;
				switch (v.getId()) {
				case R.id.ivMenu:
					upateMenuView();
					break;
				case R.id.btnFaceRec:
					mIntent = new Intent(this, MainActivity.class);
					this.startActivity(mIntent);
		            break;
				case R.id.btnUserManage:
					mIntent = new Intent(this, UserManageActivity.class);
					this.startActivity(mIntent);
					break;
				case R.id.btnRecordManage:
					mIntent = new Intent(this, RecordListActivity.class);
					this.startActivity(mIntent);
					break;
				case R.id.btnSystemConfig:
					mIntent = new Intent(this, RecognizeConfigActivity.class);
					this.startActivity(mIntent);
					break;
				case R.id.btnSystemInfo:
					mIntent = new Intent(this, InformationActivity.class);
					this.startActivity(mIntent);
					break;
				case R.id.overlayerView:
					log("onClick(overlayerView)");
					overlayerView.bringToFront();
					overlayerView.invalidate();
					break;
				case R.id.surfaceViewKJ:
					log("onClick(surfaceViewKJ)");
					overlayerView.bringToFront();
					overlayerView.invalidate();
					break;
				default:
					break;
				}
			}

			private void upateMenuView() {
				if (isViewMenu) {
					lLayoutMenu.setVisibility(View.GONE);
					ivMenu.setImageResource(R.drawable.btn_menu_up);
					isViewMenu = false;
				} else {
					lLayoutMenu.setVisibility(View.VISIBLE);
					ivMenu.setImageResource(R.drawable.btn_menu_down);
					isViewMenu = true;
				}
			}
			
			public void startRecognizeUser() {
				log("reportMessage() isRunning:" + isRunning);
				if (!isRunning) {
					isRunning = true;
					// 清空缓存记录
					imgKjStack.clearAll();
					faceKjStack.clearAll();
					if (faceDetectTask != null) {
						faceDetectTask.setStop(true);
						faceDetectTask = null;
					}
					faceDetectTask = new FaceDetectTask();
					faceDetectTask.setStop(false);
					faceDetectTask.execute();
					
					if (featureThread != null) {
						featureThread.setStop(true);
						featureThread = null;
					}
					// 启动活体检测
					featureThread = new FeatureThread();
					featureThread.setStop(false);
					featureThread.start();
				} else {
					log(String.format("isRunning:%b mInitStatus:%d", isRunning, FaceApp.mInitStatus));
				}
			}

			@SuppressLint("HandlerLeak")
			Handler mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch (msg.what) {
					case MSG_INIT_STATUS:
						if (0 == mWidth && 0 == mHeight) {
							mWidth = surfaceViewKJ.getWidth();
							mHeight = surfaceViewKJ.getHeight();
							log("mWidth:" + mWidth + " mHeight:" + mHeight);
							wRate = mWidth;
							wRate /= FaceApp.CAMERA_IMAGE_WIDTH;
							hRate = mHeight;
							hRate /= FaceApp.CAMERA_IMAGE_HEIGHT;
							log("wRate:" + wRate + " hRate:" + hRate);
							overlayerView.setRate(wRate, hRate);
						}
						if (FaceApp.mInitStatus == 2) {
						    showToast("人脸检测算法初始化成功，\r\n可以开始认证比对。");
							log("Receiver MSG_INIT_STATUS Message FaceApp.mInitStatus:" +  FaceApp.mInitStatus);

							startRecognizeUser();
						} else {
							showToast("人脸检测算法初始化没有成功，\r\n请等待或重新启动。");
							log("Receiver MSG_INIT_STATUS Message FaceApp.mInitStatus:" +  FaceApp.mInitStatus);
							mHandler.sendEmptyMessageDelayed(MSG_INIT_STATUS, INIT_DELAY_TIME);
						}
						break;
					case CAMERA_VIEW_STATUS:
						mHandler.removeMessages(CAMERA_VIEW_STATUS);
						boolean isLoadingKJ = surfaceViewKJ.isLoading();
						log("surfaceViewKJ.isLoadingKJ:" + isLoadingKJ);
						boolean isPreviewKj = surfaceViewKJ.isPreview();
						log("surfaceViewKJ.isPreview:" + isPreviewKj);
						boolean isReOpen = false;
						if (!isPreviewKj) {
							isReOpen = true;
							releaseCameraViewKj();
							surfaceViewKJ.setVisibility(View.INVISIBLE);
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							initCameraViewKj();
							surfaceViewKJ.setVisibility(View.VISIBLE);
						}
						surfaceViewKJ.setLoading(false);
						if (isReOpen) {
						    mHandler.sendEmptyMessageDelayed(CAMERA_VIEW_STATUS, 5 * CAMERA_STATUS_DELAY);
						} else {
							mHandler.sendEmptyMessageDelayed(CAMERA_VIEW_STATUS, CAMERA_STATUS_DELAY);
						}
						break;
					case MSG_VIEW_TIMEOUT:
						txtMsg.setText("");
						txtInfo.setText("");
						break;
					case MSG_RECOGNIZE_SUCCESS:
						log("receive Message MSG_RECOGNIZE_SUCCESS");
						txtMsg.setText("识别通过！");
						int uId = msg.arg1;
						int ret = msg.arg2;
						txtMsg.setTextColor(MainActivity.this.getResources().getColor(R.color.gDeepGreen));
						txtInfo.setText("工号:" + uId + "  相识度：" + ret);
						txtInfo.setTextColor(MainActivity.this.getResources().getColor(R.color.gDeepGreen));
						mHandler.removeMessages(MSG_VIEW_TIMEOUT);
						break;
					case MSG_RECOGNIZE_RESULT:
						log("received MSG_RECOGNIZE_RESULT message.");
						int userId = msg.arg1;
						int result = msg.arg2;
						txtMsg.setText("识别未通过！");
						txtMsg.setTextColor(MainActivity.this.getResources().getColor(R.color.red));
						txtInfo.setText("工号:" + userId + "  相识度：" + result);
						txtInfo.setTextColor(MainActivity.this.getResources().getColor(R.color.red));
						mHandler.removeMessages(MSG_VIEW_TIMEOUT);
						break;
					case FACE_PADDING_WARNNING:
						showToast("请将摄像头对准人脸，\r\n人脸必须处于红色线框标记范围！");
						break;
					case MSG_DISPLAY:
						mHandler.removeMessages(MSG_DISPLAY);
						txtPlease.setText(R.string.txtPleaseMsg);
						break;
					default:
						break;
					}
				}
			};

			/**
			 * 定义一个类，让其继承AsyncTask这个类
			 */
			class FaceDetectTask extends AsyncTask<Void, Rect, Void> {
				int recPadding = FaceApp.DEFAULT_DETECT_PADDING;
				int recType = FaceApp.TYPE_FACE_RECOGNIZE;
                int camWidth = FaceApp.CAMERA_IMAGE_WIDTH;
                int camHeight = FaceApp.CAMERA_IMAGE_HEIGHT;
                int recWidth = FaceApp.RECORD_IMAGE_WIDTH;
                int recHeight = FaceApp.RECORD_IMAGE_HEIGHT;
			    
			    private boolean isStop = false;

				public boolean isStop() {
					return isStop;
				}

				public void setStop(boolean isStop) {
					this.isStop = isStop;
				}

				@Override
				protected Void doInBackground(Void... params) {
					log("FaceDetectTask doInBackground() start");
					byte[] dataKj = null;					
					ImageInfo imgInfoKj = null;
					byte[] BRG24Kj = null;
					FaceInfo faceInfoKj = null;
					FaceImage faceImgKj = null;
                    //int ret = 0;
                    //long time = 0;
                    //String imgPath = null;
					while (!isStop && isRunning) {
						imgInfoKj = imgKjStack.pullImageInfo();
						log("FaceDetectTask imgKjStack.pullImageInfo() isStop:" + isStop + " isRunning:" + isRunning + " imgInfoKj.isNew():" + imgInfoKj.isNew());
						faceImgKj = null;
						if (!isStop && isRunning && imgInfoKj.isNew()) {
							log("FaceDetectTask imgInfoKj.getData() start");
							dataKj = imgInfoKj.getData();
							///imgPath = FaceApp.saveImagePath + imgInfoKj.getTime() + ".yuv";
							///imgUtils.saveImage(imgPath, dataKj);
							//BRG24Kj = getBGR24FromYuv420(dataKj, camWidth, camHeight);
							BRG24Kj = FaceApp.mImgUtil.encodeYuv420pToBGR(dataKj, camWidth, camHeight);
							if (BRG24Kj == null) {
								continue;
							}
							///imgPath = FaceApp.saveImagePath + imgInfoKj.getTime() + ".rgb";
							///ImageUtils.saveImage(imgPath, BRG24Kj);
							log("FaceDetectTask mDetect.getFacePositionFromBGR24(Kj)");
							faceInfoKj = FaceApp.mDetect.faceDetectScale(BRG24Kj, camWidth, camHeight, recType, recPadding);
							//log("FaceDetectTask FaceApp.mRecognize.faceDetectScale(Kj)");
							if (!isStop && isRunning && faceInfoKj.getRet() > 0) {
								Rect rect = faceInfoKj.getFacePos(0).getFace();
								// 上报人脸坐标
								publishProgress(new Rect[]{rect});

								faceImgKj = new FaceImage(camWidth, camHeight);
								faceImgKj.setBgr24(BRG24Kj);
								faceImgKj.setFaceInfo(faceInfoKj.getFacePosData(0));
							    faceImgKj.setTime(imgInfoKj.getTime());
								faceKjStack.pushFaceImage(faceImgKj);
								//log("FaceDetectTask faceKjStack.pushFaceImage(faceImgKj)");
							} else {
								log("FaceDetectTask isRunning is false or faceInfoKJ.getRect:" + faceInfoKj.getRet());
							}
						} else {
							threadSleep(READ_INTERVAL);
							log("FaceDetectTask isRunning or imgInfoKj.isNew() :" + imgInfoKj.isNew());
						}
					}
					return null;
				}

				@Override
				protected void onProgressUpdate(Rect... values) {
					super.onProgressUpdate(values);
					// 更新ProgressDialog的进度条
		            if (values != null && values.length > 0) {
		            	Rect rect = values[0];
						// 显示人脸坐标
						if (overlayerView != null && rect != null) {
						    overlayerView.setRecognizeResult(camWidth - rect.right, rect.top, camWidth - rect.left, rect.bottom);
						    txtPlease.setText("");
							mHandler.removeMessages(MSG_DISPLAY);
							mHandler.sendEmptyMessageDelayed(MSG_DISPLAY, DISPLAY_INTERVAL);
						}
		            }
				}
			}
			
			class FeatureThread extends Thread {
				private int recLimit = FaceApp.mRecognizeLimit;
				private int sameTime = FaceApp.mSameTime;

				private boolean isStop = false;

				public boolean isStop() {
					return isStop;
				}

				public void setStop(boolean isStop) {
					this.isStop = isStop;
				}

				@Override
				public void run() {
					//log("FeatureThread start");
					int[] retArr = {0, 0};
		            FaceImage faceKj = null;
					String imagePath = null;
					int userId = -1;
					User user =  null;
					mLastUserId = -1;
					mLastTime = 0;
					long time = 0;
					int result = 0;
					//byte[] imgData = null;
					Record recRecord = new Record();
					while (!isStop && isRunning) {
						faceKj = faceKjStack.pullFaceImage();
						//log("FeatureThread faceKjStack.pullFaceImage(live)");
						if (faceKj == null || faceKj.getFaceInfo() == null || faceKj.getBgr24() == null) {
							//log("FeatureThread faceKj == null || faceKj.getFaceInfo() == null || faceKj.getBgr24() == null");
							threadSleep(READ_INTERVAL);
							continue;
						}
						retArr = FaceApp.mRecognize.recognizeFaceMore(faceKj.getBgr24(), faceKj.getWidth(), faceKj.getHeight(), faceKj.getFaceInfo());
						//log("FeatureThread FaceApp.mRecognize.recognizeFaceMore() retArr[0]:" + retArr[0] + " retArr[1]:" + retArr[1]);
						if (retArr[0] >= 0) {
							userId = retArr[0];
							result = retArr[1];
						   	if (retArr[1] >= recLimit) {
                                time = System.currentTimeMillis();
						   		if ((mLastUserId != userId) || (time - mLastTime >= sameTime)) {
								    mLastTime = time;
								    mLastUserId = userId;
								   	recRecord.setUserId(userId);
							   		user = FaceApp.mProvider.getUserByUserId(userId);
							   	   	if (user != null) {
						   	    	    recRecord.setUserName(user.getUserName());
						   	    	    recRecord.setUserType(user.getUserType());
						   	    	    recRecord.setUserImage(user.getUserImage());
						   	        }
						   	        recRecord.setRecResult(result);
						   	        recRecord.setCreateTime(faceKj.getTime());
						            imagePath = FaceApp.recordImagePath + String.format("%08d/", userId);
						            //log("FeatureThread createDir(imagePath):" + imagePath);
						            createDir(imagePath);
						            imagePath = imagePath + String.format("%d.jpg", faceKj.getTime());
						            //log("FeatureThread imagePath:" + imagePath);
						            FaceApp.mImgUtil.encodeBGR24toJpg(faceKj.getBgr24(), faceKj.getWidth(), faceKj.getHeight(), imagePath);
						            //log("FeatureThread FaceApp.mRecognize.encodeBGR24toJpg() ret:" + ret);
			    		            recRecord.setRecImage(imagePath);
						            FaceApp.mProvider.addRecord(recRecord);
						            //log("FeatureThread send MSG_RECOGNIZE_SUCCESS message.");
						            mHandler.sendMessage(mHandler.obtainMessage(MSG_RECOGNIZE_SUCCESS, userId, result));
						   		}
						   	} else {
						   		//log("FeatureThread send message MSG_RECOGNIZE_RESULT, userId:" + userId + " result:" + result);
						   		mHandler.sendMessage(mHandler.obtainMessage(MSG_RECOGNIZE_RESULT, userId, result));
						   	}
						} else {
						   	log("FeatureThread recognizeFaceMore() fail, ret:" + retArr[0]);
						}
					}
				    //log("FeatureThread while(isRunning) end. isRunning:" + isRunning);
			    }
			}
			
		    public void createDir(String dir) {
		    	File file = new File(dir);
		    	if (!file.exists() || !file.isDirectory()) {
		    		file.mkdirs();
		    	}
		    }
			
			/**
			 * Yuv420转Bitmap
			 * @param data yuv420数据
			 * @param width 图像宽度
			 * @param height 图像高度
			 * @return 图像缩小一半
             */
			public Bitmap getBitmapFromYuv420(byte[] data, int camWidth, int camHeight, int recWidth, int recHeight) {
				if (data == null || data.length == 0) {
					return null;
				}
				Bitmap bmp = null;
				Bitmap tmp = null;
				if (data.length > 0) {
					YuvImage yuvImgKj = new YuvImage(data, ImageFormat.NV21, camWidth, camHeight, null);
					if (yuvImgKj != null) {
						ByteArrayOutputStream outs = null;
						try {
						    outs = new ByteArrayOutputStream();
							if (yuvImgKj.compressToJpeg(new Rect(0, 0, camWidth, camHeight), 100, outs)) {
							    outs.flush();
								byte[] bts = outs.toByteArray();
								tmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
								if (tmp != null) {
								    Matrix matrix = new Matrix();
								    //matrix.setScale(-1, 1);
								    float wScale = recWidth;
								    wScale /= camWidth;
								    float hScale = recHeight;
								    hScale /= camHeight;
								    matrix.setScale(wScale, hScale);
								    bmp = Bitmap.createBitmap(tmp, 0, 0, camWidth, camHeight, matrix, true);
								}
							}
						} catch(Exception ex) {
							ex.printStackTrace();
						} finally {
						    if (tmp != null && !tmp.sameAs(bmp)) {
						    	tmp.recycle();
						    }
							if (outs != null) {
								try {
									outs.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				return bmp;
			}
			
			private void threadSleep(int time) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			private void recycleBitmap(Bitmap bmp) {
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
				}
			}
			
			private void showToast(String msg) {
				if (mToask != null) {
					mToask.cancel();
					mToask = null;
				}
				mToask = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
				mToask.show();
			}

			public void log(String msg) {
				Log.e(TAG, msg);
			}
}
