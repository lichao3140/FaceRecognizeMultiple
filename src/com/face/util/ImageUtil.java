package com.face.util;

import com.face.db.Record;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * ͼƬ������
 * @author ˮ��
 * ��ӭ����ˮ���ĸ��˲��ͣ�http://www.sunhome.org.cn
 *
 */
public class ImageUtil {

    /**
     * ����ˮӡͼƬ�����Ͻ�
     * @param Context
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    public static Bitmap createWaterMaskLeftTop(
            Context context, Bitmap src, Bitmap watermark,
            int paddingLeft, int paddingTop) {
        return createWaterMaskBitmap(src, watermark, 
                dp2px(context, paddingLeft), dp2px(context, paddingTop));
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark,
            int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        //����һ��bitmap
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);// ����һ���µĺ�SRC���ȿ��һ����λͼ
        //����ͼƬ��Ϊ����
        Canvas canvas = new Canvas(newb);
        //�ڻ��� 0��0�����Ͽ�ʼ����ԭʼͼƬ
        canvas.drawBitmap(src, 0, 0, null);
        //�ڻ����ϻ���ˮӡͼƬ
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // ����
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // �洢
        canvas.restore();
        return newb;
    }

    /**
     * ����ˮӡͼƬ�����½�
     * @param Context
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingBottom
     * @return
     */
    public static Bitmap createWaterMaskRightBottom(
            Context context, Bitmap src, Bitmap watermark,
            int paddingRight, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark, 
                src.getWidth() - watermark.getWidth() - dp2px(context, paddingRight), 
                src.getHeight() - watermark.getHeight() - dp2px(context, paddingBottom));
    }

    /**
     * ����ˮӡͼƬ�����Ͻ�
     * @param Context
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public static Bitmap createWaterMaskRightTop(
            Context context, Bitmap src, Bitmap watermark,
            int paddingRight, int paddingTop) {
        return createWaterMaskBitmap( src, watermark, 
                src.getWidth() - watermark.getWidth() - dp2px(context, paddingRight), 
                dp2px(context, paddingTop));
    }

    /**
     * ����ˮӡͼƬ�����½�
     * @param Context
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    public static Bitmap createWaterMaskLeftBottom(
            Context context, Bitmap src, Bitmap watermark,
            int paddingLeft, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark, dp2px(context, paddingLeft), 
                src.getHeight() - watermark.getHeight() - dp2px(context, paddingBottom));
    }

    /**
     * ����ˮӡͼƬ���м�
     * @param Context
     * @param src
     * @param watermark
     * @return
     */
    public static Bitmap createWaterMaskCenter(Bitmap src, Bitmap watermark) {
        return createWaterMaskBitmap(src, watermark, 
                (src.getWidth() - watermark.getWidth()) / 2,
                (src.getHeight() - watermark.getHeight()) / 2);
    }

    /**
     * ��ͼƬ������ֵ����Ͻ�
     * @param context
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap drawTextToLeftTop(Context context, Bitmap bitmap, String text,
            int size, int color, int paddingLeft, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds, 
                dp2px(context, paddingLeft),  
                dp2px(context, paddingTop) + bounds.height());
    }

    /**
     * �������ֵ����½�
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    public static Bitmap drawTextToRightBottom(Context context, Bitmap bitmap, String text,
            int size, int color, int paddingRight, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds, 
                bitmap.getWidth() - bounds.width() - dp2px(context, paddingRight), 
                bitmap.getHeight() - dp2px(context, paddingBottom));
    }

    /**
     * �������ֵ����Ϸ�
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public static Bitmap drawTextToRightTop(Context context, Bitmap bitmap, String text,
            int size, int color, int paddingRight, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds, 
                bitmap.getWidth() - bounds.width() - dp2px(context, paddingRight), 
                dp2px(context, paddingTop) + bounds.height());
    }

    /**
     * �������ֵ����·�
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    public static Bitmap drawTextToLeftBottom(Context context, Bitmap bitmap, String text,
            int size, int color, int paddingLeft, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds, 
                dp2px(context, paddingLeft),  
                bitmap.getHeight() - dp2px(context, paddingBottom));
    }

    /**
     * �������ֵ��м�
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @return
     */
    public static Bitmap drawTextToCenter(Context context, Bitmap bitmap, String text,
            int size, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds, 
                (bitmap.getWidth() - bounds.width()) / 2,  
                (bitmap.getHeight() + bounds.height()) / 2);
    }

    //ͼƬ�ϻ�������
    private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text,
            Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // ��ȡ��������ͼ�����
        paint.setFilterBitmap(true);// ����һЩ
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }

    /**
     * ����ͼƬ
     * @param src
     * @param w
     * @param h
     * @return
     */
    public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // ��¼src�Ŀ��
            int width = src.getWidth();
            int height = src.getHeight();
            // ����һ��matrix����
            Matrix matrix = new Matrix();
            // �������ű���
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // ��ʼ����
            matrix.postScale(scaleWidth, scaleHeight);
            // �������ź��ͼƬ
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }
    
	public Bitmap addUserInfoToImage(Bitmap bitmap, Record record, Rect rect) {
		if (bitmap == null || rect == null) {
			return null;
		}

		String text = record.toOneLineString();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
        paint.setTextSize(20);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        paint.setDither(true); // ��ȡ��������ͼ�����
        paint.setFilterBitmap(true);// ����һЩ
        
        Paint mPaintLine = new Paint();
		mPaintLine.setColor(Color.GREEN);
		mPaintLine.setStyle(Style.STROKE);
		mPaintLine.setStrokeWidth(2);
		
        Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, (width - bounds.width()) / 2, height - bounds.height() - 5, paint);
        
        int left = rect.left;
        int top = rect.top;
        int right = rect.right;
        int bottom = rect.bottom;
        canvas.drawLine(left, top, right, top, mPaintLine);
        canvas.drawLine(left, top, left, bottom, mPaintLine);
        canvas.drawLine(right, top, right, bottom, mPaintLine);
        canvas.drawLine(right, bottom, left, bottom, mPaintLine);
        return bitmap;
	}

    /**
     * dipתpix
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp) { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int) (dp * scale + 0.5f); 
    } 
}