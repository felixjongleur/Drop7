
public class AI {

	private Grid backUpGrid;

	private Tree<Triplet> moves;

	private int maxDepth = 7;
	
	public AI(Grid currentGrid) {
		this.backUpGrid = new Grid(currentGrid);
		this.moves = new Tree<Triplet>();
		this.moves.setRootElement(new Node<Triplet>(new Triplet(0, 0, backUpGrid)));
	}
	
	public int getBestMove() {
		return getBestMove(moves.getRootElement());
	}

	private int getBestMove(Node<Triplet> root) {
		int bestPos = 0;
		int bestScore = 0;		
		for (int pos = 1; pos < 8; pos++) {
			if(root.data.grid.getNumberTileAtLocation(pos, 1) == null) {
				Grid currentGrid = new Grid(root.data.grid);
				currentGrid.currentTile.x = pos;
				currentGrid.pieceHasBeenReleased();
				root.addChild(new Node<Triplet>(new Triplet(pos, currentGrid.score, currentGrid)));
			}
		}

		// THREADING WOULD GO IN HERE?
		for (Node<Triplet> triplet : root.getChildren()) {
			int score = MaxScore(triplet, 1);
			if(score > bestScore) {
				bestScore = score;
				bestPos = triplet.getData().pos;
			}
		}		
		return bestPos;
	}
	
	// THIS METHOD MOVED TO THREAD?
	private int MaxScore(Node<Triplet> root, int depth) {
		if(depth >= maxDepth) {
			return root.getData().score;
		}
		int maxScore = 0;
		for (int pos = 1; pos < 8; pos++) {
			if(root.data.grid.getNumberTileAtLocation(pos, 1) == null) {
				Grid currentGrid = new Grid(root.data.grid);
				
				currentGrid.currentTile.x = pos;
				currentGrid.pieceHasBeenReleased();
				root.addChild(new Node<Triplet>(new Triplet(pos, currentGrid.score, currentGrid)));
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
