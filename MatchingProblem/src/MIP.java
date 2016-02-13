import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.IIOException;
import ilog.concert.*;
import ilog.cplex.*;

public class MIP {
	//Sets
	public int nArrivalTrain; //ti
	public int nDepartureTrain; //tj
	public int nArrivalBlock; //i
	public int nDepartureBlock; //j
	public int nNodes; //n

	public MIP() throws IOException, IloException {
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
		ArrayList<trainComposition> allCompositions = data.getCompositions();
		
		//create lists with all arrival and all departure blocks
		int arrivals = 0;
		int departures =0;
		for(int i=0; i < data.getCompositions().size();i++){
			allBlocks = createBlocks(data.getCompositions().get(i), allBlocks);
			if(data.getCompositions().get(i).getArrival()){
				arrivalBlocks = createBlocks(data.getCompositions().get(i), arrivalBlocks);
				arrivalTrains.add(data.getCompositions().get(i));
				arrivals++;
			} else {
				departureBlocks = createBlocks(data.getCompositions().get(i), departureBlocks);
				departureTrains.add(data.getCompositions().get(i));
				departures++;
			}
		}
		
		this.nArrivalTrain = arrivalTrains.size();//19
		this.nDepartureTrain = departureTrains.size();//21
		this.nArrivalBlock = arrivalBlocks.size();//41
		this.nDepartureBlock = departureBlocks.size();//39
		this.nNodes = 10;

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
				int[] set = getArcsOut(allCompositions.get(ti), 0, arrivalBlocks, departureBlocks);
				for(int i=0; i<nArrivalBlock;i++){ //sum over all arriving blocks
					if(inArray(set,i)){ //alternative way of taking subset of arrivalblocks
						sumUi[ti].addTerm(1.0,arrivalblock[i]);	
					}
				}
			}
			//add the constraint
			for(int ti=0;ti<nArrivalTrain;ti++){//for all arriving trains
				cplex.addEq(1, sumUi[ti]);
			}
			
			//constraint ui-ui=0
			IloLinearNumExpr[][] sumUiUi = new IloLinearNumExpr[nArrivalTrain][nNodes];
			for(int ti=0;ti<nArrivalTrain;ti++){//for all arriving trains
				IloLinearNumExpr[] sumUiUiTi = new IloLinearNumExpr[nNodes];
				int[] set = getIntermediates(allCompositions.get(ti));
				for(int n=0;n<nNodes;n++){
					sumUiUiTi[n] = cplex.linearNumExpr();
					if(inArray(set, n)){
						int[] setout = getArcsOut(allCompositions.get(ti), n, arrivalBlocks, departureBlocks);
						int[] setin = getArcsIn(allCompositions.get(ti), n, arrivalBlocks, departureBlocks);
						for(int i=0;i<nArrivalBlock;i++){
							if(inArray(setout, i)){
								sumUiUiTi[n].addTerm(1.0, arrivalblock[i]);
							}
							if(inArray(setin, i)){
								sumUiUiTi[n].addTerm(-1.0,  arrivalblock[i]);
							}
						}
					}
				}
				sumUiUi[ti]=sumUiUiTi;
			}
			//add the constraint
			for(int ti=0;ti<nArrivalTrain;ti++){
				int[] set = getIntermediates(allCompositions.get(ti));
				for(int n=0;n<nNodes;n++){
					if(inArray(set, n)){
						cplex.addEq(0, sumUiUi[ti][n]);
					}
				}
			}



		} finally {};
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
		boolean arrival = c.getArrival();
		for(int i=0;i<compositionSize;i++){ //single blocks
			int[] arc = {i, i+1};
			ArrayList<trainType> types = new ArrayList<trainType>();
			types.add(c.getTypes().get(i));
			blocks x = new blocks(arc, c.getID(), types);
			b.add(x);
		}
		if(compositionSize>1){
			for(int i=0;i<compositionSize-1;i++){
				int[] arc = {i, i+2};
				ArrayList<trainType> types = new ArrayList<trainType>();
				types.add(c.getTypes().get(i));
				types.add(c.getTypes().get(i+1));
				blocks x = new blocks(arc, c.getID(), types);
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
				blocks x = new blocks(arc, c.getID(), types);
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
				blocks x = new blocks(arc, c.getID(), types);
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
				blocks x = new blocks(arc, c.getID(), types);
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
				blocks x = new blocks(arc, c.getID(), types);
				b.add(x);
			}
		}
		return b;
	}

	//return the locations of the blocks of that train and their arc
	public static int[][] getArcs(trainComposition c, ArrayList<blocks> arrivalblocks, ArrayList<blocks> departureblocks){
		int All = arrivalblocks.size()+departureblocks.size();
		int n = c.getTypes().size()*(c.getTypes().size()-1)/2; //number of arcs in train
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

}