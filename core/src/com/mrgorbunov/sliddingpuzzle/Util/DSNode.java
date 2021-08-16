package com.mrgorbunov.sliddingpuzzle.Util;

/**
 * The graphs produced by level search spaces are
 * sparse, hence why a node & edge representation is used.
 */

public class DSNode<E> {

	public int[] edges;
	public final E elm;

	public DSNode (E elm, int[] edges) {
		this.elm = elm;	
		this.edges = edges;
	}
	
}
