import java.util.ArrayList;


public class GenomicNode implements Comparable<GenomicNode>{

	private GenomicCoordinate start, end;
	private ArrayList<Event> events;
	
	public GenomicNode(GenomicCoordinate coord){
		this.start = coord;
		this.end  = coord;
	}
	
	public GenomicNode(GenomicCoordinate coord, Event e){
		this.start = coord;
		this.end  = coord;
		this.events = new ArrayList<Event>();
		events.add(e);
	}

	public GenomicCoordinate getStart() {
		return start;
	}

	public GenomicCoordinate getEnd() {
		return end;
	}

	public ArrayList<Event> getEvents() {
		return events;
	}

	@Override
	public int compareTo(GenomicNode other) {
		return this.start.compareTo(other.start);
	}
	
	/*
	 * Assimilates the members and end coordinate of other node
	 * Assumes that the other node is downstream of this (on same chr)
	 * Also assuems that other node has only one event
	 */
	public void mergeWithNode(GenomicNode other){
		if(!this.start.onSameChromosome(other.start) || this.end.compareTo(other.start) >0 || other.events.size()>1){
			System.err.println("Assumptions violated in mergeWithNode!");
		}
		//change end coordinate of node interval
		this.end = other.end;
		//add event if necessary
		Event e = other.events.get(0);
		if(!events.contains(e)){
			events.add(e);
		}
		//adjust pointers to new node where applicable
		if(e.getC1() == other.start){
			e.setNode(this, true);
		}
		if(e.getC2() == other.start){
			e.setNode(this, false);
		}
	}

}
