import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
					lastNode = currentNode;
				}
			}
			System.out.println("Nodes Merged: "+nodesMerged);
		}
		
		//iterate through node sets again, and genotype events
		//...
	}

}
