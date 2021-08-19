package com.mrgorbunov.sliddingpuzzle.GameAnnotations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.mrgorbunov.sliddingpuzzle.GameLogic.Direction;
import com.mrgorbunov.sliddingpuzzle.GameLogic.PuzzleLevel;
import com.mrgorbunov.sliddingpuzzle.GameLogic.PuzzleState;
import com.mrgorbunov.sliddingpuzzle.Util.DSNode;
import com.mrgorbunov.sliddingpuzzle.Util.GraphAlgs;

public class LevelAnnotater {
	
	public Array<DSNode<PuzzleState>> moveGraph;

	public LevelAnnotater (PuzzleLevel levelStart) {
		generateMoveGraph(levelStart);
		GraphAlgs.debugGraph(moveGraph);
	}

	/**
	 * Constructs a graph all possible moves, starting from the current
	 * player location in the level.
	 * 
	 * Move graph is stored in instance variable moveGraph
	 */
	private void generateMoveGraph (PuzzleLevel levelAtStart) {
		PuzzleLevel level = levelAtStart.clone();
		int width = level.getWidth();
		int height = level.getHeight();

		// TODO: Use array & insertion sort instead of memo (dp) array!
		Array<DSNode<PuzzleState>> movesFound = new Array<>();
		int nextIndex = 0;	
		// The size of this memo array grows as level states become more dynamic (currently only depend on position)
		int[][] nodeIndexes = new int[width][height]; 
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				nodeIndexes[i][j] = -1;
			}
		}

		Queue<PuzzleState> statesToConsider = new Queue<>();
		Array<Boolean> indexAlreadyVisited = new Array<>(width * height / 4); // just a dumb heuristic

		statesToConsider.addFirst(level.getCurrentState());

		while (statesToConsider.size > 0) {
			PuzzleState nextState = statesToConsider.removeLast();

			int x = nextState.playerX;
			int y = nextState.playerY;

			// Check to see if this state already had an index assigned
			int thisNodeIndex;
			if (nodeIndexes[x][y] == -1) {
				thisNodeIndex = nextIndex;
				nodeIndexes[x][y] = nextIndex;
				nextIndex++;
				indexAlreadyVisited.add(true);
			} else {
				thisNodeIndex = nodeIndexes[x][y];

				if (indexAlreadyVisited.get(thisNodeIndex))
					continue;
				else
					indexAlreadyVisited.set(thisNodeIndex, true);
			}

			// Create neighbour list with -1 values by default
			// 4 possible neighbours max because only 4 possible moves
			int[] neighbours = new int[] {-1, -1, -1, -1};
			level.setCurrentState(nextState, false);

			for (Direction dir : Direction.values()) {
				PuzzleState testState = level.simulateMove(dir);
				if (testState == null)
					continue;
				
				int testX = testState.playerX;
				int testY = testState.playerY;
				int testIndex = nodeIndexes[testX][testY];

				// If this is a new state, generate a new index
				if (testIndex == -1) {
					testIndex = nextIndex;
					nodeIndexes[testX][testY] = nextIndex;
					nextIndex++;
					indexAlreadyVisited.add(false);
				}

				neighbours[dir.ordinal()] = testIndex;

				statesToConsider.addFirst(testState);
			}

			DSNode<PuzzleState> nextNode = new DSNode<>(nextState, neighbours);
			movesFound.add(nextNode);
		}

		moveGraph = movesFound;
	}


}
