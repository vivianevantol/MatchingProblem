import java.io.IOException;
import java.util.ArrayList;

import ilog.concert.IloException;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long timeBefore = System.currentTimeMillis();
		initializeData data = new initializeData();
		try{
		MatchingProblem matching = new MatchingProblem(0, data);
		} catch(IloException | IOException e) {
//			System.out.println("Error");
		}
//		System.out.println("Still works.");	
		long timeAfter = System.currentTimeMillis();
		long time = (timeAfter-timeBefore);
	System.out.println("Solving Time: " + time);
	}
}