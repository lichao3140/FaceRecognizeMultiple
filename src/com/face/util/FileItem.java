
package com.face.util;

import java.io.File;
import java.net.URI;

import com.frm.cn.R;

/**
 * �ļ����󣬼̳���File
 * 
 * @author NashLegend
 */
public class FileItem extends File {

    private static final long serialVersionUID = 2675728441786325207L;

    /**
     * �ļ����ļ��б�����ʾ��icon
     */
    private int icon = R.drawable.ic_launcher;

    /**
     * �ļ��Ƿ����б��б�ѡ��
     */
    private boolean selected = false;

    /**
     * �ļ����ͣ�Ĭ��ΪFILE_TYPE_NORMAL������ͨ�ļ���
     */
    private int fileType = FileUtil.FILE_TYPE_NORMAL;

    /**
     * �ļ���׺
     */
    private String suffix = "";

    public FileItem(File file) {
        this(file.getAbsolutePath());
    }

    public FileItem(String path) {
        super(path);
        setFileTypeBySuffix();
    }

    public FileItem(URI uri) {
        super(uri);
        setFileTypeBySuffix();
    }

    public FileItem(File dir, String name) {
        super(dir, name);
        setFileTypeBySuffix();
    }

    public FileItem(String dirPath, String name) {
        super(dirPath, name);
        setFileTypeBySuffix();
    }

    /**
     * ���ݺ�׺ȡ���ļ�����
     */
    private void setFileTypeBySuffix() {

        int type = FileUtil.getFileType(this);
        setFileType(type);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getFileType() {
        return fileType;
    }

    /**
     * ����fileTyle,ͬʱ�޸�icon
     * 
     * @param fileType
     */
    public void setFileType(int fileType) {
        this.fileType = fileType;
        switch (fileType) {
            case FileUtil.FILE_TYPE_APK:
                setIcon(R.drawable.format_apk);
                break;
            case FileUtil.FILE_TYPE_FOLDER:
                setIcon(R.drawable.format_folder);
                break;
            case FileUtil.FILE_TYPE_IMAGE:
                setIcon(R.drawable.format_picture);
                break;
            case FileUtil.FILE_TYPE_NORMAL:
                setIcon(R.drawable.format_unkown);
                break;
            case FileUtil.FILE_TYPE_AUDIO:
                setIcon(R.drawable.format_music);
                break;
            case FileUtil.FILE_TYPE_TXT:
                setIcon(R.drawable.format_text);
                break;
            case FileUtil.FILE_TYPE_VIDEO:
                setIcon(R.drawable.format_media);
                break;
            case FileUtil.FILE_TYPE_ZIP:
                setIcon(R.drawable.format_zip);
                break;
            case FileUtil.FILE_TYPE_HTML:
                setIcon(R.drawable.format_html);
                break;
            case FileUtil.FILE_TYPE_PDF:
                setIcon(R.drawable.format_pdf);
                break;
            case FileUtil.FILE_TYPE_WORD:
                setIcon(R.drawable.format_word);
                break;
            case FileUtil.FILE_TYPE_EXCEL:
                setIcon(R.drawable.format_excel);
                break;
            case FileUtil.FILE_TYPE_PPT:
                setIcon(R.drawable.format_ppt);
                break;
            case FileUtil.FILE_TYPE_TORRENT:
                setIcon(R.drawable.format_torrent);
                break;
            case FileUtil.FILE_TYPE_EBOOK:
                setIcon(R.drawable.format_ebook);
                break;
            case FileUtil.FILE_TYPE_CHM:
                setIcon(R.drawable.format_chm);
                break;
            default:
                setIcon(R.drawable.format_unkown);
                break;
        }
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
