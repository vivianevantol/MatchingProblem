import java.util.ArrayList;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		initializeData data = new initializeData();
		
		ArrayList<blocks> blocks = new ArrayList<blocks>();
		ArrayList<blocks> arrivalblocks = new ArrayList<blocks>();
		ArrayList<blocks> departureblocks = new ArrayList<blocks>();
		
		for(int i=0 ; i< data.getCompositions().size();i++){
			createBlocks(blocks, data.getCompositions().get(i));
			if(data.getCompositions().get(i).getArrival()==true){
				createBlocks(arrivalblocks, data.getCompositions().get(i));
			} else {
				createBlocks(departureblocks, data.getCompositions().get(i));
			}
		}
		
		
	}
	
	public static void createBlocks(ArrayList<blocks> b, trainComposition c){
		
	}

}
