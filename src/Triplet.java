public class Triplet {
    Integer pos;
    Integer score;
    Grid grid;

    public Triplet(Integer pos, Integer score, Grid grid) {
        this.pos = pos;
        this.score = score;
        this.grid = grid;
    }
    
    public String toString() {
    	return "["+pos+" , "+score+" , "+grid+"]";
    }
}