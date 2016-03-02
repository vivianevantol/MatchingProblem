import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class InitializeShuntingYard {
	public Track t906a;
	public Track t52;
	public Track t53;
	public Track t54;
	public Track t55;
	public Track t56;
	public Track t57;
	public Track t58;
	public Track t59;
	// WEG TOCH? public Track t64;
	// public Track t51b;
	public Track t60;
	public Track t61;
	public Track t62;
	public Track t63;
	public Track t104a;
	public int[][] travelMatrix;
	public int[][] tpm;
	public ArrayList<Track> Tracks = new ArrayList<Track>(); 

	public InitializeShuntingYard() throws FileNotFoundException, IOException{
		initializeShuntingYard();
	}

	public void initializeShuntingYard() throws FileNotFoundException, IOException{

		FileReader fr = new FileReader("Connections.csv");
		BufferedReader br = new BufferedReader(fr);
		String cvsSplitBy = ";"; 
		String line = "";
		ArrayList<Train> inTrainTrack = new ArrayList<Train>(); // initial empty LrrayList for all tracks
		this.travelMatrix = new int[14][14];
		int counter = 0;
		try {

			line = br.readLine();
			String[] track = line.split(cvsSplitBy);
			Integer[] con = new Integer[track.length - 1];
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 


			this.t906a = new Track(Integer.parseInt(track[0]),false,false,true,con,inTrainTrack);
			Tracks.add(t906a);
			
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
			this.t52 = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
			Tracks.add(t52);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
			this.t53 = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
			Tracks.add(t53);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
			this.t54 = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
			Tracks.add(t54);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
			this.t55 = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
			Tracks.add(t55);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
			this.t56 = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
			Tracks.add(t56);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
			this.t57 = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
			Tracks.add(t57);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
			this.t58 = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
			Tracks.add(t58);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
			this.t59 = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
			Tracks.add(t59);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
//			this.t64 = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
//
//			line = br.readLine();
//			track = line.split(cvsSplitBy);
//			for(int i = 1;i < track.length;i++)
//			{
//				con[i-1]=Integer.parseInt(track[i]);
//				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
//			}
//
//			counter++; 
//			this.t51b = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
//			Tracks.add(t51b);
//			line = br.readLine();
//			track = line.split(cvsSplitBy);
//			for(int i = 1;i < track.length;i++)
//			{
//				con[i-1]=Integer.parseInt(track[i]);
//				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
//			}
//
//			counter++; 
			this.t60 = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
			Tracks.add(t60);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
			this.t61 = new Track(Integer.parseInt(track[0]),false,true,false,con,inTrainTrack);
			Tracks.add(t61);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}

			counter++; 
			this.t62 = new Track(Integer.parseInt(track[0]),false,true,false,con,inTrainTrack);
			Tracks.add(t62);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}
			counter++; 

			this.t63 = new Track(Integer.parseInt(track[0]),true,false,false,con,inTrainTrack);
			Tracks.add(t63);
			line = br.readLine();
			track = line.split(cvsSplitBy);
			for(int i = 1;i < track.length;i++)
			{
				con[i-1]=Integer.parseInt(track[i]);
				travelMatrix[counter][i-1] = Integer.parseInt(track[i]);
			}
			counter++;

			this.t104a = new Track(Integer.parseInt(track[0]),false,false,false,con,inTrainTrack);
			Tracks.add(t104a);



			
		} 

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 



	}

	public void tpmbuilder(){
		// initialiseer tpm in een matrix met nullen

		int firstPosition = 0;
		int secondPosition =0;
 		int[][] positionsPerTrack = {
				{1, 2, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//t906a
				{5, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0},//t52
				{12, 13, 14, 15, 16, 17, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0},//53
				{19, 20, 21, 22, 23, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//54
				{25, 26, 27, 28, 29, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//t55
				{31, 32, 33, 34, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t56
				{35, 36, 37, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t57
				{38, 39, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//t58
				{41, 42, 43, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t59
				{ 44,45, 46, 47, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//t60
				{48, 49, 50,51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t61
				{52, 53, 54, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t62
				{56, 57, 58, 59, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t63
				{61, 62, 63, 64, 65, 66, 67, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //t104
		};


 		
		this.tpm = new int [67][67];
		for (int i = 0; i<=66;i++){
			for (int j=0; j<= 66; j++){
				tpm[i][j] = 0;
			}
		}

		for (int i = 0; i<=13;i++){
			for (int j=0; j<= 13; j++){

				if (travelMatrix[i][j] == 1){

					firstPosition = positionsPerTrack[i][0];
					secondPosition = positionsPerTrack[j][0];
					tpm[firstPosition-1][secondPosition-1] = 1;

				}
				if (travelMatrix[i][j] == 2){

					firstPosition = positionsPerTrack[i][0];
					int maxvalue = 0;
					for (int p=0;p<=13;p++){
						if (positionsPerTrack[j][p] > maxvalue){
							maxvalue = positionsPerTrack[j][p];
						}
					}
					secondPosition = maxvalue;

					tpm[firstPosition-1][secondPosition-1] = 1;						

				}
				if (travelMatrix[i][j] == 3){


					int maxvalue = 0;
					for (int p=0;p<=13;p++){
						if (positionsPerTrack[i][p] > maxvalue){
							maxvalue = positionsPerTrack[i][p];
						}
					}
					firstPosition = maxvalue;
					secondPosition = positionsPerTrack[j][0];
					tpm[firstPosition-1][secondPosition-1] = 1;	

				}
				if (travelMatrix[i][j] == 4){

					int maxvalue = 0;
					
					for (int p=0;p<=13;p++){
						if (positionsPerTrack[i][p] > maxvalue){
							maxvalue = positionsPerTrack[i][p];
						}
					}
					firstPosition = maxvalue;

					maxvalue = 0;
					for (int p=0;p<=13;p++){
						if (positionsPerTrack[j][p] > maxvalue){
							maxvalue = positionsPerTrack[j][p];
						}
					}
					secondPosition = maxvalue;
					tpm[firstPosition-1][secondPosition-1] = 1;	
				}
				

			
			}
		}
		
		for (int i = 0; i<=13;i++){
			for (int j=0; j<= 10; j++){
				if (positionsPerTrack[i][j] == positionsPerTrack[i][j+1]-1) {
					tpm[positionsPerTrack[i][j]-1][positionsPerTrack[i][j+1]-1] = 1; 
					tpm[positionsPerTrack[i][j+1]-1][positionsPerTrack[i][j]-1] = 1; 
			}
		}
		}

//		for (int i = 0; i<67;i++){
//			for (int j=0; j< 67; j++){
//				System.out.print("  "+tpm[i][j]);
//			}
//			System.out.println();
//		}


	}
	public  int[][] returnTPM(){
		return tpm;
	}
	
	public ArrayList<Track> getTracks(){
		return Tracks;
	}

}







