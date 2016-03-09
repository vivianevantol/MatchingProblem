import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.IIOException;
import ilog.concert.*;
import ilog.cplex.*;
/*
 * Constraint 1 en 3 creeren precies het aantal wat we moeten hebben dus klopt waarschijnlijk
 * Constraint 2 en 4 creeren te weinig constraints, zouden er MINSTENS net zoveel moeten zijn als 1 en 3
 */
public class MatchingProblem {
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

	private int numberOfTrains;
	
	public MatchingProblem(int maxTrack, int margin, initializeData data) throws IOException, IloException {
		solveMe(maxTrack, margin, data);
	}

	public void solveMe(int maxTrack, int margin, initializeData data) throws IOException, IloException {
		//lees benodigde data in
		margin = margin; //how much time between arrival en departure
		ArrayList<blocks> allblocks = new ArrayList<blocks>();
		ArrayList<blocks> arrivalblocks = new ArrayList<blocks>();
		ArrayList<blocks> departureblocks = new ArrayList<blocks>();

		ArrayList<trainComposition> arrivalTrains = new ArrayList<trainComposition>(); //set Ta
		ArrayList<trainComposition> departureTrains = new ArrayList<trainComposition>(); //set Td
		ArrayList<trainComposition> arrivalTrainsx = new ArrayList<trainComposition>(); //set Ta
		ArrayList<trainComposition> departureTrainsx = new ArrayList<trainComposition>(); //set Td
		ArrayList<trainComposition> allCompositions = data.getCompositions();

		//create lists with all arrival and all departure blocks
		//		int arrivals = 0;
		//		int departures =0;
		//		for(int i=0; i < data.getCompositions().size();i++){
		////			allblocks = createblocks(data.getCompositions().get(i), allblocks);
		//			int ID = data.getCompositions().get(i).getID();
		//			if(data.getCompositions().get(i).getArrival()){
		//				if(ID==83011 || ID==83013 || ID==81002 || ID==83021 || ID==80428 ||ID==80206){
		//				arrivalblocks = createblocks(data.getCompositions().get(i), arrivalblocks);
		//				arrivalTrains.add(data.getCompositions().get(i));
		////				System.out.println("Arr: " + data.getCompositions().get(i).getTypes().size());
		////				arrivals++;
		//				}
		//			} else {
		//				if(ID==80457 || ID==83048 || ID==83024 || ID==80207 || ID==83052 ||ID==83056 ||ID==83058){
		//				departureblocks = createblocks(data.getCompositions().get(i), departureblocks);
		//				departureTrains.add(data.getCompositions().get(i));
		////				System.out.println("Dep: " + data.getCompositions().get(i).getTypes().size());
		////				departures++;
		//				}
		//			}
		//		}

		for(int i=0; i < data.getCompositions().size();i++){
			allblocks = createblocks(data.getCompositions().get(i), allblocks);
			if(data.getCompositions().get(i).getArrival()){
				arrivalblocks = createblocks(data.getCompositions().get(i), arrivalblocks);
				arrivalTrains.add(data.getCompositions().get(i));
			} else {
				departureblocks = createblocks(data.getCompositions().get(i), departureblocks);
				departureTrains.add(data.getCompositions().get(i));
			}
		}

		//		for(int i=0;i<arrivalblocks.size();i++){
		//			printBlock(arrivalblocks.get(i));
		//			System.out.println();
		//		}
		//		System.out.println();
		//		for(int i=0;i<departureblocks.size();i++){
		//			printBlock(departureblocks.get(i));
		//			System.out.println();
		//		}


		this.nArrivalTrain = arrivalTrains.size();//19
		this.nDepartureTrain = departureTrains.size();//21
		this.nArrivalBlock = arrivalblocks.size();//41
		this.nDepartureBlock = departureblocks.size();//39
		this.nNodes = 10;
		this.numberOfTrains =0;

		//		for (int i=0;i<arrivalblocks.size();i++){
		//			printBlock(arrivalblocks.get(i));
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
			ArcsOutZeroA.add(getArcsOut(arrivalTrains.get(ti), 0, arrivalblocks, departureblocks));
			nArcsOut0A[ti] = ArcsOutZeroA.get(ti).length;
			nIntermediatesA[ti] = getIntermediates(arrivalTrains.get(ti)).length;//IntermediatesA.get(ti).length;



			ArcsOutHA.add(new ArrayList<int[]>()); //add list to fill in below
			for(int h=0;h<nIntermediatesA[ti]+1;h++){ //walk on h
				ArcsOutHA.get(ti).add(getArcsOut(arrivalTrains.get(ti), h, arrivalblocks, departureblocks));
				nArcsOutHA[ti][h]=getArcsOut(arrivalTrains.get(ti), h, arrivalblocks, departureblocks).length;
				//				System.out.println(ti + " ID " + h + " ");
				for(int p=0;p<nArcsOutHA[ti][h];p++){
					//					printBlock(arrivalblocks.get(ArcsOutHA.get(ti).get(h)[p]));
					//					System.out.println( "   " + ArcsOutHA.get(ti).get(h)[p]);

				}

			}

			ArcsInHA.add(new ArrayList<int[]>());
			for(int h=0;h<=nIntermediatesA[ti];h++){ //walk on h
				ArcsInHA.get(ti).add(getArcsIn(arrivalTrains.get(ti), h, arrivalblocks, departureblocks));
				nArcsInHA[ti][h]=getArcsIn(arrivalTrains.get(ti), h, arrivalblocks, departureblocks).length;
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
			ArcsOutZeroD.add(getArcsOut(departureTrains.get(ti), 0, arrivalblocks, departureblocks));
			nArcsOut0D[ti] = ArcsOutZeroD.get(ti).length;
			nIntermediatesD[ti] = getIntermediates(departureTrains.get(ti)).length;//IntermediatesA.get(ti).length;

			ArcsOutHD.add(new ArrayList<int[]>()); //add list to fill in below
			for(int h=0;h<=nIntermediatesD[ti];h++){ //walk on h
				ArcsOutHD.get(ti).add(getArcsOut(departureTrains.get(ti), h, arrivalblocks, departureblocks));
				nArcsOutHD[ti][h]=getArcsOut(departureTrains.get(ti), h, arrivalblocks, departureblocks).length;
			}

			ArcsInHD.add(new ArrayList<int[]>());
			for(int h=0;h<=nIntermediatesD[ti];h++){ //walk on h
				ArcsInHD.get(ti).add(getArcsIn(departureTrains.get(ti), h, arrivalblocks, departureblocks));
				nArcsInHD[ti][h]=getArcsIn(departureTrains.get(ti), h, arrivalblocks, departureblocks).length;
			}
		}

		ArrayList<ArrayList<Integer>> SameDepartures = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> SameArrivals = new ArrayList<ArrayList<Integer>>();

		nSameDepartures = new int[arrivalblocks.size()];
		nSameArrivals = new int[departureblocks.size()];

		for(int i=0;i<arrivalblocks.size();i++){
			SameDepartures.add(getSameDeparture(arrivalblocks.get(i),departureblocks, margin));
			nSameDepartures[i] = SameDepartures.get(i).size();
		}

		for(int j=0;j<departureblocks.size();j++){
			SameArrivals.add(getSameArrival(departureblocks.get(j),arrivalblocks, margin));
			nSameArrivals[j] = SameArrivals.get(j).size();
		}

		//		for(int j=0;j<5;j++){
		//		for (int i=0;i<nArcsOutHA[j][0];i++){
		////			System.out.println(ArcsOutHA.get(j).get(0)[i]);
		//			printBlock(arrivalblocks.get(ArcsOutHA.get(j).get(0)[i]));
		//			System.out.print("  next  ");
		//		}
		//		System.out.println();
		//		}
		//		for (int i=0;i<nArcsOut0A[2];i++){
		//			printBlock(arrivalblocks.get(ArcsOutZeroA.get(2)[i]));
		//			System.out.println();
		//		}

		//		for(int i=0;i<20;i++){
		//			printBlock(arrivalblocks.get(i));
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

			//constraint length arrivals
			for(int ba=0;ba<nArrivalBlock;ba++){
				cplex.addLe(cplex.prod(arrivalblock[ba], arrivalblocks.get(ba).getLength()), maxTrack);
			}
			//constraint length departures
			for(int ba=0;ba<nDepartureBlock;ba++){
				cplex.addLe(cplex.prod(departureblock[ba], departureblocks.get(ba).getLength()), maxTrack);
			}



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

				int count = 0;
				int[][] output = new int[(int) cplex.getValue(objective)][12];
				for(int i=0;i<nArrivalBlock;i++){
					for(int j=0; j<nDepartureBlock;j++){
						if(cplex.getValue(coupledblock[i][j])==1){ //blocks are coupled
							trainComposition arrivalComp = getComposition(arrivalblocks.get(i).getParent(), allCompositions);
							trainComposition departureComp = getComposition(departureblocks.get(j).getParent(), allCompositions);
							int first = 0;
							blocks used = null;
							//							System.out.println("Arrival: " + arrivalComp.getLength() + "  Departure: " + departureComp.getLength());
							if(arrivalComp.getLength()<=departureComp.getLength() && arrivalComp.getLength()!=0){
								first = arrivalComp.getID();
								used = arrivalblocks.get(i);
								//							} else if (arrivalComp.getLength()>=departureComp.getLength() && departureComp.getLength()!=0) {
							} else {
								first = departureComp.getID();
								used = departureblocks.get(j);
							}

							output[count][0] = first;
							output[count][1] = arrivalblocks.get(i).getParent();
							output[count][2] = departureblocks.get(j).getParent();
							output[count][3] = arrivalblocks.get(i).getTime();
							output[count][4] = departureblocks.get(j).getTime();
							output[count][5] = arrivalblocks.get(i).getTrack();
							output[count][6] = departureblocks.get(j).getTrack();
							//							output[count][7] = (int) arrivalblocks.get(i).getInspectionTime();
							//							output[count][8] = (int) arrivalblocks.get(i).getCleaningTime();
							//							output[count][9] = (int) arrivalblocks.get(i).getWashingTime();
							//							output[count][10] = (int) arrivalblocks.get(i).getRepairTime();
							output[count][7] = (int) used.getInspectionTime();
							output[count][8] = (int) used.getCleaningTime();
							output[count][9] = (int) used.getWashingTime();
							output[count][10] = (int) used.getRepairTime();
							output[count][11] = (int) used.getLength();
							count++;
						}
					}
				}
				this.numberOfTrains = count;

				//				printDoubleArray(output);
				System.out.println("Problem Solved.");
				writeExcel(output);
			}

		} catch (IloException e){
			System.err.println("Concert exception '" + e + "' caught");
		}
	}
	
	public int getNumberOfTrains(){
		return numberOfTrains;
	}

	public trainComposition getComposition(int ID, ArrayList<trainComposition> comps){
		trainComposition x = new trainComposition(new ArrayList<Train>(), new ArrayList<trainType>(), 0, true, 0, true);
		for(int i=0; i<comps.size();i++){
			if(comps.get(i).getID()==ID){ //composition found
				x = comps.get(i);
			}
		}
		return x;
	}

	public static void printDoubleArray(int[][] printer){
		for (int i=0;i<printer.length;i++){
			for(int j=0;j<printer[0].length;j++){
				System.out.print(printer[i][j] + "  " );
			}
			System.out.println();
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

	//with max
	public static ArrayList<blocks> createblocks(trainComposition c, ArrayList<blocks> b){
		//with itime is min(max+5, som)
		//with ctime is min(max+5, som)
		//with rtime is min(max+5, som)
		//with wtime is min(max+5, som)
		int compositionSize = c.getTypes().size();
		//		boolean arrival = c.getArrival();
		for(int i=0;i<compositionSize;i++){ //single blocks
			int[] arc = {i, i+1};
			ArrayList<trainType> types = new ArrayList<trainType>();
			ArrayList<Train> trains = new ArrayList<Train>();
			types.add(c.getTypes().get(i));
			if(c.getArrival()){
				trains.add(c.getTrains().get(i));
			}
			int track = 104;
			double ctime = types.get(0).getCleaningtime();
			double itime=0;
			double rtime=0;
			double wtime=0;
			int length = (int)types.get(0).getLength();
			if(c.get906a()){
				track = 906;
			}
			if(!trains.isEmpty()){
				if(trains.get(0).getInspect()){
					itime = types.get(0).getInspectiontime();
				}
				if(trains.get(0).getRepair()){
					rtime = types.get(0).getRepairtime();
				}
				if(trains.get(0).getWash()){
					wtime = types.get(0).getWashingtime();
				}
			}
			double atime = itime + ctime + wtime + rtime;
			blocks x = new blocks(arc, c.getID(), types, c.getTime(), track, atime, itime, ctime, wtime, rtime, length);
			b.add(x);
		}
		if(compositionSize>1){
			for(int i=0;i<compositionSize-1;i++){
				int[] arc = {i, i+2};
				ArrayList<trainType> types = new ArrayList<trainType>();
				ArrayList<Train> trains = new ArrayList<Train>();
				types.add(c.getTypes().get(i));
				types.add(c.getTypes().get(i+1));
				if(c.getArrival()){
					trains.add(c.getTrains().get(i));
					trains.add(c.getTrains().get(i+1));
				}
				int track = 104;
				double ctime = types.get(0).getCleaningtime()+types.get(1).getCleaningtime();
				double maxC= Double.max(types.get(0).getCleaningtime(),types.get(1).getCleaningtime());
				double itime =0;
				double maxI=0;
				double rtime =0;
				double maxR=0;
				double wtime =0;
				double maxW=0;
				int length = (int)types.get(0).getLength()+(int)types.get(1).getLength();
				if(c.get906a()){
					track = 906;
				}
				if(!trains.isEmpty()){
					if(trains.get(0).getInspect()){
						itime = itime+types.get(0).getInspectiontime();
						if(types.get(0).getInspectiontime()>maxI){
							maxI = types.get(0).getInspectiontime();
						}
					}
					if(trains.get(0).getRepair()){
						rtime = rtime+types.get(0).getRepairtime();
						if(types.get(0).getRepairtime()>maxR){
							maxR = types.get(0).getRepairtime();
						}
					}
					if(trains.get(0).getWash()){
						wtime = wtime+types.get(0).getWashingtime();
						if(types.get(0).getWashingtime()>maxW){
							maxW = types.get(0).getWashingtime();
						}
					}
					if(trains.get(1).getInspect()){
						itime = itime+types.get(1).getInspectiontime();
						if(types.get(1).getInspectiontime()>maxI){
							maxI = types.get(1).getInspectiontime();
						}
					}
					if(trains.get(1).getRepair()){
						rtime = rtime+types.get(1).getRepairtime();
						if(types.get(1).getRepairtime()>maxR){
							maxR = types.get(1).getRepairtime();
						}
					}
					if(trains.get(1).getWash()){
						wtime = wtime+types.get(1).getWashingtime();
						if(types.get(1).getWashingtime()>maxW){
							maxW = types.get(1).getWashingtime();
						}
					}
				}
				itime = Double.min(itime, maxI+5);
				rtime = Double.min(rtime, maxR+5);
				ctime = Double.min(ctime, maxC+5);
				wtime = Double.min(wtime, maxW+5);
				double atime = itime+ctime+wtime+rtime;
				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track, atime, itime, ctime, wtime, rtime, length);
				b.add(x);
			}
		}
		if(compositionSize>2){
			for(int i=0;i<compositionSize-2;i++){
				int[] arc = {i, i+3};
				ArrayList<trainType> types = new ArrayList<trainType>();
				ArrayList<Train> trains = new ArrayList<Train>();
				types.add(c.getTypes().get(i));
				types.add(c.getTypes().get(i+1));
				types.add(c.getTypes().get(i+2));
				if(c.getArrival()){
					trains.add(c.getTrains().get(i));
					trains.add(c.getTrains().get(i+1));
					trains.add(c.getTrains().get(i+2));
				}
				int track = 104;
				int length = (int)types.get(0).getLength()+(int)types.get(1).getLength()+(int)types.get(2).getLength();
				double ctime = types.get(0).getCleaningtime()+types.get(1).getCleaningtime()+types.get(2).getCleaningtime();
				double maxC= Double.max(types.get(0).getCleaningtime(),types.get(1).getCleaningtime());
				maxC = Double.max(maxC, types.get(2).getCleaningtime());
				double itime =0;
				double maxI=0;
				double rtime =0;
				double maxR=0;
				double wtime =0;
				double maxW=0;
				if(c.get906a()){
					track = 906;
				}
				if(!trains.isEmpty()){
					if(trains.get(0).getInspect()){
						itime = itime+types.get(0).getInspectiontime();
						if(types.get(0).getInspectiontime()>maxI){
							maxI = types.get(0).getInspectiontime();
						}
					}
					if(trains.get(0).getRepair()){
						rtime = rtime+types.get(0).getRepairtime();
						if(types.get(1).getRepairtime()>maxR){
							maxR = types.get(1).getRepairtime();
						}
					}
					if(trains.get(0).getWash()){
						wtime = wtime+types.get(0).getWashingtime();
						if(types.get(0).getWashingtime()>maxW){
							maxW = types.get(0).getWashingtime();
						}
					}
					if(trains.get(1).getInspect()){
						itime = itime+types.get(1).getInspectiontime();
						if(types.get(0).getInspectiontime()>maxI){
							maxI = types.get(0).getInspectiontime();
						}
					}
					if(trains.get(1).getRepair()){
						rtime = rtime+types.get(1).getRepairtime();
						if(types.get(1).getRepairtime()>maxR){
							maxR = types.get(1).getRepairtime();
						}
					}
					if(trains.get(1).getWash()){
						wtime = wtime+types.get(1).getWashingtime();
						if(types.get(0).getWashingtime()>maxW){
							maxW = types.get(0).getWashingtime();
						}
					}
					if(trains.get(2).getInspect()){
						itime = itime+types.get(2).getInspectiontime();
						if(types.get(0).getInspectiontime()>maxI){
							maxI = types.get(0).getInspectiontime();
						}
					}
					if(trains.get(2).getRepair()){
						rtime = rtime+types.get(2).getRepairtime();
						if(types.get(1).getRepairtime()>maxR){
							maxR = types.get(1).getRepairtime();
						}
					}
					if(trains.get(2).getWash()){
						wtime = wtime+types.get(2).getWashingtime();
						if(types.get(0).getWashingtime()>maxW){
							maxW = types.get(0).getWashingtime();
						}
					}
				}
				itime = Double.min(itime, maxI+5);
				rtime = Double.min(rtime, maxR+5);
				ctime = Double.min(ctime, maxC+5);
				wtime = Double.min(wtime, maxW+5);
				double atime = itime+ctime+wtime+rtime;
				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track, atime, itime, ctime, wtime, rtime, length);
				b.add(x);
			}
		}
		if(compositionSize>3){
			for(int i=0;i<compositionSize-3;i++){
				int[] arc = {i, i+4};
				ArrayList<trainType> types = new ArrayList<trainType>();
				ArrayList<Train> trains = new ArrayList<Train>();
				types.add(c.getTypes().get(i));
				types.add(c.getTypes().get(i+1));
				types.add(c.getTypes().get(i+2));
				types.add(c.getTypes().get(i+3));
				if(c.getArrival()){
					trains.add(c.getTrains().get(i));
					trains.add(c.getTrains().get(i+1));
					trains.add(c.getTrains().get(i+2));
					trains.add(c.getTrains().get(i+3));
				}
				int track = 104;
				int length = (int)types.get(0).getLength()+(int)types.get(1).getLength()+(int)types.get(2).getLength()+(int)types.get(3).getLength();
				double ctime = types.get(0).getCleaningtime()+types.get(1).getCleaningtime()+types.get(2).getCleaningtime()+types.get(3).getCleaningtime();
				double maxC= Double.max(types.get(0).getCleaningtime(),types.get(1).getCleaningtime());
				maxC = Double.max(maxC, types.get(2).getCleaningtime());
				maxC = Double.max(maxC, types.get(3).getCleaningtime());
				double itime =0;
				double maxI=0;
				double rtime =0;
				double maxR=0;
				double wtime =0;
				double maxW=0;
				if(c.get906a()){
					track = 906;
				}
				if(!trains.isEmpty()){
					if(trains.get(0).getInspect()){
						itime = itime+types.get(0).getInspectiontime();
						if(types.get(0).getInspectiontime()>maxI){
							maxI = types.get(0).getInspectiontime();
						}
					}
					if(trains.get(0).getRepair()){
						rtime = rtime+types.get(0).getRepairtime();
						if(types.get(1).getRepairtime()>maxR){
							maxR = types.get(1).getRepairtime();
						}
					}
					if(trains.get(0).getWash()){
						wtime = wtime+types.get(0).getWashingtime();
						if(types.get(0).getWashingtime()>maxW){
							maxW = types.get(0).getWashingtime();
						}
					}
					if(trains.get(1).getInspect()){
						itime = itime+types.get(1).getInspectiontime();
						if(types.get(0).getInspectiontime()>maxI){
							maxI = types.get(0).getInspectiontime();
						}
					}
					if(trains.get(1).getRepair()){
						rtime = rtime+types.get(1).getRepairtime();
						if(types.get(1).getRepairtime()>maxR){
							maxR = types.get(1).getRepairtime();
						}
					}
					if(trains.get(1).getWash()){
						wtime = wtime+types.get(1).getWashingtime();
						if(types.get(0).getWashingtime()>maxW){
							maxW = types.get(0).getWashingtime();
						}
					}
					if(trains.get(2).getInspect()){
						itime = itime+types.get(2).getInspectiontime();
						if(types.get(0).getInspectiontime()>maxI){
							maxI = types.get(0).getInspectiontime();
						}
					}
					if(trains.get(2).getRepair()){
						rtime = rtime+types.get(2).getRepairtime();
						if(types.get(1).getRepairtime()>maxR){
							maxR = types.get(1).getRepairtime();
						}
					}
					if(trains.get(2).getWash()){
						wtime = wtime+types.get(2).getWashingtime();
						if(types.get(0).getWashingtime()>maxW){
							maxW = types.get(0).getWashingtime();
						}
					}
					if(trains.get(3).getInspect()){
						itime = itime+types.get(3).getInspectiontime();
						if(types.get(0).getInspectiontime()>maxI){
							maxI = types.get(0).getInspectiontime();
						}
					}
					if(trains.get(3).getRepair()){
						rtime = rtime+types.get(3).getRepairtime();
						if(types.get(1).getRepairtime()>maxR){
							maxR = types.get(1).getRepairtime();
						}
					}
					if(trains.get(3).getWash()){
						wtime = wtime+types.get(3).getWashingtime();
						if(types.get(0).getWashingtime()>maxW){
							maxW = types.get(0).getWashingtime();
						}
					}
				}
				itime = Double.min(itime, maxI+5);
				rtime = Double.min(rtime, maxR+5);
				ctime = Double.min(ctime, maxC+5);
				wtime = Double.min(wtime, maxW+5);
				double atime = itime + ctime + wtime + rtime;
				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track, atime, itime, ctime, wtime, rtime, length);
				b.add(x);
			}
		}
		return b;
	}

	//public static ArrayList<blocks> createblocks(trainComposition c, ArrayList<blocks> b){
	//		int compositionSize = c.getTypes().size();
	////		boolean arrival = c.getArrival();
	//		for(int i=0;i<compositionSize;i++){ //single blocks
	//			int[] arc = {i, i+1};
	//			ArrayList<trainType> types = new ArrayList<trainType>();
	//			ArrayList<Train> trains = new ArrayList<Train>();
	//			types.add(c.getTypes().get(i));
	//			if(c.getArrival()){
	//				trains.add(c.getTrains().get(i));
	//			}
	//			int track = 104;
	//			double ctime = types.get(0).getCleaningtime();
	//			double itime=0;
	//			double rtime=0;
	//			double wtime=0;
	//			int length = (int)types.get(0).getLength();
	//			if(c.get906a()){
	//				track = 906;
	//			}
	//			if(!trains.isEmpty()){
	//				if(trains.get(0).getInspect()){
	//					itime = types.get(0).getInspectiontime();
	//				}
	//				if(trains.get(0).getRepair()){
	//					rtime = types.get(0).getRepairtime();
	//				}
	//				if(trains.get(0).getWash()){
	//					wtime = types.get(0).getWashingtime();
	//				}
	//			}
	//			double atime = itime + ctime + wtime + rtime;
	//			blocks x = new blocks(arc, c.getID(), types, c.getTime(), track, atime, itime, ctime, wtime, rtime, length);
	//			b.add(x);
	//		}
	//		if(compositionSize>1){
	//			for(int i=0;i<compositionSize-1;i++){
	//				int[] arc = {i, i+2};
	//				ArrayList<trainType> types = new ArrayList<trainType>();
	//				ArrayList<Train> trains = new ArrayList<Train>();
	//				types.add(c.getTypes().get(i));
	//				types.add(c.getTypes().get(i+1));
	//				if(c.getArrival()){
	//					trains.add(c.getTrains().get(i));
	//					trains.add(c.getTrains().get(i+1));
	//				}
	//				int track = 104;
	//				double ctime = types.get(0).getCleaningtime()+types.get(1).getCleaningtime();
	//				double itime =0;
	//				double rtime =0;
	//				double wtime =0;
	//				int length = (int)types.get(0).getLength()+(int)types.get(1).getLength();
	//				if(c.get906a()){
	//					track = 906;
	//				}
	//				if(!trains.isEmpty()){
	//					if(trains.get(0).getInspect()){
	//						itime = itime+types.get(0).getInspectiontime();
	//					}
	//					if(trains.get(0).getRepair()){
	//						rtime = rtime+types.get(0).getRepairtime();
	//					}
	//					if(trains.get(0).getWash()){
	//						wtime = wtime+types.get(0).getWashingtime();
	//					}
	//					if(trains.get(1).getInspect()){
	//						itime = itime+types.get(1).getInspectiontime();
	//					}
	//					if(trains.get(1).getRepair()){
	//						rtime = rtime+types.get(1).getRepairtime();
	//					}
	//					if(trains.get(1).getWash()){
	//						wtime = wtime+types.get(1).getWashingtime();
	//					}
	//				}
	//				double atime = itime+ctime+wtime+rtime;
	//				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track, atime, itime, ctime, wtime, rtime, length);
	//				b.add(x);
	//			}
	//		}
	//		if(compositionSize>2){
	//			for(int i=0;i<compositionSize-2;i++){
	//				int[] arc = {i, i+3};
	//				ArrayList<trainType> types = new ArrayList<trainType>();
	//				ArrayList<Train> trains = new ArrayList<Train>();
	//				types.add(c.getTypes().get(i));
	//				types.add(c.getTypes().get(i+1));
	//				types.add(c.getTypes().get(i+2));
	//				if(c.getArrival()){
	//					trains.add(c.getTrains().get(i));
	//					trains.add(c.getTrains().get(i+1));
	//					trains.add(c.getTrains().get(i+2));
	//				}
	//				int track = 104;
	//				double itime =0;
	//				double rtime =0;
	//				double wtime =0;
	//				int length = (int)types.get(0).getLength()+(int)types.get(1).getLength()+(int)types.get(2).getLength();
	//				double ctime = types.get(0).getCleaningtime()+types.get(1).getCleaningtime()+types.get(2).getCleaningtime();
	//				if(c.get906a()){
	//					track = 906;
	//				}
	//				if(!trains.isEmpty()){
	//					if(trains.get(0).getInspect()){
	//						itime = itime+types.get(0).getInspectiontime();
	//					}
	//					if(trains.get(0).getRepair()){
	//						rtime = rtime+types.get(0).getRepairtime();
	//					}
	//					if(trains.get(0).getWash()){
	//						wtime = wtime+types.get(0).getWashingtime();
	//					}
	//					if(trains.get(1).getInspect()){
	//						itime = itime+types.get(1).getInspectiontime();
	//					}
	//					if(trains.get(1).getRepair()){
	//						rtime = rtime+types.get(1).getRepairtime();
	//					}
	//					if(trains.get(1).getWash()){
	//						wtime = wtime+types.get(1).getWashingtime();
	//					}
	//					if(trains.get(2).getInspect()){
	//						itime = itime+types.get(2).getInspectiontime();
	//					}
	//					if(trains.get(2).getRepair()){
	//						rtime = rtime+types.get(2).getRepairtime();
	//					}
	//					if(trains.get(2).getWash()){
	//						wtime = wtime+types.get(2).getWashingtime();
	//					}
	//				}
	//				double atime = itime+ctime+wtime+rtime;
	//				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track, atime, itime, ctime, wtime, rtime, length);
	//				b.add(x);
	//			}
	//		}
	//		if(compositionSize>3){
	//			for(int i=0;i<compositionSize-3;i++){
	//				int[] arc = {i, i+4};
	//				ArrayList<trainType> types = new ArrayList<trainType>();
	//				ArrayList<Train> trains = new ArrayList<Train>();
	//				types.add(c.getTypes().get(i));
	//				types.add(c.getTypes().get(i+1));
	//				types.add(c.getTypes().get(i+2));
	//				types.add(c.getTypes().get(i+3));
	//				if(c.getArrival()){
	//					trains.add(c.getTrains().get(i));
	//					trains.add(c.getTrains().get(i+1));
	//					trains.add(c.getTrains().get(i+2));
	//					trains.add(c.getTrains().get(i+3));
	//				}
	//				int track = 104;
	//				double itime =0;
	//				double rtime =0;
	//				double wtime =0;
	//				int length = (int)types.get(0).getLength()+(int)types.get(1).getLength()+(int)types.get(2).getLength()+(int)types.get(3).getLength();
	//				double ctime = types.get(0).getCleaningtime()+types.get(1).getCleaningtime()+types.get(2).getCleaningtime()+types.get(3).getCleaningtime();
	//				if(c.get906a()){
	//					track = 906;
	//				}
	//				if(!trains.isEmpty()){
	//					if(trains.get(0).getInspect()){
	//						itime = itime+types.get(0).getInspectiontime();
	//					}
	//					if(trains.get(0).getRepair()){
	//						rtime = rtime+types.get(0).getRepairtime();
	//					}
	//					if(trains.get(0).getWash()){
	//						wtime = wtime+types.get(0).getWashingtime();
	//					}
	//					if(trains.get(1).getInspect()){
	//						itime = itime+types.get(1).getInspectiontime();
	//					}
	//					if(trains.get(1).getRepair()){
	//						rtime = rtime+types.get(1).getRepairtime();
	//					}
	//					if(trains.get(1).getWash()){
	//						wtime = wtime+types.get(1).getWashingtime();
	//					}
	//					if(trains.get(2).getInspect()){
	//						itime = itime+types.get(2).getInspectiontime();
	//					}
	//					if(trains.get(2).getRepair()){
	//						rtime = rtime+types.get(2).getRepairtime();
	//					}
	//					if(trains.get(2).getWash()){
	//						wtime = wtime+types.get(2).getWashingtime();
	//					}
	//					if(trains.get(3).getInspect()){
	//						itime = itime+types.get(3).getInspectiontime();
	//					}
	//					if(trains.get(3).getRepair()){
	//						rtime = rtime+types.get(3).getRepairtime();
	//					}
	//					if(trains.get(3).getWash()){
	//						wtime = wtime+types.get(3).getWashingtime();
	//					}
	//				}
	//				double atime = itime + ctime + wtime + rtime;
	//				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track, atime, itime, ctime, wtime, rtime, length);
	//				b.add(x);
	//			}
	//		}
	//		return b;
	//	}



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

	public static ArrayList<Integer> getSameDeparture(blocks i, ArrayList<blocks> departures, int margin){
		ArrayList<Integer> sames = new ArrayList<Integer>();
		for(int j=0;j<departures.size();j++){
			blocks nowJ = departures.get(j);
			if(compareblocks(nowJ, i) && nowJ.getTime()-i.getTime()>=margin+i.getActivityTime()){ //so same types
				sames.add(j); //location of same
			}
		}
		return sames;
	}

	//want to give the locations back not the blocks itself

	public static ArrayList<Integer> getSameArrival(blocks i, ArrayList<blocks> arrivals, int margin){
		ArrayList<Integer> sames = new ArrayList<Integer>();
		for(int j=0;j<arrivals.size();j++){
			blocks nowJ = arrivals.get(j);
			if(compareblocks(nowJ, i) && i.getTime()- nowJ.getTime()>=margin+nowJ.getActivityTime()){
				sames.add(j); //location of same
			}
		}
		return sames;
	}

	public static boolean compareblocks(blocks i, blocks j){
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

	public void writeExcel(int[][] matrix){
		for(int i=0;i<matrix.length-1;i++){ //check coupling and decoupling
			int[][] subblocks = Arrays.copyOfRange(matrix, i, matrix.length); //check same arrival or departure
			for(int j=1;j<subblocks.length;j++){

				if(matrix[i][3]==subblocks[j][3]){ //decoupling
					matrix[i][3] = matrix[i][3]+2;
					matrix[i+j][3] = matrix[i+j][3]+2;
				}
				if(matrix[i][4]==subblocks[j][4]){ //decoupling
					matrix[i][4] = matrix[i][4]-3;
					matrix[i+j][4] = matrix[i+j][4]-3;
				}
			}
		}

		FileWriter fileWriter = null;
		String FILE_HEADER = "Composition;ID A;ID D;Arrival;Departure;Track A;Track D";
		String COMMA_DELIMITER = ";"; //maybe this must be comma
		String NEW_LINE_SEPARATOR = "\r\n";


		try{
			String name = "CompositionTimesMatching.csv";
			fileWriter = new FileWriter(name);
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			for(int i=0;i<matrix.length;i++){ //each line

				for(int j=0;j<matrix[0].length;j++){ //each cel
					fileWriter.append(Integer.toString(matrix[i][j]));
					fileWriter.append(COMMA_DELIMITER);
				}
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
		} catch (Exception e) {
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {

			}
		}
	}
}