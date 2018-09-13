package com.face.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class DisplayUtil {

	public DisplayUtil() {
		// TODO �Զ����ɵĹ��캯�����
	}

	/**
	 * ��ȡ�ֻ���Ļ�߶�,��pxΪ��λ
	 * 
	 * @param activity
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	/**
	 * ��ȡ�ֻ���Ļ��ȣ���pxΪ��λ
	 * 
	 * @param activity
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	/**
	 * ���س���window���
	 * 
	 * @return
	 */
	public static int getWindowWidth(Activity activity) {
		return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT)
				.getWidth();
	}

	/**
	 * ���س���window�߶ȣ�������֪ͨ���ͱ�����
	 * 
	 * @return
	 */
	public static int getWindowContentHeight(Activity activity) {
		return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT)
				.getHeight();
	}

	/**
	 * ���س���window�߶ȣ�������֪ͨ��
	 * 
	 * @return
	 */
	public static int getWindowHeight(Activity activity) {
		return getScreenHeight(activity) - getStatusBarHeight(activity);
	}

	/**
	 * ������Ļ�����ܶ�
	 * 
	 * @param context
	 * @return
	 */
	public static float getPixelDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * ����״̬���߶�
	 * 
	 * @param activity
	 * @return
	 */
	public static int getStatusBarHeight(Activity activity) {
		Rect outRect = new Rect();
		activity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(outRect);
		return outRect.top;
	}

	public static int getTitleBarHeight(Activity activity) {
		return getScreenHeight(activity) - getWindowContentHeight(activity)
				- getStatusBarHeight(activity);
	}

	/**
	 * ��λת������dipת��Ϊpx
	 * 
	 * @param dp
	 * @param context
	 * @return
	 */
	public static int dip2px(float dp, Context context) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * ��λת������pxת��Ϊdip
	 * 
	 * @param px
	 * @param context
	 * @return
	 */
	public static int px2dip(float px, Context context) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

}
