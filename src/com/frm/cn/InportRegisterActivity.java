package com.frm.cn;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.face.db.User;
import com.face.sv.FaceInfo;
import com.face.util.FileDialog;
import com.face.util.FileDialog.FileDialogListener;
import com.face.util.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class InportRegisterActivity extends Activity implements OnClickListener {
	private final static String TAG = "InportRegisterActivity";
	private final static int MSG_REGISTER_SUCCESS  = 0x1001;
	private final static int MSG_REGISTER_FAIL  = 0x1002;
	private final static int MSG_REGISTER_COMPLETED = 0x1003;
	
	private EditText txtInportDir;
	private EditText txtInportSize;
	private EditText txtInportProgress;
	private ProgressBar progressBar;
	private TextView txtMsg;
	private String mInportDir = null;
	private int mInportSize = 0;
	private File[] mImgList = null;
	
	Button btnNext;
	
	private ImageUtils imgUtils = new ImageUtils();
	
	private boolean isStop = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inport_register);
		
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnBack.setText(R.string.txtBack);
		Button btnTitle = (Button)this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleUserRegister);
		btnNext = (Button) this.findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		btnNext.setText(R.string.txtInportImage);
		
		txtInportDir = (EditText)this.findViewById(R.id.edtInportDir);
		txtInportSize = (EditText)this.findViewById(R.id.edtInportSize);
		txtInportProgress = (EditText)this.findViewById(R.id.edtInportProgress);
		txtMsg = (TextView)this.findViewById(R.id.txtMsg);
		progressBar = (ProgressBar)this.findViewById(R.id.progressBar);
		
		Button btnSelect = (Button)this.findViewById(R.id.btnSelect);
		btnSelect.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		btnNext.setEnabled(true);
		mInportSize = 0;
		txtInportSize.setText("0");
		txtInportProgress.setText("0");
		txtMsg.setText("");
		progressBar.setMax(0);
		progressBar.setProgress(0);
	}



	@Override
	public void onClick(View view) {
		Intent mIntent;
		switch (view.getId()) {
		case R.id.btnBack:
			mIntent = new Intent(this, UserManageActivity.class);

			this.startActivity(mIntent);
			this.finish();
			break;
		case R.id.btnNext:
			startInportRegister(mInportDir);
			break;
		case R.id.btnSelect:
			FileDialog dialog = new FileDialog.Builder(InportRegisterActivity.this)
		    .setFileMode(FileDialog.FILE_MODE_OPEN_FOLDER_SINGLE)
		    .setCancelable(true).setCanceledOnTouchOutside(false)
		    .setTitle("selectFolder")
		    .setFileSelectListener(new FileDialogListener() {

		        @Override
		        public void onFileSelected(ArrayList<File> files) {
		        	log("onFileSelected() fileSize:" + files.size());
		            if (files.size() > 0) {
		                File dir = files.get(0);
		                log("onFileSelected() filedir:" + dir.getAbsolutePath());
		                if (dir != null && dir.isDirectory()) {
		                	mInportDir = dir.getAbsolutePath();
		                	txtInportDir.setText(mInportDir);
		                	log("onFileSelected() mInportDir:" + mInportDir);
		                }
		            }
		        }

		        @Override
		        public void onFileCanceled() {
		            ToastUtil.showToast(InportRegisterActivity.this, "Copy Cancelled!");
		        }
		    }).create(InportRegisterActivity.this);
		    dialog.show();
			break;
		default:
			break;
		}
	}
	
	void startInportRegister(String inportDir) {
		log("startInportRegister() inportDir:" + inportDir);
		if (inportDir == null || TextUtils.isEmpty(inportDir)) {
			log("startInportRegister inportDir is null.");
			return;
		}
		File dir = new File(inportDir);
		if (dir == null || !dir.isDirectory()) {
			log("startInportRegister inportDir is not exist.");
			return;
		}
		mImgList = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
                if (name.endsWith(".jpg") || name.endsWith(".Jpg") || name.endsWith(".JPG")){  
                    return true;  
                } else {  
                    return false;  
                }  
			}
		});
		mInportSize = mImgList.length;
		txtInportSize.setText(String.valueOf(mInportSize));
		txtInportProgress.setText("0");
		progressBar.setMax(mInportSize);
		progressBar.setProgress(0);
		
		int sum = FaceApp.mProvider.quaryUserTableRowCount();
		if (mInportSize + sum > FaceApp.MAX_USER_NUMBER) {
			showToast("当前导入图像数量超过了最大用户数量（" + FaceApp.MAX_USER_NUMBER + "）人,无法完成用户添加。");
		} else {
		    isStop = false;
		    btnNext.setEnabled(false);
	    	InportRegisterTask inportTask = new InportRegisterTask();
		    inportTask.execute();
		}
	}
	
    @SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_REGISTER_SUCCESS:
				mHandler.removeMessages(MSG_REGISTER_SUCCESS);
				
				int index = msg.arg1;
				int userId = msg.arg2;
				String imgName = (String)msg.obj;
			    txtMsg.setText("第" + index + "个图像:" +imgName + "注册成功。\r\n成功注册为userId:" + userId);
			    break;
			case MSG_REGISTER_FAIL:
				int pos = msg.arg1;
				int type = msg.arg2;
				String name = (String)msg.obj;
				if (type == 0x1001) {
			        txtMsg.setText("第" + pos + "个图像:" + name + " 注册失败。 \r\n失败原因为模型注册失败。");
				} else {
					if (type == -103) {
					    txtMsg.setText("第" + pos + "个图像:" + name + " 注册失败。\r\n失败原因为没有检测到人脸。");
					} else if (type == -102) {
						txtMsg.setText("第" + pos + "个图像:" + name + " 注册失败。\r\n失败原因为检测到的人脸无效。");
					} else {
						txtMsg.setText("第" + pos + "个图像:" + name + " 注册失败。\r\n失败原因为人脸检测失败。");
					}
				}
				break;
			case MSG_REGISTER_COMPLETED:
				int sum = msg.arg1;
				int size = msg.arg2;
				String str = "图像批量注册用户完成，导入" + sum + "个用户。";
				txtMsg.setText(str);
				showToast(str);
				btnNext.setEnabled(true);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 定义一个类，让其继承AsyncTask这个类
	 */
	class InportRegisterTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			log("InportRegisterTask doInBackground() start");
			if (mImgList == null || mImgList.length <= 0) {
				log("InportRegisterTask mImgList is null");
				return null;
			}
			String imgName = null;
			File imgFile = null;
			int    userId = -1;
			byte[] data = null;
			Bitmap img = null;
			byte[] BGR24 = null;
			FaceInfo faceInfo = null;
			int ret = 0;
			String filePath = null;
			int size = mImgList.length;
			int sum = 0;
			log("mImgList.length:" + size);
			for (int i = 0; i < size; i++) {
				// 上报人脸坐标
				imgFile = mImgList[i];
				imgName = imgFile.getName();
				log("mImgList[" + i + "]:" + imgFile.getAbsolutePath());
				img = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				BGR24 = imgUtils.getBGR24FromBitmap(img);
				log("InportRegisterTask mDetect.getFacePositionFromBGR24()");
				faceInfo = FaceApp.mDetect.faceDetectMaster(BGR24, img.getWidth(), img.getHeight(), FaceApp.TYPE_FACE_REGISTER, FaceApp.DEFAULT_DETECT_PADDING);
				log("mRecognize.faceDetectMaster() faceInfo.getRec:" + faceInfo.getRet());
				if (!isStop && faceInfo.getRet() > 0) {
					userId = FaceApp.mProvider.getMaxId() + 1;
					ret = FaceApp.mRecognize.registerFaceFeature(userId, BGR24, img.getWidth(), img.getHeight(), faceInfo.getFacePosData(0));
					log("registerFaceFeature() userId:" + userId + " ret:" + ret);
					if (1 == ret) {
						filePath = FaceApp.userDataPath + String.format("/%08d/%08d.jpg", userId, userId);
						imgUtils.saveScaleImage(filePath, img, FaceApp.IMAGE_SAVE_WIDTH);
						User user = new User();
						user.setUserId(userId);
						user.setUserName( String.format("%08d", userId));
						user.setUserImage(filePath);
						user.setUserType(0);
						Calendar cal = Calendar.getInstance();
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String createTime = format.format(cal.getTime());
						user.setCreateTime(createTime);
						FaceApp.mProvider.addUser(user);
						mHandler.sendMessage(mHandler.obtainMessage(MSG_REGISTER_SUCCESS, i + 1, userId, imgName));
						sum++;
					} else {
						mHandler.sendMessage(mHandler.obtainMessage(MSG_REGISTER_FAIL, i + 1, 0x1001, imgName));
					}
				} else {
					log("InportRegisterTask isStop is true or faceInfoKJ.getRect:" + faceInfo.getRet());
					mHandler.sendMessage(mHandler.obtainMessage(MSG_REGISTER_FAIL, i + 1, faceInfo.getRet(), imgName));
				}
				publishProgress(i);
				if (img != null && !img.isRecycled()) {
					img.recycle();
				}
			}
			mHandler.sendMessage(mHandler.obtainMessage(MSG_REGISTER_COMPLETED, sum, size));
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// 更新ProgressDialog的进度条
            if (values != null && values.length > 0) {
            	int progress = values[0] + 1;
            	txtInportProgress.setText(String.valueOf(progress));
            	progressBar.setProgress(progress);
            }
		}
	}

private void showToast(String msg) {
	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
}

public void log(String msg) {
	Log.e(TAG, msg);
}
}