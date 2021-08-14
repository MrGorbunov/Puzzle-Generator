package com.mrgorbunov.sliddingpuzzle.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * This classes parses level files. 
 * 
 * Levels are stored in simple .txt files,
 * and their format is specified in the lvlFormat.txt
 * file (in core/assets/levels/)
 */
public class LevelParser {

	/**
	 * Returns a valid LevelState if the file is parsable,
	 * otherwise null.
	 */
	public static LevelState parseFile (FileHandle file) {
		String levelStr = file.readString();

		String[] lineByLine = levelStr.split("\n");
		if (lineByLine.length < 3) {
			Gdx.app.error("LevelParsing", "Improper level format - the file must be at least 3 lines long");
			return null;
		}

		String versionStr = lineByLine[0];
		if (!versionStr.equals("V0.1")) {
			Gdx.app.error("LevelParsing", "Unsupported level format version");
			return null;
		}

		String[] lvlDimensionsStr = lineByLine[1].split(" ");
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
			char[] rowArr = lineByLine[correctRow].toCharArray();

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

		return new LevelState(levelMap, playerX, playerY);
	}
	
}
