package com.frm.cn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.face.db.User;
import com.face.sv.FaceInfo;
import com.face.util.FileDialog;
import com.face.util.ToastUtil;
import com.face.util.XmlUtil;
import com.face.util.FileDialog.FileDialogListener;
import com.frm.cn.InportRegisterActivity.InportRegisterTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class UserExportActivity extends Activity implements OnClickListener {
private final static String TAG = "UserExportActivity";
private final static int MSG_REGISTER_SUCCESS  = 0x1001;
private final static int MSG_REGISTER_FAIL  = 0x1002;
private final static int MSG_REGISTER_COMPLETED = 0x1003;
private final static int MSG_REGISTER_INFO  = 0x1004;

private EditText txtExportDir;
private EditText txtExportSize;
private EditText txtExportRecord;
private EditText txtExportModel;
private ProgressBar progressBar;
private TextView txtMsg;
private String mExportDir = null;
private int mExportSize = 0;
private File[] mImgList = null;

Button btnNext;

private ImageUtils imgUtils = new ImageUtils();

private boolean isStop = false;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_user_export);
	
	Button btnBack = (Button) this.findViewById(R.id.btnBack);
	btnBack.setOnClickListener(this);
	btnBack.setText(R.string.txtBack);
	Button btnTitle = (Button)this.findViewById(R.id.btnTitle);
	btnTitle.setText(R.string.titleUserExport);
	btnNext = (Button) this.findViewById(R.id.btnNext);
	btnNext.setOnClickListener(this);
	btnNext.setText(R.string.txtExportUser);

	txtExportDir = (EditText)this.findViewById(R.id.edtExportDir);
	txtExportSize = (EditText)this.findViewById(R.id.edtExportSize);
	txtExportRecord = (EditText)this.findViewById(R.id.edtExportRecord);
	txtExportModel = (EditText)this.findViewById(R.id.edtExportModel);
	txtMsg = (TextView)this.findViewById(R.id.txtMsg);
	progressBar = (ProgressBar)this.findViewById(R.id.progressBar);
	
	Button btnSelect = (Button)this.findViewById(R.id.btnSelect);
	btnSelect.setOnClickListener(this);
}

@Override
protected void onDestroy() {
	super.onDestroy();
}

@Override
protected void onPause() {
	super.onPause();
}

@Override
protected void onResume() {
	super.onResume();
	
	btnNext.setEnabled(true);
	mExportSize = 0;
	txtExportSize.setText("0");
	txtExportRecord.setText("0");
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
		startExportUser(mExportDir);
		break;
	case R.id.btnSelect:
		FileDialog dialog = new FileDialog.Builder(UserExportActivity.this)
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
	                	mExportDir = dir.getAbsolutePath();
	                	txtExportDir.setText(mExportDir);
	                	log("onFileSelected() mExportDir:" + mExportDir);
	                }
	            }
	        }

	        @Override
	        public void onFileCanceled() {
	            ToastUtil.showToast(UserExportActivity.this, "Copy Cancelled!");
	        }
	    }).create(UserExportActivity.this);
	    dialog.show();
		break;
	default:
		break;
	}
}

void startExportUser(String exportDir) {
	log("startExportUser() exportDir:" + exportDir);
	if (exportDir == null || TextUtils.isEmpty(exportDir)) {
		log("startExportRegister exportDir is null.");
		return;
	}
	File dir = new File(exportDir);
	if (dir == null || !dir.isDirectory()) {
		log("startExportUser exportDir is not exist.");
		dir.mkdirs();
	}
	mExportSize = FaceApp.mProvider.quaryUserTableRowCount();
	txtExportSize.setText(String.valueOf(mExportSize));
	txtExportRecord.setText("0");
	progressBar.setMax(mExportSize);
	progressBar.setProgress(0);
	if (mExportSize <= 0) {
		showToast("当前没有需要导出用户（" + mExportSize + "）。");
	} else {
	    isStop = false;
	    btnNext.setEnabled(false);
    	ExportUserTask exportTask = new ExportUserTask();
	    exportTask.execute();
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
		case MSG_REGISTER_INFO:
            switch(msg.arg2) {
            case 1:
            	txtExportRecord.setText(msg.arg1 + "(条)");
            	break;
            case 2:
            	break;
            	default:
            		break;
            }
			break;
		default:
			break;
		}
	}
};

/**
 * 定义一个类，让其继承AsyncTask这个类
 */
class ExportUserTask extends AsyncTask<Void, Integer, Void> {
    private boolean isStop = false;

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	@Override
	protected Void doInBackground(Void... params) {
		log("ExportUserTask doInBackground() start");
		String exportDir = mExportDir;
		String srcDir = FaceApp.userDataPath;
		List<User> users = FaceApp.mProvider.queryUsers();
		if (users == null || users.size() == 0) {
			return null;
		}
		File dir = new File(exportDir);
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		String xmlPath = exportDir + "/users.xml";
		File file = new File(xmlPath);
		if (file != null && file.exists()) {
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int ret = 0;
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			XmlUtil util = new XmlUtil();
			ret = util.exportUsersToXml(users, output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		mHandler.sendMessage(mHandler.obtainMessage(MSG_REGISTER_INFO, ret, 1));
		log("ExportUserTask doInBackground() end");
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		// 更新ProgressDialog的进度条
        if (values != null && values.length > 0) {
        	int progress = values[0] + 1;
        	txtExportRecord.setText(String.valueOf(progress));
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