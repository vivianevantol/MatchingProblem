
public class Train {
	private int trainID;
	private trainType trainType;
	private boolean interchangable;
	private boolean inspect;
	private boolean repair;
	private boolean clean;
	private boolean wash;
	private int arrivalminute;

	public Train(int ID, trainType type, boolean interchangable, boolean inspect, boolean repair, boolean clean, boolean wash, int arrivalminute){
		this.trainID = ID;
		this.trainType = type;
		this.interchangable = interchangable;
		this.inspect = inspect;
		this.repair = repair;
		this.clean = clean;
		this.wash = wash;
		this.arrivalminute = arrivalminute;
	}

	public int getID(){
		return trainID;
	}
	
	public trainType getType(){
		return trainType;
	}
	
	public boolean getInterchangable(){
		return interchangable;
	}
	
	public boolean getInspect(){
		return inspect;
	}
	
	public boolean getRepair(){
		return repair;
	}
	
	public boolean getClean(){
		return clean;
	}
	
	public boolean getWash(){
		return wash;
	}
	
	public int getArrivalminute(){
		return arrivalminute;
	}
	public void setInterchangable(boolean x){
		this.interchangable = x;
	}
	
	public void setInspect(boolean x){
		this.inspect = x;
	}
	
	public void setRepair(boolean x){
		this.repair = x;
	}
	
	public void setClean(boolean x){
		this.clean = x;
	}
	
	public void setWash(boolean x){
		this.wash = x;
	}

}
