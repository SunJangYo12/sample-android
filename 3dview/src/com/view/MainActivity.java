package com.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.graphics.Color;
import android.widget.ActionMenuView.LayoutParams;
import java.io.IOException;

import com.view.lib.util.android.ContentUtils;

public class MainActivity extends Activity {

    private int paramType;
    private Uri paramUri;
    private float[] backgroundColor = new float[]{0.2f, 0.2f, 0.2f, 1.0f};
    private ModelSurfaceView gLView;
    private Loaderku scene;

    public Button btnX;
    public EditText edt;
    public Button btnY;
    public static float tes = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        //this.paramUri = Uri.parse("file:///storage/emulated/0/teapot.obj");

        scene = new Loaderku(this, this);

        scene.init();
        gLView = new ModelSurfaceView(this);

        edt = new EditText(this);
        RelativeLayout.LayoutParams paramsED = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
       
        btnX = new Button(this);
        RelativeLayout.LayoutParams paramsX = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnX.setText("X");

        Button btnY = new Button(this);
        RelativeLayout.LayoutParams paramsY = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnY.setText("Y");

        Button btnZ = new Button(this);
        RelativeLayout.LayoutParams paramsZ = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnZ.setText("Z");

        LinearLayout layoutView = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutView.setOrientation(LinearLayout.VERTICAL);
        layoutView.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams params3d = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutView.setBackgroundColor(Color.TRANSPARENT);
        layoutView.addView(edt, paramsED);
        layoutView.addView(btnX, paramsX);
        layoutView.addView(btnY, paramsY);
        layoutView.addView(btnZ, paramsZ);
        layoutView.addView(gLView, params3d);

        setContentView(layoutView);

        btnX.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tes = Float.parseFloat(edt.getText().toString());
            }
        });

        btnY.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        btnZ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "z", Toast.LENGTH_LONG).show();
                scene.z += 1;

            }
        });
	}

	

    public Uri getParamUri() {
        return paramUri;
    }

    public int getParamType() {
        return paramType;
    }

    public float[] getBackgroundColor() {
        return backgroundColor;
    }

    public Loaderku getScene() {
        return scene;
    }

    public ModelSurfaceView getGLView() {
        return gLView;
    }

}
