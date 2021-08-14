package com.mrgorbunov.sliddingpuzzle.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mrgorbunov.sliddingpuzzle.RuntimeGlobals;
import com.mrgorbunov.sliddingpuzzle.LevelLoading.LevelInfo;

public class ScreenLevelSelect implements Screen {

	private Stage guiStage;

	private Skin skin = RuntimeGlobals.skin;

	public ScreenLevelSelect () {
		guiStage = new Stage();
		Gdx.input.setInputProcessor(guiStage);

		Table rootTable = new Table(RuntimeGlobals.skin);
		rootTable.setFillParent(true);
		guiStage.addActor(rootTable);

		TextButtonStyle txtButtonStyle = skin.get("default", TextButtonStyle.class);

		TextButton butBack = new TextButton("Back", txtButtonStyle);
		butBack.addListener(new ChangeListener(){
			public void changed (ChangeEvent event, Actor actor) {
				RuntimeGlobals.game.setScreen(new ScreenMainMenu());
			}
		});

		// Generate array of buttons for levels
		Table levelTable = new Table(RuntimeGlobals.skin);

		final int ROW_SIZE = Math.min((int) Math.sqrt((double) RuntimeGlobals.levels.length) + 1, 5);
		int numInRow = 0;
		
		for (LevelInfo level : RuntimeGlobals.levels) {
			String name = level.levelName;
			TextButton levelButton = new TextButton(name, skin);

			LevelSwitcher levelSwitcher = new LevelSwitcher(level);
			levelButton.addListener(levelSwitcher);

			levelTable.add(levelButton);
			numInRow++;
			if (numInRow >= ROW_SIZE)
				levelTable.row();
		}

		rootTable.add(levelTable);
		rootTable.row();
		rootTable.add(butBack);


	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		guiStage.act();
		guiStage.draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}
	
}



/*
	Because the GUI system is callback based, this class is needed
	to programatically generate the level select screen.
*/
class LevelSwitcher extends ChangeListener {
	private LevelInfo level;

	public LevelSwitcher (LevelInfo level) {
		this.level = level;
	}

	public void changed (ChangeEvent event, Actor actor) {
		RuntimeGlobals.activeLevel = level;
		RuntimeGlobals.game.setScreen(new ScreenPuzzle());
	}
}
