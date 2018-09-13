package com.frm.cn;

import java.nio.charset.Charset;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetDeviceSerialActivity extends Activity implements OnClickListener {
	private final static String TAG = "SetDeviceSerialActivity";
	private EditText edtDeviceSerial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_device_serial);
		
		Button btnTitle = (Button)this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleDevSerialSet);
		
		edtDeviceSerial = (EditText)this.findViewById(R.id.edtDeviceSerial);
		
        Button btnComit = (Button)this.findViewById(R.id.btnComit);
        btnComit.setOnClickListener(this);
        Button btnCancel = (Button)this.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		edtDeviceSerial.setText(FaceApp.mDeviceSerial);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnComit:
			String strDevSn = edtDeviceSerial.getText().toString().trim();
			if (strDevSn == null || strDevSn.length() == 0) {
				showToast(this.getResources().getString(R.string.txtDevSnEmpty));
			}
			byte[] devSn = strDevSn.getBytes(Charset.forName("UTF-8"));
			int ret = CheckSN(devSn);
			log("CheckSN(devSn) ret:" + ret);
			if(ret == 1) {
				ret = FaceApp.mDM2016.writeDeviceSerial(devSn);
				log("FaceApp.mDetect.writeDeviceSerial(devSn) ret:" + ret);
				if (ret == 0) {
					FaceApp.mDeviceSerial = strDevSn;
				    showToast(this.getResources().getString(R.string.txtDevSnSuccess));
				} else {
					showToast(this.getResources().getString(R.string.txtDevSnFail));
				}
			} else {
				showToast(this.getResources().getString(R.string.txtDevSnInvalid));
			}
			break;
		case R.id.btnCancel:
			this.finish();
			break;
			default:
				break;
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
	
	//SN合法返回1，不合法返回0
	public int CheckSN(byte[] pSn)
	{
	    if(pSn.length == 13) 
	    {
			int i = 0;
			int sum = 0;
			for(i=0; i<12; i++) 
			{
			    if((pSn[i]<'0') || (pSn[i]>'9')) 
			    {
			        sum = -1;
			        break;
			    }
			    sum += (pSn[i]-'0')*(((i&1)!=0)?3:1);
			}
			
			if(sum>=0) 
			{
				int val1 = (pSn[i]-'0');
				int val2 = ((10-(sum%10))%10);
				log("val1=" + val1 + " val2=" + val2);
			    if(val1 == val2) 
			    {
			    	return 1;
			    }
			}
	    }
	    return 0;
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
