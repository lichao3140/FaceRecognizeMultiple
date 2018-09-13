package com.face.util;

import android.content.Context;
import android.widget.Toast;

/**
 * showToastΪ����������ʹ�ò������toast��ʱ�������Ļ�ϵ������
 * makeTextAndShow��ͨ��Toast����makeText��show����������
 * 
 * @author NashLegend
 */
public class ToastUtil {

	public static Toast toast;

	public ToastUtil() {
		// TODO �Զ����ɵĹ��캯�����
	}

	/**
	 * ����������ʹ�ò������toast��ʱ�������Ļ�ϵ������durationΪToast.LENGTH_SHORT
	 * 
	 * @param context
	 * @param text
	 */
	public static void showToast(Context context, String text) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * ����������ʹ�ò������toast��ʱ�������Ļ�ϵ������ʹ��string��Դ��durationΪToast.LENGTH_SHORT
	 * 
	 * @param context
	 * @param text
	 */
	public static void showToast(Context context, int resId) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * ����������ʹ�ò������toast��ʱ�������Ļ�ϵ������durationΪ�Զ���
	 * 
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void showToast(Context context, String text, int duration) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	/**
	 * ����������ʹ�ò������toast��ʱ�������Ļ�ϵ������ʹ��string��Դ��durationΪ�Զ���
	 * 
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void showToast(Context context, int resId, int duration) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, resId, duration);
		toast.show();
	}

	/**
	 * ��ͨ��Toast����makeText��show����������durationΪToast.LENGTH_SHORT
	 * 
	 * @param context
	 * @param text
	 */
	public static void makeTextAndShow(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * ��ͨ��Toast����makeText��show����������ʹ��string��Դ��durationΪToast.LENGTH_SHORT
	 * 
	 * @param context
	 * @param resId
	 */
	public static void makeTextAndShow(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * ��ͨ��Toast����makeText��show����������durationΪ�Զ���
	 * 
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void makeTextAndShow(Context context, String text,
			int duration) {
		Toast.makeText(context, text, duration).show();
	}

	/**
	 * ��ͨ��Toast����makeText��show����������ʹ��string��Դ��durationΪ�Զ���
	 * 
	 * @param context
	 * @param resId
	 * @param duration
	 */
	public static void makeTextAndShow(Context context, int resId, int duration) {
		Toast.makeText(context, resId, duration).show();
	}

}
