package com.frm.cn;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class UserClearActivity extends Activity implements OnClickListener {
	private final static String TAG = "UserClearActivity";
	private final static int MSG_CLEAR_COMPLETED = 0x1001;
	private final static int MSG_CLEAR_UPDATE= 0x1002;
	
	private Toast mToask = null;
	
	private EditText edtUserCount;
	private EditText edtCurrentIndex;
	private ProgressBar progressBar;
	private TextView  txtMsg;
	
	private int mCount = 0;
	private int mIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_clear);
		
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnBack.setText(R.string.txtBack);
		Button btnTitle = (Button)this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleUserClear);
		Button btnNext = (Button) this.findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		btnNext.setText(R.string.txtClear);
		
		edtUserCount = (EditText)this.findViewById(R.id.edtUserCount);
		edtCurrentIndex = (EditText)this.findViewById(R.id.edtCurrentIndex);
		progressBar = (ProgressBar)this.findViewById(R.id.progressBar);
		txtMsg = (TextView)this.findViewById(R.id.txtMsg);
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
		initView();
	}

	private void initView() {
		mIndex = 0;
		mCount = FaceApp.mProvider.quaryUserTableRowCount();
		edtUserCount.setText(String.valueOf(mCount));
		edtCurrentIndex.setText(String.valueOf(mIndex));
		progressBar.setMax(mCount);
		progressBar.setProgress(mIndex);
		txtMsg.setText("请按右上角清空按钮开始清理用户！");
	}

	@Override
	public void onClick(View view) {
		Intent mIntent;
		switch(view.getId()) {
		case R.id.btnBack:
			mIntent = new Intent(this, UserManageActivity.class);
			this.startActivity(mIntent);
			this.finish();
			break;
		case R.id.btnNext:
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("清空记录")
	               .setMessage("是否清空所有用户？")
	               .setPositiveButton("确定", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   mIndex = 0;
	                	   txtMsg.setText("");
	                	   FaceApp.mRecognize.clearAllFaceFeature();
	                	   FaceApp.mProvider.deleteAllUser();
	                	   new Thread() {
							@Override
							public void run() {
								super.run();
								deleteFilesWithPath(FaceApp.userDataPath, false);
								mHandler.sendEmptyMessage(MSG_CLEAR_COMPLETED);
							} 
	                	   }.start();
	                   }
	               })
	               .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   dialog.cancel();
	                   }
	               });
	        builder.create();
	        builder.show();
			break;
			default:
				break;
		}
	}
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_CLEAR_COMPLETED:
				txtMsg.setText("清空用户完成。");
				edtCurrentIndex.setText(String.valueOf(mCount));
				progressBar.setProgress(mCount);
				showToast("用户清空完成！");
				break;
			case MSG_CLEAR_UPDATE:
				edtCurrentIndex.setText(String.valueOf(mIndex));
				progressBar.setProgress(mIndex);
				break;
			default:
				break;
			}
		}
	};
    
    public void deleteFilesWithPath(String dirPath, boolean delRoot) {
    	File dir = new File(dirPath);
    	if (dirPath == null || !dir.exists()) {
    		return;
    	}
    	String fileName = null;
    	File[] files = null;
    	if (dir.isFile()) {
    		dir.delete();
    	}
        if(dir.isDirectory()) {
        	files = dir.listFiles();
        	if (files != null && files.length > 0) {
            	for (File file : files) {
            		fileName = file.getName();
            		if (fileName.equals(".") || fileName.equals("..")) {
            			continue;
            		}
            		if (file.isFile()) {
            			file.delete();
            			mIndex++;
            			mHandler.sendEmptyMessage(MSG_CLEAR_UPDATE);
            		}
            		if (file.isDirectory()) {
            			deleteFilesWithPath(file.getAbsolutePath(), true);
            		}
            	}
        	}
        	if (delRoot) {
        	    dir.delete();
        	}
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
