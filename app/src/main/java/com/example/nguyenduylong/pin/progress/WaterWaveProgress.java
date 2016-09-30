package com.example.nguyenduylong.pin.progress;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Path.Direction;
import android.graphics.Region.Op;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

/**
 * @author Administrator
 * 
 */
public class WaterWaveProgress extends View {
	private Paint mPaintWater = null, mRingPaint = null, mTextPaint = null;


	private int mRingColor, mRingBgColor, mWaterColor, mWaterBgColor,
			mFontSize, mTextColor;

	float  crestCount = 1.5f;
	
	int mProgress = 10, mMaxProgress = 100;


	private Point mCenterPoint;

	private float mRingWidth, mProgress2WaterWidth;
	private boolean mShowProgress = false, mShowNumerical = true;

	private long mWaveFactor = 0L;

	private boolean isWaving = false;

	private float mAmplitude = 30.0F; // 20F

	private float mWaveSpeed = 0.070F; // 0.020F

	private int mWaterAlpha = 255; // 255
	WaterWaveAttrInit attrInit;

	private MyHandler mHandler = null;

	private static class MyHandler extends Handler {
		private WeakReference<WaterWaveProgress> mWeakRef = null;

		private int refreshPeriod = 100;

		public MyHandler(WaterWaveProgress host) {
			mWeakRef = new WeakReference<WaterWaveProgress>(host);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mWeakRef.get() != null) {
				mWeakRef.get().invalidate();
				sendEmptyMessageDelayed(0, refreshPeriod);
			}
		}
	}

	public WaterWaveProgress(Context paramContext) {
		super(paramContext);
	}

	public WaterWaveProgress(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
	}

	public WaterWaveProgress(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		attrInit = new WaterWaveAttrInit(context, attrs, defStyleAttr);
		init(context);
	}

	@SuppressLint("NewApi")
	private void init(Context context) {
		mCenterPoint = new Point();
		mRingColor = attrInit.getProgressColor();
		mRingBgColor = attrInit.getProgressBgColor();
		mWaterColor = attrInit.getWaterWaveColor();
		mWaterBgColor = attrInit.getWaterWaveBgColor();
		mRingWidth = attrInit.getProgressWidth();
		mProgress2WaterWidth = attrInit.getProgress2WaterWidth();
		mShowProgress = attrInit.isShowProgress();
		mShowNumerical = attrInit.isShowNumerical();
		mFontSize = attrInit.getFontSize();
		mTextColor = attrInit.getTextColor();
		mProgress = attrInit.getProgress();
		mMaxProgress = attrInit.getMaxProgress();


		if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
			setLayerType(View.LAYER_TYPE_HARDWARE, null);

		}
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(mRingColor);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mRingWidth);

		mPaintWater = new Paint();
		mPaintWater.setStrokeWidth(1.0F);
		mPaintWater.setColor(mWaterColor);
		// mPaintWater.setColor(getResources().getColor(mWaterColor));
		mPaintWater.setAlpha(mWaterAlpha);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mTextColor);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setTextSize(mFontSize);

		mHandler = new MyHandler(this);

	}

	public void animateWave() {
		if (!isWaving) {
			mWaveFactor = 0L;
			isWaving = true;
			mHandler.sendEmptyMessage(0);
		}
	}

	@SuppressLint({ "DrawAllocation", "NewApi" })
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 获取整个View（容器）的宽、高
		int width = getWidth();
		int height = getHeight();
		width = height = (width < height) ? width : height;
		mAmplitude = width / 20f;

		mCenterPoint.x = width / 2;
		mCenterPoint.y = height / 2;
		{
			mRingWidth = mRingWidth == 0 ? width / 20 : mRingWidth;
			mProgress2WaterWidth = mProgress2WaterWidth == 0 ? mRingWidth * 0.6f
					: mProgress2WaterWidth;
			mRingPaint.setStrokeWidth(mRingWidth);
			mTextPaint.setTextSize(mFontSize == 0 ? width / 5 : mFontSize);
			if (VERSION.SDK_INT==VERSION_CODES.JELLY_BEAN) {
				setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			}else {
				setLayerType(View.LAYER_TYPE_HARDWARE, null);
			}
		}

		RectF oval = new RectF();
		oval.left = mRingWidth / 2;
		oval.top = mRingWidth / 2;
		oval.right = width - mRingWidth / 2;
		oval.bottom = height - mRingWidth / 2;

		if (isInEditMode()) {
			mRingPaint.setColor(mRingBgColor);
			canvas.drawArc(oval, -90, 360, false, mRingPaint);
			mRingPaint.setColor(mRingColor);
			canvas.drawArc(oval, -90, 90, false, mRingPaint);
			canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mCenterPoint.x
					- mRingWidth - mProgress2WaterWidth, mPaintWater);
			return;
		}


		if ((width == 0) || (height == 0) || isInEditMode()) {
			canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, width / 2
					- mProgress2WaterWidth - mRingWidth, mPaintWater);
			return;
		}


		float waterPadding = mShowProgress ? mRingWidth + mProgress2WaterWidth
				: 0;

		int waterHeightCount = mShowProgress ? (int) (height - waterPadding * 2)
				: height;


		mWaveFactor++;
		if (mWaveFactor >= Integer.MAX_VALUE) {
			mWaveFactor = 0L;
		}


		mRingPaint.setColor(mRingBgColor);

		canvas.drawCircle(width / 2, width / 2, waterHeightCount / 2
				+ waterPadding - mRingWidth / 2, mRingPaint);
		mRingPaint.setColor(mRingColor);

		canvas.drawArc(oval, -90, (mProgress*1f) / mMaxProgress * 360f, false,
				mRingPaint);


		float waterHeight = waterHeightCount * (1 - (mProgress*1f) / mMaxProgress)
				+ waterPadding;
		int staticHeight = (int) (waterHeight + mAmplitude);
		Path mPath = new Path();
		mPath.reset();
		if (mShowProgress) {
			mPath.addCircle(width / 2, width / 2, waterHeightCount / 2,
					Direction.CCW);
		} else {
			mPath.addCircle(width / 2, width / 2, waterHeightCount / 2,
					Direction.CCW);
		}

		canvas.clipPath(mPath, Op.REPLACE);
		Paint bgPaint = new Paint();
		bgPaint.setColor(mWaterBgColor);

		canvas.drawRect(waterPadding, waterPadding, waterHeightCount
				+ waterPadding, waterHeightCount + waterPadding, bgPaint);

		canvas.drawRect(waterPadding, staticHeight, waterHeightCount
				+ waterPadding, waterHeightCount + waterPadding, mPaintWater);


		int xToBeDrawed = (int) waterPadding;
		int waveHeight = (int) (waterHeight - mAmplitude
				* Math.sin(Math.PI
						* (2.0F * (xToBeDrawed + (mWaveFactor * width)
								* mWaveSpeed)) / width));

		int newWaveHeight = waveHeight;
		while (true) {
			if (xToBeDrawed >= waterHeightCount + waterPadding) {
				break;
			}

			newWaveHeight = (int) (waterHeight - mAmplitude
					* Math.sin(Math.PI
							* (crestCount * (xToBeDrawed + (mWaveFactor * waterHeightCount)
									* mWaveSpeed)) / waterHeightCount));


			canvas.drawLine(xToBeDrawed, waveHeight, xToBeDrawed + 1,
					newWaveHeight, mPaintWater);


			canvas.drawLine(xToBeDrawed, newWaveHeight, xToBeDrawed + 1,
					staticHeight, mPaintWater);
			xToBeDrawed++;
			waveHeight = newWaveHeight;
		}
		if (mShowNumerical) {
			String progressTxt = String.format("%.0f", (mProgress*1f) / mMaxProgress
					* 100f)
					+ "%";
			float mTxtWidth = mTextPaint.measureText(progressTxt, 0,
					progressTxt.length());
			canvas.drawText(progressTxt, mCenterPoint.x - mTxtWidth / 2,
					mCenterPoint.x * 1.5f - mFontSize / 2, mTextPaint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = widthMeasureSpec;
		int height = heightMeasureSpec;
		width = height = (width < height) ? width : height;
		setMeasuredDimension(width, height);
	}

	/**
	 * 设置波浪的振幅
	 */
	public void setAmplitude(float amplitude) {
		mAmplitude = amplitude;
	}

	/**
	 * 设置水的透明度
	 * 
	 * @param alpha
	 *            透明的百分比，值为0到1之间的小数，越接近0越透明
	 */
	public void setWaterAlpha(float alpha) {
		mWaterAlpha = (int) (255.0F * alpha);
		mPaintWater.setAlpha(mWaterAlpha);
	}

	/** 设置水的颜色 */
	public void setWaterColor(int color) {
		mWaterColor = color;
	}

	/**
	 * 设置当前进度
	 */
	public void setProgress(int progress) {
		progress = progress > 100 ? 100 : progress < 0 ? 0 : progress;
		mProgress = progress;
		invalidate();
	}

	/** 获取进度 动画时会用到 */
	public int getProgress() {
		return mProgress;
	}

	/**
	 * 设置波浪速度
	 */
	public void setWaveSpeed(float speed) {
		mWaveSpeed = speed;
	}

	/**
	 * 是否显示进度条
	 * 
	 * @param boolean
	 */
	public void setShowProgress(boolean b) {
		mShowProgress = b;
	}

	/**
	 * 是否显示进度值
	 * 
	 * @param boolean
	 */
	public void setShowNumerical(boolean b) {
		mShowNumerical = b;
	}

	/**
	 * 设置进度条前景色
	 * 
	 * @param mRingColor
	 */
	public void setmRingColor(int mRingColor) {
		this.mRingColor = mRingColor;
	}

	/**
	 * 设置进度条背景色
	 * 
	 * @param mRingBgColor
	 */
	public void setmRingBgColor(int mRingBgColor) {
		this.mRingBgColor = mRingBgColor;
	}

	/**
	 * 设置水波颜色
	 * 
	 * @param mWaterColor
	 */
	public void setmWaterColor(int mWaterColor) {
		this.mWaterColor = mWaterColor;
	}

	/**
	 * 设置水波背景色
	 * 
	 * @param mWaterBgColor
	 */
	public void setWaterBgColor(int mWaterBgColor) {
		this.mWaterBgColor = mWaterBgColor;
	}

	/**
	 * 设置进度值显示字体大小
	 * 
	 * @param mFontSize
	 */
	public void setFontSize(int mFontSize) {
		this.mFontSize = mFontSize;
	}

	/**
	 * 设置进度值显示字体颜色
	 * 
	 * @param mTextColor
	 */
	public void setTextColor(int mTextColor) {
		this.mTextColor = mTextColor;
	}

	/**
	 * 设置进度条最大值
	 * 
	 * @param mMaxProgress
	 */
	public void setMaxProgress(int mMaxProgress) {
		this.mMaxProgress = mMaxProgress;
	}

	/**
	 * 设置浪峰个数
	 * 
	 * @param crestCount
	 */
	public void setCrestCount(float crestCount) {
		this.crestCount = crestCount;
	}

	/**
	 * 设置进度条宽度
	 * 
	 * @param mRingWidth
	 */
	public void setRingWidth(float mRingWidth) {
		this.mRingWidth = mRingWidth;
	}

	/**
	 * 设置水波到进度条之间的距离
	 * 
	 * @param mProgress2WaterWidth
	 */
	public void setProgress2WaterWidth(float mProgress2WaterWidth) {
		this.mProgress2WaterWidth = mProgress2WaterWidth;
	}

}