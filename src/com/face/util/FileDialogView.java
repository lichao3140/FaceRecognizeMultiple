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
private ListView listView;//�ļ��б�
private EditText pathText;//��ǰ·��
private ImageButton backButton;//�����ϼ���ť
private CheckBox selectAllButton;//ȫѡ��ť

private int fileMode = FileDialog.FILE_MODE_OPEN_MULTI;//ѡ���ļ���ʽ��Ĭ��Ϊ�ļ����ļ��л�ѡ
private String initialPath = "/";//����ָ���մ�ʱ��Ŀ¼��Ĭ��Ϊ��Ŀ¼

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
 * ��ʼ��view
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
pathText.setKeyListener(null);//����Ҫ�������

adapter = new FileListAdapter(context);
adapter.setDialogView(this);
listView.setAdapter(adapter);
}

/**
* ��Ŀ¼
* 
* @param file Ҫ�򿪵��ļ���
*            
*/
public void openFolder(File file) {
if (!file.exists() || !file.isDirectory()) {
    // �������ڴ�Ŀ¼�����SD����Ŀ¼
    file = Environment.getExternalStorageDirectory();
}
//openFolder������ȡ�ļ��б����FileListAdapter�Ĵ���
adapter.openFolder(file);
}

/**
* ��Ŀ¼
* 
* @param path
*            Ҫ�򿪵��ļ���·��
*/
public void openFolder(String path) {
openFolder(new File(path));
}

/**
* �򿪳�ʼĿ¼
*/
public void openFolder() {
openFolder(initialPath);
}

/**
* �����ϼ�Ŀ¼
*/
private void back2ParentLevel() {
File file = adapter.getCurrentDirectory();
// �����ǰĿ¼��Ϊ���Ҹ�Ŀ¼��Ϊ�գ���򿪸�Ŀ¼
if (file != null && file.getParentFile() != null) {
    openFolder(file.getParentFile());
}
}

/**
* ѡ�е�ǰĿ¼�����ļ�
*/
private void selectAll() {
adapter.selectAll();
}

/**
* ȡ��ѡ�е�ǰĿ¼�����ļ�
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
* @return ����ѡ�е��ļ��б�
*/
public ArrayList<File> getSelectedFiles() {
ArrayList<File> list = new ArrayList<File>();
if (adapter.getSelectedFiles().size() > 0) {
    list = adapter.getSelectedFiles();
} else {
        //������ȷ����ʱ��û��ѡ���ļ�����ģʽ��ѡ�񵥸��ļ��У���ô�ͷ��ص�ǰĿ¼
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
    // ��ѡģʽӦ�ÿ�����ȫѡ��ť�Ŷ�
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
