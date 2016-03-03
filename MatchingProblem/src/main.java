import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ilog.concert.IloException;


public class main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
//		initializeEventList eventList = new initializeEventList(); //create the eventlist
//		
//		ArrayList<Jobs> allJobs = new ArrayList<Jobs>();
//		ArrayList<Jobs> oneJobs = new ArrayList<Jobs>();
//		ArrayList<Jobs> twoJobs = new ArrayList<Jobs>();
//
//		int[][] departures = eventList.getDeparturelist();
//		int maxD = 0;
//		int comps = 0;
//		for(int j=0;j<departures.length;j++){
//			if(departures[j][0]>maxD && departures[j][0]<10000){
//				maxD = departures[j][0];
//			}
//			if(departures[j][0]<10000){
//				comps++;
//			}
//		}
//		//initialize all jobs (the cleaning platform jobs with repair on them)
//		int[][] blockdata = initializeBlockInfo(comps);
//		for (int j=0; j<comps;j++){
//			int qj = maxD-blockdata[j][4]+blockdata[j][9]+2; //D-departure+wash+2
//			int rj = blockdata[j][3]+2; //arrival+2
//			int p1j = blockdata[j][8]+ blockdata[j][10];//cleaning+repair
//			int p2j = p1j;
//			allJobs.add(new Jobs(j+1,qj,rj,p1j,p2j, blockdata[j][0]));
//		}
//		HeuristicJobShop jobshop = new HeuristicJobShop(allJobs, oneJobs, twoJobs);
//		int[][] output = jobshop.solver(); //this is already sorted on starting times
//		
//		ArrayList<Integer> M1 = new ArrayList<Integer>();
//		ArrayList<Integer> M2 = new ArrayList<Integer>();
//		for(int j=0;j<output.length;j++){
//			if(output[j][1]==1){
//				M1.add(output[j][3]);
//			} else {
//				M2.add(output[j][3]);
//			}
//		}
//		printList(M1);
//		printList(M2);
		ArrayList<Integer> x = new ArrayList<Integer>();
		x.add(3);
		x.add(1);
		x.add(4);
		x.add(2);
		printList(x);
		
		x.remove(2);
		printList(x);
		
	
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
}