import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class HeuristicWithJobShop {
	private initializeData Data;
	private InitializeShuntingYard Yard;
	private initializeEventList List;
	public int timeMovement;
	public boolean movement;
	public int movementTime;
	public int[] priorityArrivalarea;
	public int[] priorityArrivaltrack;
	public int[] priorityType1;
	public int[] priorityType2;
	public int[] priorityType3;
	public int[] priorityType4;
	public int[] priorityType4extra;
	public int[][]  matrix; // matrix voor positeis
	public int[][] 	movementtijdmatrix;
	public ArrayList<Integer> priorityPlatform1;
	public ArrayList<Integer> priorityPlatform2;


	public HeuristicWithJobShop(initializeData Data, InitializeShuntingYard Yard, initializeEventList eventlist, int[] priorityArrivaltrack, int[]priorityArrivalarea, int[] priorityType1, int[]  priorityType2, int[] priorityType3, int[] priorityType4, int[] priorityType4extra, ArrayList<Integer> priorityPlatform1, ArrayList<Integer> priorityPlatform2){



		this.Data = Data;
		this.Yard = Yard;
		this.List = eventlist;
		this.priorityArrivalarea = priorityArrivalarea;
		this.priorityArrivaltrack = priorityArrivaltrack; 
		this.priorityType1 = priorityType1;
		this.priorityType2 = priorityType2;
		this.priorityType3 = priorityType3;
		this.priorityType4 = priorityType4;
		this.priorityType4extra =  priorityType4extra;
		this.priorityPlatform1 = priorityPlatform1;
		this.priorityPlatform2 = priorityPlatform2;
	}

	public double[]  optimization(int[][] tpm) throws FileNotFoundException, IOException{
		//This should all be implemented in the data set, and the shunting yard
		ArrayList<Integer> positions = new ArrayList<Integer>(); // Alle posities leeg
		dijkstraMovement move = new dijkstraMovement();
		for (int i = 0; i<=66; i++){
			positions.add(i, 0);
		}
		fillInitialActivitylist(); //create lines for all arriving traincompositions

		int minuut = 0; 
		movement = false;
		int[][] matrix = new int[30][150]; 
		int[][] movementtijdmatrix = new int[30][150];
		int[][] r = List.getArrivallist();
		for (int i = 0; i<30; i++){
			matrix[i][0] = r[i][1]; //keep track of all positions of each train
			movementtijdmatrix[i][0] = r[i][1]; //keep track of the arrival times on all positions
		}

		int counter = 0;
		int counter4 =0;

		while (minuut <= 1500)
		{	

//			printpositionTrainMatrix(matrix);
			System.out.println("Min: " + minuut);


			int[][] a = List.getArrivallist();
			int[] arrivalMin = getarrivalMin(a, 0);
			int[][] d = List.getDeparturelist();
			int[] departureMin = getdepartureMin(d, 0);
			int end = List.endmovement;//create set methods
			int[][] activitylist = List.getActivitylist();
			int[] activityMin = getMin(activitylist, 0);




			//			if (minuut == 1500
			//					){
			//
			//				for (int i = 0; i< 30; i++){
			//					for (int j=0; j< 8; j++){
			//						System.out.print("   "+activitylist[i][j]);
			//					}
			//					System.out.println();
			//				}
			//			}

			if(arrivalMin[1]<=minuut){
//				System.out.println("Arrival");
				//do arrival
				if (List.getArrivallist()[arrivalMin[0]][1] == 81002 || List.getArrivallist()[arrivalMin[0]][1] == 80428 || List.getArrivallist()[arrivalMin[0]][1] == 80206 ){
					//arrivals on track 104
					positions.set(62, List.getArrivallist()[arrivalMin[0]][1]); //put train on position
					List.setArrivallist(Integer.MAX_VALUE, arrivalMin[0]); 
					positionTrainMatrix(63, List.getArrivallist()[arrivalMin[0]][1], matrix);
					minuutTrainMatrix(minuut, List.getArrivallist()[arrivalMin[0]][1], movementtijdmatrix);

				}
				else {
					//					System.out.println("minuut is nu dit "+minuut);
					positions.set(0, List.getArrivallist()[arrivalMin[0]][1]); //put train on position
					List.setArrivallist(Integer.MAX_VALUE, arrivalMin[0]); //set arrival time on inf
					positionTrainMatrix(1, List.getArrivallist()[arrivalMin[0]][1], matrix);
					minuutTrainMatrix(minuut, List.getArrivallist()[arrivalMin[0]][1], movementtijdmatrix);

					if (arrivalMin[3] != -1 ){
						//						System.out.println("minuut is hier "+minuut);
						positions.set(1, List.getArrivallist()[arrivalMin[2]][1]); //put train on position
						List.setArrivallist(Integer.MAX_VALUE, arrivalMin[2]); //set arrival time on inf
						positionTrainMatrix(2, List.getArrivallist()[arrivalMin[2]][1], matrix);
						minuutTrainMatrix(minuut, List.getArrivallist()[arrivalMin[2]][1], movementtijdmatrix);
					}
				}

			}

			if(departureMin[1]<=minuut ){ // check if positie is gevuld EN meerdere posities mogelijk, voor meerdere treinen op departure track
				//do departure
//				System.out.println("Departure");
				int departurePosition = getIndex(positions, List.getDeparturelist()[departureMin[0]][1]); //find leaving train
				positions.set(departurePosition, 0); //remove leaving train
				List.setDeparturelist(Integer.MAX_VALUE, departureMin[0]); // set departure time on inf
				for (int i=0;i<1000;i++){
					if (List.getMovementlist()[i][2] == List.getDeparturelist()[departureMin[0]][1]){
						List.setMovementlist(Integer.MAX_VALUE, List.getDeparturelist()[departureMin[0]][1], 1, i);
					}//if a train leaves no more moves are availible
				}

				//				if (departureMin[3] != -1){
				//					departurePosition = getIndex(positions, List.getDeparturelist()[departureMin[2]][1]); //find leaving train
				//					positions.set(departurePosition, 0); //remove leaving train
				//					List.setDeparturelist(Integer.MAX_VALUE, departureMin[2]); // set departure time on inf
				//					for (int i=0;i<1000;i++){
				//						if (List.getMovementlist()[i][2] == List.getDeparturelist()[departureMin[2]][1]){
				//							List.setMovementlist(Integer.MAX_VALUE, List.getDeparturelist()[departureMin[2]][1], 1, i);
				//						}
				//					}
				//				}

			}

			if(end==minuut){
//				System.out.println("Ednmove");
				movement=false; //endmovement aanpassen
				List.setEndmovement(Integer.MAX_VALUE);		
			}



			if (activityMin[1] <= minuut){ // nog checken: meerdere events op dezelfde minuut klaar?
				List.setActivitylist(activityMin[0], 1, Integer.MAX_VALUE);
				List.setActivitylist(activityMin[0], 3, 0);

			}

//						printIteration(positions, minuut);
			int indexcheck = -1;
			int positiearrival = -1;

			//			if(minuut==1200){
			//				for (int i=0; i<100;i++){
			//					for(int j=0; j<3;j++){
			//						System.out.print("  "+List.getMovementlist()[i][j]);
			//					}System.out.println();
			//				}
			//			}

			if (movement == false){
//				System.out.println("Check arrivalsplatform");
				//				System.out.println("minuut is "+minuut);
				//Check arrival track, niet elke trein komt op hetzelfde spoor aan
				if(positions.get(0)!=0)
				{positiearrival = 0;}
				if (positions.get(62) !=0)
				{positiearrival = 62; }
				if (positiearrival != -1){
					int endPosition = -1;	
					for (int i=0;i<priorityArrivalarea.length;i++){
						movementTime = move.possibleMovement(positiearrival+1, priorityArrivalarea[i], positions, Data, Yard);
						//						System.out.println(movementTime + " movement time");
						if(movementTime!=0 && movementTime<100){							
							endPosition = priorityArrivalarea[i];
							//							System.out.println(endPosition + "end is");
							//							System.out.println(positiearrival + "begin is");
							int id = positions.get(positiearrival);
							positions.set(endPosition, id);
							positions.set(positiearrival, 0);
							timeMovement = minuut + 2;
							movement = true;
							List.setEndmovement(timeMovement);
							setMovementList(id);
							for (int n = 0; n<50; n++){
								if (List.getDeparturelist()[n][1] == id){
									indexcheck = n;
								}
							}
							if ((getBooleans(id,1) + timeMovement) < List.getDeparturelist()[indexcheck][1] ){
								StartEvent(id, 0, timeMovement);

							}
							positionTrainMatrix(endPosition, id, matrix);
							minuutTrainMatrix(minuut, id, movementtijdmatrix);
							break;
						}

					}
				}

			}
			positiearrival = -1;	
			indexcheck = -1;
			if (movement == false){
//				System.out.println("Check arrival platform again");
				//Check arrival track, niet elke trein komt op hetzelfde spoor aan
				if(positions.get(1)!=0){
					positiearrival = 0;//cheat
					//				positiearrival = 1; //nocheat
				}
				if (positions.get(62) !=0){
					positiearrival = 62; }
				if (positiearrival != -1){
					int endPosition = -1;	
					for (int i=0;i<priorityArrivalarea.length;i++){
//												printIteration(positions, minuut);
						movementTime = move.possibleMovement(positiearrival+1, priorityArrivalarea[i], positions, Data, Yard);
						//					if(movementTime!=0 && movementTime<100){System.out.println("movement 1 "  + movementTime);}
						if(movementTime!=0 && movementTime<100){
							//							System.out.println("CHECK");
							endPosition = priorityArrivalarea[i];
							int id;
							if(positiearrival==0){
								id = positions.get(positiearrival+1);//cheat
							} else {
								id = positions.get(positiearrival);
							}
							positions.set(endPosition, id);
							if(positiearrival==0){
								positions.set(positiearrival+1, 0);//cheat
							} else {
								positions.set(positiearrival, 0);
							}							
							timeMovement = minuut + 2;
							movement = true;
							List.setEndmovement(timeMovement);
							setMovementList(id);
							for (int n = 0; n<50; n++){
								if (List.getDeparturelist()[n][1] == id){
									indexcheck = n;
								}
							}
							if ((getBooleans(id,1) + timeMovement) < List.getDeparturelist()[indexcheck][1] ){
								StartEvent(id, 0, timeMovement);
							}
							positionTrainMatrix(endPosition, id, matrix);
							minuutTrainMatrix(minuut, id, movementtijdmatrix);
							break;
						}

					}
				}

			}

			int intWashposition = -1; 
			boolean activity = false;
			int idextra = -1;

			if (movement == false){//reinigingsperron
//				System.out.println("cleaning");
				for (int i = 47; i<54;i++){
					activity = false; //trein mag niet verplaatsen, geeft aan activity finished
					if(positions.get(i)!=0 && movement == false){	

						idextra = positions.get(i);
						for(int j=0;j<50;j++){
							if(List.getActivitylist()[j][1]==idextra){

								if(List.getActivitylist()[j][2]==0){ //activity check
									//										System.out.println("index" + List.getActivitylist()[i][2]);
									activity = true;
									intWashposition = i+1;



								}
							}
						}

					}

					if (activity == true){//so train can move!!
						int idcheck = positions.get(intWashposition-1);
						int location = -1;
						for(int j=0; j<50;j++){
							if(List.getActivitylist()[j][1]==idcheck){
								location = j;
							}
						}


						if (List.getActivitylist()[location][4] == 0 && List.getActivitylist()[location][3] == 0 && List.getActivitylist()[location][6] == 0){
							int endPosition = -1;
							for (int q=0;q<priorityType3.length;q++){
								movementTime = move.possibleMovement(intWashposition, priorityType3[q], positions, Data, Yard);
								if(movementTime!=0 && movementTime <100){
									endPosition = priorityType3[q];
									int id = positions.get(intWashposition-1);
									positions.set(endPosition, id);
									positions.set(intWashposition-1, 0);
									timeMovement = minuut + 2;
									movement = true;
									List.setEndmovement(timeMovement);
									positionTrainMatrix(endPosition, id, matrix);
									minuutTrainMatrix(minuut, id, movementtijdmatrix);
									int finalt = -1;
									for (int t = 0; t<100; t++){
										if (List.getMovementlist()[t][2] == id && List.getMovementlist()[t][1]==3){
											finalt = t;
										}
									}
									if(finalt!=-1){
									List.setMovementlist(Integer.MAX_VALUE, id, 3 , finalt);
									}
									break;
								}
							}
						}
						else{
							int endPosition = -1;
							for (int q=0;q<priorityArrivalarea.length;q++){
								movementTime = move.possibleMovement(intWashposition, priorityArrivalarea[q], positions, Data, Yard);
								if(movementTime!=0 && movementTime <100){
									endPosition = priorityArrivalarea[q];
									int id = positions.get(intWashposition-1);
									positions.set(endPosition, id);
									positions.set(intWashposition-1, 0);
									timeMovement = minuut + 2;
									movement = true;
									List.setEndmovement(timeMovement);
									positionTrainMatrix(endPosition, id, matrix);
									minuutTrainMatrix(minuut, id, movementtijdmatrix);
									break;
								}
							}
						}
					}

				}
			}

			//				System.out.println("move"+ movement);
			int extWashposition = -1; 
			activity = false;
			idextra = -1;

			if (movement == false){
//				System.out.println("washing");
				for (int i = 55; i<59;i++){//wasmachine
					activity = false;
					if(positions.get(i)!=0 && movement == false){	
						idextra = positions.get(i);
						for(int j=0;j<50;j++){
							if(List.getActivitylist()[j][1]==idextra){
								if(List.getActivitylist()[j][2]==0){ //activity check							
									activity = true; //if finished activity
									extWashposition = i+1;



								}
							}
						}
					}

					if (activity == true){
						int idcheck = positions.get(extWashposition-1);
						int location = -1;
						for(int j=0; j<50;j++){
							if(List.getActivitylist()[j][1]==idcheck){
								location = j;
							}
						}
						if (List.getActivitylist()[location][4] == 0 && List.getActivitylist()[location][3] == 0 && List.getActivitylist()[location][6] == 0){

							int endPosition = -1;
							for (int q=0;q<priorityType3.length;q++){
								movementTime = move.possibleMovement(extWashposition, priorityType3[q], positions, Data, Yard);						
								if(movementTime!=0 && movementTime <100){
									endPosition = priorityType3[q];
									int id = positions.get(extWashposition-1);
									positions.set(endPosition, id);
									positions.set(extWashposition-1, 0);
									timeMovement = minuut + 2;
									movement = true;
									List.setEndmovement(timeMovement);
									positionTrainMatrix(endPosition, id, matrix);
									minuutTrainMatrix(minuut, id, movementtijdmatrix);
									int finalt = -1;
									for (int t = 0; t<100; t++){
										if (List.getMovementlist()[t][2] == id){
											finalt = t;
										}
									}
									List.setMovementlist(Integer.MAX_VALUE, id, 3 , finalt);
									break;
								}
							}
						}
						else{

							int endPosition = -1;
							for (int q=0;q<priorityArrivalarea.length;q++){
								movementTime = move.possibleMovement(extWashposition, priorityArrivalarea[q], positions, Data, Yard);						
								if(movementTime!=0 && movementTime <100){
									endPosition = priorityArrivalarea[q];
									int id = positions.get(extWashposition-1);
									positions.set(endPosition, id);
									positions.set(extWashposition-1, 0);
									timeMovement = minuut + 2;
									movement = true;
									List.setEndmovement(timeMovement);
									positionTrainMatrix(endPosition, id, matrix);
									minuutTrainMatrix(minuut, id, movementtijdmatrix);
									break;
								}
							}
						}
					}

				}
			}

			if (movement == false){ //Job shop model has priority over other moves!!
//				System.out.println("JobShop");
				int[] platform1 = {48, 49, 50, 51};
				int[] platform2 = {52, 53, 54, 55};
				boolean plat1fill = false; //did we fill the first platform
				boolean plat2fill = false;
				//if platform 1 empty --> check priorityPlatform1
				for(int j=0;j<priorityPlatform1.size();j++){ //Check all compositions 
					for(int p=0;p<platform1.length;p++){ //for all compositions check all platforms
						//check possible move
						if(plat1fill==false){
							int currentPosition = getIndex(positions, priorityPlatform1.get(j));
							movementTime = move.possibleMovement(currentPosition+1, platform1[p], positions, Data, Yard);
							if(movementTime!=0 && movementTime<100){
								plat1fill = true;
								int endPosition = platform1[p];
//								System.out.println(j);
//								System.out.println(p);
//								System.out.println(priorityPlatform1.size());
								int id = priorityPlatform1.get(j);

								positions.set(endPosition, id);
								positions.set(currentPosition, 0);
								timeMovement = minuut + 2;
								movement = true;
								List.setEndmovement(timeMovement);
//								System.out.println(id);
								for (int n = 0; n<50; n++){
//									System.out.println(n + " " + List.getDeparturelist()[n][1]);
									if (List.getDeparturelist()[n][1] == id){
										indexcheck = n;
									}
								}
								if (getBooleans(id,3) + timeMovement < List.getDeparturelist()[indexcheck][1] ){
									StartEvent(id, 1, timeMovement);//internal
								}
								positionTrainMatrix(endPosition, id, matrix);
								minuutTrainMatrix(minuut, id, movementtijdmatrix);
								//find the event in the movement list (id and type match)
								int indexmove=-1;
								for(int l=0;l<List.getMovementlist().length;l++){
									if(List.getMovementlist()[l][2]==id && List.getMovementlist()[l][1] ==1){
										indexmove = l;
									}
								}
								if(indexmove!=-1){
									List.setMovementlist(Integer.MAX_VALUE, id, 1 , indexmove);
								}
								priorityPlatform1.remove(j); //no longer in jobshop list
								int xx = -1;
								for (int x=0;x<priorityPlatform2.size();x++){
									if(priorityPlatform2.get(x)==id){
										xx = x;
									}
								}
								if(xx!=-1){
								priorityPlatform2.remove(xx);
								}
							}
						}
					}
				}
				//if platform 2 empty --> check priorityPlatform2
				for(int j=0;j<priorityPlatform2.size();j++){ //Check all compositions 
					for(int p=0;p<platform2.length;p++){ //for all compositions check all platforms
						//check possible move
						if(plat2fill==false){
							int currentPosition = getIndex(positions, priorityPlatform2.get(j));
							movementTime = move.possibleMovement(currentPosition+1, platform2[p], positions, Data, Yard);
							if(movementTime!=0 && movementTime<100){
								plat2fill = true;
								
								int endPosition = platform2[p];
								int id = priorityPlatform2.get(j);

								positions.set(endPosition, id);
								positions.set(currentPosition, 0);
								timeMovement = minuut + 2;
								movement = true;
								List.setEndmovement(timeMovement);
								for (int n = 0; n<50; n++){
									if (List.getDeparturelist()[n][1] == id){
										indexcheck = n;
									}
								}
								if (getBooleans(id,3) + timeMovement < List.getDeparturelist()[indexcheck][1] ){
									StartEvent(id, 1, timeMovement);//internal
								}
								positionTrainMatrix(endPosition, id, matrix);
								minuutTrainMatrix(minuut, id, movementtijdmatrix);
								//find the event in the movement list (id and type match)
								int indexmove=-1;
								for(int l=0;l<List.getMovementlist().length;l++){
									if(List.getMovementlist()[l][2]==id && List.getMovementlist()[l][1] ==1){
										indexmove = l;
									}
								}
								if(indexmove!=-1){
									List.setMovementlist(Integer.MAX_VALUE, id, 1 , indexmove);
								}
								priorityPlatform2.remove(j); //no longer in jobshop list
								int xx = -1;
								for (int x=0;x<priorityPlatform1.size();x++){
									if(priorityPlatform1.get(x)==id){
										xx = x;
									}
								}
								if(xx!=-1){
								priorityPlatform1.remove(xx);
								}
							}
						}
					}
				}
			}


			boolean moveexecuted = false;
			indexcheck = -1;
			if (movement == false){			//rest of movements // first copy the current movementlist
//				System.out.println("Movements");

				int[][] m = List.getMovementlist(); 
				int[][] x =  new int[1000][3] ;

				for (int i = 0; i<1000;i++){
					for (int j = 0; j<3;j++){
						x[i][j] = m[i][j];
					}
				}

				while (moveexecuted == false){
					
					boolean alreadyDeparture = false; 
					int[] movementMin = getPossibleMin(x,0); //check index -1


					if(movementMin[0] !=-1){ //move found!
//						System.out.println("move found");
						int movementType = x[movementMin[0]][1];
						int movementTrainID = x[movementMin[0]][2];
						int currentPosition = getIndex(positions, movementTrainID);
						int endPosition = -1;
						int time = movementMin[2];
//						System.out.println(movementType);



												if(movementType ==1){  // internal cleaning
						//						
						//							for (int i=0;i<priorityType1.length;i++){	
						//								movementTime = move.possibleMovement(currentPosition+1, priorityType1[i], positions, Data, Yard);				
						//								if(movementTime!=0 && movementTime<100){
						//									moveexecuted = true;
						//									endPosition = priorityType1[i];
						//								
						//									int id = movementTrainID;
						//									positions.set(endPosition, id);
						//									positions.set(currentPosition, 0);
						//									timeMovement = minuut + 2;
						//									movement = true;
						//									List.setEndmovement(timeMovement);
						//									for (int n = 0; n<50; n++){
						//										if (List.getDeparturelist()[n][1] == id){
						//											indexcheck = n;
						//										}
						//									}
						//									if (getBooleans(id,3) + timeMovement < List.getDeparturelist()[indexcheck][1] ){
						//										StartEvent(id, 1, timeMovement);//internal
						//									}
						//									positionTrainMatrix(endPosition, id, matrix);
						//									minuutTrainMatrix(minuut, id, movementtijdmatrix);
						//									List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
						//									break;
						//								}
						//							}
													if (moveexecuted == false) {
										
														x[movementMin[0]][0] = Integer.MAX_VALUE;	
													
													}
												}
						if(movementType ==2){ // external cleaning
							for (int i=0;i<priorityType2.length;i++){
								movementTime = move.possibleMovement(currentPosition+1, priorityType2[i], positions, Data, Yard);
								if(movementTime!=0 && movementTime<100){
									moveexecuted = true;
									endPosition = priorityType2[i];
									int id = movementTrainID;
									positions.set(endPosition, id);
									positions.set(currentPosition, 0);
									timeMovement = minuut + 2;
									movement = true;
									List.setEndmovement(timeMovement);
									for (int n = 0; n<50; n++){
										if (List.getDeparturelist()[n][1] == id){
											indexcheck = n;
										}
									}
									if (getBooleans(id,4) + timeMovement < List.getDeparturelist()[indexcheck][1] ){
										StartEvent(id, 2, timeMovement);//ecternal								
									}
									List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
									positionTrainMatrix(endPosition, id, matrix);
									minuutTrainMatrix(minuut, id, movementtijdmatrix);
									break;
								}
							}
							if (moveexecuted == false) {

								x[movementMin[0]][0] = Integer.MAX_VALUE;	
							}
						} 


						else if(movementType ==3){ //move to depart area
							for (int i=0;i<priorityType3.length;i++){
								if (currentPosition == priorityType3[i]){
									alreadyDeparture = true;
									List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
								}
								if (alreadyDeparture = false){
									movementTime = move.possibleMovement(currentPosition+1, priorityType3[i], positions, Data, Yard);
									if(movementTime!=0 && movementTime<100){
										moveexecuted = true;
										endPosition = priorityType3[i];
										int id = movementTrainID;
										positions.set(endPosition, id);
										positions.set(currentPosition, 0);
										timeMovement = minuut + 2;
										movement = true;
										List.setEndmovement(timeMovement);
										for (int n = 0; n<50; n++){
											if (List.getDeparturelist()[n][1] == id){
												indexcheck = n;
											}
										}
										//									if (getBooleans(id,2) + timeMovement < List.getDeparturelist()[indexcheck][1] ){
										//										StartEvent(id, 3, timeMovement); //repair
										//									}
										List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
										positionTrainMatrix(endPosition, id, matrix);
										minuutTrainMatrix(minuut, id, movementtijdmatrix);
										break;
									}
								}
							}
							if (moveexecuted == false) {


								x[movementMin[0]][0] = Integer.MAX_VALUE;	
							}
						} 

						else if(movementType ==4  ){	//naar departuretrack
							counter4++;
							if (minuut > time - 3){//not infinite on track
								if (movementTrainID == 80428 || movementTrainID == 80206 || movementTrainID == 83071){
									for (int i=0;i<priorityType4extra.length;i++){
										movementTime = move.possibleMovement(currentPosition+1, priorityType4extra[i], positions, Data, Yard);
										if(movementTime!=0 && movementTime<100){
											moveexecuted = true;
											endPosition = priorityType4extra[i];
											int id = movementTrainID;
											positions.set(endPosition, id);
											positions.set(currentPosition, 0);
											timeMovement = minuut + 2;
											movement = true;
											List.setEndmovement(timeMovement);
											counter = counter +1;
											positionTrainMatrix(endPosition, id, matrix);
											minuutTrainMatrix(minuut, id, movementtijdmatrix);
											List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
											break;
										}

									}	
									if (moveexecuted == false) {

										x[movementMin[0]][0] = Integer.MAX_VALUE;	
									}

								}
								else {
									for (int i=0;i<priorityType4.length;i++){
										movementTime = move.possibleMovement(currentPosition+1, priorityType4[i], positions, Data, Yard);
										//										System.out.println("the reason why he can't move is: "+movementTime);
										if(movementTime!=0 && movementTime<100){
											moveexecuted = true; 
											endPosition = priorityType4[i];
											int id = movementTrainID;
											positions.set(endPosition, id);
											positions.set(currentPosition, 0);
											timeMovement = minuut + 2;
											movement = true;
											List.setEndmovement(timeMovement);
											counter = counter +1;
											positionTrainMatrix(endPosition, id, matrix);
											minuutTrainMatrix(minuut, id, movementtijdmatrix);
											List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
											break;
										}

									}
									if (moveexecuted == false) {
										x[movementMin[0]][0] = Integer.MAX_VALUE;	

									}
								}
							}
							else {x[movementMin[0]][0] = Integer.MAX_VALUE; }
						}
					}
					if (movementMin[2] > 10000){
						moveexecuted = true; 
					}
				} // end while loop voor de move executed
			}	// if loop of de movement false is
			minuut++;
		} //while minuut deadline is nog niet bereikt


		//		for (int iuu = 0; iuu<30; iuu++){
		//			int roo = List.getArrivallist()[iuu][1];
		//			move.lengtetrein(roo, Data);
		//		}

		double result1 = printPerformance(List.activitylist);
		double result2 = counter; 
		//		System.out.println("aantal keer echte beweging naar departure track" + counter);
		//		System.out.println("aantal keer een poging om naar departure track te gaan" + counter4);
		//		System.out.println("");
		//		printpositionTrainMatrix(matrix);
		//		printtijdTrainMatrix(movementtijdmatrix);

		double[] results = new double[2];

		results[0] = result1;
		results[1] = result2; 
		return results; 











	}

	// SKIP EVENT IF: TYPE 4 event - (eventime + minuut) < 0




	public int[] getMin(int[] x){
		int minvalue = 800000;
		int index = -1;
		int index2 = -1;
		int minvalue2 = -1;
		for (int i=0;i<x.length;i++){
			if(x[i]<minvalue){
				index = i;
				minvalue = x[i];
			}


		}
		int[] y = {index, minvalue};
		return y;
	}
	public int[] getMin(int[][] x, int z){
		int minvalue = Integer.MAX_VALUE;
		int index = -1;
		for (int i=0;i<x.length;i++){
			if(x[i][z]<=minvalue){
				index = i;
				minvalue = x[i][z];
			}
		}
		int[] y = {index, minvalue};
		return y;
	}

	public int[] getarrivalMin(int[][] x, int z){
		int minvalue = 80000;
		int index = -1;
		int index2 = -1;
		int minvalue2 = -1;
		for (int i=0;i<x.length;i++){
			if(x[i][z]<minvalue){
				index = i;
				minvalue = x[i][z];
			}
		}
		for (int i=index+1;i<x.length;i++){
			if(x[i][z] == minvalue && x[i][z] !=0){
				index2 = i;
				minvalue2 = x[i][z];
				//			System.out.println(minvalue2);
			}
		}
		int[] y = {index, minvalue, index2, minvalue2};
		return y;
	}

	public int[] getdepartureMin(int[][] x, int z){
		int minvalue = 80000;
		int index = -1;
		int index2 = -1;
		int minvalue2 = -1;
		for (int i=0;i<x.length;i++){
			if(x[i][z]<minvalue){
				index = i;
				minvalue = x[i][z];
			}
		}
		for (int i=index+1;i<x.length;i++){
			if(x[i][z] == minvalue && x[i][z] !=0){
				index2 = i;
				minvalue2 = x[i][z];
				//			System.out.println(minvalue2);
			}
		}
		int[] y = {index, minvalue, index2, minvalue2};
		return y;
	}

	public int[] getPossibleMin(int[][] x, int z){ //movementlist erin [time type id]
		int minvalue = 50000;
		int index = 0;
		int found =0;
		int possible = -1;
		int counter = 0;

		int[][] xx =  new int[1000][3] ;

		for (int i = 0; i<1000;i++){
			for (int j = 0; j<3;j++){
				xx[i][j] = x[i][j];
			}
		}

		boolean activity = false;

		while(found==0){
			activity = false;
			for (int j=0;j<xx.length;j++){
				if(xx[j][z]<=minvalue){
					index = j;
					minvalue = xx[j][z];
				}
			}
			int id = xx[index][2];
			for(int i=0;i<50;i++){
				if(List.getActivitylist()[i][1]==id){
					if(List.getActivitylist()[i][2]==0){ //activity check
						//						System.out.println("index" + List.getActivitylist()[i][2]);
						found = 1;
						possible = id;
					} else {
						activity = true;
						xx[index][z] = Integer.MAX_VALUE;
						minvalue = Integer.MAX_VALUE;
						//						System.out.println("HOIHAOIHFASOIHFAOISHA");
					}

				}
			}
			if(minvalue>40000 && counter >= 10 && activity == true){
				found=1;
				index = -1;		
			}
			if(minvalue>40000 && activity == false ){
				found=1;
				index = -1;
			}
		}

		int[] y = {index, possible, minvalue};
		//		System.out.println("minvalue:"+minvalue);
		return y;
	}

	public int getIndex(ArrayList<Integer> x, int ID){ //getposition
		int index = -1;
		for (int i=0;i<x.size();i++){
			if(x.get(i)==ID){
				index = i;
			}
		}
		return index;
	}

	public double getLength(int id){
		String x = Integer.toString(id);
		double length=0;
		if (x.length() == 5){
			ArrayList<trainComposition> comp = Data.getCompositions();
			for (int i = 0; i< comp.size(); i++){
				if (comp.get(i).getID()==id){
					length = comp.get(i).getLength();
				}
			}
		}
		else {
			ArrayList<Train> train = Data.getTrains();
			for (int i = 0; i< train.size(); i++){
				if (train.get(i).getID()==id){
					length = train.get(i).getType().getLength();
				}
			}
		}

		return length;
	}

	public int getBooleans(int id, int bool){ //inspect, repair, clean, wash
		int time = 0;
		//		if (x.length() == 5){
		ArrayList<trainComposition> comp = Data.getCompositions();
		for (int i = 0; i< comp.size(); i++){
			if (comp.get(i).getID()==id){
				for (int j=0; j<comp.get(i).getTrains().size();j++){
					if(bool == 1){
						if(comp.get(i).getTrains().get(j).getInspect()==true){
							time = time + (int) comp.get(i).getTrains().get(j).getType().getInspectiontime();
						}
					} else if(bool == 2){
						if(comp.get(i).getTrains().get(j).getRepair()==true){
							time = time + (int) comp.get(i).getTrains().get(j).getType().getRepairtime();
						}
					} else if(bool == 3){
						if(comp.get(i).getTrains().get(j).getClean()==true){
							time = time + (int) comp.get(i).getTrains().get(j).getType().getCleaningtime();
						}
					} else if(bool == 4){
						if(comp.get(i).getTrains().get(j).getWash()==true){
							time = time + (int) comp.get(i).getTrains().get(j).getType().getWashingtime();
						}
					}
				}
			}
		}
		return time;
	}

	public double getDepartureTime(int id){ //inspect, repair, clean, wash
		String x = Integer.toString(id);
		double time=0;
		//ArrayList<trainComposition> comp = Data.getCompositions();
		int[][] list = List.getDeparturelist();
		for (int i = 0; i< 50; i++){
			if (list[i][1]==id){
				time = list[i][0];
			}
		}		
		return time;
	}

	public void setMovementList(int id){ // vult de movementmatrix met uiterlijke tijden voor het rijden naar area's. 

		// per train ID moeten we de volgende dingen uit gaan lezen: 


		double departureTime = getDepartureTime(id);
		int washExtern = getBooleans(id, 4);
		int washIntern = getBooleans(id, 3);
		int inspection = getBooleans(id, 1);
		int repair = getBooleans(id, 2);
		double movementtime = 2;
		double type1Event = Integer.MAX_VALUE; // type 4: To depart track (Every train)
		double type2Event = Integer.MAX_VALUE; // type 3: to depart area (every train. some trains get repaired there)
		double type3Event = Integer.MAX_VALUE; // type 2; to external cleaning area (some trains)
		double type4Event = Integer.MAX_VALUE; // type 1: to internal cleaning area (some trains)

		int location = -1;
		for(int i=0; i<1000;i++){
			if(List.getMovementlist()[i][2]==0){
				location = i;
				break;
			}
		}	 
		// SET TYPE 4 EVENT (MOVING TO THE DEPART TRACK)	
		type4Event = departureTime - movementtime ;  //create event time
		List.setMovementlist((int) type4Event, id, 4, location); //fill list	
		location++;
		// --------------------------------------------------------------------------------------------------		 
		//SET TYPE 3 TO BE THE ULTIMATE_START_REPAIR_EVENT	


		type3Event = type4Event - movementtime -  5;
		List.setMovementlist((int) type3Event,  id,  3, location);
		location++;


		// --------------------------------------------------------------------------------------------------				 
		// SET TYPE 2 TO BE THE TIME THAT YOU HAVE TO GO TO THE CLEANING EXTERN AREA		 
		if(washExtern>0){
			type2Event = type3Event - washExtern - movementtime;
			List.setMovementlist((int) type2Event, id, 2, location);
			location++;
		}
		// --------------------------------------------------------------------------------------------------		
		//SET TYPE 1 TO BE THE TIME THAT YOU HAVE TO GO TTHE CLEANING INTERN AREA	
		if(washExtern>0){
			type1Event = type2Event - washIntern - repair - movementtime;
		} else {
			type1Event = type3Event - washIntern - repair - movementtime;
		}
		List.setMovementlist((int) type1Event, id, 1, location);

		// -------------------------------------------------------------------------------------------------- 
		// Only start an event if type4 - eventtime > 0. (so there is always time to travel to the departure track). 
		// THE REAL start event is making sure that the train cannot move, and that the train makes an end event after which it can move. 
	}


	public void StartEvent(int id, int type, int arrival){ // vult de eventlist met events (ANDERS DAN DE MOVEMENTLIST!)  
		//
		//	 // IF A TRAIN ARRIVES, OR IF A END MOVEMENT EVENT OCCURS, WE WILL CALL THIS METHOD. PER TRAIN ID SHOULD EXIST A EVENT MATRIX:
		//	 // matrix:  TIME(1) -- ID(2) -- CURRENT event(3) -- WASHEXTERN(4) -- WASHINTERN(5) --- INSPECTION(6) -- REPAIR(7) -- event counter(8)
		int washExtern = getBooleans(id, 4);
		int washIntern = getBooleans(id, 3);
		int inspection = getBooleans(id, 1);
		int repair = getBooleans(id, 2);

		int location = -1;
		for(int i=0; i<50;i++){
			if(List.getActivitylist()[i][1]==id){
				location = i;
			}
		}


		if(type ==1){ //internal
			if(List.getActivitylist()[location][4]==1){ //needs cleaning
				List.setActivitylist(location, 1, arrival+washIntern+repair);
				List.setActivitylist(location, 3, 1);
				List.setActivitylist(location, 5, 0);
				List.setActivitylist(location, 8, List.getActivitylist()[location][7]+1);

			}
			if(List.getActivitylist()[location][6]==1){
				List.setActivitylist(location, 7, 0);
				List.setActivitylist(location, 8, List.getActivitylist()[location][7]+1);

			}
		} else if(type ==2){ //external
			if(List.getActivitylist()[location][3]==1){ //needs washing
				List.setActivitylist(location, 1, arrival+washExtern);
				List.setActivitylist(location, 3, 1);
				List.setActivitylist(location, 4, 0);
				List.setActivitylist(location, 8, List.getActivitylist()[location][7]+1);
			}
		} else if(type ==3){ //repair
			//		if(List.getActivitylist()[location][6]==1){ //needs repair
			//			List.setActivitylist(location, 1, arrival);
			//			List.setActivitylist(location, 3, 1);
			//			List.setActivitylist(location, 7, 0);
			//			List.setActivitylist(location, 8, List.getActivitylist()[location][7]+1);
			//		}
		} else if(type ==0){ //inspection
			if(List.getActivitylist()[location][5]==1){ //inspection
				List.setActivitylist(location, 1, arrival+inspection);
				List.setActivitylist(location, 3, 1); 
				List.setActivitylist(location, 6, 0);
				List.setActivitylist(location, 8, List.getActivitylist()[location][7]+1);
			}
		} 
	}

	public void fillInitialActivitylist(){
		//	 // IF A TRAIN ARRIVES, OR IF A END MOVEMENT EVENT OCCURS, WE WILL CALL THIS METHOD. PER TRAIN ID SHOULD EXIST A EVENT MATRIX:
		//	 // matrix:  TIME(1) -- ID(2) -- CURRENT event(3) -- WASHEXTERN(4) -- WASHINTERN(5) --- INSPECTION(6) -- REPAIR(7) -- event counter(8)
		for(int i=0;i<50;i++){
			int id = List.getArrivallist()[i][1];

			if(id!=0){
				int boolwashex=0;
				int boolwashin=0;
				int boolins=0;
				int boolrep=0;
				int washExtern = getBooleans(id, 4);
				if(washExtern>0){boolwashex = 1;}
				int washIntern = getBooleans(id, 3);
				if(washIntern>0){boolwashin = 1;}
				int inspection = getBooleans(id, 1);
				if(inspection>0){boolins = 1;}
				int repair = getBooleans(id, 2);
				if(repair>0){boolrep = 1;}

				List.setActivitylist(i, 1, Integer.MAX_VALUE);
				List.setActivitylist(i, 2, id);
				List.setActivitylist(i,4, boolwashex);
				List.setActivitylist(i,5, boolwashin);
				List.setActivitylist(i,6, boolins);
				List.setActivitylist(i,7, boolrep);
			}
		}
	}

	public double printPerformance(int[][] activity){
		//		TIME(1) -- ID(2) -- CURRENT event(3) -- WASHEXTERN(4) -- WASHINTERN(5) --- INSPECTION(6) -- REPAIR(7) -- event counter(8)
		double[] performance = new double[50];//create vector with performance per train
		double minPerformance =0;
		double maxPerformance=0;
		double totalPerformance=0;
		double countPerformance=0;
		double allDone = 0;

		for (int i=0;i<50;i++){
			if(activity[i][1]!=0){ //a train is assigned to the row
				double unperformed = activity[i][3]+activity[i][4]+activity[i][5]+activity[i][6]; //needed but undone binary
				performance[i] = activity[i][7]/(activity[i][7]+unperformed); 

				totalPerformance=totalPerformance+performance[i];
				countPerformance=countPerformance+1;
				if(minPerformance>performance[i]){
					minPerformance=performance[i];
				}
				if(maxPerformance<performance[i]){
					maxPerformance=performance[i];
				}
				if(performance[i]==1){
					allDone = allDone+1;
				}
			}
		}
		//	System.out.println("Dit gaat nog alleen over de eerste 8 treinen!!");//verander for loop
		//	System.out.println("The lowest performing train performs " + minPerformance*100 + " percent of their tasks.");
		//	System.out.println("The highest performing train performs " + maxPerformance*100 + " percent of their tasks.");
		//	System.out.println("The average performing train performs " + (totalPerformance/countPerformance)*100 + " percent of their tasks.");
		//	System.out.println("The percentage of trains performing all tasks is " + (allDone/countPerformance)*100 + " percent.");

		double result =  (allDone/countPerformance);
		return result;
	}

	public void printIteration(ArrayList<Integer> p, int minuut){
		System.out.print("Minuut: " + minuut + "  ");
		for (int i=0;i<p.size();i++){
			System.out.print(p.get(i) + " ");
			//			if ( i == 9 ||  i == 19 || i == 29 || i == 39 || i == 49 ||  i == 59  ){
			//				System.out.print("xxx  ");
			//			}
		}
		System.out.println("");
	}

	public int[][] positionTrainMatrix(int positienieuw, int id, int[][] matrix){
		int indexrij = -1;
		int indexkolom = -1;
		for (int i = 0; i<30 ; i++){
			if (matrix[i][0] == id){
				indexrij = i;
				break;
			}
		}
		for (int j = 0; j<150 ; j++){
			if (matrix[indexrij][j] == 0){
				indexkolom = j;	
				break;
			}
		}
		matrix[indexrij][indexkolom] = positienieuw;	
		return matrix;
	}

	public int[][]	minuutTrainMatrix(int minuut, int id, int[][] matrix){
		int indexrij = -1;
		int indexkolom = -1;
		for (int i = 0; i<30 ; i++){
			if (matrix[i][0] == id){
				indexrij = i;
				break;
			}
		}
		for (int j = 0; j<150 ; j++){
			if (matrix[indexrij][j] == 0){
				indexkolom = j;	
				break;
			}
		}
		matrix[indexrij][indexkolom] = (minuut+2);	
		return matrix;
	}

	public void printpositionTrainMatrix(int [][] positionTrainMatrix){
		System.out.println("deze posities heeft elke trein gehad");
		for (int i = 0; i<22 ; i++){
			for (int j = 0; j<150; j++){
				System.out.print(positionTrainMatrix[i][j]+ "     ");
			}
			System.out.println("");
		}
	}

	public void printtijdTrainMatrix(int [][] positionTrainMatrix){
		System.out.println("deze tijden heeft elke trein gehad");
		for (int i = 0; i<22 ; i++){
			for (int j = 0; j<15; j++){
				System.out.print(positionTrainMatrix[i][j] + "     ");
			}
			System.out.println("");
		}
	}

}

