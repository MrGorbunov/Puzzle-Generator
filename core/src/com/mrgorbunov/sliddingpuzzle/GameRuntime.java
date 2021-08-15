package com.mrgorbunov.sliddingpuzzle;

import java.util.Arrays;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrgorbunov.sliddingpuzzle.LevelLoading.LevelInfo;
import com.mrgorbunov.sliddingpuzzle.Screen.ScreenMainMenu;
import com.mrgorbunov.sliddingpuzzle.Util.InputCache;
import com.mrgorbunov.sliddingpuzzle.Util.LevelParser;

public class GameRuntime extends Game {

	@Override
	public void create() {
		RuntimeGlobals.skin = new Skin(Gdx.files.internal("skin/metal-ui.json"));
		RuntimeGlobals.game = this;
		RuntimeGlobals.input = new InputCache();

		screen = new ScreenMainMenu();

		// Load level list
		FileHandle directoryLevels = Gdx.files.internal("levels/");
		FileHandle[] levelFiles = directoryLevels.list();
		Array<LevelInfo> validLevels = new Array<>(levelFiles.length);

		for (FileHandle fh : levelFiles) {
			if (LevelParser.parseFile(fh) != null)
				validLevels.add(new LevelInfo(fh));
		}

		LevelInfo[] levelList = new LevelInfo[validLevels.size];
		for (int i=0; i<levelList.length; i++) {
			levelList[i] = validLevels.get(i);
		}
		Arrays.sort(levelList);
		RuntimeGlobals.levels = levelList;
	}

	@Override
	public void render () {
		super.render();
		ScreenUtils.clear(0.4f, 0.8f, 0.3f, 1f);
		screen.render(Gdx.graphics.getDeltaTime());

		RuntimeGlobals.input.update();
	}

	@Override
	public void dispose () {
		screen.dispose();
	}
	
}
