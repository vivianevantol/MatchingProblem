import java.io.IOException;
import java.util.ArrayList;

import ilog.concert.IloException;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		initializeData data = new initializeData();
		try{
		MIP matching = new MIP();
		matching.solveMe();
		} catch(IloException | IOException e) {
			System.out.println("Error");
		}
		System.out.println("Still works.");
		
		
		
		
		
		
		
//		ArrayList<blocks> allBlocks = new ArrayList<blocks>();
//		ArrayList<blocks> arrivalBlocks = new ArrayList<blocks>();
//		ArrayList<blocks> departureBlocks = new ArrayList<blocks>();
//		ArrayList<trainComposition> arrivalTrains = new ArrayList<trainComposition>(); //set Ta
//		ArrayList<trainComposition> departureTrains = new ArrayList<trainComposition>(); //set Td
//		ArrayList<arcs> allArcs = new ArrayList<arcs>(); //all arcs
//		//use getArcs for set At
//		//use getArcsOut for Aht+
//		//use getArcsIn for Aht-
//		//use getIntermediates for Ct-
//		
//		//create all blocks
//		int arrivals = 0;
//		int departures =0;
//		for(int i=0; i < data.getCompositions().size();i++){
//			allBlocks = createBlocks(data.getCompositions().get(i), allBlocks);
//			if(data.getCompositions().get(i).getArrival()){
//				arrivalBlocks = createBlocks(data.getCompositions().get(i), arrivalBlocks);
//				arrivalTrains.add(data.getCompositions().get(i));
//				arrivals++;
//			} else {
//				departureBlocks = createBlocks(data.getCompositions().get(i), departureBlocks);
//				departureTrains.add(data.getCompositions().get(i));
//				departures++;
//			}
//		}
//		System.out.println(arrivalBlocks.size());
//		System.out.println(departureBlocks.size());
		
		//arrivalTrains set Ta
		//departureTrains set Td
		//use getArcs for set At
		//use getArcsOut for Aht+
		//use getArcsIn for Aht-
		//use getIntermediates for Ct-
		
		//ui = 1 if i is used (arrivalparts) binari
		//vj = 1 if j is used (departure parts)  binari
		//zij = 1 if i is assigned to j
		
		
		
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

	//Every function should return a subset of blocks for which "this" is the case
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

	//subset of blocks (in locations) which belong to train c and arcs go out of h
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

	//subset of blocks (in locations) which belong to train c and arcs go into h
	public static int[] getArcsIn(trainComposition c, int h){
		int[] arcsIn = new int[h];
		ArrayList<arcs> arcsTotal = getArcs(c);
		int fillCount = 0;
		for(int i=0;i<arcsTotal.size();i++){
			if(arcsTotal.get(i).getArc()[1]==h){
//				arcsIn.add(arcsTotal.get(i));
				arcsIn[fillCount] = arcsTotal.get(i).getArc()[0];
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
	public static ArrayList<blocks> getSameDeparture(blocks i, ArrayList<blocks> departures){
		ArrayList<blocks> sames = new ArrayList<blocks>();
		for(int j=0;j<departures.size();j++){
			if(compareBlocks(departures.get(j), i)){
				sames.add(departures.get(j));
			}
		}
		return sames;
	}

	//want to give the locations back not the blocks itself
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