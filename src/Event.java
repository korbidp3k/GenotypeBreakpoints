
class Coordinate implements Comparable<Coordinate>{
	private String chr;
	private int pos;
	public Coordinate(String chr, int pos){
		this.chr = chr;
		this.pos = pos;
	}
	@Override
	public int compareTo(Coordinate other) {
		if(other.chr.equals(this.chr)){
			if(this.pos < other.pos)
				return -1;
			if(this.pos > other.pos)
				return 1;
			return 0;
		}
		return 0;
	}
}

public class Event {

	private Coordinate c1, c2;

}
