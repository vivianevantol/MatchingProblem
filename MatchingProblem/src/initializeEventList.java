import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class initializeEventList {
	public int[][] arrivallist;
	public int[][] departurelist;
	public int[][] movementlist;
	public int[][] activitylist;
	public int endmovement;
	
	
	public initializeEventList(){
		
		this.arrivallist = initializeArrivallist();
		this.departurelist = initializeDeparturelist();
		this.movementlist = initializeMovementlist();
		this.activitylist = initializeActivitylist();
		this.endmovement=0;
		
	}	

	public  int[][] initializeArrivallist() {
		int[][] arrivallist = new int [50][2]; //tijden trainID
		
		
		for (int j=0; j<50; j++){
			arrivallist[j][0] = Integer.MAX_VALUE;		
		}
		
		String csvFile = "CompositionTimes.csv";
		BufferedReader br = null;
		String cvsSplitBy = ";"; 
		String line = "";
		int count = -1;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine(); //title line
			
			while ((line = br.readLine()) != null) { //TELT EEN STAP TELANG DOOR GEEN ZIN OM NU TE FIXEN
			//while(count<=21){
			count = count+1;
			String[] data = line.split(cvsSplitBy);  //IDshort IDA IDD timeA timeD trackA trackD
			
			arrivallist[count][0] = Integer.parseInt(data[3]); //timeA
//			System.out.println(Integer.parseInt(data[3]));
			arrivallist[count][1] = Integer.parseInt(data[0]); //ID short to get compositiontype
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


		return arrivallist;
	}

	public  int[][] initializeDeparturelist() {
		int [][] departurelist = new int [50][2]; //tijden trainID
		
		for (int j=0; j<50; j++){
			departurelist[j][0] = Integer.MAX_VALUE;		
		}
			
		String csvFile = "CompositionTimes.csv";
		BufferedReader br = null;
		String cvsSplitBy = ";"; 
		String line = "";
		int count = -1;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine(); //title line
			
			while ((line = br.readLine()) != null) {
			//while(count<=21){
			count = count+1;
			String[] data = line.split(cvsSplitBy);  //IDshort IDA IDD timeA timeD trackA trackD
				
			departurelist[count][0] = Integer.parseInt(data[4]); //timeD
			departurelist[count][1] = Integer.parseInt(data[0]); //ID short (since only one ID necessary)
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
		
		
		return departurelist;

	}

	public  int[][] initializeMovementlist() {

		int [][] movementlist = new int [1000][3]; //tijden types trainID
		for (int j=0; j<1000; j++){
			movementlist[j][0] = Integer.MAX_VALUE;		
		}
		return movementlist;
	}
	
	public int[][] initializeActivitylist(){
		int[][] activitylist = new int[50][8];// TIME, ID,CURRENT event,WASHEXTERN,WASHINTERN ,INSPECTION, REPAIR ,event counter
		for (int j=0; j<50; j++){
			activitylist[j][0] = Integer.MAX_VALUE;		
		}
		return activitylist; 
	}

	
	public void setArrivallist(int time, int location){
		this.arrivallist[location][0] = time;
	}
	
	public void setDeparturelist(int time, int location){
		this.departurelist[location][0] = time;
	}
	
	public void setActivitylist(int location, int type, int value){
		this.activitylist[location][type-1] = value;
	}
	
	public void setMovementlist(int time, int id, int type, int location){
		this.movementlist[location][0] = time;
		this.movementlist[location][1] = type;
		this.movementlist[location][2] = id;
	}
	
	public void setEndmovement(int time){
		this.endmovement = time;
	}
	
	public int[][] getArrivallist(){
		return arrivallist;
	}
	
	public int[][] getDeparturelist(){
		return departurelist;
	}
	
	public int[][] getMovementlist(){
		return movementlist;
	}
	
	public int[][] getActivitylist(){
		return activitylist;
	}
}
