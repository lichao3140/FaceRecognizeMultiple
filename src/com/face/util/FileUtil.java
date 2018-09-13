
package com.face.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;
import android.util.Log;

/**
 * TODO ��
 * �ļ�����ʱҪ��·����ͳһ����/sdcard/�µ��ļ��ƶ���/storage/emulated/0/xx/�»�ʧ�ܣ������ƶ���/sdcard/xx/
 * �»�ɹ��� Thread-safe? ���ơ��ƶ���ɾ����ѹ������ѹ����ȡ����ͼ��ȡ���ļ����͡��ļ�/�ļ��д�С���㡢�ļ������ļ��������㡢
 * ��rootȨ����ʾϵͳ�ļ����ж��ļ�������ϵ���жϷ������ӡ�ת���ļ���СΪ�ɶ�String��
 * 
 * @author NashLegend
 */
@SuppressLint("DefaultLocale")
public class FileUtil {

    private static String FolderKey = ".folder.";

    /**
     * ����ǰ������ֱ�Ӳ������ļ����Ƶ�һ����ʱĿ¼�£��ı�Ȩ�ޣ��Ա���к����Ķ�д����,����ļ��н��д˲����� ��û�п����ļ����������ַ��������
     * 
     * @param file
     * @param context
     * @return
     */
    public static File getTempFileForRoot(File file, Context context) {
        File tmpFile = new File(context.getExternalCacheDir().getPath(),
                EncryptUtil.getStringDigestEncrypt(file.getAbsolutePath(), EncryptUtil.MD5)
                        .substring(0, 12) + "_"
                        + file.getName());
        if (!tmpFile.getParentFile().exists()) {
            tmpFile.getParentFile().mkdirs();
        }
        String cpCommand = "cp -f " + getCmdPath(file.getAbsolutePath()) + " "
                + getCmdPath(tmpFile.getAbsolutePath());
        SystemUtil.shellExecute(cpCommand, true);
        String chmodCommand = "chmod 777 " + getCmdPath(tmpFile.getAbsolutePath());
        SystemUtil.shellExecute(chmodCommand, true);
        return tmpFile;
    }

    public static String parseFilePathCommand() {
        return "";
    }

    /**
     * ��Windows���������������������ļ�����������name�Ƿ�Ϊnull����Ϊ��
     * 
     * @param files
     * @param name
     */
    public static void rename(File[] files, String name, Context context) {
        if (name != null) {
            name = name.trim();
            if (name.equals("")) {
                return;
            }
        } else {
            return;
        }
        if (files != null && files.length > 0) {
            String[] scanPaths = new String[files.length];
            if (files.length == 1) {
                File file = files[0];
                if (file.getParent() != null) {
                    File newPath = new File(file.getParent(), name);
                    file.renameTo(newPath);
                    scanPaths[0] = newPath.getAbsolutePath();
                }
            } else {
                HashMap<String, Integer> suffixGroup = new HashMap<String, Integer>();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    File newpath;
                    int num = 0;
                    String newName = "";
                    String suff = "";
                    if (file.isDirectory()) {
                        suff = FolderKey;
                    } else {
                        suff = getFileSuffix(file);
                    }
                    if (suffixGroup.containsKey(suff)) {
                        num = suffixGroup.get(suff);
                    } else {
                        if (hasMoreSuffix(files, suff)) {
                            num = 0;
                        } else {
                            num = -1;
                        }
                    }

                    // ��������ļ�������Ѿ��������ļ�����num+1
                    // ������һ��Сbug�����newpath�Ѿ����ڣ���������files��������
                    // ��ô������������ʱ�򣬻�����newpath������
                    // ������������ɵ��б����潫���������newpath
                    do {
                        num++;
                        if (num == 0) {
                            newName = name;
                        } else {
                            newName = name + " (" + num + ")";
                        }
                        newpath = new File(file.getParent(), newName
                                + (FolderKey.equals(suff) ? ""
                                        : suff.length() > 0 ? "." + suff : 0));
                    } while (newpath.exists());

                    suffixGroup.put(suff, num);
                    file.renameTo(newpath);
                    scanPaths[i] = newpath.getAbsolutePath();
                }
            }
            if (context != null) {
                MediaScannerConnection.scanFile(context, scanPaths, null, null);
            }
        }
    }

    private static boolean hasMoreSuffix(File[] files, String suffix) {
        int num = 0;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (suffix.equals(FolderKey)) {
                if (file.isDirectory()) {
                    num++;
                }
            } else {
                if (getFileSuffix(file).equals(suffix)) {
                    num++;
                }
            }

            if (num > 1) {
                return true;
            }
        }
        return false;
    }

    public static String getNameWithoutSuffix(File file) {
        String name = "";
        String fileName = file.getName();
        int offset = fileName.lastIndexOf(".");
        // -1��û�к�׺��offset == fileName.length() - 1����ʾ"."�����һ���ַ���û�к�׺
        if (offset >= 0 && offset < fileName.length() - 1) {
            name = fileName.substring(0, offset);
        } else {
            name = fileName;
        }
        return name;
    }

    /**
     * �ж��ļ��Ƿ��Ƿ�������
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean isSymboliclink(File file) throws IOException {
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }

    public static boolean isInExternalStorage(File file) {
        if (isDesendentOf(file, Environment.getExternalStorageDirectory())
                || isDesendentOf(file, new File("/storage/emulated/legacy"))
                || isDesendentOf(file, new File("/storage/ext_sd"))) {
            return true;
        }
        return false;
    }
    
    public static File[] ListFilesWithPath(String dirPath) {
    	File dir = new File(dirPath);
    	if (dirPath == null || !dir.exists()) {
    		return null;
    	}
        File[] files = null;
    	if (dir.isFile()) {
    		files = new File[] {dir};
    	}
        if(dir.isDirectory()) {
        	files = dir.listFiles();
        }
        return files;
    }

    public static File[] ListFilesWithRoot(String dirPath) {
        if (dirPath.lastIndexOf("/") != dirPath.length() - 1) {
            dirPath += "/";
        }
        BufferedReader reader = null; // errReader = null;
        ArrayList<File> path = new ArrayList<File>();
        File[] files = null;
        try {
            reader = SystemUtil.shellExecute("IFS='\n';CURDIR='"
                    + getCmdPath(dirPath)
                    + "';for i in `ls $CURDIR`; do echo \"$CURDIR$i\"; done",
                    true);
            File f;
            String line;
            while ((line = reader.readLine()) != null) {
                f = new File(line);
                path.add(f);
            }
            files = new File[path.size()];
            for (int i = 0; i < path.size(); i++) {
                files[i] = path.get(i);
            }
        } catch (Exception e) {
            Log.e("file", e.getMessage());
        }
        return files;
    }

    public static String getCmdPath(String path) {
        return path.replace(" ", "\\ ").replace("'", "\\'");
    }

    /**
     * ����������ж��٣�ûһ�������ԡ���
     * 
     * @param path
     * @param al
     * @return
     */
    public static String getCmdPath(String path, boolean al) {
        return path.replaceAll("\\$", "\\$").replaceAll(" ", "\\ ").replaceAll("\\*", "\\*")
                .replaceAll("\\?", "\\?").replaceAll("\\\\", "\\\\").replaceAll(">", "\\>")
                .replaceAll("'", "\\'");
    }

    /**
     * parentFile�Ƿ����sonFile,������ļ�������
     * 
     * @param parentFile
     * @param sonFile
     * @return
     */
    public static boolean isAncestorOf(File parentFile, File sonFile) {
        try {
            String parentPath = parentFile.getCanonicalPath();
            String sonPath = sonFile.getCanonicalPath();
            if (parentPath.equals("/")) {
                if (sonPath.length() > 1) {
                    // sonPath='/abc'
                    return true;
                }
            } else {
                if (parentPath.lastIndexOf("/") != parentPath.length() - 1) {
                    // ������/��β
                    parentPath += "/";
                }
                if (sonPath.length() > parentPath.length()
                        && sonPath.indexOf(parentPath) == 0) {
                    return true;
                }
            }
        } catch (IOException e) {

        }
        return false;
    }

    /**
     * parentFile�Ƿ����sonFile,������ļ�������
     * 
     * @param sonFile
     * @param parentFile
     * @return
     */
    public static boolean isDesendentOf(File sonFile, File parentFile) {
        return isAncestorOf(parentFile, sonFile);
    }

    // �ļ���СתString���֣�b,kb,mb,gb
    public static String convertStorage(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    /**
     * �������file·����Ϊͳһ·��������storage/emulated/0\legacy�ȵȡ�
     * /sdcard/data1/data2/data3���е�storage/emulated/0/data1���ܳ���
     */
    public static void trimPath(File file) {
        // TODO
    }

    /**
     * @param file
     * @return
     */
    public static int getNumFilesInFolder(File file) {
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        if (!file.exists()) {
            throw new NullPointerException("file does not exist");
        }
        if (file.isFile()) {
            throw new ClassCastException("file is not a directory");
        }
        return getSubfilesNumberInFolder(file, false, true);
    }

    /**
     * @param file
     * @param includeHiddleFiles
     * @param includeFolder
     * @return
     */
    public static int getNumFilesInFolder(File file,
            boolean includeHiddleFiles, boolean includeFolder) {
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        if (!file.exists()) {
            throw new NullPointerException("file does not exist");
        }
        if (file.isFile()) {
            throw new ClassCastException("file is not a directory");
        }
        return getSubfilesNumberInFolder(file, includeHiddleFiles,
                includeFolder);
    }

    /**
     * @param file
     * @param includeHiddleFiles
     * @param includeFolder
     * @return
     */
    public static int getNumFilesInFolder(File[] files,
            boolean includeHiddleFiles, boolean includeFolder) {
        int size = 0;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                size++;
            } else if (file.isDirectory()) {
                size += getNumFilesInFolder(file, includeHiddleFiles,
                        includeFolder);
            }
        }
        return size;
    }

    public static long getFileSize(File file) {
        return getFileSize(file, true);
    }

    public static long getFileSize(File file, boolean includeEmptyFolderSize) {
        long size = 0L;
        if (file != null && file.exists()) {
            File[] files = {
                    file
            };
            return getFileSize(files, includeEmptyFolderSize);
        }
        return size;
    }

    public static long getFileSize(File[] files) {
        return getFileSize(files, true);
    }

    public static long getFileSize(File[] files, boolean includeEmptyFolderSize) {
        if (files == null) {
            throw new NullPointerException("files cannot be null");
        }
        long size = 0L;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file != null && file.exists()) {
                if (file.isDirectory()) {
                    size += getSilgleFolderSize(file, includeEmptyFolderSize);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }

    private static boolean copy2File(File sourceFile, File destFile,
            int operationType, Context context, InnerFileOperationListener listener) {
        boolean flag = true;
        if (ensureSourceAndDestFileValid(sourceFile, destFile, operationType)) {
            if (sourceFile.equals(destFile)) {
                if (listener != null) {
                    listener.OperationEnded = true;
                    listener.result = true;
                }
                return true;
            }
            if (sourceFile.isFile()) {
                flag = copy2SingleFile(sourceFile, destFile, operationType, listener);
            } else {
                flag = copy2SingleFolder(sourceFile, destFile, operationType, listener);
            }
        } else {
            flag = false;
        }
        if (context != null) {
            MediaScannerConnection.scanFile(context,
                    new String[] {
                        destFile.getAbsolutePath()
                    }, null, null);
        }
        if (listener != null) {
            listener.OperationEnded = true;
            listener.result = flag;
        }
        return flag;
    }

    public static Runnable copy2FileAsync(final File sourceFile,
            final File destFile, final Context context,
            final FileOperationListener listener) {

        class CopyTask extends AsyncTask<String, Long, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {
                InnerFileOperationListener innerListener = null;
                if (listener != null) {
                    innerListener = new InnerFileOperationListener() {

                        @Override
                        public void processed(int l) {
                            processed += l;
                            publishProgress(processed, total);
                        }
                    };
                    innerListener.total = getFileSize(sourceFile, false);
                }
                return copy2File(sourceFile, destFile,
                        FileUtil.Operation_Merge_And_Overwrite, context, innerListener);
            }

            @Override
            protected void onProgressUpdate(Long... values) {
                listener.onProgress(values[0], values[1]);
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (listener != null) {
                    if (result) {
                        listener.onComplete();
                    } else {
                        listener.onError("");
                    }
                }
            }
        }

        CopyTask task = new CopyTask();
        task.execute("");

        return null;
    }

    public static boolean copy2Directory(File sourceFile, File destFile,
            int operationType, Context context, InnerFileOperationListener listener) {
        if (sourceFile == null || destFile == null) {
            throw new NullPointerException(sourceFile == null ? "sourceFile"
                    : "destFile" + " cannot be null");
        }
        if (!sourceFile.exists()) {
            throw new NullPointerException("sourceFile does not exist");
        }
        File[] sourceFiles = {
                sourceFile
        };
        return copy2Directory(sourceFiles, destFile, operationType, context, listener);
    }

    public static Runnable copy2DirectoryAsync(final File sourceFile,
            final File destFile, final Context context,
            final FileOperationListener listener) {
        class CopyTask extends AsyncTask<String, Long, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {
                InnerFileOperationListener innerListener = null;
                if (listener != null) {
                    innerListener = new InnerFileOperationListener() {

                        @Override
                        public void processed(int l) {
                            processed += l;
                            publishProgress(processed, total);
                        }
                    };
                    innerListener.total = getFileSize(sourceFile, false);
                }
                return copy2Directory(sourceFile, destFile,
                        FileUtil.Operation_Merge_And_Overwrite, context, innerListener);
            }

            @Override
            protected void onProgressUpdate(Long... values) {
                listener.onProgress(values[0], values[1]);
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (listener != null) {
                    if (result) {
                        listener.onComplete();
                    } else {
                        listener.onError("");
                    }
                }
            }
        }

        CopyTask task = new CopyTask();
        task.execute("");
        return null;
    }

    public static boolean copy2Directory(File[] sourceFiles, File destFile,
            int operationType, Context context, InnerFileOperationListener listener) {

        if (sourceFiles == null || destFile == null) {
            throw new NullPointerException(sourceFiles == null ? "sourceFile"
                    : "destFile" + " cannot be null");
        }

        for (int i = 0; i < sourceFiles.length; i++) {
            File file = sourceFiles[i];
            if (file == null || !file.exists()) {
                throw new NullPointerException(
                        "one or more sourceFiles not exist");
            }
        }

        if (ensureFileIsDirectory(destFile)) {
            boolean flag = true;
            String[] filePaths = new String[sourceFiles.length];
            for (int i = 0; i < sourceFiles.length; i++) {
                File sourceFile = sourceFiles[i];
                File finalFile = new File(destFile.getAbsolutePath(),
                        sourceFile.getName());
                filePaths[i] = finalFile.getAbsolutePath();
                if (!copy2File(sourceFile, finalFile, operationType, context, listener)) {
                    flag = false;
                    break;
                }
            }
            if (context != null) {
                MediaScannerConnection.scanFile(context, filePaths, null, null);
            }
            return flag;
        } else {
            return false;
        }
    }

    public static Runnable copy2DirectoryAsync(final File[] sourceFiles,
            final File destFile, final Context context,
            final FileOperationListener listener) {
        class CopyTask extends AsyncTask<String, Long, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {
                InnerFileOperationListener innerListener = null;
                if (listener != null) {
                    innerListener = new InnerFileOperationListener() {

                        @Override
                        public void processed(int l) {
                            processed += l;
                            publishProgress(processed, total);
                        }
                    };
                    innerListener.total = getFileSize(sourceFiles, false);
                }
                return copy2Directory(sourceFiles, destFile,
                        FileUtil.Operation_Merge_And_Overwrite, context, innerListener);
            }

            @Override
            protected void onProgressUpdate(Long... values) {
                listener.onProgress(values[0], values[1]);
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (listener != null) {
                    if (result) {
                        listener.onComplete();
                    } else {
                        listener.onError("");
                    }
                }
            }
        }

        CopyTask task = new CopyTask();
        task.execute("");
        return null;
    }

    /**
     * ��sourceFile�ļ��ƶ���ΪdestFile
     * 
     * @param sourceFile
     * @param destFile
     * @return
     */
    public static boolean move2File(File sourceFile, File destFile,
            int operationType, Context context) {
        boolean flag = true;
        if (ensureSourceAndDestFileValid(sourceFile, destFile, operationType)) {
            // ���ﲻ�ؼ���Ƿ�sourceFile��destFile��ͬһ���ļ���renameTo��Ȼ�᷵��true
            // renameTo�����������ļ�ʱ������false,���Բ����á�
            // �ƶ����Ǹ��ƣ�����ֻҪdeskFile�����ڣ�ֱ��renameTo���ɣ�����һ��һ���ݹ�
            if (sourceFile.equals(destFile)) {
                return true;
            }
            if (sourceFile.isFile()) {
                flag = move2SingleFile(sourceFile, destFile, operationType);
            } else {
                flag = move2SingleFolder(sourceFile, destFile, operationType);
            }
        } else {
            flag = false;
        }

        if (context != null) {
            MediaScannerConnection.scanFile(
                    context,
                    new String[] {
                            sourceFile.getAbsolutePath(),
                            destFile.getAbsolutePath()
                    }, null, null);
        }
        return flag;
    }

    public static Runnable move2FileAsync(final File sourceFile,
            final File destFile, final Context context,
            final FileOperationListener listener) {
        class MoveTask extends AsyncTask<String, Integer, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {
                return move2File(sourceFile, destFile,
                        FileUtil.Operation_Merge_And_Overwrite, context);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (listener != null) {
                    if (result) {
                        listener.onComplete();
                    } else {
                        listener.onError("");
                    }
                }
            }
        }

        MoveTask task = new MoveTask();
        task.execute("");

        return null;
    }

    /**
     * ���ļ�ת�Ƶ�ĳ��Ŀ¼���ļ�������
     * 
     * @param sourceFile
     * @param destFile
     * @return
     */
    public static boolean move2Directory(File sourceFile, File destFile,
            int operationType, Context context) {
        if (sourceFile == null || destFile == null) {
            throw new NullPointerException(sourceFile == null ? "sourceFile"
                    : "destFile" + " cannot be null");
        }
        if (!sourceFile.exists()) {
            throw new NullPointerException("sourceFile does not exist");
        }
        File[] sourceFiles = {
                sourceFile
        };
        return move2Directory(sourceFiles, destFile, operationType, context);
    }

    public static Runnable move2DirectoryAsync(final File sourceFile,
            final File destFile, final Context context,
            final FileOperationListener listener) {
        class MoveTask extends AsyncTask<String, Integer, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {
                return move2Directory(sourceFile, destFile,
                        FileUtil.Operation_Merge_And_Overwrite, context);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (listener != null) {
                    if (result) {
                        listener.onComplete();
                    } else {
                        listener.onError("");
                    }
                }
            }
        }

        MoveTask task = new MoveTask();
        task.execute("");

        return null;
    }

    public static boolean move2Directory(File[] sourceFiles, File destFile,
            int operationType, Context context) {
        if (sourceFiles == null || destFile == null) {
            throw new NullPointerException(sourceFiles == null ? "sourceFile"
                    : "destFile" + " cannot be null");
        }

        for (int i = 0; i < sourceFiles.length; i++) {
            File file = sourceFiles[i];
            if (file == null || !file.exists()) {
                throw new NullPointerException(
                        "one or more sourceFiles not exist");
            }
        }
        if (ensureFileIsDirectory(destFile)) {
            boolean flag = true;
            String[] filePaths = new String[sourceFiles.length * 2];
            for (int i = 0; i < sourceFiles.length; i++) {
                File sourceFile = sourceFiles[i];
                File finalFile = new File(destFile.getAbsolutePath(),
                        sourceFile.getName());
                filePaths[2 * i] = sourceFile.getAbsolutePath();
                filePaths[2 * i + 1] = finalFile.getAbsolutePath();
                if (!move2File(sourceFile, finalFile, operationType, null)) {
                    Log.i("file", "fail");
                    flag = false;
                }
            }
            if (context != null) {
                MediaScannerConnection.scanFile(context, filePaths, null, null);
            }
            return flag;
        } else {
            Log.i("file", "Oops");
            return false;
        }
    }

    public static Runnable move2DirectoryAsync(final File[] sourceFiles,
            final File destFile, final Context context,
            final FileOperationListener listener) {
        class MoveTask extends AsyncTask<String, Integer, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {
                return move2Directory(sourceFiles, destFile,
                        FileUtil.Operation_Merge_And_Overwrite, context);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (listener != null) {
                    if (result) {
                        listener.onComplete();
                    } else {
                        listener.onError("");
                    }
                }
            }
        }

        MoveTask task = new MoveTask();
        task.execute("");
        return null;
    }
    
    public static void deleteFilesWithPath(String dirPath) {
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

    public static boolean delete(File[] files, Context context) {
        boolean flag = true;
        String[] filePaths = new String[files.length];
        if (files.length > 0) {
            for (int j = 0; j < files.length; j++) {
                File file = files[j];
                filePaths[j] = file.getAbsolutePath();
                if (!delete(file)) {
                    flag = false;
                    break;
                }
            }
        }
        if (context != null) {
            MediaScannerConnection.scanFile(context, filePaths, null, null);
        }
        return flag;
    }

    public static Runnable deleteAsync(final File[] files,
            final Context context, final FileOperationListener listener) {
        class DeleteTask extends AsyncTask<String, Integer, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {
                return delete(files, context);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (listener != null) {
                    if (result) {
                        listener.onComplete();
                    } else {
                        listener.onError("");
                    }
                }
            }
        }

        DeleteTask task = new DeleteTask();
        task.execute("");
        return null;
    }

    private static boolean delete(File file) {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (file.isDirectory()) {
            return deleteFolder(file);
        }
        return false;
    }

    public static boolean delete(File file, Context context) {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        boolean flag = false;
        String[] filePaths = new String[] {
                file.getAbsolutePath()
        };
        if (file.isFile()) {
            flag = file.delete();
        }
        if (file.isDirectory()) {
            flag = deleteFolder(file);
        }
        if (context != null) {
            MediaScannerConnection.scanFile(context, filePaths, null, null);
        }
        return flag;
    }

    public static Runnable deleteAsync(final File file, final Context context,
            final FileOperationListener listener) {
        class DeleteTask extends AsyncTask<String, Integer, Boolean> {

            @Override
            protected Boolean doInBackground(String... params) {
                return delete(file, context);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (listener != null) {
                    if (result) {
                        listener.onComplete();
                    } else {
                        listener.onError("");
                    }
                }
            }
        }

        DeleteTask task = new DeleteTask();
        task.execute("");
        return null;
    }

    /**
     * ��ȡ�ļ�����ͼ��Ĭ�ϴ�С96 x 96. ���û��ָ��context��������ȡ��apk�ļ�������ͼ
     * 
     * @param file
     * @return
     */
    public static Bitmap extractFileThumbnail(File file, Context context) {
        int type = getFileType(file);
        Bitmap thumb = null;
        switch (type) {
            case FILE_TYPE_IMAGE:// ��ȡͼ���ļ�����ͼ
                thumb = getImageFileThumbnail(file, 96, 96);
                break;
            case FILE_TYPE_VIDEO:// ��ȡ��Ƶ�ļ�����ͼ
                thumb = getVideoFileThumbnail(file, 96, 96);
                break;
            case FILE_TYPE_AUDIO:// ��ȡ�����ļ�����ͼ��
                if (getFileSuffix(file).equals("mp3")) {
                    if (android.os.Build.VERSION.SDK_INT >= 10) {
                        thumb = extractMediaThumbnail(file);
                    } else {
                        thumb = getMP3Thumbnail(file);
                    }
                }
                break;
            case FILE_TYPE_APK:// ��ȡapk�ļ�����ͼ
                thumb = getApkIcon(context, file.getAbsolutePath());
                break;

            default:
                break;
        }
        return thumb;
    }

    /**
     * ��ȡ�ļ�����ͼ,ָ������ͼ��С, ���û��ָ��context��������ȡ��apk�ļ�������ͼ
     * 
     * @param file
     * @param width
     * @param height
     * @return
     */
    public static Bitmap extractFileThumbnail(File file, int width, int height,
            Context context) {
        int type = getFileType(file);
        Bitmap thumb = null;
        switch (type) {
            case FILE_TYPE_IMAGE:// ��ȡͼ���ļ�����ͼ
                thumb = getImageFileThumbnail(file, width, height);
                break;
            case FILE_TYPE_VIDEO:// ��ȡ��Ƶ�ļ�����ͼ
                thumb = getVideoFileThumbnail(file, width, height);
                break;
            case FILE_TYPE_AUDIO:// ��ȡ�����ļ�����ͼ��
                if (getFileSuffix(file).equals("mp3")) {
                    Bitmap tmp = null;
                    if (android.os.Build.VERSION.SDK_INT >= 10) {
                        tmp = extractMediaThumbnail(file);
                    } else {
                        tmp = getMP3Thumbnail(file);
                    }
                    thumb = ThumbnailUtils.extractThumbnail(tmp, width, height);
                }
                break;
            case FILE_TYPE_APK:// ��ȡapk�ļ���ָ���ߴ����Ź�������ͼ��
                thumb = getApkResizedIcon(context, file.getAbsolutePath(), width,
                        height);
                break;

            default:
                break;
        }
        return thumb;
    }

    /**
     * ��ȡͼƬ�ļ�������ͼ
     * 
     * @param file
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getImageFileThumbnail(File file, int width, int height) {
        Bitmap bitmap = null;
        String path = file.getAbsolutePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // ֻ��ȡ���ͼƬ�Ŀ�͸�
        bitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        // �������ű�,���ִ����ʱ���п���Ϊ-1
        int h = options.outHeight;
        int w = options.outWidth;
        if (h > 0 && w > 0) {
            int beWidth = w / width;
            int beHeight = h / height;
            int be = 1;
            if (beWidth < beHeight) {
                be = beWidth;
            } else {
                be = beHeight;
            }
            if (be <= 0) {
                be = 1;
            }
            options.inSampleSize = be;
            // ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false
            bitmap = BitmapFactory.decodeFile(path, options);
            // ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }

        return bitmap;
    }

    public static Bitmap getVideoFileThumbnail(File file, int width, int height) {
        // MINI_KIND: 512 x 384 �� MICRO_KIND: 96 x 96
        Bitmap thumb = null;
        if (width > 96 || height > 96) {
            // ����96 x 96����ȡMINI_KIND������ͼ���ڴ˻������ٴγ�ȡ
            thumb = ThumbnailUtils.extractThumbnail(ThumbnailUtils
                    .createVideoThumbnail(file.getAbsolutePath(),
                            Thumbnails.MINI_KIND), width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } else if (width == 96 && height == 96) {
            thumb = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),
                    Thumbnails.MICRO_KIND);
        } else {
            // С��96 x 96����ȡMICRO_KIND������ͼ���ڴ˻������ٴγ�ȡ
            thumb = ThumbnailUtils.extractThumbnail(ThumbnailUtils
                    .createVideoThumbnail(file.getAbsolutePath(),
                            Thumbnails.MICRO_KIND), width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return thumb;
    }

    /**
     * ����mp3�ļ���˵����getMP3Thumbnail()Ч����һ���ģ�ֻ���������Կ�
     * 
     * @param file
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap extractMediaThumbnail(File file) {
        Bitmap bitmap = null;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(file.getAbsolutePath());
            byte[] data = mmr.getEmbeddedPicture();
            if (data != null) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
        } catch (Exception e) {

        }
        return bitmap;
    }

    /**
     * http://blog.csdn.net/toni001/article/details/6724785
     * 
     * @param file
     * @return
     */
    public static Bitmap getMP3Thumbnail(File file) {
        int buffSize = 204800;//
        FileInputStream mp3ips = null;
        Bitmap bitmap = null;
        try {
            mp3ips = new FileInputStream(file);
            if (buffSize > mp3ips.available()) {
                buffSize = mp3ips.available();
            }
            byte[] buff = new byte[buffSize];
            mp3ips.read(buff, 0, buffSize);
            if (indexOf("ID3".getBytes(), buff, 1, 512) == -1) {
                // No ID3V2
                return null;
            }
            if (indexOf("APIC".getBytes(), buff, 1, 512) != -1) {
                int searLen = indexOf(new byte[] {
                        (byte) 0xFF, (byte) 0xFB
                },
                        buff);
                int imgStart = indexOf(new byte[] {
                        (byte) 0xFF, (byte) 0xD8
                },
                        buff);
                int imgEnd = lastIndexOf(
                        new byte[] {
                                (byte) 0xFF, (byte) 0xD9
                        }, buff, 1,
                        searLen) + 2;
                byte[] imgb = cutBytes(imgStart, imgEnd, buff);
                bitmap = BitmapFactory.decodeByteArray(imgb, 0, imgb.length);
            } else {
                // No APIC
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mp3ips != null) {
                try {
                    mp3ips.close();
                } catch (Exception e2) {
                }
            }
        }

        return bitmap;
    }

    /**
     * ��������
     */
    public static int indexOf(byte[] tag, byte[] src) {
        return indexOf(tag, src, 1, src.length);
    }

    /**
     * ��ȡ��index����λ��<br />
     * index��1��ʼ
     */
    private static int indexOf(byte[] tag, byte[] src, int index, int len) {
        if (len > src.length) {
            try {
                throw new Exception("�����ܸ���");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int tagLen = tag.length;
        byte[] tmp = new byte[tagLen];
        for (int j = 0; j < len - tagLen + 1; j++) {
            for (int i = 0; i < tagLen; i++) {
                tmp[i] = src[j + i];
            }
            // �ж��Ƿ����
            for (int i = 0; i < tagLen; i++) {
                if (tmp[i] != tag[i])
                    break;
                if (i == tagLen - 1) {
                    return j;
                }
            }

        }
        return -1;
    }

    /**
     * �����ȡ��index����λ��<br />
     * index��1��ʼ
     */
    private static int lastIndexOf(byte[] tag, byte[] src, int index, int len) {
        if (len > src.length) {
            try {
                throw new Exception("�����ܸ���");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int tagLen = tag.length;
        byte[] tmp = new byte[tagLen];
        for (int j = len - tagLen; j >= 0; j--) {
            for (int i = 0; i < tagLen; i++) {
                tmp[i] = src[j + i];

            }
            for (int i = 0; i < tagLen; i++) {
                if (tmp[i] != tag[i])
                    break;
                if (i == tagLen - 1) {
                    return j;
                }
            }

        }
        return -1;
    }

    /**
     * ��ȡbyte[]
     */
    private static byte[] cutBytes(int start, int end, byte[] src) {
        if (end <= start || start < 0 || end > src.length) {
            return null;
        }
        byte[] tmp = new byte[end - start];
        for (int i = 0; i < end - start; i++) {
            tmp[i] = src[start + i];
        }
        return tmp;
    }

    /**
     * ��ȡapk�ļ���ָ���ߴ����Ź�������ͼ
     * 
     * @param context
     * @param apkPath
     * @return
     */
    public static Bitmap getApkResizedIcon(Context context, String apkPath,
            int width, int height) {
        Bitmap thumb = getApkIcon(context, apkPath);
        if (thumb != null) {
            return ThumbnailUtils.extractThumbnail(thumb, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return thumb;
    }

    /**
     * ��ȡapk�ļ�����ͼ������ı�icon��С
     * 
     * @param context
     * @param apkPath
     * @return
     */
    public static Bitmap getApkIcon(Context context, String apkPath) {
        Bitmap thumb = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                thumb = ((BitmapDrawable) appInfo.loadIcon(pm)).getBitmap();
            } catch (OutOfMemoryError e) {

            }
        }
        return thumb;
    }

    /**
     * ���ļ�
     * 
     * @param file
     * @param context
     * @return
     */
    public boolean openFile(File file, Context context) {
        int type = getFileType(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri data = Uri.fromFile(file);
        switch (type) {
            case FILE_TYPE_APK:
                intent.setDataAndType(data,
                        "application/vnd.android.package-archive");
                break;
            case FILE_TYPE_IMAGE:
                intent.setDataAndType(data, "image/*");
                break;
            case FILE_TYPE_AUDIO:
                intent.putExtra("oneshot", 0);
                intent.putExtra("configchange", 0);
                intent.setDataAndType(data, "audio/*");
                break;
            case FILE_TYPE_TXT:
                intent.setDataAndType(data, "text/plain");
                break;
            case FILE_TYPE_VIDEO:
                intent.putExtra("oneshot", 0);
                intent.putExtra("configchange", 0);
                intent.setDataAndType(data, "video/*");
                break;
            case FILE_TYPE_ZIP:
                intent.setDataAndType(data, "application/zip");
                break;
            case FILE_TYPE_WORD:
                intent.setDataAndType(data, "application/msword");
                break;
            case FILE_TYPE_PPT:
                intent.setDataAndType(data, "application/vnd.ms-powerpoint");
                break;
            case FILE_TYPE_EXCEL:
                intent.setDataAndType(data, "application/vnd.ms-excel");
                break;
            case FILE_TYPE_HTML:
                intent.setDataAndType(data, "text/html");
                break;
            case FILE_TYPE_PDF:
                intent.setDataAndType(data, "application/pdf");
                break;
            case FILE_TYPE_TORRENT:
                intent.setDataAndType(data, "torrent/*");
                break;
            case FILE_TYPE_CHM:
                intent.setDataAndType(data, "application/mshelp");
                break;

            default:
                break;
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    /**
     * ���ݺ�׺ȡ���ļ�����,FILE_TYPE_IMAGE,FILE_TYPE_SOUND......
     */
    public static int getFileType(File file) {
        if (file.isDirectory()) {
            return FILE_TYPE_FOLDER;
        } else {
            String suffix = getFileSuffix(file);
            if (isArrayContains(apkSuffixArray, suffix)) {
                return FILE_TYPE_APK;
            } else if (isArrayContains(imageSuffixArray, suffix)) {
                return FILE_TYPE_IMAGE;
            } else if (isArrayContains(soundSuffixArray, suffix)) {
                return FILE_TYPE_AUDIO;
            } else if (isArrayContains(videoSuffixArray, suffix)) {
                return FILE_TYPE_VIDEO;
            } else if (isArrayContains(txtSuffixArray, suffix)) {
                return FILE_TYPE_TXT;
            } else if (isArrayContains(zipSuffixArray, suffix)) {
                return FILE_TYPE_ZIP;
            } else if (isArrayContains(wordSuffixArray, suffix)) {
                return FILE_TYPE_WORD;
            } else if (isArrayContains(pptSuffixArray, suffix)) {
                return FILE_TYPE_PPT;
            } else if (isArrayContains(excelSuffixArray, suffix)) {
                return FILE_TYPE_EXCEL;
            } else if (isArrayContains(pdfSuffixArray, suffix)) {
                return FILE_TYPE_PDF;
            } else if (isArrayContains(ebookSuffixArray, suffix)) {
                return FILE_TYPE_EBOOK;
            } else if (isArrayContains(torrentSuffixArray, suffix)) {
                return FILE_TYPE_TORRENT;
            } else if (isArrayContains(chmSuffixArray, suffix)) {
                return FILE_TYPE_CHM;
            } else if (isArrayContains(htmlSuffixArray, suffix)) {
                return FILE_TYPE_HTML;
            } else {
                return FILE_TYPE_NORMAL;
            }
        }
    }

    /**
     * ����ļ���׺
     */
    public static String getFileSuffix(File file) {
        if (file.isDirectory()) {
            return "";
        }
        String fileName = file.getName();
        String suffix = "";
        int offset = fileName.lastIndexOf(".");
        if (offset >= 0) {
            suffix = fileName.substring(offset + 1);
        }
        return suffix.toLowerCase();
    }

    /**
     * @param file
     * @param includeHiddleFiles
     * @param includeFolder
     * @return
     */
    private static int getSubfilesNumberInFolder(File file,
            boolean includeHiddleFiles, boolean includeFolder) {
        int size = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file2 = files[i];
                if (!includeHiddleFiles && file2.isHidden()) {
                    continue;
                }
                if (file2.isDirectory()) {
                    size += getSubfilesNumberInFolder(file2,
                            includeHiddleFiles, includeFolder);
                } else {
                    size += 1;
                }
            }
        }
        if (includeFolder) {
            size++;
        }
        return size;
    }

    private static long getSilgleFolderSize(File file, boolean includeEmptyFolderSize) {
        long size = 0L;
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file2 = files[i];
            if (file2.isDirectory()) {
                size += getSilgleFolderSize(file2, includeEmptyFolderSize);
            } else {
                size += file2.length();
            }
        }
        // �ļ���ռ��4k,���߸��࣬ȡ���������ļ�����
        if (includeEmptyFolderSize) {
            size += file.length();
        }
        return size;
    }

    private static boolean deleteFolder(File file) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles == null || subFiles.length == 0) {
                return file.delete();
            } else {
                for (int i = 0; i < subFiles.length; i++) {
                    File sFile = subFiles[i];
                    if (sFile.isDirectory()) {
                        if (!deleteFolder(sFile)) {
                            return false;
                        }
                    } else {
                        if (!sFile.delete()) {
                            return false;
                        }
                    }
                }
            }
            // ȫ��ɾ����Ҫ��ɾ���Լ�
            return file.delete();
        } else {
            throw new ClassCastException("file is not a directory");
        }
    }

    /**
     * ���Ƶ����ļ��ж������ļ���˽�о�̬��������copy2File(File sourceFile, File destFile)���á�
     * �ڵ���ǰ���Ѿ�ȷ�������Ϸ������ؼ�飬sourceFileһ�����������ļ��У�sourceFile��destFileҲһ������ͬһ���ļ���
     * destFile��һ�������ڡ������ڣ��п������ļ�Ҳ�п������ļ��� Folder To Folder
     * 
     * @param operationType
     * @return
     */
    private static boolean copy2SingleFolder(File sourceFile, File destFile,
            int operationType, InnerFileOperationListener listener) {
        if (destFile.exists()) {
            switch (operationType) {
                case Operation_Ski_All:
                    // ����ɾ���ļ���������ֱ������
                    return false;
                case Operation_Merge:
                    // ֻ�ϲ��ļ��У�����ɾ���ļ���ֻ�е�sourceFile��destFile�����ļ���ʱ����ͨ��
                    if (destFile.isFile()) {
                        return false;
                    }
                    break;
                case Operation_Merge_And_Overwrite:
                    // �ϲ��ļ��У�ɾ��ͬ���ļ���
                    if (destFile.isFile() && !delete(destFile)) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        // ����destFile��Ȼ���ܴ���,������ڣ�һ�����ļ���
        if (destFile.exists() || destFile.mkdirs()) {
            File[] files = sourceFile.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File sourceSubFile = files[i];
                    File destSubFile = new File(destFile.getAbsolutePath(),
                            sourceSubFile.getName());
                    if (sourceSubFile.isDirectory()) {
                        if (!copy2SingleFolder(sourceSubFile, destSubFile,
                                operationType, listener)) {
                            return false;
                        }
                    } else {
                        if (!copy2SingleFile(sourceSubFile, destSubFile,
                                operationType, listener)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * ���Ƶ����ļ��������ļ��С�˽�о�̬��������copy2File����copy2SingleFolder���á�
     * �ڵ���ǰ���Ѿ�ȷ�������Ϸ������ؽ��м��,destFile��Ȼ�и��ļ��У�sourceFileһ�����������ļ���
     * sourceFile��destFileҲһ������ͬһ���ļ���deskFile�п��ܴ���Ҳ�п��ܲ����ڡ������ڣ��п������ļ�Ҳ�п������ļ���
     * File To File
     * 
     * @param operationType
     * @return
     */
    private static boolean copy2SingleFile(File sourceFile, File destFile,
            int operationType, InnerFileOperationListener listener) {
        if (destFile.exists()) {
            if (operationType == Operation_Merge_And_Overwrite) {
                // �ϲ��ļ��У�ɾ��ͬ���ļ���ֻ������������п��ܲ�����false
                if (!delete(destFile)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        // �˴�destFileһ��������
        boolean copyOK = true;
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(
                    sourceFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    destFile));
            byte[] buffer = new byte[1024 * 5];
            int len;
            boolean hasListener = listener != null;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                if (hasListener) {
                    listener.processed(len);
                }
            }
        } catch (FileNotFoundException e) {
            copyOK = false;
            e.printStackTrace();
        } catch (IOException e) {
            copyOK = false;
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                copyOK = false;
                e.printStackTrace();
            }
        }

        return copyOK;
    }

    /**
     * ʹ��FileChannel�����ļ����ٶ��Կ�
     * 
     * @param sourceFile
     * @param destFile
     * @return
     */
    @SuppressWarnings("unused")
    private static boolean copy2SingleFileByChannel(File sourceFile,
            File destFile) {
        boolean copyOK = true;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(destFile);
            inputChannel = inputStream.getChannel();
            outputChannel = outputStream.getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        } catch (Exception e) {
            copyOK = false;
        } finally {
            try {
                inputChannel.close();
                inputStream.close();
                outputChannel.close();
                outputStream.close();
            } catch (IOException e) {
                copyOK = false;
                e.printStackTrace();
            }
        }
        return copyOK;
    }

    /**
     * �ƶ������ļ��ж������ļ���˽�о�̬��������move2File(File sourceFile, File destFile)���á�
     * �ڵ���ǰ���Ѿ�ȷ�������Ϸ������ؼ�飬sourceFileһ�����������ļ��У�sourceFile��destFileҲһ������ͬһ���ļ���
     * destFile��һ�������ڡ������ڣ��п������ļ�Ҳ�п������ļ��� Folder To Folder
     * 
     * @param operationType
     * @return
     */
    private static boolean move2SingleFolder(File sourceFile, File destFile,
            int operationType) {
        if (destFile.exists()) {
            switch (operationType) {
                case Operation_Ski_All:
                    // ����ɾ���ļ���������ֱ������
                    return false;
                case Operation_Merge:
                    // ֻ�ϲ��ļ��У�����ɾ���ļ���ֻ�е�sourceFile��destFile�����ļ���ʱ����ͨ��
                    if (destFile.isFile()) {
                        return false;
                    }
                    break;
                case Operation_Merge_And_Overwrite:
                    // �ϲ��ļ��У�ɾ��ͬ���ļ���
                    if (destFile.isFile() && !delete(destFile)) {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }
        // ����destFile��Ȼ���ܴ���,������ڣ�һ�����ļ���
        if (destFile.exists()) {
            // һ��һ��������������Ҫmkdirs
            File[] files = sourceFile.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File sourceSubFile = files[i];
                    File destSubFile = new File(destFile.getAbsolutePath(),
                            sourceSubFile.getName());
                    if (sourceSubFile.isDirectory()) {
                        if (!move2SingleFolder(sourceSubFile, destSubFile,
                                operationType)) {
                            return false;
                        }
                    } else {
                        if (!move2SingleFile(sourceSubFile, destSubFile,
                                operationType)) {
                            return false;
                        }
                    }
                }
            }
        } else {
            return sourceFile.renameTo(destFile);
        }
        return true;
    }

    /**
     * �ƶ������ļ��������ļ��С�˽�о�̬��������move2File����move2SingleFolder���á�
     * �ڵ���ǰ���Ѿ�ȷ�������Ϸ������ؽ��м��,destFile��Ȼ�и��ļ��У�sourceFileһ�����������ļ���
     * sourceFile��destFileҲһ������ͬһ���ļ���deskFile�п��ܴ���Ҳ�п��ܲ����ڡ������ڣ��п������ļ�Ҳ�п������ļ���
     * File To File
     * 
     * @param operationType
     * @return
     */
    private static boolean move2SingleFile(File sourceFile, File destFile,
            int operationType) {
        if (destFile.exists()) {
            if (operationType == Operation_Merge_And_Overwrite) {
                // �ϲ��ļ��У�ɾ��ͬ���ļ���ֻ������������п��ܲ�����false
                if (!delete(destFile)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        // �˴�destFileһ��������
        boolean flag = sourceFile.renameTo(destFile);
        return flag;
    }

    /**
     * ����ȷ��ĳ�������и�Ŀ¼�Ĵ����ԣ�ȷ��destFile�Ǹ�Ŀ¼����������ڣ��򴴽���������ڵ�����Ŀ¼���򷵻�false��
     * 
     * @param destFile
     * @return
     */
    private static boolean ensureFileIsDirectory(File destFile) {
        if (destFile == null) {
            throw new NullPointerException("destFile cannot be null");
        }
        // ���һ���ļ������ڣ���isDirectory��ȻΪfalse������mkdirs��isDirectory����true��
        if (destFile.exists()) {
            // ���destFile�����Ҳ��Ǹ��ļ���ֱ�ӷ���false
            if (!destFile.isDirectory()) {
                return false;
            }
        } else {
            // ���destFile�������򴴽��ļ��У�����ʧ���򷵻�false���п�����û��Ȩ��Ҳ�п�����·������
            // Ҳ�п���destFile�ĸ���Ŀ¼ȴ�Ǹ��ļ�������Ŀ¼
            if (!destFile.mkdirs()) {
                return false;
            }
        }
        return true;
    }

    /**
     * ȷ��Դ�ļ���Ŀ���ļ��ĺϷ��ԡ�
     * 
     * @param sourceFile Ҫ���ƻ����ƶ����ļ���������ڡ�
     * @param destFile
     *            Ҫ�ƶ�����Ŀ���ļ�������Ҫ�ƶ�����Ŀ¼��������һ�����ڣ���������ɾ������������ĳһ���õ��Ľ���п������ļ����򲻺Ϸ���
     *            ��Ϊ�������ļ��Ͳ��ܴ�����mkdirs����ɹ�
     * @return ������true����destFile�����ڣ�destFile�ĸ���Ŀ¼���ڣ�ֻ�ȸ��ƹ�ȥ
     */
    private static boolean ensureSourceAndDestFileValid(File sourceFile,
            File destFile, int operationType) {
        if (sourceFile == null || destFile == null) {
            throw new NullPointerException(sourceFile == null ? "sourceFile"
                    : "destFile" + " cannot be null");
        }

        if (!sourceFile.exists()) {
            throw new NullPointerException("sourceFile does not exist");
        }

        if (destFile.exists()) {
            switch (operationType) {
                case Operation_Ski_All:
                    // ����ɾ���ļ���������ֱ������
                    return false;
                case Operation_Merge:
                    // ֻ�ϲ��ļ��У�����ɾ���ļ���ֻ�е�sourceFile��destFile�����ļ���ʱ����ͨ��
                    if (sourceFile.isFile() || destFile.isFile()) {
                        return false;
                    }
                    break;
                case Operation_Merge_And_Overwrite:
                    // �ϲ��ļ��У�ɾ��ͬ���ļ���
                    if (sourceFile.isFile() || destFile.isFile()) {
                        if (!delete(destFile)) {
                            return false;
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        return ensureFileIsDirectory(destFile.getParentFile());
    }

    private static boolean isArrayContains(String[] strs, String suffix) {
        if (strs == null || suffix == null) {
            return false;
        }
        for (int i = 0; i < strs.length; i++) {
            if (suffix.equals(strs[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * ��ͨ�ļ�
     */
    public static final int FILE_TYPE_NORMAL = 0;
    /**
     * �ļ���
     */
    public static final int FILE_TYPE_FOLDER = 1;
    /**
     * �������͵��ļ�
     */
    public static final int FILE_TYPE_AUDIO = 2;
    /**
     * ͼ�����͵��ļ�
     */
    public static final int FILE_TYPE_IMAGE = 3;
    /**
     * ��Ƶ���͵��ļ�
     */
    public static final int FILE_TYPE_VIDEO = 4;
    /**
     * APK�ļ�
     */
    public static final int FILE_TYPE_APK = 5;
    /**
     * TXT�ļ�
     */
    public static final int FILE_TYPE_TXT = 6;
    /**
     * ZIP�ļ�
     */
    public static final int FILE_TYPE_ZIP = 7;
    /**
     * HTML�ļ�
     */
    public static final int FILE_TYPE_HTML = 8;
    /**
     * WORD�ļ�
     */
    public static final int FILE_TYPE_WORD = 9;
    /**
     * EXCEL�ļ�
     */
    public static final int FILE_TYPE_EXCEL = 10;
    /**
     * PPT�ļ�
     */
    public static final int FILE_TYPE_PPT = 11;
    /**
     * PDF�ļ�
     */
    public static final int FILE_TYPE_PDF = 12;
    /**
     * �������ļ�
     */
    public static final int FILE_TYPE_EBOOK = 13;
    /**
     * �����ļ�
     */
    public static final int FILE_TYPE_TORRENT = 14;
    /**
     * CHM�ļ�
     */
    public static final int FILE_TYPE_CHM = 15;

    public static final String[] soundSuffixArray = {
            "mp3", "wav", "ogg",
            "midi"
    };
    public static final String[] imageSuffixArray = {
            "jpg", "jpeg", "png",
            "bmp", "gif"
    };
    public static final String[] videoSuffixArray = {
            "mp4", "avi", "rmvb",
            "flv", "mkv", "wmv",
    };
    public static final String[] apkSuffixArray = {
            "apk"
    };
    public static final String[] txtSuffixArray = {
            "txt", "xml", "java", "c",
            "cpp", "py", "log", "cs", "json"
    };
    public static final String[] zipSuffixArray = {
            "zip", "rar", "gz", "7z",
            "jar", "img", "tar"
    };
    public static final String[] wordSuffixArray = {
            "doc", "docx"
    };
    public static final String[] pptSuffixArray = {
            "ppt", "pptx"
    };
    public static final String[] excelSuffixArray = {
            "xsl", "xslx"
    };
    public static final String[] htmlSuffixArray = {
            "html", "htm", "jsp",
            "asp", "php"
    };
    public static final String[] pdfSuffixArray = {
            "pdf"
    };
    public static final String[] torrentSuffixArray = {
            "torrent"
    };
    public static final String[] chmSuffixArray = {
            "chm"
    };
    public static final String[] ebookSuffixArray = {
            "epub", "caj", "ebk2",
            "ebk3", "umd"
    };

    // д�ļ�ģʽ,�����ʹ�ã�sourceFile��destFile�����ļ��е��������ģʽ������ɾ���ļ���
    public static final int Operation_Ski_All = 0x0;// ����ֱ������
    public static final int Operation_Merge = 0x1;// ֻ�ϲ��ļ��в����滻�ļ�����ζ�Ų�ɾ���κ��ļ�
    public static final int Operation_Merge_And_Overwrite = 0x2;// �ϲ��ļ��в��滻�ļ�

    public static interface FileOperationListener {

        public void onComplete();

        public void onProgress(long progress, long total);

        public void onError(String message);

    }

    static class InnerFileOperationListener {

        public void processed(int l) {
        };

        public boolean OperationEnded = false;

        public boolean result = false;

        public long processed = 0l;

        public long total = 0l;

    }
}
