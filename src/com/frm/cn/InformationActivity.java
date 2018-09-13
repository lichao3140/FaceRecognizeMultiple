package com.frm.cn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class InformationActivity extends Activity implements OnClickListener {
	private final static String TAG = "InformationActivity";
	private EditText edtDeviceSerial;
	private EditText edtSoftVersion;
	
	private ImageView ivMenu;
	private LinearLayout lLayoutMenu;
	private Boolean isViewMenu = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		
		Button btnTitle = (Button)this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleInformation);
		
		edtDeviceSerial = (EditText)this.findViewById(R.id.edtDeviceSerial);
		edtSoftVersion = (EditText)this.findViewById(R.id.edtSoftVersion);
		
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
		btnSystemInfo.setEnabled(false);
        
        ImageView ivDevSn = (ImageView)this.findViewById(R.id.ivDevSn);
        ivDevSn.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		PackageManager pm = this.getPackageManager();//context为当前Activity上下文 
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String version = "";
		if (pi != null) {
			version = pi.versionName;
		}
		edtSoftVersion.setText(version);
		
		byte[] data = FaceApp.mDM2016.readDeviceSerial();
		FaceApp.mDeviceSerial = parseOutputKey(data);
		edtDeviceSerial.setText(FaceApp.mDeviceSerial);
	}

	@Override
	public void onClick(View v) {
		Intent mIntent = null;
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
			mIntent = new Intent(this, RecognizeConfigActivity.class);

			this.startActivity(mIntent);
			break;
		case R.id.ivDevSn:
			openInputKeyDialog();
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
	
	private String parseOutputKey(byte[] data) {
		if (data != null && data.length == 13) {
			StringBuilder strb = new StringBuilder();
			byte bt = 0;
			for (int i = 0; i < 13; i++) {
				bt = data[i];
				log("data[" + i + "]=" + bt);
				if (bt != 0 && bt >= '0' && bt <= '9') {
				    strb.append((char)bt);
				}
			}
			return strb.toString();
		} else {
			int result = byteToInt2(data);
			log("readDeviceSerial() fail. ret:" + result);
			return "";
		}
	}
	
	 /**
	  * 将byte数组转换为int数据
	  * @param b 字节数组
	  * @return 生成的int数据
	  */
	 public static int byteToInt2(byte[] b){
	  return (((int)b[0]) << 24) + (((int)b[1]) << 16) + (((int)b[2]) << 8) + b[3];
	 }
	
	private void openInputKeyDialog() {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		mBuilder.setTitle(R.string.txtLoginKey);
		View view = View.inflate(this, R.layout.dialog_input_key, null);
		final EditText edtInputKey = (EditText) view.findViewById(R.id.edtInputKey);
		mBuilder.setView(view);
		mBuilder.setCancelable(true);
		mBuilder.setNegativeButton(R.string.txtCancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		mBuilder.setPositiveButton(R.string.txtOK, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String strKey = edtInputKey.getText().toString();
				if (strKey.trim().equals("*#10000")) {
					Intent intent = new Intent(InformationActivity.this, SetDeviceSerialActivity.class);
					InformationActivity.this.startActivity(intent);
					dialog.cancel();
				} else {
					showToast(InformationActivity.this.getResources().getString(R.string.txtLoginKeyError));
				}
			}
		});
		mBuilder.create();
		mBuilder.show();
	}
	
	private void showToast(String msg) {
		Toast mToask = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		LinearLayout layout = (LinearLayout) mToask.getView();
		   layout.setBackgroundColor(this.getResources().getColor(R.color.white));
		   TextView v = (TextView) mToask.getView().findViewById(android.R.id.message);
		   v.setTextColor(this.getResources().getColor(R.color.red)); 
		   v.setTextSize(25);
		mToask.show();
	}
	
	public void log(String msg) {
		Log.e(TAG, msg);
	}
}
