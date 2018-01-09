package com.mycompany.myapp.alien;


import android.graphics.Rect;
import android.view.KeyEvent;
import java.lang.Math;

import com.mycompany.myapp.alien.Entity;


public class Darah extends Entity {
	public Darah() {
		super();
		sprite_source =
			new Rect(0, kSpriteBase, kSpriteWidth, kSpriteBase + kSpriteHeight);
	}

	public void Step(float time_step) {
		super.Step(time_step);

		time_remaining -= time_step;
		if (time_remaining < 0) {
			alive = false;  // Signal for deletion.
		}
	}

	private float time_remaining = kTimeRemaining;

	private static final float kTimeRemaining = 0.75f;  // Seconds.
	private static final int kSpriteBase = 64;
	private static final int kSpriteWidth = 64;
	private static final int kSpriteHeight = 64;
}
