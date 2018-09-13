package com.frm.cn;

import com.face.db.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterResultActivity extends Activity implements OnClickListener {
    private final static String TAG = "RegisterResultActivity";
    private final static int    ACTIVITY_TIMEOUT = 0x1001;
    
    private final static int    ACTIVITY_TIMEOUT_DELAY = 5000;
    
	private int  mUserId = -1;
	private User mUser = null;
	private int mOperateType = 0;

	private EditText edtRecordId;
	private EditText edtUserId;
	private EditText edtUserName;
	private EditText edtCreateTime;
	private ImageView imgUserImage;
	private Button btnTitle = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_result);
		
		btnTitle = (Button)this.findViewById(R.id.btnTitle);
		Button btnNext = (Button) this.findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		btnNext.setText(R.string.txtClose);
		
		edtRecordId = (EditText)this.findViewById(R.id.edtRecordId);
		edtUserId = (EditText)this.findViewById(R.id.edtUserId);
		edtUserName = (EditText)this.findViewById(R.id.edtUserName);
		edtCreateTime = (EditText)this.findViewById(R.id.edtCreateTime);
		imgUserImage = (ImageView)this.findViewById(R.id.imgUserImage);
	}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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
		Intent intent = this.getIntent();
		if (intent != null) {
			mUserId = intent.getIntExtra(FaceApp.KEY_USER_ID, -1);
			mOperateType = intent.getIntExtra(FaceApp.KEY_OPERATE_TYPE, FaceApp.OPERATE_TYPE_REGISTER);
			if (mOperateType == FaceApp.OPERATE_TYPE_REGISTER) {
				btnTitle.setText(R.string.titleRegisterComplete);
			} else {
				btnTitle.setText(R.string.titleEditComplete);
			}
			if (mUserId < 0) {
				showToast("UserId is uninvalid.\r\nRecognizeResultActivity finish.");
				log("UserId is uninvalid.\r\nRecognizeResultActivity finish.");
				this.finish();
			}
		} else {
			showToast("getIntent() is null.\r\nRecognizeResultActivity finish.");
			log("getIntent() is null. RecognizeResultActivity finish.");
			this.finish();
		}
		
		if (mUserId >= 0) {
			mUser = FaceApp.mProvider.getUserByUserId(mUserId);
			if (mUser != null) {
				edtRecordId.setText(String.valueOf(mUser.getId()));
				edtUserId.setText(String.valueOf(mUser.getUserId()));
				edtUserName.setText(mUser.getUserName());
				edtCreateTime.setText(mUser.getCreateTime());
			}
			String userPath = String.format("%s/%08d/%08d.jpg", FaceApp.userDataPath, mUserId, mUserId);
			imgUserImage.setImageBitmap(new BitmapFactory().decodeFile(userPath));
		} else {
			imgUserImage.setImageResource(R.drawable.nobody);
		}
		mHandler.sendEmptyMessageDelayed(ACTIVITY_TIMEOUT, ACTIVITY_TIMEOUT_DELAY);
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == ACTIVITY_TIMEOUT) {
				Intent intent = null;
				if (mOperateType == FaceApp.OPERATE_TYPE_REGISTER) {
					intent = new Intent(RegisterResultActivity.this, MainActivity.class);
					RegisterResultActivity.this.startActivity(intent);
				} else if (mOperateType == FaceApp.OPERATE_TYPE_UPDATE_INFO) {

				} else {
					intent = new Intent(RegisterResultActivity.this, UserEditActivity.class);
					RegisterResultActivity.this.startActivity(intent);
				}
				RegisterResultActivity.this.finish();
			}
		}
	};

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btnNext:
			if (mOperateType == FaceApp.OPERATE_TYPE_REGISTER) {
				intent = new Intent(RegisterResultActivity.this, MainActivity.class);
				RegisterResultActivity.this.startActivity(intent);
			} else if (mOperateType == FaceApp.OPERATE_TYPE_UPDATE_INFO) {

			} else {
				intent = new Intent(RegisterResultActivity.this, UserEditActivity.class);
				RegisterResultActivity.this.startActivity(intent);
			}
			this.finish();
		default:
			break;
		}
	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void log(String msg) {
		Log.e(TAG, msg);
	}
}
