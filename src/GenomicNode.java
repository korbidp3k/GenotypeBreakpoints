import java.util.ArrayList;


public class GenomicNode implements Comparable<GenomicNode>{

	private GenomicCoordinate start, end;
	private ArrayList<Event> events;
	
	public GenomicNode(GenomicCoordinate coord){
		this.start = coord;
		this.end  = coord;
	}

	@Override
	public int compareTo(GenomicNode other) {
		return this.start.compareTo(other.start);
	}

}
