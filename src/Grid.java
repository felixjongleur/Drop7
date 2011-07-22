import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Grid {
	
	int level = 1;
	int turnsLeft = 0;
	int score = 0;	
	
	int multiplier = 0;
	NumberTile currentTile;
	int currentPieceIndex = 0;
	int raisingIndex = 0;
	
	static ArrayList<NumberTile> userPieces;	
	static ArrayList<NumberTile> rows;
	
	ArrayList<NumberTile> grid;
	static HashMap<Integer, String> multiplierToScore;
	static HashMap<Integer, NumberTile> allNumberTiles;
	
	boolean updateScore = false;
	
	ArrayList<Boolean> hits = new ArrayList<Boolean>();

	StopWatch g1 = new StopWatch();
	StopWatch g2 = new StopWatch();
	StopWatch g3 = new StopWatch();
	StopWatch g4 = new StopWatch();
	StopWatch g5 = new StopWatch();
	
	static long t1 = 0, t2 = 0, t3 = 0, t4 = 0, t5 = 0;
		
	int maxMultiplier = 22;
	
	public Grid() throws FileNotFoundException {
		grid = new ArrayList<NumberTile>();

		for(int y = 1; y < 8; y++) {
			for(int x = 1; x < 8; x++) {
				grid.add(null);
				hits.add(false);
			}
		}
		
		multiplierToScore = new HashMap<Integer, String>();
		multiplierToScore.put(1, "7");
		multiplierToScore.put(2, "39");
		multiplierToScore.put(3, "109");
		multiplierToScore.put(4, "224");
		multiplierToScore.put(5, "391");
		multiplierToScore.put(6, "617");
		multiplierToScore.put(7, "907");
		multiplierToScore.put(8, "1267");
		multiplierToScore.put(9, "1701");
		multiplierToScore.put(10, "2213");
		multiplierToScore.put(11, "2809");
		multiplierToScore.put(12, "3491");
		multiplierToScore.put(13, "4265");
		multiplierToScore.put(14, "5133");
		multiplierToScore.put(15, "6099");
		multiplierToScore.put(16, "7168");
		multiplierToScore.put(17, "8341");
		multiplierToScore.put(18, "9622");
		multiplierToScore.put(19, "11014");
		multiplierToScore.put(20, "12521");
		multiplierToScore.put(21, "14146");
		multiplierToScore.put(22, "15891");
		multiplierToScore.put(23, "15891");
		multiplierToScore.put(24, "15891");
		multiplierToScore.put(25, "15891");
		multiplierToScore.put(26, "15891");
		multiplierToScore.put(27, "15891");
		multiplierToScore.put(28, "15891");
		multiplierToScore.put(29, "15891");
		multiplierToScore.put(30, "15891");

		if(userPieces == null)
			loadUserPieces();
		if(rows == null)
			loadLevelRows();
	}
	
	public Grid(Grid g) {
		grid = new ArrayList<NumberTile>();

		for(int pos = 0; pos < g.grid.size(); pos++) {
			if(g.grid.get(pos) == null) {
				grid.add(null);
			} else {
				grid.add(getNumberTile(g.grid.get(pos).x, g.grid.get(pos).y, g.grid.get(pos).value, g.grid.get(pos).unknown));
			}
		}
		this.level = g.level;
		this.turnsLeft = g.turnsLeft;
		this.currentPieceIndex = g.currentPieceIndex;
		this.raisingIndex = g.raisingIndex;
		this.hits = g.hits;
		this.score = new Integer(g.score);
		this.currentTile = new NumberTile(g.currentTile.value, g.currentTile.unknown, g.currentTile.inHand);
	}
	
	public void loadAllNumberTiles() {
		allNumberTiles = new HashMap<Integer, NumberTile>();
		int pos = 0;
		for(int y = 1; y < 8; y++) {
			for(int x = 1; x < 8; x++) {
				for(int unknown = 0; unknown < 3; unknown++) {
					for(int value = 1; value < 8; value++) {
						allNumberTiles.put(pos, new NumberTile(x, y, value, unknown));
						pos++;
					}
				}
			}
		}
	}
	
	public static synchronized NumberTile getNumberTile(int x, int y, int value, int unknown) {
		int posInGrid = ((x - 1) + (y - 1) * 7);
		int offSet = posInGrid * 21;
		int innerValue = 0;
		
		if(unknown == 0) {
			innerValue = value - 1;
		} else if(unknown == 1) {
			innerValue = value + 6;
		} else {
			innerValue = value + 13;
		}
	//	System.out.println("GET = " + (innerValue + offSet));
		return allNumberTiles.get(innerValue + offSet);
	}
			
	public void loadLevel() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("files", "depth9.txt"));
		String str;
		while (scanner.hasNext()) {
			str = scanner.next();
			if(str.equals("#level")) {
				this.level = scanner.nextInt();
			} else if(str.equals("#turnsLeft")) {
				this.turnsLeft = scanner.nextInt();
			} else if(str.equals("#score")) {
				this.score = scanner.nextInt();
			} else if(str.equals("#currentPieceIndex")) {
				this.currentPieceIndex = scanner.nextInt();
			} else if(str.equals("#raisingIndex")) {
				this.raisingIndex = scanner.nextInt();
			} else if(str.equals("#grid")) {
				for(int y = 1; y < 8; y++) {
					for(int x = 1; x < 8; x++) {						
						str = scanner.next();
						if(!str.equals("-")) {
							int value = Integer.parseInt(str.substring(0, 1));
							int location = getNumberTileLocation(x, y);
							if (str.length() == 1) {
								grid.set(location, new NumberTile(x, y, value, 0));
							} else if (str.length() == 2) {
								grid.set(location, new NumberTile(x, y, value, 1));
							} else if (str.length() ==3) {
								grid.set(location, new NumberTile(x, y, value, 2));
							}
						}
					}
				}
			}
		}
		System.out.println(level);
		System.out.println(turnsLeft);
		System.out.println(score);
		System.out.println(currentPieceIndex);
		System.out.println(raisingIndex);

		currentTile = nextFallingPiece();
	//	if(MainWindow.debug)
	//		printOutBoard();
	}
	
	public boolean levelUp() {
		
		for (int x = 1; x < 8; x++) {
			// Check Top Row For Openings
			if (getNumberTileAtLocation(x, 1) != null)
				return false;
			// Move Everything Up One
			for (int y = 2; y < 8; ++y) {
				NumberTile nt = getNumberTileAtLocation(x, y);
				if(nt != null) {
					int location = getNumberTileLocation(x, y - 1);
					grid.set(location, getNumberTile(x, y - 1, nt.value, nt.unknown));				
				}
			}
			// Add In New Row
			NumberTile nt = nextRaisingPiece();
			int location = getNumberTileLocation(x, 7);
			grid.set(location, getNumberTile(x, 7, nt.value, nt.unknown));
		}		
		
//		printOutBoard();
		// Update Board
		burnDown();

		turnsLeft = Math.max(31 - level, 5);
		currentTile = nextFallingPiece();
		
		return true;
	}

	private void loadUserPieces() throws FileNotFoundException {
		userPieces = new ArrayList<NumberTile>();
		
		Scanner scanner = new Scanner(new File("files", "userPieces.txt"));
		String str;
		while (scanner.hasNext()) {
			str = scanner.next();
			if (str.length() == 1) {
				userPieces.add(new NumberTile(Integer.parseInt(str), 0, true));
			} else if (str.length() > 1) {
				userPieces.add(new NumberTile(Integer.parseInt(str.substring(0, 1)), 2, true));
			}			
		}
	}
	
	private void loadLevelRows() throws FileNotFoundException {
		rows = new ArrayList<NumberTile>();

		Scanner scanner = new Scanner(new File("files", "rows.txt"));

		String str;
		while (scanner.hasNext()) {
			str = scanner.next();
			rows.add(new NumberTile(Integer.parseInt(str.substring(0, 1)), 2, true));
		}
	}
	
	private void burnDown() {
	//	if(MainWindow.debug)
	//		printOutBoard();
		while (checkColumns() | checkRows()) {
			multiplier++;
			explodeHits();
		//	if(MainWindow.debug)
			//	printOutBoard();

			dropAllColumns();
			//if(MainWindow.debug)
				//printOutBoard();
			
			if(gridIsEmpty()) {
				score += 70000;
			}
		}
	}
	
	public boolean pieceHasBeenReleased() {
		if(getNumberTileAtLocation(currentTile.x, 1) == null) {
			int x = currentTile.x;
			int location = getNumberTileLocation(x, 1);
			grid.set(location, getNumberTile(x, 1, currentTile.value, currentTile.unknown));
			
			turnsLeft--;
			currentTile = null;
			multiplier = 0;
			
			dropColumn(x);			
			burnDown();
			
			if(turnsLeft == 0) {
				score += 7000;
				level++;
				if(!levelUp())
					return false;
			} else {
				currentTile = nextFallingPiece();
			}
			
			return true;
		}
		return false;
	}
	
	public boolean gridIsEmpty() {
		for(NumberTile nt : grid) {
			if(nt != null)
				return false;
		}
		return true;
	}

	public NumberTile getNumberTileAtLocation(int x, int y) {
		return grid.get((x - 1) + (y - 1) * 7);
	}
	
	public int getNumberTileLocation(int x, int y) {
		return ((x - 1) + (y - 1) * 7);
	}
	
	private void explodeHits() {
		for (int y = 1; y < 8; ++y) {
			for (int x = 1; x < 8; ++x) {
				int location = getNumberTileLocation(x, y);
				if(hits.get(location)) {
					hits.set(location, false);
					grid.set(location, null);					
					addToScore();
					// Check Up
					if(y > 1) {
						NumberTile nt = getNumberTileAtLocation(x, y - 1);
						if(nt != null && nt.unknown > 0) {
							int location2 = getNumberTileLocation(x, y - 1);
							grid.set(location2, getNumberTile(x, y - 1, nt.value, nt.unknown - 1));
						}
					}
					// Check Down
					if(y < 7) {
						NumberTile nt = getNumberTileAtLocation(x, y + 1);
						if(nt != null && nt.unknown > 0) {
							int location2 = getNumberTileLocation(x, y + 1);
							grid.set(location2, getNumberTile(x, y + 1, nt.value, nt.unknown - 1));
						}
					}
					// Check Left
					if(x > 1) {
						NumberTile nt = getNumberTileAtLocation(x - 1, y);
						if(nt != null && nt.unknown > 0) {
							int location2 = getNumberTileLocation(x - 1, y);
							grid.set(location2, getNumberTile(x - 1, y, nt.value, nt.unknown - 1));
						}
					}
					// Check Right
					if(x < 7) {
						NumberTile nt = getNumberTileAtLocation(x + 1, y);
						if(nt != null && nt.unknown > 0) {
							int location2 = getNumberTileLocation(x + 1, y);
							grid.set(location2, getNumberTile(x + 1, y, nt.value, nt.unknown - 1));
						}
					}
				}
			}
		}		
	}

	private boolean checkColumns() {
		boolean hit = false;
		for (int x = 1; x < 8; x++) {
			int numberOfTilesInColumn = 0;
			for (int y = 1; y < 8; y++) {
				if (getNumberTileAtLocation(x, y) != null) {
					numberOfTilesInColumn++;
				}
			}
			
//			System.out.println("Number of Tiles in Column["+x+"]= " + numberOfTilesInColumn);

			for (int y = 1; y < 8; y++) {
				NumberTile temp = getNumberTileAtLocation(x, y);
				if (temp != null && temp.unknown == 0 && temp.value == numberOfTilesInColumn) {
					hits.set(getNumberTileLocation(x, y), true);
					hit = true;
				}
			}
		}
		return hit;
	}

	private boolean checkRows() {
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
					for(int pos = currentStart; pos <= currentEnd; pos++) {
						NumberTile temp = getNumberTileAtLocation(pos, y);
						if (temp != null && temp.unknown == 0 && temp.value == numberOfTiles) {
							hits.set(getNumberTileLocation(pos, y), true);
							hit = true;
						}
					}
					currentStart = x + 1;	
					currentEnd = currentStart;
					numberOfTiles = 0;
				}
				if(x == 7 && currentEnd < 8) {
					for(int pos = currentStart; pos <= currentEnd; pos++) {
						NumberTile temp = getNumberTileAtLocation(pos, y);
						if (temp != null && temp.unknown == 0 && temp.value == numberOfTiles) {
							hits.set(getNumberTileLocation(pos, y), true);
							hit = true;
						}
					}
				}
			}
		}
		return hit;
	}
	
	private void dropColumn(int x) {
//		printOutBoard();
		
		int y1 = 7;

		for (int y2 = 7; y2 >= 1; --y2) {
			if (getNumberTileAtLocation(x, y2) != null) {
				if (y1 != y2) {
					int newLocation = getNumberTileLocation(x, y1);
					NumberTile nt = getNumberTileAtLocation(x, y2);					
					grid.set(newLocation, getNumberTile(x, y1, nt.value, nt.unknown));
				}
				--y1;
			}
		}

		for (; y1 >= 1; --y1) {
			int newLocation = getNumberTileLocation(x, y1);
			grid.set(newLocation, null);
		}
		
//		printOutBoard();		
	}
	
	private void dropAllColumns() {
		for(int x = 1; x < 8; x++) {
			dropColumn(x);
		}
	}
	
	public String toString() {
		String string = "";
		string += "\n";
		for(int y = 1; y < 8; y++) {
			for(int x = 1; x < 8; x++) {
				NumberTile temp = getNumberTileAtLocation(x, y);
				if(temp != null) {
					if(temp.unknown == 2)
						string += temp.value+"??";
					else if(temp.unknown == 1)
						string += temp.value+"? ";
					else
						string += temp.value+"  ";
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
		for(int y = 1; y < 8; y++) {
			for(int x = 1; x < 8; x++) {
				NumberTile temp = getNumberTileAtLocation(x, y);
				if(temp != null) {
					if(temp.unknown == 2)
						System.out.print(" "+temp.value+"?? ");
					else if(temp.unknown == 1)
						System.out.print(" "+temp.value+"?  ");
					else
						System.out.print(" "+temp.value+"   ");
				} else {
					System.out.print("  -  ");
				}
			//	System.out.print("|");
			}
			System.out.println();
		}
		System.out.println();
	}
			
	public void addToScore() {
	//	System.out.println("Multiplier = "+multiplier);
		if(multiplier > maxMultiplier) {
			System.out.println("NEW HIGH MULTIPLIER FOUND " + multiplier);
		//	maxMultiplier = multiplier;
		} else {
			score += getMultiplierToScore(multiplier);
		}
	}
	
	public static synchronized int getMultiplierToScore(int multiplier) {
		return Integer.parseInt(multiplierToScore.get(multiplier));
	}
	
	private NumberTile nextFallingPiece() {
		return getPiece(userPieces, currentPieceIndex++);
	}

	private NumberTile nextRaisingPiece() {
		return getPiece(rows, raisingIndex++);
	}

	private static synchronized NumberTile getPiece(ArrayList<NumberTile> list, int index) {
		return list.get(index);
	}
}
