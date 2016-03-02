import java.util.ArrayList;

public class HeuristicJobShop {
	//given into the heuristic
	private final ArrayList<Jobs> allJobs;
	private final ArrayList<Jobs> oneJobs;
	private final ArrayList<Jobs> twoJobs;

	public HeuristicJobShop(ArrayList<Jobs> allJobs, ArrayList<Jobs> oneJobs, ArrayList<Jobs> twoJobs){
		this.allJobs = allJobs;
		this.oneJobs = oneJobs;
		this.twoJobs = twoJobs;
	}

	public int[][] solver(){
		//created by the heuristic
		ArrayList<Jobs> J = this.allJobs;
		ArrayList<Jobs> J1 = this.oneJobs;
		ArrayList<Jobs> J2 = this.twoJobs;

		//initialize output values
		int[][] output = new int[J.size()][4]; //job number, h, tau
		int[][] outputx = new int[J.size()][4]; //job number, h, tau

		//initialize heurstic values
		ArrayList<Jobs> T = new ArrayList<Jobs>();
		for(int i=0;i<J.size();i++){
			T.add(J.get(i));
		} 
		int z1 = minRelease(AminB(T, J2))[1];
		int z2 = minRelease(AminB(T, J1))[1];

		//Iterations start!!!
		while(!T.isEmpty()){
			Jobs I1 = null;
			Jobs I2 = null;
			ArrayList<Jobs> A1 = setAk(T, J2, z1);
			if(!A1.isEmpty()){I1 = maxTail(A1);} else {I1 = new Jobs(0,0,0,0,0,0);}
			ArrayList<Jobs> A2 = setAk(T, J1, z2);
			if(!A2.isEmpty()){I2 = maxTail(A2);} else {I2 = new Jobs(0,0,0,0,0,0);}

//			System.out.println();
//			System.out.println("z1: " + z1 + " z2: " + z2);
//			System.out.println("A1 jobs: ");
//			for(int i=0;i<A1.size();i++){
//				System.out.print(A1.get(i).getNumber());
//				System.out.print("  ");
//			}
//			System.out.println();
//			System.out.println("A2 jobs: ");
//			for(int i=0;i<A2.size();i++){
//				System.out.print(A2.get(i).getNumber());
//				System.out.print("  ");
//			}
//			System.out.println();
//			System.out.println("I1: " + I1.getNumber() + " I2: " + I2.getNumber());
			
			if(I1.getNumber()!=0 && I2.getNumber()!=0){
				if(z1+I1.getProcessing1()+I1.getTail() <= z2+I2.getProcessing2()+I2.getTail()){//add I1 to M1
					output[I1.getNumber()-1][0] = I1.getNumber();
					output[I1.getNumber()-1][1] = 1;
					output[I1.getNumber()-1][2] = z1;
					output[I1.getNumber()-1][3] = I1.getID();

					T.remove(I1); //no longer to be planned
					z1 = Integer.max(z1+I1.getProcessing1(),minRelease(AminB(T, J2))[1]);
					z2 = Integer.max(z2, minRelease(AminB(T, J1))[1]);
//					System.out.println("Add I1 to M1");
				} else {
					output[I2.getNumber()-1][0] = I2.getNumber();
					output[I2.getNumber()-1][1] = 2;
					output[I2.getNumber()-1][2] = z2;
					output[I2.getNumber()-1][3] = I2.getID();

					T.remove(I2); //no longer to be planned
					z2 = Integer.max(z2+I2.getProcessing2(),minRelease(AminB(T, J1))[1]);
					z1 = Integer.max(z1, minRelease(AminB(T, J2))[1]);
//					System.out.println("Add I2 to M2");
//					System.out.println("Calc: " + z1 + " " + minRelease(AminB(T, J2))[1]);
				}
			} else if (I1.getNumber()!=0 && I2.getNumber()==0){ //only one non zero
				output[I1.getNumber()-1][0] = I1.getNumber();
				output[I1.getNumber()-1][1] = 1;
				output[I1.getNumber()-1][2] = z1;
				output[I1.getNumber()-1][3] = I1.getID();

				T.remove(I1); //no longer to be planned
				z1 = Integer.max(z1+I1.getProcessing1(),minRelease(AminB(T, J2))[1]);
				z2 = Integer.max(z2, minRelease(AminB(T, J1))[1]);
//				System.out.println("Add I1 to M1");
			} else if (I1.getNumber()==0 && I2.getNumber()!=0){ //only one non zero
				output[I2.getNumber()-1][0] = I2.getNumber();
				output[I2.getNumber()-1][1] = 2;
				output[I2.getNumber()-1][2] = z2;
				output[I2.getNumber()-1][3] = I2.getID();

				T.remove(I2); //no longer to be planned
				z2 = Integer.max(z2+I2.getProcessing2(),minRelease(AminB(T, J1))[1]);
				z1 = Integer.max(z1, minRelease(AminB(T, J2))[1]);
//				System.out.println("Add I2 to M2");
			} else {
				System.out.println("All zero Ak Error");
			}

		}

		ArrayList<int[]> sorting = new ArrayList<int[]>();
		sorting.add(output[0]);
		for(int i=1;i<output.length;i++){
			int found = 0;
			for(int j=0;j<sorting.size();j++){
				if(output[i][2]<sorting.get(j)[2]){
					sorting.add(j, output[i]);
					found =1;
					j=100;
				}
			}
			if(found==0){
				sorting.add(output[i]);
			}
		}
		for(int i=0;i<sorting.size();i++){
			outputx[i] = sorting.get(i);
		}

		
		return outputx;
	}

	//   [index value]
	public int[] minRelease(ArrayList<Jobs> list){
		int minvalue = Integer.MAX_VALUE;
		int minindex = -1;
		for(int i=0;i<list.size();i++){
			if(list.get(i).getRelease()<minvalue){
				minvalue = list.get(i).getRelease();
				minindex = i;
			}
		}
		int[] output = {minindex, minvalue};
		return output;
	}

	//   [index value]
	public Jobs maxTail(ArrayList<Jobs> list){
		int maxvalue = -1;
		int maxindex = -1;
		for(int i=0;i<list.size();i++){
			if(list.get(i).getTail()>maxvalue){
				maxvalue = list.get(i).getTail();
				maxindex = i;
			}
		}
		Jobs output = list.get(maxindex);
		return output;
	}

	//   A\B set
	public ArrayList<Jobs> AminB(ArrayList<Jobs> A, ArrayList<Jobs> B){
		ArrayList<Jobs> output = new ArrayList<Jobs>();
		for(int i=0;i<A.size();i++){
			if(!B.contains(A.get(i))){
				output.add(A.get(i));
			}
		}
		return output;
	}

	//   create Ak 
	public ArrayList<Jobs> setAk(ArrayList<Jobs> T, ArrayList<Jobs> Jk, int zk){
		ArrayList<Jobs> output = new ArrayList<Jobs>();
		for(int i=0;i<T.size();i++){
			if(!Jk.contains(T.get(i)) && T.get(i).getRelease()<=zk){
				output.add(T.get(i));
			}
		}
		return output;
	}
}

