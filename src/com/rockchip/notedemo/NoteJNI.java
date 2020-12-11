package com.rockchip.notedemo;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rockchip.notedemo.utils.FlashPoint;

public class NoteJNI {
    private static final String TAG = "NoteJNI";
    public static final int MSG_FLAG_TOUCH	= 10000;
    public static final int MSG_FLAG_UPDATE	= 10001;

    private static Handler mPointHandler = null;

    // JNI
    public native int native_init(Rect rect, String file_path);
	public native int native_enable(int status);
    public native int native_exit();
	public native int native_touch_down(float x, float y);
	public native int native_touch_up();
	public native int native_add_point(float x, float y);
    public native int native_clear();
	public native int native_save(String file_path);
	public native int native_update();
    public native int native_set_display_mode(int mode);

    public NoteJNI() {
		Log.d(TAG, "NoteJNI");
        //native_init(height);
        //receiveEMREvent(0.0f, 0.0f, 3, 0.0f, 0, 0, -1, true);
        getClassLoader();
        System.loadLibrary("paintworker");
    }

    /*public WeNoteJNI(int height, String filePath) {
    	native_init(new Rect(0, height, 0, 1872), filePath);
    }*/

    public void init(Rect rect, String file_path) {
        Log.d(TAG, "Flash test : ++++++++++ init() rect = " + rect);
        native_init(rect, file_path);
    }


    public static ClassLoader getClassLoader() {
        Log.d(TAG,"zj add getClassLoader");
        return NoteJNI.class.getClassLoader();
    }

    /*static {
        //System.loadLibrary("wenote_jni");
        System.loadLibrary("paintworker");
    }*/

    public static void receiveEMREvent(float x, float y, int penWidth, float press, int eraserEnable, int strokesEnable, int action, boolean isRedraw) {
        //Log.d(TAG, "Flash test : ++++++++++ receiveEMREvent() updateTouch point = (x, y, press, acttion) = (" + x + ", " + y + ",  " + press + ", " + action + ")");
        Log.d(TAG,"receiveEMREvent");
		if(mPointHandler == null) {
            Log.i(TAG, "Flash test : ++++ receiveEMREvent() mPointHandler is null");
            return;
        }

        Message msg = mPointHandler.obtainMessage();
        FlashPoint point = new FlashPoint(x, y, penWidth, press, eraserEnable, strokesEnable, action);
        msg.what = MSG_FLAG_TOUCH;
        msg.obj = (Object) point;
        msg.arg1 = isRedraw ? 1 : 0;
        msg.sendToTarget();
    }

    public static void forceUpdateJava() {
        //Log.d(TAG, "Flash test : ++++++++++ receiveEMREvent() (x, y, press, acttion) = (" + x + ", " + y + ",  " + press + ", " + action + ")");
        Log.d(TAG,"forceUpdateJava");
		if(mPointHandler == null) {
            Log.i(TAG, "Flash test : ++++ forceUpdateJava() mPointHandler is null");
            return;
        }

        Message msg = mPointHandler.obtainMessage();
        msg.what = MSG_FLAG_UPDATE;
        msg.sendToTarget();
    }


    public void setPointEventHandler(Handler handler) {
        mPointHandler = handler;
    }

}
