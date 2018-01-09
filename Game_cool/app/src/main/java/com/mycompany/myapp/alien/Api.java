package com.mycompany.myapp.alien;

import android.graphics.Rect;
import android.view.KeyEvent;
import java.lang.Math;

import com.mycompany.myapp.alien.Entity;


public class Api extends Entity {
	public Api() {
		super();
		radius = kRadius;
		sprite_source =
			new Rect(0, kSpriteBase, kSpriteWidth, kSpriteBase + kSpriteHeight);
	}

	public void Step(float time_step) {
		super.Step(time_step);

		// Update the sprite to reflect the age / life of the fire entity.
		frame += time_step * kFrameRate;
		int rounded_frame = (int)frame;
		if (rounded_frame <= kFrames) {
			sprite_source.top = kSpriteBase + kSpriteHeight * rounded_frame;
			sprite_source.bottom = kSpriteBase + kSpriteHeight * (rounded_frame + 1);
		} else {
			alive = false;  // Signal for deletion.
		}
	}

	public void CollideEntity(Entity entity) {
		if (Math.abs(entity.x - x) < radius + entity.radius &&
			Math.abs(entity.y - y) < radius + entity.radius) {
			entity.alive = false;  // Anything which collides with fire dies.
		}
	}

	private float frame = 0.0f;

	private static final int kFrames = 14;
	private static final float kFrameRate = 10.0f;  // Frames / sec.
	private static final float kRadius = 3.0f;
	private static final int kSpriteBase = 521;
	private static final int kSpriteWidth = 64;
	private static final int kSpriteHeight = 36;
}
