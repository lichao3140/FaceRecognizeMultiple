package com.face.util;

import java.io.File;
import java.util.ArrayList;

import com.frm.cn.R;
import com.frm.cn.R.id;
import com.frm.cn.R.layout;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

public class FileDialogView extends FrameLayout implements OnCheckedChangeListener {
	 
private FileListAdapter adapter;
private ListView listView;//文件列表
private EditText pathText;//当前路径
private ImageButton backButton;//返回上级按钮
private CheckBox selectAllButton;//全选按钮

private int fileMode = FileDialog.FILE_MODE_OPEN_MULTI;//选择文件方式，默认为文件、文件夹混选
private String initialPath = "/";//用来指定刚打开时的目录，默认为根目录

private Button cancelButton;
private Button okButton;

public FileDialogView(Context context) {
super(context);
initView(context);
}

public FileDialogView(Context context, AttributeSet attrs) {
super(context, attrs);
initView(context);
}

public FileDialogView(Context context, AttributeSet attrs, int defStyle) {
super(context, attrs, defStyle);
initView(context);
}

/**
 * 初始化view
 */
private void initView(Context context) {
LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
inflater.inflate(R.layout.dialog_file, this);

listView = (ListView) findViewById(R.id.listview_dialog_file);
pathText = (EditText) findViewById(R.id.edittext_dialog_file_path);
backButton = (ImageButton) findViewById(R.id.imagebutton_dialog_file_back);
selectAllButton = (CheckBox) findViewById(R.id.checkbox_dialog_file_all);
cancelButton = (Button) findViewById(R.id.button_dialog_file_cancel);
okButton = (Button) findViewById(R.id.button_dialog_file_ok);

backButton.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		back2ParentLevel();
	}
});
//cancelButton.setOnClickListener();
//okButton.setOnClickListener();
selectAllButton.setOnCheckedChangeListener(this);
pathText.setKeyListener(null);//不需要弹起键盘

adapter = new FileListAdapter(context);
adapter.setDialogView(this);
listView.setAdapter(adapter);
}

/**
* 打开目录
* 
* @param file 要打开的文件夹
*            
*/
public void openFolder(File file) {
if (!file.exists() || !file.isDirectory()) {
    // 若不存在此目录，则打开SD卡根目录
    file = Environment.getExternalStorageDirectory();
}
//openFolder用来读取文件列表详见FileListAdapter的代码
adapter.openFolder(file);
}

/**
* 打开目录
* 
* @param path
*            要打开的文件夹路径
*/
public void openFolder(String path) {
openFolder(new File(path));
}

/**
* 打开初始目录
*/
public void openFolder() {
openFolder(initialPath);
}

/**
* 返回上级目录
*/
private void back2ParentLevel() {
File file = adapter.getCurrentDirectory();
// 如果当前目录不为空且父目录不为空，则打开父目录
if (file != null && file.getParentFile() != null) {
    openFolder(file.getParentFile());
}
}

/**
* 选中当前目录所有文件
*/
private void selectAll() {
adapter.selectAll();
}

/**
* 取消选中当前目录所有文件
*/
private void unselectAll() {
adapter.unselectAll();
}

public void unselectCheckBox() {
selectAllButton.setOnCheckedChangeListener(null);
selectAllButton.setChecked(false);
selectAllButton.setOnCheckedChangeListener(this);
}

/**
* @return 返回选中的文件列表
*/
public ArrayList<File> getSelectedFiles() {
ArrayList<File> list = new ArrayList<File>();
if (adapter.getSelectedFiles().size() > 0) {
    list = adapter.getSelectedFiles();
} else {
        //如果点击确定的时候没有选择文件并且模式是选择单个文件夹，那么就返回当前目录
    if (fileMode == FileDialog.FILE_MODE_OPEN_FOLDER_SINGLE) {
        list.add(adapter.getCurrentDirectory());
    }
}
return list;
}

public EditText getPathText() {
return pathText;
}

public int getFileMode() {
return fileMode;
}

public void setFileMode(int fileMode) {
this.fileMode = fileMode;
if (fileMode > FileDialog.FILE_MODE_OPEN_FILE_MULTI) {
    // 单选模式应该看不到全选按钮才对
    selectAllButton.setVisibility(View.GONE);
} else {
    selectAllButton.setVisibility(View.VISIBLE);
}
}

public String getInitialPath() {
return initialPath;
}

public void setInitialPath(String initialPath) {
this.initialPath = initialPath;
}

@Override
public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
if (selectAllButton.isChecked()) {
    selectAll();
} else {
    unselectAll();
}
}

public CheckBox getSelectAllButton() {
return selectAllButton;
}
}
