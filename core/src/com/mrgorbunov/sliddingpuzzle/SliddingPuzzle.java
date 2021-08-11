package com.mrgorbunov.sliddingpuzzle;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
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

	boolean animationPlaying;

	// TODO: Seperate this logic out of the main method
	// Animation logic
	long animStart;
	float startX;
	float startY;
	float endX;
	float endY;
	float normDirX;
	float normDirY;

	Texture texAnimation;
	float curX;
	float curY;

	// These are used to define the velocity curve (which is a method)
	final float EPSILON_ANIM_EQUALS = 0.0001f;
	float MAX_SPEED; // px / second
	final long ACCEL_TIME_MILLIS = 250;

	
	@Override
	public void create () {
		level = LevelParser.parseFile("levels/lvl0.txt");
		level.printLevel();


		// Texture loading
		batch = new SpriteBatch();

		texWall = new Texture(Gdx.files.internal("img/wall.png"));
		texFloor = new Texture(Gdx.files.internal("img/floor.png"));
		texFinish = new Texture(Gdx.files.internal("img/finish_flag.png"));
		texPlayer = new Texture(Gdx.files.internal("img/player.png"));

		texWall.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		background = new TextureRegion(texWall, -20f, -20f, 20 + level.getWidth(), 20 + level.getHeight());


		// Setup camera to show entire level, and be centered
		if (level.getWidth() < level.getHeight())
			camera = new OrthographicCamera(level.getWidth(), level.getWidth() * 720 / 1080);
		else
			camera = new OrthographicCamera(level.getHeight() * 1080 / 720, level.getHeight());

		tileSizePx = texWall.getHeight();
		camera.zoom = tileSizePx * 1.1f;
		camera.position.set(level.getWidth() * tileSizePx / 2, level.getHeight() * tileSizePx / 2, 0);


		// Animation Setting
		animationPlaying = false;
		MAX_SPEED = 100 * tileSizePx;
	}

	@Override
	public void render () {
		//
		// Updating Call
		//

		handleInput();



		//
		// Draw Call
		//

		ScreenUtils.clear(1, 1, 1, 1);
		camera.update();
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
		if (animationPlaying) {
			animationTick();

			batch.draw(texAnimation, curX, curY);

			if (animationOver())
				animationPlaying = false;
		} else {
			int playerPxX = level.getPlayerX() * tileSizePx;
			int playerPxY = level.getPlayerY() * tileSizePx;
			batch.draw(texPlayer, playerPxX, playerPxY);
		}


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

		if (!animationPlaying) {
			if (level.canMoveInDir(Direction.LEFT) &&
				Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
					makeMoveAndStartAnimation(Direction.LEFT);

			} else if (level.canMoveInDir(Direction.RIGHT) &&
				Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
					makeMoveAndStartAnimation(Direction.RIGHT);

			} else if (level.canMoveInDir(Direction.UP) &&
				Gdx.input.isKeyPressed(Input.Keys.UP)) {
					makeMoveAndStartAnimation(Direction.UP);

			} else if (level.canMoveInDir(Direction.DOWN) &&
				Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
					makeMoveAndStartAnimation(Direction.DOWN);
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
				level.resetLevel();
			}
		}


		// if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
		// 	camera.rotate(-0.5f);
		// }
		// if (Gdx.input.isKeyPressed(Input.Keys.E)) {
		// 	camera.rotate(0.5f);
		// }

	}

	void makeMoveAndStartAnimation (Direction dir) {
		float pX = level.getPlayerX() * tileSizePx;
		float pY = level.getPlayerY() * tileSizePx;

		level.makeMove(dir);

		float newPx = level.getPlayerX() * tileSizePx;
		float newPy = level.getPlayerY() * tileSizePx;

		animationPlaying = true;
		startAnimation(pX, pY, newPx, newPy);
	}
	
	@Override
	public void dispose () {
	}






	//
	// Animation Logic (very poor that it's here rn)
	//

	void startAnimation (float startX, float startY, float endX, float endY) {
		animStart = TimeUtils.millis();

		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;

		curX = startX;
		curY = startY;
		texAnimation = texPlayer;

		Vector2 offset = new Vector2(endX - startX, endY - startY);
		offset.nor();
		normDirX = offset.x;
		normDirY = offset.y;
	}

	void animationTick () {
		float speed = speed();
		speed *= Gdx.graphics.getDeltaTime();
		
		curX += normDirX * speed;
		curY += normDirY * speed;

		// Checking for overshooting
		if (animationOver()) {
			curX = endX;
			curY = endY;
		}
	}

	boolean animationOver () {
		// To detect overshooting, a dot product is taken between 
		// curPos -> end & start -> end
		// If the two are pointed the same way (i.e the animation is still progressing)
		// the dot product is positive, and if it's overshot then it's negative.
		Vector2 posToEnd = new Vector2(curX - endX, curY - endY);
		Vector2 startToEnd = new Vector2(startX - endX, startY - endY);
		return posToEnd.dot(startToEnd) <= EPSILON_ANIM_EQUALS;
	}

	/**
	 * Speed curve looks like this:
	 * 
	 * 		/-------
	 *	   / 
	 *	  / 
	 *   /
	 * 
	 * Plateous at MAX_VEL, after ACCEL_TIME_MILLIS milliseconds.
	 * 
	 * Note: The speed should be multiplied by time delta, this function
	 * 		does NOT do that
	 */
	private float speed () {
		long timeElapsed = TimeUtils.millis() - animStart;

		if (timeElapsed >= ACCEL_TIME_MILLIS)
			return MAX_SPEED;

		return timeElapsed * MAX_SPEED / (float) ACCEL_TIME_MILLIS;
	}


}
