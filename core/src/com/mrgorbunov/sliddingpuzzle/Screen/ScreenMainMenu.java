package com.mrgorbunov.sliddingpuzzle.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mrgorbunov.sliddingpuzzle.RuntimeGlobals;

public class ScreenMainMenu implements Screen {

	private Stage guiStage;
	private Table rootTable;

	private Skin skin = RuntimeGlobals.skin;

	public ScreenMainMenu () {
		guiStage = new Stage();
		Gdx.input.setInputProcessor(guiStage);

		rootTable = new Table(RuntimeGlobals.skin);
		rootTable.setFillParent(true);
		guiStage.addActor(rootTable);

		TextButtonStyle txtButtonStyle = skin.get("default", TextButtonStyle.class);

		TextButton butLevels = new TextButton("Levels", txtButtonStyle);
		butLevels.addListener(new ChangeListener () {
			public void changed (ChangeEvent event, Actor actor) {
				RuntimeGlobals.game.setScreen(new ScreenLevelSelect());
			}
		});

		TextButton butGenerator = new TextButton("Generator", txtButtonStyle);
		butGenerator.addListener(new ChangeListener () {
			public void changed (ChangeEvent event, Actor actor) {
				RuntimeGlobals.game.setScreen(new ScreenGenerator());
			}
		});

		TextButton butExit = new TextButton("Exit", txtButtonStyle);
		butExit.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				Gdx.app.exit();
				System.exit(0);
			}
		});

		// Add buttons onto stage to be centered and one after another
		rootTable.add(butLevels);
		rootTable.row();
		rootTable.add(butGenerator);
		rootTable.row();
		rootTable.add(butExit);
		rootTable.row();
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
