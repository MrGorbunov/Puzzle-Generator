package com.mrgorbunov.sliddingpuzzle.GameLogic;

public class PuzzleState {

	// NOTE: When adding new fields, update .equals() & .toString()
	public final int playerX;
	public final int playerY;

	public final boolean isSolved;
	
	/**
	 * Creates a new puzzle state, which stores dynamic data.
	 * Basically, anything that can change by making moves.
	 * 
	 * Right now, that's player position & whether the level is beat
	 */
	public PuzzleState (int playerX, int playerY, boolean isSolved) {
		this.playerX = playerX;
		this.playerY = playerY;
		this.isSolved = isSolved;
	}

	@Override
	public PuzzleState clone () {
		return new PuzzleState(playerX, playerY, isSolved);
	}

	@Override
	public boolean equals (Object o) {
		if (o == this)
			return true;
		
		if (!(o instanceof PuzzleState))
			return false;

		PuzzleState otherState = (PuzzleState) o;

		return otherState.playerX == playerX &&
			   otherState.playerY == playerY &&
			   otherState.isSolved == isSolved;
	}

	@Override
	public String toString () {
		return String.format("{ playerX: %d, playerY: %d, solved: %b }", playerX, playerY, isSolved);
	}
}
