package com.leon.assistivetouch.main.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.leon.assistivetouch.main.SettingActivity;
import com.leon.assistivetouch.main.bean.KeyItemInfo;
import com.leon.assistivetouch.main.util.L;
import com.leon.assistivetouch.main.util.Settings;
import com.leon.assistivetouch.main.util.VibratorHelper;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

/** 
 * 类名      TouchView.java
 * 说明   全局的Touch View
 * 创建日期 2012-8-20
 * 作者  LiWenLong
 * Email lendylongli@gmail.com
 * 更新时间  $Date$
 * 最后更新者 $Author$
 */
public class TouchView {

	private static final String TAG = "TouchView"; 
	
	private static Object mLock = new Object();
	private static TouchView  current;
	
	public static TouchView getInstance (Context context) {
		synchronized (mLock) {
			if (current == null) {
				current = new TouchView(context);
			}
			return current;
		}
	}
	
	
	private Context mContext;
	private Settings mSetting;
	
	private WindowManager.LayoutParams mTouchDotParams = null;
	private WindowManager.LayoutParams mTouchMainParams = null;
	private WindowManager mWindowManager = null;
	private TouchDotView mTouchDotView = null;
	private TouchMainView mTouchMainView = null;
	
	private int mCurrentShowing = 0;
	
	public static final int SHOWING_DOTVIEW = 1;
	public static final int SHOWING_MAINVIEW = 2;
	
	public TouchView (Context context) {
		this.mContext = context;
		this.mSetting = Settings.getInstance(context);
		
		mTouchDotParams = new WindowManager.LayoutParams();
		mTouchMainParams = new WindowManager.LayoutParams();
		mWindowManager = (WindowManager) context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
	}
	
	public void removeView () {
		if (mWindowManager != null) {
			if (mCurrentShowing == SHOWING_DOTVIEW) {
				mWindowManager.removeView(mTouchDotView);
				mCurrentShowing = 0;
			} else if (mCurrentShowing == SHOWING_MAINVIEW) {
				mWindowManager.removeView(mTouchMainView);
				mCurrentShowing = 0;
			}
		}
	}
	
	public void showView () {
		setupLayoutParams ();
		showTouchDotView();
	}
	
	public int getShowingView () {
		return mCurrentShowing;
	}
	
	private void setupLayoutParams () {
		// 设置window type
		mTouchDotParams.type = LayoutParams.TYPE_PRIORITY_PHONE;
		// 设置图片格式，效果为背景透明
		mTouchDotParams.format = PixelFormat.RGBA_8888;

		/* 设置Window flag
		/*
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */
		mTouchDotParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		// 调整悬浮窗口至左上角，便于调整坐标
		mTouchDotParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值
		mTouchDotParams.x = mSetting.getTouchPositionX();
		mTouchDotParams.y = mSetting.getTouchPositionY();

		// 设置悬浮窗口长宽数据
		mTouchDotParams.width = LayoutParams.WRAP_CONTENT;
		mTouchDotParams.height = LayoutParams.WRAP_CONTENT;
		
		
		/*----*/
		mTouchMainParams.type = LayoutParams.TYPE_PRIORITY_PHONE;
		mTouchMainParams.format = PixelFormat.RGBA_8888;
		mTouchMainParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		mTouchMainParams.gravity = Gravity.CENTER;
		mTouchMainParams.width = LayoutParams.WRAP_CONTENT;
		mTouchMainParams.height = LayoutParams.WRAP_CONTENT;
	}

	public WindowManager.LayoutParams getTouchDotParams() {
		return mTouchDotParams;
	}
	
	public WindowManager.LayoutParams getTouchMainParams () {
		return mTouchMainParams;
	}

	public void showTouchDotView() {
		if (mTouchDotView == null) {
			createTouchDotView();
		}
		if (mCurrentShowing == SHOWING_DOTVIEW) {
			return;
		} else if (mCurrentShowing == SHOWING_MAINVIEW) {
			mWindowManager.removeView(mTouchMainView);
		}
		// 显示TouchDotView
		mWindowManager.addView(mTouchDotView, mTouchDotParams);
		mCurrentShowing = SHOWING_DOTVIEW;
	}
	
	public void showTouchMainView() {
		if (mTouchMainView == null) {
			createTouchMainView();
		}
		if (mCurrentShowing == SHOWING_MAINVIEW) {
			return;
		} else if (mCurrentShowing == SHOWING_DOTVIEW) {
			mWindowManager.removeView(mTouchDotView);
		}
		mWindowManager.addView(mTouchMainView, mTouchMainParams);
		mCurrentShowing = SHOWING_MAINVIEW;
	}
	
	public void reload () {
		int showing = mCurrentShowing;
		removeView();
		mTouchDotView = null;
		mTouchMainView = null;
		if (showing == SHOWING_DOTVIEW) {
			showTouchDotView();
		} else if (showing == SHOWING_MAINVIEW) {
			showTouchMainView();
		}
	}

	private void createTouchDotView() {
		mTouchDotView = new TouchDotView(mContext.getApplicationContext());
		mTouchDotView.setOnTouchDotViewListener(mScrollListener);
		ImageView img = mTouchDotView.getTouchDotImageView();
		int size = mSetting.getTouchDotSize();
		SettingActivity.changeImageViewSize(mContext, img, size);
		int alpha = mSetting.getTouchDotTransparency();
		img.setAlpha(alpha);
	}
	
	private void createTouchMainView () {
		mTouchMainView = new TouchMainView(mContext.getApplicationContext());
		mTouchMainView.setOnKeyClickListener(mOnKeyClickListener);
		List<KeyItemInfo> list = new ArrayList<KeyItemInfo>();
		Map<Integer, KeyItemInfo> map = mSetting.getMainItemMap();
		
		L.d(TAG, "map size=" + map.size());
		
		for (int i = 1; i <= map.size(); i ++) {
			KeyItemInfo info = map.get(i);
			list.add(info);
		}
		mTouchMainView.setKeyList(list);
	}
	
	/**
	 * 触发按键事件处理
	 * */
	private void onKeyItemInfoEvent (KeyItemInfo info) {
		switch (info.getType()) {
		case KeyItemInfo.TYPE_APP:
			break;
		case KeyItemInfo.TYPE_KEY:
			if (info.getData().equals(String.valueOf(KeyItemInfo.KEY_HIDE))) {
				showTouchDotView();
			} else {
				KeyItemInfo.doKeyEvent(info.getData());
			}
			break;
		case KeyItemInfo.TYPE_TOOL:
			break;
		}
		boolean enable = mSetting.isEnableVirbrator();
		if (enable) {
			VibratorHelper.KeyVibrate(mContext);
		}
	}
	
	private TouchDotView.OnTouchDotViewListener mScrollListener = 
			new TouchDotView.OnTouchDotViewListener() {
		@Override
		public void onScrollTo(View v, int x, int y) {
			mTouchDotParams.x = x;
			mTouchDotParams.y = y;
			mWindowManager.updateViewLayout(v, mTouchDotParams);
		}

		@Override
		public void onTouchUp(View view, int x, int y) {
			mSetting.setTouchPosition(x, y);
		}

		@Override
		public void onSingleTap(View view) {
			showTouchMainView ();
		}

		@Override
		public void onLongPress() {
			boolean enable_long_press = mSetting.isEnableLongPress();
			if (enable_long_press) {
				L.Toast("长按事件", mContext);
				//onKeyItemInfoEvent (null);
			}
		}

		@Override
		public boolean onDoubleTap() {
			boolean enable_double_tap = mSetting.isEnableDoubleTap();
			if (enable_double_tap) {
				L.Toast("双击事件", mContext);
				//onKeyItemInfoEvent (null);
			}
			return enable_double_tap;
		}
	};
	
	private TouchMainView.OnKeyClickListener mOnKeyClickListener = 
			new TouchMainView.OnKeyClickListener() {
		@Override
		public void onClick(KeyItemInfo info) {
			onKeyItemInfoEvent(info);
			showTouchDotView();
		}
	};
}
