package com.mrgorbunov.sliddingpuzzle.GameLogic;

public enum Tile {
	FLOOR, WALL, FINISH;

	/**
	 * When defining a 2D array within Java, the indexing is [y][x],
	 * while the layout array needs to be indexed [x][y], so this function
	 * flips the input over the center diagnol, and then also flips vertically
	 * so that old index 0 becomes the last index, since drawing happens bottom to
	 * top but indexing by default is top to bottom.
	 * 
	 * new Tile[][] {
	 *	new Tile[] {FLOOR, FLOOR};   < index 0, but shuold be 1 so it's drawn last
	 *	new Tile[] {WALL, FINISH};
	 * }
	 */
	public static Tile[][] flipLayout (Tile[][] layout) {
		Tile[][] newLayout = new Tile[layout[0].length][layout.length];

		for (int i=0; i<layout.length; i++) {
			for (int j=0; j<layout[0].length; j++) {
				newLayout[j][layout.length - 1 - i] = layout[i][j];
			}
		}

		return newLayout;
	}

}
