package pandemic;
/*This is just a simple pandemic file.  It allows the user to type in a few commands.
  It reads in the cities from a file.
  The user can quit, print their current location, and print the actions they can perform,
  move, print all cities, print all connections, and print connections from the current location.
  You probably need to change the path for the cityMapFileName below.
  Chris Huyck wrote this.  I've not asked the pandemic folks to use their game, so don't 
  distribute it beyond CST 3170.
 */

//Note: you need to import scanner to use it to read from the screen.
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays; // save keypresses
import java.util.Collections;

//There's only one class in this program.  All functions are here, and it runs from
//the main function.
public class Simulation {
	//class variables on top
	private static Scanner shellInput;    //These two are for the shell scanner.
	private static boolean shellOpen = false;
	//Note use a seed (1337) for debugging.  
	private static Random randomGenerator = new Random();
	
	// geography comes back to bite us
	private static int numberCities = -1;	
	private static int numberConnections = -1;	
	private static String[] cities; //Cities
	private static int lenCities = 48;
	private static ArrayList<String> infectedCities = new ArrayList<String>(lenCities);;
	private static int[] diseaseCubes; //Number of disease cubes in the associated city.
	private static  int[][] connections; //The connections via offset in the cities array.
	private static int[] userLocation = {0,0};  //These are the users' location that can change.
	
	// gay cards
	private static int epidemicCards = 5;
	private static int userDeckSize = 48 + epidemicCards;
	private static int infectionDeckSize = 48;
	private static int userDeckCardsRemoved = 0;
	private static int discardDeckSize = 0;
	private static ArrayList<String> userDeck = new ArrayList<String>(userDeckSize);
	private static ArrayList<String> infectionDeck = new ArrayList<String>(infectionDeckSize);
	private static ArrayList<String> discardDeck = new ArrayList<String>(userDeckSize);
	
	// cities colours
	private static String[] blueCities;
	private static String[] yellowCities;
	private static String[] redCities;
	private static String[] blackCities;
	
	// Disease spread meter
	private static int blueCubesLeft = 24;
	private static int yellowCubesLeft = 24;
	private static int redCubesLeft = 24;
	private static int blackCubesLeft = 24;
	
	// Cure diseases and research stations
	private static ArrayList<String> researchStations = new ArrayList<String>(6);
	private static boolean blueDiseaseCure = false;
	private static boolean yellowDiseaseCure = false;
	private static boolean redDiseaseCure = false;
	private static boolean blackDiseaseCure = false;
	private static int maxStations = 6;
	//private static int curesDiscovered = 0;
	
	// Outbreak meter
	private static int outbreaks = 0;
	private static int outbreaksMax = 8;

	//##Change this to your path.##
	private static final String cityMapFileName= "./data/fullMap.txt";
	
	// Players configs
	private static final int NUMBER_USERS = 2;
	private static final String[] userNames = {"Human","The Machine"};
	
	// Player turn
	private static int turnsLeft = 4;
	private static int handLimit = 7;
	private int numberCardsDraw = 2;
	private static int currentUser = 0;
	private static ArrayList<String> userOneHand = new ArrayList<String>(handLimit);
	private static ArrayList<String> userTwoHand = new ArrayList<String>(handLimit);;
	
	//The constants for the commands.
	private static final int QUIT = 0;
	private static final int PRINT_LOCATION = 1;
	private static final int MOVE = 2;
	private static final int PRINT_ACTIONS = 3;
	private static final int PRINT_CITIES = 4;
	private static final int PRINT_CONNECTIONS = 5;
	private static final int PRINT_ADJACENT_CITIES = 6;
	private static final int PRINT_DISEASES = 7;
	private static final int REMOVE = 8;
	private static final int TRADE_CARD = 9;
	private static final int PASS = 10;
	private static final int CURE_DISEASE = 11;
	private static final int BUILD_RESEARCH = 12;
	private static final int DIRECT_MOVE = 13;
	private static final int CHARTER_MOVE = 14;
	private static final int SHUTTLE_MOVE = 15;
	private static final int PRINT_STATIONS = 16;
	private static final int PRINT_HANDS = 17;
	private static final int COMMUNICATE  = 18;
	
	
	/***Functions for user commands***/
	//Get the users input and translate it to the constants.  Could do lots more 
	//error handling here.
	private static int processUserInput(String inputString) {
		inputString = inputString.toLowerCase().split(" ")[0];
		if (inputString.compareTo("quit") == 0)
			return QUIT;
		else if (inputString.compareTo("location") == 0)
			return PRINT_LOCATION;
		else if (inputString.compareTo("move") == 0)
			return MOVE;
		else if ((inputString.compareTo("actions") == 0) ||
				 (inputString.compareTo("help") == 0))
			return PRINT_ACTIONS;
		else if (inputString.compareTo("cities") == 0)
			return PRINT_CITIES;
		else if (inputString.compareTo("connections") == 0)
			return PRINT_CONNECTIONS;
		else if (inputString.compareTo("adjacent") == 0)
			return PRINT_ADJACENT_CITIES;
		else if (inputString.compareTo("infections") == 0)
			return PRINT_DISEASES;
		else if (inputString.compareTo("remove") == 0)
			return REMOVE;
		else if (inputString.compareTo("trade") == 0)
			return TRADE_CARD;
		else if (inputString.compareTo("pass") == 0)
			return PASS;
		else if (inputString.compareTo("cure") == 0)
			return CURE_DISEASE;
		else if (inputString.compareTo("research") == 0)
			return BUILD_RESEARCH;
		else if (inputString.compareTo("direct") == 0)
			return DIRECT_MOVE;
		else if (inputString.compareTo("charter") == 0)
			return CHARTER_MOVE;
		else if (inputString.compareTo("shuttle") == 0)
			return SHUTTLE_MOVE;
		else if (inputString.compareTo("stations") == 0)
			return PRINT_STATIONS;
		else if (inputString.compareTo("hands") == 0)
			return PRINT_HANDS;
		else if (inputString.compareTo("communicate") == 0)
			return PRINT_STATIONS;
		else 
			return -1;
	}
	
	private static void printStations() {
		System.out.printf("Research Stations: %s\n", researchStations.size());
		for (String station:researchStations) {
			System.out.println(station);
		}
	}
	
	private static void communicate() {
		System.out.println("Agent go brr");
	}
	
	//Make sure the scanner is open, then get the user input and make sure it's reasonable.
	//Return the integer command.
	private static int getUserInput() {
		boolean gotReasonableInput = false;
		int processedUserInput = -1;

		//Open up the scanner if it's not already open.
		if (!shellOpen) {
			try {
				shellInput = new Scanner(System. in);
				shellOpen = true;
			}
			catch(Exception e) {
				System.out.printf("Error reading user input: %s", e);
				e.printStackTrace();
			}
		}
		//loop until the user types in a command that is named.  It may not be a valid move.
		while (!gotReasonableInput) {
			System.out.printf("[%s]: ", userNames[currentUser]);
			String userInput = shellInput.nextLine();
			System.out.println("The user typed:"+ userInput);
			//Translate the user's input to an integer.
			processedUserInput = processUserInput(userInput); 						
			if (processedUserInput >= 0)
				gotReasonableInput = true;
			else
				System.out.println(userInput + "is not a good command. Try 'actions'.");				
		}		
		return processedUserInput;
	}

	//print out the integer associated with what the user typed.
	private static void echoUserInput(int userInput) {
		System.out.println("The user chose:"+ userInput);
	}
		
	//Print out the cities adjacent to the userLocation
	private static void printAdjacentCities () {
		for (int cityNumber = 0; cityNumber < numberCities; cityNumber++) {
			if (citiesAdjacent(userLocation[currentUser],cityNumber)) {
				System.out.println(cities[cityNumber]);
			}
		}
	}
	
	//Print out all possible user actions.
	private static void printActions() {
		System.out.println ("Type in on the terminal with the following followed by no spaces finish with return.");
		System.out.println ("quit");
		System.out.println ("location");
		System.out.println ("move");
		System.out.println ("actions/help");
		System.out.println ("cities");
		System.out.println ("connections");
		System.out.println ("adjacent");
		System.out.println ("infections");
		System.out.println ("remove");
		System.out.println("trade");
		System.out.println("pass");
		System.out.println("cure");
		System.out.println("research");
		System.out.println("direct");
		System.out.println("shuttle");
		System.out.println("charter");
		System.out.println("stations");
		System.out.println("hands");
		System.out.println("communicate");
	}

	//Print out all the users' locations.
	private static void printUserLocations() {
		System.out.println("The current user is " + userNames[currentUser]);
		for (int userNumber = 0; userNumber<NUMBER_USERS;userNumber++) {
			int printUserLocation = userLocation[userNumber];
			
			System.out.println (userNames[userNumber] + " is in " + cities[printUserLocation]);
		}
	}
		
	private static boolean tradeCard() {
		if (userLocation[0] == userLocation[1]) {
			printHands();
			System.out.println("Choose user one card to trade");
			System.out.println("Machine's consent not apply, for now...");
			String userInput;
			userInput = shellInput.nextLine().toLowerCase();
			for (String cardOne:userOneHand) {
				if (userInput.compareTo(cardOne.toLowerCase()) == 0){
					System.out.println("Choose user two card to trade");
					printHands();
					userInput = shellInput.nextLine().toLowerCase();
					for (String cardTwo:userTwoHand) {
						if (userInput.compareTo(cardTwo.toLowerCase()) == 0) {
							userOneHand.remove(cardOne);
							userOneHand.add(cardTwo);
							userTwoHand.remove(cardTwo);
							userTwoHand.add(cardOne);
							System.out.println("Traded user one "+cardOne+"for user two"+cardTwo);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private static boolean cureDisease() {
		return true;
	}
	
	private static boolean buildResearch() {
		return true;
	}
	
	private static boolean directMove() {
		return true;
	}
	
	private static boolean charterMove() {
		return true;
	}
	
	private static boolean shuttleMove() {
		return true;
	}
	
	private static void printHands() {
		System.out.println("User one hand:");
		for(String card:userOneHand) {
			System.out.println(card);
		}
		System.out.println("User two hand:");
		for(String card:userTwoHand) {
			System.out.println(card);
		}
	}
	
	//Handle the user's commands.
	private static boolean processUserCommand(int userInput) {
		echoUserInput(userInput);
		
		if (userInput == QUIT) 
			return true;
		else if (userInput == PRINT_LOCATION)
			printUserLocations();
		else if (userInput == MOVE) {
			moveUser();
			actionDone();
		} else if (userInput == PRINT_ACTIONS)
			printActions();
		else if (userInput == PRINT_CITIES)
			printCities();
		else if (userInput == PRINT_CONNECTIONS)
			printConnections();
		else if (userInput == PRINT_ADJACENT_CITIES)
			printAdjacentCities();
		else if (userInput == PRINT_DISEASES)
			printInfectedCities();
		else if (userInput == REMOVE) {
			if (removeCube()) actionDone();
		} else if (userInput == TRADE_CARD) {
			if (tradeCard()) actionDone();
		} else if (userInput == PASS)
			actionDone();
		else if (userInput == CURE_DISEASE) {
			if (cureDisease()) actionDone();
		} else if (userInput == BUILD_RESEARCH) {
			if (buildResearch()) actionDone();
		} else if (userInput == DIRECT_MOVE) {
			if (directMove()) actionDone();
		} else if (userInput == CHARTER_MOVE) {
			if (charterMove()) actionDone();
		} else if (userInput == SHUTTLE_MOVE) {
			if (shuttleMove()) actionDone();
		}
		else if (userInput == PRINT_STATIONS)
			printStations();
		else if (userInput == PRINT_HANDS)
			printHands();
		else if (userInput == COMMUNICATE)
			communicate();
		return false;
	}
	
	/*** Action Functions ***/
	//Ask the user where to move, get the city, and if valid, move the user's location.
	private static void moveUser() {
		boolean moved = false;
		while (!moved) {
			System.out.println ("Type where you'd like to move.");
			String userInput = shellInput.nextLine();
			int cityToMoveTo = getCityOffset(userInput);
		
			if (cityToMoveTo == -1) {
				System.out.println(userInput + " is not a valid city. Try one of these.");
				printAdjacentCities();
			}
		
			//If adjacent move the user, if not print an error.
			else if (citiesAdjacent(userLocation[currentUser],cityToMoveTo)) {
				System.out.println("The user has moved from " +
					cities[userLocation[currentUser]] + " to " + 
					cities[cityToMoveTo] + ".");
				userLocation[currentUser] = cityToMoveTo;
				moved = true;
			}
			else {
				System.out.println ("You can't move to " + userInput + ".  Try one of these.");
				printAdjacentCities();
				}
		}
		
	}
	
	private static boolean isDiseaseCured(int currentUserLocation) {
		String userCity = cities[currentUserLocation];
		if (blueDiseaseCure) {
			for (String city:blueCities) {
				if (userCity == city)
					return true;
			}
		}
		if (yellowDiseaseCure) {
			for (String city:yellowCities) {
				if (userCity == city)
					return true;
			}
		}	
		if (redDiseaseCure) {
			for (String city:redCities) {
				if (userCity == city)
					return true;
			}
		}
		if (blackDiseaseCure) {
			for (String city:blackCities) {
				if (userCity == city)
					return true;
			}	
		}
		return false;
	}
	
	//Remove a cube from the current location.  If there's not, return false for an error.
	private static boolean removeCube() {
		int currentUserLocation = userLocation[currentUser];
		if (diseaseCubes[currentUserLocation] > 0) 
			{
			if (isDiseaseCured(currentUserLocation)) {
				diseaseCubes[currentUserLocation] = 0;
			} else {
				diseaseCubes[currentUserLocation]--;
			}
			System.out.println("There are " + diseaseCubes[currentUserLocation] + " left");
			return true;
			}
		else {
			System.out.println("The space you're on has no disease cubes.");
			return false;
		}
	}
	
	private static void actionDone() {
		turnsLeft--;
		System.out.println(userNames[currentUser]+" turns left "+turnsLeft);
		if (turnsLeft == 0) {
			currentUser++;
			currentUser%=NUMBER_USERS;
			System.out.println("It's now " + userNames[currentUser] + " turn.");
			turnsLeft = 4;
		}
	}
	
	/***Code for the city graph ***/
	//Read the specified number of cities.  If it crashes, it should throw to the calling catch.
	private static void readCities(int numCities, Scanner scanner) {
		//A simple loop reading cities in.  It assumes the file is text with the last character 
		//of the line being the last letter of the city name.
		for (int cityNumber = 0; cityNumber < numCities; cityNumber++) {
			String cityName = scanner.nextLine();
			cities[cityNumber] = cityName;
		}
	}
		
	//Print out the list of all the cities.
	private static void printCities() {
		System.out.println(numberCities + " Cities.");
		for (int cityNumber = 0; cityNumber < numberCities; cityNumber++) {
			System.out.println(cities[cityNumber]);
		}
	}
	
	//Loop through the city array, and return the offset of the cityName parameter in that
	//array.  Return -1 if the cityName is not in the array.
	private static int getCityOffset(String cityName) {
		cityName = cityName.toLowerCase();
		for (int cityNumber = 0; cityNumber < numberCities; cityNumber++) {
			if (cityName.compareTo(cities[cityNumber].toLowerCase()) == 0) 
				return cityNumber;
		}
		return -1;
	}

	//Look through the connections and see if the city numbers are in them.  If
	//Return whether they are in the list.
	private static boolean citiesAdjacent(int city1,int city2) {
		for (int compareConnection = 0; compareConnection < numberConnections; compareConnection ++) {
			if ((connections[0][compareConnection] == city1) &&
				(connections[1][compareConnection] == city2))
				return true;
			//Need to check both ways A to B and B to A as only one connection is stored.
			else if ((connections[0][compareConnection] == city2) &&
					(connections[1][compareConnection] == city1))
					return true;		
		}
		return false;
	}

	//Read the specified number of connections.  If it crashes, it should throw to the calling catch.
	private static void readConnections(int numConnections, Scanner scanner) {
		//A simple loop reading connections in.  It assumes the file is text with the last 
		//character of the line being the last letter of the city name.  The two 
		//cities are separated by a ; with no spaces
		for (int connectionNumber = 0; connectionNumber < numConnections; connectionNumber++) {
			String connectionName = scanner.nextLine();
			String cityName[] = connectionName.split(";");
			int firstCityOffset = getCityOffset(cityName[0]);
			int secondCityOffset = getCityOffset(cityName[1]);
			connections[0][connectionNumber] = firstCityOffset;
			connections[1][connectionNumber] = secondCityOffset;
		}
	}		
	
	//Print out the full list of connections.
	private static void printConnections( ) {
		System.out.println(numberConnections + " Connections.");
		for (int connectionNumber = 0; connectionNumber < numberConnections; connectionNumber++) {
			String firstCity = cities[connections[0][connectionNumber]];
			String secondCity = cities[connections[1][connectionNumber]];
			System.out.println(firstCity + " " + secondCity);
		}
	}
			
	//Open the city file, allocate the space for the cities, and connections, then read the 
	//cities, and then read the connections.  It uses those class variables.
	private static void readCityGraph() {

		//Open the file and read it.  
		try {
		      File fileHandle = new File(cityMapFileName);
		      Scanner mapFileReader = new Scanner(fileHandle);

		      //read the number of cities and allocate variables.
		      numberCities = mapFileReader.nextInt();
		      String data = mapFileReader.nextLine();  //read the rest of the line after the int
		      cities = new String[numberCities]; //allocate the cities array
		      diseaseCubes = new int[numberCities];
		      
		      //read the number of connections and allocate variables.
		      numberConnections = mapFileReader.nextInt();
		      data = mapFileReader.nextLine();  //read the rest of the line after the int
		      connections = new int[2][numberConnections];

		      //read cities
		      readCities(numberCities,mapFileReader);
		      //readConnections 
		      readConnections(numberConnections,mapFileReader);
		      
		      mapFileReader.close();
		    } 
		 
		 catch (FileNotFoundException e) {
		      System.out.println("An error occurred reading the city graph.");
		      e.printStackTrace();
		    }
	}
	
	//A stub for now just to put some disease cubes on the board.  Do it properly later.

	private static void infectCities() {
		// spread 3 infections, 3 cubes, 2 cubes and 1 cube
		// effecting 3 cities each
		int nTarget = 0;
		String target;
		
		for (int cubes=3; cubes<=1; cubes--) {
			for (int wave=0; wave<3; wave++) {
				// draw from infection deck
				target = infectionDeck.get(-1);
				infectionDeck.remove(-1);
				
				// look for matching city number to apply infection
				for (int city=0; city<lenCities; city++) {
					if (cities[city].compareTo(target) == 0) {
						nTarget = city;
					}
				}
				
				// apply according number of cubes
				diseaseCubes[nTarget] = cubes;
			}
		}
	}
	
	private static boolean spreadInfection() {
		return false;
	}

	private static void printInfectedCities() {
		for (int cityNumber = 0;  cityNumber < numberCities; cityNumber ++) {
			if (diseaseCubes[cityNumber] > 0) {
				System.out.println(cities[cityNumber] + " has " + diseaseCubes[cityNumber] + " cubes.");
			}
		}
	}
	
	private static void initCities() {
		blueCities = Arrays.copyOfRange(cities,0,11);
		yellowCities = Arrays.copyOfRange(cities,12,23);
		redCities = Arrays.copyOfRange(cities,24,35);
		blackCities = Arrays.copyOfRange(cities,36,47);
	}
	
	private static boolean playerDraw() {
		return false;
	}
	
	private static boolean checkWin() {
		if (blueDiseaseCure && yellowDiseaseCure && redDiseaseCure && blackDiseaseCure) {
			return true;
		} else {
			return false;
		}	
	}
	
	private static boolean checkLoss() {
		if (blueCubesLeft == 0 || redCubesLeft == 0 || yellowCubesLeft == 0 ||
				blackCubesLeft == 0) {
			return true;
		} else if (outbreaks == outbreaksMax){
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean playerTurn() {
		int turnsLeft = 4;
		int userInput;
		
		while (turnsLeft != 0) {
			userInput = getUserInput();
			// QUIT returns true else false
			if (processUserCommand(userInput))
				return true;
			turnsLeft--;
			if (checkWin())
				return true;
			if (checkLoss())
				return true;
		}
		if (playerDraw())
			return true;
		return false;
	}
	
	private static String[] shuffle(String[] array) {	
		// rebuilt array in random order
		for (int i = 0; i < array.length; i++) {
			int randomIndexToSwap = randomGenerator.nextInt(array.length);
			String temp = array[randomIndexToSwap];
			array[randomIndexToSwap] = array[i];
			array[i] = temp;
		}
		return array;
	}
	
	private static void initDeck() {
		// shuffle player deck
		String[] citiesShuffled = shuffle(Arrays.copyOfRange(cities,0,lenCities)); 
		
		// deal 4 cards to each player
		for (int card=0; card<4; card++) {
			userDeckCardsRemoved++;
			userOneHand.add(citiesShuffled[lenCities-userDeckCardsRemoved]);
			citiesShuffled = Arrays.copyOfRange(citiesShuffled,0,lenCities-userDeckCardsRemoved);
		}
		for (int card=0; card<4; card++) {
			userDeckCardsRemoved++;
			userTwoHand.add(citiesShuffled[lenCities-userDeckCardsRemoved]);
			citiesShuffled = Arrays.copyOfRange(citiesShuffled,0,lenCities-userDeckCardsRemoved);
		}
		// remove dealt cards from player deck
		// -1 for last el as starts from 0 -8 for 2x4 cards
		userDeckSize = userDeckSize - 8;
		citiesShuffled = Arrays.copyOfRange(citiesShuffled, 0, userDeckSize-1);
		
		// split into 5 piles and add an epidemic card to each
		String epidemicCard = "Epidemic";
		// 8 city cards + 1 epidemic card
		// we copy 1 repeated and replace my epidemic card
		String[] pileOne = Arrays.copyOfRange(cities,0,9); // 8 + epi
		String[] pileTwo = Arrays.copyOfRange(cities,8,17); // 8 + epi
		String[] pileThree = Arrays.copyOfRange(cities,16,25); // 8 + epi
		String[] pileFour = Arrays.copyOfRange(cities,24,33); // 8 + epi
		String[] pileFive = Arrays.copyOfRange(cities,31,40); // epi + 8
		
		// add epidemic card
		pileOne[8] = epidemicCard;
		pileTwo[8] = epidemicCard;
		pileThree[8] = epidemicCard;
		pileFour[8] = epidemicCard;
		pileFive[0] = epidemicCard;
		
		// shuffle piles
		pileOne = shuffle(pileOne);
		pileTwo = shuffle(pileOne);
		pileThree = shuffle(pileThree);
		pileFour = shuffle(pileFour);
		pileFive = shuffle(pileFour);

		// build player deck back from the 5 piles 
		for (int pile = 1; pile <= 5; pile++) {
			if (pile == 1) {
				for (int card=0; card<9;card++) {
					userDeck.add(pileOne[card]);
				}
			} else if (pile == 2) {
				for (int card=0; card<9;card++) {
					userDeck.add(pileTwo[card]);
				}
			} else if (pile == 3) {
				for (int card=0; card<9;card++) {
					userDeck.add(pileThree[card]);
				}
				
			} else if (pile == 4) {
				for (int card=0; card<9;card++) {
					userDeck.add(pileFour[card]);
				}
			} else { // pile == 5
				for (int card=0; card<9;card++) {
					userDeck.add(pileFive[card]);
				}
			}
		}
		
		// shuffle infection deck
		String[] infectionPile = shuffle(cities);
		for (int card=0; card<infectionDeckSize; card++) {
			infectionDeck.add(infectionPile[card]);
		}
	}
	
	private static void initResearchStations() {
		researchStations.add("Atlanta");
	}

	//The main function of the program.  Enter and exit from here.
	//It is a simple getInput processInput loop until the game is over.  
	public static void main(String[] args) {
		boolean gameDone = false;

		System.out.println("Welcome to the Pandemic. Please don't be rational.");
		
		readCityGraph();
		initCities();
		initResearchStations();
		initDeck();
		infectCities();

		while (!gameDone) {
			gameDone = playerTurn();
			
		}
		
		if (checkWin()) {
			System.out.println("'gz u won u nerd, go touch grass now' - (ur mom)");
		} else if (checkLoss() || playerDraw()){
			System.out.println("'while u ve been taking L's, i ve been popping shells - (yung innanet)'");
		} else {
			System.out.println("quitters dont go far, kid, full send or go home cry");
		}
	}
}
