package com.mrgorbunov.sliddingpuzzle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mrgorbunov.sliddingpuzzle.LevelLoading.LevelInfo;
import com.mrgorbunov.sliddingpuzzle.Util.InputCache;

public final class RuntimeGlobals {

	private RuntimeGlobals () {}

	public static Skin skin;
	
	public static Game game;
	public static InputCache input;

	public static LevelInfo[] levels;
	public static LevelInfo activeLevel;
	
}
