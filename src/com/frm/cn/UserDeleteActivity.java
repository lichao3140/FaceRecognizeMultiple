package com.frm.cn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.face.db.User;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserDeleteActivity extends Activity implements OnClickListener, OnItemClickListener {
    private final static String TAG = "UserManageActivity";
    private final static int PAGE_ROW_COUNT = 10;
    private ListView lvUsers;
    private ArrayList<Map<String, Object>> faceList = new ArrayList<Map<String,Object>>();
    private SimpleAdapter mAdapter = null;
    private List<User> listUsers = null;
    private int pageIndex = 1;
    private int pageCount = 0;
    
    private User mCurrentUser = null; // 当前显示的用户
    
    private TextView txtPageIndex;
    private TextView txtPageCount;
    private TextView txtRecordCount;
    private ImageView ivUserImg;
    
	private EditText edtSearchByNo;
	private EditText edtSearchByName;
    
    private TextView edtUserId;
    private TextView edtUserName;
    private TextView edtCreateTime;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_delete);
		
		lvUsers = (ListView)this.findViewById(R.id.lvUsers);
		lvUsers.setOnItemClickListener(this);
		lvUsers.setSelector(R.drawable.btn_ok_pressed);
		lvUsers.setSelectionAfterHeaderView();
		lvUsers.addHeaderView(LayoutInflater.from(this).inflate( R.layout.user_list_header, null));
        
		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnBack.setText(R.string.txtBack);
		Button btnTitle = (Button)this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleUserDelete);
		Button btnNext = (Button) this.findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		btnNext.setText(R.string.txtDelete);
		
		ImageView ivSearch = (ImageView)this.findViewById(R.id.ivSearch);
		ivSearch.setOnClickListener(this);
		edtSearchByNo = (EditText)this.findViewById(R.id.edtSearchByNo);
		edtSearchByName = (EditText)this.findViewById(R.id.edtSearchByName);
		edtSearchByNo.setHintTextColor(getResources().getColor(R.color.hintGrey));
		edtSearchByName.setHintTextColor(getResources().getColor(R.color.hintGrey));
        
        edtUserId = (EditText)this.findViewById(R.id.edtUserId);
        edtUserName = (EditText)this.findViewById(R.id.edtUserName);
        edtCreateTime = (EditText)this.findViewById(R.id.edtCreateTime);
        ivUserImg = (ImageView)this.findViewById(R.id.ivUserImg);
        
        txtPageIndex = (TextView)this.findViewById(R.id.txtPageIndex);
        txtPageCount = (TextView)this.findViewById(R.id.txtPageCount);
        txtRecordCount = (TextView)this.findViewById(R.id.txtRecordCount);
        ImageView btnUp = (ImageView)this.findViewById(R.id.ivPageUp);
        btnUp.setOnClickListener(this);
        ImageView btnDown = (ImageView)this.findViewById(R.id.ivPageDown);
        btnDown.setOnClickListener(this);
        
        mAdapter = new SimpleAdapter(this, faceList,R.layout.user_list_context,
                new String[]{"id","userId", "userName","createTime"},
                new int[]{R.id.txtId, R.id.txtUserId, R.id.txtUserName,R.id.txtCreateTime});
        lvUsers.setAdapter(mAdapter);
	}
    
    private void initView() {
        edtUserId.setText("");
        edtUserName.setText("");
        edtCreateTime.setText("");
        ivUserImg.setImageResource(R.drawable.nobody);
        txtPageIndex.setText("0");
        txtPageCount.setText("0");
        txtRecordCount.setText("0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = null;
        initView();
        
        updatePageData(1);
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
		case R.id.btnBack:
			mIntent = new Intent(this, UserManageActivity.class);
			this.startActivity(mIntent);
			this.finish();
			break;
		case R.id.ivPageUp:
			if (1 < pageIndex && pageIndex <= pageCount) {
				pageIndex--;
			} else {
				//showToast("当前已经到达首页。");
				pageIndex = pageCount;
			}
			updatePageData(pageIndex);
			break;
		case R.id.ivPageDown:
			if (1 <= pageIndex && pageIndex < pageCount) {
				pageIndex++;
			} else {
				//showToast("当前已经到达末页。");
				pageIndex = 1;
			}
			updatePageData(pageIndex);
			break;
		case R.id.ivSearch:
			updatePageData(1);
			break;
		case R.id.btnNext:
			if (mCurrentUser != null) {
		        AlertDialog.Builder sbuilder = new AlertDialog.Builder(this);
		        sbuilder.setTitle("删除用户")
		               .setMessage("是否删除当前用户？")
		               .setPositiveButton("确定", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   FaceApp.mRecognize.deleteFaceFeature(mCurrentUser.getUserId());
		                	   FaceApp.mProvider.deleteUserById(mCurrentUser.getId());
		                	   File file = new File(mCurrentUser.getUserImage());
		                	   if (file.exists()) {
		                		   file.delete();
		                	   }
		                	   int userId = mCurrentUser.getUserId();
		                	   String fPath;
		                	   String pFile;
		                	   pFile = FaceApp.userDataPath + String.format("/%08d", userId);
		                	   fPath =  pFile + "/feature.dat";
		                	   file = new File(fPath);
		                	   if (file != null) {
		                		   file.delete();
		                	   }
		                	   file = new File(pFile);
		                	   if (file != null) {
		                		   file.delete();
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
    
    
    private void loadPageCount(String name, String no) {
    	int size = FaceApp.mProvider.quaryUserTableRowCount(no, name);
    	txtRecordCount.setText(String.valueOf(size));
    	pageCount = size / PAGE_ROW_COUNT;
    	if (0 != size % PAGE_ROW_COUNT) {
    		pageCount++;
    	}
    	txtPageCount.setText(String.valueOf(pageCount));
    }

    private void updatePageData(int pageIndex) {
    	log("updatePageData() pageIndex:" + pageIndex);
		String no = edtSearchByNo.getText().toString();
		String name = edtSearchByName.getText().toString();
		loadPageCount(name, no);
		
        faceList.clear();
        if (listUsers != null) {
        	listUsers.clear();
        	listUsers = null;
        }

    	if (pageIndex > pageCount && pageCount >= 1) {
    		pageIndex = pageCount;
    	}
    	if (pageIndex < 1) {
    		pageIndex = 1;
    	}
    	txtPageIndex.setText(String.valueOf(pageIndex));

        Map<String, Object> map;
        listUsers = FaceApp.mProvider.queryUserPage(no, name, pageIndex, PAGE_ROW_COUNT);
        if (null != listUsers && listUsers.size() > 0) {
            log("listUsers.size() size:" + listUsers.size());
            for (User data : listUsers) {
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
        mCurrentUser = null;
        if (null != listUsers && position > 0 && listUsers.size() >= position) {
        	mCurrentUser = listUsers.get(position - 1);
            
            edtUserId.setText(String.valueOf(mCurrentUser.getUserId()));
            edtUserName.setText(mCurrentUser.getUserName());
            edtCreateTime.setText(mCurrentUser.getCreateTime());
            ivUserImg.setImageResource(R.drawable.nobody);
            String path = mCurrentUser.getUserImage();
            if (path != null && !TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                	ivUserImg.setImageBitmap(BitmapFactory.decodeFile(mCurrentUser.getUserImage()));
                }
            }
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