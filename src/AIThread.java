// Class for running the AIEngine in a separate thread
// so as not to interfere with the rest of the GUI in the GameInterface

public class AIThread extends Thread {
	
	Grid grid;
	boolean done;
	int bestPos;
	
    public AIThread(Grid grid) {
		this.grid = grid;
		this.bestPos = 0;
		this.done = false;
    }

    public void run() {
    	AI ai = new AI(grid);

		bestPos = ai.getBestMove();
		System.out.println(bestPos);
		done = true;
	//	System.out.println(bestPos);
    }
    
    public int getBestPos() {
    	return bestPos;
    }
}
