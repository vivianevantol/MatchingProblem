import ilog.concert.IloException;

//import ilog.concert.IloException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TotalMain {

	public static void main(String[] args) throws IOException, IOException, IloException {
		// TODO Auto-generated method stub
		//CHECK WHETER THE RIGHT FILE IS SELECTED IN INTIIALIZE EVENTLIST AND IN INITIALIZE BLOCKINFO
//=============================INITIALIZE VARIABES==========================================================================
		long timeBefore = System.currentTimeMillis();
		int counterdeparture = 0;
		int countervolledigfeasible = 0;
		int countervolledigfeasiblehelemaal = 0;
		double avgactivity = 0;

//=============================GIVE INPUT===================================================================================
		int MatchingMargin = 20;
		int maxBlock = 247;
		int nriterations = 100;

		for (int i = 0; i < nriterations ; i++){
			System.out.println("Iteration: " + (i+1));
			long timeItStart = System.currentTimeMillis();
			long timeMatchStart = System.currentTimeMillis();
			initializeData data = new initializeData();
			
			
//==============================EXECUTE MATCHING============================================================================
			int nrTrains = 0;
			while(nrTrains==0){
				System.out.println("try match");
				try{ //directly prints output into "CompositionTimesMatching.csv"
					data = null;
					data = new initializeData(); //create the data set
					MatchingProblem matching = new MatchingProblem(maxBlock, MatchingMargin, data);
					nrTrains = matching.getNumberOfTrains();
					matching = null;
				} catch(IloException | IOException e) {
					System.out.println("Error");
				} //Check what file is used in initializeEventList??!!
			}
			long timeMatchEnd = System.currentTimeMillis();
			System.out.println("Matching Executed in " + (timeMatchEnd-timeMatchStart));
			long timeJobStart = System.currentTimeMillis();
			
			InitializeShuntingYard yard = new InitializeShuntingYard(); //create the shunting yard
			initializeEventList eventList = new initializeEventList(); //create the eventlist
			int[][] blockdata = initializeBlockInfo(nrTrains);
			
			int[][] departures = eventList.getDeparturelist();
			int[][] arrivals = eventList.getArrivallist();
			int[][] trainInfo = new int[nrTrains][4]; //ID Atime Dtime Length
			for (int x=0;x<trainInfo.length;x++){
				trainInfo[x][0] = departures[x][1];
				trainInfo[x][1] = arrivals[x][0];
				trainInfo[x][2] = departures[x][0];
//				trainInfo[x][3] = (int) getLength(departures[x][1], data);
				trainInfo[x][3] = blockdata[x][11];
			}
			String trainInf = "trainInfo";
			String infoname = trainInf.concat(Integer.toString(i+1));
			writeExcel(trainInfo, infoname.concat(".csv"));
			ArrayList<Track> tracks = yard.getTracks();
			int[][] trackInfo = new int[tracks.size()][3]; //lengt left right
			for(int s=0;s<tracks.size();s++){
				trackInfo[s][0] = (int) tracks.get(s).getLength();
				trackInfo[s][1] = 0;
				trackInfo[s][2] = 0;
			}
//			printDoubleArray(trackInfo);
			
//============================EXECUTE JOBSHOP===============================================================================
			ArrayList<Jobs> allJobs = new ArrayList<Jobs>();
			ArrayList<Jobs> oneJobs = new ArrayList<Jobs>();
			ArrayList<Jobs> twoJobs = new ArrayList<Jobs>();
//			ArrayList<Train> trains = data.getTrains();

			int maxD = 0;
			for(int j=0;j<departures.length;j++){
				if(departures[j][0]>maxD && departures[j][0]<10000){
					maxD = departures[j][0];
				}
			}
			//initialize all jobs (the cleaning platform jobs with repair on them)

			for (int j=0; j<nrTrains;j++){
				int qj = maxD-blockdata[j][4]+blockdata[j][9]+2; //D-departure+wash+2
				int rj = blockdata[j][3]+2; //arrival+2
				int p1j = blockdata[j][8]+ blockdata[j][10];//cleaning+repair
				int p2j = p1j;
				allJobs.add(new Jobs(j+1,qj,rj,p1j,p2j, blockdata[j][0]));
			}
			
			HeuristicJobShop jobshop = new HeuristicJobShop(allJobs, oneJobs, twoJobs);
			int[][] output = jobshop.solver(); //this is already sorted on starting times

			ArrayList<Integer> M1 = new ArrayList<Integer>();
			ArrayList<Integer> M2 = new ArrayList<Integer>();
			for(int j=0;j<output.length;j++){
				if(output[j][1]==1){
					M1.add(output[j][3]);
				} else {
					M2.add(output[j][3]);
				}
			}
			long timeJobEnd = System.currentTimeMillis();
			System.out.println("Job Shop Executed in " + (timeJobEnd-timeJobStart));
			long timeParkingStart  = System.currentTimeMillis();
//============================EXECUTE PARKING===============================================================================
			int firstblock = 0;
			int bl=0;
			while(bl<blockdata.length){
				if(blockdata[bl][3]>500){
					firstblock = bl;
					bl=100;
				}
				bl++;
			}
			int secondblock = nrTrains-firstblock;
			
			int zero = 1;
			int[][] outputParking = new int[nrTrains][2];
//			System.out.println("test: " + firstblock + "  " + secondblock);

//			ParkingProblem ParkingProblem = new ParkingProblem(firstblock, secondblock, blockdata, 500, yard, eventList);
//			outputParking = ParkingProblem.returnOutput();
//			zero = ParkingProblem.getObjective();

			long timeParkingEnd  = System.currentTimeMillis();
			System.out.println("Parking Executed in " + (timeParkingEnd-timeParkingStart));
			long timeHeurStart = System.currentTimeMillis();
			
//=====================DEFINE INPUT FOR HEURISTIC!!!!=======================================================================
			ArrayList<Integer> priorityPlatform1 = new ArrayList<Integer>();
			ArrayList<Integer> priorityPlatform2 = new ArrayList<Integer>();
			priorityPlatform1.addAll(M1);
			priorityPlatform2.addAll(M2);
			//if also try other platform  ================================> CAN ALTER
			priorityPlatform1.addAll(M2);
			priorityPlatform2.addAll(M1);

			int[] priorityArrivaltrack = {1, 2, 3, 4}; 
//			int[] priorityArrival =  { 35, 38, 31, 40, 37, 33, 34, 25, 26, 27, 19, 24, 12, 18, 5, 11, 61, 62};
			int[] priorityArrival =  { 35, 38, 31, 40, 37, 33, 34,   62, 61};
			int[] priorityType1 = {48, 50, 49, 52, 53, 54}; // Internal , 49, 53, 50, 54, 51, 55
			int[] priorityType2 = {56}; // External , 57, 58, 59, 60
			int[] priorityType3 = {11, 5, 18, 12, 24,19, 30, 25, 34, 31, 11, 18, 24, 30, 10,  17, 23, 29, 9, 16, 22, 28, 8, 15, 21, 27, 7, 14, 20, 26, 6, 13, 19, 25,5, 12}; // depart
			int[] priorityType4 = {4, 3, 2, 1}; //departing track
			int[] priorityType4extra = {61, 62}; // other departing track
			double [] results = new double [2];
			
//	   int[][] positionsPerTrack = {
//			{1, 2, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//t906a vrijhouden / aankomst+depart area
//			{5, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0},//t52 depart area
//			{12, 13, 14, 15, 16, 17, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0},//53 depart area
//			{19, 20, 21, 22, 23, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//54 depart area
//			{25, 26, 27, 28, 29, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//t55 depart area
//			{31, 32, 33, 34, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t56 arrival area
//			{35, 36, 37, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t57 arrival area
//			{38, 39, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//t58 arrival area
//			{41, 42, 43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t59 arrival area
//			{44, 45, 46, 47, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//t60 vrijhouden
//			{48, 49, 50,51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t61 intern wassen
//			{52, 53, 54, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t62 intern wassen
//			{56, 57, 58, 59, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t63 extern wassen
//			{61, 62, 63, 64, 65, 66, 67, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t104 vrijhouden
//	};

			
			yard.tpmbuilder();
			int[][] test = yard.returnTPM();
//			optimizingModel3 model = new optimizingModel3(data, yard, eventList, priorityArrivaltrack,  priorityArrival, priorityType1, priorityType2, priorityType3, priorityType4, priorityType4extra); //create the model
//			Heuristic model = new Heuristic(blockdata, data, yard, eventList, priorityArrivaltrack,  priorityArrival, priorityType1, priorityType2, priorityType3, priorityType4, priorityType4extra); //create the model
//			HeuristicWithJobShopAndParking model = new HeuristicWithJobShopAndParking(outputParking, nrTrains, blockdata, data, yard, eventList, priorityArrivaltrack,  priorityArrival, priorityType1, priorityType2, priorityType3, priorityType4, priorityType4extra, priorityPlatform1, priorityPlatform2); //create the model
			HeuristicWithJobShop model = new HeuristicWithJobShop(nrTrains, blockdata, data, yard, eventList, priorityArrivaltrack,  priorityArrival, priorityType1, priorityType2, priorityType3, priorityType4, priorityType4extra, priorityPlatform1, priorityPlatform2); //create the model

			
			int run = i+1;
			results = model.optimization(nrTrains, test, run); //run the model and obtain output

			long timeHeurEnd = System.currentTimeMillis();
			System.out.println("Heuristic Executed in " + (timeHeurEnd-timeHeurStart));
			
			System.out.println("Run " + run);
			System.out.println("Right track departures:  " + results[1]);
			System.out.println("Completed activities:  " + results[0]);
			System.out.println();
			
//			if(results[1] > 22.5 && results[1] < 23.5)
//			{counterdeparture = counterdeparture +1;}
			if(results[1] > nrTrains-0.5 && results[1] < nrTrains+0.5)
			{counterdeparture = counterdeparture +1;}
			if(results[0] > 0.999)
			{countervolledigfeasible = countervolledigfeasible +1;}
			
			avgactivity = avgactivity + results[0];
//			if(results[0] > 0.999 && results[1] > 22.5){
//				countervolledigfeasiblehelemaal = countervolledigfeasiblehelemaal +1;
//			}
			if(results[0] > 0.999 && results[1] > nrTrains-0.5){
			countervolledigfeasiblehelemaal = countervolledigfeasiblehelemaal +1;
			}
			
			long timeItEnd = System.currentTimeMillis();
			System.out.println("Iteration finished in " + (timeItEnd-timeItStart));
			
			data = null;
			yard = null;
			eventList = null;
			
//			matching is emptied in the try
			jobshop = null;
//			ParkingProblem = null;
			model = null;
			
			System.gc();
		}

		long timeAfter = System.currentTimeMillis();
		long elapsedTime = timeAfter - timeBefore;

		System.out.println();
		System.out.println("Right track departures:   " + counterdeparture + " out of " + nriterations + "(" + 100*counterdeparture/nriterations + "%)");
		System.out.println("Completed activities:   " + countervolledigfeasible + " out of " + nriterations + "(" + 100*countervolledigfeasible/nriterations + "%)");
		System.out.println("Feasible run:   " + countervolledigfeasiblehelemaal + " out of "+ nriterations + "(" + 100*countervolledigfeasiblehelemaal/nriterations + "%)");
		System.out.println("The runX time was " + elapsedTime/1000 + " seconds.");
		System.out.println("avg activity score: " + avgactivity/nriterations);
		

	}

	public static void printList(ArrayList<Integer> p){
		for (int i=0;i<p.size();i++){
			System.out.print(p.get(i) + " ");
		}
		System.out.println("");
	}

	public static void printDoubleArray(int[][] printer){
		for (int i=0;i<printer.length;i++){
			for(int j=0;j<printer[0].length;j++){
				System.out.print(printer[i][j] + "  " );
			}
			System.out.println();
		}
	}

	public static void printIteration(ArrayList<Integer> p){
		for (int i=0;i<p.size();i++){
			System.out.print(p.get(i) + " ");
		}
		System.out.println("");
	}

	public static int[][] initializeBlockInfo(int blocks){
		int[][] output = new int[blocks+1][12];

		String csvFile = "CompositionTimesMatching.csv";
//		String csvFile = "CompositionTimes.csv";
		BufferedReader br = null;
		String cvsSplitBy = ";"; 
		String line = "";
		int count = -1;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine(); //title line

			while ((line = br.readLine()) != null) { //TELT EEN STAP TELANG DOOR GEEN ZIN OM NU TE FIXEN
				count = count+1;
				String[] data = line.split(cvsSplitBy);  //IDshort IDA IDD timeA timeD trackA trackD
				output[count][0] = Integer.parseInt(data[0]); //comp ID
				output[count][1] = Integer.parseInt(data[1]); //A ID
				output[count][2] = Integer.parseInt(data[2]); //D ID
				output[count][3] = Integer.parseInt(data[3]); //A time
				output[count][4] = Integer.parseInt(data[4]); //D time
				output[count][5] = Integer.parseInt(data[5]); //A track
				output[count][6] = Integer.parseInt(data[6]); //D track
				output[count][7] = Integer.parseInt(data[7]); //I time
				output[count][8] = Integer.parseInt(data[8]); //C time
				output[count][9] = Integer.parseInt(data[9]); //W time
				output[count][10] = Integer.parseInt(data[10]);  //R time
				output[count][11] = Integer.parseInt(data[11]);  //Lengt
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return output;
	}
	
	public static double getLength(int id, initializeData data){
		String x = Integer.toString(id);
		double length=0;
		if (x.length() == 5){
			ArrayList<trainComposition> comp = data.getCompositions();
			for (int i = 0; i< comp.size(); i++){
				if (comp.get(i).getID()==id){
					length = comp.get(i).getLength();
				}
			}
		}
		return length;
	}
	
	public static void writeExcel(int[][] matrix, String filename){

		FileWriter fileWriter = null;
		String FILE_HEADER = filename;
		String COMMA_DELIMITER = ";"; //maybe this must be comma
		String NEW_LINE_SEPARATOR = "\r\n";


		try{
//			String name = "CompositionTimesMatching.csv";
			String name = filename;
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






