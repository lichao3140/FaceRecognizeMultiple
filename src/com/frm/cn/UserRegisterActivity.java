package com.frm.cn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.face.db.User;
import com.face.sv.FaceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class UserRegisterActivity extends Activity implements OnClickListener {
	private final static String TAG = "UserRegisterActivity";
	private final static int MSG_FACE_POSITION = 0x1001;
	private final static int MSG_SIM = 0x1008;
	private final static int FACE_REGISTER_TIMEOUT = 0x1010;
	private final static int MSG_LIVE_UPDATE = 0x1011;
	private final static int MSG_REGISTER_SUCCESS = 0x1012;
	private final static int MSG_INIT_STATUS =   0x1013;
	private final static int CAMERA_VIEW_STATUS = 0x1018;

	// 超时时间
	private final static int OPEN_DELAY_TIME = 2000;
	private final static int INIT_DELAY_TIME = 1000;
	private final static int DISPLAY_INTERVAL = 1000;
	private final static int UNDISPLAY_INTERVAL = 1000;
	private final static int FACE_LIVE_DELAY = 500;
	private final static int REGISTER_TIMEOUT = 10000;
	private final static int CAMERA_STATUS_DELAY = 2000;

	public final static int REQUEST_CODE = 10010;
	public final static int REQUEST_RESULT = 10011;
	public final static int DISPLAY_MESSAGE = 10012;

	private final static int READ_INTERVAL = 50;
	// / private final static int LOAD_IMAGE_INTERVAL = 100;
	private final static int HW_KJ_FRAME_INTERVAL = 300;

	private final static int HW_POSITION_DEVIATION = 50;

	public final static int DEFAULT_ROTATE_VALUE = 90;

	// 控件
	private EditText txtId;
	private EditText txtUserId;
	private EditText txtUserName;
	Button btnStartRegister = null;
	Button btnTitle = null;

	private int mRId = -1;
	private int mUserId = 0;
	private String mUserName;
	private int mOperateType = 0;  // 操作类型， 0表示注册， 0x1001表示修改

	// 图像相关参数
	private int cameraIndexKJ = 0;
	private CameraView surfaceViewKJ;

	FaceImage faceImgKj = null;

	// 摄像头图像大小
	private int pWidth = FaceApp.CAMERA_IMAGE_WIDTH;
	private int pHeight = FaceApp.CAMERA_IMAGE_HEIGHT;

	private float wRate = 0.0f;
	private float hRate = 0.0f;

	private int mWidth = 0;
	private int mHeight = 0;

	private FrameLayout fLayout = null;
	private OverlayerView overlayerView = null;

	private Toast mToask = null;

	private Bitmap bmp = null;

	// 人脸检测
	private FaceImage fImgUser = null;
	private ImageStack imgKjStack = null;
	// 活体检测

	private ImageUtils imgUtils = new ImageUtils();

	private FaceDetectTask faceDetectTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_register);

		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		// log("==initView()");
		txtId = (EditText) this.findViewById(R.id.editId);
		txtUserId = (EditText) this.findViewById(R.id.editUserId);
		txtUserName = (EditText) this.findViewById(R.id.editUserName);

		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnBack.setText(R.string.txtBack);
		btnTitle = (Button) this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleUserRegister);
		Button btnNext = (Button) this.findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		btnNext.setText(R.string.txtCancel);

		// btnStartRegister
		btnStartRegister = (Button) this.findViewById(R.id.btnStartRegister);
		btnStartRegister.setOnClickListener(this);
		btnStartRegister.setEnabled(true);

		fLayout = (FrameLayout) this.findViewById(R.id.frameLayout);
		overlayerView = (OverlayerView) this.findViewById(R.id.overlayerView);

		surfaceViewKJ = (CameraView) findViewById(R.id.surfaceViewKJ);
		surfaceViewKJ.initView();
		surfaceViewKJ.setTag("surfaceViewKJ");
		imgKjStack = surfaceViewKJ.getImgStack();
	}

	private void initData() {
		// log("==initData()");
		Intent intent = this.getIntent();
		mRId = intent.getIntExtra(FaceApp.KEY_USER_RID, -1);
		mOperateType = intent.getIntExtra(FaceApp.KEY_OPERATE_TYPE, FaceApp.OPERATE_TYPE_REGISTER);
		log("initData() mRId:" + mRId);
		if (mRId < 0) {
			this.finish();
		}
		if (mOperateType == FaceApp.OPERATE_TYPE_REGISTER) {
		    btnTitle.setText(R.string.titleUserRegister);
		    mUserId = intent.getIntExtra(FaceApp.KEY_USER_ID, -1);
		    mUserName = intent.getStringExtra(FaceApp.KEY_USER_NAME);
		} else if (mOperateType == FaceApp.OPERATE_TYPE_UPDATE_FEATURE) {
			btnTitle.setText(R.string.titleUserFeatureEdit);
		    User user = FaceApp.mProvider.getUserById(mRId);
		    if (user != null) {
		    	mUserId = user.getUserId();
		    	mUserName = user.getUserName();
		    } else {
		    	showToast("未找到更新用户的记录id,请确认该记录是否存在！", Toast.LENGTH_SHORT);
		    }
		} else if (mOperateType == FaceApp.OPERATE_TYPE_UPDATE_ALL) {
			btnTitle.setText(R.string.titleUserAllEdit);
		    mUserId = intent.getIntExtra(FaceApp.KEY_USER_ID, -1);
		    mUserName = intent.getStringExtra(FaceApp.KEY_USER_NAME);
		}
		txtId.setText(String.valueOf(mRId));
		txtUserId.setText(String.valueOf(mUserId));
		txtUserName.setText(mUserName);
	}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		log("==onResume()");
		btnStartRegister.setEnabled(true);
		
		initData();

		initCameraViewKj();

		overlayerView.bringToFront();
		fLayout.bringChildToFront(overlayerView);
		fLayout.updateViewLayout(overlayerView, overlayerView.getLayoutParams());

		mHandler.sendEmptyMessageDelayed(MSG_INIT_STATUS, INIT_DELAY_TIME);
        mHandler.sendEmptyMessageDelayed(CAMERA_VIEW_STATUS, 3 * CAMERA_STATUS_DELAY);
	}

	@Override
	protected void onPause() {
		super.onPause();
		log("onPause()");
		
		mHandler.removeMessages(CAMERA_VIEW_STATUS);
		mHandler.removeMessages(MSG_FACE_POSITION);
		mHandler.removeMessages(FACE_REGISTER_TIMEOUT);

		if (mToask != null) {
			mToask.cancel();
			mToask = null;
		}

		releaseCameraViewKj();

		if (faceDetectTask != null) {
			faceDetectTask.setStop(true);
			faceDetectTask = null;
		}
		btnStartRegister.setEnabled(true);
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
	
	/**
	 * @return 摄像头是否存在
	 */
	private boolean checkCamera() {
		return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	@Override
	public void onClick(View v) {
		Intent mIntent;
		switch (v.getId()) {
		case R.id.btnBack:
			mIntent = new Intent(this, RegisterInputActivity.class);
			mIntent.putExtra(FaceApp.KEY_USER_RID, mRId);
			mIntent.putExtra(FaceApp.KEY_USER_ID, mUserId);
			mIntent.putExtra(FaceApp.KEY_USER_NAME, mUserName);
			mIntent.putExtra(FaceApp.KEY_OPERATE_TYPE, mOperateType);
			this.startActivity(mIntent);
			this.finish();
			break;
		case R.id.btnStartRegister:
			btnStartRegister.setEnabled(false);
			startRegisterUser();
			break;
		case R.id.btnNext:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			this.finish();
			break;
		default:
			break;
		}
	}

	public void startRegisterUser() {
		log("reportMessage() ");
		if (FaceApp.mInitStatus == 2) {
			showToast("人脸检测算法初始化成功，\r\n可以开始认证比对。", Toast.LENGTH_SHORT);
			if (0 == mWidth && 0 == mHeight) {
				mWidth = surfaceViewKJ.getWidth();
				mHeight = surfaceViewKJ.getHeight();
				log("mWidth:" + mWidth + " mHeight:" + mHeight);
				wRate = mWidth;
				wRate /= FaceApp.WIDTH;
				hRate = mHeight;
				hRate /= FaceApp.HEIGHT;
				log("wRate:" + wRate + " hRate:" + hRate);
			}

			if (fImgUser != null) {
				fImgUser.setBgr24(null);
			}
			fImgUser = null;

			// 清空缓存记录
			imgKjStack.clearAll();
			if (faceDetectTask != null) {
				faceDetectTask.setStop(true);
				faceDetectTask = null;
			}
			faceDetectTask = new FaceDetectTask();
			faceDetectTask.setStop(false);
			faceDetectTask.execute();
			
			mHandler.removeMessages(FACE_REGISTER_TIMEOUT);
			mHandler.sendEmptyMessageDelayed(FACE_REGISTER_TIMEOUT, REGISTER_TIMEOUT);
			return;
		} else {
			showToast("人脸检测算法初始化没有成功，\r\n请等待或重新启动。", Toast.LENGTH_SHORT);
			log("FaceApp.mInitStatus:" + FaceApp.mInitStatus);
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
				break;
			case CAMERA_VIEW_STATUS:
				mHandler.removeMessages(CAMERA_VIEW_STATUS);
				boolean isLoadingKJ = surfaceViewKJ.isLoading();
				log("surfaceViewKJ.isLoadingKJ:" + isLoadingKJ);
				boolean isPreviewKj = surfaceViewKJ.isPreview();
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
			case MSG_FACE_POSITION:
				mHandler.removeMessages(MSG_FACE_POSITION);
				btnStartRegister.setEnabled(true);
				showRegisterDialog();
				break;
			case MSG_REGISTER_SUCCESS:

				break;
			case FACE_REGISTER_TIMEOUT:
				mHandler.removeMessages(FACE_REGISTER_TIMEOUT);
				if (faceDetectTask != null) {
					faceDetectTask.setStop(true);
					faceDetectTask.cancel(false);
					faceDetectTask = null;
				}
				btnStartRegister.setEnabled(true);
				showToast("注册超时，请重新开始注册！", Toast.LENGTH_SHORT);
				break;
			default:
				break;
			}
		}
	};

	private void showRegisterDialog() {
		log("showRegisterDialog()");
		if (faceImgKj == null) {
			return;
		}
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.layout_register_dialog, null);
		EditText editId = (EditText) view.findViewById(R.id.editId);
		editId.setText(String.valueOf(mRId));
		EditText editUserId = (EditText) view.findViewById(R.id.editUserId);
		editUserId.setText(String.valueOf(mUserId));
		EditText editUserName = (EditText) view.findViewById(R.id.editUserName);
		editUserName.setText(mUserName);
		ImageView ivUserImage = (ImageView) view.findViewById(R.id.ivUserImage);
		bmp = BitmapFactory.decodeFile(FaceApp.registerTempImage);
		if (faceImgKj != null && bmp != null) {
			ivUserImage.setImageBitmap(bmp);
		} else {
			ivUserImage.setImageResource(R.drawable.img_face);
		}
		mBuilder.setTitle(R.string.txtRegisterUserInfo);
		mBuilder.setView(view);
		mBuilder.setCancelable(true);
		mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
					bmp = null;
				}
			}
		});
		mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int ret = FeatureRegister(faceImgKj);
				switch (ret) {
				case 1:
					Intent intent = new Intent(UserRegisterActivity.this, RegisterResultActivity.class);
					intent.putExtra(FaceApp.KEY_USER_ID, mUserId);
					intent.putExtra(FaceApp.KEY_OPERATE_TYPE, mOperateType);
					UserRegisterActivity.this.startActivity(intent);
					UserRegisterActivity.this.finish();
					break;
				case -1:
					log("人脸信息为空！");
					break;
				case -2:
					log("人脸图片为空！");
					break;
				case -3:
					log("注册失败！");
					break;
				default:
					break;
				}
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
					bmp = null;
				}
			}
		});
		mBuilder.create();
		mBuilder.show();
	}

	/**
	 * 定义一个类，让其继承AsyncTask这个类
	 */
	class FaceDetectTask extends AsyncTask<Void, Rect, Void> {
		private boolean isStop = false;
		int recPadding = FaceApp.DEFAULT_DETECT_PADDING;
		int recType = FaceApp.TYPE_FACE_REGISTER;
        int camWidth = FaceApp.CAMERA_IMAGE_WIDTH;
        int camHeight = FaceApp.CAMERA_IMAGE_HEIGHT;
        int recWidth = FaceApp.RECORD_IMAGE_WIDTH;
        int recHeight = FaceApp.RECORD_IMAGE_HEIGHT;
        
		public boolean isStop() {
			return isStop;
		}

		public void setStop(boolean isStop) {
			this.isStop = isStop;
		}

		@Override
		protected Void doInBackground(Void... params) {
			log("FaceDetectTask doInBackground() start");
			byte[] data = null;
			//Bitmap imgKj = null;
			ImageInfo imgInfoKj = null;
			byte[] BRG24 = null;
			FaceInfo faceInfo = null;
			faceImgKj = null;
			while (!isStop) {
				imgInfoKj = imgKjStack.pullImageInfo();
				log("FaceDetectTask imgKjStack.pullImageInfo() isNew:"+ imgInfoKj.isNew());
				faceImgKj = null;
				if (!isStop && imgInfoKj.isNew()) {
					data = imgInfoKj.getData();
					// imgUtils.saveImage(imgUtils.getCamImgPath(FaceApp.imageDir,
					// imgInfoHw.getTime()), imgKj);
					BRG24 = FaceApp.mImgUtil.encodeYuv420pToBGR(data, camWidth, camHeight);
					log("FaceDetectTask mDetect.getFacePositionFromBGR24(Kj)");
					faceInfo = FaceApp.mDetect.faceDetectMaster(BRG24, camWidth, camHeight, recType, recPadding);
					log("FaceDetectTask FaceApp.mRecognize.faceDetectMaster(BRG24, camWidth, camHeight, recType, recPadding) ret:" + faceInfo.getRet());
					if (!isStop && faceInfo.getRet() > 0) {
						Rect rect = faceInfo.getFacePos(0).getFace();
						faceImgKj = new FaceImage(recWidth, recHeight);
						faceImgKj.setBgr24(BRG24);
						// faceImgKj.setImage(imgKj);
						faceImgKj.setFaceInfo(faceInfo.getFacePosData(0));
						faceImgKj.setTime(imgInfoKj.getTime());
						log("FaceDetectTask encodeBGR24ToScaleBitmap(data, camWidth, camHeight, recWidth, recHeight)");
						//imgKj = getBitmapFromYuv420(data, camWidth, camHeight);
						FaceApp.mImgUtil.encodeBGR24toJpg(BRG24, imgInfoKj.getWidth(), imgInfoKj.getHeight(), FaceApp.registerTempImage);
						log("FaceDetectTask imgUtils.saveImage(FaceApp.registerTempImage, imgKj)");
						//imgUtils.saveImage(FaceApp.registerTempImage, imgKj);
						//if (imgKj != null && !imgKj.isRecycled()) {
						//	imgKj.recycle();
						//	imgKj = null;
						//}
						log("FaceDetectTask publishProgress(new Rect[] { rect })");
						// 上报人脸坐标
						publishProgress(new Rect[] { rect });
						isStop = true;
					} else {
						log("FaceDetectTask isRunning is false or faceInfoKJ.getRect:" + faceInfo.getRet());
					}
				} else {
					threadSleep(READ_INTERVAL);
					log("FaceDetectTask isRunning or imgInfoKj.isNew() is false, isNew:" + imgInfoKj.isNew());
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
					overlayerView.setRecognizeResult((int) (rect.left * 2 * wRate),
							(int) (rect.top * 2 * hRate),
							(int) (rect.right * 2 * wRate),
							(int) (rect.bottom * 2 * hRate));
					mHandler.sendMessage(mHandler
							.obtainMessage(MSG_FACE_POSITION));
				}
			}
		}
	}
	
	/**
	 * Yuv420转Bitmap
	 * @param data yuv420数据
	 * @param width 图像宽度
	 * @param height 图像高度
	 * @return 图像缩小一半
	 */
	public Bitmap getBitmapFromYuv420(byte[] data, int camWidth, int camHeight) {
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
						    matrix.setScale(-1, 1);
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

	public int FeatureRegister(FaceImage faceImage) {
		int ret = 0;
		if (faceImage != null && faceImage.getFaceInfo() != null) {
			Bitmap fImg = BitmapFactory.decodeFile(FaceApp.registerTempImage);
			if (fImg != null) {
				ret = FaceApp.mRecognize.registerFaceFeature(mUserId,faceImage.getBgr24(), fImg.getWidth(), fImg.getHeight(),faceImage.getFaceInfo());
				log("registerFaceFeature() userId:" + mUserId + " ret:" + ret);
				if (1 == ret) {
					String imgPath = FaceApp.userDataPath + String.format("/%08d/%08d.jpg", mUserId, mUserId);
					imgUtils.saveImage(imgPath, fImg);
					User user = new User();
					user.setUserId(mUserId);
					user.setUserName(mUserName);
					user.setUserImage(imgPath);
					user.setUserType(0);
					Calendar cal = Calendar.getInstance();
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String createTime = format.format(cal.getTime());
					user.setCreateTime(createTime);
					if (mOperateType == FaceApp.OPERATE_TYPE_REGISTER) {
					    FaceApp.mProvider.addUser(user);
					} else {
						FaceApp.mProvider.updateUserbyId(mRId, user);
					}
					ret = 1;
				} else {
					log("registerFaceFeature() ret:" + ret);
					ret = -3;
				}
				if (fImg != null && !fImg.isRecycled()) {
					fImg.recycle();
				}
			} else {
				log("fImgKj == null");
				ret = -2;
			}
		} else {
			log("faceImage == null || faceImage.getFaceInfo() == null");
			ret = -1;
		}
		return ret;
	}

	private void showToast(String msg, int time) {
		if (mToask != null) {
			mToask.cancel();
			mToask = null;
		}
		mToask = Toast.makeText(this, msg, time);
		mToask.show();
	}

	public void log(String msg) {
		Log.e(TAG, msg);
	}
}
