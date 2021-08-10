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

	public static LevelState parseFile (String filePath) {
		FileHandle file = Gdx.files.internal(filePath);
		String levelStr = file.readString();

		String[] lineByLine = levelStr.split("\n");
		if (lineByLine.length < 2) {
			throw new IllegalArgumentException("Improper level format");
		}

		String[] lvlDimensionsStr = lineByLine[0].split(" ");
		int width = Integer.parseInt(lvlDimensionsStr[0]);
		int height = Integer.parseInt(lvlDimensionsStr[1]);


		Tile[][] levelMap = new Tile[width][height];
		int playerX = -1;
		int playerY = -1;

		for (int x=0; x<width; x++) {
			levelMap[x] = new Tile[height];
		}

		for (int y=0; y<height; y++) {
			int correctRow = height - y;
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
						if (playerX != -1 || playerY != -1)
							throw new IllegalArgumentException("Supplied map has multiple players");
						playerX = x;
						playerY = y;

						levelMap[x][y] = Tile.FLOOR;
						break;
					
					default:
						throw new IllegalArgumentException("Supplied map has illegal characters");
				}
			}
		}

		return new LevelState(levelMap, playerX, playerY);
	}
	
}
