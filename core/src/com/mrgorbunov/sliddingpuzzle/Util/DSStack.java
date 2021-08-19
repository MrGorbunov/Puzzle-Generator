package com.mrgorbunov.sliddingpuzzle.Util;

import com.badlogic.gdx.utils.Array;

/**
 * Stack Implementation, using Gdx.Array under the hood.
 * 
 * Stack is prefixed with DS to mean Data Structure Stack,
 * and avoid naming conflict with the GDX UI stack & with
 * java.util stack.
 */

public class DSStack<T> {

	Array<T> arr;

	public DSStack () {
		arr = new Array<>();
	}

	public int size () {
		return arr.size;
	}

	/**
	 * Returns the top of the stack without
	 * popping it.
	 * 
	 * Returns null if the Stack is empty
	 */
	public T peekTop () {
		if (arr.size == 0)
			return null;

		return arr.get(arr.size - 1);
	}

	public T pop () {
		return arr.pop();
	}

	public void add (T elm) {
		arr.add(elm);
	}
	
}
