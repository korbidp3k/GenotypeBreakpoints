import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;




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
		//this compare method never spits out equal, so that the adding to TreeSets 
		//would not ignore them.
		int compare = this.start.compareTo(other.start);
		if(compare == 0)
			return -1;
		return compare;
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

	public void checkForRedundantEvents(){
		Event e1, e2;
		HashSet<Event> redundantEvents = new HashSet<Event>();
		for(int i=0; i<events.size(); i++){
			e1 = events.get(i);
			for(int j=i+1; j< events.size(); j++){
				e2=events.get(j);
				if(Event.sameNodeSets(e1,e2) && e1.getType() == e2.getType()){
					//System.out.println("Redundant events identified: "+e1+" "+e2);
					redundantEvents.add(e2);
				}
			}
		}
		this.events.removeAll(redundantEvents);
	}
	
	public Event existsDeletionEventTo(GenomicNode other){
		for(Event e: this.events){
			if(e.otherNode(this) == other && e.getType()==EVENT_TYPE.DEL)
				return e;
		}
		return null;
	}
}
