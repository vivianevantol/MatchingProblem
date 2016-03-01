import java.util.ArrayList;

public class mainJobShop {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ArrayList<Jobs> allJobs = new ArrayList<Jobs>();
		ArrayList<Jobs> oneJobs = new ArrayList<Jobs>();
		ArrayList<Jobs> twoJobs = new ArrayList<Jobs>();
		
		allJobs.add(new Jobs(1, 10, 2, 8, 10));
		allJobs.add(new Jobs(2, 12, 2, 3, 4));
		allJobs.add(new Jobs(3, 9, 8, 8, 9));
		allJobs.add(new Jobs(4, 2, 12, 15, 10));
		allJobs.add(new Jobs(5, 7, 15, 5, 2));
		allJobs.add(new Jobs(6, 3, 18, 10, 10));
		allJobs.add(new Jobs(7, 1, 29, 5, 4));
		
		twoJobs.add(allJobs.get(2));
		
		HeuristicJobShop test = new HeuristicJobShop(allJobs, oneJobs, twoJobs);
		int[][] output = test.solver();
		printDoubleArray(output);
	}
	
	public static void printDoubleArray(int[][] printer){
		for (int i=0;i<printer.length;i++){
			for(int j=0;j<printer[0].length;j++){
				System.out.print(printer[i][j] + "  " );
			}
			System.out.println();
		}
	}

}
