import java.util.ArrayList;


public class blocks {

	private int[] arc;
	private int parent;
	private ArrayList<trainType> types;
	private int time;
	private int track;

	
	public blocks(int[] arc, int parent, ArrayList<trainType> types, int time, int track){
		this.arc=arc;
		this.parent =parent;
		this.types = types;
		this.time = time;
		this.track = track;
	}
	
	public int[] getArc(){
		return arc;
	}
	
	public int getParent(){
		return parent;
	}

	public int getTrack(){
		return track;
	}
	
	public void printBlock(){
		System.out.print("Arc (" + arc[0] + "," + arc[1] + ") ");
		System.out.print("Parent " + parent + " ");
		for(int i=0;i<types.size();i++){
		System.out.print("Type " + types.get(i).getLength()+ " ");
		}
		System.out.println();
	}
	
	public ArrayList<trainType> getTypes(){
		return types;
	}
	
	public int getTime(){
		return time;
	}
}