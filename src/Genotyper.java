import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.sun.awt.AWTUtilities.Translucency;





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
	
	private static String generateNodeLabel(GenomicNode n){
		return n.getStart().getChr()+"_"+n.getStart().getPos()+"_"+n.getEnd().getPos();
	}
	private static void graphVisualisation(String outputFilename, Hashtable<String, TreeSet<GenomicNode>> genomicNodes) throws IOException{
		HashSet<Event> eventsWritten = new HashSet<Event>();
		FileWriter output = new FileWriter(outputFilename);
		output.write("digraph g {\n");
		for(Entry<String, TreeSet<GenomicNode>> tableEntry: genomicNodes.entrySet()) {
			if(!tableEntry.getKey().equals("chr12"))
				continue;
			output.write("{rank=same; ");
			for(GenomicNode n : tableEntry.getValue()){
				output.write(generateNodeLabel(n)+"; ");
			}
			output.write("}\n");
			for(GenomicNode n : tableEntry.getValue()){
				for(Event e: n.getEvents()) {
					if(eventsWritten.contains(e))
						continue;
					else
						eventsWritten.add(e);
					String l1 = generateNodeLabel(e.getNode(true)), l2 = generateNodeLabel(e.getNode(false));
					switch(e.getType()){
					case DEL: output.write(l1+"->"+l2+"[label=\"DEL\"];\n"); break;
					case TAN: output.write(l1+"->"+l2+"[label=\"TAN\" arrowtail=normal arrowhead=none dir=both];\n"); break;
					case COMPLEX_INVERSION: 
						Event ee = ((ComplexEvent)e).getEventsInvolvedInComplexEvent()[0];
						l1 = generateNodeLabel(ee.getNode(true));
						l2 = generateNodeLabel(ee.getNode(false));
						//if (l1.equals("chr12_28204143_28204160")){
						//if (l1.equals("chr12_24118329_24118583")){
							System.out.println("COMPLEX_INV:");
							System.out.println("l1:\t"+generateNodeLabel(ee.getNode(true))+"\t");
							System.out.println("l2:\t"+generateNodeLabel(ee.getNode(false))+"\t");
						//}
						output.write(l1+"->"+l2+"[label=\"COMPLEX_INV\"  dir=both];\n"); break;//should we differentiate COMPLEX_INV and INV?
					case COMPLEX_TRANSLOCATION: 
					case COMPLEX_DUPLICATION:
						GenomicNode insNode = e.getNode(true);
						String label = (e.getType() == EVENT_TYPE.COMPLEX_DUPLICATION? "DUP" : "TRANS");
						l1 = generateNodeLabel(insNode);
						Event[] allEvents = ((ComplexEvent)e).getEventsInvolvedInComplexEvent();
						for(int i=0;i<allEvents.length;i++){
							if(allEvents[i].getNode(true) == insNode){
								l2 = generateNodeLabel(allEvents[i].getNode(false));
								output.write(l1+"->"+l2+"[label=\""+label+"\" arrowtail=odiamond arrowhead=normal dir=both];\n");
							} else if(allEvents[i].getNode(false) == insNode){
								l2 = generateNodeLabel(allEvents[i].getNode(true));
								output.write(l1+"->"+l2+"[label=\""+label+"\" arrowtail=odiamond arrowhead=normal dir=both];\n");
							}
							
						}
						break;
					default: output.write(l1+"->"+l2+"[label=\""+e.getType()+"\"];\n");
					}
				}
			}
		}
		output.write("}\n");
		output.flush();
		output.close();
	}
	
	public static void compareToGoldStandard(String goldFileName, Hashtable<String, TreeSet<GenomicNode>> genomicNodes, int margin) throws IOException {
		BufferedReader gold = new BufferedReader(new FileReader(goldFileName));
		String goldLine = gold.readLine();
		Iterator<GenomicNode> iter = genomicNodes.get("chr12").iterator();
		GenomicNode n = iter.next();
		Event e = n.getEvents().get(0);
		int fps = 0, fns = 0, tps = 0;
		
		while(goldLine != null && e != null){
			StringTokenizer st = new StringTokenizer(goldLine, ":-\t");
			String type = st.nextToken();
			String chr = st.nextToken();
			int start = Integer.parseInt(st.nextToken());
			//int end = Integer.parseInt(st.nextToken());
			if(type.equals("SNP") || type.equals("JOIN")){
				goldLine = gold.readLine();
				continue;
			}
			GenomicCoordinate compare = (e.getType() == EVENT_TYPE.COMPLEX_DUPLICATION || e.getType() == EVENT_TYPE.COMPLEX_TRANSLOCATION? ((ComplexEvent)e).getInsertionPoint() : e.getC1());
			if(compare.distanceTo(new GenomicCoordinate(chr, start)) > margin) {
				if(compare.compareTo(new GenomicCoordinate(chr, start)) < 0) {
					System.out.println("FP: "+e);
					fps++;
				} else {
					System.out.println("FN: "+goldLine);
					fns++;
					goldLine = gold.readLine();
					continue;
				}
			} else {
				if(type.equals("INVERSION") && e.getType()==EVENT_TYPE.COMPLEX_INVERSION || type.equals("DELETION") && e.getType()==EVENT_TYPE.DEL
						|| type.equals("TANDEM") && e.getType()==EVENT_TYPE.TAN || type.equals("INSERTION") && e.getType()==EVENT_TYPE.COMPLEX_DUPLICATION
						|| type.equals("TRANSLOCATION") && e.getType()==EVENT_TYPE.COMPLEX_TRANSLOCATION) {
					System.out.println("TP: "+e+" "+goldLine);
					tps++;
					goldLine = gold.readLine();
				} else {
					System.out.println("FP: Type mismatch: "+e+" "+goldLine);
					fps++;
				}
			}
			n = iter.next();
			while(n!=null && n.getEvents().size()==0){
				n = iter.next();
			}
			if(n!=null)
				e = n.getEvents().get(0);
			else 
				e = null;
		}
		while(goldLine!=null){
			System.out.println("FN: "+goldLine);
			fns++;
			goldLine = gold.readLine();
		}
		while(n!=null){
			if(n.getEvents().size()>0){
				System.out.println("FP: "+n.getEvents().get(0));
				fps++;
			}
			if(iter.hasNext())
				n = iter.next();
			else 
				n = null;
		}
		System.out.println("FP:"+fps+"\tFN:"+fns+"\tTP:"+tps);
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
		case DELLY:		maxDistanceForNodeMerge = 250; break;
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
		
		//compareToGoldStandard("data/simulated_chr12_1.fa", genomicNodes, 150);
		
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
									System.out.println("Complex inversion between "+e1+" and "+e2);
									GenomicCoordinate invstart = (e1.getC1().compareTo(e1.getC2()) < 0? e1.getC1() : e1.getC2());
									GenomicCoordinate invend   = (e2.getC2().compareTo(e2.getC1()) < 0? e2.getC1() : e2.getC2());
									System.out.println(e1.getC1()+"\t"+e1.getC2()+"\t"+e2.getC1()+"\t"+e2.getC2()+"\t"+invstart+"\t"+invend);
									newComplexEvent = new ComplexEvent(invstart, invend, EVENT_TYPE.COMPLEX_INVERSION, (new Event[] {e1, e2}), currentNode);
									//currentNode?
									System.out.println(currentNode.getStart().toString());
									System.out.println(currentNode.getEnd().toString());
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
										Event e3 = other1.existsDeletionEventTo(other2);
										if(e3 != null){
											//System.out.println("Translocation between "+e1+ " and "+ e2);
											//intrachromosomal translocations are actually ambiguous as to where they come from and got to
											//as a convention, we call the smaller bit the translocated one inserted into the larger bit.
											if(e1.size() < e3.size()) {
												//area under e1 is translocated
												GenomicCoordinate transtart, tranend;
												if(e1.getC1().compareTo(e1.getC2()) < 0) { // don't assume ordered coordinates
													transtart = e1.getC1();
													tranend   = e1.getC2();
												} else {
													transtart = e1.getC2();
													tranend   = e1.getC1();
												}
												GenomicCoordinate traninsert = (e2.getNode(true) == currentNode? e2.getC2() : e2.getC1());
												GenomicNode hostingNode = (e2.getNode(true) == currentNode? e2.getNode(false) : e2.getNode(true));
												newComplexEvent = new ComplexEvent(transtart, tranend, EVENT_TYPE.COMPLEX_TRANSLOCATION, (new Event[] {e1, e2, e3}), hostingNode, traninsert);
											} else {
												//area under e3 is translocated
												GenomicCoordinate transtart, tranend;
												if(e3.getC1().compareTo(e3.getC2()) < 0) {
													transtart = e3.getC1();
													tranend   = e3.getC2();
												} else {
													transtart = e3.getC2();
													tranend   = e3.getC1();
												}
												GenomicCoordinate traninsert = (e2.getNode(true) == currentNode? e2.getC1() : e2.getC2());
												newComplexEvent = new ComplexEvent(transtart, tranend, EVENT_TYPE.COMPLEX_TRANSLOCATION, (new Event[] {e1, e2, e3}), currentNode, traninsert);
											}
										}
										else {
											//System.out.println("Duplication between "+e1+ " and "+ e2);
											GenomicCoordinate dupstart, dupend, insert;
											if(other1.compareTo(other2) < 0){
												//duplicated bit is downstream of currentNode
												dupstart = (e1.getNode(true) == currentNode? e1.getC2() : e1.getC1());
												dupend   = (e2.getNode(true) == currentNode? e2.getC2() : e2.getC1());
												insert   = (e1.getNode(true) == currentNode? e1.getC1() : e1.getC2());
												newComplexEvent = new ComplexEvent(dupstart, dupend, EVENT_TYPE.COMPLEX_DUPLICATION, (new Event[] {e1, e2}), currentNode, insert);
											} else {
												//duplication upstream of currentNode
												dupstart = (e2.getNode(true) == currentNode? e2.getC2() : e2.getC1());
												dupend   = (e1.getNode(true) == currentNode? e1.getC2() : e1.getC1());
												insert   = (e2.getNode(true) == currentNode? e2.getC1() : e2.getC2());
												newComplexEvent = new ComplexEvent(dupstart, dupend, EVENT_TYPE.COMPLEX_DUPLICATION, (new Event[] {e1, e2}), currentNode, insert);
											}
											
										}
									}
								}
								break;
							}
							//interchromosomal events
							case ITX1: {
								if(e2.getType() == EVENT_TYPE.ITX2) {
									GenomicNode other1 = e1.otherNode(currentNode), other2 = e2.otherNode(currentNode);
									if(other1.getStart().onSameChromosome(other2.getStart()) && other1.getEnd().compareTo(other2.getStart()) < 0){
										GenomicCoordinate eventStart = (e1.getNode(true)==currentNode? e1.getC2() : e1.getC1()), 
												eventEnd = (e2.getNode(true)==currentNode? e2.getC2() : e2.getC1()),
												eventInsert = (e1.getNode(true)==currentNode? e1.getC1() : e1.getC2());
										Event e3 = other1.existsDeletionEventTo(other2);
										if(e3 != null){
											newComplexEvent = new ComplexEvent(eventStart, eventEnd, EVENT_TYPE.COMPLEX_INTERCHROMOSOMAL_TRANSLOCATION, new Event[] {e1, e2, e3}, currentNode, eventInsert);
										} else {
											newComplexEvent = new ComplexEvent(eventStart, eventEnd, EVENT_TYPE.COMPLEX_INTERCHROMOSOMAL_DUPLICATION, new Event[] {e1, e2}, currentNode, eventInsert);
										}
									}
								}
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
				for(Event e: newComplexEvents){
					e.getNode(true).getEvents().add(e);
				}
			}
		}
		
		//while we're at it: let's run through the nodes again!
		//this time for output
		int totalEvents = 0;
		for(Entry<String, TreeSet<GenomicNode>> tableEntry: genomicNodes.entrySet()) {
			for(GenomicNode currentNode: tableEntry.getValue()){
				if(currentNode.getEvents().size() > 1){
					System.out.println("Node might be shifty: "+currentNode.getEvents().size()+" members!");
				}
				totalEvents += currentNode.getEvents().size();
				HashSet<Event> skipEvents = new HashSet<Event>();
				for(Event e: currentNode.getEvents()){
					if(skipEvents.contains(e))
						continue;
					//if(currentNode.getEvents().size() < 3 && (e.getType() == EVENT_TYPE.COMPLEX_INTERCHROMOSOMAL_DUPLICATION || e.getType()==EVENT_TYPE.COMPLEX_INTERCHROMOSOMAL_TRANSLOCATION))
					System.out.println(e);
					if(e.otherNode(currentNode) == currentNode){
						skipEvents.add(e);
						//System.out.println("Self reference: "+e);
					} else {
						e.otherNode(currentNode).getEvents().remove(e);
					}
				}
			}
		}
		//System.out.println("Total events: "+totalEvents);
		
		//compareToGoldStandard("data/simulated_chr12_1.fa", genomicNodes, 150);
	
		graphVisualisation("data/simul_chr12_graph.gv", genomicNodes);
		
	}

	

}
