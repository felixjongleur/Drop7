package main;

public class MaxScoreThread extends Thread {

	private int maxDepth;
	private int position;
	private int maxScore;
	private boolean isDone;
	private Node<Triplet> root;

	StopWatch a1 = new StopWatch();
	StopWatch a2 = new StopWatch();
	StopWatch a3 = new StopWatch();
	StopWatch a4 = new StopWatch();
	StopWatch a5 = new StopWatch();
	StopWatch a6 = new StopWatch();
	
	static long t1, t2, t3, t4, t5, t6;
	
	public MaxScoreThread(Node<Triplet> root, int maxDepth) {
		this.maxDepth = maxDepth;
		this.root = root;
		this.position = root.getData().getPos();
	}

	@Override
	public void run() {
		maxScore = maxScore(root, 1);
		AI.getResultsFromThreads(position, maxScore);
		isDone = true;
	}

	public boolean getIsDone() {
		return isDone;
	}

	private int maxScore(Node<Triplet> node, int depth) {
		if (depth >= maxDepth) {
			return node.getData().getScore();
		}
		
		int maxScore = 0;		
		
		a1.start();
		for (int pos = 1; pos < 8; pos++) {
			if (node.getData().getGrid().getNumberTileAtLocation(pos, 1) == null) {
				Board currentGrid = new Board(node.getData().getGrid());
				currentGrid.getDropSequence().add(pos);
				
				currentGrid.getCurrentTile().setX(pos);
				if (currentGrid.pieceHasBeenReleased()) {
					node.addChild(new Node<Triplet>(new Triplet(pos,
							currentGrid.getScore(), currentGrid)));
				}
			}
		}
		a1.stop();
		t1 += a1.getElapsedTime();

		for (Node<Triplet> triplet : node.getChildren()) {
			int score = maxScore(triplet, depth + 1);
			if (score > maxScore)
				maxScore = score;
		}

		if (node.getNumberOfChildren() > 0)
			node.getChildren().clear();
		
		return maxScore;
	}
}
