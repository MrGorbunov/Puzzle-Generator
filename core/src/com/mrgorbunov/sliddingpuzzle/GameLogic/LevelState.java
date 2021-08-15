package com.mrgorbunov.sliddingpuzzle.GameLogic;

import com.badlogic.gdx.Gdx;
import com.mrgorbunov.sliddingpuzzle.Util.DSStack;

public class LevelState {

	final int WIDTH;
	final int HEIGHT;
	Tile[][] layout; // [0][0] = bottom left

	PuzzleDynamicsState startState;
	PuzzleDynamicsState currentState; // TODO: Not use this variable
	DSStack<PuzzleDynamicsState> moveStack;
	DSStack<PuzzleDynamicsState> redoStack; // used to handle redoing

	public LevelState (Tile[][] layout, int playerX, int playerY) {
		this.layout = layout;
		WIDTH = layout.length;
		HEIGHT = layout[0].length;

		// Initialize stack of states
		startState = new PuzzleDynamicsState(playerX, playerY, false);
		moveStack = new DSStack<>();
		moveStack.add(startState.clone());

		redoStack = new DSStack<>();

		currentState = moveStack.peekTop();
	}

	public int getWidth () { return WIDTH; }
	public int getHeight () { return HEIGHT; }
	public int getPlayerX () { return currentState.playerX; }
	public int getPlayerY () { return currentState.playerY; }
	public Tile[][] getLayout () { return layout; }

	public boolean levelIsBeat () { return currentState.isSolved; }

	public void resetLevel () {
		moveStack = new DSStack<>();
		redoStack = new DSStack<>();

		moveStack.add(startState.clone());
		currentState = moveStack.peekTop();
	}

	public boolean isValidMove (Direction dir) {
		int[] delta = dir.getDelta();

		int testX = currentState.playerX + delta[0];
		int testY = currentState.playerY + delta[1];

		return (testX >= 0 && testX < WIDTH && testY >= 0 && testY < HEIGHT) &&
			layout[testX][testY] != Tile.WALL;
	}

	// TODO: have makeMove return the new game state, and check for valid move
	/**
	 * Moves the player along the specified direction until either
	 * hitting a wall or the exit. Does nothing if the level is beat
	 * or inside a wall.
	 */
	public void makeMove (Direction dir) {
		// Don't allow moving out of the goal
		if (currentState.isSolved)
			return;

		int[] delta = dir.getDelta();

		int testX = currentState.playerX;
		int testY = currentState.playerY;
		boolean solvedState = currentState.isSolved;

		// if state is solved, the move shouldn't be evaluating
		assert(solvedState == false);

		while (true) {
			if (testX < 0 || testX >= WIDTH || testY < 0 || testY >= HEIGHT || // Bounds Check
				layout[testX][testY] == Tile.WALL) // Walls check
			{
				// Go back one step to exit the wall, then exit
				testX -= delta[0];
				testY -= delta[1];
				break;
			}

			if (layout[testX][testY] == Tile.FINISH) {
				// No need to undo a move when on the finish flag.
				solvedState = true;
				break;
			}

			// This is done at the end, so that in a glitched or final position
			// the player doesn't move. Helping avoid exploits.
			testX += delta[0];
			testY += delta[1];
		}

		moveStack.add(new PuzzleDynamicsState(testX, testY, solvedState));
		currentState = moveStack.peekTop();

		// Reset redo stack after makign a move
		if (redoStack.size() != 0)
			redoStack = new DSStack<>();
	}

	public void undoMove () {
		if (moveStack.size() == 1)
			return;
		
		PuzzleDynamicsState prevMove = moveStack.pop();
		redoStack.add(prevMove);

		currentState = moveStack.peekTop();
	}

	public void redoMove () {
		if (redoStack.size() == 0)
			return;

		PuzzleDynamicsState moveToRedo = redoStack.pop();
		moveStack.add(moveToRedo);

		currentState = moveStack.peekTop();
	}

	public boolean canUndo () { return moveStack.size() > 1; }
	public boolean canRedo () { return redoStack.size() > 0; }

	public void printLevel () {
		StringBuilder levelLine;
		Gdx.app.log("LevelState", "=== Printing Level ===");

		for (int y=HEIGHT-1; y>=0; y--) {
			levelLine = new StringBuilder(WIDTH);

			for (int x=0; x<WIDTH; x++) {
				if (x == currentState.playerX && y == currentState.playerY) {
					levelLine.append("&");
					continue;
				}

				switch (layout[x][y]) {
					case FINISH:
						levelLine.append("$");
						break;
					case FLOOR:
						levelLine.append("*");
						break;
					case WALL:
						levelLine.append("#");
						break;
				}
			}

			Gdx.app.log("LevelState", levelLine.toString());
		}
	}
	
}
