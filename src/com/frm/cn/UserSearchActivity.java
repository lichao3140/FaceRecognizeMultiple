package com.frm.cn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.face.db.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

public class UserSearchActivity extends Activity implements OnClickListener, OnItemClickListener {
    private final static String TAG = "UserManageActivity";
    private final static int PAGE_ROW_COUNT = 10;
    private ListView lvUsers;
    private ArrayList<Map<String, Object>> faceList = new ArrayList<Map<String,Object>>();
    private SimpleAdapter mAdapter = null;
    private List<User> listUsers = null;
    private int pageIndex = 1;
    private int pageCount = 0;
    
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
		setContentView(R.layout.activity_user_search);
		
		lvUsers = (ListView)this.findViewById(R.id.lvUsers);
		lvUsers.setOnItemClickListener(this);
		lvUsers.setSelector(R.drawable.btn_ok_pressed);
		lvUsers.setSelectionAfterHeaderView();
		lvUsers.addHeaderView(LayoutInflater.from(this).inflate( R.layout.user_list_header, null));

		Button btnBack = (Button) this.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		btnBack.setText(R.string.txtBack);
		Button btnTitle = (Button)this.findViewById(R.id.btnTitle);
		btnTitle.setText(R.string.titleUserSearch);
		
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
        default:
            break;
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
        if (null != listUsers && position > 0 && listUsers.size() >= position) {
            User user = listUsers.get(position - 1);
            
            int userId = user.getUserId();
            edtUserId.setText(String.valueOf(userId));
            edtUserName.setText(user.getUserName());
            edtCreateTime.setText(user.getCreateTime());
            String imgPath = String.format("%s/%08d/%08d.jpg", FaceApp.userDataPath, userId, userId);
            log("userImgPath:" + imgPath);
            File file = new File(imgPath);
            if (file.exists() && file.isFile()) {
            	ivUserImg.setImageBitmap(BitmapFactory.decodeFile(imgPath));
            } else {
            	ivUserImg.setImageResource(R.drawable.nobody);
            }
        } else {
        	initView();
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