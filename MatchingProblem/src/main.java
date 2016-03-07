import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ilog.concert.IloException;


public class main {

	public static void main(String[] args) throws FileNotFoundException, IOException, IloException {
		initializeData data = new initializeData(); //create the data set
		InitializeShuntingYard yard = new InitializeShuntingYard(); //create the shunting yard
		initializeEventList eventList = new initializeEventList(); //create the eventlist
		
		int MatchingMargin = 0;
		try{ //directly prints output into "CompositionTimesMatching.csv"
			MatchingProblem matching = new MatchingProblem(MatchingMargin, data);
		} catch(IloException | IOException e) {
			System.out.println("Error");
		} 
		
//		int[][] departures = eventList.getDeparturelist();
//		int[][] arrivals = eventList.getArrivallist();
//		int[][] trainInfo = new int[22][4]; //ID Atime Dtime Length //22 is number of trains!!
//		for (int x=0;x<trainInfo.length;x++){ 
//			trainInfo[x][0] = departures[x][1];
//			trainInfo[x][1] = arrivals[x][0];
//			trainInfo[x][2] = departures[x][0];
//			trainInfo[x][3] = (int) getLength(departures[x][1], data);
//		}
//		
//		printDoubleArray(trainInfo);
//		ArrayList<Track> tracks = yard.getTracks();
//		int[][] trackInfo = new int[4][3]; //lengt left right
//		for(int s=1;s<5;s++){ //the departure tracks!!
//			trackInfo[s-1][0] = (int) tracks.get(s).getLength(); 
//			trackInfo[s-1][1] = 0;
//			trackInfo[s-1][2] = 0;
//		}
//		
//		
//		ArrayList<ArrayList<Integer>> allA =  new ArrayList<ArrayList<Integer>>();
//		int[] AssTrack = new int[trackInfo.length];
//		int[][] AssTrackTrain = new int[trackInfo.length][trainInfo.length];
//		
//		ParkingSetCovering Parking = new ParkingSetCovering(false, allA, trackInfo.length, trainInfo.length, AssTrack, AssTrackTrain, trainInfo);
////		double[] output = Parking.getDuals();
////		printArray(output);
//		int[] penalties = Parking.getPenalty();
//		int[][] assignments = Parking.getAssignments();
////		printArray(penalties);
//		printDoubleArray(assignments);
		
//		int[] test = {7, 2, 4, 9, 1};
//		int[] output = getMin(test, 0);
//		System.out.println("Index: " + output[0]);
//		System.out.println("Value: " + output[1]);
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
			now++;
			minvalue = Integer.MAX_VALUE;
			index = -1;
			for (int i=0;i<x.length;i++){
				if(x[i]<minvalue){
					index = i;
					minvalue = x[i];
				}
			}
			x[index] = Integer.MAX_VALUE;
		}
		int[] yy = {index, minvalue};
		return yy;
	}
	

	public static void printArray(double[] printer){
		for (int i=0;i<printer.length;i++){
				System.out.print(printer[i] + "  " );
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
	public static void printDoubleArray(int[][] printer){
		for (int i=0;i<printer.length;i++){
			for(int j=0;j<printer[0].length;j++){
				System.out.print(printer[i][j] + "  " );
			}
			System.out.println();
		}
	}
	public static void printDoubleArray(double[][] printer){
		for (int i=0;i<printer.length;i++){
			for(int j=0;j<printer[0].length;j++){
				System.out.print(printer[i][j] + "  " );
			}
			System.out.println();
		}
	}
	
	public static int[][] initializeBlockInfo(int blocks){
		int[][] output = new int[blocks][11];
		
		String csvFile = "CompositionTimesMatching.csv";
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
}