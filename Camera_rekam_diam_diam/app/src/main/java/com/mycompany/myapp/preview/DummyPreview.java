package com.mycompany.myapp.preview;
import android.view.*;
import android.hardware.*;
import com.mycompany.myapp.*;
import android.util.*;

public class DummyPreview extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    private VideoRecordService videoRecordService;
    private RecordThread recorderThread;
    private int serviceId;

    public DummyPreview(VideoRecordService videoRecordService, int serviceId) {
        super(videoRecordService);
        this.videoRecordService = videoRecordService;
        this.serviceId = serviceId;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open(1);
            camera.setPreviewDisplay(holder);
            recorderThread = new RecordThread(serviceId, videoRecordService, camera);
            recorderThread.start();
        } catch (Exception e) {
            Log.e("MyRecorder", "Terjadi kesalahan saat menampilkan preview...", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recorderThread != null) {
            recorderThread.setAktif(false);
        }
    }

    public boolean isAktif() {
        return (recorderThread == null)? false: recorderThread.isAktif();
    }

}
