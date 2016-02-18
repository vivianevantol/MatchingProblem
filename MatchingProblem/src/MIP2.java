import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.IIOException;
import ilog.concert.*;
import ilog.cplex.*;
/*
 * Constraint 1 en 3 creeren precies het aantal wat we moeten hebben dus klopt waarschijnlijk
 * Constraint 2 en 4 creeren te weinig constraints, zouden er MINSTENS net zoveel moeten zijn als 1 en 3
 */
public class MIP2 {
	//Sets
	public int nArrivalTrain; //ti
	public int nDepartureTrain; //tj
	public int nArrivalBlock; //i
	public int nDepartureBlock; //j
	public int nNodes; //n
	
	public int[] nArcsOut0A; //subset for constraint 1
	public int[][] nArcsOutHA;//constraint 2
	public int[][] nArcsInHA;//constraint 2
	public int[] nIntermediatesA;//constraint 2
	
	public int[] nArcsOut0D; //subset for constraint 1
	public int[][] nArcsOutHD;//constraint 2
	public int[][] nArcsInHD;//constraint 2
	public int[] nIntermediatesD;//constraint 2
	
	public int[] nSameDepartures; //matches with arrival blocks
	public int[] nSameArrivals; //matches with departure blocks

	public MIP2() throws IOException, IloException {
		solveMe();
	}

	public void solveMe() throws IOException, IloException {
		//lees benodigde data in
		initializeData data = new initializeData();
		ArrayList<blocks> allBlocks = new ArrayList<blocks>();
		ArrayList<blocks> arrivalBlocks = new ArrayList<blocks>();
		ArrayList<blocks> departureBlocks = new ArrayList<blocks>();
		
		ArrayList<trainComposition> arrivalTrains = new ArrayList<trainComposition>(); //set Ta
		ArrayList<trainComposition> departureTrains = new ArrayList<trainComposition>(); //set Td
		ArrayList<trainComposition> arrivalTrainsx = new ArrayList<trainComposition>(); //set Ta
		ArrayList<trainComposition> departureTrainsx = new ArrayList<trainComposition>(); //set Td
		ArrayList<trainComposition> allCompositions = data.getCompositions();
		
		//create lists with all arrival and all departure blocks
//		int arrivals = 0;
//		int departures =0;
		for(int i=0; i < data.getCompositions().size();i++){
//			allBlocks = createBlocks(data.getCompositions().get(i), allBlocks);
			int ID = data.getCompositions().get(i).getID();
			if(data.getCompositions().get(i).getArrival()){
				if(ID==83011 || ID==83013 || ID==81002 || ID==83021 || ID==80428 ||ID==80206){
				arrivalBlocks = createBlocks(data.getCompositions().get(i), arrivalBlocks);
				arrivalTrains.add(data.getCompositions().get(i));
//				System.out.println("Arr: " + data.getCompositions().get(i).getTypes().size());
//				arrivals++;
				}
			} else {
				if(ID==80457 || ID==83048 || ID==83024 || ID==80207 || ID==83052 ||ID==83056 ||ID==83058){
				departureBlocks = createBlocks(data.getCompositions().get(i), departureBlocks);
				departureTrains.add(data.getCompositions().get(i));
//				System.out.println("Dep: " + data.getCompositions().get(i).getTypes().size());
//				departures++;
				}
			}
		}
		
		
		for(int i=0;i<arrivalBlocks.size();i++){
			printBlock(arrivalBlocks.get(i));
			System.out.println();
		}
		System.out.println();
		for(int i=0;i<departureBlocks.size();i++){
			printBlock(departureBlocks.get(i));
			System.out.println();
		}
		
		
		this.nArrivalTrain = arrivalTrains.size();//19
		this.nDepartureTrain = departureTrains.size();//21
		this.nArrivalBlock = arrivalBlocks.size();//41
		this.nDepartureBlock = departureBlocks.size();//39
		this.nNodes = 10;
		
//		for (int i=0;i<arrivalBlocks.size();i++){
//			printBlock(arrivalBlocks.get(i));
//			System.out.println();
//		}
		
		ArrayList<int[]> ArcsOutZeroA = new ArrayList<int[]>();
		ArrayList<ArrayList<int[]>> ArcsOutHA = new ArrayList<ArrayList<int[]>>(); //for all ti for all h int[]
		ArrayList<ArrayList<int[]>> ArcsInHA = new ArrayList<ArrayList<int[]>>(); //for all ti for all h int[]
		nArcsOut0A = new int[nArrivalTrain];
		nIntermediatesA = new int[nArrivalTrain];
		nArcsOutHA = new int[nArrivalTrain][10];
		nArcsInHA = new int[nArrivalTrain][10];
		
		for(int ti=0;ti<nArrivalTrain;ti++){
			ArcsOutZeroA.add(getArcsOut(arrivalTrains.get(ti), 0, arrivalBlocks, departureBlocks));
			nArcsOut0A[ti] = ArcsOutZeroA.get(ti).length;
			nIntermediatesA[ti] = getIntermediates(arrivalTrains.get(ti)).length;//IntermediatesA.get(ti).length;
			
			
			
			ArcsOutHA.add(new ArrayList<int[]>()); //add list to fill in below
			for(int h=0;h<nIntermediatesA[ti]+1;h++){ //walk on h
				ArcsOutHA.get(ti).add(getArcsOut(arrivalTrains.get(ti), h, arrivalBlocks, departureBlocks));
				nArcsOutHA[ti][h]=getArcsOut(arrivalTrains.get(ti), h, arrivalBlocks, departureBlocks).length;
//				System.out.println(ti + " ID " + h + " ");
				for(int p=0;p<nArcsOutHA[ti][h];p++){
//					printBlock(arrivalBlocks.get(ArcsOutHA.get(ti).get(h)[p]));
//					System.out.println( "   " + ArcsOutHA.get(ti).get(h)[p]);
				
				}
				
			}
			
			ArcsInHA.add(new ArrayList<int[]>());
			for(int h=0;h<=nIntermediatesA[ti];h++){ //walk on h
				ArcsInHA.get(ti).add(getArcsIn(arrivalTrains.get(ti), h, arrivalBlocks, departureBlocks));
				nArcsInHA[ti][h]=getArcsIn(arrivalTrains.get(ti), h, arrivalBlocks, departureBlocks).length;
			}
		}
		
		ArrayList<int[]> ArcsOutZeroD = new ArrayList<int[]>();
		ArrayList<ArrayList<int[]>> ArcsOutHD = new ArrayList<ArrayList<int[]>>(); //for all ti for all h int[]
		ArrayList<ArrayList<int[]>> ArcsInHD = new ArrayList<ArrayList<int[]>>(); //for all ti for all h int[]
		nArcsOut0D = new int[nDepartureTrain];
		nIntermediatesD = new int[nDepartureTrain];
		nArcsOutHD = new int[nDepartureTrain][10];
		nArcsInHD = new int[nDepartureTrain][10];
		
		for(int ti=0;ti<nDepartureTrain;ti++){
			ArcsOutZeroD.add(getArcsOut(departureTrains.get(ti), 0, arrivalBlocks, departureBlocks));
			nArcsOut0D[ti] = ArcsOutZeroD.get(ti).length;
			nIntermediatesD[ti] = getIntermediates(departureTrains.get(ti)).length;//IntermediatesA.get(ti).length;
			
			ArcsOutHD.add(new ArrayList<int[]>()); //add list to fill in below
			for(int h=0;h<=nIntermediatesD[ti];h++){ //walk on h
				ArcsOutHD.get(ti).add(getArcsOut(departureTrains.get(ti), h, arrivalBlocks, departureBlocks));
				nArcsOutHD[ti][h]=getArcsOut(departureTrains.get(ti), h, arrivalBlocks, departureBlocks).length;
			}
			
			ArcsInHD.add(new ArrayList<int[]>());
			for(int h=0;h<=nIntermediatesD[ti];h++){ //walk on h
				ArcsInHD.get(ti).add(getArcsIn(departureTrains.get(ti), h, arrivalBlocks, departureBlocks));
				nArcsInHD[ti][h]=getArcsIn(departureTrains.get(ti), h, arrivalBlocks, departureBlocks).length;
			}
		}
		
		ArrayList<ArrayList<Integer>> SameDepartures = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> SameArrivals = new ArrayList<ArrayList<Integer>>();
		
		nSameDepartures = new int[arrivalBlocks.size()];
		nSameArrivals = new int[departureBlocks.size()];
		
		for(int i=0;i<arrivalBlocks.size();i++){
			SameDepartures.add(getSameDeparture(arrivalBlocks.get(i),departureBlocks));
			nSameDepartures[i] = SameDepartures.get(i).size();
		}
		
		for(int j=0;j<departureBlocks.size();j++){
			SameArrivals.add(getSameDeparture(departureBlocks.get(j),arrivalBlocks));
			nSameArrivals[j] = SameArrivals.get(j).size();
		}
		
//		for(int j=0;j<5;j++){
//		for (int i=0;i<nArcsOutHA[j][0];i++){
////			System.out.println(ArcsOutHA.get(j).get(0)[i]);
//			printBlock(arrivalBlocks.get(ArcsOutHA.get(j).get(0)[i]));
//			System.out.print("  next  ");
//		}
//		System.out.println();
//		}
//		for (int i=0;i<nArcsOut0A[2];i++){
//			printBlock(arrivalBlocks.get(ArcsOutZeroA.get(2)[i]));
//			System.out.println();
//		}
		
//		for(int i=0;i<20;i++){
//			printBlock(arrivalBlocks.get(i));
//			System.out.println();
//		}
		
		try{
			//define new model
			IloCplex cplex = new IloCplex();

			//decision variables
			IloIntVar[] arrivalblock = new IloIntVar[nArrivalBlock];
			IloIntVar[] departureblock = new IloIntVar[nDepartureBlock];
			IloIntVar[][] coupledblock = new IloIntVar[nArrivalBlock][nDepartureBlock];

			//making all variables boolean
			arrivalblock = cplex.boolVarArray(nArrivalBlock);
			departureblock = cplex.boolVarArray(nArrivalBlock);
			for (int i=0;i<nArrivalBlock;i++){
				coupledblock[i] = cplex.boolVarArray(nDepartureBlock);
			}

			//define objective
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for(int i=0;i<nArrivalBlock;i++){
				for(int j=0; j<nDepartureBlock;j++){
					objective.addTerm(coupledblock[i][j], 1);
				}
			}
			cplex.addMinimize(objective);

			//add constraints

			//constraint ui=1
			IloLinearNumExpr[] sumUi = new IloLinearNumExpr[nArrivalTrain]; //for all arriving trains
			for(int ti=0;ti<nArrivalTrain;ti++){//for all arriving trains
				sumUi[ti] = cplex.linearNumExpr();
				for(int i=0; i<nArcsOut0A[ti];i++){ //sum over all arriving blocks
						sumUi[ti].addTerm(1.0,arrivalblock[ArcsOutZeroA.get(ti)[i]]);	
				}
			}
			//add the constraint
			for(int ti=0;ti<nArrivalTrain;ti++){//for all arriving trains
				cplex.addEq(1, sumUi[ti]);
			}
			
			//constraint ui-ui=0
//			IloLinearNumExpr[][] sumUiUi = new IloLinearNumExpr[nArrivalTrain][nNodes];
//			for(int ti=0;ti<nArrivalTrain;ti++){//for all arriving trains
//				IloLinearNumExpr[] sumUiUiTi = new IloLinearNumExpr[nIntermediatesA[ti]];
//				for(int n=0;n<nIntermediatesA[ti];n++){ //this is only number value out of intermediatesA
//					sumUiUiTi[n] = cplex.linearNumExpr();
//						for(int i=0;i<nArcsOutHA[ti][n];i++){
//							sumUiUiTi[n].addTerm(1.0, arrivalblock[ArcsOutHA.get(ti).get(n)[i]]);
//						} //intermediates are in same order n=0 corresponds to intemediate 1
//						for(int i=0;i<nArcsInHA[ti][n];i++){
//							sumUiUiTi[n].addTerm(-1.0,  arrivalblock[ArcsInHA.get(ti).get(n)[i]]);
//						}
//				}
//				sumUiUi[ti]=sumUiUiTi;
//			}
//			//add the constraint
//			for(int ti=0;ti<nArrivalTrain;ti++){
//				for(int n=0;n<nIntermediatesA[ti];n++){
//						cplex.addEq(0, sumUiUi[ti][n]); 
//				}
//			}
			IloLinearNumExpr[][] sumUiUiPos = new IloLinearNumExpr[nArrivalTrain][nNodes];
			IloLinearNumExpr[][] sumUiUiNeg = new IloLinearNumExpr[nArrivalTrain][nNodes];
			for(int ti=0;ti<nArrivalTrain;ti++){//for all arriving trains
				IloLinearNumExpr[] sumUiUiTiPos = new IloLinearNumExpr[nIntermediatesA[ti]];
				IloLinearNumExpr[] sumUiUiTiNeg = new IloLinearNumExpr[nIntermediatesA[ti]];
				for(int n=0;n<nIntermediatesA[ti];n++){ //this is only number value out of intermediatesA
					sumUiUiTiPos[n] = cplex.linearNumExpr();
					sumUiUiTiNeg[n] = cplex.linearNumExpr();
						for(int i=0;i<nArcsOutHA[ti][n+1];i++){
							sumUiUiTiPos[n].addTerm(1.0, arrivalblock[ArcsOutHA.get(ti).get(n+1)[i]]);
						} //intermediates are in same order n=0 corresponds to intemediate 1
						for(int i=0;i<nArcsInHA[ti][n+1];i++){
							sumUiUiTiNeg[n].addTerm(1.0,  arrivalblock[ArcsInHA.get(ti).get(n+1)[i]]);
						}
				}
				sumUiUiPos[ti]=sumUiUiTiPos;
				sumUiUiNeg[ti]=sumUiUiTiNeg;
			}
			//add the constraint
			for(int ti=0;ti<nArrivalTrain;ti++){
				for(int n=0;n<nIntermediatesA[ti];n++){
//					cplex.addEq(0, cplex.diff(sumUiUiPos[ti][n], sumUiUiNeg[ti][n])); 
					cplex.addEq(sumUiUiPos[ti][n], sumUiUiNeg[ti][n]);
				}
			}

//			//constraint vj=1
			IloLinearNumExpr[] sumVj = new IloLinearNumExpr[nDepartureTrain]; //for all arriving trains
			for(int tj=0;tj<nDepartureTrain;tj++){//for all arriving trains
				sumVj[tj] = cplex.linearNumExpr();
				for(int j=0; j<nArcsOut0D[tj];j++){ //sum over all arriving blocks
						sumVj[tj].addTerm(1.0,departureblock[ArcsOutZeroD.get(tj)[j]]);	
				}
			}
			//add the constraint
			for(int tj=0;tj<nDepartureTrain;tj++){//for all arriving trains
				cplex.addEq(1, sumVj[tj]);
			}
			
			//constraint vj-vj=0
//			IloLinearNumExpr[][] sumVjVj = new IloLinearNumExpr[nDepartureTrain][nNodes];
//			for(int tj=0;tj<nDepartureTrain;tj++){//for all arriving trains
//				IloLinearNumExpr[] sumVjVjTj = new IloLinearNumExpr[nIntermediatesD[tj]];
//				for(int n=0;n<nIntermediatesD[tj];n++){ //this is only number value out of intermediatesA
//					sumVjVjTj[n] = cplex.linearNumExpr();
//						for(int j=0;j<nArcsOutHD[tj][n];j++){
//								sumVjVjTj[n].addTerm(1.0, departureblock[ArcsOutHD.get(tj).get(n)[j]]);
//						} //intermediates are in same order n=0 corresponds to intemediate 1
//						for(int j=0;j<nArcsInHD[tj][n];j++){
//							sumVjVjTj[n].addTerm(-1.0,  departureblock[ArcsInHD.get(tj).get(n)[j]]);
//						}
//				}
//				sumVjVj[tj]=sumVjVjTj;
//			}
//			//add the constraint
//			for(int tj=0;tj<nDepartureTrain;tj++){
//				for(int n=0;n<nIntermediatesD[tj];n++){
//						cplex.addEq(0, sumVjVj[tj][n]); 
//				}
//			}
			IloLinearNumExpr[][] sumVjVjPos = new IloLinearNumExpr[nDepartureTrain][nNodes];
			IloLinearNumExpr[][] sumVjVjNeg = new IloLinearNumExpr[nDepartureTrain][nNodes];
			for(int tj=0;tj<nDepartureTrain;tj++){//for all arriving trains
				IloLinearNumExpr[] sumVjVjTjPos = new IloLinearNumExpr[nIntermediatesD[tj]];
				IloLinearNumExpr[] sumVjVjTjNeg = new IloLinearNumExpr[nIntermediatesD[tj]];
				for(int n=0;n<nIntermediatesD[tj];n++){ //this is only number value out of intermediatesA
					sumVjVjTjPos[n] = cplex.linearNumExpr();
					sumVjVjTjNeg[n] = cplex.linearNumExpr();
						for(int j=0;j<nArcsOutHD[tj][n+1];j++){
							sumVjVjTjPos[n].addTerm(1.0, departureblock[ArcsOutHD.get(tj).get(n+1)[j]]);
						} //intermediates are in same order n=0 corresponds to intemediate 1
						for(int j=0;j<nArcsInHD[tj][n+1];j++){
							sumVjVjTjNeg[n].addTerm(1.0,  departureblock[ArcsInHD.get(tj).get(n+1)[j]]);
						}
				}
				sumVjVjPos[tj]=sumVjVjTjPos;
				sumVjVjNeg[tj]=sumVjVjTjNeg;
			}
			//add the constraint
			for(int tj=0;tj<nDepartureTrain;tj++){
				for(int n=0;n<nIntermediatesD[tj];n++){
						cplex.addEq(sumVjVjPos[tj][n], sumVjVjNeg[tj][n]); 
				}
			}
			
			//constraint zij=ui 
			
			IloLinearNumExpr[] sumZijUi = new IloLinearNumExpr[nArrivalBlock];
			for(int i=0;i<nArrivalBlock;i++){//for all arriving blocks
				sumZijUi[i] = cplex.linearNumExpr();
				for(int j=0;j<nSameDepartures[i];j++){
						sumZijUi[i].addTerm(1.0,  coupledblock[i][SameDepartures.get(i).get(j)]);
				}
			}
			//add the constraint
			for(int i=0;i<nArrivalBlock;i++){
				cplex.addEq(arrivalblock[i], sumZijUi[i]);
			}
			
			//constraint zij=vj 
			IloLinearNumExpr[] sumZijVj = new IloLinearNumExpr[nDepartureBlock];
			for(int j=0;j<nDepartureBlock;j++){//for all arriving blocks
				sumZijVj[j] = cplex.linearNumExpr();
				for(int i=0;i<nSameArrivals[j];i++){
						sumZijVj[j].addTerm(1.0,  coupledblock[SameArrivals.get(j).get(i)][j]);
				}
			}
			//add the constraint
			for(int j=0;j<nDepartureBlock;j++){
				cplex.addEq(departureblock[j], sumZijVj[j]);
			}
			
			cplex.exportModel("MatchingModel.lp");
			System.out.println("Model exported");
			
			if(cplex.solve()){
				System.out.println("Problem Solved.");
//				for(int i=0;i<nArrivalBlock;i++){
//					for(int j=0; j<nDepartureBlock;j++){
//						if(cplex.getValue(coupledblock[i][j])==1){
//							System.out.println(cplex.getAlgorithm());
//							printBlock(arrivalBlocks.get(i));
//							printBlock(departureBlocks.get(j));//print the info on the coupled blocks
//							System.out.println("Next Match.");
//						}
//					}
//				}
			}
			
		} catch (IloException e){
			System.err.println("Concert exception '" + e + "' caught");
		}
	}

	public static boolean inArray(int[] array, int x){
		boolean check = false;
		for(int i=0;i<array.length;i++){
			if(array[i]==x){
				check = true;
			}
		}
		return check;
	}
	
	public static boolean inArrayList(ArrayList<Integer> list, int x){
		boolean check = false;
		for(int i=0;i<list.size();i++){
			if(list.get(i)==x){
				check = true;
			}
		}
		return check;
	}

	public static ArrayList<blocks> createBlocks(trainComposition c, ArrayList<blocks> b){
		int compositionSize = c.getTypes().size();
//		boolean arrival = c.getArrival();
		for(int i=0;i<compositionSize;i++){ //single blocks
			int[] arc = {i, i+1};
			ArrayList<trainType> types = new ArrayList<trainType>();
			types.add(c.getTypes().get(i));
			blocks x = new blocks(arc, c.getID(), types, c.getTime());
			b.add(x);
		}
		if(compositionSize>1){
			for(int i=0;i<compositionSize-1;i++){
				int[] arc = {i, i+2};
				ArrayList<trainType> types = new ArrayList<trainType>();
				types.add(c.getTypes().get(i));
				types.add(c.getTypes().get(i+1));
				blocks x = new blocks(arc, c.getID(), types, c.getTime());
				b.add(x);
			}
		}
		if(compositionSize>2){
			for(int i=0;i<compositionSize-2;i++){
				int[] arc = {i, i+3};
				ArrayList<trainType> types = new ArrayList<trainType>();
				types.add(c.getTypes().get(i));
				types.add(c.getTypes().get(i+1));
				types.add(c.getTypes().get(i+2));
				blocks x = new blocks(arc, c.getID(), types, c.getTime());
				b.add(x);
			}
		}
		if(compositionSize>3){
			for(int i=0;i<compositionSize-3;i++){
				int[] arc = {i, i+4};
				ArrayList<trainType> types = new ArrayList<trainType>();
				types.add(c.getTypes().get(i));
				types.add(c.getTypes().get(i+1));
				types.add(c.getTypes().get(i+2));
				types.add(c.getTypes().get(i+3));
				blocks x = new blocks(arc, c.getID(), types, c.getTime());
				b.add(x);
			}
		}
		if(compositionSize>4){
			for(int i=0;i<compositionSize-4;i++){
				int[] arc = {i, i+5};
				ArrayList<trainType> types = new ArrayList<trainType>();
				types.add(c.getTypes().get(i));
				types.add(c.getTypes().get(i+1));
				types.add(c.getTypes().get(i+2));
				types.add(c.getTypes().get(i+3));
				types.add(c.getTypes().get(i+4));
				blocks x = new blocks(arc, c.getID(), types, c.getTime());
				b.add(x);
			}
		}
		if(compositionSize>5){
			for(int i=0;i<compositionSize-5;i++){
				int[] arc = {i, i+6};
				ArrayList<trainType> types = new ArrayList<trainType>();
				types.add(c.getTypes().get(i));
				types.add(c.getTypes().get(i+1));
				types.add(c.getTypes().get(i+2));
				types.add(c.getTypes().get(i+3));
				types.add(c.getTypes().get(i+4));
				types.add(c.getTypes().get(i+5));
				blocks x = new blocks(arc, c.getID(), types, c.getTime());
				b.add(x);
			}
		}
		return b;
	}

	//return the locations of the blocks of that train and their arc
	public static int[][] getArcs(trainComposition c, ArrayList<blocks> arrivalblocks, ArrayList<blocks> departureblocks){
		int n = c.getTypes().size()*(c.getTypes().size()+1)/2; //number of arcs in train
		int[][] allArcs = new int[n][3]; //for all blocks location and arc[2]
		int count = 0;
		if(c.getArrival()){
			for(int i=0;i<arrivalblocks.size();i++){
				if(c.getID()==arrivalblocks.get(i).getParent()){
					allArcs[count][0] = i;
					allArcs[count][1] = arrivalblocks.get(i).getArc()[0];
					allArcs[count][2] = arrivalblocks.get(i).getArc()[1];
					count++;
				}
			}
		} else {
			for(int i=0;i<departureblocks.size();i++){
				if(c.getID()==departureblocks.get(i).getParent()){
					allArcs[count][0] = i;
					allArcs[count][1] = departureblocks.get(i).getArc()[0];
					allArcs[count][2] = departureblocks.get(i).getArc()[1];
					count++;
				}
			}
		}
//		ArrayList<arcs> arcs = new ArrayList<arcs>();
//		int compositionSize = c.getTypes().size();
//		for(int i=0;i<compositionSize;i++){ //single blocks
//			int[] arc = {i, i+1};
//			arcs.add(new arcs(arc));
//		}
//		if(compositionSize>1){
//			for(int i=0;i<compositionSize-1;i++){
//				int[] arc = {i, i+2};
//				arcs.add(new arcs(arc));
//			}
//		}
//		if(compositionSize>2){
//			for(int i=0;i<compositionSize-2;i++){
//				int[] arc = {i, i+3};
//				arcs.add(new arcs(arc));
//			}
//		}
//		if(compositionSize>3){
//			for(int i=0;i<compositionSize-3;i++){
//				int[] arc = {i, i+4};
//				arcs.add(new arcs(arc));
//			}
//		}
//		if(compositionSize>4){
//			for(int i=0;i<compositionSize-4;i++){
//				int[] arc = {i, i+5};
//				arcs.add(new arcs(arc));
//			}
//		}
//		if(compositionSize>5){
//			for(int i=0;i<compositionSize-5;i++){
//				int[] arc = {i, i+6};
//				arcs.add(new arcs(arc));
//			}
//		}
		return allArcs;
	}

	//subset of blocks (in locations) which belong to train c and arcs go out of h
	public static int[] getArcsOut(trainComposition c, int h, ArrayList<blocks> arrivalblocks, ArrayList<blocks> departureblocks){
		int size = c.getTypes().size()-h;
		int[] arcsOut = new int[size];
		int[][] arcsTotal = getArcs(c, arrivalblocks, departureblocks);
		
		int fillCount = 0;
		for(int i=0;i<arcsTotal.length;i++){
			if(arcsTotal[i][1]==h){ //the origin of the arc
				arcsOut[fillCount] = arcsTotal[i][0]; //the location of the block
				fillCount++;
			}
		}
		return arcsOut;
	}

	//subset of blocks (in locations) which belong to train c and arcs go into h
	public static int[] getArcsIn(trainComposition c, int h, ArrayList<blocks> arrivalblocks, ArrayList<blocks> departureblocks){
		int[] arcsIn = new int[h];
		int[][] arcsTotal = getArcs(c, arrivalblocks, departureblocks);
		int fillCount = 0;
		for(int i=0;i<arcsTotal.length;i++){
			if(arcsTotal[i][2]==h){ //destination of arc
				arcsIn[fillCount] = arcsTotal[i][0]; //the location of the block
				fillCount++;
			}
		}
		return arcsIn;
	}

	//only give number to fill into h so correct
	public static int[] getIntermediates(trainComposition c){
		int[] x = new int[c.getTypes().size()-1];
		for(int i=0; i< x.length;i++){
			x[i] = i+1;
		}
		return x;
	}

	//want to give the locations back not the blocks itself
	public static ArrayList<Integer> getSameDeparture(blocks i, ArrayList<blocks> departures){
		ArrayList<Integer> sames = new ArrayList<Integer>();
		for(int j=0;j<departures.size();j++){
			if(compareBlocks(departures.get(j), i)){
				sames.add(j); //location of same
			}
		}
		return sames;
	}

	//want to give the locations back not the blocks itself
	public static ArrayList<Integer> getSameArrival(blocks i, ArrayList<blocks> arrivals){
		ArrayList<Integer> sames = new ArrayList<Integer>();
		for(int j=0;j<arrivals.size();j++){
			if(compareBlocks(arrivals.get(j), i)){
				sames.add(j); //location of same
			}
		}
		return sames;
	}

	public static boolean compareBlocks(blocks i, blocks j){
		ArrayList<trainType> bi = i.getTypes();
		ArrayList<trainType> bj = j.getTypes();
		boolean match = false;
		if(bi.size()==bj.size()){
			match = true;
			for(int x=0 ; x< bi.size();x++){
				if(!bi.get(x).equals(bj.get(x))){
					match = false;
				}
			}
		}
		return match;
	}
	
	public void printBlock(blocks b){
		int ID = b.getParent();
		int time = b.getTime();
		ArrayList<trainType> types = b.getTypes();
		System.out.print(" ID: " + ID + " Time: " + time);
		for (int i=0;i<types.size();i++){
			System.out.print(" type " + types.get(i).getLength());
		}
	}

}