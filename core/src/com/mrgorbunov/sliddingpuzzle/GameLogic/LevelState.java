package com.mrgorbunov.sliddingpuzzle.GameLogic;

public class LevelState {

	final int WIDTH;
	final int HEIGHT;
	Tile[][] layout; // [0][0] = bottom left
	boolean levelBeat;

	int startPlayerX;
	int startPlayerY;

	int playerX;
	int playerY;

	public LevelState (Tile[][] layout, int playerX, int playerY) {
		this.layout = layout;
		WIDTH = layout.length;
		HEIGHT = layout[0].length;
		levelBeat = false;

		this.playerX = playerX;
		this.playerY = playerY;

		startPlayerX = playerX;
		startPlayerY = playerY;
	}

	public int getWidth () { return WIDTH; }
	public int getHeight () { return HEIGHT; }
	public int getPlayerX () { return playerX; }
	public int getPlayerY () { return playerY; }
	public Tile[][] getLayout () { return layout; }

	public boolean levelIsBeat () { return levelBeat; }

	public void resetLevel () {
		playerX = startPlayerX;
		playerY = startPlayerY;

		levelBeat = false;
	}

	public boolean canMoveInDir (Direction dir) {
		int[] delta = dir.getDelta();

		int testX = playerX + delta[0];
		int testY = playerY + delta[1];

		return (testX >= 0 && testX < WIDTH && testY >= 0 && testY < HEIGHT) &&
			layout[testX][testY] != Tile.WALL;
	}

	/**
	 * Moves the player along the specified direction until either
	 * hitting a wall or the exit. Does nothing if the level is beat
	 * or inside a wall.
	 */
	public void makeMove (Direction dir) {
		if (levelBeat)
			return;

		int[] delta = dir.getDelta();

		int testX = playerX;
		int testY = playerY;

		while (true) {
			if (testX < 0 || testX >= WIDTH || testY < 0 || testY >= HEIGHT || // Bounds Check
				layout[testX][testY] == Tile.WALL) // Walls check
			{
				// Go back one step, then exit
				testX -= delta[0];
				testY -= delta[1];
				break;
			}

			if (layout[testX][testY] == Tile.FINISH) {
				// No need to undo a move when on the finish flag.
				levelBeat = true;
				break;
			}

			// This is done at the end, so that in a glitched or final position
			// the player doesn't move. Helping avoid exploits.
			testX += delta[0];
			testY += delta[1];
		}

		playerX = testX;
		playerY = testY;
	}

	public void printLevel () {
		for (int y=HEIGHT-1; y>=0; y--) {
			for (int x=0; x<WIDTH; x++) {
				if (x == playerX && y == playerY) {
					System.out.print("&");
					continue;
				}

				switch (layout[x][y]) {
					case FINISH:
						System.out.print("$");
						break;
					case FLOOR:
						System.out.print("*");
						break;
					case WALL:
						System.out.print("#");
						break;
				}
			}

			System.out.println();
		}
	}
	
}
