import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ilog.concert.IloException;

public class cheatMainParking {


	public static void main(String[] args) throws FileNotFoundException, IOException, IloException {
		// TODO Auto-generated method stub
		InitializeShuntingYard yard = new InitializeShuntingYard(); //create the shunting yard
		initializeEventList eventList = new initializeEventList(); //create the eventlist


		ParkingProblem test = new ParkingProblem(500, yard, eventList);
		int[][] output = test.returnOutput();
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

