package main;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BoardTest {

	Board board;
	
	@Before
	public void setUp() throws FileNotFoundException {
		board = new Board();
	}
	
	@Test
	public void testLoadFiles() throws FileNotFoundException {
		assertNotNull(Board.getUserPieces());
		assertNotNull(Board.getMultiplierToScore());
		assertNotNull(Board.getRows());
		// The rows file has to have multiples of 7 tiles
		assertTrue(Board.getRows().size() % 7 == 0);
		assertNotNull(Board.getAllNumberTiles());
		// 21 Tiles per each of the 49 Squares
		assertEquals(1029, Board.getAllNumberTiles().size());
		
		board = new Board(true, true, true, "sequence9.txt", "");
		assertNotNull(Board.getSequence());
	}
	
	@Test
	public void testBoard() throws FileNotFoundException {		
		//assertEquals(49, board.getGrid().size());
		assertEquals(49, board.getGrid().length);
		
		// First 6 Rows are empty
		for(int pos = 0; pos < 42; pos++)
			assertNull(board.getGrid()[pos]);
			//assertNull(board.getGrid().get(pos));
		
		// Last Row contains 7 tiles
		for(int pos = 42; pos < 49; pos++)
			assertNotNull(board.getGrid()[pos]);
			//assertNotNull(board.getGrid().get(pos));

	//	for(boolean hit : board.getHits())
	//		assertFalse(hit);
	}
	
	@Test
	public void testBoardCopyConstructor() {
		Board b = new Board(board);		
		assertNotSame(b, board);
		assertNotSame(b.getGrid(), board.getGrid());
		assertNotSame(b.getCurrentTile(), board.getCurrentTile());
	}
	
	@Test
	public void testGetNumberTile() {
		NumberTile nt1 = board.getNumberTile(1, 1, 5, 2);
		assertEquals(1, nt1.getX());
		assertEquals(1, nt1.getY());
		assertEquals(5, nt1.getValue());
		assertEquals(2, nt1.getUnknown());		

		NumberTile nt2 = board.getNumberTile(4, 7, 3, 0);
		assertEquals(4, nt2.getX());
		assertEquals(7, nt2.getY());
		assertEquals(3, nt2.getValue());
		assertEquals(0, nt2.getUnknown());
	}
	
	@Test
	public void testLevelUpColumnFullSoFalse() {
		Board b = new Board(board);
		for(int y = 1; y < 8; y++) {
			b.setNumberTile(1, y, 1, 2);
		}
		assertFalse(b.levelUp());
	}
	
	@Test
	public void testLevelUpMoveUpOneRowAndAddNewRow() {
		Board b = new Board(board);
		List<NumberTile> firstRow = new ArrayList<NumberTile>();
		
		for(int x = 1; x < 8; x++) {
			if(b.getNumberTileAtLocation(x, 7) != null)
				firstRow.add(b.getNumberTileAtLocation(x, 7));
		}
		
		b.levelUp();
		
		List<NumberTile> secondRow = new ArrayList<NumberTile>();
		
		for(int x = 1; x < 8; x++) {
			if(b.getNumberTileAtLocation(x, 6) != null)
				secondRow.add(b.getNumberTileAtLocation(x, 6));
		}
		
		assertEquals(firstRow.size(), secondRow.size());
		
		for(int pos = 0; pos < firstRow.size(); pos++) {
			assertEquals(firstRow.get(pos).getX(), secondRow.get(pos).getX());
			assertEquals(firstRow.get(pos).getY() - 1, secondRow.get(pos).getY());
			assertEquals(firstRow.get(pos).getValue(), secondRow.get(pos).getValue());
			assertEquals(firstRow.get(pos).getUnknown(), secondRow.get(pos).getUnknown());
		}
		
		firstRow = new ArrayList<NumberTile>();
		
		for(int x = 1; x < 8; x++) {
			if(b.getNumberTileAtLocation(x, 7) != null)
				firstRow.add(b.getNumberTileAtLocation(x, 7));
		}
		
		assertEquals(7, firstRow.size());
	}
	
	@Test
	public void testLevelUpTurnsLeftDecremented() {
		Board b = new Board(board);
		b.levelUp();
		
		int startingTurns = b.getTurnsLeft();
		
		b.levelUp();
		
		assertEquals(startingTurns - 1, b.getTurnsLeft());
	}
	
	@Test
	public void testLevelUpCurrentTileUpdated() {
		Board b = new Board(board);
		NumberTile nt = b.getCurrentTile();
		b.levelUp();
		
		assertNotSame(nt, b.getCurrentTile());
	}
	
	@Test
	public void testDropColumn() {
		Board b = new Board(board);

		NumberTile nt  = b.getNumberTile(1, 1, 3, 0);
		NumberTile nt2 = b.getNumberTile(1, 6, 3, 0);
		b.setCurrentTile(nt);
		
		List<NumberTile> firstColumn = new ArrayList<NumberTile>();
		
		for(int y = 1; y < 8; y++) {
			if(b.getNumberTileAtLocation(1, y) != null) 
				firstColumn.add(b.getNumberTileAtLocation(1, y));
		}
		
		assertEquals(1, firstColumn.size());
		
		b.setNumberTile(nt.getX(), nt.getY(), nt.getValue(), nt.getUnknown());

		b.dropColumn(1);

		firstColumn = new ArrayList<NumberTile>();
		
		for(int y = 1; y < 8; y++) {
			if(b.getNumberTileAtLocation(1, y) != null) 
				firstColumn.add(b.getNumberTileAtLocation(1, y));
		}
		
		assertEquals(2, firstColumn.size());
		assertSame(nt2, b.getNumberTileAtLocation(1, 6));
	}
	
	@Test
	public void testCheckColumnFalseWithNoHits() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// -
			// 6
			// 4
			// 6
			// 3
			// 2 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 2, 0);
			b.setNumberTile(x, 6, 3, 0);
			b.setNumberTile(x, 5, 6, 0);
			b.setNumberTile(x, 4, 4, 0);	
			b.setNumberTile(x, 3, 6, 0);		
			
			assertFalse(b.checkColumns());		
			assertFalse(b.getHitAt(x, 3));	
		}
	}
	
	@Test
	public void testCheckColumnsWithOnes() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// -
			// -
			// -
			// -
			// -
			// 1 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 1, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 7));
		}
	}
	
	@Test
	public void testCheckColumnsWith1Two() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// -
			// -
			// -
			// -
			// 6
			// 2 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 2, 0);
			b.setNumberTile(x, 6, 6, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 7));		
		}	
	}
	
	@Test
	public void testCheckColumnsWith2Twos() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// -
			// -
			// -
			// -
			// 2
			// 2 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 2, 0);
			b.setNumberTile(x, 6, 2, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 6));
			assertTrue(b.getHitAt(x, 7));
		}	
	}
	
	@Test
	public void testCheckColumnsWithThrees() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// -
			// -
			// -
			// 6
			// 3
			// 2 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 2, 0);
			b.setNumberTile(x, 6, 3, 0);
			b.setNumberTile(x, 5, 6, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 6));	
		}
	}
	
	@Test
	public void testCheckColumnsWithMultipleThrees() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// -
			// -
			// -
			// 3
			// 3
			// 2 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 2, 0);
			b.setNumberTile(x, 6, 3, 0);
			b.setNumberTile(x, 5, 3, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 5));	
			assertTrue(b.getHitAt(x, 6));	
		}
	}
	
	@Test
	public void testCheckColumnsWithFours() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// -
			// -
			// 4
			// 6
			// 3
			// 2 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 2, 0);
			b.setNumberTile(x, 6, 3, 0);
			b.setNumberTile(x, 5, 6, 0);
			b.setNumberTile(x, 4, 4, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 4));	
		}
	}
	
	@Test
	public void testCheckColumnsWithMultipleFours() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// -
			// -
			// 4
			// 6
			// 4
			// 2 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 2, 0);
			b.setNumberTile(x, 6, 4, 0);
			b.setNumberTile(x, 5, 6, 0);
			b.setNumberTile(x, 4, 4, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 4));
			assertTrue(b.getHitAt(x, 6));	
		}
	}
	
	@Test
	public void testCheckColumnsWithFives() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// -
			// 5
			// 4
			// 6
			// 3
			// 2 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 2, 0);
			b.setNumberTile(x, 6, 3, 0);
			b.setNumberTile(x, 5, 6, 0);
			b.setNumberTile(x, 4, 4, 0);	
			b.setNumberTile(x, 3, 5, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 3));	
		}
	}
	
	@Test
	public void testCheckColumnsWithMultipleFives() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// -
			// 5
			// 5
			// 6
			// 3
			// 5 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 5, 0);
			b.setNumberTile(x, 6, 3, 0);
			b.setNumberTile(x, 5, 6, 0);
			b.setNumberTile(x, 4, 5, 0);	
			b.setNumberTile(x, 3, 5, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 3));	
			assertTrue(b.getHitAt(x, 4));	
			assertTrue(b.getHitAt(x, 7));	
		}
	}
	
	@Test
	public void testCheckColumnsWithSixes() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// 2
			// 5
			// 4
			// 6
			// 3
			// 2 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 2, 0);
			b.setNumberTile(x, 6, 3, 0);
			b.setNumberTile(x, 5, 6, 0);
			b.setNumberTile(x, 4, 4, 0);	
			b.setNumberTile(x, 3, 5, 0);	
			b.setNumberTile(x, 2, 2, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 5));	
		}
	}
	
	@Test
	public void testCheckColumnsWithMultipleSixes() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// -
			// 6
			// 5
			// 4
			// 6
			// 3
			// 6 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 6, 0);
			b.setNumberTile(x, 6, 3, 0);
			b.setNumberTile(x, 5, 6, 0);
			b.setNumberTile(x, 4, 4, 0);	
			b.setNumberTile(x, 3, 5, 0);	
			b.setNumberTile(x, 2, 6, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 2));	
			assertTrue(b.getHitAt(x, 5));	
			assertTrue(b.getHitAt(x, 7));	
		}
	}
	
	@Test
	public void testCheckColumnsWithSevens() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// 7
			// 2
			// 5
			// 4
			// 6
			// 3
			// 2 - - - - - -
			clearBoard(b);
			b.setNumberTile(x, 7, 2, 0);
			b.setNumberTile(x, 6, 3, 0);
			b.setNumberTile(x, 5, 6, 0);
			b.setNumberTile(x, 4, 4, 0);	
			b.setNumberTile(x, 3, 5, 0);	
			b.setNumberTile(x, 2, 2, 0);	
			b.setNumberTile(x, 1, 7, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 1));	
		}
	}
	
	@Test
	public void testCheckColumnsWithMultipleSevens() {
		for(int x = 1; x < 8; x++) {
			Board b = new Board(board);
			// 7
			// 2
			// 7
			// 7
			// 6
			// 3
			// 7 - - - - - -
			clearBoard(b);	
			b.setNumberTile(x, 1, 7, 0);	
			b.setNumberTile(x, 2, 2, 0);	
			b.setNumberTile(x, 3, 7, 0);
			b.setNumberTile(x, 4, 7, 0);
			b.setNumberTile(x, 5, 6, 0);
			b.setNumberTile(x, 6, 3, 0);
			b.setNumberTile(x, 7, 7, 0);		
			
			assertTrue(b.checkColumns());		
			assertTrue(b.getHitAt(x, 1));	
			assertTrue(b.getHitAt(x, 3));	
			assertTrue(b.getHitAt(x, 4));	
			assertTrue(b.getHitAt(x, 7));	
		}
	}

	@Test
	public void testCheckRowsFalseWithNoHits() {
		Board b = new Board(board);
		clearBoard(b);
		// -
		// -
		// -
		// -
		// -
		// -
		// 2 3 6 4 6 - -
		b.setNumberTile(1, 7, 2, 0);
		b.setNumberTile(2, 7, 3, 0);
		b.setNumberTile(3, 7, 6, 0);
		b.setNumberTile(4, 7, 4, 0);	
		b.setNumberTile(5, 7, 6, 0);		
		
		assertFalse(b.checkColumns());		
		assertFalse(b.getHitAt(1, 7));
		assertFalse(b.getHitAt(2, 7));
		assertFalse(b.getHitAt(3, 7));
		assertFalse(b.getHitAt(4, 7));
		assertFalse(b.getHitAt(5, 7));	
	}
	
	@Test
	public void testCheckRowsWith1One() {
		
		Board b = new Board(board);
		clearBoard(b);

		createColumns(1, 2, b);
		
		for(int y = 1; y < 8; y++) {
			// 2
			// 2
			// 2
			// 2
			// 2
			// 2
			// y - - - - - -
			b.setNumberTile(1, y, 1, 0);
			
			assertTrue(b.checkRows());
			assertTrue(b.getHitAt(1, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithTwos() {
		
		Board b = new Board(board);
		clearBoard(b);

		createColumns(1, 2, b);
		
		for(int y = 1; y < 8; y++) {
			// 1 1
			// 1 1
			// 1 1
			// 1 1
			// 1 1
			// 1 1
			// 1 y - - - - -
			b.setNumberTile(2, y, 2, 0);
			
			assertTrue(b.checkRows());
			assertTrue(b.getHitAt(2, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithTwosNotNextToEachOther() {
		
		Board b = new Board(board);
		clearBoard(b);

		createColumns(1, 2, b);
		
		for(int y = 1; y < 8; y++) {
			// 2 - 2
			// 2 - 2
			// 2 - 2
			// 2 - 2
			// 2 - 2
			// 2 - 2
			// 2 - y - - - -
			b.setNumberTile(3, y, 2, 0);
			
			assertFalse(b.checkRows());
			assertFalse(b.getHitAt(3, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithThrees() {
		
		Board b = new Board(board);
		clearBoard(b);

		createColumns(2, 2, b);
		
		for(int y = 1; y < 8; y++) {
			// 2 2 3
			// 2 2 3
			// 2 2 3
			// 2 2 3
			// 2 2 3
			// 2 2 3
			// 2 2 y - - - -
			b.setNumberTile(3, y, 3, 0);
			
			assertTrue(b.checkRows());
			assertTrue(b.getHitAt(3, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithThreesNotNextToEachOther() {
		
		Board b = new Board(board);
		clearBoard(b);
		
		createColumns(2, 3, b);
		
		for(int y = 1; y < 8; y++) {
			// 3 3 - 3
			// 3 3 - 3
			// 3 3 - 3
			// 3 3 - 3
			// 3 3 - 3
			// 3 3 - 3
			// 3 3 - y - - -
			b.setNumberTile(4, y, 3, 0);
			
			assertFalse(b.checkRows());
			assertFalse(b.getHitAt(4, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithFours() {
		
		Board b = new Board(board);
		clearBoard(b);

		createColumns(3, 2, b);
		
		for(int y = 1; y < 8; y++) {
			// 2 2 2 4
			// 2 2 2 4
			// 2 2 2 4
			// 2 2 2 4
			// 2 2 2 4
			// 2 2 2 4
			// 2 2 2 y - - -
			b.setNumberTile(4, y, 4, 0);
			
			assertTrue(b.checkRows());
			assertTrue(b.getHitAt(4, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithFoursNotNextToEachOther() {
		
		Board b = new Board(board);
		clearBoard(b);
		
		createColumns(2, 3, b);
		
		for(int y = 1; y < 8; y++) {
			// 2 2 2 - 4
			// 2 2 2 - 4
			// 2 2 2 - 4
			// 2 2 2 - 4
			// 2 2 2 - 4
			// 2 2 2 - 4
			// 2 2 2 - y - -
			b.setNumberTile(5, y, 4, 0);
			
			assertFalse(b.checkRows());
			assertFalse(b.getHitAt(5, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithFives() {
		
		Board b = new Board(board);
		clearBoard(b);

		createColumns(4, 2, b);
		
		for(int y = 1; y < 8; y++) {
			// 2 2 2 2 5
			// 2 2 2 2 5
			// 2 2 2 2 5
			// 2 2 2 2 5
			// 2 2 2 2 5
			// 2 2 2 2 5
			// 2 2 2 2 y - -
			b.setNumberTile(5, y, 5, 0);
			
			assertTrue(b.checkRows());
			assertTrue(b.getHitAt(5, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithFivesNotNextToEachOther() {
		
		Board b = new Board(board);
		clearBoard(b);
		
		createColumns(4, 2, b);
		
		for(int y = 1; y < 8; y++) {
			// 2 2 2 2 - 5
			// 2 2 2 2 - 5
			// 2 2 2 2 - 5
			// 2 2 2 2 - 5
			// 2 2 2 2 - 5
			// 2 2 2 2 - 5
			// 2 2 2 2 - y -
			b.setNumberTile(5, y, 3, 0);
			
			assertFalse(b.checkRows());
			assertFalse(b.getHitAt(5, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithSixes() {
		
		Board b = new Board(board);
		clearBoard(b);

		createColumns(5, 2, b);
		
		for(int y = 1; y < 8; y++) {
			// 2 2 2 2 2 6
			// 2 2 2 2 2 6
			// 2 2 2 2 2 6
			// 2 2 2 2 2 6
			// 2 2 2 2 2 6
			// 2 2 2 2 2 6
			// 2 2 2 2 2 y - -
			b.setNumberTile(6, y, 6, 0);
			
			assertTrue(b.checkRows());
			assertTrue(b.getHitAt(6, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithSixesNotNextToEachOther() {
		
		Board b = new Board(board);
		clearBoard(b);
		
		createColumns(4, 2, b);
		
		for(int y = 1; y < 8; y++) {
			// 2 2 2 2 2 - 6
			// 2 2 2 2 2 - 6
			// 2 2 2 2 2 - 6
			// 2 2 2 2 2 - 6
			// 2 2 2 2 2 - 6
			// 2 2 2 2 2 - 6
			// 2 2 2 2 2 - y 
			b.setNumberTile(7, y, 6, 0);
			
			assertFalse(b.checkRows());
			assertFalse(b.getHitAt(7, y));			
		}
	}
	
	@Test
	public void testCheckRowsWithSevens() {
		
		Board b = new Board(board);
		clearBoard(b);

		createColumns(6, 2, b);
		
		for(int y = 1; y < 8; y++) {
			// 2 2 2 2 2 2 7
			// 2 2 2 2 2 2 7
			// 2 2 2 2 2 2 7
			// 2 2 2 2 2 2 7
			// 2 2 2 2 2 2 7
			// 2 2 2 2 2 2 7
			// 2 2 2 2 2 2 y
			b.setNumberTile(7, y, 7, 0);
			
			assertTrue(b.checkRows());
			assertTrue(b.getHitAt(7, y));			
		}
	}
	
	@Test
	public void testExplodeHits() {
		
		Board b = new Board(board);
		clearBoard(b);
		b.setMultiplier(1);
		
		// 2?? 2?? 2?? 2?? 2?? 2?? 2??
		// 2?? 2?? 2?? 7   2?? 2?? 2??
		// 2?? 2?? 2?? 2?? 2?? 2?? 2??
		
		for(int x = 1; x < 8; x++) {
			for(int y = 5; y < 8; y++) {
				b.setNumberTile(x, y, 2, 2);
			}
		}

		int unknownUp2    = b.getNumberTileAtLocation(4, 5).getUnknown();
		int unknownDown2  = b.getNumberTileAtLocation(4, 7).getUnknown();
		int unknownLeft2  = b.getNumberTileAtLocation(3, 6).getUnknown();
		int unknownRight2 = b.getNumberTileAtLocation(5, 6).getUnknown();
		
		b.setNumberTile(4, 6, 7, 0);
		b.checkColumns();
		b.checkRows();
		
		assertNotNull(b.getNumberTileAtLocation(4, 6));
		assertTrue(b.getHitAt(4, 6));
		
		b.explodeHits();
		
		int unknownUp1    = b.getNumberTileAtLocation(4, 5).getUnknown();
		int unknownDown1  = b.getNumberTileAtLocation(4, 7).getUnknown();
		int unknownLeft1  = b.getNumberTileAtLocation(3, 6).getUnknown();
		int unknownRight1 = b.getNumberTileAtLocation(5, 6).getUnknown();
		
		assertNull(b.getNumberTileAtLocation(4, 6));
		assertFalse(b.getHitAt(4, 6));
		assertEquals(unknownUp1, unknownUp2 - 1);
		assertEquals(unknownDown1, unknownDown2 - 1);
		assertEquals(unknownLeft1, unknownLeft2 - 1);
		assertEquals(unknownRight1, unknownRight2 - 1);		
	}
	
	@Test
	public void testScoreIncreasesBy7WhenOneTileExplodes() {
		
		Board b = new Board(board);
		clearBoard(b);
		
		int score = b.getScore();

		b.setNumberTile(3, 7, 1, 0);
		b.setNumberTile(1, 7, 2, 0);
		b.burnDown();
		
		assertEquals(score + 7, b.getScore());
	}
	
	@Test
	public void testScoreIncreasesAccordingToMultiplierWhenTilesExplode() {
		
		// 7
		// 39
		// 109
		// 224
		
		Board b = new Board(board);
		clearBoard(b);
		
		int score = b.getScore();

		b.setNumberTile(1, 7, 3, 0);
		b.setNumberTile(2, 7, 2, 0);
		b.setNumberTile(3, 6, 3, 0);
		b.setNumberTile(3, 7, 1, 0);
		b.burnDown();
		
		assertEquals(score + 7 + 39 + 109, b.getScore());
	}
	
	@Test
	public void testScorePlus70000WhenBoardCleared() {
		
		Board b = new Board(board);
		clearBoard(b);
		
		int score = b.getScore();

		b.setNumberTile(3, 7, 1, 0);
		b.burnDown();
		
		assertEquals(score + 70007, b.getScore());
	}
	
	@Test
	public void testGridIsEmpty() {
		
		Board b = new Board(board);
		clearBoard(b);
		
		assertTrue(b.gridIsEmpty());
	}
	
	@Test
	public void testPieceHasBeenReleased() {
				
		Board b = new Board(board);
		int turnsLeft = b.getTurnsLeft();
		int score = b.getScore();
		
		assertEquals(30, turnsLeft);
		assertEquals(0, score);
		assertNotNull(b.getCurrentTile());
		assertEquals(2, b.getCurrentTile().getValue());
		assertEquals(0, b.getCurrentTile().getUnknown());

		// -   -   -   2   -   -   -
		// -
		// -
		// -
		// -
		// -
		// 6?? 4?? 5?? 7?? 5?? 1?? 3??
		b.getCurrentTile().setX(4);
		b.pieceHasBeenReleased();
		assertEquals(b.getTurnsLeft(), turnsLeft - 1);
		assertEquals(b.getScore(), score + 7);
		assertNotNull(b.getCurrentTile());
		assertEquals(1, b.getCurrentTile().getValue());
		assertEquals(0, b.getCurrentTile().getUnknown());
		// -   -   -   1   -   -   -
		// -
		// -
		// -
		// -
		// -
		// 6?? 4?? 5?? 7? 5?? 1?? 3??
		b.getCurrentTile().setX(4);
		b.pieceHasBeenReleased();
		assertEquals(b.getTurnsLeft(), turnsLeft - 2);
		assertEquals(b.getScore(), score + 7 + 7 + 39);
		assertNotNull(b.getCurrentTile());
		// -   -   -   -   -   -   -
		// -
		// -
		// -
		// -
		// -
		// 6?? 4?? 5?? -  5?? 1?? 3??
	}
	
	private void createColumns(int num, int value, Board b) {
		for(int x = 1; x <= num; x++) {
			for(int y = 1; y < 8; y++) {
				b.setNumberTile(x, y, value, 0);
			}
		}
	}
	
	private void clearBoard(Board b) {
		for(int pos = 0; pos < b.getGrid().length; pos++) {
			b.getGrid()[pos] = null;
		}
		/*
		for(int y = 1; y < 8; y++)
			for(int x = 1; x < 8; x++)
				b.getGrid().set(b.getNumberTileLocation(x, y), null);*/
	}
}
