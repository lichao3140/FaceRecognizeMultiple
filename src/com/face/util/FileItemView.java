package com.face.util;

import com.frm.cn.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class FileItemView extends FrameLayout implements OnCheckedChangeListener {

private ImageView icon;//�ļ�ͼ��
private TextView title;//�ļ���
private CheckBox checkBox;//ѡ��ť
private ViewGroup rootFileItemView;//FileItemView��xml�ļ��ĸ�view
private FileListAdapter adapter;
private int fileMode = FileDialog.FILE_MODE_OPEN_MULTI;
private boolean selectable = true;

private FileItem fileItem;

public FileItemView(Context context) {
super(context);
LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
inflater.inflate(R.layout.view_file_item, this);
icon = (ImageView) findViewById(R.id.image_file_icon);
title = (TextView) findViewById(R.id.text_file_title);
rootFileItemView = (ViewGroup) findViewById(R.id.rootFileItemView);
checkBox = (CheckBox) findViewById(R.id.checkbox_file_item_select);
this.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
    @Override
    public void onDoubleClick() {
        //����˫���¼�
		if (fileItem.isDirectory()) {
			openFolder();
		}
    }
   })
);

this.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		// ѡ��һ��
		selectOne();
	}
});
}

public FileItem getFileItem() {
return fileItem;
}

public void setFileItem(FileItem fileItem, FileListAdapter adapter,
    int fileMode) {
this.fileItem = fileItem;
this.adapter = adapter;
this.fileMode = fileMode;
icon.setImageResource(fileItem.getIcon());
title.setText(fileItem.getName());
toggleSelectState();

if (!fileItem.isDirectory()
        && (fileMode == FileDialog.FILE_MODE_OPEN_FOLDER_MULTI || fileMode == FileDialog.FILE_MODE_OPEN_FOLDER_SINGLE)) {
    //���ѡ��ģʽ�뵱ǰ�ļ����Ͳ����������Ϊ����ѡ�񣬱�����ֻ��ѡ���ļ�ƽʱ���ļ�����ѡ
    checkBox.setEnabled(false);
    selectable = false;
    checkBox.setOnCheckedChangeListener(null);
    return;
}

if (fileItem.isDirectory()
        && (fileMode == FileDialog.FILE_MODE_OPEN_FILE_MULTI || fileMode == FileDialog.FILE_MODE_OPEN_FILE_SINGLE)) {
    //���ѡ��ģʽ�뵱ǰ�ļ����Ͳ����������Ϊ����ѡ�񣬱�����ֻ��ѡ���ļ�ʱ���ļ��в���ѡ
    checkBox.setEnabled(false);
    selectable = false;
    checkBox.setOnCheckedChangeListener(null);
    return;
}
selectable = true;
checkBox.setEnabled(true);
checkBox.setOnCheckedChangeListener(this);
}

/**
* �л�ѡ�С�δѡ��״̬,fileItem.setSelected(boolean)�ȷ���;
*/
public void toggleSelectState() {
if (fileItem.isSelected()) {
    rootFileItemView
            .setBackgroundResource(R.drawable.bg_file_item_select);
} else {
    rootFileItemView
            .setBackgroundResource(R.drawable.bg_file_item_normal);
}
checkBox.setOnCheckedChangeListener(null);
checkBox.setChecked(fileItem.isSelected());
checkBox.setOnCheckedChangeListener(this);
}

public void selectOne() {//ѡ��һ���ļ�(��)
if (selectable) {
    if (fileItem.isSelected()) {
        // ȡ��ѡ��״̬��ֻ��FileItemView�Ϳ���
        fileItem.setSelected(!fileItem.isSelected());
        toggleSelectState();
        adapter.unselectOne();
    } else {
        // ���Ҫѡ��ĳ��FileItem�������Ҫ��adapter������У���Ϊ����ǵ�ѡ�Ļ�����Ҫȡ��������ѡ��״̬
        adapter.selectOne(fileItem);
    }
}
}

/**
* ���ļ���
*/
public void openFolder() {
adapter.openFolder(fileItem);
}

public FileListAdapter getAdapter() {
return adapter;
}

@Override
public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
if (isChecked) {
    adapter.selectOne(fileItem);
} else {
    fileItem.setSelected(false);
    rootFileItemView
            .setBackgroundResource(R.drawable.bg_file_item_normal);
    adapter.unselectOne();
}
}

public int getFileMode() {
return fileMode;
}
}