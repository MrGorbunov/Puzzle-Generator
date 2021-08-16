package com.mrgorbunov.sliddingpuzzle.Util;

import com.badlogic.gdx.Gdx;

/**
 * Graph Algorithms
 * 
 * Utility class with static methods for graph algorithms.
 */

public final class GraphAlgs {

	private GraphAlgs () {}

	public static void debugGraph (DSNode<?>[] nodes) {
		Gdx.app.log("GraphAlgs", "=== Printing Graph ===");
		Gdx.app.log("GraphAlgs", "Edge ordering: UP, RIGHT, DOWN, LEFT");

		DSNode<?> node;
		StringBuilder edgeStr;

		for (int i=0; i<nodes.length; i++) {
			node = nodes[i];
			Gdx.app.log("GraphAlgs", String.format("%3d | %s", i, node.elm.toString()));

			edgeStr = new StringBuilder();
			for (int j=0; j<node.edges.length; j++) {
				edgeStr.append(node.edges[j]);

				if (j + 1 != node.edges.length)
					edgeStr.append(", ");
			}

			Gdx.app.log("GraphAlgs", "    | E: " + edgeStr.toString());
		}
	}
	
}
