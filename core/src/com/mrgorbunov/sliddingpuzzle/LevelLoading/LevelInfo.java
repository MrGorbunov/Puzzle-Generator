package com.mrgorbunov.sliddingpuzzle.LevelLoading;

import com.badlogic.gdx.files.FileHandle;

public class LevelInfo implements Comparable<LevelInfo> {
	
	public final FileHandle file;
	public final String levelName;

	public LevelInfo (FileHandle file) {
		// TODO: Check for improper file?
		this.file = file;
		levelName = file.nameWithoutExtension();
	}

	/**
	 * Sorting is done by level name
	 */
	@Override
	public int compareTo(LevelInfo l) {
		return levelName.compareTo(l.levelName);
	}

}
