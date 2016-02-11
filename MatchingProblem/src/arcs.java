public class arcs {
	private int[] arc;
	
	public arcs(int[] x){
		this.arc = x;
	}
	
	public void printArc(){
		System.out.println("Arc (" + arc[0] + "," + arc[1] + ") ");
	}
	
	public int[] getArc(){
		return arc;
	}
}