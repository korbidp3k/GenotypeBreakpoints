import java.util.StringTokenizer;


enum EVENT_TYPE {INS, INV1, INV2, DEL, TAN, INVTX1, INVTX2, ITX1, ITX2, XXX, COMPLEX_INVERSION, COMPLEX_INVERTED_DUPLICATION, COMPLEX_DUPLICATION, COMPLEX_TRANSLOCATION, COMPLEX_INVERTED_TRANSLOCATION, COMPLEX_INTERCHROMOSOMAL_TRANSLOCATION, COMPLEX_INTERCHROMOSOMAL_DUPLICATION, COMPLEX_INTERCHROMOSOMAL_INVERTED_TRANSLOCATION, COMPLEX_INTERCHROMOSOMAL_INVERTED_DUPLICATION};

public class Event {

	private GenomicCoordinate c1, c2;
	private EVENT_TYPE type;
	private GenomicNode[] myNodes;
	private String additionalInformation;
	
	public Event(GenomicCoordinate c1, GenomicCoordinate c2, EVENT_TYPE type){
		if(c1.compareTo(c2) < 0){
			this.c1 = c1;
			this.c2 = c2;
		} else {
			this.c1 = c2;
			this.c2 = c1;
		}
		this.type = type;
		myNodes = new GenomicNode[2];
	}
	
	public Event(GenomicCoordinate c1, GenomicCoordinate c2, EVENT_TYPE type, String additionalInformation){
		this(c1,c2,type);
		this.additionalInformation = additionalInformation;
	}

	/*
	 * Static function to handle the particularities of Socrates output, and convert it into a general
	 * purpose Event.
	 */
	public static Event createNewEventFromSocratesOutput(String output){
		String line = output.replace("\t\t", "\tX\t");
		StringTokenizer t = new StringTokenizer(line);
		String chr1 = t.nextToken(":");
		int p1 = Integer.parseInt(t.nextToken(":\t"));
		String o1 = t.nextToken("\t");
		t.nextToken("\t");
		String chr2 = t.nextToken("\t:");
		int p2 = Integer.parseInt(t.nextToken("\t:"));
		String o2 = t.nextToken("\t");
		
		GenomicCoordinate c1 = new GenomicCoordinate(chr1, p1);
		GenomicCoordinate c2 = new GenomicCoordinate(chr2, p2);
		EVENT_TYPE type = classifySocratesBreakpoint(c1, o1, c2, o2);
		
		//look for additional information at the end of the call
		int i = 0;
		while(i<19 && t.hasMoreTokens()){
      i++;
			t.nextToken();
		}
		String additionalComments = (t.hasMoreTokens()? t.nextToken() : "");
		if(additionalComments.startsWith("Inserted sequence")){
			String insert = additionalComments.substring("Inserted sequence: ".length());
			return new Event(c1, c2, type, insert);
		}
		
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
		} else if(o1.equals("+") &&  c1.compareTo(c2) < 0 || o1.equals("-") && c1.compareTo(c2) >= 0){
			return EVENT_TYPE.ITX1;
		} else {
			return EVENT_TYPE.ITX2;
		}
	}
	/*
	 * Static function to handle the particularities of Delly output, and convert it into a general
	 * purpose Event.
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
		
		//System.out.println(chr1 +"\t"+ p1 +"\t"+ p2 +"\t" + type +"\t"+ typeT);
		
		return new Event(c1, c2, type);
	}

	/*
	 * Function to classify a line of Delly output into a genomic event type.
	 * The distinctions between INV1/2 etc are arbitrary, and have to be consistent across all the inputs.
	 * c1 and c2 are always the same chromosome
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
	
	
	public static Event createNewEventFromCrestOutput(String output) {
		StringTokenizer t = new StringTokenizer(output, "\t");
		
		String chr1 = t.nextToken();
		int p1 = Integer.parseInt(t.nextToken());
		String o1 = t.nextToken();
		t.nextToken();
		String chr2 = t.nextToken();
		int p2 = Integer.parseInt(t.nextToken());
		String o2 = t.nextToken();
		
		GenomicCoordinate c1 = new GenomicCoordinate(chr1, p1);
		GenomicCoordinate c2 = new GenomicCoordinate(chr2, p2);
		
		t.nextToken();
		EVENT_TYPE type = classifyCrestBreakpoint(t.nextToken());
		
		if(type == EVENT_TYPE.COMPLEX_INVERSION){
			return new ComplexEvent(c1, c2, type, null, null);
		}
		return new Event(c1, c2, type);
	}
	private static EVENT_TYPE classifyCrestBreakpoint(String t){
		if(t.equals("DEL")){
			return EVENT_TYPE.DEL;
		} else if (t.equals("INS")){
			return EVENT_TYPE.TAN;
		} else if (t.equals("INV")){
			return EVENT_TYPE.COMPLEX_INVERSION;
		} else if(t.equals("ITX")){
			return EVENT_TYPE.INV1;
		} else {
			return EVENT_TYPE.XXX;
		}
	}


	public static Event createNewEventFromGustafOutput(String output) {
		StringTokenizer t = new StringTokenizer(output, "\t");
		
		String chr1 = t.nextToken();
		t.nextToken();
		EVENT_TYPE type = classifyGustafBreakpoint(t.nextToken());
		int p1 = Integer.parseInt(t.nextToken());
		int p2 = Integer.parseInt(t.nextToken());
		t.nextToken();
		String o = t.nextToken();
		if(type==EVENT_TYPE.INV1 && o.equals("-"))
			type = EVENT_TYPE.INV2;
		t.nextToken();
		String info = t.nextToken();
		StringTokenizer i = new StringTokenizer(info, "=;");
		i.nextToken();
		i.nextToken();
		String d = i.nextToken();
		String chr2;
		if(d.equals("endChr")){
			chr2 = i.nextToken();
			i.nextToken();
			p2 = Integer.parseInt(i.nextToken());
		} else if (d.equals("size")) {
			chr2 = chr1;
		} else {
			System.err.println("Confusion in the Gustaf camp!");
			chr2=null;
		}
		
		GenomicCoordinate c1 = new GenomicCoordinate(chr1, p1);
		GenomicCoordinate c2 = new GenomicCoordinate(chr2, p2);
		
		return new Event(c1, c2, type);
	}
	private static EVENT_TYPE classifyGustafBreakpoint(String t){
		if(t.equals("deletion")){
			return EVENT_TYPE.DEL;
		} else if (t.equals("duplication")){
			return EVENT_TYPE.TAN;
		} else if (t.equals("inversion")){
			return EVENT_TYPE.INV1;
		} else if(t.equals("ITX")){
			return EVENT_TYPE.INV1;
		} else if (t.equals("insertion")) {
			return EVENT_TYPE.INS;
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
	
	
	public static boolean sameNodeSets(Event e1, Event e2){
		if(e1.myNodes[0] == e2.myNodes[0] && e1.myNodes[1] == e2.myNodes[1] 
				|| e1.myNodes[0] == e2.myNodes[1] && e1.myNodes[1] == e2.myNodes[0])
			return true;
		return false;		
	}
	
	@Override
	public String toString() {
		if(c1.onSameChromosome(c2)){
			return c1.getChr()+":"+c1.getPos()+"-"+c2.getPos()+" "+type;
		} else {
			return c1+"<->"+c2+" "+type;
		}
	}
	
	public GenomicNode otherNode(GenomicNode node){
		if(myNodes[0] == node) {
			return myNodes[1];
		}
		if(myNodes[1] == node) {
			return myNodes[0];
		}
		System.err.println("otherNode: query node is not assiciated with Event!");
		return null;
	}
	
	public int size() {
		return c1.distanceTo(c2);
	}
	
	public void processAdditionalInformation(){
		if(this.additionalInformation!= null && this.additionalInformation.matches("[ACGT]+") && myNodes[0] == myNodes[1]){
			this.type = EVENT_TYPE.INS;
		}
	}
}
