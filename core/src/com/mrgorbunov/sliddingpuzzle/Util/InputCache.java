/**
 * For some reason, Gdx.input.isKeyJustPressed()
 * returned true multiple frames in a row. So,
 * this input cache is used as a wrapper for
 * Gdx.input.
 * 
 * Under the hood, this uses Gdx.input, and must
 * be updated once per frame.
 */

package com.mrgorbunov.sliddingpuzzle.Util;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputCache {

	private boolean[] keyPressedState;
	
	public InputCache () {
		keyPressedState = new boolean[Input.Keys.MAX_KEYCODE + 1];
		Arrays.fill(keyPressedState, false);
	}

	public boolean isKeyPressed (int key) {
		return Gdx.input.isKeyPressed(key);
	}

	public boolean isKeyJustPressed (int key) {
		if (Gdx.input.isKeyJustPressed(key) 
			&& keyPressedState[key] == false) {
				keyPressedState[key] = true;
				return true;
		}

		return false;
	}

	/**
	 * This must be called once per frame to keep
	 */
	public void update () {
		for (int keyCode=0; keyCode<keyPressedState.length; keyCode++) {
			// Reset currently pressed keys only when the key is no longer being
			// pressed
			if (keyPressedState[keyCode] && 
				!Gdx.input.isKeyPressed(keyCode))
					keyPressedState[keyCode] = false;
		}
	}

}
