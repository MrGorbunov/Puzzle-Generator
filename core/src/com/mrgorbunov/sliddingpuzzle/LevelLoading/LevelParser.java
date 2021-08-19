package com.mrgorbunov.sliddingpuzzle.LevelLoading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mrgorbunov.sliddingpuzzle.GameLogic.PuzzleLevel;
import com.mrgorbunov.sliddingpuzzle.GameLogic.Tile;

/**
 * This classes parses level files. 
 * 
 * Levels are stored in simple .txt files,
 * and their format is specified in the lvlFormat.txt
 * file (in core/assets/levels/)
 */
public class LevelParser {

	// TODO: Read with buffered reader instead of into a string

	/**
	 * Returns a valid LevelState if the file is parsable,
	 * otherwise null.
	 */
	public static PuzzleLevel parseFile (FileHandle file) {
		String[] fileByLine = file.readString().split("\n");

		if (fileByLine.length < 3) {
			Gdx.app.error("LevelParsing", "Improper level format - the file must be at least 3 lines long");
			return null;
		}

		String versionStr = fileByLine[0];
		if (!versionStr.equals("V0.1")) {
			Gdx.app.error("LevelParsing", "Unsupported level format version");
			return null;
		}

		String[] lvlDimensionsStr = fileByLine[1].split(" ");
		int width, height = 0;
		try {
			width = Integer.parseInt(lvlDimensionsStr[0]);
			height = Integer.parseInt(lvlDimensionsStr[1]);
		} catch (NumberFormatException nfe) {
			Gdx.app.error("LevelParsing", "Improper level format - could not parse either level width or height");
			return null;
		}


		Tile[][] levelMap = new Tile[width][height];
		int playerX = -1;
		int playerY = -1;

		for (int x=0; x<width; x++) {
			levelMap[x] = new Tile[height];
		}

		for (int y=0; y<height; y++) {
			int correctRow = height - y + 1;
			char[] rowArr = fileByLine[correctRow].toCharArray();

			for (int x=0; x<width; x++) {
				char tileChar = rowArr[x];
				
				switch (tileChar) {
					case '*':
						levelMap[x][y] = Tile.FLOOR;
						break;
					
					case '#':
						levelMap[x][y] = Tile.WALL;
						break;

					case '$':
						levelMap[x][y] = Tile.FINISH;
						break;

					case '&':
						if (playerX != -1 || playerY != -1) {
							Gdx.app.error("LevelParsing", "Improper level format - multiple player characters");
							return null;
						}

						playerX = x;
						playerY = y;

						levelMap[x][y] = Tile.FLOOR;
						break;
					
					default:
						Gdx.app.error("LevelParsing", "Improper level format - unrecognized character '" + tileChar + "'");
						return null;
				}
			}
		}

		return new PuzzleLevel(levelMap, playerX, playerY);
	}

	/**
	 * Will check if the file is valid without instancing a whole
	 * LevelState. In terms of File I/O this is as expensive as parsing
	 * the file, however if instancing LevelState ever becomes expensive
	 * then this will save on that.
	 */
	public static boolean isValidFile (FileHandle file) {
		String[] fileByLine = file.readString().split("\n");

		if (fileByLine.length < 3)
			return false;

		String versionStr = fileByLine[0];
		if (!versionStr.equals("V0.1"))
			return false;
		
		String[] lvlDimensionsStr = fileByLine[1].split(" ");
		int width = 0;
		int height = 0;
		try {
			width = Integer.parseInt(lvlDimensionsStr[0]);
			height = Integer.parseInt(lvlDimensionsStr[1]);
		} catch (NumberFormatException nfe) {
			return false;
		}

		boolean playerFound = false;

		for (int y=0; y<height; y++) {
			char[] rowArr = fileByLine[height - y + 1].toCharArray();

			for (int x=0; x<width; x++) {
				char tileChar = rowArr[x];

				switch (tileChar) {
					case '*':
					case '#':
					case '$':
						continue;
					
					case '&':
						if (playerFound)
							return false;
						playerFound = true;
						break;

					default:
						return false;
				}
			}
		}

		return playerFound;
	}
	
}
