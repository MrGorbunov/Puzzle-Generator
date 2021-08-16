/**
 * Level Search
 * 
 * This class will search through all possible moves of a level,
 * constructing a graph of PuzzleStates with directions as edges.
 */

package com.mrgorbunov.sliddingpuzzle.GameAnnotations;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.mrgorbunov.sliddingpuzzle.GameLogic.Direction;
import com.mrgorbunov.sliddingpuzzle.GameLogic.LevelState;
import com.mrgorbunov.sliddingpuzzle.GameLogic.PuzzleState;
import com.mrgorbunov.sliddingpuzzle.Util.DSNode;
import com.mrgorbunov.sliddingpuzzle.Util.GraphAlgs;

public final class LevelSearch {

	private LevelSearch () { }

	/**
	 * Constructs a graph all possible moves, starting with the current
	 * player location in the level.
	 * 
	 * Does so by perfoming a BFS
	 */
	public static DSNode<PuzzleState>[] getMoveGraph (LevelState levelAtStart) {
		LevelState level = levelAtStart.clone();
		int width = level.getWidth();
		int height = level.getHeight();

		// TODO: Use array & insertion sort instead of memo (dp) array!
		Array<DSNode<PuzzleState>> moveGraphObject = new Array<>();
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

		Gdx.app.log("LevelSearch", "Started BFS");

		statesToConsider.addFirst(level.getCurrentState());

		while (statesToConsider.size > 0) {
			Gdx.app.log("LevelSearch", "BFS Iteration; Queue size: " + statesToConsider.size);

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
			moveGraphObject.add(nextNode);
		}

		Gdx.app.log("LevelSearch", "BFS Done, now converting datatype");

		int numNodes = moveGraphObject.size;
		DSNode<PuzzleState> classReference = new DSNode<PuzzleState>(null, null);
		DSNode<PuzzleState>[] moveGraph = (DSNode<PuzzleState>[]) java.lang.reflect.Array.newInstance(classReference.getClass(), numNodes);

		for (int i=0; i<numNodes; i++) {
			moveGraph[i] = moveGraphObject.get(i);
		}

		Gdx.app.log("LevelSearch", "Conversion done, returning");

		return moveGraph;
	}
	
}

