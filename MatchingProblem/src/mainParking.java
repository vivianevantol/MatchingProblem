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
		
//===========INITIALIZE DATA================================================================================================
		
		int[][] departures = eventList.getDeparturelist();
		int[][] arrivals = eventList.getArrivallist();
		int nTracks = 8; //input!!
		int nTrains = 22;
		int nIterations = 5;
		
		
		int[][] trainInfo = new int[nTrains][4]; //ID Atime Dtime Length //22 is number of trains!!
		for (int x=0;x<trainInfo.length;x++){ 
			trainInfo[x][0] = departures[x][1];
			trainInfo[x][1] = arrivals[x][0];
			trainInfo[x][2] = departures[x][0];
			trainInfo[x][3] = (int) getLength(departures[x][1], data);
		}
		ArrayList<Track> tracks = yard.getTracks();
		int[][] trackInfo = new int[nTracks][3]; //lengt left right
		for(int s=1;s<1+nTracks;s++){ //the departure tracks!!
			trackInfo[s-1][0] = (int) tracks.get(s).getLength(); 
			trackInfo[s-1][1] = 0;
			trackInfo[s-1][2] = 0;
		}
		
//===========BUILD LIST SHOWING POSSIBLE ARRIVAL AND DEPARTURE SIDES=======================================================
		int[][][] sides = new int[trackInfo.length][trainInfo.length][4]; //LL LR RL RR
		for(int b=0;b<trainInfo.length;b++){
			for(int s=0;s<trackInfo.length;s++){
				if(departures[b][2]==906){
					sides[s][b][1]=1; //LR
					sides[s][b][0]=1; //LL
				} else if(departures[b][2]==104){
					sides[s][b][0]=1;//LL
				} else {
					System.out.println("Error tracks");
				}
			}
		}

		//ArrayList<ArrayList<Integer>> allA, int Tracks, int Trains, int[] AssTrack, int[][] AssTrackTrain, int[][] trainInfo
		ArrayList<ArrayList<Integer>> allA =  new ArrayList<ArrayList<Integer>>();
		
		int[] AssTrack = new int[trackInfo.length];
		int[][] AssTrackTrain = new int[trackInfo.length][trainInfo.length];
		
//==============INITIALIZE SET COVERING PROBLEM======================================================================
		System.out.println("Initialization model===========================================================");
		ParkingSetCovering ParkingInitialize = new ParkingSetCovering(true, allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
//		ParkingSetCovering ParkingMIPinitialize = new ParkingSetCovering(false, allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
		double[] output = ParkingInitialize.getDuals();
//		int[] outputMIP = ParkingMIPinitialize.getPenalty();
		int[] Ub = new int[trainInfo.length];
		int[] Vs = new int[trackInfo.length];
		for(int b=0;b<Ub.length;b++){
			Ub[b] = -(int) output[b];
		}
//		for(int b=0;b<Ub.length;b++){
//			if(outputMIP[b]==1){
//				Ub[b] = -1;
		
//			} else {
//				Ub[b] = 1;
//			}
//		}
		for(int s=0;s<Vs.length;s++){
			Vs[s] = -(int) output[Ub.length+s];
//			if(output[Ub.length+s]<0){allPos = false;}
		}
		
		System.out.println("UB");
		printArray(Ub);
		System.out.println("VS");
		printArray(Vs);
		
		boolean allPos = false; //Checks wheter all duals are positive
		int iterations = 1;
		while(allPos==false){
			System.out.println("=========================================================================================");
			System.out.println("Iteration " + iterations);
//==============CREATE THE MOST VALUABLE ASSIGNMENT=========================================================================
			int trys=0;
			int tryb=0;
			boolean foundnew = false;
			iterations++;
			ArrayList<Integer> assignment = new ArrayList<Integer>();
			while(foundnew==false && tryb<(trainInfo.length)){  //try all trains
				trys = 0;
				while(foundnew==false && trys<(trackInfo.length)){ //try all tracks
					assignment.clear();
					CreateAssignment AssignmentObject = new CreateAssignment(sides, tryb, trys, Ub, Vs, eventList, trainInfo, trackInfo);
					assignment = AssignmentObject.getCreation(); //first is track rest is trainNR
					//				System.out.println("Assignment created for track: " + assignment.get(0) );
					//				printList(assignment);
					if(!allA.contains(assignment)){
						System.out.println("New assignment");
						printList(assignment);
						foundnew = true;
					}
					trys++;
				}
				tryb++;
			}
			if(foundnew){
				allA.add(assignment);
				AssTrack[assignment.get(0)] = AssTrack[assignment.get(0)]+1;
				for(int b=0;b<trainInfo.length;b++){
					if(inAssignment(assignment, b)){
						AssTrackTrain[assignment.get(0)][b] = AssTrackTrain[assignment.get(0)][b]+1;
					}
				}
			}
			
			//CHEAT FOR ITERATIONS INPUT========================
			if(iterations>=nIterations){
				allPos = true;
			}


//=====RUN THE PARKING SET COVERING PROBLEM=======================================================================
			if(!allPos){
	
			ParkingSetCovering Parking = new ParkingSetCovering(true, allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
//				ParkingSetCovering ParkingMIP = new ParkingSetCovering(false, allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
				output = Parking.getDuals();
//				outputMIP = ParkingMIP.getPenalty();
				for(int b=0;b<Ub.length;b++){
					Ub[b] = -(int) output[b];
				}
//				for(int b=0;b<Ub.length;b++){
//					if(outputMIP[b]==1){
//						Ub[b] = -1;
//					} else {
//						Ub[b] = 1;
//					}
//				}
				for(int s=0;s<Vs.length;s++){
					Vs[s] = -(int) output[Ub.length+s];
					//					if(output[Ub.length+s]<0){allPos = false;}
				}
				
				System.out.println("UB");
				printArray(Ub);
				System.out.println("VS");
				printArray(Vs);
			}
				

		}

		System.out.println();
		System.out.println("All assignments");
		for(int i=0;i<allA.size();i++){
			printList(allA.get(i));
		}
		
		System.out.println();
		System.out.println("Out of while");
		System.out.println("Final MIP model===========================================================");
		ParkingSetCovering ParkingFinal = new ParkingSetCovering(false, allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
		int[] Assignments = ParkingFinal.getAssignments();
		int[] Penalties = ParkingFinal.getPenalty();
		
//		printArray(Assignments);
//		printArray(Penalties);
		
	}
	public static boolean inAssignment(ArrayList<Integer> assignment, int trainID){
		ArrayList<Integer> trains = new ArrayList<Integer>();
		for(int i=1;i<assignment.size();i++){
			trains.add(assignment.get(i)); //dont take track into account
		}
		boolean check = false;
		for(int i=0;i<trains.size();i++){
			if(trains.get(i)==trainID){
				check = true;
			}
		}
		return check;
	}
	public static int[] getMin(int[] x, int trys){
//		int[] y= new int[x.length];
//		for (int i=0;i<x.length;i++){
//			y[i] = x[i];
//		}
//		int now = -1;
//		
//		int minvalue = 800000;
//		int index = -1;
//		while(now!=trys){
//			now++;
//			minvalue = Integer.MAX_VALUE;
//			index = -1;
//			for (int i=0;i<x.length;i++){
//				if(x[i]<minvalue){
//					index = i;
//					minvalue = x[i];
//				}
//			}
//			x[index] = Integer.MAX_VALUE;
//		}
//		int[] yy = {index, minvalue};
//		return yy;
		int[] y= new int[x.length];
		for (int i=0;i<x.length;i++){
			y[i] = x[i];
		}
		int now = -1;
		
		int minvalue = -800000;
		int index = -1;
		while(now!=trys){
			now++;
			minvalue = -Integer.MAX_VALUE;
			index = -1;
			for (int i=0;i<x.length;i++){
				if(x[i]>minvalue){
					index = i;
					minvalue = x[i];
				}
			}
			x[index] = -Integer.MAX_VALUE;
		}
		int[] yy = {index, minvalue};
		return yy;
	}
	
	public static void printList(ArrayList<Integer> p){
		for (int i=0;i<p.size();i++){
			System.out.print(p.get(i) + " ");
		}
		System.out.println("");
	}
	
	public static void printDoubleArray(int[][] printer){
		for (int i=0;i<printer.length;i++){
			for(int j=0;j<printer[0].length;j++){
				System.out.print(printer[i][j] + "  " );
			}
			System.out.println();
		}
	}
	
	public static void printDoubleArray(double[][] printer){
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
	
//	TEST MET 4 BEGIN ASSIGNMENTS OM TE KIJKEN OF HET WERKT=========================================
//	ArrayList<Integer> assignment = new ArrayList<Integer>();
//	assignment.add(0);
//	assignment.add(0);
//	assignment.add(1);
//	assignment.add(4);
//	assignment.add(6);
//	assignment.add(7);
//	allA.add(assignment);
//	AssTrack[assignment.get(0)] = AssTrack[assignment.get(0)]+1;
//	for(int b=0;b<trainInfo.length;b++){
//		if(inAssignment(assignment, b)){
//			AssTrackTrain[assignment.get(0)][b] = AssTrackTrain[assignment.get(0)][b]+1;
//		}
//	}
//	ArrayList<Integer> assignment2 = new ArrayList<Integer>();
//	assignment2.add(1);
//	assignment2.add(8);
//	assignment2.add(9);
//	assignment2.add(13);
//	allA.add(assignment2);
//	AssTrack[assignment2.get(0)] = AssTrack[assignment2.get(0)]+1;
//	for(int b=0;b<trainInfo.length;b++){
//		if(inAssignment(assignment2, b)){
//			AssTrackTrain[assignment2.get(0)][b] = AssTrackTrain[assignment2.get(0)][b]+1;
//		}
//	}
//	ArrayList<Integer> assignment3 = new ArrayList<Integer>();
//	assignment3.add(2);
//	assignment3.add(14);
//	assignment3.add(17);
//	allA.add(assignment3);
//	AssTrack[assignment3.get(0)] = AssTrack[assignment3.get(0)]+1;
//	for(int b=0;b<trainInfo.length;b++){
//		if(inAssignment(assignment3, b)){
//			AssTrackTrain[assignment3.get(0)][b] = AssTrackTrain[assignment3.get(0)][b]+1;
//		}
//	}
//	ArrayList<Integer> assignment4 = new ArrayList<Integer>();
//	assignment4.add(3);
//	assignment4.add(18);
//	assignment4.add(19);
//	assignment4.add(20);
//	allA.add(assignment4);
//	AssTrack[assignment4.get(0)] = AssTrack[assignment4.get(0)]+1;
//	for(int b=0;b<trainInfo.length;b++){
//		if(inAssignment(assignment4, b)){
//			AssTrackTrain[assignment4.get(0)][b] = AssTrackTrain[assignment4.get(0)][b]+1;
//		}
//	}
//	allPos = true;
}
