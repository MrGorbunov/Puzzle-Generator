package com.mrgorbunov.sliddingpuzzle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class GameMain extends Game {

	@Override
	public void create() {
		screen = new PuzzleScreen();
	}

	@Override
	public void render () {
		super.render();
		screen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose () {
		screen.dispose();
	}
	
}
