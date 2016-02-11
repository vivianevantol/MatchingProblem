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
		ArrayList<trainComposition> allCompositions = data.getCompositions();

		this.nArrivalTrain = 19;
		this.nDepartureTrain = 21;
		this.nArrivalBlock = 41;
		this.nDepartureBlock = 39;
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
				int[] set = getArcsOut(allCompositions.get(ti), 0);
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
				int[] set = getIntermediates(allCompositions.get(ti));
				for(int n=0;n<nNodes;n++){
					if(inArray(set, n)){
						int[] set2 = getArcsOut(allCompositions.get(ti), n);
						for(int i=0;i<nArrivalBlock;i++){
							if(inArray(set2, i)){
								sumUiUi[ti][n].addTerm(1.0, arrivalblock[i]);
							}
						}
					}
				}
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

	public static ArrayList<arcs> getArcs(trainComposition c){
		ArrayList<arcs> arcs = new ArrayList<arcs>();
		int compositionSize = c.getTypes().size();
		for(int i=0;i<compositionSize;i++){ //single blocks
			int[] arc = {i, i+1};
			arcs.add(new arcs(arc));
		}
		if(compositionSize>1){
			for(int i=0;i<compositionSize-1;i++){
				int[] arc = {i, i+2};
				arcs.add(new arcs(arc));
			}
		}
		if(compositionSize>2){
			for(int i=0;i<compositionSize-2;i++){
				int[] arc = {i, i+3};
				arcs.add(new arcs(arc));
			}
		}
		if(compositionSize>3){
			for(int i=0;i<compositionSize-3;i++){
				int[] arc = {i, i+4};
				arcs.add(new arcs(arc));
			}
		}
		if(compositionSize>4){
			for(int i=0;i<compositionSize-4;i++){
				int[] arc = {i, i+5};
				arcs.add(new arcs(arc));
			}
		}
		if(compositionSize>5){
			for(int i=0;i<compositionSize-5;i++){
				int[] arc = {i, i+6};
				arcs.add(new arcs(arc));
			}
		}
		return arcs;
	}

	public static int[] getArcsOut(trainComposition c, int h){
		//		ArrayList<arcs> arcsOut = new ArrayList<arcs>();
		int size = c.getTypes().size()-h;
		int[] arcsOut = new int[size];
		ArrayList<arcs> arcsTotal = getArcs(c);
		int fillCount = 0;
		for(int i=0;i<arcsTotal.size();i++){
			if(arcsTotal.get(i).getArc()[0]==h){
				//				arcsOut.add(arcsTotal.get(i));
				arcsOut[fillCount] = arcsTotal.get(i).getArc()[1];
				fillCount++;
			}
		}
		return arcsOut;
	}

	public static ArrayList<arcs> getArcsIn(trainComposition c, int h){
		ArrayList<arcs> arcsIn = new ArrayList<arcs>();
		ArrayList<arcs> arcsTotal = getArcs(c);
		for(int i=0;i<arcsTotal.size();i++){
			if(arcsTotal.get(i).getArc()[1]==h){
				arcsIn.add(arcsTotal.get(i));
			}
		}
		return arcsIn;
	}

	public static int[] getIntermediates(trainComposition c){
		int[] x = new int[c.getTypes().size()-1];
		for(int i=0; i< x.length;i++){
			x[i] = i+1;
		}
		return x;
	}

	public static ArrayList<blocks> getSameDeparture(blocks i, ArrayList<blocks> departures){
		ArrayList<blocks> sames = new ArrayList<blocks>();
		for(int j=0;j<departures.size();j++){
			if(compareBlocks(departures.get(j), i)){
				sames.add(departures.get(j));
			}
		}
		return sames;
	}

	public static ArrayList<blocks> getSameArrival(blocks i, ArrayList<blocks> arrivals){
		ArrayList<blocks> sames = new ArrayList<blocks>();
		for(int j=0;j<arrivals.size();j++){
			if(compareBlocks(arrivals.get(j), i)){
				sames.add(arrivals.get(j));
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