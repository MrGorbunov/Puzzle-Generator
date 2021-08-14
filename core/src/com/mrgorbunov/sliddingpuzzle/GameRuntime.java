package com.mrgorbunov.sliddingpuzzle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrgorbunov.sliddingpuzzle.GameLogic.LevelParser;
import com.mrgorbunov.sliddingpuzzle.Screen.ScreenMainMenu;

public class GameRuntime extends Game {

	@Override
	public void create() {
		RuntimeGlobals.skin = new Skin(Gdx.files.internal("skin/metal-ui.json"));
		RuntimeGlobals.game = this;

		screen = new ScreenMainMenu();

		// Testing out file handling
		FileHandle directoryLevels = Gdx.files.internal("levels/");
		FileHandle[] levelFiles = directoryLevels.list();

		for (FileHandle fh : levelFiles) {
			if (LevelParser.parseFile(fh) != null)
				Gdx.app.log("testing", fh.path());
		}
	}

	@Override
	public void render () {
		super.render();
		ScreenUtils.clear(0.4f, 0.8f, 0.3f, 1f);
		screen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose () {
		screen.dispose();
	}
	
}
