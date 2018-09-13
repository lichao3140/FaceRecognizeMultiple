package com.frm.cn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.face.db.Record;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RecordListActivity extends Activity implements OnClickListener, OnItemClickListener {

    private final static String TAG = "RecordListActivity";
    private final static int PAGE_ROW_COUNT = 10;
    private ListView lvRecords;
    private ArrayList<Map<String, Object>> faceList = new ArrayList<Map<String,Object>>();
    private SimpleAdapter mAdapter = null;
    private List<Record> listRecords = null;
    private int pageIndex = 1;
    private int pageCount = 0;
    
    private Record mCurrentRecord = null;
    
    private TextView txtPageIndex;
    private TextView txtPageCount;
    private TextView txtRecordCount;
    private ImageView ivUserImg;
    private ImageView ivRecImg;
    
    private TextView txtUserInfo;
    private TextView txtRecInfo;
    
	private ImageView ivMenu;
	private LinearLayout lLayoutMenu;
	private Boolean isViewMenu = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_list);
		
		Button btnTitle = (Button)this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleRecordManage);
		Button btnBack = (Button)this.findViewById(R.id.btnBack);
		btnBack.setText(R.string.txtClearAllRecords);
		btnBack.setOnClickListener(this);
		Button btnNext = (Button)this.findViewById(R.id.btnNext);
		btnNext.setText(R.string.txtClearCurrentRecords);
		btnNext.setOnClickListener(this);
		
		lLayoutMenu = (LinearLayout) this.findViewById(R.id.lLayoutMenu);
		ivMenu = (ImageView) this.findViewById(R.id.ivMenu);
		ivMenu.setOnClickListener(this);
		
		lvRecords = (ListView)this.findViewById(R.id.lvRecords);
		lvRecords.setOnItemClickListener(this);
		lvRecords.setSelector(R.drawable.btn_ok_pressed);
		lvRecords.setSelectionAfterHeaderView();
		lvRecords.addHeaderView(LayoutInflater.from(this).inflate( R.layout.user_list_header, null));
        
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
		btnRecordManage.setEnabled(false);
        
		txtUserInfo = (TextView)this.findViewById(R.id.txtUserInfo);
		txtRecInfo = (TextView)this.findViewById(R.id.txtRecInfo);

        ivUserImg = (ImageView)this.findViewById(R.id.ivUserImg);
        ivRecImg = (ImageView)this.findViewById(R.id.ivRecImg);
        
        txtPageIndex = (TextView)this.findViewById(R.id.txtPageIndex);
        txtPageCount = (TextView)this.findViewById(R.id.txtPageCount);
        txtRecordCount = (TextView)this.findViewById(R.id.txtRecordCount);
        ImageView btnUp = (ImageView)this.findViewById(R.id.ivPageUp);
        btnUp.setOnClickListener(this);
        ImageView btnDown = (ImageView)this.findViewById(R.id.ivPageDown);
        btnDown.setOnClickListener(this);
        
        mAdapter = new SimpleAdapter(this, faceList,R.layout.record_list_context,
                new String[]{"id","userId", "userName","createTime"},
                new int[]{R.id.txtId, R.id.txtUserId, R.id.txtUserName, R.id.txtCreateTime});
        lvRecords.setAdapter(mAdapter);
	}
    
    private void initView() {
    	txtUserInfo.setText("");
        txtRecInfo.setText("");
        ivUserImg.setImageResource(R.drawable.nobody);
        ivRecImg.setImageResource(R.drawable.nobody);
        txtPageIndex.setText("0");
        txtPageCount.setText("0");
        txtRecordCount.setText("0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentRecord = null;
        
        initView();
        
        updatePageData(1);
        mAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetInvalidated();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
		case R.id.ivPageUp:
			if (1 < pageIndex && pageIndex <= pageCount) {
				pageIndex--;
			} else {
				pageIndex = pageCount;
			}
			txtPageIndex.setText(String.valueOf(pageIndex));
			updatePageData(pageIndex);
			mAdapter.notifyDataSetChanged();
			mAdapter.notifyDataSetInvalidated();
			break;
		case R.id.ivPageDown:
			if (1 <= pageIndex && pageIndex < pageCount) {
				pageIndex++;
			} else {
				pageIndex = 1;
			}
			txtPageIndex.setText(String.valueOf(pageIndex));
			updatePageData(pageIndex);
			mAdapter.notifyDataSetChanged();
			mAdapter.notifyDataSetInvalidated();
			break;
		case R.id.btnBack:
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("清空记录")
	               .setMessage("是否清楚所有记录？")
	               .setPositiveButton("确定", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   FaceApp.mProvider.deleteAllRecord();
	                	   new Thread() {
							@Override
							public void run() {
								super.run();
								deleteFilesWithPath(FaceApp.recordImagePath);
							} 
	                	   }.start();
	                	   initView();
	                       updatePageData(1);
	                       mAdapter.notifyDataSetChanged();
	                       mAdapter.notifyDataSetInvalidated();
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
		case R.id.btnNext:
			if (mCurrentRecord != null && mCurrentRecord.getId() >= 0) {
		        AlertDialog.Builder sbuilder = new AlertDialog.Builder(this);
		        sbuilder.setTitle("删除记录")
		               .setMessage("是否删除当前记录？")
		               .setPositiveButton("确定", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   FaceApp.mProvider.deleteRecordById(mCurrentRecord.getId());
		                	   String path = mCurrentRecord.getRecImage();
		                	   File file = new File(path);
		                	   File pFile = null;
		                	   if (file.exists()) {
		                		   pFile = file.getParentFile();
		                		   file.delete();
		                		   if (pFile.exists()) {
		                			   pFile.delete();
		                		   }
		                	   }
		                	   initView();
		                       updatePageData(pageIndex);
		                   }
		               })
		               .setNegativeButton("取消", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   dialog.cancel();
		                   }
		               });
		        sbuilder.create();
		        sbuilder.show();
			} else {
				showToast("请选择要删除的用户！");
			}
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
    
    public void deleteFilesWithPath(String dirPath) {
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
            		}
            		if (file.isDirectory()) {
            			deleteFilesWithPath(file.getAbsolutePath());
            		}
            	}
        	}
        	dir.delete();
        }
    }
    
    private void loadPageCount() {
    	int size = FaceApp.mProvider.quaryRecordTableRowCount();
    	txtRecordCount.setText(String.valueOf(size));
    	pageCount = size / PAGE_ROW_COUNT;
    	if (0 != size % PAGE_ROW_COUNT) {
    		pageCount++;
    	}
    	txtPageCount.setText(String.valueOf(pageCount));
    }

    private void updatePageData(int pageIndex) {
    	log("updatePageData() pageIndex:" + pageIndex);
    	loadPageCount();
    	
        faceList.clear();
        if (listRecords != null) {
        	listRecords.clear();
        	listRecords = null;
        }
    	if (pageIndex > pageCount && pageCount >= 1) {
    		pageIndex = pageCount;
    	}
    	if (pageIndex < 1) {
    		pageIndex = 1;
    	}
    	txtPageIndex.setText(String.valueOf(pageIndex));

        Map<String, Object> map;
        listRecords = FaceApp.mProvider.queryRecordPage(pageIndex, PAGE_ROW_COUNT);
        if (null != listRecords && listRecords.size() > 0) {
            log("listRecords.size() size:" + listRecords.size());
            for (Record data : listRecords) {
                map = new HashMap<String, Object>();
                map.put("id", data.getId());
                map.put("userId", data.getUserId());
                map.put("userName", data.getUserName());
                map.put("createTime", data.getCreateTime());
                faceList.add(map);
            }
        }
        mAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        log("onItemClick() position:" + position);
        mCurrentRecord = null;
        if (null != listRecords && position > 0 && listRecords.size() >= position) {
        	mCurrentRecord = listRecords.get(position - 1);
            int rId = mCurrentRecord.getId();
            
            txtUserInfo.setText(mCurrentRecord.toUserInfoString());
            txtRecInfo.setText(mCurrentRecord.toRecInfoString());
            
            ivUserImg.setImageResource(R.drawable.nobody);
            File file = null;
            String path = mCurrentRecord.getUserImage();
            log("userImage:" + path);
            if (path != null && !TextUtils.isEmpty(path)) {
                file = new File(path);
                if (file != null && file.exists() && file.isFile()) {
            	    ivUserImg.setImageBitmap(BitmapFactory.decodeFile(mCurrentRecord.getUserImage()));
                }
            }
            ivRecImg.setImageResource(R.drawable.nobody);
            path = mCurrentRecord.getRecImage();
            log("recImage:" + path);
            if (path != null && !TextUtils.isEmpty(path)) {
                file = new File(path);
                if (file != null && file.exists() && file.isFile()) {
            	    ivRecImg.setImageBitmap(BitmapFactory.decodeFile(mCurrentRecord.getRecImage()));
                }
            }
        } else {
        	initView();
        	mCurrentRecord = null;
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
