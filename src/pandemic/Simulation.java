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
	//private static ArrayList<String> infectedCities = new ArrayList<String>(lenCities);;
	private static int[] diseaseCubes; //Number of disease cubes in the associated city.
	private static ArrayList<String> blueDisease = new ArrayList<String>(lenCities);
	private static ArrayList<String> yellowDisease = new ArrayList<String>(lenCities);
	private static ArrayList<String> redDisease = new ArrayList<String>(lenCities);
	private static ArrayList<String> blackDisease = new ArrayList<String>(lenCities);
	private static ArrayList<Integer> blueDiseaseCubes = new ArrayList<Integer>(lenCities);
	private static ArrayList<Integer> yellowDiseaseCubes = new ArrayList<Integer>(lenCities);;
	private static ArrayList<Integer> redDiseaseCubes = new ArrayList<Integer>(lenCities);;
	private static ArrayList<Integer> blackDiseaseCubes = new ArrayList<Integer>(lenCities);;
	private static  int[][] connections; //The connections via offset in the cities array.
	private static int[] userLocation = {0,0};  //These are the users' location that can change.
	
	// gay cards
	private static int epidemicCards = 5;
	private static int userDeckSize = 48 + epidemicCards;
	private static int infectionDeckSize = 48;
	private static int infectionDeckRemoved = 0;
	private static ArrayList<String> userDeck = new ArrayList<String>(userDeckSize);
	private static ArrayList<String> infectionDeck = new ArrayList<String>(infectionDeckSize);
	
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
	private static boolean[] cures = {false, false, false, false};
	private static int maxStations = 6;
	//private static int curesDiscovered = 0;
	
	// Outbreak meter
	private static int outbreaks = 0;
	private static int outbreaksMax = 8;

	//##Change this to your path.##
	private static final String cityMapFileName= "./data/fullMap.txt";
	private static final String rulesFilepath = "./data/rules.txt";
	
	// Players configs
	private static final int NUMBER_USERS = 2;
	private static final String[] userNames = {"Human","The Machine"};
	
	// Player turn
	private static int turnsLeft = 4;
	private static int handLimit = 7;
	private static int totalTurns = 4;
	private static int numberCardsDraw = 2;
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
	private static final int PRINT_COLOUR = 18;
	private static final int COMMUNICATE  = 19;
	private static ArrayList<String> agentMemory = new ArrayList<String>(totalTurns);
	
	
	
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
		else if (inputString.compareTo("colour") == 0)
			return PRINT_COLOUR;
		else if (inputString.compareTo("communicate") == 0)
			return COMMUNICATE;
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
		if (agentMemory.size() <= 4) {
			System.out.println("["+userNames[1]+"]: give me a command to save to memory");
			try {
				String userInput = shellInput.nextLine();
				String action = userInput.split(" ")[0];
				String city = null;
				if (action.compareTo("move") == 0) {
					city = userInput.split(" ")[1];
					agentMemory.add(action+" "+city);
				} else if (userInput.compareTo("remove") == 0) {
					agentMemory.add(action);
				} else if (userInput.compareTo("trade") == 0) {
					agentMemory.add(action);
				} else {
					System.out.println("Invalid command");
				}
			} catch(Exception e) {
				System.out.printf("Error reading user input: %s", e);
				e.printStackTrace();
				System.out.println("Invalid command");
			}
		}		
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
		System.out.println("colour");
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
			System.out.println("");
			printHands();
			System.out.println("\nChoose user one card to trade");
			System.out.println("Machine's consent not apply, for now...");
			String userInput;
			userInput = shellInput.nextLine().toLowerCase();
			for (String cardOne:userOneHand) {
				if (userInput.compareTo(cardOne.toLowerCase()) == 0){
					System.out.println("\nChoose user two card to trade");
					printHands();
					userInput = shellInput.nextLine().toLowerCase();
					for (String cardTwo:userTwoHand) {
						if (userInput.compareTo(cardTwo.toLowerCase()) == 0) {
							userOneHand.remove(cardOne);
							userOneHand.add(cardTwo);
							userTwoHand.remove(cardTwo);
							userTwoHand.add(cardOne);
							System.out.println("Traded user one "+cardOne+" for user two "+cardTwo);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private static int getCityColour(String localCity) {
		for (String city:blueCities) {
			if (localCity.compareTo(city) == 0) {
				return 0;
			}
		}
		for (String city:yellowCities) {
			if (localCity.compareTo(city) == 0) {
				return 1;
			}
		}
		for (String city:redCities) {
			if (localCity.compareTo(city) == 0) {
				return 2;
			}
		}
		for (String city:blackCities) {
			if (localCity.compareTo(city) == 0) {
				return 3;
			}
		}
		return -1;
	}
	
	private static String getCureColour(int cure) {
		if (cure == 0)
			return "blue";
		else if (cure == 1)
			return "yellow";
		else if (cure == 2)
			return "red";
		else if (cure == 3)
			return "black";
		else
			return "";
		
	}
	
	private static boolean cureDisease() {
		String localCity = cities[userLocation[currentUser]];
		int colour = getCityColour(localCity);
		int cardCount = 0;
		ArrayList<String> playerHand = new ArrayList<String>(handLimit);
		if (currentUser == 0) {
			playerHand = userOneHand;
		} else {
			playerHand = userTwoHand;
		}
		ArrayList<Integer> removeCards = new ArrayList<Integer>(5);
		int nCard = 0;
		
		boolean researchStationFlag = false;
		for (String city:researchStations) {
			if (city.compareTo(localCity) == 0) {
				researchStationFlag = true;
			}
		}
		if (!researchStationFlag) {
			System.out.println("First you need to build a research station in "+localCity);
			return false;
		}
		for(String card:playerHand) {
			if (getCityColour(card) == colour) {
				 cardCount++;
				 removeCards.add(nCard);
			}
				nCard++;
		}
		if (cardCount == 5) {
			cures[colour] = true;
			for(int card:removeCards) {
				playerHand.remove(card);
			}
			System.out.println("Cured "+getCureColour(colour)+ " disease");
			return true;
		}
		System.out.println("User "+userNames[currentUser]+" needs a total of 5 same colour cards to cure a disease in this city");
		return false;
	}
	
	private static boolean buildResearch() {
		String localCity = cities[userLocation[currentUser]];
		ArrayList<String> playerHand = new ArrayList<String>(handLimit);
		int userInput;
		if (currentUser == 0) {
			playerHand = userOneHand;
		} else {
			playerHand = userTwoHand;
		}
		for (String card:playerHand) {
			if (localCity.compareTo(card) == 0) {
				if (researchStations.size() == maxStations) {
					System.out.println("Limit of research stations reached");
					System.out.println("Enter number 0-5 to replace reseach station");
					int i = 0;
					for(String station:researchStations) {
						System.out.println(i+" : "+station);
					}
					userInput = shellInput.nextInt();
					researchStations.remove(userInput);
					researchStations.add(localCity);
					System.out.println("Research stations build in "+localCity);
					return true;
				} else {
					researchStations.add(localCity);
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean directMove() {
		int userInput;
		int nCard = 0;
		String flyCity;
		ArrayList<String> playerHand = new ArrayList<String>(handLimit);
		
		if (currentUser == 0) {
			playerHand = userOneHand;
		} else {
			playerHand = userTwoHand;
		}
		
		System.out.println("Choose a city to direct fly to");
		for(String card:playerHand) {
			System.out.println(nCard+" : "+card);
		}
		userInput = shellInput.nextInt();
		flyCity = playerHand.get(userInput);
		if (flyCity.compareTo("Epidemic") == 0 ) {
			System.out.println("Invalid option");
			return false;
		}
		boolean cardExistsFlag = false;
		for (String card:playerHand) {
			if (flyCity.compareTo(card) == 0) {
				cardExistsFlag = true;
			}
		}
		if (!cardExistsFlag) {
			System.out.println("Invalid option");
		}
		
		for (int city=0; city<lenCities;city++) {
			if (cities[city].compareTo(flyCity) == 0) {
				playerHand.remove(userInput);
				if (currentUser == 0) {
					userOneHand = playerHand;
				} else {
					userTwoHand = playerHand;
				}
				System.out.println(userNames[currentUser]+" moved from "+userLocation[currentUser]+" to "+cities[city]);
				userLocation[currentUser] = city;
				return true;
			}
		}
		return false;
	}
	
	private static boolean charterMove() {
		ArrayList<String> playerHand = new ArrayList<String>(handLimit);
		String localCity = cities[userLocation[currentUser]];
		String userInput;
		int nCard = 0;
		if (currentUser == 0) {
			playerHand = userOneHand;
		} else {
			playerHand = userTwoHand;
		}
		
		for(String card: playerHand) {
			if (localCity.compareTo(card) == 0) {
				for(String city:cities) {
					System.out.println(city);
				}
				System.out.println("Choose city to move to 0-47");
				userInput = shellInput.nextLine();
				
				for(int nCity=0; nCity<lenCities; nCity++) {
					if (userInput.compareTo(cities[nCity]) == 0) {
						playerHand.remove(nCard);
						if (currentUser == 0) {
							userOneHand = playerHand;
						} else {
							userTwoHand = playerHand;
						}
						System.out.println(userNames[currentUser]+" moved from "+cities[userLocation[currentUser]]+" to "+cities[nCity]);
						userLocation[currentUser] = nCity;
					}
				}
			nCard++;
			}
			return false;
		}
		return true;
	}
	
	private static boolean shuttleMove() {
		String userInput = null;
		if (researchStations.size() > 1) {
			for(String station:researchStations) {
				System.out.println(station);
			}
			System.out.println("Choose city to move to");
			userInput = shellInput.nextLine();
		}
		for(String station:researchStations) {
			if (userInput.compareTo(station) == 0) {
				for (int city=0; city<lenCities; city++) {
					if (cities[city].compareTo(station) == 0) {
						System.out.println(userNames[currentUser]+" move from "+userLocation[currentUser]+" to "+station);
						userLocation[currentUser] = city;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static void printHands() {
		System.out.println("User one at "+ cities[userLocation[0]] +":");
		for(String card:userOneHand) {
			System.out.println(card);
		}
		System.out.println("User two at "+ cities[userLocation[1]] +":");
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
			if (removeCube(false)) actionDone();
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
		else if (userInput == PRINT_COLOUR)
			printColour();
		else if (userInput == COMMUNICATE)
			communicate();
		return false;
	}
	
	private static void printColour() {
		String city = cities[userLocation[currentUser]];
		for(String blueCity:blueCities) {
			if (city.compareTo(blueCity) == 0) {
				System.out.println(city+" has colour blue");
			}
		}
		for(String yellowCity:yellowCities) {
			if (city.compareTo(yellowCity) == 0) {
				System.out.println(city+" has colour yellow");
			}
		}
		for(String redCity:redCities) {
			if (city.compareTo(redCity) == 0) {
				System.out.println(city+" has colour red");
			}
		}
		for(String blackCity:blackCities) {
			if (city.compareTo(blackCity) == 0) {
				System.out.println(city+" has colour black");
			}
		}
	}
	
	/*** Action Functions ***/
	//Ask the user where to move, get the city, and if valid, move the user's location.
	private static void moveUser() {
		boolean moved = false;
		printAdjacentCities();
		//printAdjacentCities();
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
		if (cures[0]) {
			for (String city:blueCities) {
				if (userCity == city)
					return true;
			}
		}
		if (cures[1]) {
			for (String city:yellowCities) {
				if (userCity == city)
					return true;
			}
		}	
		if (cures[2]) {
			for (String city:redCities) {
				if (userCity == city)
					return true;
			}
		}
		if (cures[3]) {
			for (String city:blackCities) {
				if (userCity == city)
					return true;
			}	
		}
		return false;
	}
	
	//Remove a cube from the current location.  If there's not, return false for an error.
	private static boolean removeCube(boolean silent) {
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
			if (!silent)
				System.out.println("The space you're on has no disease cubes.");
			return false;
		}
	}
	
	private static void actionDone() {
		turnsLeft--;
		System.out.println(userNames[currentUser]+" turns left "+turnsLeft);
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
        System.out.println("\nBlue cities");
		for (int cityNumber = 0; cityNumber < blueCities.length; cityNumber++){
            System.out.println(blueCities[cityNumber]);
        }
        System.out.println("\nYellow cities");
		for (int cityNumber = 0; cityNumber < yellowCities.length; cityNumber++){
            System.out.println(yellowCities[cityNumber]);
        }
        System.out.println("\nRed cities");
		for (int cityNumber = 0; cityNumber < redCities.length; cityNumber++){
            System.out.println(redCities[cityNumber]);
        }
        System.out.println("\nBlack cities");
		for (int cityNumber = 0; cityNumber < blackCities.length; cityNumber++){
            System.out.println(blackCities[cityNumber]);
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
	
	// get city root disease colour as in deck
	private static int getColour(String city) {
		for(String blueCity:blueCities) {
			if (city.compareTo(blueCity) == 0) {
				return 0;
			}
		}
		for(String yellowCity:yellowCities) {
			if (city.compareTo(yellowCity) == 0) {
				return 1;
			}
		}
		for(String redCity:redCities) {
			if (city.compareTo(redCity) == 0) {
				return 2;
			}
		}
		for(String blackCity:blackCities) {
			if (city.compareTo(blackCity) == 0) {
				return 3;
			}
		}
		return -1;
	}
	
	// draws 3 waves of cards and infects them with 3 to 1 cubes as in game rules
	private static void infectCities() {
		// spread 3 infections, 3 cubes, 2 cubes and 1 cube
		// effecting 3 cities each
		int targetColour = 0;
		String target;
		
		for (int cubes=3; cubes>0; cubes--) {
			for (int wave=0; wave<3; wave++) {
				// draw from infection deck
				infectionDeckRemoved++;
				target = infectionDeck.get(infectionDeckSize-infectionDeckRemoved);
				//infectionDeck.remove(infectionDeckSize-infectionDeckRemoved);
				targetColour = getColour(target);
				if (targetColour == 0) {
					blueCubesLeft = blueCubesLeft - cubes;
					blueDisease.add(target);
					blueDiseaseCubes.add(cubes);
				} else if (targetColour == 1) {
					yellowCubesLeft = yellowCubesLeft - cubes;
					yellowDisease.add(target);
					yellowDiseaseCubes.add(cubes);
				}else if (targetColour == 2) {
					redCubesLeft = redCubesLeft - cubes;
					redDisease.add(target);
					redDiseaseCubes.add(cubes);
				} else {
					blackCubesLeft = blackCubesLeft - cubes;
					blackDisease.add(target);
					blackDiseaseCubes.add(cubes);
				}
			}
		}
	}
	
	// find epidemicCity in city array
	private static int findCity(String epidemicCity) {
		int nCity = 0;
		for (String city:cities) {
			if (city.compareTo(epidemicCity) == 0) {
				return nCity;
			}
			nCity++;
		}
		return -1;
	}
	
	// spread epidemic through adjacent cities, recurses if 4 same colour cubes are now in one of the cities
	// increases outbreak meter
	private static boolean epidemic(String epidemicCity) {
		System.out.println("Epidemic outbreak is spreading");
		outbreaks++;
		int nEpidemicCity = findCity(epidemicCity);
		int colour = getColour(epidemicCity);
		int cubes = 0;
		for (int nCity=0;nCity<lenCities;nCity++) {
			if (citiesAdjacent(nEpidemicCity, nCity)) {
				// find cities[nCity] in each disease array and increase cubes by 1
				// now sleep sleep
				if (colour == 0) {
					for (int i=0;i<blueDisease.size();i++) {
						if (cities[nCity].compareTo(blueDisease.get(i)) == 0) {
							blueDisease.remove(i);
							blueDisease.add(cities[nCity]);
							cubes = blueDiseaseCubes.get(i) + 1;
							if (cubes > 3) {
								cubes = 3;
								blueCubesLeft--;
								blueDiseaseCubes.remove(i);
								blueDiseaseCubes.add(cubes);
								if (checkLoss())
									return true;
								epidemic(cities[nCity]);
							} else {
								blueCubesLeft--;
								blueDiseaseCubes.remove(i);
								blueDiseaseCubes.add(cubes);
							}

						} else {
							blueDisease.add(cities[nCity]);
							blueDiseaseCubes.add(1);
							blueCubesLeft--;
						}
					}
					cubes = 0;
				} else if (colour == 1) {
					for (int i=0;i<yellowDisease.size();i++) {
						if (cities[nCity].compareTo(yellowDisease.get(i)) == 0) {
							yellowDisease.remove(i);
							yellowDisease.add(cities[nCity]);
							cubes = yellowDiseaseCubes.get(i) + 1;
							if (cubes > 3) {
								cubes = 3;
								yellowCubesLeft--;
								yellowDiseaseCubes.remove(i);
								yellowDiseaseCubes.add(cubes);
								if (checkLoss())
									return true;
								epidemic(cities[nCity]);
							} else {
								yellowCubesLeft--;
								yellowDiseaseCubes.remove(i);
								yellowDiseaseCubes.add(cubes);
							}

						} else {
							yellowDisease.add(cities[nCity]);
							yellowDiseaseCubes.add(1);
							yellowCubesLeft--;
						}
					}
					cubes = 0;
				} else if (colour == 2) {
					for (int i=0;i<redDisease.size();i++) {
						if (cities[nCity].compareTo(redDisease.get(i)) == 0) {
							redDisease.remove(i);
							redDisease.add(cities[nCity]);
							cubes = redDiseaseCubes.get(i) + 1;
							if (cubes > 3) {
								cubes = 3;
								redCubesLeft--;
								redDiseaseCubes.remove(i);
								redDiseaseCubes.add(cubes);
								if (checkLoss())
									return true;
								epidemic(cities[nCity]);
							} else {
								redCubesLeft--;
								redDiseaseCubes.remove(i);
								redDiseaseCubes.add(cubes);
							}

						} else {
							redDisease.add(cities[nCity]);
							redDiseaseCubes.add(1);
							redCubesLeft--;
						}
					}
					cubes = 0;
				} else if (colour == 3) {
					for (int i=0;i<blackDisease.size();i++) {
						if (cities[nCity].compareTo(blackDisease.get(i)) == 0) {
							blackDisease.remove(i);
							blackDisease.add(cities[nCity]);
							cubes = blackDiseaseCubes.get(i) + 1;
							if (cubes > 3) {
								cubes = 3;
								blackCubesLeft--;
								blackDiseaseCubes.remove(i);
								blackDiseaseCubes.add(cubes);
								if (checkLoss())
									return true;
								epidemic(cities[nCity]);
							} else {
								blackCubesLeft--;
								blackDiseaseCubes.remove(i);
								blackDiseaseCubes.add(cubes);
							}

						} else {
							blackDisease.add(cities[nCity]);
							blackDiseaseCubes.add(1);
							blackCubesLeft--;
						}
					}
					cubes = 0;
				}
			}
		}
		return false;
	}
	
	// epidemic card drawn outbreak (draws from infection deck to pick target cities)
	private static boolean spreadInfection() {
		System.out.println("Epidemic card drawn");
		outbreaks++;
		int cubes = 3;
		String target;
		int targetColour;
		int targetCity = 0;
		String tempDisease;
		int tempCube;
		int diff;
		String infectedCity;
		
		for (int wave=0; wave<3; wave++) {
			// draw from infection deck
			target = infectionDeck.get(0);
			infectionDeck.remove(0);
			//infectionDeck.remove(infectionDeckSize-infectionDeckRemoved);
			targetColour = getColour(target);
			targetCity=0;
			if (targetColour == 0) {
				for (int city=0; city<blueDisease.size();city++) {
					infectedCity = blueDisease.get(city);
					if (target.compareTo(infectedCity) == 0) {
						if (blueDiseaseCubes.get(targetCity)+cubes > 3) {
							tempCube = blueDiseaseCubes.get(targetCity);
							tempDisease = blueDisease.get(targetCity);
							diff = tempCube-3;
							blueCubesLeft-=diff;
							blueDisease.remove(targetCity);
							blueDiseaseCubes.remove(targetCity);
							blueDisease.add(tempDisease);
							blueDiseaseCubes.add(3);
							if (epidemic(blueDisease.get(targetCity))) {
								return true;
							}
						}
					}
					targetCity++;
				}
			} else if (targetColour == 1) {
				for (int city=0; city<yellowDisease.size();city++) {
					infectedCity = yellowDisease.get(city);
					if (target.compareTo(infectedCity) == 0) {
						if (yellowDiseaseCubes.get(targetCity)+cubes > 3) {
							tempCube = yellowDiseaseCubes.get(targetCity);
							tempDisease = yellowDisease.get(targetCity);
							diff = tempCube-3;
							yellowCubesLeft-=diff;
							yellowDisease.remove(targetCity);
							yellowDiseaseCubes.remove(targetCity);
							yellowDisease.add(tempDisease);
							yellowDiseaseCubes.add(3);
							if (epidemic(yellowDisease.get(targetCity))) {
								return true;
							}
						}
					}
					targetCity++;
				}
			}else if (targetColour == 2) {
				for (int city=0; city<redDisease.size();city++) {
					infectedCity = redDisease.get(city);
					if (target.compareTo(infectedCity) == 0) {
						if (redDiseaseCubes.get(targetCity)+cubes > 3) {
							tempCube = redDiseaseCubes.get(targetCity);
							tempDisease = redDisease.get(targetCity);
							diff = tempCube-3;
							redCubesLeft-=diff;
							redDisease.remove(targetCity);
							redDiseaseCubes.remove(targetCity);
							redDisease.add(tempDisease);
							redDiseaseCubes.add(3);
							if (epidemic(redDisease.get(targetCity))) {
								return true;
							}
						}
					}
					targetCity++;
				}
			} else {
				for (int city=0; city<blackDisease.size();city++) {
					infectedCity = blackDisease.get(city);
					if (target.compareTo(infectedCity) == 0) {
						if (blackDiseaseCubes.get(targetCity)+cubes > 3) {
							tempCube = blackDiseaseCubes.get(targetCity);
							tempDisease = blackDisease.get(targetCity);
							diff = tempCube-3;
							blackCubesLeft-=diff;
							blackDisease.remove(targetCity);
							blackDiseaseCubes.remove(targetCity);
							blackDisease.add(tempDisease);
							blackDiseaseCubes.add(3);
							if (epidemic(blackDisease.get(targetCity))) {
								return true;
							}
						}
					}
					targetCity++;
				}
			}
		}
		String[] tempInfectionDeck = shuffle(cities);
		infectionDeck = new ArrayList<String>(lenCities);
		for (String city:tempInfectionDeck) {
			infectionDeck.add(city);
		}
		infectionDeckRemoved = 1;
		return false;
	}

	private static void printInfectedCities() {
		int i = 0;
		
		System.out.println("Blue infections");
		for(String infected:blueDisease) {
			System.out.println(infected+": " + blueDiseaseCubes.get(i));
			i++;
		}
		
		i = 0;
		System.out.println("Yellow infections");
		for(String infected:yellowDisease) {
			System.out.println(infected+": " + yellowDiseaseCubes.get(i));
			i++;
		}
		
		i = 0;
		System.out.println("Red infections");
		for(String infected:redDisease) {
			System.out.println(infected+": " + redDiseaseCubes.get(i));
			i++;
		}
		
		i = 0;
		System.out.println("Black infections");
		for(String infected:blackDisease) {
			System.out.println(infected+": " + blackDiseaseCubes.get(i));
			i++;
		}
	}
	
	// give disease colour to corresponding cities
	private static void initCities() {
		blueCities = Arrays.copyOfRange(cities,0,12);
		yellowCities = Arrays.copyOfRange(cities,12,24);
		redCities = Arrays.copyOfRange(cities,24,36);
		blackCities = Arrays.copyOfRange(cities,36,48);
	}
	
	private static boolean playerDraw() {
		ArrayList<String> playerHand = new ArrayList<String>(handLimit);
		String userInput = null;
		boolean cardLimitFlag = true;
		
		if (currentUser == 0) {
			playerHand = userOneHand;
		} else {
			playerHand = userTwoHand;
		}
		for(int nCards=0;nCards<numberCardsDraw;nCards++) {
			if (playerHand.size() == 7) {
				cardLimitFlag = true;
				while (cardLimitFlag) {
					System.out.println("\n\nChoose a card to discard "+userNames[currentUser]);
					for (String card:playerHand) {
						System.out.println(card);
					}	
					userInput = shellInput.nextLine();
					
					if (userInput.compareTo("Epidemic") == 0) {
						System.out.println("invalid");
						return false;
					} 
					for (int card=0; card<playerHand.size();card++) {
						if(userInput.compareTo(playerHand.get(card)) == 0){
							playerHand.remove(card);
							cardLimitFlag = false;
						}
					}
				}

			}

			if (userDeck.get(0).compareTo("Epidemic") == 0) {
				 spreadInfection();
				 userDeck.remove(0);
				 userDeckSize--;
			} else {
				playerHand.add(userDeck.get(0));
				userDeck.remove(0);
				userDeckSize--;
			}
		}

//		if (currentUser == 0) {
//			userOneHand = playerHand;
//		} else {
//			userTwoHand = playerHand;
//		}
		return false;
	}
	
	// win when all disease is cured
	private static boolean checkWin() {
		if (cures[0] && cures[1] && cures[2] && cures[3]) {
			return true;
		} else {
			return false;
		}	
	}
	
	// loses when no cubes left for a disease, max outbreaks reached, user deck empty
	private static boolean checkLoss() {
		if (blueCubesLeft < 1 || redCubesLeft <1 || yellowCubesLeft <1 ||
				blackCubesLeft <1) {
			return true;
		} else if (outbreaks >= outbreaksMax){
			return true;
		} else if (userDeckSize < 1) {
			return true;
		} else {
			return false;
		}
	}
	
	// get user command and run action x4
	// ai agent is kind of silly playing compared to a smart human
	private static boolean playerTurn() {
		turnsLeft = 4;
		int userInput;
		
		System.out.println("It's now " + userNames[currentUser] + " turn.");
		if (currentUser == 0) {
			while (turnsLeft > 0) {
				userInput = getUserInput();
				// QUIT returns true else false
				if (processUserCommand(userInput))
					return true;
				if (checkWin())
					return true;
				if (checkLoss())
					return true;
			}
			playerDraw();
			if (checkWin())
				return true;
			if (checkLoss())
				return true;
		} else if (currentUser == 1) {
			boolean loopFlag = false;
			String lastCity = cities[userLocation[currentUser]];
			String prevLastCity = cities[userLocation[currentUser]];
			while (turnsLeft > 0) {
				for (int i=0; i<agentMemory.size();i++) {
					String cmd = agentMemory.get(i);
					if (turnsLeft <= 0)
						break;
					System.out.println(cmd);
					String action = cmd.split(" ")[0];
					if (action.compareTo("move") == 0) {
						String cityMove = cmd.split(" ")[1];
						if (citiesAdjacent(findCity(cityMove), userLocation[currentUser])) {
							lastCity = cities[userLocation[currentUser]];
							userLocation[currentUser] = findCity(cityMove);
							System.out.println("["+userNames[currentUser]+ "]Moved to "+cityMove);
							agentMemory.remove(i);
							turnsLeft--;
						} else {
							System.out.println("["+userNames[currentUser]+ "] Invalid adjacent city");
							agentMemory.remove(i);
						}
					} else if (action.compareTo("trade") == 0) {
						System.out.println("["+userNames[currentUser]+ "] Trading card");
						if (tradeCard()) {
							agentMemory.remove(i);
							turnsLeft--;
						}
					} else if (action.compareTo("remove") == 0) {
						System.out.println("["+userNames[currentUser]+ "]Removing cube from "+cities[userLocation[currentUser]]);
						if (removeCube(true)) {
							System.out.println("Removed cube from "+cities[userLocation[currentUser]]);
							agentMemory.remove(i);
							turnsLeft--;
						}
					}
				}
				if (turnsLeft > 0){
					// find sick city and cure it
					for (int city=0; city<blueDisease.size();city++) {
						if (cities[userLocation[currentUser]].compareTo(blueDisease.get(city)) == 0) {
							if(removeCube(true)) {
								System.out.println("Removed cube from "+cities[userLocation[currentUser]]);
								turnsLeft--;
								break;
							}
						}
					}
				}
				if (turnsLeft > 0){
					// find sick city and cure it
					for (int city=0; city<yellowDisease.size();city++) {
						if (cities[userLocation[currentUser]].compareTo(yellowDisease.get(city)) == 0) {
							System.out.println("Removed cube from "+cities[userLocation[currentUser]]);
							if(removeCube(true)) {
								turnsLeft--;
								break;
							}
						}
					}
				}
				if (turnsLeft > 0){
					// find sick city and cure it
					for (int city=0; city<redDisease.size();city++) {
						if (cities[userLocation[currentUser]].compareTo(redDisease.get(city)) == 0) {
							if(removeCube(true)) {
								System.out.println("Removed cube from "+cities[userLocation[currentUser]]);
								turnsLeft--;
								break;
							}
						}
					}
				}
				if (turnsLeft > 0){
					// find sick city and cure it
					for (int city=0; city<blackDisease.size();city++) {
						if (cities[userLocation[currentUser]].compareTo(blackDisease.get(city)) == 0) {
							if(removeCube(true)) {
								System.out.println("Removed cube from "+cities[userLocation[currentUser]]);
								turnsLeft--;
								break;
							}
						}
					}
				}
				if (turnsLeft > 0) {
					for (String city:shuffle(cities)) {
						if (citiesAdjacent(findCity(city), userLocation[currentUser]) && (lastCity.compareTo(city) != 0) && (prevLastCity.compareTo(city) != 0)) {
								userLocation[currentUser] = findCity(city);
								System.out.println("["+userNames[1]+"] Moved to "+city);
								prevLastCity = lastCity;
								lastCity = city;
								turnsLeft--;
								break;
						} 
					}
					loopFlag = true;
					if (loopFlag) {
						// reset otherwise stuck
						prevLastCity = "Atlanta";
					}
				}
			}
			agentMemory = new ArrayList<String>(totalTurns);
			playerDraw();
			if (checkWin())
				return true;
			if  (checkLoss())
				return true;
		}
			
		currentUser++;
		currentUser%=NUMBER_USERS;
		return false;
	}
	
	// randomly shuffle array
	private static String[] shuffle(String[] array) {	
		// rebuilt array in random order
		String[] newArray = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			int randomIndexToSwap = randomGenerator.nextInt(array.length);
			newArray[i] = array[randomIndexToSwap];
		}
		return newArray;
	}
	
	// shuffle deck according to rules
	private static void initDeck() {
		// shuffle player deck
		String[] citiesShuffled = shuffle(cities); 
		String[] infectionCities = shuffle(cities);
		for (String card:infectionCities) {
			infectionDeck.add(card);
		}
		// deal 4 cards to each player
		for (int card=0; card<4; card++) {
			userDeckSize--;
			userOneHand.add(citiesShuffled[0]);
			citiesShuffled = Arrays.copyOfRange(citiesShuffled,1,lenCities);
		}
		for (int card=0; card<4; card++) {
			userDeckSize--;
			userTwoHand.add(citiesShuffled[0]);
			citiesShuffled = Arrays.copyOfRange(citiesShuffled,1,lenCities);
		}
		// remove dealt cards from player deck
		// -1 for last el as starts from 0 -8 for 2x4 cards
		//citiesShuffled = Arrays.copyOfRange(citiesShuffled, , userDeckSize);
		
		// split into 5 piles and add an epidemic card to each
		String epidemicCard = "Epidemic";
		// 8 city cards + 1 epidemic card
		// we copy 1 repeated and replace my epidemic card
		String[] pileOne = Arrays.copyOfRange(citiesShuffled,0,9); // 8 + epi
		String[] pileTwo = Arrays.copyOfRange(citiesShuffled,8,17); // 8 + epi
		String[] pileThree = Arrays.copyOfRange(citiesShuffled,16,25); // 8 + epi
		String[] pileFour = Arrays.copyOfRange(citiesShuffled,24,33); // 8 + epi
		String[] pileFive = Arrays.copyOfRange(citiesShuffled,31,40); // epi + 8
		
		// add epidemic card
		pileOne[8] = epidemicCard;
		pileTwo[8] = epidemicCard;
		pileThree[8] = epidemicCard;
		pileFour[8] = epidemicCard;
		pileFive[0] = epidemicCard;
		
		// shuffle piles
		pileOne = shuffle(pileOne);
		pileTwo = shuffle(pileTwo);
		pileThree = shuffle(pileThree);
		pileFour = shuffle(pileFour);
		pileFive = shuffle(pileFive);

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
		
		for (int card = 0; card<userDeck.size()-2;card++) {
			for (int p1Card=0; p1Card <4; p1Card++) {
				if (userDeck.get(card).compareTo(userOneHand.get(p1Card)) == 0) {
					userDeck.remove(card);
				}
			}
			for (int p2Card=0; p2Card <4; p2Card++) {
				if (userDeck.get(card).compareTo(userOneHand.get(p2Card)) == 0) {
					userDeck.remove(card);
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
	
	private static void printRules() {
		//Open the file and read it.  
		String data;
		try {
			File fileHandle = new File(rulesFilepath);
		    Scanner fileReader = new Scanner(fileHandle);
			while (fileReader.hasNextLine()) {
				data = fileReader.nextLine();
				System.out.println(data);
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
		      System.out.println("An error occurred reading the city graph.");
		      e.printStackTrace();
		}
	}
	

	//The main function of the program.  Enter and exit from here.
	//It is a simple getInput processInput loop until the game is over.  
	public static void main(String[] args) {
		boolean gameDone = false;

		System.out.println("Welcome to the Pandemic.");
		printRules();
		readCityGraph();
		initCities();
		initResearchStations();
		initDeck();
		infectCities();

		while (!gameDone) {
			gameDone = playerTurn();
			
		}
		
		if (checkWin()) {
			System.out.println("Victory");
		} else if (checkLoss() || playerDraw()){
			System.out.println("Defeat");
		} else {
			System.out.println("graceful exit");
		}
	}
}
