import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.IIOException;
import ilog.concert.*;
import ilog.cplex.*;

public class ParkingSetCovering {
	//Sets
		public int nTracks; //s
		public int nTotalAssignments; //K
		public int[] nAssignments; //Ks -> for every track
		public int[][] nAssignmentsTrains; //Ksb //for every track for every block
		public int nTrains; //number of blocks
		public int[][] costs;//for every track for every assignment
		public int penalty;
		
		public double[] output;
		
		public ParkingSetCovering(ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> K, int[] id) throws IOException, IloException {
			solveMe(K, id);
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
		public void solveMe(ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> K, int[] id) throws IOException, IloException {
			/*
			 * data uit dynamic programming ArrayList<ArrayList<ArrayList<ArrayList<int>>>>() assignments
			 * assignments.get(track).get(block).get(assignment).get(id)=int
			 */
			
			ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> assignmentData = K;
			
			//create list with all assignments {number, track, assignments}
			ArrayList<ArrayList<Integer>> allAssignments = new ArrayList<ArrayList<Integer>>();
			int count=1;
			for (int s=0 ; s<nTracks; s++){ //for each track --> KS
				for(int b=0; b <assignmentData.get(s).size(); b++){ //for each block --> Ksb
					for(int a=0;a<assignmentData.get(s).get(b).size();a++){ //one assignment from Ksb
						ArrayList<Integer> now = new ArrayList<Integer>(); //build the data
						now.add(count); //the assignment number
						now.add(s); //the corresponding train
						for(int t=0;t<assignmentData.get(s).get(b).get(a).size();t++){ //how many trains in assignment
							now.add(assignmentData.get(s).get(b).get(a).get(t));//add the id of the train in the assingment
						}
						allAssignments.add(now);
						count++;
					}
				}
			}
			
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
			this.nTracks = assignmentData.size(); //number of tracks in the used area
			this.nTrains = assignmentData.get(0).size(); //number of trains
			this.nTotalAssignments = allAssignments.size(); //size of K
			this.nAssignments = new int[nTracks]; //# ass per track SIZE Ks
			for(int s = 0;s<nAssignments.length;s++){
				int number = 0;
				for(int b=0;b<assignmentData.get(s).size();b++){
					number = number + assignmentData.get(s).get(b).size();
				}
				nAssignments[s] = number;
			}
			this.nAssignmentsTrains = new int[nTracks][nTrains]; //# assignments containing b SIZE Ksb
			for(int s = 0;s<nAssignments.length;s++){
				for(int b=0;b<assignmentData.get(s).size();b++){
					nAssignmentsTrains[s][b] = assignmentData.get(s).get(b).size();
				}
			}
			penalty=1;
			

			//initialize sets S, B, Kb, Kbs=============================================================================================
			//Set with trainInfo
			int[][] trainInfo = new int[id.length][2];
			for(int b=0;b<trainInfo.length;b++){
				trainInfo[b][0] = b+1;
				trainInfo[b][1] = id[b]; //LIJST MET ID!!!!!!
			}
			
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
				Ks.get(allAssignments.get(i).get(1)).add(allAssignments.get(i).get(0)); //add to correct arraylist
				for (int b=0; b<nTrains ;b++){
					if(inAssignment(allAssignments.get(i), trainInfo[b][1])){ //train is in assignment
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
//				for(int s=0;s<nTracks;s++){
//					for(int k=0; k<nAssignments[s];k++){
//						//Ks.get(s) as a set
//						objective.addTerm(costs[s][Ks.get(s).get(k)], xsk[s][Ks.get(s).get(k)]); 
//					}
//				}
				for(int b=0;b<nTrains;b++){
					objective.addTerm(penalty, yb[b]);
				}
				cplex.addMinimize(objective);
				
//				IloRange[] constraints = new IloRange[nTrains+nTracks];
				List<IloRange> constraints = new ArrayList<IloRange>();
				
				//add constraints
				//constraint1 sum_sum_xsk + yb = 1 for all b -> or assigned or not
				IloLinearNumExpr[] constraint1 = new IloLinearNumExpr[nTrains]; //for all arriving trains
				for(int b=0;b<nTrains;b++){
					constraint1[b] = cplex.linearNumExpr();
					for(int s=0;s<nTracks;s++){
						for(int k=0;k<nAssignmentsTrains[s][b];k++){
							//Ksb.get(s).get(b) as a set
							constraint1[b].addTerm(1, xsk[s][Ksb.get(s).get(b).get(k)-1]);
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
						constraint2[s] = cplex.linearNumExpr(nTracks);
						for(int k=0;k<nAssignments[s];k++){
							//use Ks.get(s) as a set
							constraint2[s].addTerm(1, xsk[s][Ks.get(s).get(k)]);
						}
					}
				}
				for(int s=0;s<nTracks;s++){
					if(nAssignments[s]!=0){
						constraints.add(cplex.addLe(constraint2[s], 1));
					}
				}

				cplex.exportModel("ParkingProblem.lp");
				System.out.println("Model exported");
				
				if(cplex.solve()){
//					this.output = new double[nTrains+nTracks];
//					for(int i=0;i<constraints.size();i++){
//						output[i] = cplex.getSlack(constraints.get(i));
//					}
					
					
//					System.out.println("Problem Solved.");
//					System.out.println("Objective: " + cplex.getValue(objective));
//					for(int ub=0;ub<nTrains;ub++){
////						System.out.println("Duals u" + (ub+1) + "  " + cplex.getDual(constraints.get(ub)));
//						System.out.println("Slack u" + (ub+1) + "  " + cplex.getSlack(constraints.get(ub)));
////						System.out.println("Reduced costs " + (ub+1) + " " + cplex.getReducedCost(yb[ub]));
//					}
					
				}
				if(cplex.solveFixed()){
					this.output = new double[nTrains+nTracks];
					for(int i=0;i<constraints.size();i++){
						output[i] = cplex.getDual(constraints.get(i));
					}
					System.out.println("Solve Fixed");
					System.out.println("Objective: " + cplex.getValue(objective));
					
//					for(int ub=0;ub<nTrains;ub++){
//						System.out.println("Dual u" + (ub+1) + "  " + cplex.getDual(constraints.get(ub)));
//						System.out.println("Slack u" + (ub+1) + "  " + cplex.getSlack(constraints.get(ub)));
//					}
				}

			} catch (IloException e) {
				System.out.print("Catch clause" + e);
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
		
		public double[] getOutput(){
			return output;
		}
}
