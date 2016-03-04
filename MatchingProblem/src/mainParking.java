import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ilog.concert.IloException;

public class mainParking {

	public static void main(String[] args) throws FileNotFoundException, IOException, IloException {
		// TODO Auto-generated method stub
		initializeData data = new initializeData(); //create the data set
		InitializeShuntingYard yard = new InitializeShuntingYard(); //create the shunting yard
		initializeEventList eventList = new initializeEventList(); //create the eventlist

		int[][] Dep =eventList.getArrivallist();
		int[] IDx = new int[Dep.length];
		int count=0;
		for(int i=0;i<Dep.length;i++){
			if(Dep[i][1]!=0){
				IDx[i] = Dep[i][1];
				count++;
			}
		}
		int[] ID = Arrays.copyOfRange(IDx, 0, count);
		int nTracks = 4;
		
		int[][] departures = eventList.getDeparturelist();
		int[][] arrivals = eventList.getArrivallist();
		int[][] trainInfo = new int[departures.length][4]; //ID Atime Dtime Length
		for (int x=0;x<trainInfo.length;x++){
			trainInfo[x][0] = departures[x][1];
			trainInfo[x][1] = arrivals[x][0];
			trainInfo[x][2] = departures[x][0];
			trainInfo[x][3] = (int) getLength(departures[x][1], data);
		}
		ArrayList<Track> tracks = yard.getTracks();
		int[][] trackInfo = new int[tracks.size()][3]; //lengt left right
		for(int s=0;s<tracks.size();s++){
			trackInfo[s][0] = (int) tracks.get(s).getLength();
			trackInfo[s][1] = 0;
			trackInfo[s][2] = 0;
		}
		
		ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> K = new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>();
		for(int s=0;s<nTracks;s++){
			K.add(new ArrayList<ArrayList<ArrayList<Integer>>>()); //add the trainlists
			for(int b=0;b<ID.length;b++){
				K.get(s).add(new ArrayList<ArrayList<Integer>>());
			}
		}
		
		
		//=====RUN THE PARKING SET COVERING PROBLEM=======================================================================
		ParkingSetCovering Parking = new ParkingSetCovering(K, ID);
		double[] output = Parking.getOutput();
//		printArray(output);
	}
	
	public static void printDoubleArray(int[][] printer){
		for (int i=0;i<printer.length;i++){
			for(int j=0;j<printer[0].length;j++){
				System.out.print(printer[i][j] + "  " );
			}
			System.out.println();
		}
	}
	
	public static void printArray(int[] printer){
		for (int i=0;i<printer.length;i++){
				System.out.print(printer[i] + "  " );
			System.out.println();
		}
	}
	
	public static void printArray(double[] printer){
		for (int i=0;i<printer.length;i++){
				System.out.print(printer[i] + "  " );
			System.out.println();
		}
	}

	public static double getLength(int id, initializeData data){
		String x = Integer.toString(id);
		double length=0;
		if (x.length() == 5){
			ArrayList<trainComposition> comp = data.getCompositions();
			for (int i = 0; i< comp.size(); i++){
				if (comp.get(i).getID()==id){
					length = comp.get(i).getLength();
				}
			}
		}
		return length;
	}
}
