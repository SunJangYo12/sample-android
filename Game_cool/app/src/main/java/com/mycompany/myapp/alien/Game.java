package com.mycompany.myapp.alien;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;


/** Simple interface which hides most of the Android specifics. All method calls
 * are serialized. */
public interface Game {
	void LoadResources(Context context);

	boolean OnKeyDown(int key_code);
	boolean OnKeyUp(int key_code);
	boolean Update(float time_step, Canvas canvas);

	void Reset();
}
