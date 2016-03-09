

	
	import java.io.FileNotFoundException;
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.Collections;

	public class HeuristicWithJobShopAndParking {
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
		public int[][] blockdata;
		public int[][] results;
		public ArrayList<Integer> priorityPlatform1;
		public ArrayList<Integer> priorityPlatform2;
		public int numberTrains;
		public ArrayList<Integer> departureArea;
		public int[][] parkingMatrix;


			
			public HeuristicWithJobShopAndParking(int[][] parkingMatrix, int numberTrains, int[][] blockdata,initializeData Data, InitializeShuntingYard Yard, initializeEventList eventlist, int[] priorityArrivaltrack, int[]priorityArrivalarea, int[] priorityType1, int[]  priorityType2, int[] priorityType3, int[] priorityType4, int[] priorityType4extra, ArrayList<Integer> priorityPlatform1, ArrayList<Integer> priorityPlatform2){



			this.Data = Data;
			this.numberTrains = numberTrains; 
			this.Yard = Yard;
			this.List = eventlist;
			this.priorityArrivalarea = priorityArrivalarea;
			this.priorityArrivaltrack = priorityArrivaltrack; 
			this.priorityType1 = priorityType1;
			this.priorityType2 = priorityType2;
			this.priorityType3 = priorityType3;
			this.priorityType4 = priorityType4;
			this.priorityType4extra =  priorityType4extra;
			this.blockdata = blockdata;
			this.priorityPlatform1 = priorityPlatform1;
			this.priorityPlatform2 = priorityPlatform2;
			this.departureArea = departureArea;
			this.parkingMatrix = parkingMatrix; 
		}

		public double[]  optimization(int[][] tpm) throws FileNotFoundException, IOException{
			//This should all be implemented in the data set, and the shunting yard
			ArrayList<Integer> positions = new ArrayList<Integer>(); // Alle posities leeg
			dijkstraMovement move = new dijkstraMovement();
			for (int i = 0; i<=66; i++){
				positions.add(i, 0);
			}
		
			fillInitialActivitylist( blockdata, numberTrains); //create lines for all arriving traincompositions

			int minuut = 0; 
			movement = false;
			int[][] matrix = new int[30][150]; 
			int[][] results = new int[24][1402];
			int[][] movementtijdmatrix = new int[30][15];
			int[][] r = List.getArrivallist();
			for (int i = 0; i<30; i++){
				matrix[i][0] = r[i][1]; //keep track of all positions of each train
				movementtijdmatrix[i][0] = r[i][1]; //keep track of the arrival times on all positions
			}
			
			// find arrival en departure sporen voor de treinen
			ArrayList<Integer> arrival104 = new ArrayList<Integer>();
			ArrayList<Integer> departure104 = new ArrayList<Integer>();
			
			// Arrival 
			int countindex = 0; 
			for (int i = 0; i<numberTrains; i++){
					 if (blockdata[i][5] == 104){
						 arrival104.add(countindex, blockdata[i][0]);
						 countindex = countindex+1; 
					 }
				 }
			
			// departure
			countindex = 0; 
			for (int i = 0; i<numberTrains; i++){
					 if (blockdata[i][6] == 104){
						 departure104.add(countindex, blockdata[i][0]);
						 countindex = countindex+1; 
					 }
				 }
				 
			// Nu volgt het verwerken van het parking probleem. 
			ArrayList<Integer> depArea52 = new ArrayList<Integer>();
			ArrayList<Integer> depArea53 = new ArrayList<Integer>();
			ArrayList<Integer> depArea54 = new ArrayList<Integer>();
			ArrayList<Integer> depArea55 = new ArrayList<Integer>();
			ArrayList<Integer> depArea56 = new ArrayList<Integer>();
			ArrayList<Integer> depArea57 = new ArrayList<Integer>();
			
			countindex =0; 
			for (int i = 0; i<numberTrains; i++){
				if (parkingMatrix[i][1] == 0){
					depArea52.add(countindex, parkingMatrix[i][0]);
					countindex = countindex +1; 
				}	
			}
			countindex =0; 
			for (int i = 0; i<numberTrains; i++){
				if (parkingMatrix[i][1] == 1){
					depArea53.add(countindex, parkingMatrix[i][0]);
					countindex = countindex +1; 
				}	
			}
			countindex =0; 
			for (int i = 0; i<numberTrains; i++){
				if (parkingMatrix[i][1] == 2){
					depArea54.add(countindex, parkingMatrix[i][0]);
					countindex = countindex +1; 
				}	
			}
			countindex =0; 
			for (int i = 0; i<numberTrains; i++){
				if (parkingMatrix[i][1] == 3){
					depArea55.add(countindex, parkingMatrix[i][0]);
					countindex = countindex +1; 
				}	
			}
			countindex =0; 
			for (int i = 0; i<numberTrains; i++){
				if (parkingMatrix[i][1] == 4){
					depArea56.add(countindex, parkingMatrix[i][0]);
					countindex = countindex +1; 
				}	
			}	
			countindex =0; 
			for (int i = 0; i<numberTrains; i++){
				if (parkingMatrix[i][1] == 5){
					depArea57.add(countindex, parkingMatrix[i][0]);
					countindex = countindex +1; 
				}	
			}	
			
			
			int counterdep = 0;
			int counter4 =0;

			while (minuut <= 1400)
			{	

				// Altijd departure treinen zover mogelijk naar rechts schuiven op spoor 906
				if (positions.get(3) == 0 && positions.get(2) != 0){
					positions.set(3, positions.get(2));
					positions.set(2, 0);
				}
				
				// Altijd alle treinen in de dep-area zover mogelijk naar rechts schuiven (kost geen tijd)
				
				// Spoor 52
				for (int i = 11; i>5 ; i--){
					for (int j= i-1; j>5; j--){
				if (positions.get(i) == 0 && positions.get(j) != 0){
					positions.set(i, positions.get(j));
					positions.set(j, 0);
						}
					}
				}
				
				// Spoor 53
				for (int i = 18; i>12 ; i--){
					for (int j= i-1; j>12; j--){
				if (positions.get(i) == 0 && positions.get(j) != 0){
					positions.set(i, positions.get(j));
					positions.set(j, 0);
						}
					}
				}
				
				// Spoor 54
				for (int i = 24; i>19 ; i--){
					for (int j= i-1; j>19; j--){
				if (positions.get(i) == 0 && positions.get(j) != 0){
					positions.set(i, positions.get(j));
					positions.set(j, 0);
						}
					}
				}
				
				// Spoor 55
				for (int i = 30; i>25 ; i--){
					for (int j= i-1; j>25; j--){
				if (positions.get(i) == 0 && positions.get(j) != 0){
					positions.set(i, positions.get(j));
					positions.set(j, 0);
						}
					}
				}
				
				// Spoor 56
				for (int i = 34; i>31 ; i--){
					for (int j= i-1; j>31; j--){
				if (positions.get(i) == 0 && positions.get(j) != 0){
					positions.set(i, positions.get(j));
					positions.set(j, 0);
						}
					}
				}
				
				// Spoor 57
				for (int i = 37; i>35 ; i--){
					for (int j= i-1; j>35; j--){
				if (positions.get(i) == 0 && positions.get(j) != 0){
					positions.set(i, positions.get(j));
					positions.set(j, 0);
						}
					}
				}

				int[][] a = List.getArrivallist();
				int[] arrivalMin = getarrivalMin(a, 0);
				int[][] d = List.getDeparturelist();
				int[] departureMin = getdepartureMin(d, 0);
				int end = List.endmovement;//create set methods
				int[][] activitylist = List.getActivitylist();
				int[] activityMin = getMin(activitylist, 0);
				ArrayList<Integer> departureArea = new ArrayList<Integer>();
				
				
				// First of all, we check the arrivals, first the trains on track 104, later the trains on trakc 906. 
				if(arrivalMin[1]<=minuut){
					
					if (arrival104.contains(List.getArrivallist()[arrivalMin[0]][1])){
//						== 81002 || List.getArrivallist()[arrivalMin[0]][1] == 80428 || List.getArrivallist()[arrivalMin[0]][1] == 80206 ){
						//arrivals on track 104				
						positions.set(62, List.getArrivallist()[arrivalMin[0]][1]); //put train on position
						List.setArrivallist(Integer.MAX_VALUE, arrivalMin[0]); 
						positionTrainMatrix(63, List.getArrivallist()[arrivalMin[0]][1], matrix, numberTrains); // positie, id, positiematrix
						minuutTrainMatrix(minuut-2, List.getArrivallist()[arrivalMin[0]][1], movementtijdmatrix,numberTrains); // minuut, id, tijdmatrix
						
						if (arrivalMin[3] != -1 ){
							// als meerdere treinen aankomen on track 104
							positions.set(61, List.getArrivallist()[arrivalMin[2]][1]); //put train on position
							List.setArrivallist(Integer.MAX_VALUE, arrivalMin[2]); //set arrival time on inf
							positionTrainMatrix(2, List.getArrivallist()[arrivalMin[2]][1], matrix, numberTrains);
							minuutTrainMatrix(minuut-2, List.getArrivallist()[arrivalMin[2]][1], movementtijdmatrix, numberTrains);
						}
					}
					else {
						//arrivals on track 906
						positions.set(0, List.getArrivallist()[arrivalMin[0]][1]); //put train on position
						List.setArrivallist(Integer.MAX_VALUE, arrivalMin[0]); //set arrival time on inf
						positionTrainMatrix(1, List.getArrivallist()[arrivalMin[0]][1], matrix, numberTrains); // positie, id, positiematrix
						minuutTrainMatrix(minuut-2, List.getArrivallist()[arrivalMin[0]][1], movementtijdmatrix,numberTrains);// minuut, id, tijdmatrix
//						System.out.println(List.getArrivallist()[arrivalMin[0]][1] + "  hoi");
						if (arrivalMin[3] != -1 ){
							// als meerdere treinen aankomen on track 906
							positions.set(1, List.getArrivallist()[arrivalMin[2]][1]); //put train on position
							List.setArrivallist(Integer.MAX_VALUE, arrivalMin[2]); //set arrival time on inf
							positionTrainMatrix(2, List.getArrivallist()[arrivalMin[2]][1], matrix, numberTrains);
							minuutTrainMatrix(minuut-2, List.getArrivallist()[arrivalMin[2]][1], movementtijdmatrix, numberTrains);
							
						}
					}
				}

//				if (minuut == 1220){
//				for (int i = 0; i<100; i++){
//					for (int j=0; j<3; j++){
//						System.out.print(List.getMovementlist()[i][j] + "  ");
//					}
//					System.out.println();
//				}
//				}
				

				// Next step is to check if a departure happens, departure happens always if a train is in the yard
				// So not necessarily on the departing track. 
				
				if(departureMin[1]<=minuut ){ 
					int departurePosition = getIndex(positions, List.getDeparturelist()[departureMin[0]][1]); //find leaving train
					positions.set(departurePosition, 0); //remove leaving train
					List.setDeparturelist(Integer.MAX_VALUE, departureMin[0]); // set departure time on inf
					
					for (int i=0;i<1000;i++){ 
					// set alle moves in movementlist op oneindig van deze trein.
						if (List.getMovementlist()[i][2] == List.getDeparturelist()[departureMin[0]][1]){
							List.setMovementlist(Integer.MAX_VALUE, List.getDeparturelist()[departureMin[0]][1], 1, i);
						}
					}

					if (departureMin[3] != -1){
						// als meerdere treinen tegelijk weg moeten gaan. 
						departurePosition = getIndex(positions, List.getDeparturelist()[departureMin[2]][1]); 
						positions.set(departurePosition, 0); 
						List.setDeparturelist(Integer.MAX_VALUE, departureMin[2]); 
						for (int i=0;i<1000;i++){
							if (List.getMovementlist()[i][2] == List.getDeparturelist()[departureMin[2]][1]){
								List.setMovementlist(Integer.MAX_VALUE, List.getDeparturelist()[departureMin[2]][1], 1, i);
							}
						}
					}
				}
				
//				print iteration (op deze plek omdat nu alle treinen in het model staan van deze minuut. 			
				printIteration(positions, minuut);
				int[][] aa = List.getArrivallist();
				results = makeResults(minuut, aa, results, positions);
				
				// Set movement op false op het moment dat er een move klaar is op deze minuut. 
				if(end==minuut){
					movement=false; 
					List.setEndmovement(Integer.MAX_VALUE);		
				}

				// Set activity's uit op het moment dat ze klaar zijn, er van uitgaande dat het zelden gebeurd dat twee activities 
				// in dezelfde minuut eindigen.
				if (activityMin[1] <= minuut){ 
					List.setActivitylist(activityMin[0], 1, Integer.MAX_VALUE);
					List.setActivitylist(activityMin[0], 3, 0);
				}

				
				// Eerste move check: arrival track leegmaken.
				int indexcheck = -1;
				int positiearrival = -1;

				if (movement == false){
					//Check arrival track voor beide arrival tracks, eerst en voor meerdere posities op de tracks
					if(positions.get(0)!=0)
					{positiearrival = 0;}
					if (positions.get(62)!=0)
					{positiearrival = 62; }
					if (positiearrival != -1){
						int endPosition = -1;	
						for (int i=0;i<priorityArrivalarea.length;i++){
							movementTime = move.possibleMovement(blockdata, positiearrival+1, priorityArrivalarea[i], positions, Data, Yard);
							if(movementTime!=0 && movementTime<100){							
								endPosition = priorityArrivalarea[i];
								int id = positions.get(positiearrival);
								positions.set(endPosition-1, id);
								positions.set(positiearrival, 0);
								timeMovement = minuut + 2;
								movement = true;
								List.setEndmovement(timeMovement);
								setMovementList(id, blockdata, numberTrains);
								for (int n = 0; n<50; n++){
									if (List.getDeparturelist()[n][1] == id){
										indexcheck = n;
									}
								}
								if ((getBooleans(id,1, blockdata, numberTrains) + timeMovement) < List.getDeparturelist()[indexcheck][0] ){
									StartEvent(id, 0, timeMovement, blockdata, numberTrains);
								}
								positionTrainMatrix(endPosition, id, matrix, numberTrains);
								minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
								break;
							}
						}
					}
				}
				
				positiearrival = -1;	
				indexcheck = -1;
				if (movement == false){
					//Nu hetzelfde voor andere posities
					if(positions.get(1)!=0){
						positiearrival = 1; 
					}
					if (positions.get(61) !=0){
						positiearrival = 61; }
					if (positiearrival != -1){
						int endPosition = -1;	
						for (int i=0;i<priorityArrivalarea.length;i++){
							movementTime = move.possibleMovement(blockdata, positiearrival+1, priorityArrivalarea[i], positions, Data, Yard);
							if(movementTime!=0 && movementTime<100){
								endPosition = priorityArrivalarea[i];
								int id;
								if(positiearrival==0){
									id = positions.get(positiearrival+1);
								} else {
									id = positions.get(positiearrival);
								}
								positions.set(endPosition-1, id);
								if(positiearrival==0){
									positions.set(positiearrival+1, 0);
								} else {
									positions.set(positiearrival, 0);
								}							
								timeMovement = minuut + 2;
								movement = true;
								List.setEndmovement(timeMovement);
								setMovementList(id, blockdata, numberTrains);
								for (int n = 0; n<50; n++){
									if (List.getDeparturelist()[n][1] == id){
										indexcheck = n;
									}
								}
								if ((getBooleans(id,1, blockdata, numberTrains) + timeMovement) < List.getDeparturelist()[indexcheck][0] ){
									StartEvent(id, 0, timeMovement, blockdata, numberTrains);
								}
								positionTrainMatrix(endPosition, id, matrix, numberTrains);
								minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
								break;
							}
						}
					}
				}
				
				// Next priority is het legen van de wasmachines, dit is belangrijk zodat deze zsm weer gebruikt kunnen worden

				int intWashposition = -1; 
				boolean activity = false;
				int idextra = -1;

				if (movement == false){//reinigingsperron
					for (int i = 47; i<54;i++){
						activity = false; //trein mag niet verplaatsen, geeft aan activity finished
						if(positions.get(i)!=0 && movement == false){	
							idextra = positions.get(i);
							for(int j=0;j<50;j++){
								// first check is of er een activity aan de gang is (als activity = true, dan geen activity meer)
								if(List.getActivitylist()[j][1]==idextra){
									if(List.getActivitylist()[j][2]==0){ //activity check
										activity = true; // er wordt geen activity meer gedaan
										intWashposition = i+1;
									}
								}
							}
						}

						if (activity == true){
							// Check het id in de activitylist om te kijken wat hij hierna nog moet doen.
							int idcheck = positions.get(intWashposition-1);
							int location = -1;
							for(int j=0; j<50;j++){
								if(List.getActivitylist()[j][1]==idcheck){
									location = j;
								}
							}
							
							// Als hij niks meer hoeft te doen, dan richting de departure area, geen activities meer. 
							if (List.getActivitylist()[location][3] == 0 && List.getActivitylist()[location][4] == 0 && List.getActivitylist()[location][5] == 0 && List.getActivitylist()[location][6] == 0){
								int endPosition = -1;
								
								int counterindex = 0; 
								
								if (depArea52.contains(positions.get(intWashposition-1))){
									for (int p = 11; p > 5; p--){
									departureArea.add(counterindex, p);
									counterindex = counterindex +1; 
									}
								}
								counterindex = 0;
								if (depArea53.contains(positions.get(intWashposition-1))){
									for (int p = 18; p > 12; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								if (depArea54.contains(positions.get(intWashposition-1))){
									System.out.println(positions.get(intWashposition-1));
									for (int p = 24; p > 19; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								if (depArea55.contains(positions.get(intWashposition-1))){
									for (int p = 30; p > 25; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								if (depArea56.contains(positions.get(intWashposition-1))){
									for (int p = 34; p > 31; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								if (depArea57.contains(positions.get(intWashposition-1))){
									for (int p = 37; p > 35; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								for (int q=0;q<departureArea.size();q++){
									movementTime = move.possibleMovement(blockdata, intWashposition, departureArea.get(q), positions, Data, Yard);
									if(movementTime!=0 && movementTime <100){
										endPosition = departureArea.get(q);
										int id = positions.get(intWashposition-1);
										positions.set(endPosition-1, id);
										positions.set(intWashposition-1, 0);
										timeMovement = minuut + 2;
										movement = true;
										List.setEndmovement(timeMovement);
										positionTrainMatrix(endPosition, id, matrix, numberTrains);
										minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
										int finalt = -1;
										for (int t = 0; t<100; t++){
											if (List.getMovementlist()[t][2] == id && List.getMovementlist()[t][1] == 3){
												finalt = t;
											}
										}
										List.setMovementlist(Integer.MAX_VALUE, id, 3 , finalt);
										break;
									}
								}
							}
							
							// als hij nog extern gewassen moet worden gaat hij daar naar toe. 
							else if (List.getActivitylist()[location][3] != 0){
								int endPosition = -1;
								for (int q=0;q<priorityType2.length;q++){
									movementTime = move.possibleMovement(blockdata, intWashposition, priorityType2[q], positions, Data, Yard);
									if(movementTime!=0 && movementTime <100){
										endPosition = priorityType2[q];
										int id = positions.get(intWashposition-1);
										positions.set(endPosition-1, id);
										positions.set(intWashposition-1, 0);
										timeMovement = minuut + 2;
										movement = true;
										List.setEndmovement(timeMovement);
										positionTrainMatrix(endPosition, id, matrix, numberTrains);
										minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
										for (int n = 0; n<50; n++){
											if (List.getDeparturelist()[n][1] == id){
												indexcheck = n;
											}
										}
										if (getBooleans(id,4, blockdata, numberTrains) + timeMovement < List.getDeparturelist()[indexcheck][0] ){
											StartEvent(id, 2, timeMovement, blockdata, numberTrains);								
										}
										int finalt = -1;
										for (int t = 0; t<100; t++){
											if (List.getMovementlist()[t][2] == id && List.getMovementlist()[t][1] == 2){
												finalt = t;
											}
										}
										List.setMovementlist(Integer.MAX_VALUE, id, 2 , finalt);

										break;
									}
								}	
							}
							
							else{
								// Als hij nog iets anders moet doen (kan eigenlijk niet, maar to be sure), gaat hij terug naar de arrival area
								int endPosition = -1;
								for (int q=0;q<priorityArrivalarea.length;q++){
									movementTime = move.possibleMovement(blockdata, intWashposition, priorityArrivalarea[q], positions, Data, Yard);
									if(movementTime!=0 && movementTime <100){
										endPosition = priorityArrivalarea[q];
										int id = positions.get(intWashposition-1);
										positions.set(endPosition-1, id);
										positions.set(intWashposition-1, 0);
										timeMovement = minuut + 2;
										movement = true;
										List.setEndmovement(timeMovement);
										positionTrainMatrix(endPosition, id, matrix, numberTrains);
										minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
										break;
									}
								}
							}
						}
					}
				}

				// Daarna gaan we de external cleaning machine leeg maken.
			
				
				int extWashposition = -1; 
				activity = false;
				idextra = -1;

				if (movement == false){
					for (int i = 55; i<59;i++){ //posities van externe wasmachine
						activity = false;
						if(positions.get(i)!=0 && movement == false){	
							idextra = positions.get(i);
							for(int j=0;j<50;j++){
								// check of er een activity plaats vind (activity = true als hij niet meer bezig is)
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
							if (List.getActivitylist()[location][3] == 0 && List.getActivitylist()[location][4] == 0 && List.getActivitylist()[location][5] == 0 && List.getActivitylist()[location][6] == 0){
								// als alle activities klaar zijn gaat hij naar de departure area
								int endPosition = -1;	
								int counterindex = 0; 
								
								if (depArea52.contains(positions.get(extWashposition-1))){
									for (int p = 11; p > 5; p--){
									departureArea.add(counterindex, p);
									counterindex = counterindex +1; 
									}
								}
								counterindex = 0;
								if (depArea53.contains(positions.get(extWashposition-1))){
									for (int p = 18; p > 12; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								if (depArea54.contains(positions.get(extWashposition-1))){
									for (int p = 24; p > 19; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								if (depArea55.contains(positions.get(extWashposition-1))){
									for (int p = 30; p > 25; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								if (depArea56.contains(positions.get(extWashposition-1))){
									for (int p = 34; p > 31; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								if (depArea57.contains(positions.get(extWashposition-1))){
									for (int p = 37; p > 35; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								for (int q=0;q<departureArea.size();q++){
									movementTime = move.possibleMovement(blockdata, extWashposition, departureArea.get(q), positions, Data, Yard);
									if(movementTime!=0 && movementTime <100){
										endPosition = departureArea.get(q);
										int id = positions.get(extWashposition-1);
										positions.set(endPosition-1, id);
										positions.set(extWashposition-1, 0);
										timeMovement = minuut + 2;
										movement = true;
										List.setEndmovement(timeMovement);
										positionTrainMatrix(endPosition, id, matrix, numberTrains);
										minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
										int finalt = -1;
										for (int t = 0; t<100; t++){
											if (List.getMovementlist()[t][2] == id && List.getMovementlist()[t][1] == 3){
												finalt = t;
											}
										}
										List.setMovementlist(Integer.MAX_VALUE, id, 3 , finalt);
										break;
									}
								}
							}
							else{
								//Zoniet dan gaat hij naar de arrival area (niet terug naar internal cleaning omdat die te druk bezet is). 
								int endPosition = -1;
								for (int q=0;q<priorityArrivalarea.length;q++){
									movementTime = move.possibleMovement(blockdata, extWashposition, priorityArrivalarea[q], positions, Data, Yard);						
									if(movementTime!=0 && movementTime <100){
										endPosition = priorityArrivalarea[q];
										int id = positions.get(extWashposition-1);
										positions.set(endPosition-1, id);
										positions.set(extWashposition-1, 0);
										timeMovement = minuut + 2;
										movement = true;
										List.setEndmovement(timeMovement);
										positionTrainMatrix(endPosition, id, matrix, numberTrains);
										minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
										break;
									}
								}
							}
						}
					}
				}

				// next we start with the normal movement list.

				if (movement == false){ //Job shop model has priority over other moves!!
//					System.out.println("JobShop");
					int[] platform1 = {48, 49, 50, 51};
					int[] platform2 = {52, 53, 54, 55};
					int locatie = -1;
					boolean plat1fill = false; //did we fill the first platform
					boolean plat2fill = false;
					//if platform 1 empty --> check priorityPlatform1
					for(int j=0;j<priorityPlatform1.size();j++){ //Check all compositions 
						for (int uu = 0; uu<numberTrains; uu++){
							if (List.getActivitylist()[uu][1] == priorityPlatform1.get(j)){// check activity
								locatie = uu; 
//								System.out.println(priorityPlatform1.get(j));
//								System.out.println(List.getActivitylist()[locatie][0]);
							}
						}
						if (List.getActivitylist()[locatie][0] > 10000){
						for(int p=0;p<platform1.length;p++){ //for all compositions check all platforms
							//check possible move
							if(plat1fill==false){
								int currentPosition = getIndex(positions, priorityPlatform1.get(j));
								if (currentPosition != -1){ // als hij nog niet in het model is
								movementTime = move.possibleMovement(blockdata, currentPosition+1, platform1[p], positions, Data, Yard);
								if(movementTime!=0 && movementTime<100){
									plat1fill = true;
									int endPosition = platform1[p];
									int id = priorityPlatform1.get(j);
									positions.set(endPosition-1, id);
									positions.set(currentPosition, 0);
									timeMovement = minuut + 2;
									movement = true;
									List.setEndmovement(timeMovement);
									for (int n = 0; n<50; n++){
										if (List.getDeparturelist()[n][1] == id){
											indexcheck = n;
										}
									}
									if (getBooleans(id,3, blockdata, numberTrains) + timeMovement < List.getDeparturelist()[indexcheck][1] ){
										StartEvent(id, 1, timeMovement, blockdata, numberTrains);//internal
									}
									positionTrainMatrix(endPosition, id, matrix, numberTrains);
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
									minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
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
						}
					}
					locatie = -1;
					//if platform 2 empty --> check priorityPlatform2
					for(int j=0;j<priorityPlatform2.size();j++){ //Check all compositions 
						for (int uu = 0; uu<numberTrains; uu++){
							if (List.getActivitylist()[uu][1] == priorityPlatform2.get(j)){
								locatie = uu; 
//								System.out.println(priorityPlatform2.get(j));
//								System.out.println(List.getActivitylist()[locatie][0]);
							}
						}
						if (List.getActivitylist()[locatie][0] >10000){
						for(int p=0;p<platform2.length;p++){ //for all compositions check all platforms
							//check possible move
							if(plat2fill==false){
								int currentPosition = getIndex(positions, priorityPlatform2.get(j));
								if (currentPosition != -1){
								movementTime = move.possibleMovement(blockdata, currentPosition+1, platform2[p], positions, Data, Yard);
								if(movementTime!=0 && movementTime<100){
									plat2fill = true;			
									int endPosition = platform2[p];
									int id = priorityPlatform2.get(j);
									positions.set(endPosition-1, id);
									positions.set(currentPosition, 0);
									timeMovement = minuut + 2;
									movement = true;
									List.setEndmovement(timeMovement);
									for (int n = 0; n<50; n++){
										if (List.getDeparturelist()[n][1] == id){
											indexcheck = n;
										}
									}
									if (getBooleans(id,3, blockdata, numberTrains) + timeMovement < List.getDeparturelist()[indexcheck][1] ){
										StartEvent(id, 1, timeMovement, blockdata, numberTrains);//internal
									}
									positionTrainMatrix(endPosition, id, matrix, numberTrains);
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
									minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
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
				}
				}

				boolean moveexecuted = false; // geeft aan of er in de iteratie al een move is uitgevoerd (zoniet dan moet hij een volgende zoekN).
				indexcheck = -1;
				
				if (movement == false){	
					// first we copy the current movementlist, als we een infeasible move tegen komen zetten we m in deze copy op oneindig
					int[][] m = List.getMovementlist(); 
					int[][] x =  new int[1000][3] ;
					for (int i = 0; i<1000;i++){
						for (int j = 0; j<3;j++){
							x[i][j] = m[i][j];
						}
					}
			
					while (moveexecuted == false){
						boolean alreadyDeparture = false; // checkt of een trein al in de departure area staat
						int[] movementMin = getPossibleMin(x,0, minuut); //check index -1
						if(movementMin[0] !=-1){ //move found!

							int movementType = x[movementMin[0]][1]; // type
							int movementTrainID = x[movementMin[0]][2]; // id
							int currentPosition = getIndex(positions, movementTrainID); // current position
							int endPosition = -1; // initialize end position
							int time = movementMin[2]; // minimum movement time, nodig voor departure track, max verblijf op die track. 

							// Start with the real moves, per type. 
							
							if(movementType ==1){  // internal cleaning
							
//								for (int i=0;i<priorityType1.length;i++){	
//									movementTime = move.possibleMovement(blockdata, currentPosition+1, priorityType1[i], positions, Data, Yard);				
//									if(movementTime!=0 && movementTime<100){
//										moveexecuted = true;
//										endPosition = priorityType1[i];
//										int id = movementTrainID;
//										positions.set(endPosition-1, id);
//										positions.set(currentPosition, 0);
//										timeMovement = minuut + 2;
//										movement = true;
//										List.setEndmovement(timeMovement);
//										for (int n = 0; n<50; n++){
//											if (List.getDeparturelist()[n][1] == id){
//												indexcheck = n;
//											}
//										}
//										if (getBooleans(id,3) + timeMovement < List.getDeparturelist()[indexcheck][0] ){
//											// check of hij met de activiteit mag beginnen, zoniet dan blijft de activity op 1 staan.
//											StartEvent(id, 1, timeMovement);
//										}
//										positionTrainMatrix(endPosition, id, matrix);
//										minuutTrainMatrix(minuut, id, movementtijdmatrix);
//										List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
//										break;
//									}
//								}
								if (moveexecuted == false) {
									// Als hij geen move gevonden heeft, wordt de kopie van de movementlist op oneindig gezet
									x[movementMin[0]][0] = Integer.MAX_VALUE;	
								}
							}
							else if(movementType ==2){ // external cleaning
							
								for (int i=0;i<priorityType2.length;i++){
									movementTime = move.possibleMovement(blockdata, currentPosition+1, priorityType2[i], positions, Data, Yard);
									if(movementTime!=0 && movementTime<100){
										moveexecuted = true;
										endPosition = priorityType2[i];
										int id = movementTrainID;
										positions.set(endPosition-1, id);
										positions.set(currentPosition, 0);
										timeMovement = minuut + 2;
										movement = true;
										List.setEndmovement(timeMovement);
										for (int n = 0; n<50; n++){
											if (List.getDeparturelist()[n][1] == id){
												indexcheck = n;
											}
										}
										if (getBooleans(id,4, blockdata, numberTrains) + timeMovement < List.getDeparturelist()[indexcheck][0] ){
											StartEvent(id, 2, timeMovement, blockdata, numberTrains);								
										}
										List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
										positionTrainMatrix(endPosition, id, matrix, numberTrains);
										minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
										break;
									}
								}
								if (moveexecuted == false) {
									x[movementMin[0]][0] = Integer.MAX_VALUE;	
								}
							} 

							else if(movementType ==3){ //move to depart area
								
								int counterindex = 0; 
								
								if (depArea52.contains(positions.get(currentPosition))){
									for (int p = 11; p > 5; p--){
									departureArea.add(counterindex, p);
									counterindex = counterindex +1; 
									}
								}
								counterindex = 0;
								if (depArea53.contains(positions.get(currentPosition))){
									for (int p = 18; p > 12; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								if (depArea54.contains(positions.get(currentPosition))){
									for (int p = 24; p > 19; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								if (depArea55.contains(positions.get(currentPosition))){
									for (int p = 30; p > 25; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								if (depArea56.contains(positions.get(currentPosition))){
									for (int p = 34; p > 31; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								if (depArea57.contains(positions.get(currentPosition))){
									for (int p = 37; p > 35; p--){
										departureArea.add(counterindex, p);
										counterindex = counterindex +1; 
										}
									}
								counterindex = 0;
								for (int q=0;q<departureArea.size();q++){
									
									if (currentPosition == departureArea.get(q)){
										alreadyDeparture = true;
										List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
									}
									if (alreadyDeparture = false){
										movementTime = move.possibleMovement(blockdata, currentPosition+1, departureArea.get(q), positions, Data, Yard);
										if(movementTime!=0 && movementTime<100){
											moveexecuted = true;
											endPosition = departureArea.get(q);
											int id = movementTrainID;
											positions.set(endPosition-1, id);
											positions.set(currentPosition, 0);
											timeMovement = minuut + 2;
											movement = true;
											List.setEndmovement(timeMovement);
											for (int n = 0; n<50; n++){
												if (List.getDeparturelist()[n][1] == id){
													indexcheck = n;
												}
											}
											List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
											positionTrainMatrix(endPosition, id, matrix, numberTrains);
											minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
											break;
										}
									}
								}
								if (moveexecuted == false) {
									x[movementMin[0]][0] = Integer.MAX_VALUE;	
								}
							} 
							
						else if(movementType ==4  ){	//naar departuretrack, twee keer voor verschillende departure tracks. 
								if (minuut > time - 5){	//not infinite on departing track, max 3 minutes. 
									if (departure104.contains(movementTrainID)){
//									if (movementTrainID == 80428 || movementTrainID == 80206 || movementTrainID == 83071){
										for (int i=0;i<priorityType4extra.length;i++){
											if (priorityType4[i] != 1 && priorityType4[i] != 2){
											movementTime = move.possibleMovement(blockdata, currentPosition+1, priorityType4extra[i], positions, Data, Yard);
											if(movementTime!=0 && movementTime<100){
												moveexecuted = true;
												endPosition = priorityType4extra[i];
												int id = movementTrainID;
												positions.set(endPosition-1, id);
												positions.set(currentPosition, 0);
												timeMovement = minuut + 2;
												movement = true;
												List.setEndmovement(timeMovement);
												counterdep = counterdep +1;  //  belangrijke counter, telt het aantal succesvolle departures
												positionTrainMatrix(endPosition, id, matrix, numberTrains);
												minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
												List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
												
												for (int e=0;e<1000;e++){ 
													// set alle moves in movementlist op oneindig van deze trein.
														if (List.getMovementlist()[e][2] == id){
															List.setMovementlist(Integer.MAX_VALUE, id, 1, e);
														}
													}
												
												break;
											}

										}	
										}
										if (moveexecuted == false) {

											x[movementMin[0]][0] = Integer.MAX_VALUE;	
										}

									}
									else {
										for (int i=0;i<priorityType4.length;i++){
											if (priorityType4[i] != 1 && priorityType4[i] != 2){
											movementTime = move.possibleMovement(blockdata, currentPosition+1, priorityType4[i], positions, Data, Yard);
											if(movementTime!=0 && movementTime<100){
												moveexecuted = true; 
												endPosition = priorityType4[i];
												int id = movementTrainID;
												positions.set(endPosition-1, id);
												positions.set(currentPosition, 0);
												timeMovement = minuut + 2;
												movement = true;
												List.setEndmovement(timeMovement);
												counterdep = counterdep +1; //  belangrijke counter, telt het aantal succesvolle departures
												positionTrainMatrix(endPosition, id, matrix, numberTrains);
												minuutTrainMatrix(minuut, id, movementtijdmatrix, numberTrains);
												List.setMovementlist(Integer.MAX_VALUE, movementTrainID, movementType , movementMin[0]);
												
												for (int e=0;e<1000;e++){ 
													// set alle moves in movementlist op oneindig van deze trein.
														if (List.getMovementlist()[e][2] == id){
															List.setMovementlist(Integer.MAX_VALUE, id, 1, e);
														}
													}
												break;
											}
										}
										}
										if (moveexecuted == false) {
											x[movementMin[0]][0] = Integer.MAX_VALUE;	
										}
									}
								}
								else {x[movementMin[0]][0] = Integer.MAX_VALUE; } // als die geen enkele move gevonden heeft deze minuut.
							}
						}
						if (movementMin[2] > 10000){ // als minimum moventtime allemaal te hoog worden stoppen we deze minuut. 
							moveexecuted = true; 
						}
					} // end while loop voor de move executed
				}	// if loop of de movement false is
			
//				for (int i = 0 ; i<5; i++){
//					System.out.println(List.getActivitylist()[i][1] + "  " + List.getActivitylist()[i][0]);
//					System.out.println(minuut);
//				}
				minuut++;
			} //while minuut deadline is nog niet bereikt

		
			// Print activitylist
//			for (int i=0; i<numberTrains;;i++){
//				for(int j=0; j<8;j++){
//						System.out.print("  "+List.getActivitylist()[i][j]);
//					}System.out.println();
//				}
			
			// print end position matrix
			System.out.println();
			printpositionTrainMatrix(matrix, numberTrains);
			System.out.println();
			printtijdTrainMatrix(movementtijdmatrix, numberTrains);
			
			// print performance
			double result1 = printPerformance(List.activitylist);
			double result2 = counterdep; 
			double[] results2 = new double[2];
			results2[0] = result1; // all activity's
			results2[1] = result2;  //right track?
			

			
			return results2; 
			
		}
		
		// Informatie over de activitylist uitlezen. 
		// 5 is inspectie
		// 4 is cleaning
		// 3 is washing
		// 6 is repairing



		public int[][] makeResults (int minuut, int[][]arrivallist, int[][] results, ArrayList<Integer> positions  ){
			
//			int[][] results = new int[24][1400]; 
			
			// id's invullen in eerste kolom
			for (int i = 1; i < 24; i++){  
				results[i][0] = arrivallist[i-1][1];
			}
			
			// eerst eerste rij invullen;
			for (int i = 1; i < 1402; i++){  
				results[0][i] = i-1;
			}
			
			// posities invullen:
				for (int j = 1 ; j < 24; j++){
						for (int l = 0 ; l<67;l++){
						if (results[j][0] == positions.get(l)){  // id's gelijk
								results[j][minuut+1] = l+1;
//								System.out.println("yes" + results[j][minuut+1]);
								break;
						}
						else 
						 {
							results[j][minuut+1] = 0; 
						}			
						}	
				}
				for (int j = 1 ; j < 24; j++){
//					System.out.println("no" + results[j][minuut+1]);
					                               
				}
				
				return results; 
		}

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
				}
			}
			int[] y = {index, minvalue, index2, minvalue2};
			return y;
		}

		public int[] getPossibleMin(int[][] x, int z, int minuut){ //movementlist erin [time type id]
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
							found = 1;
							possible = id;
						} else {
							activity = true;
							xx[index][z] = Integer.MAX_VALUE;
							minvalue = Integer.MAX_VALUE;
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
			for (int i=0; i<100; i++){
				if (minuut + 5 > x[i][0] && x[i][1] == 4){
					index = i;
					minvalue = x[i][0];
					possible = x[i][2];
				}
					
			}
			
			int[] y = {index, possible, minvalue};
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

		public int getBooleans(int id, int bool, int[][] blockdata, int numberTrains){ //inspect, repair, clean, wash
			int time = 0;
			int idindex = -1;
			
			for (int i = 0; i<numberTrains; i++){
				if (blockdata[i][0] == id){
					 idindex = i;
				}
			}
			
			if (bool == 2){
			time = blockdata[idindex][10];
			}
			if (bool == 1){
			time = blockdata[idindex][7];
				}
			if (bool == 3){
			time = blockdata[idindex][8];
				}
			if (bool == 4){
			time = blockdata[idindex][9];
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

		public void setMovementList(int id, int[][] blockdata, int numberTrains){ // vult de movementmatrix met uiterlijke tijden voor het rijden naar area's. 

			// per train ID moeten we de volgende dingen uit gaan lezen: 

			
			double departureTime = getDepartureTime(id);
			int washExtern = getBooleans(id, 4, blockdata, numberTrains);
			int washIntern = getBooleans(id, 3, blockdata, numberTrains);
			int inspection = getBooleans(id, 1, blockdata, numberTrains);
			int repair = getBooleans(id, 2, blockdata, numberTrains);
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
			type4Event = departureTime - movementtime;  //create event time
			List.setMovementlist((int) type4Event, id, 4, location); //fill list	
			location++;
			// --------------------------------------------------------------------------------------------------		 
			//SET TYPE 3 TO BE THE ULTIMATE_START_REPAIR_EVENT	


			type3Event = type4Event - movementtime-5;
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

		public void StartEvent(int id, int type, int arrival, int[][] blockdata, int numberTrains){ // vult de eventlist met events (ANDERS DAN DE MOVEMENTLIST!)  
			//
			//	 // IF A TRAIN ARRIVES, OR IF A END MOVEMENT EVENT OCCURS, WE WILL CALL THIS METHOD. PER TRAIN ID SHOULD EXIST A EVENT MATRIX:
			//	 // matrix:  TIME(1) -- ID(2) -- CURRENT event(3) -- WASHEXTERN(4) -- WASHINTERN(5) --- INSPECTION(6) -- REPAIR(7) -- event counter(8)
			int washExtern = getBooleans(id, 4, blockdata, numberTrains);
			int washIntern = getBooleans(id, 3, blockdata, numberTrains);
			int inspection = getBooleans(id, 1, blockdata, numberTrains);
			int repair = getBooleans(id, 2, blockdata, numberTrains);

			int location = -1;
			for(int i=0; i<50;i++){
				if(List.getActivitylist()[i][1]==id){
					location = i;
				}
			}

//			System.out.println("dit22  " + type + "  " + id + "   " + inspection);
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

		public void fillInitialActivitylist(int[][] blockdata, int numberTrains){
			//	 // IF A TRAIN ARRIVES, OR IF A END MOVEMENT EVENT OCCURS, WE WILL CALL THIS METHOD. PER TRAIN ID SHOULD EXIST A EVENT MATRIX:
			//	 // matrix:  TIME(1) -- ID(2) -- CURRENT event(3) -- WASHEXTERN(4) -- WASHINTERN(5) --- INSPECTION(6) -- REPAIR(7) -- event counter(8)
			for(int i=0;i<50;i++){
				int id = List.getArrivallist()[i][1];

				if(id!=0){
					int boolwashex=0;
					int boolwashin=0;
					int boolins=0;
					int boolrep=0;
					int washExtern = getBooleans(id, 4, blockdata, numberTrains);
					if(washExtern>0){boolwashex = 1;}
					int washIntern = getBooleans(id, 3, blockdata, numberTrains);
					if(washIntern>0){boolwashin = 1;}
					int inspection = getBooleans(id, 1, blockdata, numberTrains);
					if(inspection>0){boolins = 1;}
					int repair = getBooleans(id, 2, blockdata, numberTrains);
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
			double minPerformance =0;
			double maxPerformance=0;
			double totalPerformance=0;
			double countPerformance=0;
			double allDone = 0;
			double unperformed =0; 
			double performance = 0; 

			for (int i=0;i<50;i++){
				if(activity[i][1]!=0){ //a train is assigned to the row
					unperformed = unperformed + activity[i][3]+activity[i][4]+activity[i][5]+activity[i][6]; //needed but undone binary
					performance = performance + activity[i][7]; 		
					}
				}
			double result =  (performance/(performance+unperformed));
			return result;
		}

		public void printIteration(ArrayList<Integer> p, int minuut){
			System.out.print("Minuut: " + minuut + "  ");
			for (int i=0;i<p.size();i++){
				System.out.print(p.get(i) + " ");
			}
			System.out.println("");
		}

		public int[][] positionTrainMatrix(int positienieuw, int id, int[][] matrix, int numberTrains){
			int indexrij = -1;
			int indexkolom = -1;
			for (int i = 0; i<numberTrains ; i++){
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

		public int[][]	minuutTrainMatrix(int minuut, int id, int[][] matrix, int numberTrains){
			int indexrij = -1;
			int indexkolom = -1;
			for (int i = 0; i<numberTrains ; i++){
				if (matrix[i][0] == id){
					indexrij = i;
					break;
				}
			}
			for (int j = 0; j<15 ; j++){

				if (matrix[indexrij][j] == 0){
					indexkolom = j;	
					break;
				}
			}
			matrix[indexrij][indexkolom] = (minuut+2);	
			return matrix;
		}


		public void printpositionTrainMatrix(int [][] positionTrainMatrix, int numberTrains){
			

		
			System.out.println("deze posities heeft elke trein gehad");
			for (int i = 0; i<numberTrains ; i++){
				for (int j = 0; j<15; j++){
					System.out.print(positionTrainMatrix[i][j]+ "     ");
				}
				System.out.println("");
			}
		}

		public void printtijdTrainMatrix(int [][] positionTrainMatrix, int numberTrains){
			 int[][] xx  = new int[1][15];
		
			 
			for (int i = 1 ; i<14; i++){
				xx[0][i+1] =  positionTrainMatrix[0][i];
			}
			positionTrainMatrix[0][1] = 0;
			for (int i = 2 ; i<15; i++){
				positionTrainMatrix[0][i] = xx[0][i] ;
			}
			
			System.out.println("deze tijden heeft elke trein gehad");
			for (int i = 0; i<numberTrains ; i++){
				for (int j = 0; j<15; j++){
					System.out.print(positionTrainMatrix[i][j] + "     ");
				}
				System.out.println("");
			}
		}

		//public void postionsdeparturelist(int oldposition, int newposition, int id, int [] positiondeparturelist){
		//	for (int i = 0; i< 30; i++){
		//		if (List.getDeparturelist()[i][1] == id){
		//			positiondeparturelist[oldposition] = 0;
		//			positiondeparturelist[newposition] = List.getDeparturelist()[i][1];
		//		}
		//	}
		//}
		//
		//
		//public int[] departureprioritylist(int [] positiondeparturelist, int id){
		//	int departuretime = -1;
		//	for (int i = 0; i< 30; i++){
		//		if (List.getDeparturelist()[i][1] == id){
		//			departuretime = List.getDeparturelist()[i][1];
		//			break;
		//		}
		//	}
		//	int [] track1 =			{5, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0}	;		//t52 depart area
		//	int [] track2 =				{12, 13, 14, 15, 16, 17, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0};	//t53 depart area
		//	int [] track3 =				{19, 20, 21, 22, 23, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};		//t54 depart area
		//	int [] track4 =				{25, 26, 27, 28, 29, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};		//t55 depart area
		//
		//	boolean firstfull = false; 
		//	int lastposition = -1;
		//	int [] departurepriority = new int[30];
		//	// spoor 1: 
		//	firstfull = false; 
		//	lastposition = 11; 
		//	for (int i = 6; i >= 0; i--){
		//		if (positiondeparturelist[track1[i]] != 0){
		//			lastposition = track1[i];
		//		}
		//	}
		//
		//	if (lastposition != 5 && departuretime > positiondeparturelist[lastposition]){
		//					place on postion -1
		//				}
		//					if {depa
		//				}

	}


