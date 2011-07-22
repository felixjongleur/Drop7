
public class MaxScoreThread extends Thread {

	private int maxDepth;
	private int position;
	private int maxScore;
	private boolean isDone;
	private Node<Triplet> root;
	
	public MaxScoreThread(Node<Triplet> root, int maxDepth) {
		this.maxDepth = maxDepth;
		this.root = root;
		this.position = root.getData().pos;
	}
	
	public void run() {
		maxScore = maxScore(root, 1);
		AI.getResultsFromThreads(position, maxScore);
		isDone = true;
	}
	
	public boolean getIsDone() {
		return isDone;
	}
	
	private int maxScore(Node<Triplet> root, int depth) {
//		root.getData().grid.printOutBoard();
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
			int score = maxScore(triplet, depth + 1);						
			if(score > maxScore)
				maxScore = score;
		}
				
		if(root.getNumberOfChildren() > 0)
			root.children.clear();		
		
		return maxScore;
	}
}
