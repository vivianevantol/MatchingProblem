import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.IIOException;
import ilog.concert.*;
import ilog.cplex.*;

public class ParkingSetCovering {
	//Sets
		public int nTracks; //s
		public int nTotalAssignments;
		public int[] nAssignments; //k -> for every track
		public int[][] nAssignmentsTrains; //for every track for every block
		public int nTrains; //number of blocks
		public int[][] costs;//for every track for every assignment
		public int penalty;
		
		public ParkingSetCovering() throws IOException, IloException {
			solveMe();
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
		public void solveMe() throws IOException, IloException {
			/*
			 * data uit dynamic programming ArrayList<ArrayList<ArrayList<int>>>() assignments
			 * assignments.get(track).get(assignment).get(block)=int
			 */
			
			ArrayList<ArrayList<ArrayList<Integer>>> assignmentData = new ArrayList<ArrayList<ArrayList<Integer>>>();
			//create list with all assignments {number, track, assignments}
			ArrayList<ArrayList<Integer>> allAssignments = new ArrayList<ArrayList<Integer>>();
			int count=1;
			for (int i=0 ; i<nTracks; i++){ //for each track
				for(int j=0; j <assignmentData.get(i).size(); j++){ //for each assignment
					ArrayList<Integer> now = new ArrayList<Integer>();
					now.add(count); //the assignment number
					now.add(i); //the corresponding train
					for(int t=0;t<assignmentData.get(i).get(j).size();t++){ //how many trains in assignment
						now.add(assignmentData.get(i).get(j).get(t));//add the id of the train
					}
					allAssignments.add(now);
					count++;
				}
			}
			
			//lees benodigde data in
			initializeData data = new initializeData();
			ArrayList<trainComposition> arrivalTrains = new ArrayList<trainComposition>(); //set Ta
			ArrayList<trainComposition> departureTrains = new ArrayList<trainComposition>(); //set Td
			ArrayList<trainComposition> allTrains = new ArrayList<trainComposition> ();
			
			for(int i=0; i < data.getCompositions().size();i++){
				allTrains.add(data.getCompositions().get(i));
				if(data.getCompositions().get(i).getArrival()){
					arrivalTrains.add(data.getCompositions().get(i));
				} else {
					departureTrains.add(data.getCompositions().get(i));
				}
			}
			//LET OP ALLTRAINS MOET ALLEEN DE TREINEN DIE GEPARKED WORDEN BEVATTEN dus de gevonden composities
			
			//initialize sizes of sets
			this.nTracks = assignmentData.size(); //number of tracks in the used area
			this.nTrains = allTrains.size();
			this.nTotalAssignments = allAssignments.size();
			this.nAssignments = new int[nTracks]; //# ass per track SIZE Ks
			int maxAssignment=0;
			for (int i=0; i<nAssignments.length; i++){
				if(nAssignments[i]>maxAssignment){
					maxAssignment = nAssignments[i];
				}
			}
			this.nAssignmentsTrains = new int[nTracks][nTrains]; //# assignments containing b SIZE Ksb
			this.costs = new int[nTracks][maxAssignment]; //costs s k

			//initialize sets S, B, Kb, Kbs
			ArrayList<ArrayList<Integer>> Ks = new ArrayList<ArrayList<Integer>>(); //all assignments on that track
			ArrayList<ArrayList<ArrayList<Integer>>> Ksb = new ArrayList<ArrayList<ArrayList<Integer>>>();
			//for all tracks for all blocks a list of assignments
			//ksb.get(track).get(train).get(assignments)
			for (int i=0; i<nTracks; i++){
				Ks.add(new ArrayList<Integer>());
				Ksb.add(new ArrayList<ArrayList<Integer>>()); //for each track a list
				for (int j=0;j<nTrains; j++){
					Ksb.get(i).add(new ArrayList<Integer>()); //so for each track for each train
				}
			}
			
			for (int i=0 ; i<nTotalAssignments; i++){ //for each assignment
				Ks.get(allAssignments.get(i).get(1)).add(allAssignments.get(i).get(0)); //add to correct arraylist
				for (int b=0; b<nTrains ;b++){
					if(inAssignment(allAssignments.get(i), allTrains.get(b).getID())){ //train is in assignment
						Ksb.get(allAssignments.get(i).get(1)).get(b).add(allAssignments.get(i).get(0));//add associated assignment
					}
				}
			}
			
			
			try{
				//define new model
				IloCplex cplex = new IloCplex();
				
				//decision variables
				IloIntVar[][] xsk = new IloIntVar[nTracks][nTotalAssignments];
				IloIntVar[] yb = new IloIntVar[nTrains];
				
				//making all variables boolean
				yb = cplex.boolVarArray(nTrains); //for every train
				for (int i=0;i<nTotalAssignments;i++){
					xsk[i] = cplex.boolVarArray(nTracks); //for every track for every assignment
				}

				//define objective
				IloLinearNumExpr objective = cplex.linearNumExpr();
				for(int s=0;s<nTracks;s++){
					for(int k=0; k<nAssignments[s];k++){
						//Ks.get(s) as a set
						objective.addTerm(costs[s][Ks.get(s).get(k)], xsk[s][Ks.get(s).get(k)]); 
					}
				}
				for(int b=0;b<nTrains;b++){
					objective.addTerm(penalty, yb[b]);
				}
				cplex.addMinimize(objective);
				
				//add constraints
				//constraint1 sum_sum_xsk + yb = 1 for all b -> or assigned or not
				IloLinearNumExpr[] constraint1 = new IloLinearNumExpr[nTrains]; //for all arriving trains
				for(int b=0;b<nTrains;b++){
					constraint1[b] = cplex.linearNumExpr();
					for(int s=0;s<nTracks;s++){
						for(int k=0;k<nAssignmentsTrains[s][b];k++){
							//Ksb.get(s).get(b) as a set
							constraint1[b].addTerm(1, xsk[s][Ksb.get(s).get(b).get(k)]);
						}
					}
					constraint1[b].addTerm(1, yb[b]);
				}
				for(int b=0;b<nTrains;b++){
					cplex.addEq(1, constraint1[b]);
				}

				//constraint2 sum_xsk <=1
				IloLinearNumExpr[] constraint2 = new IloLinearNumExpr[nTracks];
				for(int s=0;s<nTracks;s++){
					constraint2[s] = cplex.linearNumExpr(nTracks);
					for(int k=0;k<nAssignments[s];k++){
						//use Ks.get(s) as a set
						constraint2[s].addTerm(1, xsk[s][Ks.get(s).get(k)]);
					}
				}
				for(int s=0;s<nTracks;s++){
					cplex.addLe(constraint2[s], 1);
				}

				cplex.exportModel("ParkingProblem.lp");
				System.out.println("Model exported");
				
				if(cplex.solve()){
					System.out.println("Problem Solved.");
				}
			} catch (IloException e) {
				System.out.print("Catch clause");
			}
		}
		
		public boolean inAssignment(ArrayList<Integer> assignment, int trainID){
			boolean check = false;
			for(int i=0;i<assignment.size();i++){
				if(assignment.get(i)==trainID){
					check = true;
				}
			}
			return check;
		}
}
