package com.frm.cn;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.face.db.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterInputActivity extends Activity implements OnClickListener {
	private final static String TAG = "UserRegisterActivity";
	private EditText txtId;
	private EditText txtUserId;
	private EditText txtUserName;
	private Button btnTitle;
	private Button btnNext;
	
	private int mId = -1;
	private int mUserId = 0;
	private String mUserName = "";
	private int mOperateType = 0;  // 操作类型， 0表示注册， 0x1001表示修改
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_input);
		
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnBack.setText(R.string.txtBack);
		btnTitle = (Button)this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleRegisterInput);
		btnNext = (Button) this.findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		btnNext.setText(R.string.txtNext);
		
		txtId = (EditText) this.findViewById(R.id.editId);
		txtUserId = (EditText) this.findViewById(R.id.editUserId);
		txtUserName = (EditText) this.findViewById(R.id.editUserName);

	}
	
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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
		
		//mIntent.putExtra(FaceApp.KEY_OPERATE_TYPE, 0x1001);
		//mIntent.putExtra(FaceApp.KEY_USER_RID, mCurrentUser.getId());
		Intent intent = this.getIntent();
		if (intent != null) {
			mId = intent.getIntExtra(FaceApp.KEY_USER_RID, -1);
			mOperateType = intent.getIntExtra(FaceApp.KEY_OPERATE_TYPE, FaceApp.OPERATE_TYPE_REGISTER);
		}
		log("onResume() mOperateType:" + mOperateType + " mId:" + mId);
		if (mOperateType == FaceApp.OPERATE_TYPE_REGISTER) {
			mId = -1;
		    int sum = FaceApp.mProvider.quaryUserTableRowCount();
		    if (sum >= FaceApp.MAX_USER_NUMBER) {
			    btnNext.setEnabled(false);
			    showToast("已达到用户上限（" + FaceApp.MAX_USER_NUMBER + "）人,无法继续添加用户。");
		    } else {
			    mId = FaceApp.mProvider.getMaxId() + 1;
			    txtId.setText(String.valueOf(mId));
			    mUserId = FaceApp.mProvider.getMaxUserId() + 1;
	            txtUserId.setText(String.valueOf(mUserId));
	            txtUserName.setText(String.format("%08d", mUserId));
		    }
		} else if (mOperateType == FaceApp.OPERATE_TYPE_UPDATE_INFO || mOperateType == FaceApp.OPERATE_TYPE_UPDATE_ALL) {
			if (mOperateType == FaceApp.OPERATE_TYPE_UPDATE_INFO) {
			    btnTitle.setText(R.string.titleUserInfoEdit);
			    btnNext.setText(R.string.txtEditUser);
			} else {
				btnTitle.setText(R.string.titleUserAllEdit);
				btnNext.setText(R.string.txtNext);
			}
			if (mId >= 0) {
				txtId.setText(String.valueOf(mId));
			    User user = FaceApp.mProvider.getUserById(mId);
			    if (user != null) {
			    	mUserId = user.getUserId();
			    	mUserName = user.getUserName();
		            txtUserId.setText(String.valueOf(mUserId));
		            txtUserName.setText(mUserName);
			    } else {
			    	showToast("未找到更新用户的记录id,请确认该记录是否存在！");
			    }
			} else {
				showToast("无效的记录id！");
			}
		} else {

		}
		
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
			if (checkInputData()) {
				if (mOperateType == FaceApp.OPERATE_TYPE_UPDATE_INFO) {
					User user = new User();
					user.setUserId(mUserId);
					user.setUserName(mUserName);
					user.setUserType(0);
					FaceApp.mProvider.updateUserSubById(mId, user);
					Intent intent = new Intent(this, RegisterResultActivity.class);
					intent.putExtra(FaceApp.KEY_USER_ID, mUserId);
					intent.putExtra(FaceApp.KEY_OPERATE_TYPE, mOperateType);
					this.startActivity(intent);
					this.finish();
				} else {
					Intent intent = new Intent(this, UserRegisterActivity.class);
					intent.putExtra(FaceApp.KEY_USER_RID, mId);
					intent.putExtra(FaceApp.KEY_USER_ID, mUserId);
					intent.putExtra(FaceApp.KEY_USER_NAME, mUserName);
					intent.putExtra(FaceApp.KEY_OPERATE_TYPE, mOperateType);
					startActivity(intent);
				}

			}
			break;
			default:
				break;
		}
		
	}
	
	private boolean checkInputData() {
		String strUserId = txtUserId.getText().toString();
		if (strUserId == null || TextUtils.isEmpty(strUserId)) {
			showToast("用户编号不能为空！");
			return false;
		}
		if (mUserId > 99999999) {
			showToast("用户编号最大为 8位数字！");
			return false;
		}
		String strUserName = txtUserName.getText().toString();
		if (strUserName == null || TextUtils.isEmpty(strUserName)) {
			showToast("用户名称不能为空！");
			return false;
		}
		if (mUserName.trim().length() > 32) {
			showToast("用户名称最长 32位！");
			return false;
		}
		int userId = -1;
		try {
		userId = Integer.valueOf(strUserId);
		} catch(NumberFormatException ex) {
			ex.printStackTrace();
			showToast("用户编号由数字组成！");
			return false;
		}
		mUserId = userId;
		mUserName = strUserName.trim();
		return true;
	}
	
	
	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void log(String msg) {
		Log.e(TAG, msg);
	}
}
