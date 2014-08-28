import java.util.StringTokenizer;


enum EVENT_TYPE {INS, INV1, INV2, DEL, TAN, TRAN, DUP, INVTX1, INVTX2, ITX1, ITX2, XXX};

public class Event {

	private GenomicCoordinate c1, c2;
	private EVENT_TYPE type;
	private GenomicNode[] myNodes;
	
	public Event(GenomicCoordinate c1, GenomicCoordinate c2, EVENT_TYPE type){
		this.c1 = c1;
		this.c2 = c2;
		this.type = type;
		myNodes = new GenomicNode[2];
	}

	/*
	 * Static function to handle the particularities of Socrates output, and convert it into a general
	 * purpose Event.
	 */
	public static Event createNewEventFromSocratesOutput(String output){
		StringTokenizer t = new StringTokenizer(output, "\t:");
		String chr1 = t.nextToken();
		int p1 = Integer.parseInt(t.nextToken());
		String o1 = t.nextToken();
		t.nextToken();
		String chr2 = t.nextToken();
		int p2 = Integer.parseInt(t.nextToken());
		String o2 = t.nextToken();
		
		GenomicCoordinate c1 = new GenomicCoordinate(chr1, p1);
		GenomicCoordinate c2 = new GenomicCoordinate(chr2, p2);
		EVENT_TYPE type = classifySocratesBreakpoint(c1, o1, c2, o2);
		
		return new Event(c1, c2, type);
	}
	/*
	 * Function to classify a line of Socrates output into a genomic event type.
	 * The distinctions between INV1/2 etc are arbitrary, and have to be consistent across all the inputs.
	 */
	private static EVENT_TYPE classifySocratesBreakpoint(GenomicCoordinate c1, String o1, GenomicCoordinate c2, String o2){
		if(c1.onSameChromosome(c2)){
			if(o1.equals(o2)){
				if(o1.equals("+"))
					return EVENT_TYPE.INV1;
				else
					return EVENT_TYPE.INV2;
			} else if (o1.equals("+") && c1.compareTo(c2) < 0 || o1.equals("-") && c1.compareTo(c2) >=0 ){
				return EVENT_TYPE.DEL;
			} else if (o1.equals("-") && c1.compareTo(c2) < 0 || o1.equals("+") && c1.compareTo(c2) >=0 ){
				return EVENT_TYPE.TAN;
			} else {
				return EVENT_TYPE.XXX;
			}
		} else if(o1.equals(o2)) {
			if(o1.equals("+"))
				return EVENT_TYPE.INVTX1;
			else
				return EVENT_TYPE.INVTX2;
		} else if(o1.equals("+")){
			return EVENT_TYPE.ITX1;
		} else {
			return EVENT_TYPE.ITX2;
		}
	}
	/*
	 * 
	 */
	public static Event createNewEventFromDellyOutput(String output){
		StringTokenizer t = new StringTokenizer(output, "\t:");
		String chr1 = t.nextToken();
		String chr2 = chr1;
		int p1 = Integer.parseInt(t.nextToken());
		int p2 = Integer.parseInt(t.nextToken());
		t.nextToken();
		t.nextToken();
		t.nextToken();
		String typeT;
		String tempT = t.nextToken(); 
		typeT = tempT.substring(1,tempT.indexOf("_"));
		if (typeT.equals("Inversion")){
			typeT = tempT.substring(1,(tempT.indexOf("_")+2));
		}
		
		GenomicCoordinate c1 = new GenomicCoordinate(chr1, p1);
		GenomicCoordinate c2 = new GenomicCoordinate(chr2, p2);
		EVENT_TYPE type = classifyDellyBreakpoint(c1, c2, typeT);
		
		System.out.println(chr1 +"\t"+ p1 +"\t"+ p2 +"\t" + type +"\t"+ typeT);
		
		return new Event(c1, c2, type);
	}

	/*
	 * Function to classify a line of Delly output into a genomic event type.
	 * The distinctions between INV1/2 etc are arbitrary, and have to be consistent across all the inputs.
	 */
	private static EVENT_TYPE classifyDellyBreakpoint(GenomicCoordinate c1, GenomicCoordinate c2, String t){
		if(t.equals("Inversion_0")){
			return EVENT_TYPE.INV1;
		} else if (t.equals("Inversion_1")){
			return EVENT_TYPE.INV2;
		} else if (t.equals("Deletion")){
			return EVENT_TYPE.DEL;
		} else if (t.equals("Duplication")){
			return EVENT_TYPE.TAN;
		} else {
			return EVENT_TYPE.XXX;
		}
	}
	
	public GenomicCoordinate getC1() {
		return c1;
	}

	public GenomicCoordinate getC2() {
		return c2;
	}

	public EVENT_TYPE getType() {
		return type;
	}
	
	public void setNode(GenomicNode n, boolean firstCoordinate){
		if(firstCoordinate)
			myNodes[0] = n;
		else
			myNodes[1] = n;
	}
	
	public GenomicNode getNode(boolean firstCoordinate){
		if(firstCoordinate)
			return myNodes[0];
		else
			return myNodes[1];
	}
}
