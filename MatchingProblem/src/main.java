import java.io.IOException;
import java.util.ArrayList;

import ilog.concert.IloException;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		initializeData data = new initializeData();
		try{
		MIP2 matching = new MIP2();
		} catch(IloException | IOException e) {
			System.out.println("Error");
		}
		System.out.println("Still works.");	
	
	}
	
	public static trainComposition getComposition(int ID, ArrayList<trainComposition> comps){
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
	
	public static int[][] getArcs(trainComposition c, ArrayList<blocks> arrivalblocks, ArrayList<blocks> departureblocks){
		int n = c.getTypes().size()*(c.getTypes().size()+1)/2; //number of arcs in train
		int[][] allArcs = new int[n][3]; //for all blocks location and arc[2]
		int count = 0;
		if(c.getArrival()){
//			System.out.println("arrival found");
			for(int i=0;i<arrivalblocks.size();i++){
//				System.out.println("block " + arrivalblocks.size());
				if(c.getID()==arrivalblocks.get(i).getParent()){
					allArcs[count][0] = i;
					allArcs[count][1] = arrivalblocks.get(i).getArc()[0];
					allArcs[count][2] = arrivalblocks.get(i).getArc()[1];
					count++;
//					System.out.println(allArcs[count-1][0] + "  " + allArcs[count-1][1] + " " + allArcs[count-1][2]);
				}
			}
		} else if(!c.getArrival()) {
//			System.out.println("arrival found xxx");
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

	public static ArrayList<blocks> createBlocks(trainComposition c, ArrayList<blocks> b){
		int compositionSize = c.getTypes().size();
//		boolean arrival = c.getArrival();
		for(int i=0;i<compositionSize;i++){ //single blocks
			int[] arc = {i, i+1};
			ArrayList<trainType> types = new ArrayList<trainType>();
			types.add(c.getTypes().get(i));
			int track = 104;
			if(c.get906a()){
				track = 906;
			}
			blocks x = new blocks(arc, c.getID(), types, c.getTime(), track);
			b.add(x);
		}
		if(compositionSize>1){
			for(int i=0;i<compositionSize-1;i++){
				int[] arc = {i, i+2};
				ArrayList<trainType> types = new ArrayList<trainType>();
				types.add(c.getTypes().get(i));
				types.add(c.getTypes().get(i+1));
				int track = 104;
				if(c.get906a()){
					track = 906;
				}
				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track);
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
				int track = 104;
				if(c.get906a()){
					track = 906;
				}
				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track);
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
				int track = 104;
				if(c.get906a()){
					track = 906;
				}
				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track);
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
				int track = 104;
				if(c.get906a()){
					track = 906;
				}
				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track);
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
				int track = 104;
				if(c.get906a()){
					track = 906;
				}
				blocks x = new blocks(arc, c.getID(), types, c.getTime(), track);
				b.add(x);
			}
		}
		return b;
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
}