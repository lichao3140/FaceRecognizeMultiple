package com.frm.cn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class OverlayerView extends View
{
	public final static String TAG = OverlayerView.class.getSimpleName();
	private final static String LIVE_BODY = "living";
	private int width = 0;
	private int height = 0;

	private float wRate = 0.0f;
	private float hRate = 0.0f;

	private Paint mPaint = null;
	private int len = 30;
	private float pLeft = 0;
	private float pRight = 0;
	private float pTop = 0;
	private float pBottom = 0;
	
	private int padding = 15;
	
	Path path = null;

	private boolean isLive = false;
	private Paint textPaint = new Paint();
	
	private Paint mPaintP = null;
    private Rect pRect = new Rect();

	private Handler handler = new Handler();

	private volatile boolean display = false;

	private int STATUS_TIME_OUT = 500;

	public OverlayerView(Context context)
	{
		super(context);
		this.init();
	}

	public OverlayerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.init();
	}

	public OverlayerView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		this.init();
	}

	private void init()
	{
		setLayerType(LAYER_TYPE_SOFTWARE, null);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		
		mPaint = new Paint();
		mPaint.setColor(Color.GREEN);
		//mPaint.setColor(Color.RED);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(1);

		mPaintP = new Paint();
		mPaintP.setColor(Color.RED);
		mPaintP.setStyle(Style.STROKE);
		mPaintP.setPathEffect(new DashPathEffect(new float[]{8, 4}, 0));
		mPaintP.setStrokeWidth(1);
		
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(16);
		mPaintP.setDither(true); 
		// 过滤一下，抗剧齿
		mPaintP.setFilterBitmap(true);
        textPaint.setStyle(Paint.Style.FILL);  
        //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center  
        textPaint.setTextAlign(Paint.Align.LEFT); 
		
		path = new Path();
		
		padding = FaceApp.DEFAULT_DETECT_PADDING;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		canvas.drawRect(pRect, mPaintP);
		
		if (isLive) {
			textPaint.setColor(Color.GREEN);
		    canvas.drawText(LIVE_BODY, 10, 20, textPaint);
		}
		
		if (display) {
			path.reset();
			// left-top
			path.moveTo(pLeft + len, pTop);
			path.lineTo(pLeft, pTop);
			path.lineTo(pLeft, pTop + len);
			//canvas.drawPath(path, mPaint);
			
				// left-bottom
			path.moveTo(pLeft + len, pBottom);
			path.lineTo(pLeft, pBottom);
			path.lineTo(pLeft , pBottom - len);
			//canvas.drawPath(path, mPaint);
			
				// right-top
			path.moveTo(pRight - len, pTop);
			path.lineTo(pRight, pTop);
			path.lineTo(pRight, pTop + len);
			//canvas.drawPath(path, mPaint);
			
				// right-bottom
			path.moveTo(pRight - len, pBottom);
			path.lineTo(pRight, pBottom);
			path.lineTo(pRight, pBottom - len);
			canvas.drawPath(path, mPaint);
		}
	}
	
	Runnable runnableClean = new Runnable()
	{
		@Override
		public void run()
		{
			display = false;
			OverlayerView.this.invalidate();
		}
	};
	
	Runnable runnableCleanLive = new Runnable()
	{
		@Override
		public void run()
		{
			isLive = false;
			OverlayerView.this.invalidate();
		}
	};

	public void setRecognizeResult(int left, int top, int right, int bottom)
	{
		if (right != 0 && 
			bottom != 0 &&
			(left < right) &&
			(top < bottom))
		{
			handler.removeCallbacks(runnableClean);
			handler.postDelayed(runnableClean, STATUS_TIME_OUT);
			pLeft = left * wRate;
			pRight = right * wRate;
			pTop = top * hRate;
			pBottom = bottom * hRate;
			display = true;
			this.invalidate();
		}
	}


	public void setRate(float wRate, float hRate) {
		this.wRate = wRate;
		this.hRate = hRate;
		if (width >= padding * 2) {
			pRect.left = (int)(padding * wRate);
			pRect.right = (int)(width - padding * wRate);
		}
		if (height >= padding * 2) {
			pRect.top = (int)(padding * hRate);
			pRect.bottom = (int)(height - padding * hRate);
		}
		log("pRect.left:" + pRect.left + " pRect.top:" + pRect.top);
		this.invalidate();
	}

	public void setLive(boolean isLive) {
		handler.removeCallbacks(runnableCleanLive);
		handler.postDelayed(runnableCleanLive, STATUS_TIME_OUT);
		this.isLive = isLive;
	}

	private void log(String msg) {
		Log.d(TAG, msg);
	}
}
