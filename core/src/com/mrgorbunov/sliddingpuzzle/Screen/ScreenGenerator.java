package com.mrgorbunov.sliddingpuzzle.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mrgorbunov.sliddingpuzzle.RuntimeGlobals;

public class ScreenGenerator implements Screen {

	private Stage guiStage;

	private Skin skin = RuntimeGlobals.skin;

	public ScreenGenerator () {
		guiStage = new Stage();
		Gdx.input.setInputProcessor(guiStage);

		Table rootTable = new Table(RuntimeGlobals.skin);
		rootTable.setFillParent(true);
		guiStage.addActor(rootTable);

		TextButtonStyle txtButtonStyle = skin.get("default", TextButtonStyle.class);

		TextButton butBack = new TextButton("Back", txtButtonStyle);
		butBack.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				RuntimeGlobals.game.setScreen(new ScreenMainMenu());
			}
		});

		Label text = new Label("Generator not implemented yet", skin);

		// Add to table
		rootTable.add(text);
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
