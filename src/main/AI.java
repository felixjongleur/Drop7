package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class AI {

	private Board backUpGrid;

	private Tree<Triplet> moves;
	private static HashMap<Integer, Integer> posAndScore;

	static private int numOfThreads = 0;
	static int getResultsFromThread = 0;

	StopWatch a1 = new StopWatch();
	
	static int counter = 0;
	static long t1 = 0;

	public AI(Board currentGrid) {
		posAndScore = new HashMap<Integer, Integer>();
		this.backUpGrid = new Board(currentGrid);
		this.moves = new Tree<Triplet>();
		this.moves.setRootElement(new Node<Triplet>(new Triplet(0, 0,
				backUpGrid)));
	}

	public int getBestMove() {
		return getBestMove(moves.getRootElement());
	}

	private int getBestMove(Node<Triplet> root) {
		a1.start();
		int bestPos = 0;
		int bestScore = 0;
		for (int pos = 1; pos < 8; pos++) {
			if (root.data.grid.getNumberTileAtLocation(pos, 1) == null) {
				Board currentGrid = new Board(root.data.grid);
				currentGrid.getCurrentTile().setX(pos);
				if (currentGrid.pieceHasBeenReleased()) {
					root.addChild(new Node<Triplet>(new Triplet(pos,
							currentGrid.getScore(), currentGrid)));
				}
			}
		}

		ArrayList<MaxScoreThread> threads = new ArrayList<MaxScoreThread>();

		// THREADING WOULD GO IN HERE?
		for (Node<Triplet> triplet : root.getChildren()) {
			MaxScoreThread mst = new MaxScoreThread(triplet, MainWindow.maxDepth);
			threads.add(mst);
			numOfThreads++;
			mst.start();
		}

		boolean done = false;

		while (!done) {
			done = true;
			for (MaxScoreThread thread : threads) {
				if (!thread.getIsDone())
					done = false;
			}
		}

		for (Entry<Integer, Integer> entry : posAndScore.entrySet()) {
			if(MainWindow.debug)
			System.out.println("POS[" + entry.getKey() + "] => " + entry.getValue());
			if (entry.getValue() > bestScore) {
				bestScore = entry.getValue();
				bestPos = entry.getKey();
			}
		}
		a1.stop();
		t1 += a1.getElapsedTime();
		counter++;
		if(MainWindow.debug) {
			System.out.println("T1 = " + t1 + " / " + counter + " = " + (t1 / counter));
			System.out.println("T1 = " + Board.t1 + " / " + Board.n1 + " = " + (Board.t1 / Board.n1));
			System.out.println("T2 = " + Board.t2 + " / " + Board.n2 + " = " + (Board.t2 / Board.n2));
			System.out.println("T3 = " + Board.t3 + " / " + Board.n3 + " = " + (Board.t3 / Board.n3));
			System.out.println("T4 = " + Board.t4 + " / " + Board.n4 + " = " + (Board.t4 / Board.n4));
			System.out.println("T5 = " + Board.t5);
		}
		return bestPos;
	}

	static synchronized void getResultsFromThreads(int position, int score) {
		getResultsFromThread++;
		numOfThreads--;
		posAndScore.put(position, score);
	}
}
