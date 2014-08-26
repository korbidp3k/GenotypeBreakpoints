import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;

class Cluster{
	String c1, c2, clusterString;
	int p1, p2;
	String o1, o2;
	ClusterNode[] nodes;
	public Cluster(String clusterString) {
		StringTokenizer t = new StringTokenizer(clusterString, "\t:");
//		t.nextToken();
		this.c1 = t.nextToken();
		this.p1 = Integer.parseInt(t.nextToken());
		this.o1 = t.nextToken();
		t.nextToken();
		this.c2 = t.nextToken();
		this.p2 = Integer.parseInt(t.nextToken());
		this.o2 = t.nextToken();
		
		this.clusterString = clusterString;
		
		nodes = new ClusterNode[2];
		//System.out.println(this.c1 +"\t"+ this.c2 +"\t"+ this.p1 +"\t"+ this.p2 +"\t"+ this.o1 +"\t"+ this.o2);
		
		if(p1==p2)
			System.err.println("Same coodinates for cluster -- this may lead to problems.");
	}
	 @Override
		public String toString() {
			//return c1+":"+p1+" ("+o1+") <-> "+c2+":"+p2+" ("+o2+")" ;
			return clusterString;
		}
	 public int distanceTo(Cluster other, boolean compareThisFirst, boolean compareOtherFirst){
		 if(compareThisFirst){
			 if(compareOtherFirst){
				 return (this.c1.equals(other.c1)? Math.abs(this.p1-other.p1) : Integer.MAX_VALUE);
			 } else {
				 return (this.c1.equals(other.c2)? Math.abs(this.p1-other.p2) : Integer.MAX_VALUE);
			 }
		 } else {
			 if(compareOtherFirst){
				 return (this.c2.equals(other.c1)? Math.abs(this.p2-other.p1) : Integer.MAX_VALUE);
			 } else {
				 return (this.c2.equals(other.c2)? Math.abs(this.p2-other.p2) : Integer.MAX_VALUE);
			 }
		 }
	 }
	 public void addNode(ClusterNode n, int coordinate){
		 if(p1==coordinate){
			 nodes[0] = n;
		 } 
		 if(p2==coordinate){
			 nodes[1] = n;
		 } 
		 if(p1!=coordinate && p2!=coordinate)	{
			 System.err.println("Coordinate does not match cluster configuration (addNode).");
		 }
	 }
	 
	 public ClusterNode getNode(int coordinate){
		 if(p1==coordinate){
			 return nodes[0];
		 } else if(p2==coordinate){
			 return nodes[1];
		 } else {
			 System.err.println("Coordinate does not match cluster configuration (getNode).");
			 return null;
		 }
	 }
}

class ClusterDelly{
	String c1, c2, type, clusterString;
	int p1, p2;
	//String o1, o2;
	ClusterNodeDelly[] nodes;
	public ClusterDelly(String clusterString) {
		StringTokenizer t = new StringTokenizer(clusterString, "\t:");
		//System.out.println(clusterString);
		this.c1 = t.nextToken();
		
		this.c2 = this.c1;
		this.p1 = Integer.parseInt(t.nextToken());
		this.p2 = Integer.parseInt(t.nextToken());
		//this.o1 = t.nextToken();
		//this.o2 = t.nextToken();
		t.nextToken();
		t.nextToken();
		t.nextToken();
		this.type = t.nextToken();
		
		this.type = this.type.substring(1,this.type.indexOf("_"));
		//System.out.println(this.c1 +"\t"+ this.c2 +"\t"+ this.p1 +"\t"+ this.p2 +"\t"+ this.type);
		
		this.clusterString = clusterString;
		
		nodes = new ClusterNodeDelly[2];
		
		if(p1==p2)
			System.err.println("Same coodinates for cluster -- this may lead to problems."+p1+" "+p2);
	}
	 @Override
		public String toString() {
			//return c1+":"+p1+" ("+o1+") <-> "+c2+":"+p2+" ("+o2+")" ;
			return clusterString;
		}
	 public int distanceTo(Cluster other, boolean compareThisFirst, boolean compareOtherFirst){
		 if(compareThisFirst){
			 if(compareOtherFirst){
				 return (this.c1.equals(other.c1)? Math.abs(this.p1-other.p1) : Integer.MAX_VALUE);
			 } else {
				 return (this.c1.equals(other.c2)? Math.abs(this.p1-other.p2) : Integer.MAX_VALUE);
			 }
		 } else {
			 if(compareOtherFirst){
				 return (this.c2.equals(other.c1)? Math.abs(this.p2-other.p1) : Integer.MAX_VALUE);
			 } else {
				 return (this.c2.equals(other.c2)? Math.abs(this.p2-other.p2) : Integer.MAX_VALUE);
			 }
		 }
	 }
	 public void addNode(ClusterNodeDelly n, int coordinate){
		 if(p1==coordinate){
			 nodes[0] = n;
		 } 
		 if(p2==coordinate){
			 nodes[1] = n;
		 } 
		 if(p1!=coordinate && p2!=coordinate)	{
			 System.err.println("Coordinate does not match cluster configuration (addNode).");
		 }
	 }
	 
	 public ClusterNodeDelly getNode(int coordinate){
		 if(p1==coordinate){
			 return nodes[0];
		 } else if(p2==coordinate){
			 return nodes[1];
		 } else {
			 System.err.println("Coordinate does not match cluster configuration (getNode).");
			 return null;
		 }
	 }
}

class CoordinateClusterPair implements Comparable<CoordinateClusterPair> {
	int coordinate;
	int otherCoordinate;
	String otherChromosome;
	String orientation;
	Cluster cluster;
	public CoordinateClusterPair(Cluster cluster, int coordinate, String orientation) {
		this.cluster = cluster;
		this.coordinate = coordinate;
		this.orientation = orientation;
		if(cluster.p1 == coordinate){
			otherChromosome = cluster.c2;
			otherCoordinate = cluster.p2;
		} else {
			otherChromosome = cluster.c1;
			otherCoordinate = cluster.p1;
		}
	}
	@Override
	public int compareTo(CoordinateClusterPair arg0) {
		if(this.coordinate < arg0.coordinate)
			return -1;
		if(this.coordinate > arg0.coordinate)
			return 1;
		return 0;
	}
	@Override
	public String toString() {
		return this.cluster.toString();
	}
	public int distanceTo(CoordinateClusterPair c){
		return Math.abs(this.coordinate - c.coordinate);
	}
	public int otherDistanceTo(CoordinateClusterPair c) {
		if(this.otherChromosome.equals(c.otherChromosome))		
			return Math.abs(this.otherCoordinate - c.otherCoordinate);
		else
			return Integer.MAX_VALUE;
	}
	public boolean sameChromosomes(CoordinateClusterPair c) {
		if(this.cluster.c1.equals(c.cluster.c1) && this.cluster.c2.equals(c.cluster.c2)
		|| this.cluster.c2.equals(c.cluster.c1) && this.cluster.c1.equals(c.cluster.c2)) 
			return true;
		return false;
	}
}

class CoordinateClusterPairDelly implements Comparable<CoordinateClusterPairDelly> {
	int coordinate;
	int otherCoordinate;
	String otherChromosome;
	String orientation;
	ClusterDelly cluster;
	public CoordinateClusterPairDelly(ClusterDelly cluster, int coordinate) {
		this.cluster = cluster;
		this.coordinate = coordinate;
		//this.orientation = orientation;
		if(cluster.p1 == coordinate){
			otherChromosome = cluster.c2;
			otherCoordinate = cluster.p2;
		} else {
			otherChromosome = cluster.c1;
			otherCoordinate = cluster.p1;
		}
	}
	@Override
	public int compareTo(CoordinateClusterPairDelly arg0) {
		if(this.coordinate < arg0.coordinate)
			return -1;
		if(this.coordinate > arg0.coordinate)
			return 1;
		return 0;
	}
	@Override
	public String toString() {
		return this.cluster.toString();
	}
	public int distanceTo(CoordinateClusterPair c){
		return Math.abs(this.coordinate - c.coordinate);
	}
	public int otherDistanceTo(CoordinateClusterPair c) {
		if(this.otherChromosome.equals(c.otherChromosome))		
			return Math.abs(this.otherCoordinate - c.otherCoordinate);
		else
			return Integer.MAX_VALUE;
	}
	public boolean sameChromosomes(CoordinateClusterPair c) {
		if(this.cluster.c1.equals(c.cluster.c1) && this.cluster.c2.equals(c.cluster.c2)
		|| this.cluster.c2.equals(c.cluster.c1) && this.cluster.c1.equals(c.cluster.c2)) 
			return true;
		return false;
	}
}



enum SVTYPE {INS, INV, DEL, TAN, TRAN, DUP, INVTX, ITX, XXX};

class ClusterLink {
	Cluster link;
	SVTYPE type;
	public ClusterLink(Cluster c, SVTYPE t){
		this.link = c;
		this.type = t;
	}
}


class ClusterNode {
	
	public static HashSet<Cluster> redundantMembers;
	
	ArrayList<Cluster> members;
	Hashtable<ClusterNode, ArrayList<Cluster>> edges;

	public ClusterNode(){
		this.members = new ArrayList<Cluster>();
		this.edges = new Hashtable<ClusterNode, ArrayList<Cluster>>();
	}
	public void addMember(Cluster c){
		if(!members.contains(c))
			this.members.add(c);
		
	}
	public void addEdge(ClusterNode n, Cluster c){
		if(! edges.containsKey(n)){
			edges.put(n, new ArrayList<Cluster>());
		}
		edges.get(n).add(c);
	}
	public boolean supportSameEdge(Cluster c1, Cluster c2){
		ClusterNode other1 = null, other2 = null;
		if(c1.getNode(c1.p1) == this){
			other1 = c1.getNode(c1.p2);
		} else { //assume correct clusters
			other1 = c1.getNode(c1.p1);
		}
		if(c2.getNode(c2.p1) == this){
			other2 = c2.getNode(c2.p2);
		} else { //assume correct clusters
			other2 = c2.getNode(c2.p1);
		}
		if(other1 == other2)
			return true;
		return false;
	}
	public ClusterNode otherNode(Cluster c){
		if(this==c.nodes[0])
			return c.getNode(c.p2);
		if(this==c.nodes[1])
			return c.getNode(c.p1);
		System.err.println("Cluster not a member of node!");
		return null;
	}
	public int otherCoordinate (Cluster c){
		if(this==c.nodes[0])
			return c.p2;
		if(this==c.nodes[1])
			return c.p1;
		System.err.println("Cluster not a member of node!");
		return -1;
	}
	public int thisCoordinate (Cluster c){
		if(this==c.nodes[0])
			return c.p1;
		if(this==c.nodes[1])
			return c.p2;
		System.err.println("Cluster not a member of node!");
		return -1;
	}
	public void checkMembers(){
		for (int i=0;i<members.size();i++){
			for (int j=i+1; j<members.size();j++){
				Cluster c1 = members.get(i), c2 = members.get(j);
				if(this.otherNode(c1) != this.otherNode(c2))
					continue;
				String o11, o12, o21, o22;
				if(c1.nodes[0] == this){
					o11 = c1.o1;
					o12 = c1.o2;
				} else{
					o11 = c1.o2;
					o12 = c1.o1;
				}
				if(c2.nodes[0] == this){
					o21 = c2.o1;
					o22 = c2.o2;
				} else{
					o21 = c2.o2;
					o22 = c2.o1;
				}
				if(o11.equals(o21) && o12.equals(o22)){
					ClusterNode.redundantMembers.add(c2);
				}
			}
		}
	}
}

class ClusterNodeDelly {
	
	public static HashSet<ClusterDelly> redundantMembers;
	
	ArrayList<ClusterDelly> members;
	Hashtable<ClusterNodeDelly, ArrayList<ClusterDelly>> edges;

	public ClusterNodeDelly(){
		this.members = new ArrayList<ClusterDelly>();
		this.edges = new Hashtable<ClusterNodeDelly, ArrayList<ClusterDelly>>();
	}
	public void addMember(ClusterDelly c){
		if(!members.contains(c))
			this.members.add(c);
		
	}
	public void addEdge(ClusterNodeDelly n, ClusterDelly c){
		if(! edges.containsKey(n)){
			edges.put(n, new ArrayList<ClusterDelly>());
		}
		edges.get(n).add(c);
	}
	public boolean supportSameEdge(ClusterDelly c1, ClusterDelly c2){
		ClusterNodeDelly other1 = null, other2 = null;
		if(c1.getNode(c1.p1) == this){
			other1 = c1.getNode(c1.p2);
		} else { //assume correct clusters
			other1 = c1.getNode(c1.p1);
		}
		if(c2.getNode(c2.p1) == this){
			other2 = c2.getNode(c2.p2);
		} else { //assume correct clusters
			other2 = c2.getNode(c2.p1);
		}
		if(other1 == other2)
			return true;
		return false;
	}
	public ClusterNodeDelly otherNode(ClusterDelly c){
		if(this==c.nodes[0])
			return c.getNode(c.p2);
		if(this==c.nodes[1])
			return c.getNode(c.p1);
		System.err.println("Cluster not a member of node!");
		return null;
	}
	public int otherCoordinate (ClusterDelly c){
		if(this==c.nodes[0])
			return c.p2;
		if(this==c.nodes[1])
			return c.p1;
		System.err.println("Cluster not a member of node!");
		return -1;
	}
	public int thisCoordinate (ClusterDelly c){
		if(this==c.nodes[0])
			return c.p1;
		if(this==c.nodes[1])
			return c.p2;
		System.err.println("Cluster not a member of node!");
		return -1;
	}
	public void checkMembers(){
		for (int i=0;i<members.size();i++){
			for (int j=i+1; j<members.size();j++){
				ClusterDelly c1 = members.get(i), c2 = members.get(j);
				if(this.otherNode(c1) != this.otherNode(c2))
					continue;
				/*String o11, o12, o21, o22;
				if(c1.nodes[0] == this){
					o11 = c1.o1;
					o12 = c1.o2;
				} else{
					o11 = c1.o2;
					o12 = c1.o1;
				}
				if(c2.nodes[0] == this){
					o21 = c2.o1;
					o22 = c2.o2;
				} else{
					o21 = c2.o2;
					o22 = c2.o1;
				}
				if(o11.equals(o21) && o12.equals(o22)){
					ClusterNode.redundantMembers.add(c2);
				}*/
				String t1="", t2="";
				if(c1.nodes[0] == this){
					t1 = c1.type;

				} else{
					t2 = c1.type;
				}
				if(c2.nodes[0] == this){
					t1 = c2.type;
				} else{
					t2 = c2.type;
				}
				if(t1.equals(t2)) {
					ClusterNodeDelly.redundantMembers.add(c2);
				}
				/*if((c1.p1)==(c2.p1) && (c1.p2)==(c2.p2)){
					ClusterNodeDelly.redundantMembers.add(c2);
				}*/
			}
		}
	}
}

public class GenotypeBreakpoints {
	
	private static SVTYPE classifyBreakpoint(Cluster bp){
		if(bp.o1.equals(bp.o2)){
			if(bp.c1.equals(bp.c2)){
				return SVTYPE.INV;
			} else {
				return SVTYPE.INVTX;
			}
		} else {
			if(bp.c1.equals(bp.c2)){
				if(bp.o1.equals("+") && bp.p1 < bp.p2 || bp.o1.equals("-") && bp.p1>=bp.p2){
					return SVTYPE.DEL;
				} 
				if(bp.o1.equals("-") && bp.p1 < bp.p2 || bp.o1.equals("+") && bp.p1>=bp.p2){
					return SVTYPE.TAN;
				}
				return SVTYPE.XXX;
			} else {
				return SVTYPE.ITX;
			}
		}
	}
	
	private static SVTYPE classifyBreakpointDelly(ClusterDelly bp){
		if(bp.type.equals("Inversion")){//Is this necessary?
			if(bp.c1.equals(bp.c2)){
				return SVTYPE.INV;
			} else {
				return SVTYPE.INVTX;//Is this necessary?
			}
		} else {
			if(bp.c1.equals(bp.c2)){//Is this necessary?
				if(bp.type.equals("Deletion")){
					return SVTYPE.DEL;
				} 
				else if(bp.type.equals("Duplication")){
					return SVTYPE.TAN;
				}
				return SVTYPE.XXX;
			} else {
				return SVTYPE.ITX;//Is this necessary?
			}
		}
	}
	
	
	private static void addClusterLink(Hashtable<Cluster, ArrayList<ClusterLink>> pairedBPs, Cluster cluster1, Cluster cluster2, SVTYPE type){
		if(!pairedBPs.containsKey(cluster1)){
			pairedBPs.put(cluster1, new ArrayList<ClusterLink>());
		}
		if(!pairedBPs.containsKey(cluster2)){
			pairedBPs.put(cluster2, new ArrayList<ClusterLink>());
		}
		pairedBPs.get(cluster1).add(new ClusterLink(cluster2, type));
		pairedBPs.get(cluster2).add(new ClusterLink(cluster1, type));
	}
	
	private static boolean isOldLink(Hashtable<Cluster, ArrayList<ClusterLink>> pairedBPs, Cluster cluster1, Cluster cluster2){
		if(pairedBPs.containsKey(cluster1)){
			for(ClusterLink c: pairedBPs.get(cluster1)){
				if(cluster2 == c.link){
					//paired with this guy already
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		if(args.length < 1){
			System.err.println("Usage: <list of cluster pairs (DELLY output)>");
			System.exit(0);
		}
		
		BufferedReader input = new BufferedReader(new FileReader(args[0]));
		
		Hashtable<String,ArrayList<CoordinateClusterPairDelly>> clusters = new Hashtable<String, ArrayList<CoordinateClusterPairDelly>>();
		String line;
		
		ArrayList<ClusterDelly> allClusters = new ArrayList<ClusterDelly>();
		
		while ((line = input.readLine()) != null){
		
			if(line.startsWith("#"))
				continue;

			ClusterDelly c = new ClusterDelly(line);
			allClusters.add(c);
			
			if(!clusters.containsKey(c.c1))
				clusters.put(c.c1, new ArrayList<CoordinateClusterPairDelly>());
			if(!clusters.containsKey(c.c2))
				clusters.put(c.c2, new ArrayList<CoordinateClusterPairDelly>());
			clusters.get(c.c1).add(new CoordinateClusterPairDelly(c, c.p1));
			clusters.get(c.c2).add(new CoordinateClusterPairDelly(c, c.p2));

		}
		input.close();
		//System.out.println(allClusters.size());
		
		Hashtable<String, ArrayList<CoordinateClusterPairDelly>> sortedChrs = new Hashtable<String, ArrayList<CoordinateClusterPairDelly>>();
		for(String key : clusters.keySet()) {
			ArrayList<CoordinateClusterPairDelly> l = clusters.get(key);
			//System.out.println(key);
			//System.out.println(l);
			Collections.sort(l);
			sortedChrs.put(key, l);
		}
		
		int maxDistance = 15;
		ArrayList<ClusterNodeDelly> allNodes = new ArrayList<ClusterNodeDelly>();
		
		for(String key: sortedChrs.keySet()){
			ArrayList<CoordinateClusterPairDelly> l = sortedChrs.get(key);
			if(l.size() == 0)
				continue;
			
			System.out.println("investigating "+key+", with "+l.size()+" BPs");
			int lastPos = (maxDistance + 1)*-1;		
			ClusterNodeDelly currentNode = null;
			
			CoordinateClusterPairDelly cplast= l.get(0);//
			for(int i=0; i<l.size();i++){
				CoordinateClusterPairDelly cp = l.get(i);
				if(cp.coordinate - lastPos > maxDistance){
					currentNode = new ClusterNodeDelly();
					allNodes.add(currentNode);
					//System.out.println(i + " " +cp.coordinate + ":" +cp);
				}
				/*else{
					System.out.println((i-1) + " " +cplast.coordinate + " " +lastPos + ":" +cplast);
					System.out.println(i + " " +cp.coordinate + " " +lastPos + ":" +cp);
				}*/
				currentNode.addMember(cp.cluster);
				cp.cluster.addNode(currentNode, cp.coordinate);
				lastPos = cp.coordinate;
				cplast = cp;//
			}
		}
		
		//System.out.println(allNodes.size());
		
		ClusterNodeDelly.redundantMembers = new HashSet<ClusterDelly>();
		for (ClusterNodeDelly n: allNodes){
			n.checkMembers();
			for(ClusterDelly c: n.members){
				
				if(c.nodes[0] == n){
					n.addEdge(c.nodes[1],	 c);
				} else if (c.nodes[1] == n){
					n.addEdge(c.nodes[0],	 c);
				} else {
					System.err.println("No node link!");
				}
			}
			
			//System.out.println("Finished node with "+n.members.size()+" members and "+n.edges.size()+" links.");
		}
		
		System.out.println("Redundant clusters: "+ClusterNodeDelly.redundantMembers.size()); //REDUNDANT_MEMBERS
		
		for(String key: sortedChrs.keySet()){
			ArrayList<CoordinateClusterPairDelly> l = sortedChrs.get(key);
			if(l.size() == 0)
				continue;
			
			ClusterNodeDelly currentNode = null;
			for(int i=0; i<l.size();i++){
				CoordinateClusterPairDelly cp = l.get(i);
				ClusterNodeDelly node = cp.cluster.getNode(cp.coordinate);
				if(node==currentNode )
					continue;
				else
					currentNode = node;
				if(node.members.size() > 9)
					continue;
				
				//System.out.println(node.members.size());
				for(int v=0; v<node.members.size(); v++){
					ClusterDelly c1 = node.members.get(v);
					
					/*if (node.members.size()==1){//Simple inversion (Single Node)
						switch (classifyBreakpointDelly(c1)){
						case INV: {
							System.out.println(i +"-"+ v + " Inversion between "+c1.p1+" and "+c1.p2 +" : "+c1);
							break;
						}
//						case DEL: {
//							System.out.println(i +"-"+ v + " Deletion between "+c1.p1+" and "+c1.p2 +" : "+c1);
//							break;
//						}
//						case TAN: {
//							System.out.println(i +"-"+ v + " Tandem duplication between "+c1.p1+" and "+c1.p2 +" : "+c1);
//							break;
//						}
						}
						//System.out.println(i +"-"+ v + " Inversion between "+c1.p1+" and "+c1.p2 +" : "+c1);
					}*/
					
					for(int w=v+1; w<node.members.size(); w++){
					//for(int w=v; w<node.members.size(); w++){
						
						//ClusterDelly c1 = node.members.get(v);
						ClusterDelly c2 = node.members.get(w);
						if(ClusterNodeDelly.redundantMembers.contains(c1) || ClusterNodeDelly.redundantMembers.contains(c2) )
							continue; //REDUNDANT_MEMBERS
						
						if(node.edges.containsKey(node)){
							if(node.edges.get(node).contains(c1) || node.edges.get(node).contains(c2)){
								//deal with self edges...
								//System.out.println("Self reference");
								continue;
							}
						}
						
						switch (classifyBreakpointDelly(c1)){
						case INV: {
							if(classifyBreakpointDelly(c2)==SVTYPE.INV){//Complex Inversion (>1 Node)
								if(node.supportSameEdge(c1,c2)){
									//System.out.println(i +"-"+ v + " Complex inversion between "+c1+" and "+c2);
									System.out.println("Complex inversion between "+c1+" and "+c2);
									//addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.INV);
									//TODO: check for translocation or duplication
								} else {
									//TODO: merge the edges?
									if(node.supportSameEdge(c1,c2)){
										System.out.println("Merge edge for INV?");
									}
									//System.out.println(i +"-"+ v + " XXX inversion between "+c1+" and "+c2);
									System.out.println("Complex inversion between "+c1+" and "+c2);
								}
							}
							break;
						}
						case DEL: {
							if(classifyBreakpointDelly(c2)==SVTYPE.TAN){
								boolean connect = false;
								if(node.otherCoordinate(c1)<node.otherCoordinate(c2) && node.otherCoordinate(c1)>node.thisCoordinate(c2)){
									//System.out.println("Interspersed duplication at "+key+":"+cp.coordinate+" from "+cp.otherChromosome+":"+cp.otherCoordinate+"-"+node.otherCoordinate(c2));
									//addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.DUP);
									//TODO: check for translocation
									connect = true;
								} else if(node.otherCoordinate(c2)<node.otherCoordinate(c1) && node.otherCoordinate(c1)<node.thisCoordinate(c2)){
									System.out.println("Interspersed duplication at "+key+":"+node.thisCoordinate(c1)+" from "+c1.c1+":"+node.otherCoordinate(c2)+"-"+node.otherCoordinate(c1));
									System.out.println("\t"+c1);
									System.out.println("\t"+c2);
									connect = true;
								}
								if(connect ){
									ClusterNodeDelly otherNode1 = node.otherNode(c1), otherNode2 = node.otherNode(c2);
									if(otherNode1.edges.containsKey(otherNode2)){
										for(ClusterDelly c : otherNode1.edges.get(otherNode2)){
											if(classifyBreakpointDelly(c) == SVTYPE.DEL){
												System.out.println("Translocation found between\n\t"+c1+"\n\t"+c2+"\n\t"+c);
												break;
											}
										}
									}
								}
							}
							//else{
								//System.out.println("DEL "+c1+" and "+c2+":"+classifyBreakpointDelly(c2));
							//}
							break;
						}
						case TAN: {
							if(classifyBreakpointDelly(c2)==SVTYPE.DEL){
								boolean connect = false;
								if(node.otherCoordinate(c1)>node.otherCoordinate(c2) && node.otherCoordinate(c1)>node.thisCoordinate(c2)){
									//System.out.println("Interspersed duplication at "+key+":"+cp.coordinate+" from "+cp.otherChromosome+":"+node.otherCoordinate(c2)+"-"+node.otherCoordinate(c1));
									//addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.DUP);
									//TODO: check for translocation
									connect = true;
								} else if(node.otherCoordinate(c1)>node.otherCoordinate(c2) && node.otherCoordinate(c1)<node.thisCoordinate(c2)){
									System.out.println("Interspersed duplication at "+key+":"+cp.coordinate+" from "+cp.otherChromosome+":"+node.otherCoordinate(c2)+"-"+node.otherCoordinate(c1));
									connect = true;
								}
								if(connect ){
									ClusterNodeDelly otherNode1 = node.otherNode(c1), otherNode2 = node.otherNode(c2);
									if(otherNode1.edges.containsKey(otherNode2)){
										for(ClusterDelly c : otherNode1.edges.get(otherNode2)){
											if(classifyBreakpointDelly(c) == SVTYPE.DEL){
												System.out.println("Translocation found between\n\t"+c1+"\n\t"+c2+"\n\t"+c);
												break;
											}
										}
									}
								}
							}
							//else{
								//System.out.println("TAN "+c1+" and "+c2+":"+classifyBreakpointDelly(c2));
							//}
							break;
						}
						}
					}
				}
			}
		}
		System.out.println("Fin");
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main2(String[] args) throws IOException {
		
		if(args.length < 1){
			System.err.println("Usage: <list of cluster pairs (socrates output)>");
			System.exit(0);
		}
		
		BufferedReader input = new BufferedReader(new FileReader(args[0]));
		
		Hashtable<String,ArrayList<CoordinateClusterPair>> clusters = new Hashtable<String, ArrayList<CoordinateClusterPair>>();
		String line;
		
		ArrayList<Cluster> allClusters = new ArrayList<Cluster>();
		
		while ((line = input.readLine()) != null){
		
			if(line.startsWith("#"))
				continue;

			Cluster c = new Cluster(line);
			allClusters.add(c);
			
//			System.out.println(line);
//			System.out.println(c1+"\t"+pos1);
//			System.out.println(c2+"\t"+pos2);
//		
			if(!clusters.containsKey(c.c1))
				clusters.put(c.c1, new ArrayList<CoordinateClusterPair>());
			if(!clusters.containsKey(c.c2))
				clusters.put(c.c2, new ArrayList<CoordinateClusterPair>());
			clusters.get(c.c1).add(new CoordinateClusterPair(c, c.p1, c.o1));
			clusters.get(c.c2).add(new CoordinateClusterPair(c, c.p2, c.o2));
			
//			x++;
//			if(x>10)
//				break;
		}
		input.close();
		Hashtable<String, ArrayList<CoordinateClusterPair>> sortedChrs = new Hashtable<String, ArrayList<CoordinateClusterPair>>();
		for(String key : clusters.keySet()) {
			ArrayList<CoordinateClusterPair> l = clusters.get(key);
			Collections.sort(l);
			sortedChrs.put(key, l);
		}
//		for(CoordinateClusterPair c: l){
//			System.out.println(c.coordinate+"\t"+c.orientation);
//		}
		
		int maxDistance = 15;
		//Hashtable<CoordinateClusterPair, ArrayList<CoordinateClusterPair>> nodePairs = new Hashtable<CoordinateClusterPair, ArrayList<CoordinateClusterPair>>();

		int pairs = 0;
		
		Hashtable<Cluster, ArrayList<ClusterLink>> pairedBPs = new Hashtable<Cluster, ArrayList<ClusterLink>>();

		for(String key: sortedChrs.keySet()){
			ArrayList<CoordinateClusterPair> l = sortedChrs.get(key);
			if(l.size() == 0)
				continue;

			System.out.println("investigating chr "+key+", with "+l.size()+" BPs");
			for(int i=0; i<l.size();i++){
				CoordinateClusterPair cp = l.get(i);
				for(int j = i+1; j<l.size(); j++){	
					
					CoordinateClusterPair n = l.get(j);
					if(n.cluster==cp.cluster)
						continue;
					if(cp.distanceTo(n) <= maxDistance){
						pairs++;
						// establish old relationship of cluster pairs
						if(isOldLink(pairedBPs, cp.cluster, n.cluster))
							continue;
						switch (classifyBreakpoint(cp.cluster)){
						case INV: {
							if(classifyBreakpoint(n.cluster)==SVTYPE.INV){
								if(!cp.orientation.equals(n.orientation) && cp.otherDistanceTo(n) <=maxDistance){
									//System.out.println("Inversion between "+cp.cluster+" and "+n.cluster);
									addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.INV);
									//TODO: check for translocation or duplication
								} else {
								
								}
							}
							break;
						}
						case DEL: {
							if(classifyBreakpoint(n.cluster)==SVTYPE.TAN){
								if(cp.otherCoordinate<n.otherCoordinate && cp.otherCoordinate>n.coordinate
										|| cp.otherCoordinate>n.otherCoordinate && cp.otherCoordinate<n.coordinate){
									//System.out.println("Interspersed duplication at "+key+":"+cp.coordinate+" from "+cp.otherChromosome+":"+cp.otherCoordinate+"-"+n.otherCoordinate);
									addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.DUP);
									//TODO: check for translocation
								}
							}
							break;
						}
						case TAN: {
							if(classifyBreakpoint(n.cluster)==SVTYPE.DEL){
								if(cp.otherCoordinate>n.otherCoordinate && n.otherCoordinate>cp.coordinate
										|| cp.otherCoordinate<n.otherCoordinate && n.otherCoordinate<cp.coordinate){
									//System.out.println("Interspersed duplication at "+key+":"+cp.coordinate+" from "+cp.otherChromosome+":"+cp.otherCoordinate+"-"+n.otherCoordinate);
									addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.DUP);
									//TODO: check for translocation
								}
							}
							break;
						}
						default:
						}
						
//						if(cp.orientation.equals(n.orientation)){
//							addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.PROX);
//							//System.out.println("unpairable: ");
//							//System.out.println(cp);
//							//System.out.println(n);
//							continue;
//						}
//						
//						// establish old relationship of cluster pairs
//						if(isOldLink(pairedBPs, cp.cluster, n.cluster))
//							continue;
//						
//						if(cp.cluster.o1.equals(cp.cluster.o2)){
//							//inversion type
//							if(n.cluster.o1.equals(n.cluster.o2)){
//								if(cp.otherDistanceTo(n) <=maxDistance){
//									if(cp.cluster.c1.equals(cp.cluster.c2)){
//										System.out.println("Inversion between "+cp.cluster+" and "+n.cluster);
//										addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.INV);
//									}
//									else {
//										System.out.println("Balanced folding back of chromosome arms:");
//										System.out.println("\t"+cp);
//										System.out.println("\t"+n);
//										addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.INVTX);
//									}
//								} else{
//									//unresolved 
//								}
//							}
//						} else {
//							if(!n.cluster.o1.equals(n.cluster.o2)){
//								//Duplication signature
//								if(cp.sameChromosomes(n) && (cp.orientation.equals("+") && cp.otherCoordinate < n.otherCoordinate || cp.orientation.equals("-") && cp.otherCoordinate > n.otherCoordinate) ){
//									
//									System.out.println("Interspersed duplication at "+key+":"+cp.coordinate+" from "+cp.otherChromosome+":"+cp.otherCoordinate+"-"+n.otherCoordinate);
//									addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.DUP);
//									
//								}
//							}
//						}
//					}else
//						break;

					} else
						break;
				}
			}
		}
		System.out.println("investigated pairings: "+pairs);
		
		for(Cluster c: pairedBPs.keySet()){
			ArrayList<ClusterLink> partners = pairedBPs.get(c);
			if(partners.size() == 1){
				ClusterLink partner = partners.get(0);
				if(pairedBPs.get(partner.link).size()==1){
					System.out.println("Complex event of type "+partner.type+" between \n\t"+c+"\n\t"+partner.link);
				} else {
					System.out.println("Investigate other event first");
				}
			} else if(partners.size() == 2){
				if(partners.get(0).type == SVTYPE.DUP && partners.get(1).type == SVTYPE.DUP){
					
					System.out.println("Translocation event between \n\t"+c+"\n\t"+partners.get(0).link+"\n\t"+partners.get(1).link);
				}
			}
		}

		
	}
	
	public static void mainSocrates(String[] args) throws IOException {
		
		if(args.length < 1){
			System.err.println("Usage: <list of cluster pairs (socrates output)>");
			System.exit(0);
		}
		
		BufferedReader input = new BufferedReader(new FileReader(args[0]));
		
		Hashtable<String,ArrayList<CoordinateClusterPair>> clusters = new Hashtable<String, ArrayList<CoordinateClusterPair>>();
		String line;
		
		ArrayList<Cluster> allClusters = new ArrayList<Cluster>();
		
		while ((line = input.readLine()) != null){
		
			if(line.startsWith("#"))
				continue;

			Cluster c = new Cluster(line);
			allClusters.add(c);
	
			if(!clusters.containsKey(c.c1))
				clusters.put(c.c1, new ArrayList<CoordinateClusterPair>());
			if(!clusters.containsKey(c.c2))
				clusters.put(c.c2, new ArrayList<CoordinateClusterPair>());
			clusters.get(c.c1).add(new CoordinateClusterPair(c, c.p1, c.o1));
			clusters.get(c.c2).add(new CoordinateClusterPair(c, c.p2, c.o2));

		}
		input.close();
		//System.out.println(allClusters.size());
		
		Hashtable<String, ArrayList<CoordinateClusterPair>> sortedChrs = new Hashtable<String, ArrayList<CoordinateClusterPair>>();
		for(String key : clusters.keySet()) {
			ArrayList<CoordinateClusterPair> l = clusters.get(key);
			Collections.sort(l);
			sortedChrs.put(key, l);
		}
		
		int maxDistance = 15;
		ArrayList<ClusterNode> allNodes = new ArrayList<ClusterNode>();

		for(String key: sortedChrs.keySet()){
			ArrayList<CoordinateClusterPair> l = sortedChrs.get(key);
			if(l.size() == 0)
				continue;

			System.out.println("investigating chr "+key+", with "+l.size()+" BPs");
			int lastPos = (maxDistance + 1)*-1;		
			ClusterNode currentNode = null;
			
			for(int i=0; i<l.size();i++){
				CoordinateClusterPair cp = l.get(i);
				if(cp.coordinate - lastPos > maxDistance){
					currentNode = new ClusterNode();
					allNodes.add(currentNode);
				}
				currentNode.addMember(cp.cluster);
				cp.cluster.addNode(currentNode, cp.coordinate);
				lastPos = cp.coordinate;
			}
		}
		
		ClusterNode.redundantMembers = new HashSet<Cluster>();
		for (ClusterNode n: allNodes){
			n.checkMembers();
			for(Cluster c: n.members){
				
				if(c.nodes[0] == n){
					n.addEdge(c.nodes[1],	 c);
				} else if (c.nodes[1] == n){
					n.addEdge(c.nodes[0],	 c);
				} else {
					System.err.println("No node link!");
				}
			}
			
			//System.out.println("Finished node with "+n.members.size()+" members and "+n.edges.size()+" links.");
		}
		
		System.out.println("Redundant clusters: "+ClusterNode.redundantMembers.size());

		for(String key: sortedChrs.keySet()){
			ArrayList<CoordinateClusterPair> l = sortedChrs.get(key);
			if(l.size() == 0)
				continue;
			
			ClusterNode currentNode = null;
			for(int i=0; i<l.size();i++){
				CoordinateClusterPair cp = l.get(i);
				ClusterNode node = cp.cluster.getNode(cp.coordinate);
				if(node==currentNode )
					continue;
				else
					currentNode = node;
				if(node.members.size() > 9)
					continue;
				
				for(int v=0; v<node.members.size(); v++){
					//System.out.println(i +"-"+ v + " Inversion between "+node.members.get(v).p1+" and "+node.members.get(v).p2 );
					for(int w=v+1; w<node.members.size(); w++){
						
						Cluster c1 = node.members.get(v);
						Cluster c2 = node.members.get(w);
						if(ClusterNode.redundantMembers.contains(c1) || ClusterNode.redundantMembers.contains(c2) )
							continue;
						
						if(node.edges.containsKey(node)){
							if(node.edges.get(node).contains(c1) || node.edges.get(node).contains(c2)){
								//deal with self edges...
								//System.out.println("Self reference");
								continue;
							}
						}
						
						switch (classifyBreakpoint(c1)){
						case INV: {
							if(classifyBreakpoint(c2)==SVTYPE.INV){
								if(!c1.o1.equals(c2.o1) && node.supportSameEdge(c1,c2)){
									System.out.println("Inversion between "+c1+" and "+c2);
									//addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.INV);
									//TODO: check for translocation or duplication
								} else {
									//TODO: merge the edges?
									if(node.supportSameEdge(c1,c2)){
										System.out.println("Merge edge for INV?");
									}
								}
							}
							break;
						}
						case DEL: {
							if(classifyBreakpoint(c2)==SVTYPE.TAN){
								boolean connect = false;
								if(node.otherCoordinate(c1)<node.otherCoordinate(c2) && node.otherCoordinate(c1)>node.thisCoordinate(c2)){
									//System.out.println("Interspersed duplication at "+key+":"+cp.coordinate+" from "+cp.otherChromosome+":"+cp.otherCoordinate+"-"+node.otherCoordinate(c2));
									//addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.DUP);
									//TODO: check for translocation
									connect = true;
								} else if(node.otherCoordinate(c2)<node.otherCoordinate(c1) && node.otherCoordinate(c1)<node.thisCoordinate(c2)){
									System.out.println("Interspersed duplication at "+key+":"+node.thisCoordinate(c1)+" from "+c1.c1+":"+node.otherCoordinate(c2)+"-"+node.otherCoordinate(c1));
									System.out.println("\t"+c1);
									System.out.println("\t"+c2);
									connect = true;
								}
								if(connect ){
									ClusterNode otherNode1 = node.otherNode(c1), otherNode2 = node.otherNode(c2);
									if(otherNode1.edges.containsKey(otherNode2)){
										for(Cluster c : otherNode1.edges.get(otherNode2)){
											if(classifyBreakpoint(c) == SVTYPE.DEL){
												System.out.println("Translocation found between\n\t"+c1+"\n\t"+c2+"\n\t"+c);
												break;
											}
										}
									}
								}
							}
							break;
						}
						case TAN: {
							if(classifyBreakpoint(c2)==SVTYPE.DEL){
								boolean connect = false;
								if(node.otherCoordinate(c1)>node.otherCoordinate(c2) && node.otherCoordinate(c1)>node.thisCoordinate(c2)){
									//System.out.println("Interspersed duplication at "+key+":"+cp.coordinate+" from "+cp.otherChromosome+":"+node.otherCoordinate(c2)+"-"+node.otherCoordinate(c1));
									//addClusterLink(pairedBPs, cp.cluster, n.cluster, SVTYPE.DUP);
									//TODO: check for translocation
									connect = true;
								} else if(node.otherCoordinate(c1)>node.otherCoordinate(c2) && node.otherCoordinate(c1)<node.thisCoordinate(c2)){
									System.out.println("Interspersed duplication at "+key+":"+cp.coordinate+" from "+cp.otherChromosome+":"+node.otherCoordinate(c2)+"-"+node.otherCoordinate(c1));
									connect = true;
								}
								if(connect ){
									ClusterNode otherNode1 = node.otherNode(c1), otherNode2 = node.otherNode(c2);
									if(otherNode1.edges.containsKey(otherNode2)){
										for(Cluster c : otherNode1.edges.get(otherNode2)){
											if(classifyBreakpoint(c) == SVTYPE.DEL){
												System.out.println("Translocation found between\n\t"+c1+"\n\t"+c2+"\n\t"+c);
												break;
											}
										}
									}
								}
							}
							break;
						}
						}
					}
				}
			}
			System.out.println("Fin-Socrates");
		}
	}
	
}

