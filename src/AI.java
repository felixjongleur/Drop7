
public class AI {

	Grid backUpGrid;

	Tree<Triplet> moves;

	int maxDepth = 7;
	static int boardNumber = 0;

	static StopWatch a1 = new StopWatch();
	static StopWatch a2 = new StopWatch();
	static StopWatch a3 = new StopWatch();
	static StopWatch a4 = new StopWatch();
	static StopWatch a5 = new StopWatch();
	static StopWatch a6 = new StopWatch();
	static StopWatch a7 = new StopWatch();
	static StopWatch a8 = new StopWatch();
	
	static long t1, t2, t3, t4, t5, t6, t7, t8;
	static long n1 = 0, n2 = 0, n3 = 0, n4 = 0, n5 = 1, n6 = 1, n7 = 1, n8 = 1;

	public AI(Grid currentGrid) {
		this.backUpGrid = new Grid(currentGrid);

		this.moves = new Tree<Triplet>();

		this.moves.setRootElement(new Node<Triplet>(
				new Triplet(0, 0, backUpGrid)));
	}
	
	public int getBestMove() {
		return getBestMove(moves.getRootElement());
	}

	public int getBestMove(Node<Triplet> root) {
		a1.start();
		int bestPos = 0;
		int bestScore = 0;		
		for (int pos = 1; pos < 8; pos++) {
			if(root.data.grid.getNumberTileAtLocation(pos, 1) == null) {
				Grid currentGrid = new Grid(root.data.grid);
				currentGrid.currentTile.x = pos;
				if (currentGrid.pieceHasBeenReleased()) {
					root.addChild(new Node<Triplet>(new Triplet(pos, currentGrid.score, currentGrid)));
				}
			}
		}

		for (Node<Triplet> triplet : root.getChildren()) {
			int score = MaxScore(triplet, 1);
			if(MainWindow.debug)
			System.out.println("POS => " + triplet.getData().pos + " SCORE => " + score);
			if(score > bestScore) {
				bestScore = score;
				bestPos = triplet.getData().pos;
			}
		}
		n1++;
		a1.stop();
		t1 += a1.getElapsedTime();
	//	System.out.println("T1 = " + t1);
		if(MainWindow.debug) {
		/*	System.out.println("GRID TIME = " + t1);
	
			System.out.println("G1 = " + Grid.t1);
			System.out.println("G2 = " + Grid.t2);
			System.out.println("G3 = " + Grid.t3);
			System.out.println("G4 = " + Grid.t4);
			System.out.println("G5 = " + Grid.t5);
			System.out.println("Number of Times = " + Grid.numberOfTimes);
	*/
			System.out.println("TOTAL TIME = " + t1 + " / " + n1 + " =\t" +(t1/n1));
			System.out.println("GRID() CALLED = " + t2 + " / " + n2 + " =\t" +(t2/n2));
			System.out.println("NEW NUMBERTILE = " + t3 + " / " + n3 + " =\t" +(t3/n3));
			System.out.println("A4 = " + t4 + " / " + n4 + " =\t" +(t4/n1));
			System.out.println("A5 = " + t5 + " / " + n5 + " =\t" +(t5/n1));
			System.out.println("A6 = " + t6 + " / " + n6 + " =\t" +(t6/n1));
			System.out.println("A7 = " + t7 + " / " + n7 + " =\t" +(t7/n1));
			System.out.println("A8 = " + t8 + " / " + n8 + " =\t" +(t8/n1));
			
			t3 = 0;
			n3 = 0;
			Grid.t1 = 0;
			Grid.t2 = 0;
			Grid.t3 = 0;
			Grid.t4 = 0;
			Grid.t5 = 0;
		}
		
		return bestPos;
	}
	
	private int MaxScore(Node<Triplet> root, int depth) {
		int maxScore = 0;
		if(depth >= maxDepth) {
			return root.getData().score;
		}
		for (int pos = 1; pos < 8; pos++) {
			if(root.data.grid.getNumberTileAtLocation(pos, 1) == null) {
		//		a2.start();
				Grid currentGrid = new Grid(root.data.grid);
		//		n2++;
		//		a2.stop();
		//		t2 += a2.getElapsedTime();
				
				currentGrid.currentTile.x = pos;
				
				if (currentGrid.pieceHasBeenReleased()) {
					root.addChild(new Node<Triplet>(new Triplet(pos, currentGrid.score, currentGrid)));
				}
			}
		}
		
		for (Node<Triplet> triplet : root.getChildren()) {
			int score = MaxScore(triplet, depth + 1);						
			if(score > maxScore)
				maxScore = score;
		}
				
		if(root.getNumberOfChildren() > 0)
			root.children.clear();		
		
		return maxScore;
	}
}
