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

private ImageView icon;//文件图标
private TextView title;//文件名
private CheckBox checkBox;//选择按钮
private ViewGroup rootFileItemView;//FileItemView的xml文件的根view
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
        //处理双击事件
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
		// 选中一个
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
    //如果选择模式与当前文件类型不符，则设计为不可选择，比如在只可选择文件平时，文件不可选
    checkBox.setEnabled(false);
    selectable = false;
    checkBox.setOnCheckedChangeListener(null);
    return;
}

if (fileItem.isDirectory()
        && (fileMode == FileDialog.FILE_MODE_OPEN_FILE_MULTI || fileMode == FileDialog.FILE_MODE_OPEN_FILE_SINGLE)) {
    //如果选择模式与当前文件类型不符，则设计为不可选择，比如在只可选择文件时，文件夹不可选
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
* 切换选中、未选中状态,fileItem.setSelected(boolean)先发生;
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

public void selectOne() {//选中一个文件(夹)
if (selectable) {
    if (fileItem.isSelected()) {
        // 取消选中状态，只在FileItemView就可以
        fileItem.setSelected(!fileItem.isSelected());
        toggleSelectState();
        adapter.unselectOne();
    } else {
        // 如果要选中某个FileItem，则必须要在adapter里面进行，因为如果是单选的话，还要取消其他的选中状态
        adapter.selectOne(fileItem);
    }
}
}

/**
* 打开文件夹
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