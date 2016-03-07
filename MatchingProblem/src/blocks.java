import java.util.ArrayList;


public class blocks {

	private int[] arc;
	private int parent;
	private ArrayList<trainType> types;
	private int time;
	private int track;
	private double activityTime;
	private double inspectionTime;
	private double washingTime;
	private double cleaningTime;
	private double repairTime;
	private int length;

	
public blocks(int[] arc, int parent, ArrayList<trainType> types, int time, int track, double activityTime,  double inspectionTime, double cleaningTime, double washingTime, double repairTime, int length){
		this.arc=arc;
		this.parent =parent;
		this.types = types;
		this.time = time;
		this.track = track;
		this.activityTime = activityTime;
		this.washingTime = washingTime;
		this.inspectionTime = inspectionTime;
		this.cleaningTime = cleaningTime;
		this.repairTime = repairTime;
		this.length = length;
	}
	
	public int[] getArc(){
		return arc;
	}
	
	public int getLength(){
		return length;
	}
	
	public double getActivityTime(){
		return activityTime;
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
	
	public double getWashingTime(){
		return washingTime;
	}
	
	public double getCleaningTime(){
		return cleaningTime;
	}
	
	public double getRepairTime(){
		return repairTime;
	}
	
	public double getInspectionTime(){
		return inspectionTime;
	}
}