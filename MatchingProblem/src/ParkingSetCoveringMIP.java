import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.IIOException;
import ilog.concert.*;
import ilog.cplex.*;

public class ParkingSetCoveringMIP{
	//Sets
	public int nTracks; //s
	public int nTotalAssignments; //K
	public int[] nAssignments; //Ks -> for every track
	public int[][] nAssignmentsTrains; //Ksb //for every track for every block
	public int nTrains; //number of blocks
	public int[][] costs;//for every track for every assignment
	public int penalty;

	public double[] duals;
	public int[][] XSK;
	public int[] XK;
	public int[] YB;
	public int[][] outputID;
	public int outputObjective;

	public ParkingSetCoveringMIP(ArrayList<ArrayList<Integer>> allA, int Tracks, int Trains, int[] AssTrack, int[][] AssTrackTrain, int[][] trainInfo) throws IOException, IloException {
		solveMe(allA, Tracks, Trains, AssTrack, AssTrackTrain, trainInfo);
		//allA is trackNR trainNR trainNR trainNR...
	}


	/*
	 * Assignment = feasible assignment of subset of blocks to a shunt track
	 * S = shunttracks --> nTracks
	 * Ks = set of assignments on S --> nAssignments
	 * Kbs = set of assignments on S that contain block b
	 * B = set of blocks
	 * 
	 * decisions:
	 * xks = 1 if k{Ks used on track s{S
	 * yb = 1 if block b is NOT parked on a track
	 * 
	 * parameters:
	 * cks = costs of assigning k on s
	 * d = penalty if block not assigned
	 */
	public void solveMe(ArrayList<ArrayList<Integer>> allA, int Tracks, int Trains, int[] AssTrack, int[][] AssTrackTrain, int[][] trainInfo) throws IOException, IloException {
		/*
		 * data uit dynamic programming ArrayList<ArrayList<ArrayList<ArrayList<int>>>>() assignments
		 * assignments.get(track).get(block).get(assignment).get(id)=int
		 */
		//create list with all assignments {number, track, assignments}
		ArrayList<ArrayList<Integer>> allAssignments = new ArrayList<ArrayList<Integer>>();
		allAssignments = allA; //track idNR



		//lees benodigde data in=======================================================================================
		initializeData data = new initializeData();
		ArrayList<trainComposition> arrivalTrains = new ArrayList<trainComposition>(); //set Ta
		ArrayList<trainComposition> departureTrains = new ArrayList<trainComposition>(); //set Td
		ArrayList<trainComposition> allTrains = new ArrayList<trainComposition> ();


		//DIT MOET UIT COMPOSITION TIMES KOMEN!!!
		for(int i=0; i < data.getCompositions().size();i++){
			allTrains.add(data.getCompositions().get(i));
			if(data.getCompositions().get(i).getArrival()){
				arrivalTrains.add(data.getCompositions().get(i));
			} else {
				departureTrains.add(data.getCompositions().get(i));
			}
		}
		//LET OP ALLTRAINS MOET ALLEEN DE TREINEN DIE GEPARKED WORDEN BEVATTEN dus de gevonden composities uit matching

		//initialize sizes of sets==============================================================================================
		this.nTracks = Tracks; //number of tracks in the used area
		this.nTrains = Trains; //number of trains
		this.nTotalAssignments = allAssignments.size(); //size of K
		this.nAssignments = AssTrack; //# ass per track SIZE Ks
		this.nAssignmentsTrains = AssTrackTrain; //# assignments containing b SIZE Ksb
		penalty=1;
		this.YB =  new int[nTrains];
		this.XSK = new int[nTracks][nTotalAssignments];
		this.XK = new int[nTotalAssignments];
		this.outputID = new int[nTrains][2];


		//initialize sets S, B, Kb, Kbs=============================================================================================
		//Set with trainInfo

		//K is allAssignments
		ArrayList<ArrayList<Integer>> Ks = new ArrayList<ArrayList<Integer>>(); //all assignments on that track
		ArrayList<ArrayList<ArrayList<Integer>>> Ksb = new ArrayList<ArrayList<ArrayList<Integer>>>();
		//for all tracks for all blocks a list of assignments in Ksb
		//ksb.get(track).get(train).get(assignments)
		for (int i=0; i<nTracks; i++){
			Ks.add(new ArrayList<Integer>()); //of assignments
			Ksb.add(new ArrayList<ArrayList<Integer>>()); //for each track a list for the blocks
			for (int j=0;j<nTrains; j++){
				Ksb.get(i).add(new ArrayList<Integer>()); //so for each track for each train list of assignments
			}
		}

		for (int i=0 ; i<nTotalAssignments; i++){ //for each assignment
			Ks.get(allAssignments.get(i).get(0)).add(i); //add to correct arraylist
			for (int b=0; b<nTrains ;b++){
				if(inAssignment(allAssignments.get(i), b)){ //train is in assignment
					Ksb.get(allAssignments.get(i).get(0)).get(b).add(i);//add associated assignment
				}
			}
		}

		try{
			//define new model
			IloCplex cplex = new IloCplex();

			//decision variables
			IloNumVar[] xk = new IloNumVar[nTotalAssignments];
			IloNumVar[] yb = new IloNumVar[nTrains];
			
			//making all variables boolean
			yb = cplex.boolVarArray(nTrains); //for every train
			xk = cplex.boolVarArray(nTotalAssignments);

			//define objective
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for(int b=0;b<nTrains;b++){
				objective.addTerm(penalty, yb[b]);
			}
			cplex.addMinimize(objective);

			List<IloRange> constraints = new ArrayList<IloRange>();

			//add constraints
			//constraint1 sum_sum_xsk + yb = 1 for all b -> or assigned or not
			IloLinearNumExpr[] constraint1 = new IloLinearNumExpr[nTrains]; //for all arriving trains
			for(int b=0;b<nTrains;b++){
				constraint1[b] = cplex.linearNumExpr();
				for(int s=0;s<nTracks;s++){
					for(int k=0;k<nAssignmentsTrains[s][b];k++){
						//Ksb.get(s).get(b) as a set
						if(!Ksb.get(s).get(b).isEmpty()){
							constraint1[b].addTerm(1, xk[Ksb.get(s).get(b).get(k)]);
						}
					}
				}
				constraint1[b].addTerm(1, yb[b]);
			}
			for(int b=0;b<nTrains;b++){
				constraints.add(cplex.addEq(1, constraint1[b]));
			}

			//constraint2 sum_xsk <=1

			IloLinearNumExpr[] constraint2 = new IloLinearNumExpr[nTracks];
			for(int s=0;s<nTracks;s++){
				if(nAssignments[s]!=0){
					constraint2[s] = cplex.linearNumExpr();
					for(int k=0;k<nAssignments[s];k++){
						//use Ks.get(s) as a set
						if(!Ks.get(s).isEmpty()){
							constraint2[s].addTerm(1, xk[Ks.get(s).get(k)]);
						}
					}
				}
			}
			for(int s=0;s<nTracks;s++){
				if(nAssignments[s]!=0){
					constraints.add(cplex.addLe(constraint2[s], 1));
				}
			}

			cplex.exportModel("ParkingProblemModel.lp");
			System.out.println("Model exported");

				if(cplex.solve()){
					this.outputObjective = (int) cplex.getValue(objective);
					this.duals = new double[nTrains+nTracks];
					for(int i=0;i<constraints.size();i++){
						duals[i] = cplex.getSlack(constraints.get(i));
					}
					System.out.println("MIP Problem Solved");
					System.out.println("Objective: " + cplex.getValue(objective));

					for(int b=0;b<nTrains;b++){
						YB[b] = (int) cplex.getValue(yb[b]);
					}
					for(int k=0;k<nTotalAssignments;k++){
						XK[k] = (int) cplex.getValue(xk[k]); //for every track for every assignment
						if(XK[k]==1){
							ArrayList<Integer> assignment = allA.get(k);
							for(int a=1;a<assignment.size();a++){ //for all trains in assignment
								outputID[assignment.get(a)][0] = trainInfo[assignment.get(a)][0];
								outputID[assignment.get(a)][1] = assignment.get(0);
							}
						}
					}
				}

		} catch (IloException e) {
			System.out.print("Catch clause" + e);
		}
	}
	
	public int[][] getOutputID(){
		return outputID;
	}

	public boolean inAssignment(ArrayList<Integer> assignment, int trainID){
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

	public double[] getDuals(){
		return duals;
	}
	
	public int[] getAssignments(){
		return XK;
	}
	
	public int[] getPenalty(){
		return YB;
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
	
	public static void printList(ArrayList<Integer> p){
		for (int i=0;i<p.size();i++){
			System.out.print(p.get(i) + " ");
		}
		System.out.println("");
	}
	
	public int getObjective(){
		return outputObjective;
	}
}
