package com.sofar.widget.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ProgressBar;

import com.sofar.widget.R;
import com.sofar.widget.Util;


/**
 * 仿网易云音乐条形进度条
 */
public class MusicProgress extends ProgressBar {
  private static final int REACH_COLOR = 0xFFFF1493; // 已完成进度的默认颜色
  private static final int UN_REACH_COLOR = 0x90FF1493; // 未完成进度的默认颜色
  private static final int REACH_HEIGHT = 5; // dp,决定已完成ProgressBar的高度
  private static final int UN_REACH_HEIGHT = 5; // dp,决定未完成ProgressBar的高度

  protected int mReachColor;
  protected int mUnReachColor;
  protected int mReachHeight;
  protected int mUnReachHeight;
  protected boolean mRoundConner;

  private Paint mPaint;
  private int mRealWidth; // 进度条的真正长度
  private int minProgressX;

  public MusicProgress(Context context) {
    this(context, null);
  }

  public MusicProgress(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MusicProgress(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();

    // 获取自定义的属性
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MusicProgress);

    mReachColor = ta.getColor(R.styleable.MusicProgress_reachColor, mReachColor);
    mUnReachColor = ta.getColor(R.styleable.MusicProgress_unReachColor, mUnReachColor);
    mReachHeight = (int) (ta.getDimension(R.styleable.MusicProgress_reachHeight, mReachHeight));
    mUnReachHeight = (int) (ta.getDimension(R.styleable.MusicProgress_unReachHeight, mUnReachHeight));
    mRoundConner = ta.getBoolean(R.styleable.MusicProgress_roundConner, false);
    ta.recycle();

    // 初始化画笔
    mPaint = new Paint();
    mPaint.setAntiAlias(true); // 抗锯齿
    mPaint.setDither(true); // 抗抖动
    mPaint.setStyle(Paint.Style.FILL);
  }

  private void init() {
    mReachColor = REACH_COLOR;
    mUnReachColor = UN_REACH_COLOR;
    mReachHeight = Util.dp2px(getContext(), REACH_HEIGHT);
    mUnReachHeight = Util.dp2px(getContext(), UN_REACH_HEIGHT);
    minProgressX = Util.dp2px(getContext(), 5);
  }


  @Override
  protected synchronized void onDraw(Canvas canvas) {
    mRealWidth = getWidth() - getPaddingLeft() - getPaddingRight();
    int progressX = (int) (getProgress() * 1.0f / getMax() * mRealWidth);

    canvas.save();
    canvas.translate(getPaddingLeft(), getHeight() / 2);

    if (mRoundConner) {
      mPaint.setStrokeCap(Paint.Cap.ROUND); // 边缘为圆
      if ((progressX - mReachHeight / 2) < minProgressX) {
        progressX = mReachHeight / 2 + minProgressX;
      }
      if (progressX > mRealWidth - mReachHeight / 2) {
        progressX = mRealWidth - mReachHeight / 2;
      }
    }

    // 画unReachLine
    mPaint.setColor(mUnReachColor);
    mPaint.setStrokeWidth(mUnReachHeight);
    canvas.drawLine(mUnReachHeight / 2, 0, mRealWidth - mUnReachHeight / 2, 0, mPaint);

    // 画ReachLine
    mPaint.setColor(mReachColor);
    mPaint.setStrokeWidth(mReachHeight);
    canvas.drawLine(mReachHeight / 2, 0, progressX - mReachHeight / 2, 0, mPaint);

    canvas.restore();
  }

  @Override
  protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthValue = MeasureSpec.getSize(widthMeasureSpec);
    int height = measureHeight(heightMeasureSpec);
    setMeasuredDimension(widthValue, height);
  }

  private int measureHeight(int heightMeasureSpec) {

    int result = 0;
    int mode = MeasureSpec.getMode(heightMeasureSpec);
    int size = MeasureSpec.getSize(heightMeasureSpec);


    if (mode == MeasureSpec.EXACTLY) {
      result = size;
    } else {
      int h = getPaddingTop() + getPaddingBottom() + Math.max(mReachHeight, mUnReachHeight);
      result = h;
      if (mode == MeasureSpec.AT_MOST) {
        result = Math.min(size, h);
      }
    }
    return result;
  }

  public void setReachColor(int color) {
    mReachColor = color;
    invalidate();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event);
  }
}
