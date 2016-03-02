import java.util.ArrayList;

public class Track {
	public double trackLength;
	public boolean washTrack;
	public boolean platformTrack;
	public boolean ADTrack;
	public ArrayList<Train> trainTrack; //Contains the trains that are on this track in a certain order. From front to end. (important to define front and back of each track)
	public double idleSpace;
	public boolean front;
	public boolean back;
	public Integer[] connections; //1. From front of this track to front of other track, 2. from front of this track to back other, 3. from back of this track to front of other, 4. from back of this track to back of other 



	public Track(double trackLength, boolean washTrack, boolean platformTrack, boolean ADTrack,Integer[] connections, ArrayList<Train> trainTrack){
		this.trackLength=trackLength;
		this.washTrack=washTrack;
		this.platformTrack=platformTrack;
		this.trainTrack=trainTrack;
		this.ADTrack=ADTrack;
		this.connections=connections;
	}

	public void remove(Train toRemove){ //finds the train and removes it from this track
		trainTrack.remove(toRemove);
	}

	public void addFront(Train toAdd){ //adds the train to the front of the arrayList
		trainTrack.add(0,toAdd); 
	}

	public void addBack(Train toAdd){ //adds the train to the end (back) of the arrayList
		trainTrack.add(toAdd);
	}
	
	public int getConnection(int i){ //returns the integer in the connections vector of this track
		return connections[i];
	}

	public double getLength(){ //returns the length of the track
		return trackLength;
	}

	public boolean getWash(){ // returns true if this track is a washing machine
		return washTrack;
	}

	public boolean getPlatform(){ //returns true if this track is a platform
		return platformTrack;
	}
	
	public boolean getAD(){
		return ADTrack;
	}

	public ArrayList<Train> getTrains(){ //returns the arrayList containing all trains on this track
		return trainTrack;
	}

	public int getNTrains(){ //returns the number of trains on this track
		return trainTrack.size();
	}

	public Train getTrain(int i){ //returns the train on a certain position on this track
		return trainTrack.get(i);
	}

	public double getIdle(){//returns the idle space left on this track
		double fullSpace=0;
		for (int i=0;i<trainTrack.size();i++){
			fullSpace = fullSpace + trainTrack.get(i).getType().getLength();
		}
		idleSpace = trackLength - fullSpace;
		return idleSpace;
	}

	public boolean inFront(Train toCheck){ //returns true if train is in the front
		if (trainTrack.get(0)==toCheck)
			front = true;
		else
			front=false;
		return front;
	}

	public boolean inBack(Train toCheck){ //returns true if train is in the back
		if (trainTrack.get(trainTrack.size())==toCheck)
			back=true;
		else
			back=false;
		return back;
	}

}
