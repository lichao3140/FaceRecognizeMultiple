package com.frm.cn;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RecognizeConfigActivity extends Activity implements OnClickListener {

	private final static String TAG = "RecognizeConfigActivity";
    private EditText edtRecognizeLimit;
    private EditText edtSameTime;
    
	private ImageView ivMenu;
	private LinearLayout lLayoutMenu;
	private Boolean isViewMenu = false;
    
    private Toast toast = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recognize_config);

		Button btnTitle = (Button)this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleRecognizeConfig);
		
        Button btnCommit = (Button)this.findViewById(R.id.btnCommit);
        btnCommit.setOnClickListener(this);
        Button btnCancel = (Button)this.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        
		lLayoutMenu = (LinearLayout) this.findViewById(R.id.lLayoutMenu);
		ivMenu = (ImageView) this.findViewById(R.id.ivMenu);
		ivMenu.setOnClickListener(this);
		
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
		btnSystemConfig.setEnabled(false);
        edtRecognizeLimit = (EditText)this.findViewById(R.id.edtRecognizeLimit);
        edtSameTime = (EditText)this.findViewById(R.id.edtSameTime);
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
    	if (toast != null) {
    		toast.cancel();
    		toast = null;
    	}
	}

	@Override
	protected void onResume() {
		super.onResume();
        
        loadConfig();
        loadView();
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
		case R.id.btnCommit:
			if (checkInputValid()) {
				saveConfig();
				loadView();
				showToast("人脸识别参数配置成功.");
			}
			break;
		case R.id.btnCancel:
	        loadConfig();
	        loadView();
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

    private boolean checkInputValid() {
    	int value = 0;
    	String limit = edtRecognizeLimit.getText().toString();
    	value = Integer.valueOf(limit);
    	if (value <= 0 || value >= 100) {
    		
    		showToast("识别门限取值范围0~100。");
    		return false;
    	}
    	return true;
    }
	
    private void loadView() {
        edtRecognizeLimit.setText(String.valueOf(FaceApp.mRecognizeLimit));
        edtSameTime.setText(String.valueOf(FaceApp.mSameTime));
    }
    
    /**
     * 加载人脸相似度门限数据
     */
    private void loadConfig() 
    {
        SharedPreferences pre = this.getSharedPreferences("config", MODE_PRIVATE);
        FaceApp.mRecognizeLimit = pre.getInt(FaceApp.KEY_RECOGNIZE_LIMIT, FaceApp.DEFAULT_RECOGNIZE_LIMIT);
        FaceApp.mSameTime = pre.getInt(FaceApp.KEY_SAME_TIME, FaceApp.DEFAULT_SAME_TIME);
    }

    /**
     * 保存人脸相似度门限设置
     */
    private void saveConfig() 
    {
        SharedPreferences pre = this.getSharedPreferences("config", MODE_PRIVATE);
        Editor edt = pre.edit();
        String limit = edtRecognizeLimit.getText().toString();
        try {
        	FaceApp.mRecognizeLimit = Integer.valueOf(limit);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            FaceApp.mRecognizeLimit = FaceApp.DEFAULT_RECOGNIZE_LIMIT;
        }
        edt.putInt(FaceApp.KEY_RECOGNIZE_LIMIT, FaceApp.mRecognizeLimit);
        
        String stime = edtSameTime.getText().toString();
        try {
        	FaceApp.mSameTime = Integer.valueOf(stime);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            FaceApp.mSameTime = FaceApp.DEFAULT_SAME_TIME;
        }
        
        edt.putInt(FaceApp.KEY_SAME_TIME, FaceApp.mSameTime);
        edt.commit();
    }
    
    
    private void showToast(String msg) {
    	if (toast != null) {
    		toast.cancel();
    		toast = null;
    	}
    	toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
    	toast.show();
    }
    
    // 打印log
    public static void log(String msg)
    {
        Log.e(TAG, msg);
    }
}

