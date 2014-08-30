import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;





public class Genotyper {
	
	private static void addEventToNodeList(Event e, Hashtable<String, TreeSet<GenomicNode>> genomicNodes, boolean useFirstCoordinate){
		GenomicCoordinate coordinate = (useFirstCoordinate? e.getC1() : e.getC2());
		TreeSet<GenomicNode> nodeSet;
		if(! genomicNodes.containsKey(coordinate.getChr())){
			nodeSet = new TreeSet<GenomicNode>();
			genomicNodes.put(coordinate.getChr(), nodeSet);
		} else {
			nodeSet = genomicNodes.get(coordinate.getChr()) ;
		}
		GenomicNode newNode = new GenomicNode(coordinate, e);
		e.setNode(newNode, useFirstCoordinate);
		nodeSet.add(newNode);

	}
	
	
	
	enum SV_ALGORITHM {SOCRATES, DELLY};
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if(args.length < 2){
			System.err.println("Usage: <list of breakpoints> <algorithm (Socrates/Delly)>");
			System.exit(0);
		}
		
		
		/*
		 * parse the algorithm parameter from command line
		 */	
		SV_ALGORITHM algorithm = SV_ALGORITHM.SOCRATES;
		
		try{
			algorithm = SV_ALGORITHM.valueOf(args[1].toUpperCase());
			System.out.println("Interpreting input as "+algorithm+" breakpoints");
		} catch (IllegalArgumentException e){
			System.out.println("Unknown SV algorithm identifier.");
			System.exit(1);
		}
		
		
		/*
		 * parse the entire input file and collect all events in list
		 */
		ArrayList<Event> allEvents = new ArrayList<Event>();
		
		BufferedReader input = new BufferedReader(new FileReader(args[0]));
		String line;
		while ((line = input.readLine()) != null){
			//TODO: make # check algorithm specific?
			if(line.startsWith("#"))
				continue;
			Event e;
			switch(algorithm){
			case SOCRATES: 	e = Event.createNewEventFromSocratesOutput(line); 	break;
			case DELLY: 	e = Event.createNewEventFromDellyOutput(line); 		break;
			default:		e = null;
			}
			allEvents.add(e);
		}
		System.out.println("Total events: "+allEvents.size());
		input.close();
		
		
		/*
		 * Create nodes data structure that combines close events into the same 
		 * genomic node
		 */
		Hashtable<String, TreeSet<GenomicNode>> genomicNodes = new Hashtable<String, TreeSet<GenomicNode>>();
		
		//parse all events and create new nodes 
		for (Event e: allEvents){
			addEventToNodeList(e, genomicNodes, true);
			addEventToNodeList(e, genomicNodes, false);
		}
		
		//establish distance for "close" events according to algorithm
		int maxDistanceForNodeMerge = 0;
		switch(algorithm){
		case SOCRATES: 	maxDistanceForNodeMerge = 15; break;
		case DELLY:		maxDistanceForNodeMerge = 150; break;
		}
		
		//iterate through node sets and merge nodes where necessary
		//also checks each node for redundant members
		//TODO: handle redundant members
		for(Entry<String, TreeSet<GenomicNode>> tableEntry: genomicNodes.entrySet()) {
			GenomicNode lastNode = null, currentNode = null;
			Iterator<GenomicNode> iterator = tableEntry.getValue().iterator();
			int nodesMerged = 0;
			while(iterator.hasNext()){
				currentNode = iterator.next();
				if(lastNode != null && currentNode.getStart().distanceTo(lastNode.getEnd()) < maxDistanceForNodeMerge){
					lastNode.mergeWithNode(currentNode);
					iterator.remove();
					nodesMerged++;
				} else {
					if(lastNode != null)
						lastNode.checkForRedundantEvents();
					lastNode = currentNode;
				}
			}
			if(lastNode != null)
				lastNode.checkForRedundantEvents();
			System.out.println("Nodes Merged: "+nodesMerged);
		}
		
		//iterate through node sets again, and genotype events
		for(Entry<String, TreeSet<GenomicNode>> tableEntry: genomicNodes.entrySet()) {
			for(GenomicNode currentNode: tableEntry.getValue()){
				//iterate through all event-event pairing in this node and assess for complex events
				Event e1, e2;
				HashSet<Event> removeEvents = new HashSet<Event>();
				HashSet<ComplexEvent> newComplexEvents = new HashSet<ComplexEvent>();
				ComplexEvent newComplexEvent = null;
				for(int i=0; i<currentNode.getEvents().size(); i++){
					e1 = currentNode.getEvents().get(i);
					for(int j=0; j<currentNode.getEvents().size(); j++){
						e2 = currentNode.getEvents().get(j);
						if(e1 == e2 || removeEvents.contains(e2) || removeEvents.contains(e1))
							continue;
						switch(e1.getType()){
							//inversions
							case INV1: {
								if(e2.getType() == EVENT_TYPE.INV2 && Event.sameNodeSets(e1, e2)){
									//System.out.println("Complex inversion between "+e1+" and "+e2);
									GenomicCoordinate invstart = (e1.getC1().compareTo(e1.getC2()) < 0? e1.getC1() : e1.getC2());
									GenomicCoordinate invend   = (e2.getC2().compareTo(e2.getC1()) < 0? e2.getC1() : e2.getC2());
									newComplexEvent = new ComplexEvent(invstart, invend, EVENT_TYPE.COMPLEX_INVERSION, (new Event[] {e1, e2}), currentNode);
								}
								else {
									//unknown pairing
								}
								break;
							}
							//duplications and translocations
							case DEL: {
								if(e2.getType() == EVENT_TYPE.TAN){
									GenomicNode other1 = e1.otherNode(currentNode), other2 = e2.otherNode(currentNode);
									if(other1.compareTo(other2) < 0 && currentNode.compareTo(other1) < 0
											|| other2.compareTo(other1) < 0 && other1.compareTo(currentNode) < 0){
										if(other1.existsDeletionEventTo(other2) != null){
											//System.out.println("Translocation between "+e1+ " and "+ e2);
											newComplexEvent = new ComplexEvent(null, null, EVENT_TYPE.COMPLEX_TRANSLOCATION, (new Event[] {e1, e2, other1.existsDeletionEventTo(other2)}), currentNode);
										}
										else {
											//System.out.println("Duplication between "+e1+ " and "+ e2);
											newComplexEvent = new ComplexEvent(null, null, EVENT_TYPE.COMPLEX_DUPLICATION, (new Event[] {e1, e2}), currentNode);
										}
									}
								}
								break;
							}
							case INV2: //handled as INV1 above
							
							default: //don't even attempt other types
						}
						//check if a new complex event has been generated
						if(newComplexEvent != null){
							//-> add events to cleanup and break current loop
							newComplexEvents.add(newComplexEvent);
							for(Event e: newComplexEvent.getEventsInvolvedInComplexEvent()){
								removeEvents.add(e);
							}
							newComplexEvent = null;
							break; //break the for-j loop, as this guy is already paired
						}
					}
				}
				//all event pairings have been investigated 
				//-> clean up some stuff by removing events and adding the new complex ones.
				for(Event e: removeEvents){
					e.getNode(true).getEvents().remove(e);
					e.getNode(false).getEvents().remove(e);
				}
				currentNode.getEvents().addAll(newComplexEvents);
			}
		}
		
		//while we're at it: let's run through the nodes again!
		//this time for output
		for(Entry<String, TreeSet<GenomicNode>> tableEntry: genomicNodes.entrySet()) {
			for(GenomicNode currentNode: tableEntry.getValue()){
				if(currentNode.getEvents().size() > 1){
					System.out.println("Node might be shifty: "+currentNode.getEvents().size()+" members!");
				}
				HashSet<Event> skipEvents = new HashSet<Event>();
				for(Event e: currentNode.getEvents()){
					if(skipEvents.contains(e))
						continue;
					System.out.println(e);
					if(e.otherNode(currentNode) == currentNode){
						skipEvents.add(e);
					} else {
						e.otherNode(currentNode).getEvents().remove(e);
					}
				}
			}
		}
	}

}
