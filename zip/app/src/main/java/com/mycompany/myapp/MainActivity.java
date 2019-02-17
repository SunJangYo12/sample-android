package com.mycompany.myapp;

import android.app.*;
import android.os.*;
import java.util.*;
import android.widget.*;
import java.io.*;
import android.view.*;
import java.util.zip.*;
import android.util.*;

public class MainActivity extends Activity 
{

    private static final int BUFFER = 2048;
    private CompressFiles mCompressFiles;
    private ArrayList<String> mFilePathList = new ArrayList<>();
    private TextView mProgressView;

    public static File getOutputZipFile(String fileName) {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "devdeeds");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mProgressView = (TextView) findViewById(R.id.progress_text_view);
        Button btnZip = (Button) findViewById(R.id.btn_zip);

        //Add Files To Zip
        addFilesToZip();

        btnZip.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					mCompressFiles = new CompressFiles();
                    mCompressFiles.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			});
    }

    private void addFilesToZip() {
        //mFilePathList.add("/storage/emulated/0/File1.jpg");
        //mFilePathList.add("/storage/emulated/0/File2.jpg");
        //mFilePathList.add("/storage/emulated/0/File3.jpg");
        mFilePathList.add("/storage/emulated/0/build.prop");
        mFilePathList.add("/storage/emulated/0/olo");
    }

    //Function will get the call from compress function
    public void setCompressProgress(int filesCompressionCompleted) {
        mCompressFiles.publish(filesCompressionCompleted);
    }

    //Zipping function
    public void zip(String zipFilePath) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFilePath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for (int i = 0; i < mFilePathList.size(); i++) {

                setCompressProgress(i + 1);

                FileInputStream fi = new FileInputStream(mFilePathList.get(i));
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(mFilePathList.get(i).substring(mFilePathList.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
			Toast.makeText(MainActivity.this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    //zip() will be called from this AsyncTask as this is long task.
    private class CompressFiles extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            try {
                mProgressView.setText("0% Completed");
            } catch (Exception ignored) {
            }
        }

        protected Boolean doInBackground(Void... urls) {

            File file = getOutputZipFile("zipped_file.zip");

            String zipFileName;
            if (file != null) {
                zipFileName = file.getAbsolutePath();

                if (mFilePathList.size() > 0) {
                    zip(zipFileName);
                }
            }

            return true;
        }

        public void publish(int filesCompressionCompleted) {
            int totalNumberOfFiles = mFilePathList.size();
            publishProgress((100 * filesCompressionCompleted) / totalNumberOfFiles);
        }


        protected void onProgressUpdate(Integer... progress) {

            try {
                mProgressView.setText(Integer.toString(progress[0]) + "% Completed");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        protected void onPostExecute(Boolean flag) {
            Log.d("COMPRESS_TASK", "COMPLETED");
            mProgressView.setText("100 % Completed");
            Toast.makeText(getApplicationContext(), "Zipping Completed", Toast.LENGTH_SHORT).show();
        }
    }
}
