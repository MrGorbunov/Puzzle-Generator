package com.mrgorbunov.sliddingpuzzle.Screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mrgorbunov.sliddingpuzzle.RuntimeGlobals;
import com.mrgorbunov.sliddingpuzzle.GameAnnotations.LevelSearch;
import com.mrgorbunov.sliddingpuzzle.GameLogic.Direction;
import com.mrgorbunov.sliddingpuzzle.GameLogic.LevelState;
import com.mrgorbunov.sliddingpuzzle.GameLogic.Tile;
import com.mrgorbunov.sliddingpuzzle.LevelLoading.LevelInfo;
import com.mrgorbunov.sliddingpuzzle.LevelLoading.LevelParser;
import com.mrgorbunov.sliddingpuzzle.Util.GraphAlgs;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;

public class ScreenPuzzle implements Screen {

	private long frame;

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



	// GUI
	private Stage stage;
	private Table table;

	private Skin skin = RuntimeGlobals.skin;


	public ScreenPuzzle () {
		Gdx.app.log("PuzzleScreen", "Constructing new puzzle screen");

		frame = 0;

		// Load level
		LevelInfo activeLevel = RuntimeGlobals.activeLevel;
		level = LevelParser.parseFile(activeLevel.file);

		if (level == null) {
			Gdx.app.error("ScreenSwitch", "Switched to puzzle screen without active level in runtimeGlobals");
			Gdx.app.exit();
			System.exit(0);
		}

		GraphAlgs.debugGraph(LevelSearch.getMoveGraph(level));

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
		if (level.getWidth() <= 5 && level.getHeight() <= 5)
			camera.zoom = tileSizePx * 2f;
		camera.position.set(level.getWidth() * tileSizePx / 2, level.getHeight() * tileSizePx / 2, 0);


		// Animation Setting
		animationPlaying = false;
		MAX_SPEED = 100 * tileSizePx;


		
		//
		// GUI
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

		// table.setDebug(true);


		// Button
		// TODO: Create some better (in-program) mapping of hotkeys to actions?
		TextButtonStyle buttonStyle = skin.get("default", TextButtonStyle.class);
		TextButton butBack = new TextButton("Back", buttonStyle);
		butBack.addListener(new ChangeListener () {
			public void changed (ChangeEvent event, Actor actor) {
				RuntimeGlobals.game.setScreen(new ScreenLevelSelect());
			}
		});

		TextButton butReset = new TextButton("(R)eset", buttonStyle);
		butReset.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				level.resetLevel();
			}
		});

		TextButton butUndo = new TextButton("(U)ndo", buttonStyle);
		butUndo.addListener(new ChangeListener () {
			public void changed (ChangeEvent event, Actor actor) {
				level.undoMove();
			}
		});

		TextButton butRedo = new TextButton("(Y) Redo", buttonStyle);
		butRedo.addListener(new ChangeListener () {
			public void changed (ChangeEvent event, Actor actor) {
				level.redoMove();
			}
		});



		// TextButton button = new TextButton("Oompa Loompa", skin, "default");
		Table backSubTable = new Table(skin);
		backSubTable.padBottom(10f);
		backSubTable.add(butBack);

		table.add(backSubTable);
		table.row();
		table.add(butReset);
		table.row();
		table.add(butUndo);
		table.row();
		table.add(butRedo);
		table.row();

		table.left();
		table.top();
		table.pad(50);
	}


	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		//
		// Updating Call
		//
		frame++;

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


		//
		// GUI Call
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}



	void handleInput () {
		if (RuntimeGlobals.input.isKeyPressed(Input.Keys.Q)) {
			camera.zoom *= 1.02;
		}
		if (RuntimeGlobals.input.isKeyPressed(Input.Keys.E)) {
			camera.zoom /= 1.02;
		}
		if (RuntimeGlobals.input.isKeyPressed(Input.Keys.A)) {
			camera.translate(-0.05f * camera.zoom, 0, 0);
		}
		if (RuntimeGlobals.input.isKeyPressed(Input.Keys.D)) {
			camera.translate(0.05f * camera.zoom, 0, 0);
		}
		if (RuntimeGlobals.input.isKeyPressed(Input.Keys.S)) {
			camera.translate(0, -0.05f * camera.zoom, 0);
		}
		if (RuntimeGlobals.input.isKeyPressed(Input.Keys.W)) {
			camera.translate(0, 0.05f * camera.zoom, 0);
		}

		if (!animationPlaying) {
			if (level.isValidMove(Direction.LEFT) &&
				RuntimeGlobals.input.isKeyPressed(Input.Keys.LEFT)) {
					makeMoveAndStartAnimation(Direction.LEFT);

			} else if (level.isValidMove(Direction.RIGHT) &&
				RuntimeGlobals.input.isKeyPressed(Input.Keys.RIGHT)) {
					makeMoveAndStartAnimation(Direction.RIGHT);

			} else if (level.isValidMove(Direction.UP) &&
				RuntimeGlobals.input.isKeyPressed(Input.Keys.UP)) {
					makeMoveAndStartAnimation(Direction.UP);

			} else if (level.isValidMove(Direction.DOWN) &&
				RuntimeGlobals.input.isKeyPressed(Input.Keys.DOWN)) {
					makeMoveAndStartAnimation(Direction.DOWN);

			} else if (RuntimeGlobals.input.isKeyJustPressed(Input.Keys.U)) {
				level.undoMove();

			} else if (RuntimeGlobals.input.isKeyJustPressed(Input.Keys.Y)) {
				level.redoMove();

			}

			if (RuntimeGlobals.input.isKeyJustPressed(Input.Keys.R)) {
				level.resetLevel();
			}
		}


		// if (RuntimeGlobals.input.isKeyPressed(Input.Keys.Q)) {
		// 	camera.rotate(-0.5f);
		// }
		// if (RuntimeGlobals.input.isKeyPressed(Input.Keys.E)) {
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


	

	@Override
	public void dispose() {
	}

	@Override
	public void pause() {
		System.out.println("Just paused");
	}




	//
	// Unused methods 
	// (because only target is desktop & window is non-resizable)

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	
}
