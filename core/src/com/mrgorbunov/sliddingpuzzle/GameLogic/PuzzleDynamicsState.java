package com.mrgorbunov.sliddingpuzzle.GameLogic;

public class PuzzleDynamicsState {

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
	public PuzzleDynamicsState (int playerX, int playerY, boolean isSolved) {
		this.playerX = playerX;
		this.playerY = playerY;
		this.isSolved = isSolved;
	}

	@Override
	public PuzzleDynamicsState clone () {
		return new PuzzleDynamicsState(playerX, playerY, isSolved);
	}

	@Override
	public boolean equals (Object o) {
		if (o == this)
			return true;
		
		if (!(o instanceof PuzzleDynamicsState))
			return false;

		PuzzleDynamicsState otherState = (PuzzleDynamicsState) o;

		return otherState.playerX == playerX &&
			   otherState.playerY == playerY &&
			   otherState.isSolved == isSolved;
	}

	@Override
	public String toString () {
		return String.format("{ playerX: %d, playerY: %d, solved: $b }", playerX, playerY, isSolved);
	}
}
