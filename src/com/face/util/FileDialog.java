package com.face.util;

import java.io.File;
import java.util.ArrayList;

import com.frm.cn.R;
import com.frm.cn.R.id;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class FileDialog extends Dialog {
	 
    /**
     * �Դ��ļ�ģʽ���ļ��Ի����п������ļ���Ҳ�п������ļ�,�ɶ�ѡ�����շ���ֵΪһ��File�����б�
     */
    public static final int FILE_MODE_OPEN_MULTI = 0;
 
    /**
     * �Դ��ļ�ģʽ���ļ��Ի���ֻ��ѡ���ļ��ж������ļ����ɶ�ѡ�����շ���ֵΪһ��File�����б�
     */
    public static final int FILE_MODE_OPEN_FOLDER_MULTI = 1;
 
    /**
     * �Դ��ļ�ģʽ���ļ��Ի���ֻ��ѡ���ļ��������ļ��У��ɶ�ѡ�����շ���ֵΪһ��File�����б�
     */
    public static final int FILE_MODE_OPEN_FILE_MULTI = 2;
 
    /**
     * �Դ��ļ�ģʽ���ļ��Ի����п������ļ���Ҳ�п������ļ�,���շ���ֵΪһ������Ϊ1��File�����б�
     */
    public static final int FILE_MODE_OPEN_SINGLE = 3;
 
    /**
     * �Դ��ļ�ģʽ���ļ��Ի���ֻ��ѡ���ļ��ж������ļ������շ���ֵΪһ������Ϊ1��File�����б�
     */
    public static final int FILE_MODE_OPEN_FOLDER_SINGLE = 4;
 
    /**
     * �Դ��ļ�ģʽ���ļ��Ի���ֻ��ѡ���ļ��������ļ��У����շ���ֵΪһ������Ϊ1��File�����б�
     */
    public static final int FILE_MODE_OPEN_FILE_SINGLE = 5;
 
    public FileDialog(Context context) {
        super(context);
    }
 
    public FileDialog(Context context, int theme) {
        super(context, theme);
    }
 
    public FileDialog(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface FileDialogListener {
        public void onFileSelected(ArrayList<File> files);
 
        public void onFileCanceled();
    }
 
    public static class Builder {
        private int fileMode = FileDialog.FILE_MODE_OPEN_MULTI;
        private String initialPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        private FileDialogListener fileSelectListener;
        private FileDialogView dialogView;
        private Context context;
        private boolean canceledOnTouchOutside = true;
        private boolean cancelable = true;
        private String title = "ѡ���ļ�";
 
        public Builder(Context context) {
            this.context = context;
        }
 
        public Builder setCanceledOnTouchOutside(boolean flag) {
            canceledOnTouchOutside = flag;
            return this;
        }
 
        public Builder setCancelable(boolean flag) {
            cancelable = flag;
            return this;
        }
 
        public Builder setFileMode(int fileMode) {
            this.fileMode = fileMode;
            return this;
        }
 
        public Builder setInitialPath(String initialPath) {
            this.initialPath = initialPath;
            return this;
        }
 
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
 
        public Builder setFileSelectListener(
                FileDialogListener fileSelectListener) {
            this.fileSelectListener = fileSelectListener;
            return this;
        }
 
        /**
         * ����ǿ������dialog�Ĵ�С����ΪListView��С����ȷ��������ListView��Adapter��getView��ִ�кܶ��,
         * ����ȡ����listview��������ʾ�����
         * 
         * @return
         */
        public FileDialog create(int width, int height) {
            final FileDialog dialog = new FileDialog(context);
            dialogView = new FileDialogView(context);
            dialogView.setFileMode(fileMode);
            dialogView.setInitialPath(initialPath);
            dialogView.openFolder();
            dialog.setTitle(title);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            if (width > 0 && height > 0) {
                dialog.getWindow().setLayout(width, height);
            }
            Button okButton = (Button) dialogView.findViewById(R.id.button_dialog_file_ok);
            Button cancelButton = (Button) dialogView.findViewById(R.id.button_dialog_file_cancel);
            okButton.setOnClickListener(new View.OnClickListener() {
 
                @Override
                public void onClick(View v) {
                    // ���ȷ����ť�������ļ��б�
                    if (fileSelectListener != null) {
                        if (dialogView.getSelectedFiles().size() > 0) {
                            fileSelectListener.onFileSelected(dialogView
                                    .getSelectedFiles());
                        } else {
                            fileSelectListener.onFileCanceled();
                        }
                    }
                    dialog.dismiss();
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
 
                @Override
                public void onClick(View v) {
                        //���ȡ����ť��ֱ��dismiss
                    if (fileSelectListener != null) {
                        fileSelectListener.onFileCanceled();
                    }
                    dialog.dismiss();
                }
            });
            return dialog;
        }
 
        /**
         * ʹ��FileDialog��С��activityһ��,��Activity�������֮ǰ�����ص����ֿ��ܲ���
         * 
         * @param activity
         * @return
         */
        public FileDialog create(Activity activity) {
                //���������������ǻ�ô��ڵĿ�ߣ������������������ˣ�����������������Ŀ��ַ
            int width = DisplayUtil.getWindowWidth(activity);
            int height = DisplayUtil.getWindowHeight(activity);
            return create(width, height);
        }
 
    }
 
}