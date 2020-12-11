package com.rockchip.notedemo;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

//import androidx.annotation.RequiresApi;

import com.rockchip.notedemo.utils.FlashPoint;

import java.util.ArrayList;

public class NoteView extends View {
    private static final String TAG = "NoteView";

    private static final boolean DEBUG = true;

    private int mPenWidth = PEN_WIDTH_DEFAULT;

    private boolean mIsNativeInited = false;
    private boolean mIsNativeExited = false;
    private boolean mIsDrawReady = false;
    private boolean mIsWrited = false;

    private static final int DISPLAY_MODE_NDK 	= 0;
    private static final int DISPLAY_MODE_JAVA 	= 1;

    private static final int PEN_WIDTH_DEFAULT = 3;

    private NoteJNI mNativeJNI;
    private Context mCtx;

    private Rect mViewRect;

    private float mLastX = 0;
    private float mLastY = 0;

    private int mViewWidth;
    private int mViewHeight;

    private Paint mPaint;

    private PenThread mPenThread = null;

    private Bitmap mBitmap = null; //绘制当前页面使用
    private Canvas mCanvas = null;

    private TimeCountUtil mTimeCountUtil;


    private static ArrayList<FlashPoint> mPointList;

    public NoteView(Context context) {
        super(context);
		Log.d(TAG, "NoteView1");
        mCtx = context;
        initView();
    }

    public NoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
		Log.d(TAG, "NoteView2");
        mCtx = context;
        initView();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d(TAG, "Flash test : +++++ onDraw() ");
        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    private void initView() {
        setDrawingCacheEnabled(true);

        //Fixed parameters
        mPaint = new Paint();//Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(mPenWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setColor(0XFF000000);


        mPointList = new ArrayList<FlashPoint>();
        mPointList.clear();

        mDirtyRect = new RectF();

        mNativeJNI = new NoteJNI();

        mPenThread = new PenThread();

        mTimeCountUtil = new TimeCountUtil();


        invalidate();
    }

    /*
     * 初始化jni相关
     * 参数 rect:View边界距离屏幕的距离值px
     * 参数 bkPng:设置手写背景
     */
    public void initNative(Rect rect, String file_path) {
        Log.d(TAG, "Flash test : +++++++++ initNative() rect = " + rect);
        init(rect, file_path);
    }

    private void init(Rect rect, String file_path) {

            mNativeJNI.init(rect, file_path);
            setUIMode(DISPLAY_MODE_JAVA);
            invalidate();
    }

    private int mCount = 0;
    private int mSize = 0;
    private class PenThread extends Thread {
        public boolean canRun = true;
        public PenThread() {
        }

        public void setEnable(boolean enable) {
            canRun = enable;
        }
        @Override
        public void run() {
            while (canRun) {
                int size = mPointList.size();
                if (size > mCount) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //Log.d(TAG, "Flash test : +++++++ PenThread run() mSize = " + mSize + ", mCount = " + mCount);
                    while ((size - mCount) > 0) {
                        FlashPoint point = mPointList.get(mCount);
                        if (point != null) {
                            updateTouch(point);
                        }
                        mCount++;
                    }
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateTouch(FlashPoint p) {
        //Log.d(TAG, "Flash test : +++++++ updateTouch point = " + p);
        float x = p.x;
        float y = p.y;
        if (p.action == FlashPoint.ACTION_DOWN) { //down
            if (DEBUG) Log.d(TAG, "Flash test : ++++++++++ updateTouch in Down");
            mLastX = x;
            mLastY = y;
            mDirtyRect.left = x;
            mDirtyRect.right = x;
            mDirtyRect.top = y;
            mDirtyRect.bottom = y;
            mTimeCountUtil.cancel();
            if (p.strokesEnable == FlashPoint.PEN_STROKES_DISABLE) mPaint.setStrokeWidth(p.penWidth);
    		/*if (p.eraserEnable == FlashPoint.PEN_ERASE_ENABLE) {
    			mTimeCountUtil.onFinish();
    		}*/
        } else if (p.action == FlashPoint.ACTION_MOVE) { //move

            if (!mIsDrawReady) {
                if (p.strokesEnable == FlashPoint.PEN_STROKES_ENABLE) mPaint.setStrokeWidth(p.press * p.penWidth);
                mCanvas.drawPoint(x, y, mPaint);
                mLastX = x;
                mLastY = y;
                mDirtyRect.left = x;
                mDirtyRect.right = x;
                mDirtyRect.top = y;
                mDirtyRect.bottom = y;
                mIsDrawReady = true;
                return;
            }

            if (mLastX != 0 && mLastY != 0) {
                if (p.strokesEnable == FlashPoint.PEN_STROKES_ENABLE) mPaint.setStrokeWidth(p.press * p.penWidth);
                mCanvas.drawLine(mLastX, mLastY, x, y, mPaint);
                mIsWrited = true;
            } else if (x != 0 || y != 0) {
                if (p.strokesEnable == FlashPoint.PEN_STROKES_ENABLE) mPaint.setStrokeWidth(p.press * p.penWidth);
                mCanvas.drawPoint(x, y, mPaint);
                mIsWrited = true;
            }
            resetDirtyRect(x, y);
            mLastX = x;
            mLastY = y;
        } else if (p.action == FlashPoint.ACTION_UP) { //up
            if (DEBUG) Log.d(TAG, "Flash test : ++++++++++ updateTouch in Up");
            mLastX = 0;
            mLastY = 0;
            invalidate(
                    (int) (mDirtyRect.left),
                    (int) (mDirtyRect.top),
                    (int) (mDirtyRect.right),
                    (int) (mDirtyRect.bottom));
            mIsDrawReady = false;
            mTimeCountUtil.start();

        } else if (p.action == -1) {
            mLastX = x;
            mLastY = y;
        }
    }

    private RectF mDirtyRect;
    private void resetDirtyRect(float eventX, float eventY) {
        // The mLastTouchX and mLastTouchY were set when the ACTION_DOWN motion event occurred.
        mDirtyRect.left = Math.min(mDirtyRect.left, Math.min(mLastX, eventX));
        mDirtyRect.right = Math.max(mDirtyRect.right, Math.max(mLastX, eventX));
        mDirtyRect.top = Math.min(mDirtyRect.top, Math.min(mLastY, eventY));
        mDirtyRect.bottom = Math.max(mDirtyRect.bottom, Math.max(mLastY, eventY));
    }

    public class TimeCountUtil extends CountDownTimer {
        public static final long MILLISINFUTRUE = 1 * 2000;
        public static final long COUNTDOWNINTERVAL = 1 * 100;

        // 在这个构造方法里需要传入三个参数一个是总的时间millisInFuture，一个是countDownInterval，然后就是你在哪个按钮上做这个是，就把这个按钮传过来就可以了
        public TimeCountUtil(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public TimeCountUtil() {
            super(MILLISINFUTRUE, COUNTDOWNINTERVAL);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            setUIMode(DISPLAY_MODE_JAVA);
            cancel();
        }

        public void reset() {
            //重新获得点击
            cancel();
            start();
        }
    }

    public void setUIMode(int mode) {
        mNativeJNI.native_set_display_mode(mode);
    }

    public void setEnable(boolean status) {
        if (mIsNativeInited) {
            mNativeJNI.native_enable(status ? 1 : 0);
        }
        //if (status) setSleepMode(true);
    }

    public void setSleepMode(boolean sleep) {
        if (sleep) {
            setUIMode(DISPLAY_MODE_JAVA);
        } else {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            setUIMode(DISPLAY_MODE_NDK);
        }
    }

    public void exitNativeOnly() {
        mNativeJNI.native_exit();
    }

    public void exit() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }


        if (mPointList != null) {
            mPointList.clear();
            mSize = 0;
            mCount = 0;
        }

        /*if (!mIsNativeExited) {
    		mIsNativeInited = false;
    		mPenThread.setEnable(false);
    		mNativeJNI.native_exit();
    		mIsNativeExited = true;
    	}*/

        //requestEpdMode(View.EINK_MODE.EPD_FULL, true);
    }
}
