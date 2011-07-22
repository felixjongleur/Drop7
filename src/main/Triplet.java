package main;

public class Triplet {
	Integer pos;
	Integer score;
	Board grid;

	public Triplet(Integer pos, Integer score, Board grid) {
		this.pos = pos;
		this.score = score;
		this.grid = grid;
	}

	@Override
	public String toString() {
		return "[" + pos + " , " + score + " , " + grid.getCurrentTile()
				+ " , " + grid + "]";
	}
}