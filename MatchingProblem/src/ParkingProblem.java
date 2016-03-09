import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ilog.concert.IloException;

public class ParkingProblem {
	private int[][] outputID;

	public ParkingProblem(int[][] blockInfo, int it, InitializeShuntingYard yard, initializeEventList eventList) throws IOException, IloException{
		outputID = solveParking(blockInfo, it, yard, eventList);
	}

	public int[][] solveParking(int[][] blockInfo, int it, InitializeShuntingYard yard, initializeEventList eventList) throws IOException, IloException{
		//===========PARKING INITIALIZE DATA===========================================================================================
		int[][] departures = eventList.getDeparturelist();
		int nIterations = it;

		for(int i=0;i<blockInfo.length;i++){ //arrival + 2 + inspection + 2 + cleaning + repair + (2 + washing) + 2
			blockInfo[i][3] = blockInfo[i][3]+ 2 + blockInfo[i][7] + 2 + blockInfo[i][8] + blockInfo[i][10]+ 2;
			if(blockInfo[i][9]>0){
				blockInfo[i][3] = blockInfo[i][3]  + 2 + blockInfo[i][9];
			}
		}
		ArrayList<Track> tracks = yard.getTracks();
		
		int nTracks = 4; //input!!
		int nTracks2 = 6; //input!!

		//=====FOR THE FIRST 7 TRAINS==================================================================================================

		int nTrains = 7;
		

		
		int[][] trackInfo = new int[nTracks][3]; //lengt left right
		for(int s=1;s<1+nTracks;s++){ //the departure tracks!!
			trackInfo[s-1][0] = (int) tracks.get(s).getLength(); 
			trackInfo[s-1][1] = 0;
			trackInfo[s-1][2] = 0;
		}
		//==============PARKING INITIALIZE SET COVERING PROBLEM========================================================================
		int[][] trainInfo = new int[nTrains][4]; //ID Atime Dtime Length //22 is number of trains!!
		for (int x=0;x<trainInfo.length;x++){ 
			trainInfo[x][0] = blockInfo[x][0]; //id
			trainInfo[x][1] = blockInfo[x][3]; //atime
			trainInfo[x][2] = blockInfo[x][4]; //dtime
			trainInfo[x][3] = blockInfo[x][11]; //length
		}
		//===========PARKING BUILD SIDES===============================================================================================
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

		ArrayList<ArrayList<Integer>> allA =  new ArrayList<ArrayList<Integer>>();

		int[] AssTrack = new int[trackInfo.length];
		int[][] AssTrackTrain = new int[trackInfo.length][trainInfo.length];

		System.out.println("Initialization parking model===========================================================");
		ParkingSetCoveringRelaxed ParkingInitialize = new ParkingSetCoveringRelaxed(allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
		ParkingSetCoveringMIP ParkingMIPinitialize = new ParkingSetCoveringMIP(allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
		double[] output = ParkingInitialize.getDuals();
		int[] outputMIP = ParkingMIPinitialize.getPenalty();
		int[] Ub = new int[trainInfo.length];
		int[] Vs = new int[trackInfo.length];
//		for(int b=0;b<Ub.length;b++){
//			if(outputMIP[b]==1){
//				Ub[b] = -1;
//			} else {
//				Ub[b] = 1;
//			}
//		}
		for(int b=0;b<Ub.length;b++){
			Ub[b] = -(int) output[b];
		}
		for(int s=0;s<Vs.length;s++){
			Vs[s] = -(int) output[Ub.length+s];
		}

		boolean allPos = false; //Checks wheter all duals are positive
		int iterations = 1;
		while(allPos==false){
			System.out.println("=========================================================================================");
			System.out.println("Iteration " + iterations);
			//==============PARKING CREATE ASSIGNMENT======================================================================================
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
					if(!allA.contains(assignment)){
						//								printList(assignment);
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


			//=====RUN THE PARKING SET COVERING PROBLEM=======================================================================
			ParkingSetCoveringRelaxed Parking = new ParkingSetCoveringRelaxed(allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
			ParkingSetCoveringMIP ParkingMIP = new ParkingSetCoveringMIP(allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
			output = Parking.getDuals();
			outputMIP = ParkingMIP.getPenalty();
//			for(int b=0;b<Ub.length;b++){
//				if(outputMIP[b]==1){
//					Ub[b] = -1;
//				} else {
//					Ub[b] = 1;
//				}
//			}
			for(int b=0;b<Ub.length;b++){
				Ub[b] = -(int) output[b];
			}
			for(int s=0;s<Vs.length;s++){
				Vs[s] = -(int) output[Ub.length+s];
			}

			//================PARKING ITERATIONS===========================================================================================
			if(iterations>=nIterations){
				allPos = true;
			}
		}
		ParkingSetCoveringMIP ParkingFinal = new ParkingSetCoveringMIP(allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
		int[][] OutputFirst = ParkingFinal.getOutputID();

		//FOR THE LAST 16 TRAINS=======================================================================================================
		int nTrains2 = 16;


		int[][] trackInfo2 = new int[nTracks2][3]; //lengt left right
		for(int s=1;s<1+nTracks2;s++){ //the departure tracks!!
			trackInfo2[s-1][0] = (int) tracks.get(s).getLength(); 
			trackInfo2[s-1][1] = 0;
			trackInfo2[s-1][2] = 0;
		}
		int[][] trainInfo2 = new int[nTrains2][4]; //ID Atime Dtime Length //22 is number of trains!!
		for (int x=0;x<trainInfo2.length;x++){ 
			trainInfo2[x][0] = blockInfo[x+7][0]; //id
			trainInfo2[x][1] = blockInfo[x+7][3]; //atime
			trainInfo2[x][2] = blockInfo[x+7][4]; //dtime
			trainInfo2[x][3] = blockInfo[x+7][11]; //length
		}
		//===========PARKING BUILD SIDES===============================================================================================
		int[][][] sides2 = new int[trackInfo2.length][trainInfo2.length][4]; //LL LR RL RR
		for(int b=0;b<trainInfo2.length;b++){
			for(int s=0;s<trackInfo2.length;s++){
				if(departures[b][2]==906){
					sides2[s][b][1]=1; //LR
					sides2[s][b][0]=1; //LL
				} else if(departures[b][2]==104){
					sides2[s][b][0]=1;//LL
				} else {
					System.out.println("Error tracks");
				}
			}
		}

		ArrayList<ArrayList<Integer>> allA2 =  new ArrayList<ArrayList<Integer>>();

		int[] AssTrack2 = new int[trackInfo2.length];
		int[][] AssTrackTrain2 = new int[trackInfo2.length][trainInfo2.length];
		//==============PARKING INITIALIZE SET COVERING PROBLEM========================================================================
		System.out.println("Initialization parking model===========================================================");
		ParkingSetCoveringRelaxed ParkingInitialize2 = new ParkingSetCoveringRelaxed(allA2, trackInfo2.length, trainInfo2.length, AssTrack2, AssTrackTrain2, trainInfo2);
		ParkingSetCoveringMIP ParkingMIPinitialize2 = new ParkingSetCoveringMIP(allA2, trackInfo2.length, trainInfo2.length, AssTrack2, AssTrackTrain2, trainInfo2);
		double[] output2 = ParkingInitialize2.getDuals();
		int[] outputMIP2 = ParkingMIPinitialize2.getPenalty();
		int[] Ub2 = new int[trainInfo2.length];
		int[] Vs2 = new int[trackInfo2.length];
//		for(int b=0;b<Ub2.length;b++){
//			if(outputMIP2[b]==1){
//				Ub2[b] = -1;
//			} else {
//				Ub2[b] = 1;
//			}
//		}
		for(int b=0;b<Ub2.length;b++){
			Ub2[b] = -(int) output2[b];
		}
		for(int s=0;s<Vs2.length;s++){
			Vs2[s] = -(int) output2[Ub2.length+s];
		}

		boolean allPos2 = false; //Checks wheter all duals are positive
		int iterations2 = 1;
		while(allPos2==false){
			System.out.println("=========================================================================================");
			System.out.println("Iteration " + iterations2);
			//==============PARKING CREATE ASSIGNMENT======================================================================================
			int trys2=0;
			int tryb2=0;
			boolean foundnew2 = false;
			iterations2++;
			ArrayList<Integer> assignment2 = new ArrayList<Integer>();
			while(foundnew2==false && tryb2<(trainInfo2.length)){  //try all trains
				trys2 = 0;
				while(foundnew2==false && trys2<(trackInfo2.length)){ //try all tracks
					assignment2.clear();
					CreateAssignment AssignmentObject2 = new CreateAssignment(sides2, tryb2, trys2, Ub2, Vs2, eventList, trainInfo2, trackInfo2);
					assignment2 = AssignmentObject2.getCreation(); //first is track rest is trainNR
					if(!allA2.contains(assignment2)){
						//										printList(assignment2);
						foundnew2 = true;
					}
					trys2++;
				}
				tryb2++;
			}
			if(foundnew2){
				allA2.add(assignment2);
				AssTrack2[assignment2.get(0)] = AssTrack2[assignment2.get(0)]+1;
				for(int b=0;b<trainInfo2.length;b++){
					if(inAssignment(assignment2, b)){
						AssTrackTrain2[assignment2.get(0)][b] = AssTrackTrain2[assignment2.get(0)][b]+1;
					}
				}
			}


			//=====RUN THE PARKING SET COVERING PROBLEM=======================================================================
			ParkingSetCoveringRelaxed Parking2 = new ParkingSetCoveringRelaxed(allA2, trackInfo2.length, trainInfo2.length, AssTrack2, AssTrackTrain2, trainInfo2);
			ParkingSetCoveringMIP ParkingMIP2 = new ParkingSetCoveringMIP(allA2, trackInfo2.length, trainInfo2.length, AssTrack2, AssTrackTrain2, trainInfo2);
			output2 = Parking2.getDuals();
			outputMIP2 = ParkingMIP2.getPenalty();
//			for(int b=0;b<Ub2.length;b++){
//				if(outputMIP2[b]==1){
//					Ub2[b] = -1;
//				} else {
//					Ub2[b] = 1;
//				}
//			}
			for(int b=0;b<Ub2.length;b++){
				Ub2[b] = -(int) output2[b];
			}
			for(int s=0;s<Vs2.length;s++){
				Vs2[s] = -(int) output2[Ub2.length+s];
			}

			//================PARKING ITERATIONS===========================================================================================
			if(iterations2>=nIterations){
				allPos2 = true;
			}
		}
		ParkingSetCoveringMIP ParkingFinal2 = new ParkingSetCoveringMIP(allA2, trackInfo2.length, trainInfo2.length, AssTrack2, AssTrackTrain2, trainInfo2);
		int[][] OutputSecond =ParkingFinal2.getOutputID();		

		int[][] totalOutput = new int[OutputFirst.length+OutputSecond.length][2];
		for(int i=0;i<OutputFirst.length;i++){
			totalOutput[i]=OutputFirst[i];
		}
		for(int i=0;i<OutputSecond.length;i++){
			totalOutput[i+OutputFirst.length]=OutputSecond[i];
		}
		return totalOutput;

	}

	public int[][] returnOutput(){
		return outputID;
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

	public static int[][] initializeBlockInfo(int blocks){
		int[][] output = new int[blocks+1][12];

		String csvFile = "CompositionTimesMatching.csv";
		//		String csvFile = "CompositionTimes.csv";
		BufferedReader br = null;
		String cvsSplitBy = ";"; 
		String line = "";
		int count = -1;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine(); //title line

			while ((line = br.readLine()) != null) { //TELT EEN STAP TELANG DOOR GEEN ZIN OM NU TE FIXEN
				count = count+1;
				String[] data = line.split(cvsSplitBy);  //IDshort IDA IDD timeA timeD trackA trackD
				output[count][0] = Integer.parseInt(data[0]); //comp ID
				output[count][1] = Integer.parseInt(data[1]); //A ID
				output[count][2] = Integer.parseInt(data[2]); //D ID
				output[count][3] = Integer.parseInt(data[3]); //A time
				output[count][4] = Integer.parseInt(data[4]); //D time
				output[count][5] = Integer.parseInt(data[5]); //A track
				output[count][6] = Integer.parseInt(data[6]); //D track
				output[count][7] = Integer.parseInt(data[7]); //I time
				output[count][8] = Integer.parseInt(data[8]); //C time
				output[count][9] = Integer.parseInt(data[9]); //W time
				output[count][10] = Integer.parseInt(data[10]);  //R time
				output[count][11] = Integer.parseInt(data[11]);  //Length
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return output;
	}
}
