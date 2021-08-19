package com.mrgorbunov.sliddingpuzzle.GameLogic;

import com.badlogic.gdx.Gdx;
import com.mrgorbunov.sliddingpuzzle.Util.DSStack;

// TODO: Maybe seperate out move logic from undo & redo logic?

public class PuzzleLevel {

	final int WIDTH;
	final int HEIGHT;
	Tile[][] layout; // [0][0] = bottom left

	PuzzleState startState;
	DSStack<PuzzleState> moveStack;
	DSStack<PuzzleState> redoStack; // used to handle redoing

	public PuzzleLevel (Tile[][] layout, int playerX, int playerY) {
		this.layout = layout;
		WIDTH = layout.length;
		HEIGHT = layout[0].length;

		// Initialize stack of states
		startState = new PuzzleState(playerX, playerY, false);
		moveStack = new DSStack<>();
		moveStack.add(startState.clone());

		redoStack = new DSStack<>();
	}

	/**
	 * Returns a copy of this level state, with new objects for everything, allowing
	 * safe mutations w/o affecting this object.
	 */
	@Override
	public PuzzleLevel clone () {
		PuzzleState curState = moveStack.peekTop();
		return new PuzzleLevel(layout.clone(), curState.playerX, curState.playerY);
	}

	public int getWidth () { return WIDTH; }
	public int getHeight () { return HEIGHT; }
	public int getPlayerX () { return moveStack.peekTop().playerX; }
	public int getPlayerY () { return moveStack.peekTop().playerY; }
	public Tile[][] getLayout () { return layout; }
	public boolean levelIsBeat () { return moveStack.peekTop().isSolved; }
	public PuzzleState getCurrentState () { return moveStack.peekTop(); }

	public void setCurrentState (PuzzleState state) { 
		setCurrentState(state, true);
	}

	public void setCurrentState (PuzzleState state, boolean resetMoveStacks) {
		if (resetMoveStacks) {
			moveStack = new DSStack<>();
			redoStack = new DSStack<>();
		}

		moveStack.add(state);
	}

	public void resetLevel () {
		moveStack = new DSStack<>();
		redoStack = new DSStack<>();

		moveStack.add(startState.clone());
	}

	public boolean isValidMove (Direction dir) {
		int[] delta = dir.getDelta();

		PuzzleState curState = moveStack.peekTop();
		int testX = curState.playerX + delta[0];
		int testY = curState.playerY + delta[1];

		return (testX >= 0 && testX < WIDTH && testY >= 0 && testY < HEIGHT) &&
			layout[testX][testY] != Tile.WALL;
	}

	public PuzzleState simulateMove (Direction dir) {
		PuzzleState curState = moveStack.peekTop();

		if (curState.isSolved || !isValidMove(dir))
			return null;

		int[] delta = dir.getDelta();

		int testX = curState.playerX;
		int testY = curState.playerY;
		boolean solvedState = curState.isSolved;

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

		return new PuzzleState(testX, testY, solvedState);
	}

	/**
	 * Moves the player along the specified direction until either
	 * hitting a wall or the exit. Does nothing if the level is beat
	 * or inside a wall.
	 */
	public void makeMove (Direction dir) {
		PuzzleState curState = moveStack.peekTop();

		// Don't allow moving out of the goal
		if (curState.isSolved)
			return;

		int[] delta = dir.getDelta();

		int testX = curState.playerX;
		int testY = curState.playerY;
		boolean solvedState = curState.isSolved;

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

		moveStack.add(new PuzzleState(testX, testY, solvedState));

		// Reset redo stack after making a move
		if (redoStack.size() != 0)
			redoStack = new DSStack<>();
	}

	public void undoMove () {
		if (moveStack.size() == 1)
			return;
		
		PuzzleState prevMove = moveStack.pop();
		redoStack.add(prevMove);
	}

	public void redoMove () {
		if (redoStack.size() == 0)
			return;

		PuzzleState moveToRedo = redoStack.pop();
		moveStack.add(moveToRedo);
	}

	public boolean canUndo () { return moveStack.size() > 1; }
	public boolean canRedo () { return redoStack.size() > 0; }

	public void printLevel () {
		PuzzleState curState = moveStack.peekTop();

		StringBuilder levelLine;
		Gdx.app.log("LevelState", "=== Printing Level ===");

		for (int y=HEIGHT-1; y>=0; y--) {
			levelLine = new StringBuilder(WIDTH);

			for (int x=0; x<WIDTH; x++) {
				if (x == curState.playerX && y == curState.playerY) {
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
