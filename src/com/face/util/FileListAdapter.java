package com.face.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
 
public class FileListAdapter extends BaseAdapter {
    private ArrayList<FileItem> list = new ArrayList<FileItem>();
    private Context mContext;
    private File currentDirectory;
    private FileDialogView dialogView;
 
    public FileListAdapter(Context Context) {
        mContext = Context;
    }
 
    @Override
    public int getCount() {
        return list.size();
    }
 
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = new FileItemView(mContext);
            holder.fileItemView = (FileItemView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.fileItemView.setFileItem(list.get(position), this,
                dialogView.getFileMode());
        return holder.fileItemView;
    }
 
    class ViewHolder {
        FileItemView fileItemView;
    }
 
    public ArrayList<FileItem> getList() {
        return list;
    }
 
    public void setList(ArrayList<FileItem> list) {
        this.list = list;
    }
 
    /**
     * ���ļ��У������ļ��б�
     * 
     * @param file
     */
    public void openFolder(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            if (!file.equals(currentDirectory)) {
                // �뵱ǰĿ¼��ͬ
                currentDirectory = file;
                list.clear();
                File[] files = file.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        File tmpFile = files[i];
            if (tmpFile.isFile()
                && (dialogView.getFileMode() == FileDialog.FILE_MODE_OPEN_FOLDER_MULTI || dialogView
                    .getFileMode() == FileDialog.FILE_MODE_OPEN_FOLDER_SINGLE)) {
                    //���ֻ��ѡ���ļ��в��ҵ�ǰ�ļ������ļ��У���������
                continue;
            }                   
                        list.add(new FileItem(files[i]));
                    }
                }
                files = null;
                sortList();
                notifyDataSetChanged();
            }
        }
        //�ı�FileDialogView�ĵ�ǰ·����ʾ
        dialogView.getPathText().setText(file.getAbsolutePath());
    }
 
    /**
     * ѡ��ǰĿ¼�������ļ�
     */
    public void selectAll() {
        int mode = dialogView.getFileMode();
        if (mode > FileDialog.FILE_MODE_OPEN_FILE_MULTI) {
            // ���if���ᷢ������ΪɶҪд����
            return;
        }
        for (Iterator<FileItem> iterator = list.iterator(); iterator.hasNext();) {
            FileItem fileItem = (FileItem) iterator.next();
 
            if (mode == FileDialog.FILE_MODE_OPEN_FILE_MULTI
                    && fileItem.isDirectory()) {
                // fileItem��Ŀ¼������ֻ��ѡ���ļ���������
                continue;
            }
            if (mode == FileDialog.FILE_MODE_OPEN_FOLDER_MULTI
                    && !fileItem.isDirectory()) {
                // fileItem���ļ�������ֻ��ѡ��Ŀ¼��������
                continue;
            }
 
            fileItem.setSelected(true);
        }
        notifyDataSetChanged();
    }
 
    /**
     * ȡ�������ļ���ѡ��״̬
     */
    public void unselectAll() {
        for (Iterator<FileItem> iterator = list.iterator(); iterator.hasNext();) {
            FileItem fileItem = (FileItem) iterator.next();
            fileItem.setSelected(false);
        }
        notifyDataSetChanged();
    }
 
    /**
     * ѡ��һ���ļ���ֻ��ѡ��ʱ���ã�ȡ��ѡ�в����ã���ֻ��FileItemView����
     * 
     * @param fileItem
     */
    public void selectOne(FileItem fileItem) {
        int mode = dialogView.getFileMode();
        if (mode > FileDialog.FILE_MODE_OPEN_FILE_MULTI) {
            // ����ǵ�ѡ
            if (mode == FileDialog.FILE_MODE_OPEN_FILE_SINGLE
                    && fileItem.isDirectory()) {
                // fileItem��Ŀ¼������ֻ��ѡ���ļ����򷵻�
                return;
            }
            if (mode == FileDialog.FILE_MODE_OPEN_FOLDER_SINGLE
                    && !fileItem.isDirectory()) {
                // fileItem���ļ�������ֻ��ѡ��Ŀ¼���򷵻�
                return;
            }
            for (Iterator<FileItem> iterator = list.iterator(); iterator
                    .hasNext();) {
                FileItem tmpItem = (FileItem) iterator.next();
                if (tmpItem.equals(fileItem)) {
                    tmpItem.setSelected(true);
                } else {
                    tmpItem.setSelected(false);
                }
            }
        } else {
            // ����Ƕ�ѡ
            if (mode == FileDialog.FILE_MODE_OPEN_FILE_MULTI
                    && fileItem.isDirectory()) {
                // fileItem��Ŀ¼������ֻ��ѡ���ļ����򷵻�
                return;
            }
            if (mode == FileDialog.FILE_MODE_OPEN_FOLDER_MULTI
                    && !fileItem.isDirectory()) {
                // fileItem���ļ�������ֻ��ѡ��Ŀ¼���򷵻�
                return;
            }
            fileItem.setSelected(true);
        }
 
        notifyDataSetChanged();
    }
 
    public void sortList() {
        FileItemComparator comparator = new FileItemComparator();
        Collections.sort(list, comparator);
    }
 
    /**
     * ȡ��һ����ѡ�������߼�����FileItemView����
     */
    public void unselectOne() {
        dialogView.unselectCheckBox();
    }
 
    /**
     * @return ѡ�е��ļ��б�
     */
    public ArrayList<File> getSelectedFiles() {
        ArrayList<File> selectedFiles = new ArrayList<File>();
        for (Iterator<FileItem> iterator = list.iterator(); iterator.hasNext();) {
            FileItem file = iterator.next();// ǿ��ת��ΪFile
            if (file.isSelected()) {
                selectedFiles.add(file);
            }
        }
        return selectedFiles;
    }
 
    public class FileItemComparator implements Comparator<FileItem> {
 
        @Override
        public int compare(FileItem lhs, FileItem rhs) {
            if (lhs.isDirectory() != rhs.isDirectory()) {
                // ���һ�����ļ���һ�����ļ��У����Ȱ�����������
                if (lhs.isDirectory()) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                // ���ͬ���ļ��л����ļ�������������
                return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
            }
        }
    }
 
    public File getCurrentDirectory() {
        return currentDirectory;
    }
 
    public FileDialogView getDialogView() {
        return dialogView;
    }
 
    public void setDialogView(FileDialogView dialogView) {
        this.dialogView = dialogView;
    }
 
}