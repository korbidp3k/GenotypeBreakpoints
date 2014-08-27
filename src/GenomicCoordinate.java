
class GenomicCoordinate implements Comparable<GenomicCoordinate>{
	private String chr;
	private int pos;
	public GenomicCoordinate(String chr, int pos){
		this.chr = chr;
		this.pos = pos;
	}
	@Override
	public int compareTo(GenomicCoordinate other) {
		if(other.chr.equals(this.chr)){
			if(this.pos < other.pos)
				return -1;
			if(this.pos > other.pos)
				return 1;
			return 0;
		}
		return 0;
	}
	public String getChr() {
		return chr;
	}
	public int getPos() {
		return pos;
	}
	public boolean onSameChromosome(GenomicCoordinate other){
		if (this.chr.equals(other.chr))
			return true;
		return false;
	}
}