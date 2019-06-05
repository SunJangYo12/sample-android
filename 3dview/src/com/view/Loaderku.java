package com.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.util.Log;

import com.view.lib.engine.services.Object3DBuilder;
import com.view.lib.engine.model.Object3DData;
import com.view.lib.util.android.ContentUtils;
import com.view.lib.util.io.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class loads a 3D scene as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class Loaderku extends SceneLoader {

    public static float x, y, z = 0;

	public Loaderku(MainActivity modelActivity, Context context) {
		super(modelActivity, context);
	}

	// TODO: fix this warning
	@SuppressLint("StaticFieldLeak")
    public void init() {
		super.init();
		new AsyncTask<Void, Void, Void>() {

			List<Exception> errors = new ArrayList<>();

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

            @Override
            protected Void doInBackground(Void... params) {
                Log.i("oke", "dsds");
                try {
                    // 3D Axis
                    Object3DData axis = Object3DBuilder.buildAxis().setId("axis");
                    axis.setColor(new float[] { 1.0f, 0, 0, 1.0f });
                    addObject(axis);

                    try {
                        // this has color array
                        Object3DData obj52 = Object3DBuilder.loadV5(parent, Uri.parse("file:///storage/emulated/0/teapot.obj"));
                        obj52.centerAndScale(9.0f);
                        obj52.setPosition(new float[] { x, y, z });
                        obj52.setColor(new float[] { 0.0f, 1.0f, 1f, 1.0f });
                        addObject(obj52);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }

                } catch (Exception ex) {
                    errors.add(ex);
                } finally{
                    ContentUtils.setThreadActivity(null);
                    ContentUtils.clearDocumentsProvided();
                }
                return null;
            }

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				
				if (!errors.isEmpty()) {
					StringBuilder msg = new StringBuilder("There was a problem loading the data");
					for (Exception error : errors) {
						Log.e("Example", error.getMessage(), error);
						msg.append("\n" + error.getMessage());
					}
				}
			}
		}.execute();
	}
}
