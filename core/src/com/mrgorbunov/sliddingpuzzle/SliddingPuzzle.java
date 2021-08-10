package com.mrgorbunov.sliddingpuzzle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mrgorbunov.sliddingpuzzle.GameLogic.Direction;
import com.mrgorbunov.sliddingpuzzle.GameLogic.LevelParser;
import com.mrgorbunov.sliddingpuzzle.GameLogic.LevelState;
import com.mrgorbunov.sliddingpuzzle.GameLogic.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class SliddingPuzzle extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera camera;

	Texture texWall;
	Texture texFloor;
	Texture texFinish;
	Texture texPlayer;
	TextureRegion background;
	int tileSizePx;

	LevelState level;

	
	@Override
	public void create () {
		batch = new SpriteBatch();

		// Tile[][] gameLevel = new Tile[][] {
		// 	new Tile[] {Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR},
		// 	new Tile[] {Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR},
		// 	new Tile[] {Tile.FLOOR,  Tile.WALL,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR},
		// 	new Tile[] {Tile.FLOOR,  Tile.FLOOR,  Tile.WALL,  Tile.FLOOR,  Tile.FLOOR},
		// 	new Tile[] {Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR},
		// 	new Tile[] {Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FINISH,  Tile.FLOOR},
		// 	new Tile[] {Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR},
		// 	new Tile[] {Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR},
		// 	new Tile[] {Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR,  Tile.FLOOR}
		// };
		// gameLevel = Tile.flipLayout(gameLevel);

		level = LevelParser.parseFile("levels/lvl0.txt");
		level.printLevel();

		texWall = new Texture(Gdx.files.internal("img/wall.png"));
		texFloor = new Texture(Gdx.files.internal("img/floor.png"));
		texFinish = new Texture(Gdx.files.internal("img/finish_flag.png"));
		texPlayer = new Texture(Gdx.files.internal("img/player.png"));

		texWall.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		background = new TextureRegion(texWall, -20f, -20f, 20 + level.getWidth(), 20 + level.getHeight());

		// All textures are squares, so height = width
		tileSizePx = texWall.getHeight();

		if (level.getWidth() < level.getHeight())
			camera = new OrthographicCamera(level.getWidth(), level.getWidth() * 720 / 1080);
		else
			camera = new OrthographicCamera(level.getHeight() * 1080 / 720, level.getHeight());

		camera.zoom = tileSizePx * 1.1f;

		// Move camera to be centered on level
		camera.position.set(level.getWidth() * tileSizePx / 2, level.getHeight() * tileSizePx / 2, 0);
	}

	@Override
	public void render () {
		handleInput();

		ScreenUtils.clear(1, 1, 1, 1);
		camera.update();
		// System.out.println(camera.position.toString());
		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		batch.draw(background, -20 * tileSizePx, -20 * tileSizePx);

		// Draw the game map
		Texture tileTex;

		for (int x=0; x<level.getWidth(); x++) {
			for (int y=0; y<level.getHeight(); y++) {
				switch (level.getLayout()[x][y]) {
					case FINISH:
						tileTex = texFinish;
						break;
					case FLOOR:
						tileTex = texFloor;
						break;
					case WALL:
						tileTex = texWall;
						break;
					default:
						tileTex = texFloor;
						break;
				}

				batch.draw(tileTex, x * tileSizePx, y * tileSizePx);
			}
		}

		// Draw the player
		int playerPxX = level.getPlayerX() * tileSizePx;
		int playerPxY = level.getPlayerY() * tileSizePx;
		batch.draw(texPlayer, playerPxX, playerPxY);


		batch.end();
		
	}

	void handleInput () {
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			camera.zoom *= 1.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.E)) {
			camera.zoom /= 1.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.translate(-0.05f * camera.zoom, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camera.translate(0.05f * camera.zoom, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			camera.translate(0, -0.05f * camera.zoom, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			camera.translate(0, 0.05f * camera.zoom, 0);
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			level.makeMove(Direction.LEFT);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			level.makeMove(Direction.RIGHT);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			level.makeMove(Direction.UP);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
			level.makeMove(Direction.DOWN);
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
			level.resetLevel();
		}



		// if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
		// 	camera.rotate(-0.5f);
		// }
		// if (Gdx.input.isKeyPressed(Input.Keys.E)) {
		// 	camera.rotate(0.5f);
		// }

	}
	
	@Override
	public void dispose () {
	}
}
