import java.util.ArrayList;

public class CreateAssignment {
	private int[] Ub;
	private int[] Vs;
	
	public CreateAssignment(int[] Ub, int[] Vs, initializeData data, initializeEventList list, int[][] trainInfo, int[][] trackInfo){
		this.Ub = Ub;
		this.Vs = Vs;
		creation(data, list, trainInfo, trackInfo);
	}
	
	public ArrayList<Integer> creation(initializeData data, initializeEventList list, int[][] trainInfo, int[][] trackInfo){
		ArrayList<Integer> current = new ArrayList<Integer>();
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
		
		//FIND MINUMUM DUAL VARIABLE TRACK=====================================================================================
		int minTrack = getMin(Vs)[0]; //index, value --> this is the one you build on
		boolean idle = true; //enough space for shortest train
		while(idle == true){
			int minTrain = getMin(Ub)[0]; //we want to add this train to the assignment
		}
		
		
		
		return current;
	}
	
	public boolean checkFeasibility(ArrayList<Integer> currentID, ArrayList<Integer> currentNR, int currentTrack, int currentLength, int[][] trainInfo, int train, int currentCapacity, int[][][] sides){
		boolean feasible = false;
		int min = 0; //this is minimum Ub
		while (feasible = false && min<100000){ //so min is not infinity
			if(currentCapacity>=currentLength+trainInfo[train][3]){ //don't look if length doesn't fit
				//sides: [s] [b] [LL LR RL RR] --> we only have LL LR
				//All trains arrive left
				if(sides[currentTrack][train][1]==1){ //train departures right --> LR only of leaves after ALL TRAINS
					for(int b=0;b<currentNR.size();b++){
						feasible = true;
						if(trainInfo[train][2]<trainInfo[b][2]){ //other train departs later
							feasible=false;
							b=100;
						}
					}
					//CHECK WHETER THE LEFT TRAIN DEPART LEFT
				} else { //train departs left
					//IF first goes right side ALL GOOD
					//ELSE if first goes left check departure time --> all good
				}
			}
		}
		return true;
	}
	
	public int[] getMin(int[][] x, int z){
		int minvalue = Integer.MAX_VALUE;
		int index = -1;
		for (int i=0;i<x.length;i++){
			if(x[i][z]<=minvalue){
				index = i;
				minvalue = x[i][z];
			}
		}
		int[] y = {index, minvalue};
		return y;
	}
	
	public int[] getMin(int[] x){
		int minvalue = 800000;
		int index = -1;
		int index2 = -1;
		int minvalue2 = -1;
		for (int i=0;i<x.length;i++){
			if(x[i]<minvalue){
				index = i;
				minvalue = x[i];
			}


		}
		int[] y = {index, minvalue};
		return y;
	}
}
