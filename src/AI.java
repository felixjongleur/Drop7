import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


public class AI {

	private Grid backUpGrid;

	private Tree<Triplet> moves;
	private static HashMap<Integer, Integer> posAndScore;

	private int maxDepth = 3;
	
	public AI(Grid currentGrid) {
		posAndScore = new HashMap<Integer, Integer>();
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
		
		ArrayList<MaxScoreThread> threads = new ArrayList<MaxScoreThread>();
		
		// THREADING WOULD GO IN HERE?
		for (Node<Triplet> triplet : root.getChildren()) {
			MaxScoreThread mst = new MaxScoreThread(triplet, maxDepth);
			threads.add(mst);
			mst.start();
		}
		
		boolean done = false;		
		
		while(!done) {
			done = true;
			for(MaxScoreThread thread : threads) {
				if(!thread.getIsDone())
					done = false;
			}
		}
		
		for(Entry<Integer, Integer> entry : posAndScore.entrySet()) {
			System.out.println("POS["+entry.getKey()+"] => "+entry.getValue());
			if(entry.getValue() > bestScore) {
				bestScore = entry.getValue();
				bestPos = entry.getKey();
			}
		}
		
		return bestPos;
	}
		
	static synchronized void getResultsFromThreads(int position, int score) {
		posAndScore.put(position, score);
	}
}
