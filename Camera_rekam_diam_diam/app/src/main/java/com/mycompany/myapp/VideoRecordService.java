package com.mycompany.myapp;
import android.app.*;
import com.mycompany.myapp.preview.*;
import android.content.*;
import android.view.*;
import android.os.*;
import android.graphics.*;

public class VideoRecordService extends Service {

    private LocalBinder localBinder = new LocalBinder();
    private DummyPreview dummyPreview;

    public DummyPreview getDummyPreview() {
        return dummyPreview;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        this.dummyPreview = new DummyPreview(this, startId);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(300, 300,
																	   WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
																	   WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.START | Gravity.TOP;
        wm.addView(dummyPreview, lp);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.removeViewImmediate(dummyPreview);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public class LocalBinder extends Binder {

        public void matikan() {
            stopSelf();
        }

        public boolean isAktif() {
            return (dummyPreview != null) && dummyPreview.isAktif();
        }

    }

}
