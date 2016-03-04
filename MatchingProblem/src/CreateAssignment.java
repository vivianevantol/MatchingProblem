import java.util.ArrayList;

public class CreateAssignment {
	private int[] Ub;
	private int[] Vs;
	private ArrayList<Integer> created;
	
	public CreateAssignment(int tryb, int trys, int[] Ub, int[] Vs, initializeEventList list, int[][] trainInfo, int[][] trackInfo){
		this.Ub = Ub;
		this.Vs = Vs;
		created = creation(tryb, trys, list, trainInfo, trackInfo);
	}
	
	public ArrayList<Integer> creation(int tryb, int trys, initializeEventList list, int[][] trainInfo, int[][] trackInfo){
		int[][] departures = list.getDeparturelist();
		int[][] arrivals = list.getArrivallist();

		
		
		//BUILD LIST SHOWING POSSIBLE ARRIVAL AND DEPARTURE SIDES=======================================================
		int[][][] sides = new int[Vs.length][Ub.length][4]; //LL LR RL RR
		for(int b=0;b<Ub.length;b++){
			for(int s=0;s<Vs.length;s++){
				if(departures[b][2]==906){
					sides[s][b][1]=1; //LR
				} else if(departures[b][2]==104){
					sides[s][b][0]=1;//LL
				} else {
					System.out.println("Error tracks");
				}
			}
		}
		
		//FIND MINUMUM DUAL VARIABLE TRACK an build==============================================================================
		int minTrack = getMin(Vs, trys)[0]; //index, value --> this is the one you build on
		boolean idle = true; //enough space for shortest train
		//assignment still emtpy
//		ArrayList<Integer> currentID = new ArrayList<Integer>(); //still empty
		ArrayList<Integer> currentNR = new ArrayList<Integer>(); //still empty
		currentNR.add(minTrack); //so track is in the assignment
		int currentLength =0;
		int currentCapacity = trackInfo[minTrack][0];
		
		while(idle == true){
			System.out.println("print UB");
			printArray(Ub);
			int ubtried = 0;
			int[] min = getMin(Ub, tryb, ubtried);
			int minTrain = min[0]; //we want to add this train to the assignment
			boolean add = checkFeasibility(minTrack, minTrain, currentNR, currentLength, trainInfo, currentCapacity, sides);
			if(add){
//				currentID.add(trainInfo[minTrain][0]);
				currentNR.add(minTrain);
				currentLength = currentLength + trainInfo[minTrain][3];
				Ub[minTrain] = Integer.MAX_VALUE; //dont add twice
				ubtried++;
			} else { //want to check next train
				Ub[minTrain] = Integer.MAX_VALUE;
				ubtried++;
			}
			
			if(currentLength>currentCapacity-70 || min[1]>100000){
				idle = false;
			}
			if(ubtried>=Ub.length-1){
				idle = false;
			}
		}
		return currentNR;
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
		if(currentCapacity>=currentLength+trainInfo[train][3]){ //don't look if length doesn't fit
			//sides: [s] [b] [LL LR RL RR] --> we only have LL LR
			//All trains arrive left
			if(sides[currentTrack][train][1]==1){ //train departures right --> LR only of leaves after ALL TRAINS
				if(trainInfo[train][2]>=trainInfo[currentNR.get(0)][2]){ //new train departs later
					feasible=true;
				}
				if(sides[currentTrack][currentNR.get(0)][0]==1){
					feasible = false; //CHECK WHETER THE LEFT TRAIN DEPART LEFT
				}
			} else { //train departs left --> LL
				//IF first goes right side ALL GOOD
				if(sides[currentTrack][currentNR.get(0)][1]==1){
					feasible = true; //CHECK WHETER THE LEFT TRAIN DEPART RIGHT
				} else { //ELSE if first goes left check departure time --> all good
					if(trainInfo[train][2]<=trainInfo[currentNR.get(0)][2]){
						feasible = true;
					}
				}
			}
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
