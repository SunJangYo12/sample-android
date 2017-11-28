package com.mycompany.myapp;
import android.hardware.*;
import java.io.*;
import android.os.*;
import java.text.*;
import android.util.*;
import android.media.*;
import java.util.*;

public class RecordThread extends Thread {

    private boolean aktif;
    private int serviceId;
    private final VideoRecordService recorderService;
    private Camera camera;

    public RecordThread(int serviceId, VideoRecordService recorderService, Camera camera) {
        this.serviceId = serviceId;
        this.aktif = true;
        this.recorderService = recorderService;
        this.camera = camera;
    }

    @Override
    public void run() {
        // Tentukan lokasi penyimpanan dan nama file
        File picDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "MyRecorder");
        if (!picDir.exists()) {
            picDir.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String outputFile = picDir.getAbsolutePath() + File.separator +  "REC_" + timeStamp + ".mp4";
        Log.d("MyRecorder", "Menyimpan ke " + outputFile);

        try {
            // Memulai proses rekaman
            MediaRecorder mediaRecorder = new MediaRecorder();
            camera.unlock();
            mediaRecorder.setCamera(camera);
            mediaRecorder.setOrientationHint(270);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            mediaRecorder.setOutputFile(outputFile);
            mediaRecorder.setPreviewDisplay(recorderService.getDummyPreview().getHolder().getSurface());
            mediaRecorder.prepare();
            mediaRecorder.start();
            aktif = true;
            while (aktif) {
                Thread.sleep(100);
            }
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();

            // Publikasikan file
            MediaScannerConnection.scanFile(recorderService, new String[]{outputFile}, new String[]{"video/mp4"}, null);
        } catch (Exception ex) {
            Log.e("MyRecorder", "Terjadi kesalahan saat merekam", ex);
        } finally {
            camera.release();
            recorderService.stopSelf(serviceId);
        }
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

}
