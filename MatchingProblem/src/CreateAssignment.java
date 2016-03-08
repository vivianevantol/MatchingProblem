import java.util.ArrayList;
import java.util.Random;

public class CreateAssignment {
	private int[] Ubfinal;
	private int[] Vs;
	private ArrayList<Integer> created;
	private int newLength; //the length adjusted for departed trains
	
	public CreateAssignment(int[][][] sides, int tryb, int trys, int[] Ubfinal, int[] Vs, initializeEventList list, int[][] trainInfo, int[][] trackInfo){
		this.Ubfinal = Ubfinal;
		this.Vs = Vs;
		created = creation(sides, tryb, trys, list, trainInfo, trackInfo);
	}
	
	public ArrayList<Integer> creation(int[][][] sides, int tryb, int trys, initializeEventList list, int[][] trainInfo, int[][] trackInfo){
//		int[][] departures = list.getDeparturelist();
//		int[][] arrivals = list.getArrivallist();
		
		//FIND MINUMUM DUAL VARIABLE TRACK an build==============================================================================
		int minTrack = getMin(Vs, trys)[0]; //index, value --> this is the one you build on
		boolean idle = true; //enough space for shortest train
		//assignment still emtpy
//		ArrayList<Integer> currentID = new ArrayList<Integer>(); //still empty
		ArrayList<Integer> currentNR = new ArrayList<Integer>(); //still empty
		currentNR.add(minTrack); //so track is in the assignment
		int currentLength =0;
		int currentPresentLength=0;
		int currentCapacity = trackInfo[minTrack][0];
		int ubtried = 0;
		
		//copy UB
		int[] Ub = new int[Ubfinal.length];
		for(int i=0;i<Ubfinal.length;i++){
			Ub[i]=Ubfinal[i];
		}

		while(idle == true){
			int minTrain = 0;
//			System.out.println("print UB" +  ubtried);
			if(currentNR.size()>1){ //first train is assigned
				tryb=0;
			}
//			if(currentNR.size()>0){ //two trains have been added
//				Random randomGenerator = new Random();
//				minTrain = randomGenerator.nextInt(Ub.length);
//			} else {
//				int[] min = getMin(Ub, tryb, ubtried);
//				minTrain = min[0];
//			}
			
			int[] min = getMin(Ub, tryb, ubtried);
			Random randomGenerator = new Random();
			int randomChoice = randomGenerator.nextInt(10);
			if(randomChoice>2){ //20% change of taking the random value
				minTrain = min[0]; //we want to add this train to the assignment
			} else {
				minTrain = randomGenerator.nextInt(Ub.length);
			}
//			minTrain = min[0]; //we want to add this train to the assignment
			boolean add = checkFeasibility(minTrack, minTrain, currentNR, currentLength, trainInfo, currentCapacity, sides);
			if(add){ //need to call newLength
				currentPresentLength = getNewLength();
				currentNR.add(1, minTrain);
				currentLength = currentLength + trainInfo[minTrain][3];
				Ub[minTrain] = Integer.MAX_VALUE; //dont add twice
				ubtried++;
			} else { //want to check next train
				Ub[minTrain] = Integer.MAX_VALUE;
				ubtried++;
			}
			
//			if(currentPresentLength>currentCapacity-70 || min[1]>100000){
//				idle = false;
//			}
			if(ubtried>=Ub.length){ //|| min[1]>100000
				idle = false;
			}
		}
		return currentNR;
	}
	
	public int getNewLength(){
		return newLength;
	}
	
	/*
	 * currentID of trains on assignment
	 * currentNR of trains on assignment
	 * currentLength of assignment
	 * trainInfo
	 * currentCapacity of current track
	 * sides about where to arrival and depart
	 */
	public boolean checkFeasibility(int currentTrack, int train, ArrayList<Integer> currentNR,  int currentLength, int[][] trainInfo,  int currentCapacity, int[][][] sides){
		boolean feasible = false;
		int newLength = currentLength + trainInfo[train][3]; //if all trains were present
		ArrayList<Integer> currentPresent = new ArrayList<Integer>();
		currentPresent.add(currentNR.get(0)); //trains still on same the track
		for (int i=1;i<currentNR.size();i++){ //check which are departed to not include in length
			if(trainInfo[currentNR.get(i)][2]<trainInfo[train][1]){
//				System.out.println("Adjust length with: " + trainInfo[currentNR.get(i)][3]);
				newLength = newLength - trainInfo[currentNR.get(i)][3];
			} else {
				currentPresent.add(currentNR.get(i));
			}
		}
		if(currentPresent.size()==1){ //only track in assignment so no trains
			if(trainInfo[train][3]<=currentCapacity){
				feasible = true; //always add to empty assignment
			}
		}

		if(currentCapacity>=newLength && currentPresent.size()>1){ //don't look if length doesn't fit
			//sides: [s] [b] [LL LR RL RR] --> we only have LL LR
			//All trains arrive left and all trains can depart left (some also right)
			//this is easiest check so always check whether new train can be LL (because if LR than also LL)
			//only check if the train next to it can be LR
				//IF first can go right side ALL GOOD
				feasible = true;
				for(int i=1;i<currentPresent.size();i++){ //can they all leave right
					if(sides[currentTrack][currentPresent.get(i)][1]!=1){
						feasible = false; //first cannot leave right side
					}
					if(i>1 && trainInfo[currentPresent.get(i-1)][2]<trainInfo[currentPresent.get(i)][2]){
						feasible = false; //not in order departure so cannot all leave right side
					}
				}
				if(trainInfo[currentPresent.get(1)][2]>trainInfo[train][2]){
					feasible = true; //first train can follow to leave left
				}
			}
		if(feasible){
			this.newLength = newLength;
//			System.out.println("CurrentLength: " + newLength);
		}
		return feasible;
	}

	
	public ArrayList<Integer> getCreation(){
		return created;
	}
	
	public static int[] getMin(int[] x){
		int minvalue = Integer.MAX_VALUE;;
		int index = -1;
			for (int i=0;i<x.length;i++){
				if(x[i]<=minvalue){
					index = i;
					minvalue = x[i];
				}
			}
			x[index] = Integer.MAX_VALUE;
		int[] yy = {index, minvalue};
		return yy;
	}
	
	public static int[] getMin(int[] x, int trys){
		int[] y= new int[x.length];
		for (int i=0;i<x.length;i++){
			y[i] = x[i];
		}
		int now = -1;
		
		int minvalue = 800000;
		int index = -1;
		while(now!=trys){
//			printArray(y);
			now++;
			minvalue = Integer.MAX_VALUE;
			index = -1;
			for (int i=0;i<y.length;i++){
				if(y[i]<minvalue){
					index = i;
					minvalue = y[i];
				}
			}
			y[index] = Integer.MAX_VALUE;
		}
		int[] yy = {index, minvalue};
		return yy;
	}
	
	public static int[] getMin(int[] x, int trys, int tried){
		int[] y= new int[x.length];
		int tries = trys-tried-1;
		if(tries<0){
			tries=0;
		}
		for (int i=0;i<x.length;i++){
			y[i] = x[i];
		}
		int now = -1;
		
		int minvalue = 800000;
		int index = -1;
		while(now!=tries){
			now++;
			minvalue = Integer.MAX_VALUE;
			index = -1;
			for (int i=0;i<y.length;i++){
				if(y[i]<minvalue){
					index = i;
					minvalue = y[i];
				}
			}
			y[index] = Integer.MAX_VALUE;
		}
		int[] yy = {index, minvalue};
		return yy;
	}
	
	public static void printArray(int[] printer){
		for (int i=0;i<printer.length;i++){
				System.out.print(printer[i] + "  " );
			System.out.println();
		}
	}

}
