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
		assertEquals(true, Board.getRows().size() > 6);
		assertNotNull(Board.getAllNumberTiles());
		assertEquals(true, Board.getAllNumberTiles().size() == 1029);
		
		board.loadSequence("sequence9.txt");
		assertNotNull(Board.getSequence());
	}
	
	@Test
	public void testBoard() throws FileNotFoundException {		
		assertEquals(49, board.getGrid().size());
		
		for(int pos = 0; pos < 42; pos++) {
			assertNull(board.getGrid().get(pos));
		}
		
		for(int pos = 42; pos < 49; pos++) {
			assertNotNull(board.getGrid().get(pos));
		}
		
		for(int pos = 0; pos < 49; pos++) {
			assertFalse(board.getHits().get(pos));
		}
	}
	
	@Test
	public void testBoardCopyConstructor() {
		Board b = new Board(board);		
		assertNotSame(b, board);
		assertNotSame(b.getGrid(), board.getGrid());
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
			int pos = b.getNumberTileLocation(1, y);
			b.getGrid().set(pos, b.getNumberTile(1, y, 1, 2));
		}
		//System.out.println(board);
		assertFalse(b.levelUp());
	}
	
	@Test
	public void testLevelUpMoveUpOneRow() {
		Board b = new Board(board);
		List<NumberTile> firstRow = new ArrayList<NumberTile>();
		
		for(int x = 1; x < 8; x++) {
			firstRow.add(b.getNumberTileAtLocation(x, 7));
		}
		
		b.levelUp();
		
		List<NumberTile> secondRow = new ArrayList<NumberTile>();
		
		for(int x = 1; x < 8; x++) {
			secondRow.add(b.getNumberTileAtLocation(x, 6));
		}
		
		assertTrue(firstRow.size() == secondRow.size());
		
		for(int pos = 0; pos < firstRow.size(); pos++) {
			assertEquals(firstRow.get(pos).getX(), secondRow.get(pos).getX());
			assertEquals(firstRow.get(pos).getY() - 1, secondRow.get(pos).getY());
			assertEquals(firstRow.get(pos).getValue(), secondRow.get(pos).getValue());
			assertEquals(firstRow.get(pos).getUnknown(), secondRow.get(pos).getUnknown());
		}
	}
}
