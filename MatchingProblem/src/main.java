import java.io.IOException;
import java.util.ArrayList;

import ilog.concert.IloException;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		initializeData data = new initializeData();
		try{
		MIP2 matching = new MIP2(0);
		} catch(IloException | IOException e) {
			System.out.println("Error");
		}
		System.out.println("Still works.");	
	
	}
}