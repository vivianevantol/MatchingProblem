import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class mainJobShop {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ArrayList<Jobs> allJobs = new ArrayList<Jobs>();
		ArrayList<Jobs> oneJobs = new ArrayList<Jobs>();
		ArrayList<Jobs> twoJobs = new ArrayList<Jobs>();
		
//		TEST DATA UIT ARTIKEL
//		allJobs.add(new Jobs(1, 10, 2, 8, 10));
//		allJobs.add(new Jobs(2, 12, 2, 3, 4));
//		allJobs.add(new Jobs(3, 9, 8, 8, 9));
//		allJobs.add(new Jobs(4, 2, 12, 15, 10));
//		allJobs.add(new Jobs(5, 7, 15, 5, 2));
//		allJobs.add(new Jobs(6, 3, 18, 10, 10));
//		allJobs.add(new Jobs(7, 1, 29, 5, 4));
//		twoJobs.add(allJobs.get(2));
		
		//Jobs(int number, int tail, int release, int processing1, int processing2)
//		initializeData data = new initializeData();
		initializeEventList list = new initializeEventList();
		int[][] departures = list.getDeparturelist();
		
		printDoubleArray(departures);
		
		int maxD = 0;
		int comps = 0;
		for(int i=0;i<departures.length;i++){
			if(departures[i][0]>maxD && departures[i][0]<10000){
				maxD = departures[i][0];
			}
			if(departures[i][0]<10000){
				comps++;
			}
		}
		int[][] blockdata = initializeBlockInfo(comps);
//		printDoubleArray(blockdata);
		
		for (int i=0; i<comps;i++){
			int qj = maxD-blockdata[i][4]+blockdata[i][9]+2; //D-departure+wash+2
			int rj = blockdata[i][3]+2; //arrival+2
			int p1j = blockdata[i][8]+ blockdata[i][10];//cleaning+repair
			int p2j = p1j;
			allJobs.add(new Jobs(i+1,qj,rj,p1j,p2j, blockdata[i][0]));
//			int nr = i+1;
//			System.out.println("nr: "+ nr +"  qj " + qj + "  rj " + rj + "  p1j " + p1j + "  p2j " + p2j);
		}
		
		HeuristicJobShop test = new HeuristicJobShop(allJobs, oneJobs, twoJobs);
		int[][] output = test.solver();
//		printDoubleArray(output);
		
		ArrayList<Integer> M1 = new ArrayList<Integer>();
		ArrayList<Integer> M2 = new ArrayList<Integer>();
		for(int j=0;j<output.length;j++){
			if(output[j][1]==1){
				M1.add(output[j][3]);
			} else {
				M2.add(output[j][3]);
			}
		}
		printList(M1);
		printList(M2);
		
		M1.addAll(M2);
		printList(M1);
	}
	
	//this method gives compID AID DID Atime Dtime Atrack Dtrack Itime Ctime Wtime Rtime
	public static int[][] initializeBlockInfo(int blocks){
		int[][] output = new int[blocks+1][11];
		
		String csvFile = "CompositionTimes.csv";
//		String csvFile = "CompositionTimesMatching.csv";
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
	
 	public static void printDoubleArray(int[][] printer){
		for (int i=0;i<printer.length;i++){
			for(int j=0;j<printer[0].length;j++){
				System.out.print(printer[i][j] + "  " );
			}
			System.out.println();
		}
	}

	public static void printList(ArrayList<Integer> p){
		for (int i=0;i<p.size();i++){
			System.out.print(p.get(i) + " ");
		}
		System.out.println("");
	}
}
