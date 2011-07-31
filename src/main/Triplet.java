package main;

public class Triplet {
	private Integer pos;
	private Integer score;
	private Board grid;

	public Triplet(Integer pos, Integer score, Board grid) {
		this.pos = pos;
		this.score = score;
		this.grid = grid;
	}

	public int getPos() {
		return pos;
	}
	
	public int getScore() {
		return score;
	}
	
	public Board getGrid() {
		return grid;
	}
	
	@Override
	public String toString() {
		return "[" + pos + " , " + score + " , " + grid.getCurrentTile()
				+ " , " + grid + "]";
	}
}