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

		// Generate array of levels
		Table levelTable = new Table(RuntimeGlobals.skin);
		String[] levelPaths = new String[] {
			"levels/lvl0.txt", "levels/lvl1.txt"
		};

		for (String levelPath : levelPaths) {
			String[] levelDirpath = levelPath.split("\\.")[0] // remove exentsion
				.split("/"); // remove file path
			String name = levelDirpath[levelDirpath.length - 1];

			TextButton levelButton = new TextButton(name, skin);

			ChangeListener gotoLevel = new ChangeListener () {
				private FileHandle levelFileHandle;

				public ChangeListener setFileHandle (FileHandle levelFileHandle) {
					this.levelFileHandle = levelFileHandle;
					return this;
				}

				public void changed (ChangeEvent event, Actor actor) {
					RuntimeGlobals.game.setScreen(
						new ScreenPuzzle(levelFileHandle)
					);
				}
			}.setFileHandle(Gdx.files.internal(levelPath));;

			levelButton.addListener(gotoLevel);

			levelTable.add(levelButton);
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
