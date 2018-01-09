package com.mycompany.myapp.alien;

import android.app.Activity;
import com.mycompany.myapp.alien.GameState;
import com.mycompany.myapp.alien.GameView;
import android.os.Bundle;
import android.widget.TextView;
import com.mycompany.myapp.R;

public class AlienActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alien_activity);

		game_view_ = (GameView)findViewById(R.id.GAME_VIEW);
		game_view_.SetTitleView((TextView)findViewById(R.id.TEXT_VIEW));
		game_view_.SetGame(game_state_);
	}

	private GameState game_state_ = new GameState();
	private GameView game_view_;
}
