import java.util.ArrayList;

public class trainComposition {
	private ArrayList<Train> trains = new ArrayList<Train>();
	private ArrayList<trainType> types = new ArrayList<trainType>();
	private int ID;
	private boolean arrival;
	private int time;
	private boolean t906a;

	public trainComposition(ArrayList trains, ArrayList types, int ID, boolean arrival, int time, boolean t906a){
		this.trains = trains;
		this.ID = ID;
		this.arrival = arrival;
		this.time = time;
		this.types = types;
		this.t906a = t906a;
	}
	
	public boolean getArrival(){
		return arrival;
	}
	
	public int getTime(){
		return time;
	}
	
	public int getID(){
		return ID;
	}
	
	public int getNumber(){
		return trains.size();
	}
	
	public double getLength(){
		double length = 0;
		for (int i=0; i<types.size() ;i++){
			length = length + types.get(i).getLength();
		}
		return length;
	}
	
	public boolean get906a(){
		return t906a;
	}
	
	public void addTrain(Train x){
		trains.add(x);
	}
	
	public void removeTrain(Train x){
		trains.remove(x);
	}
	
	public ArrayList<Train> getTrains(){
		return trains;
	}
	
	public ArrayList<trainType> getTypes(){
		return types;
	}
	
	public void setTypes(ArrayList<trainType> x){
		this.types.clear();
		for (int i=0;i<x.size();i++){
			this.types.set(i, x.get(i));
		}
	}
}
