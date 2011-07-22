
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
		System.out.println("MAIN POS " + root.getData().pos + "\n" + root.toString());
		isDone = true;
	}
	
	public boolean getIsDone() {
		return isDone;
	}
	
	private int maxScore(Node<Triplet> node, int depth) {
//		root.getData().grid.printOutBoard();
		if(depth >= maxDepth) {
			return node.getData().score;
		}
		int maxScore = 0;
		for (int pos = 1; pos < 8; pos++) {
			if(node.data.grid.getNumberTileAtLocation(pos, 1) == null) {
				Grid currentGrid = new Grid(node.data.grid);
				
				currentGrid.currentTile.x = pos;
				currentGrid.pieceHasBeenReleased();
				node.addChild(new Node<Triplet>(new Triplet(pos, currentGrid.score, currentGrid)));
			}
		}
		
		for (Node<Triplet> triplet : node.getChildren()) {
			int score = maxScore(triplet, depth + 1);						
			if(score > maxScore)
				maxScore = score;
		}
				
	//	if(node.getNumberOfChildren() > 0)
	//		node.children.clear();		
		
		return maxScore;
	}
}
