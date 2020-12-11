package com.rockchip.notedemo;

//import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import com.rockchip.notedemo.utils.Point;

import java.util.ArrayList;
import java.util.List;
import android.os.EinkManager;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private static Context mContext;
    private static Handler mHandler;

    private static NoteView mView;

    private static int mScreenH;
    private static int mScreenW;
	
	private static EinkManager mEinkManager;

    private static ArrayList<Point> mPointList = null;
	private String mEinkMode;

    private static Runnable mRunnable = new Runnable() {
        public void run() {
            int count = 0;
            while(mView.getHeight() <= 0) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (count++ > 40) {
                    Log.d(TAG, "Flash test : ++++++++ removeCallbacks");
                    mHandler.removeCallbacks(mRunnable);
                    System.exit(0);
                }
                Log.d(TAG, "Flash test : ++++++++ mView.getHeight() = " + mView.getHeight() + ", count = " + count);
            }
            //mView.initNative(new Rect(mScreenW, mScreenH - mView.getHeight(), 0, mScreenH), SAVE_PIC_PATH);
            mView.initNative(new Rect(mScreenW, mScreenH - mView.getHeight(), 0, mScreenH), "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Flash test : +++++++ onCreate()");
        super.onCreate(savedInstanceState);
		mContext = MainActivity.this;
		if(mEinkManager == null){
            mEinkManager = (EinkManager) mContext.getSystemService(Context.EINK_SERVICE);
        }
		mEinkMode = mEinkManager.getMode();
		Log.d(TAG, "mEinkMode : " + mEinkMode);
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mView = (NoteView) findViewById(R.id.note_view);
        Log.d(TAG, "getHeight: " + mView.getHeight());
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 50);
    }

    public void onResume() {
        Log.d(TAG, "Flash test : +++++++ onResume()");
        /*mView.setEnable(true);
        mView.setSleepMode(true);*/
        super.onResume();
    }

    public void onPause() {
        Log.d(TAG, "Flash test : +++++++ onPause()");

        //mView.setEnable(false);
        super.onPause();
    }

    public void onDestroy() {
        Log.d(TAG, "Flash test : +++++++ onDestroy()");
        mView.exitNativeOnly();
        mHandler.removeCallbacks(mRunnable);
        mView.exit();
        System.exit(0);
		mEinkManager.setMode(mEinkMode);
        super.onDestroy();
    }

}
