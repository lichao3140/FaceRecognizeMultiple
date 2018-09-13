package com.frm.cn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class UserManageActivity extends Activity implements OnClickListener {
    private final static String TAG = "UserManageActivity";
	private ImageView ivMenu;
	private LinearLayout lLayoutMenu;
	private Boolean isViewMenu = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_manage);
		
		Button btnTitle = (Button)this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleUserManage);
		
		lLayoutMenu = (LinearLayout) this.findViewById(R.id.lLayoutMenu);
		ivMenu = (ImageView) this.findViewById(R.id.ivMenu);
		ivMenu.setOnClickListener(this);
		
        Button btnRegisterFace = (Button)this.findViewById(R.id.btnRegisterFace);
        btnRegisterFace.setOnClickListener(this);
        Button btnRegisterImage = (Button)this.findViewById(R.id.btnRegisterImage);
        btnRegisterImage.setOnClickListener(this);
        
        Button btnEdit = (Button)this.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);
        
        Button btnSearch = (Button)this.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        
        Button btnDelete = (Button)this.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        
        Button btnClear = (Button)this.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        
        Button btnInport = (Button)this.findViewById(R.id.btnInport);
        btnInport.setOnClickListener(this);
        
        Button btnExport = (Button)this.findViewById(R.id.btnExport);
        btnExport.setOnClickListener(this);
        
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
		btnUserManage.setEnabled(false);
	}

	@Override
	public void onClick(View view) {
		Intent mIntent;
		switch(view.getId()) {
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
		case R.id.btnRegisterFace:
			mIntent = new Intent(this, RegisterInputActivity.class);
			this.startActivity(mIntent);
			break;
		case R.id.btnRegisterImage:
			mIntent = new Intent(this, InportRegisterActivity.class);
			this.startActivity(mIntent);
			break;
		case R.id.btnEdit:
			mIntent = new Intent(this, UserEditActivity.class);
			this.startActivity(mIntent);
			break;
		case R.id.btnSearch:
			mIntent = new Intent(this, UserSearchActivity.class);
			this.startActivity(mIntent);
			break;
		case R.id.btnDelete:
			mIntent = new Intent(this, UserDeleteActivity.class);
			this.startActivity(mIntent);
			break;
		case R.id.btnClear:
			mIntent = new Intent(this, UserClearActivity.class);
			this.startActivity(mIntent);
			break;
		case R.id.btnInport:
			
			break;
		case R.id.btnExport:
			
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
    
    private void showToast(String msg) {
    	Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
    	toast.show();
    }

    private void log(String msg) {
        Log.e(TAG, msg);
    }
}
