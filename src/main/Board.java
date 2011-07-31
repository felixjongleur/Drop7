package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Board {

	private int level = 0;
	private int turnsLeft = 0;
	private int score = 0;

	private int multiplier = 0;
	private NumberTile currentTile;
	private int currentPieceIndex = 0;
	private int raisingIndex = 0;
	private int sequenceIndex = 0;

	static List<NumberTile> userPieces;
	static List<NumberTile> rows;

	static List<Integer> sequence;
	static Map<Integer, Integer> multiplierToScore;
	static Map<Integer, NumberTile> allNumberTiles;

	private boolean sequenceLoaded = false;
	private boolean sequenceLoop = false;

	private Boolean[] hits;
	private NumberTile[] grid;
	private List<Integer> dropSequence;

	StopWatch g1 = new StopWatch();
	StopWatch g2 = new StopWatch();
	StopWatch g3 = new StopWatch();
	StopWatch g4 = new StopWatch();
	StopWatch g5 = new StopWatch();

	static long t1 = 0, t2 = 0, t3 = 0, t4 = 0, t5 = 0;
	static long n1 = 0, n2 = 0, n3 = 0, n4 = 0, n5 = 0;

	private int maxMultiplier = 26;

	public Board() throws FileNotFoundException {
		this(true, false, false, "", "");
	}

	public Board(boolean newGame, boolean loadSequence, boolean sequenceLoop,
			String loadFileName, String levelFileName)
			throws FileNotFoundException {

		grid = new NumberTile[49];
		hits = new Boolean[49];
		dropSequence = new ArrayList<Integer>();

		if (userPieces == null)
			loadUserPieces();
		if (rows == null)
			loadLevelRows();
		if (multiplierToScore == null)
			loadMultiplierToScore();
		if (allNumberTiles == null)
			loadAllNumberTiles();
		if (loadSequence && sequence == null)
			loadSequence(loadFileName, sequenceLoop);
		else
			sequence = new ArrayList<Integer>();

		if (newGame)
			levelUp();
		else
			loadLevel(levelFileName);
	}

	public Board(Board g) {
		grid = new NumberTile[49];

		for (int pos = 0; pos < 49; pos++) {
			if (g.getGrid()[pos] != null) {
				NumberTile nt = g.getGrid()[pos];
				getGrid()[pos] = getNumberTile(nt.getX(), nt.getY(), nt.getValue(), nt.getUnknown());
			}
		}

		hits = new Boolean[49];

		this.level = g.level;
		this.turnsLeft = g.turnsLeft;
		this.currentPieceIndex = g.currentPieceIndex;
		this.raisingIndex = g.raisingIndex;
		this.score = g.score;
		this.dropSequence = new ArrayList<Integer>(g.dropSequence);
		this.currentTile = getNumberTile(1, 1, g.currentTile.getValue(), g.currentTile.getUnknown());
	}

	private void loadSequence(String fileName, boolean sequenceLoop) throws FileNotFoundException {
		List<Integer> temp = new ArrayList<Integer>();
		Scanner scanner = new Scanner(new File("files", fileName));
		while (scanner.hasNext()) {
			String str = scanner.next();
			if (str.length() == 1) {
				temp.add(Integer.parseInt(str));
			}
		}
		sequence = Collections.unmodifiableList(temp);
		sequenceLoaded = true;
		this.sequenceLoop = sequenceLoop;
	}

	private void loadAllNumberTiles() {
		Map<Integer, NumberTile> temp = new HashMap<Integer, NumberTile>();
		int pos = 0;
		for (int y = 1; y < 8; y++) {
			for (int x = 1; x < 8; x++) {
				for (int unknown = 0; unknown < 3; unknown++) {
					for (int value = 1; value < 8; value++) {
						temp.put(pos, new NumberTile(x, y, value, unknown));
						pos++;
					}
				}
			}
		}
		allNumberTiles = Collections.unmodifiableMap(temp);
	}

	private void loadMultiplierToScore() throws FileNotFoundException {
		Map<Integer, Integer> temp = new HashMap<Integer, Integer>();

		Scanner scanner = new Scanner(new File("files", "chainScores.txt"));
		int pos = 1;
		while (scanner.hasNext()) {
			temp.put(pos++, scanner.nextInt());
		}
		multiplierToScore = Collections.unmodifiableMap(temp);
	}

	private void loadUserPieces() throws FileNotFoundException {
		List<NumberTile> temp = new ArrayList<NumberTile>();

		Scanner scanner = new Scanner(new File("files", "userPieces.txt"));
		String str;
		while (scanner.hasNext()) {
			str = scanner.next();
			if (str.length() == 1) {
				temp.add(new NumberTile(Integer.parseInt(str), 0, true));
			} else if (str.length() > 1) {
				temp.add(new NumberTile(Integer.parseInt(str.substring(0, 1)),
						2, true));
			}
		}
		userPieces = Collections.unmodifiableList(temp);
	}

	private void loadLevelRows() throws FileNotFoundException {
		List<NumberTile> temp = new ArrayList<NumberTile>();

		Scanner scanner = new Scanner(new File("files", "rows.txt"));

		String str;
		while (scanner.hasNext()) {
			str = scanner.next();
			temp.add(new NumberTile(Integer.parseInt(str.substring(0, 1)), 2,
					true));
		}
		rows = Collections.unmodifiableList(temp);
	}

	public void loadLevel(String fileName) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("files", fileName));
		String str;
		while (scanner.hasNext()) {
			str = scanner.next();
			if (str.equals("#level")) {
				this.level = scanner.nextInt();
			} else if (str.equals("#turnsLeft")) {
				this.turnsLeft = scanner.nextInt();
			} else if (str.equals("#score")) {
				this.score = scanner.nextInt();
			} else if (str.equals("#currentPieceIndex")) {
				this.currentPieceIndex = scanner.nextInt();
			} else if (str.equals("#raisingIndex")) {
				this.raisingIndex = scanner.nextInt();
			} else if (str.equals("#grid")) {
				for (int y = 1; y < 8; y++) {
					for (int x = 1; x < 8; x++) {
						str = scanner.next();
						if (!str.equals("-")) {
							int value = Integer.parseInt(str.substring(0, 1));
							if (str.length() == 1) {
								setNumberTile(x, y, value, 0);
							} else if (str.length() == 2) {
								setNumberTile(x, y, value, 1);
							} else if (str.length() == 3) {
								setNumberTile(x, y, value, 2);
							}
						}
					}
				}
			}
		}
		currentTile = nextFallingPiece();
	}

	public void setNumberTile(int x, int y, int value, int unknown) {
		if (value == 0)
			grid[getNumberTileLocation(x, y)] = null;
		else
			grid[getNumberTileLocation(x, y)] = getNumberTile(x, y, value,
					unknown);
	}

	public NumberTile getNumberTile(int x, int y, int value, int unknown) {
		int posInGrid = ((x - 1) + (y - 1) * 7);
		int offSet = posInGrid * 21;
		int innerValue = 0;

		if (unknown == 0) {
			innerValue = value - 1;
		} else if (unknown == 1) {
			innerValue = value + 6;
		} else {
			innerValue = value + 13;
		}
		return allNumberTiles.get(innerValue + offSet);
	}

	public boolean getHitAt(int x, int y) {
		if (hits[getNumberTileLocation(x, y)] == null)
			return false;
		else
			return true;
	}

	public boolean levelUp() {
		level++;

		for (int x = 1; x < 8; x++) {
			// Check Top Row For Openings
			if (getNumberTileAtLocation(x, 1) != null)
				return false;
			// Move Everything Up One
			for (int y = 2; y < 8; ++y) {
				NumberTile nt = getNumberTileAtLocation(x, y);
				if (nt != null) {
					setNumberTile(x, y - 1, nt.getValue(), nt.getUnknown());
				}
			}
			// Add In New Row
			NumberTile nt = nextRaisingPiece();
			setNumberTile(x, 7, nt.getValue(), nt.getUnknown());
		}

		// Update Board
		burnDown();

		turnsLeft = Math.max(31 - level, 5);

		currentTile = nextFallingPiece();

		return true;
	}

	public void burnDown() {
		while (checkColumns() | checkRows()) {
			multiplier++;
			explodeHits();
			dropAllColumns();

			if (gridIsEmpty())
				score += 70000;
		}
	}

	public void doSequence() {
		if (sequenceIndex < sequence.size()) {
			currentTile.setX(nextSequenceColumn());
		} else {
			System.out.println("#level " + level);
			System.out.println("#turnsLeft " + turnsLeft);
			System.out.println("#score " + score);
			System.out.println("#currentPieceIndex " + currentPieceIndex);
			//System.out.println("#currentPieceIndex " + (currentPieceIndex - 1));
			System.out.println("#raisingIndex " + raisingIndex);
			System.out.println("#grid");
			printBoardForFile();
		}
	}

	public boolean pieceHasBeenReleased() {
		if (sequenceLoaded && sequenceIndex < sequence.size() + 1)
			doSequence();

		if (getNumberTileAtLocation(currentTile.getX(), 1) == null) {
			int x = currentTile.getX();
			setNumberTile(x, 1, currentTile.getValue(),
					currentTile.getUnknown());

			turnsLeft--;
			currentTile = null;
			multiplier = 0;
			dropColumn(x);
			burnDown();

			if (turnsLeft == 0) {
				score += 7000;
				if (!levelUp())
					return false;
			} else {
				currentTile = nextFallingPiece();
			}

			if (sequenceLoop && sequenceIndex < sequence.size() + 1)
				pieceHasBeenReleased();

			return true;
		}
		return false;
	}

	public boolean gridIsEmpty() {
		for (int pos = 0; pos < grid.length; pos++) {
			if (grid[pos] != null)
				return false;
		}
		return true;
	}

	public NumberTile getNumberTileAtLocation(int x, int y) {
		return grid[(x - 1) + (y - 1) * 7];
	}

	public int getNumberTileLocation(int x, int y) {
		return ((x - 1) + (y - 1) * 7);
	}

	public void explodeHits() {
		for (int y = 1; y < 8; ++y) {
			for (int x = 1; x < 8; ++x) {
				int location = getNumberTileLocation(x, y);
				if (hits[location] != null) {
					hits[location] = null;
					grid[location] = null;
					addToScore();
					// Check Up
					if (y > 1) {
						NumberTile nt = getNumberTileAtLocation(x, y - 1);
						if (nt != null && nt.getUnknown() > 0)
							setNumberTile(x, y - 1, nt.getValue(),
									nt.getUnknown() - 1);
					}
					// Check Down
					if (y < 7) {
						NumberTile nt = getNumberTileAtLocation(x, y + 1);
						if (nt != null && nt.getUnknown() > 0)
							setNumberTile(x, y + 1, nt.getValue(),
									nt.getUnknown() - 1);
					}
					// Check Left
					if (x > 1) {
						NumberTile nt = getNumberTileAtLocation(x - 1, y);
						if (nt != null && nt.getUnknown() > 0)
							setNumberTile(x - 1, y, nt.getValue(),
									nt.getUnknown() - 1);
					}
					// Check Right
					if (x < 7) {
						NumberTile nt = getNumberTileAtLocation(x + 1, y);
						if (nt != null && nt.getUnknown() > 0)
							setNumberTile(x + 1, y, nt.getValue(),
									nt.getUnknown() - 1);
					}
				}
			}
		}
	}

	public boolean checkColumns() {
		boolean hit = false;
		for (int x = 1; x < 8; x++) {
			int numberOfTilesInColumn = 0;
			for (int y = 1; y < 8; y++) {
				if (getNumberTileAtLocation(x, y) != null) {
					numberOfTilesInColumn++;
				}
			}

			for (int y = 1; y < 8; y++) {
				NumberTile temp = getNumberTileAtLocation(x, y);
				if (temp != null && temp.getUnknown() == 0
						&& temp.getValue() == numberOfTilesInColumn) {
					hits[getNumberTileLocation(x, y)] = true;
					hit = true;
				}
			}
		}
		return hit;
	}

	public boolean checkRows() {
		boolean hit = false;
		for (int y = 7; y > 0; y--) {
			int numberOfTiles = 0;

			int currentStart = 1;
			int currentEnd = 1;

			for (int x = 1; x < 8; x++) {
				if (getNumberTileAtLocation(x, y) != null) {
					numberOfTiles++;
					currentEnd = x;
				} else {
					// There is an open space
					for (int pos = currentStart; pos <= currentEnd; pos++) {
						NumberTile temp = getNumberTileAtLocation(pos, y);
						if (temp != null && temp.getUnknown() == 0
								&& temp.getValue() == numberOfTiles) {
							hits[getNumberTileLocation(pos, y)] = true;
							hit = true;
						}
					}
					currentStart = x + 1;
					currentEnd = currentStart;
					numberOfTiles = 0;
				}
				if (x == 7 && currentEnd < 8) {
					for (int pos = currentStart; pos <= currentEnd; pos++) {
						NumberTile temp = getNumberTileAtLocation(pos, y);
						if (temp != null && temp.getUnknown() == 0
								&& temp.getValue() == numberOfTiles) {
							hits[getNumberTileLocation(pos, y)] = true;
							hit = true;
						}
					}
				}
			}
		}
		return hit;
	}

	public void dropColumn(int x) {
		int y1 = 7;

		for (int y2 = 7; y2 >= 1; --y2) {
			if (getNumberTileAtLocation(x, y2) != null) {
				if (y1 != y2) {
					NumberTile nt = getNumberTileAtLocation(x, y2);
					setNumberTile(x, y1, nt.getValue(), nt.getUnknown());
				}
				--y1;
			}
		}

		for (; y1 >= 1; --y1) {
			setNumberTile(x, y1, 0, 0);
		}
	}

	private void dropAllColumns() {
		for (int x = 1; x < 8; x++) {
			dropColumn(x);
		}
	}

	@Override
	public String toString() {
		String string = "";
		string += "\n";
		for (int y = 1; y < 8; y++) {
			for (int x = 1; x < 8; x++) {
				NumberTile temp = getNumberTileAtLocation(x, y);
				if (temp != null) {
					if (temp.getUnknown() == 2)
						string += temp.getValue() + "??";
					else if (temp.getUnknown() == 1)
						string += temp.getValue() + "? ";
					else
						string += temp.getValue() + "  ";
				} else {
					string += "   ";
				}
				string += "|";
			}
			string += "\n";
		}
		string += "\n";
		return string;
	}

	public void printBoardForFile() {
		System.out.println();
		for (int y = 1; y < 8; y++) {
			for (int x = 1; x < 8; x++) {
				NumberTile temp = getNumberTileAtLocation(x, y);
				if (temp != null) {
					if (temp.getUnknown() == 2)
						System.out.print(" " + temp.getValue() + "?? ");
					else if (temp.getUnknown() == 1)
						System.out.print(" " + temp.getValue() + "?  ");
					else
						System.out.print(" " + temp.getValue() + "   ");
				} else {
					System.out.print("  -  ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	public void addToScore() {
		if (multiplier > maxMultiplier) {
			System.out.println("NEW HIGH MULTIPLIER FOUND " + multiplier);
			System.out.println(dropSequence);
		}
		score += getMultiplierToScore(multiplier);
	}

	public int getMultiplierToScore(int multiplier) {
		return multiplierToScore.get(multiplier);
	}

	private NumberTile nextFallingPiece() {
		return getNumberTile(userPieces, currentPieceIndex++);
	}

	private NumberTile nextRaisingPiece() {
		return getNumberTile(rows, raisingIndex++);
	}

	private int nextSequenceColumn() {
		return getColumn(sequence, sequenceIndex++);
	}

	private NumberTile getNumberTile(List<NumberTile> list, int index) {
		return list.get(index);
	}

	private int getColumn(List<Integer> list, int index) {
		return list.get(index);
	}

	public NumberTile getCurrentTile() {
		return currentTile;
	}

	public void setCurrentTile(NumberTile currentTile) {
		this.currentTile = currentTile;
	}

	public int getScore() {
		return score;
	}

	public NumberTile[] getGrid() {
		return grid;
	}
	
	public int getLevel() {
		return level;
	}

	public int getTurnsLeft() {
		return turnsLeft;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int m) {
		multiplier = m;
	}

	public int getCurrentPieceIndex() {
		return currentPieceIndex;
	}

	public int getRaisingIndex() {
		return raisingIndex;
	}

	public int getSequenceIndex() {
		return sequenceIndex;
	}

	public static List<NumberTile> getUserPieces() {
		return userPieces;
	}

	public static List<NumberTile> getRows() {
		return rows;
	}

	public static List<Integer> getSequence() {
		return sequence;
	}

	public List<Integer> getDropSequence() {
		return dropSequence;
	}

	public static Map<Integer, Integer> getMultiplierToScore() {
		return multiplierToScore;
	}

	public static Map<Integer, NumberTile> getAllNumberTiles() {
		return allNumberTiles;
	}

	public int getMaxMultiplier() {
		return maxMultiplier;
	}
}
