
/* This file includes java source code to run Wa-Tor which

* is a population dynamics simulation devised by Alexander Keewatin Dewdney[1] and 
* presented in the December 1984 issue of Scientific American in a 5-page article 
* entitled "Computer Recreations: Sharks and fish wage an ecological war on the toroidal 
* planet Wa-Tor".
//*

//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title:           (Wa-Tor)
// Files:           (WaTor.java, TestWaTor.java, Config.java)
// Course:          (200, Spring, 2018)
//
// Author:          (Luke Sprangers)
// Email:           (lsprangers@wisc.edu )
// Lecturer's Name: (Jim Williams)
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully 
// acknowledge and credit those sources of help here.  Instructors and TAs do 
// not need to be credited here, but tutors, friends, relatives, room mates 
// strangers, etc do.  If you received no outside help from either type of 
// source, then please explicitly indicate NONE.
//
// Persons:         (identify each person and describe their help in detail)
// Online Sources:  (identify each URL and describe their assistance in detail)
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class WaTor {

	/**
	 * This is the main method for WaTor simulation. Based on:
	 * http://home.cc.gatech.edu/biocs1/uploads/2/wator_dewdney.pdf This method
	 * contains the main simulation loop. In main the Scanner for System.in is
	 * allocated and used to interact with the user.
	 * 
	 * @param args
	 *            (unused)
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to Wa-Tor");
		char fileLoadResponseChar = 'n';
		String fileLoadResponse = "";
		int checkInput = 0;
		//history Array list to track chronon, numFish, & numSharks throughout simulation
		ArrayList<int[]> history = new ArrayList<int[]>();
		// scanner and random number generator for use throughout
		Scanner input = new Scanner(System.in);
		Random randGen = new Random();

		// values at the same index in these parallel arrays correspond to the
		// same creature

		// a value equal or greater than 0 at a location indicates a fish of
		// that age at that location.
		int[][] fish = null;

		// true at a location indicates that the fish moved during the current
		// chronon
		boolean[][] fishMoved = null;

		// a value equal or greater than 0 at a location indicates a shark of
		// that age at that location
		int[][] sharks = null;

		// true at a location indicates that the shark moved during the current
		// chronon
		boolean[][] sharksMoved = null;

		// a value equal or greater than 0 indicates the number of chronon
		// since the shark last ate.
		int[][] starve = null;

		// an array for simulation parameters
		// to be used when saving or loading parameters in Milestone 3
		int[] simulationParameters = null;

		// Ask user if they would like to load simulation parameters from a file.
		// If the user enters a y or Y as the only non-whitespace characters
		// then prompt for filename and call loadSimulationParameters
		System.out.print("Do you want to load simulation parameters from a file (y/n): ");
		if (input.hasNextLine()) {
			fileLoadResponse = input.nextLine();
			if (fileLoadResponse.length() == 1) {
				fileLoadResponseChar = Character.toLowerCase(fileLoadResponse.charAt(0));
				if (fileLoadResponseChar == 'y') {
					System.out.print("Enter filename to load: ");
					String fileNameToLoad = input.nextLine();
					simulationParameters = loadSimulationParameters(fileNameToLoad);
				}
			}
		}

		// prompts the user to enter the simulation parameters
		if (simulationParameters == null) {
			checkInput = 1;
			simulationParameters = new int[Config.SIM_PARAMS.length];
			for (int i = 0; i < Config.SIM_PARAMS.length; i++) {
				System.out.print("Enter " + Config.SIM_PARAMS[i] + ": ");
				simulationParameters[i] = input.nextInt();
			}
			input.nextLine(); // read and ignore remaining newline
		}

		// if seed is > 0 then set the random number generator to seed
		if (simulationParameters[indexForParam("seed")] > 0) {
			randGen.setSeed(simulationParameters[indexForParam("seed")]);
		}

		// save simulation parameters in local variables to help make code
		// more readable.
		int oceanWidth = simulationParameters[indexForParam("ocean_width")];
		int oceanHeight = simulationParameters[indexForParam("ocean_height")];
		int startingFish = simulationParameters[indexForParam("starting_fish")];
		int startingSharks = simulationParameters[indexForParam("starting_sharks")];
		int fishBreed = simulationParameters[indexForParam("fish_breed")];
		int sharksBreed = simulationParameters[indexForParam("sharks_breed")];
		int sharksStarve = simulationParameters[indexForParam("sharks_starve")];

		// create parallel arrays to track fish and sharks
		fish = new int[oceanHeight][oceanWidth];
		fishMoved = new boolean[oceanHeight][oceanWidth];
		sharks = new int[oceanHeight][oceanWidth];
		sharksMoved = new boolean[oceanHeight][oceanWidth];
		starve = new int[oceanHeight][oceanWidth];

		// make sure fish, sharks and starve arrays are empty (call emptyArray)
		emptyArray(fish);
		emptyArray(sharks);
		emptyArray(starve);

		// place the initial fish and sharks and print out the number
		// placed
		int numFish = 0;
		int numSharks = 0;
		numFish = placeFish(fish, startingFish, fishBreed, randGen);
		numSharks = placeSharks(fish, sharks, startingSharks, sharksBreed, randGen);
		System.out.println("Placed " + numFish + " fish.");
		System.out.println("Placed " + numSharks + " sharks.");
		int currentChronon = 1;
		int responseInt = 1;

		// simulation ends when no more sharks or fish remain
		boolean simulationEnd = numFish <= 0 || numSharks <= 0;
		while (!simulationEnd) {
			int[] historyCount = new int[Config.NUM_HISTORY_ITEMS];
			if (responseInt == 1) {
				showFishAndSharks(currentChronon, fish, sharks);
				System.out.print("Press Enter, # of chronon, or 'end': ");
				String response = input.nextLine().trim();
				if (response.equals("")) {
				} else if (response.equalsIgnoreCase("end")) {
					break; // leave simulation loop
				} else {
					try {
						responseInt = Integer.parseInt(response);
					} catch (NumberFormatException e) {
					}
				}
			} else {
				--responseInt;
			}
			// Add the current chronon, numFish, and numSharks to history
			historyCount[Config.HISTORY_CHRONON_INDEX] = currentChronon;
			historyCount[Config.HISTORY_NUM_FISH_INDEX] = numFish;
			historyCount[Config.HISTORY_NUM_SHARKS_INDEX] = numSharks;
			history.add(historyCount);
			clearMoves(fishMoved);
			clearMoves(sharksMoved);
			fishSwimAndBreed(fish, sharks, fishMoved, fishBreed, randGen);
			sharksHuntAndBreed(fish, sharks, fishMoved, sharksMoved, sharksBreed, starve, sharksStarve, randGen);
			// increment current chronon and count the current number of fish and sharks
			++currentChronon;
			numFish = countCreatures(fish);
			numSharks = countCreatures(sharks);
			// if all the fish or sharks are gone then end simulation
			simulationEnd = numFish <= 0 || numSharks <= 0;

		}
		;
		int[] historyCount2 = new int[Config.NUM_HISTORY_ITEMS];
		historyCount2[Config.HISTORY_CHRONON_INDEX] = currentChronon;
		historyCount2[Config.HISTORY_NUM_FISH_INDEX] = numFish;
		historyCount2[Config.HISTORY_NUM_SHARKS_INDEX] = numSharks;
		history.add(historyCount2);
		
		// print the final ocean contents
		showFishAndSharks(currentChronon, fish, sharks);

		// Print out why the simulation ended.
		if (numSharks <= 0) {
			System.out.println("Wa-Tor simulation ended since no sharks remain.");
		} else if (numFish <= 0) {
			System.out.println("Wa-Tor simulation ended since no fish remain.");
		} else {
			System.out.println("Wa-Tor simulation ended at user request.");
		}

		// If the user was prompted to enter simulation parameters
		// then prompt the user to see if they would like to save them.
		// If the user enters a y or Y as the only non-whitespace characters
		// then prompt for filename and save, otherwise don't save parameters.
		// call saveSimulationParameters to actually save the parameters to the file.
		// If saveSimulationParameters throws an IOException then catch it and
		// repeat the code to prompt asking the user if they want to save
		// the population chart.
		boolean x;
		String fileNameToSave = "";
		if (checkInput == 1) {
			x = false;
			do {
				System.out.print("Save simulation parameters (y/n): ");
				try {
					fileLoadResponse = input.nextLine();
					if (fileLoadResponse.length() == 1) {
						fileLoadResponseChar = Character.toLowerCase(fileLoadResponse.charAt(0));
						if (fileLoadResponseChar == 'y') {
							System.out.print("Enter filename: ");
							fileNameToSave = input.nextLine();
							saveSimulationParameters(simulationParameters, fileNameToSave);
						}
					}
				} catch (IOException e) {
					System.out.print("Unable to save to: " + fileNameToSave);
					x = true;
				} catch (IndexOutOfBoundsException err) {
					x = false;
				}
			} while (x);
		}

		// Always prompt the user to see if they would like to save a
		// population chart of the simulation.
		// If the user enters a y or Y as the only non-whitespace characters
		// then prompt for filename and save, otherwise don't save chart.
		// call savePopulationChart to save the parameters to the file.
		// If savePopulationChart throws an IOException then catch it and
		// repeat the code to prompt asking the user if they want to save
		// the population chart.
		boolean y;
		String fileNameForPopChart = "";
		do {
			y = false;
			System.out.print("Save population chart (y/n): ");
			try {
				fileLoadResponse = input.nextLine();
				if (fileLoadResponse.length() == 1) {
					fileLoadResponseChar = Character.toLowerCase(fileLoadResponse.charAt(0));
					if (fileLoadResponseChar == 'y') {
						System.out.print("Enter filename: ");
						fileNameForPopChart = input.nextLine();
						savePopulationChart(simulationParameters, history, oceanWidth, oceanHeight,
								fileNameForPopChart);
					}
				}
			} catch (IOException e) {
				System.out.print("Unable to save to: " + fileNameForPopChart);
				y = true;
			} catch (IndexOutOfBoundsException err) {
				y = false;
			}
		} while (y);

		input.close();
		System.out.println("Goodbye.");
	}

	/**
	 * This is called when a fish cannot move. This increments the fish's age and
	 * notes in the fishMove array that it has been updated this chronon.
	 * 
	 * @param fish
	 *            The array containing all the ages of all the fish.
	 * @param fishMove
	 *            The array containing the indicator of whether each fish moved this
	 *            chronon.
	 * @param row
	 *            The row of the fish that is staying.
	 * @param col
	 *            The col of the fish that is staying.
	 */
	public static void aFishStays(int[][] fish, boolean[][] fishMove, int row, int col) {
		if (Config.DEBUG) {
			System.out.printf("DEBUG fish %d,%d stays\n", row, col);
		}
		fish[row][col]++; // increment age of fish
		fishMove[row][col] = true;
	}

	/**
	 * The fish moves from fromRow,fromCol to toRow,toCol. The age of the fish is
	 * incremented. The fishMove array records that this fish has moved this
	 * chronon.
	 * 
	 * @param fish
	 *            The array containing all the ages of all the fish.
	 * @param fishMove
	 *            The array containing the indicator of whether each fish moved this
	 *            chronon.
	 * @param fromRow
	 *            The row the fish is moving from.
	 * @param fromCol
	 *            The column the fish is moving from.
	 * @param toRow
	 *            The row the fish is moving to.
	 * @param toCol
	 *            The column the fish is moving to.
	 */
	public static void aFishMoves(int[][] fish, boolean[][] fishMove, int fromRow, int fromCol, int toRow, int toCol) {
		if (Config.DEBUG) {
			System.out.printf("DEBUG fish moved from %d,%d to %d,%d\n", fromRow, fromCol, toRow, toCol);
		}
		// just move fish
		fish[toRow][toCol] = fish[fromRow][fromCol] + 1; // increment age
		fishMove[toRow][toCol] = true;

		// clear previous location
		fish[fromRow][fromCol] = Config.EMPTY;
		fishMove[fromRow][fromCol] = false;
	}

	/**
	 * The fish moves from fromRow,fromCol to toRow,toCol. This fish breeds so its
	 * age is reset to 0. The new fish is put in the fromRow,fromCol with an age of
	 * 0. The fishMove array records that both fish moved this chronon.
	 * 
	 * @param fish
	 *            The array containing all the ages of all the fish.
	 * @param fishMove
	 *            The array containing the indicator of whether each fish moved this
	 *            chronon.
	 * @param fromRow
	 *            The row the fish is moving from and where the new fish is located.
	 * @param fromCol
	 *            The column the fish is moving from and where the new fish is
	 *            located.
	 * @param toRow
	 *            The row the fish is moving to.
	 * @param toCol
	 *            The column the fish is moving to.
	 */
	public static void aFishMovesAndBreeds(int[][] fish, boolean[][] fishMove, int fromRow, int fromCol, int toRow,
			int toCol) {
		if (Config.DEBUG) {
			System.out.printf("DEBUG fish moved from %d,%d to %d,%d and breed\n", fromRow, fromCol, toRow, toCol);
		}
		// move fish, resetting age in new location
		fish[toRow][toCol] = 0;
		fishMove[toRow][toCol] = true;

		// breed
		fish[fromRow][fromCol] = 0; // new fish in previous location
		fishMove[fromRow][fromCol] = true;
	}

	/**
	 * This removes the shark from the sharks, sharksMove and starve arrays.
	 * 
	 * @param sharks
	 *            The array containing all the ages of all the sharks.
	 * @param sharksMove
	 *            The array containing the indicator of whether each shark moved
	 *            this chronon.
	 * @param starve
	 *            The array containing the time in chronon since the sharks last
	 *            ate.
	 * @param row
	 *            The row the shark is in.
	 * @param col
	 *            The column the shark is in.
	 */
	public static void sharkStarves(int[][] sharks, boolean[][] sharksMove, int[][] starve, int row, int col) {
		if (Config.DEBUG) {
			System.out.printf("DEBUG shark %d,%d starves\n", row, col);
		}
		sharks[row][col] = Config.EMPTY;
		starve[row][col] = Config.EMPTY;
		sharksMove[row][col] = false;
	}

	/**
	 * This is called when a shark cannot move. This increments the shark's age and
	 * time since the shark last ate and notes in the sharkMove array that it has
	 * been updated this chronon.
	 * 
	 * @param sharks
	 *            The array containing all the ages of all the sharks.
	 * @param sharksMove
	 *            The array containing the indicator of whether each shark moved
	 *            this chronon.
	 * @param starve
	 *            The array containing the time in chronon since the sharks last
	 *            ate.
	 * @param row
	 *            The row the shark is in.
	 * @param col
	 *            The column the shark is in.
	 */
	public static void sharkStays(int[][] sharks, boolean[][] sharksMove, int[][] starve, int row, int col) {
		if (Config.DEBUG) {
			System.out.printf("DEBUG shark %d,%d can't move\n", row, col);
		}
		sharks[row][col]++; // increment age of shark
		starve[row][col]++; // increment time since last ate
		sharksMove[row][col] = true;
	}

	/**
	 * This moves a shark from fromRow,fromCol to toRow,toCol. This increments the
	 * age and time since the shark last ate and notes that this shark has moved
	 * this chronon.
	 * 
	 * @param sharks
	 *            The array containing all the ages of all the sharks.
	 * @param sharksMove
	 *            The array containing the indicator of whether each shark moved
	 *            this chronon.
	 * @param starve
	 *            The array containing the time in chronon since the sharks last
	 *            ate.
	 * @param fromRow
	 *            The row the shark is moving from.
	 * @param fromCol
	 *            The column the shark is moving from.
	 * @param toRow
	 *            The row the shark is moving to.
	 * @param toCol
	 *            The column the shark is moving to.
	 */
	public static void sharkMoves(int[][] sharks, boolean[][] sharksMove, int[][] starve, int fromRow, int fromCol,
			int toRow, int toCol) {
		if (Config.DEBUG) {
			System.out.printf("DEBUG shark moved from %d,%d to %d,%d\n", fromRow, fromCol, toRow, toCol);
		}
		// just move shark
		sharks[toRow][toCol] = sharks[fromRow][fromCol] + 1; // move age
		sharksMove[toRow][toCol] = true;
		starve[toRow][toCol] = starve[fromRow][fromCol] + 1;

		sharks[fromRow][fromCol] = Config.EMPTY;
		sharksMove[fromRow][fromCol] = false;
		starve[fromRow][fromCol] = 0;
	}

	/**
	 * The shark moves from fromRow,fromCol to toRow,toCol. This shark breeds so its
	 * age is reset to 0 but its time since last ate is incremented. The new shark
	 * is put in the fromRow,fromCol with an age of 0 and 0 time since last ate. The
	 * fishMove array records that both fish moved this chronon.
	 * 
	 * @param sharks
	 *            The array containing all the ages of all the sharks.
	 * @param sharksMove
	 *            The array containing the indicator of whether each shark moved
	 *            this chronon.
	 * @param starve
	 *            The array containing the time in chronon since the sharks last
	 *            ate.
	 * @param fromRow
	 *            The row the shark is moving from.
	 * @param fromCol
	 *            The column the shark is moving from.
	 * @param toRow
	 *            The row the shark is moving to.
	 * @param toCol
	 *            The column the shark is moving to.
	 */
	public static void sharkMovesAndBreeds(int[][] sharks, boolean[][] sharksMove, int[][] starve, int fromRow,
			int fromCol, int toRow, int toCol) {

		if (Config.DEBUG) {
			System.out.printf("DEBUG shark moved from %d,%d to %d,%d and breeds\n", fromRow, fromCol, toRow, toCol);
		}
		sharks[toRow][toCol] = 0; // reset age in new location
		sharks[fromRow][fromCol] = 0; // new fish in previous location

		sharksMove[toRow][toCol] = true;
		sharksMove[fromRow][fromCol] = true;

		starve[toRow][toCol] = starve[fromRow][fromCol] + 1;
		starve[fromRow][fromCol] = 0;
	}

	/**
	 * The shark in fromRow,fromCol moves to toRow,toCol and eats the fish. The
	 * sharks age is incremented, time since it last ate and that this shark moved
	 * this chronon are noted. The fish is now gone.
	 * 
	 * @param sharks
	 *            The array containing all the ages of all the sharks.
	 * @param sharksMove
	 *            The array containing the indicator of whether each shark moved
	 *            this chronon.
	 * @param starve
	 *            The array containing the time in chronon since the sharks last
	 *            ate.
	 * @param fish
	 *            The array containing all the ages of all the fish.
	 * @param fishMove
	 *            The array containing the indicator of whether each fish moved this
	 *            chronon.
	 * @param fromRow
	 *            The row the shark is moving from.
	 * @param fromCol
	 *            The column the shark is moving from.
	 * @param toRow
	 *            The row the shark is moving to.
	 * @param toCol
	 *            The column the shark is moving to.
	 */
	public static void sharkEatsFish(int[][] sharks, boolean[][] sharksMove, int[][] starve, int[][] fish,
			boolean[][] fishMove, int fromRow, int fromCol, int toRow, int toCol) {
		if (Config.DEBUG) {
			System.out.printf("DEBUG shark moved from %d,%d and ate fish %d,%d\n", fromRow, fromCol, toRow, toCol);
		}
		// eat fish
		fish[toRow][toCol] = Config.EMPTY;
		fishMove[toRow][toCol] = false;

		// move shark
		sharks[toRow][toCol] = sharks[fromRow][fromCol] + 1; // move age
		sharksMove[toRow][toCol] = true;
		starve[toRow][toCol] = starve[fromRow][fromCol] = 0;

		// clear old location
		sharks[fromRow][fromCol] = Config.EMPTY;
		sharksMove[fromRow][fromCol] = true;
		starve[fromRow][fromCol] = 0;
	}

	/**
	 * The shark in fromRow,fromCol moves to toRow,toCol and eats the fish. The fish
	 * is now gone. This shark breeds so its age is reset to 0 and its time since
	 * last ate is incremented. The new shark is put in the fromRow,fromCol with an
	 * age of 0 and 0 time since last ate. That these sharks moved this chronon is
	 * noted.
	 * 
	 * @param sharks
	 *            The array containing all the ages of all the sharks.
	 * @param sharksMove
	 *            The array containing the indicator of whether each shark moved
	 *            this chronon.
	 * @param starve
	 *            The array containing the time in chronon since the sharks last
	 *            ate.
	 * @param fish
	 *            The array containing all the ages of all the fish.
	 * @param fishMove
	 *            The array containing the indicator of whether each fish moved this
	 *            chronon.
	 * @param fromRow
	 *            The row the shark is moving from.
	 * @param fromCol
	 *            The column the shark is moving from.
	 * @param toRow
	 *            The row the shark is moving to.
	 * @param toCol
	 *            The column the shark is moving to.
	 */
	public static void sharkEatsFishAndBreeds(int[][] sharks, boolean[][] sharksMove, int[][] starve, int[][] fish,
			boolean[][] fishMove, int fromRow, int fromCol, int toRow, int toCol) {
		if (Config.DEBUG) {
			System.out.printf("DEBUG shark moved from %d,%d and ate fish %d,%d and breed\n", fromRow, fromCol, toRow,
					toCol);
		}
		// shark eats fish and may breed
		// eat fish
		fish[toRow][toCol] = Config.EMPTY;
		fishMove[toRow][toCol] = false;

		// move to new location
		sharks[toRow][toCol] = 0; // reset age in new location
		sharksMove[toRow][toCol] = true;
		starve[toRow][toCol] = 0;

		// breed
		sharks[fromRow][fromCol] = 0; // new shark in previous location
		sharksMove[fromRow][fromCol] = true;
		starve[fromRow][fromCol] = 0;
	}

	/**
	 * This sets all elements within the array to Config.EMPTY. This does not assume
	 * any array size but uses the .length attribute of the array. If arr is null
	 * the method prints an error message and returns.
	 * 
	 * @param arr
	 *            The array that only has EMPTY elements when method has executed.
	 */
	public static void emptyArray(int[][] arr) {
		if (arr == null) {
			System.out.println("emptyArray arr is null");
			return;
		}
		for (int row = 0; row < arr.length; row++) {
			for (int col = 0; col < arr[row].length; col++) {
				arr[row][col] = Config.EMPTY;
			}
		}
	}

	/**
	 * This sets all elements within the array to false, indicating not moved this
	 * chronon. This does not assume any array size but uses the .length attribute
	 * of the array. If arr is null the method prints a message and returns.
	 * 
	 * @param arr
	 *            The array will have only false elements when method completes.
	 */
	public static void clearMoves(boolean[][] arr) {
		if (arr == null) {
			System.out.println("clearMoves arr is null");
			return;
		}
		for (int row = 0; row < arr.length; row++) {
			for (int col = 0; col < arr[row].length; col++) {
				arr[row][col] = false;
			}
		}
	}

	/**
	 * Shows the locations of all the fish and sharks noting a fish with
	 * Config.FISH_MARK, a shark with Config.SHARK_MARK and empty water with
	 * Config.WATER_MARK. At the top is a title "Chronon: " with the current chronon
	 * and at the bottom is a count of the number of fish and sharks. Example of a 3
	 * row, 5 column ocean. Note every mark is also followed by a space. Chronon: 1
	 * O . . O . . . . . O fish:7 sharks:3
	 * 
	 * @param chronon
	 *            The current chronon.
	 * @param fish
	 *            The array containing all the ages of all the fish.
	 * @param sharks
	 *            The array containing all the ages of all the sharks.
	 */
	public static void showFishAndSharks(int chronon, int[][] fish, int[][] sharks) {
		int fishCount = 0;
		int sharkCount = 0;
		System.out.println("Chronon: " + chronon);
		for (int i = 0; i < fish.length; ++i) {
			for (int j = 0; j < fish[i].length; ++j) {
				if (fish[i][j] != Config.EMPTY) {
					System.out.print(Config.FISH_MARK + " ");
					++fishCount;
				} else if (sharks[i][j] != Config.EMPTY) {
					System.out.print(Config.SHARK_MARK + " ");
					++sharkCount;
				} else {
					System.out.print(Config.WATER_MARK + " ");
				}
			}
			System.out.println();
		}
		System.out.println("fish:" + fishCount + " sharks:" + sharkCount);
	}

	/**
	 * This places up to startingFish fish in the fish array. This randomly chooses
	 * a location and age for each fish. Algorithm: For each fish this tries to
	 * place reset the attempts to place the particular fish to 0. Try to place a
	 * single fish up to Config.MAX_PLACE_ATTEMPTS times Randomly choose a row, then
	 * column using randGen.nextInt( ) with the appropriate fish array dimension as
	 * the parameter. Increment the number of attempts to place the fish. If the
	 * location is empty in the fish array then place the fish in that location,
	 * randomly choosing its age from 0 up to and including fishBreed. If the
	 * location is already occupied, generate another location and try again. On the
	 * Config.MAX_PLACE_ATTEMPTS try, whether or not the fish is successfully
	 * placed, stop trying to place additional fish. Return the number of fish
	 * actually placed.
	 * 
	 * @param fish
	 *            The array containing all the ages of all the fish.
	 * @param startingFish
	 *            The number of fish to attempt to place in the fish array.
	 * @param fishBreed
	 *            The age at which fish breed.
	 * @param randGen
	 *            The random number generator.
	 * @return the number of fish actually placed.
	 */
	public static int placeFish(int[][] fish, int startingFish, int fishBreed, Random randGen) {
		int numFishPlaced = 0;
		int numFishTried = 0;
		int fishCol;
		int fishRow;
		int fishAge;

		while (numFishPlaced < startingFish && numFishTried < Config.MAX_PLACE_ATTEMPTS) {
			numFishTried++;
			fishRow = randGen.nextInt(fish.length);
			fishCol = randGen.nextInt(fish[fishRow].length);
			if (fish[fishRow][fishCol] == Config.EMPTY) {
				fishAge = randGen.nextInt(fishBreed + 1);
				fish[fishRow][fishCol] = fishAge;
				numFishPlaced++;
				if (numFishTried < Config.MAX_PLACE_ATTEMPTS) {
					numFishTried = 0;
				}
			}
		}
		return numFishPlaced;
	}

	/**
	 * This places up to startingSharks sharks in the sharks array. This randomly
	 * chooses a location and age for each shark. Algorithm: For each shark this
	 * tries to place reset the attempts to place the particular shark to 0. Try to
	 * place a single shark up to Config.MAX_PLACE_ATTEMPTS times Randomly choose a
	 * row, then column using randGen.nextInt( ) with the appropriate shark array
	 * dimension as the parameter. Increment the number of attempts to place the
	 * shark. If the location is empty in both the fish array and sharks array then
	 * place the shark in that location, randomly choosing its age from 0 up to and
	 * including sharkBreed. If the location is already occupied, generate another
	 * location and try again. If a single shark is not placed after
	 * Config.MAX_PLACE_ATTEMPTS times, then stop trying to place the rest of the
	 * sharks. Return the number of sharks actually placed. *
	 * 
	 * @param fish
	 *            The array containing all the ages of all the fish.
	 * @param sharks
	 *            The array containing all the ages of all the sharks.
	 * @param startingSharks
	 *            The number of sharks to attempt to place in the sharks array.
	 * @param sharksBreed
	 *            The age at which sharks breed.
	 * @param randGen
	 *            The random number generator.
	 * @return the number of sharks actually placed.
	 * 
	 */
	public static int placeSharks(int[][] fish, int[][] sharks, int startingSharks, int sharksBreed, Random randGen) {
		int numSharksPlaced = 0;
		int numSharksTried = 0;
		int sharkCol;
		int sharkRow;
		int sharkAge;
		// two while loops
		while (numSharksTried < Config.MAX_PLACE_ATTEMPTS && numSharksPlaced < startingSharks) {
			++numSharksTried;
			sharkRow = randGen.nextInt(sharks.length);
			sharkCol = randGen.nextInt(sharks[0].length);
			if (fish[sharkRow][sharkCol] == Config.EMPTY && sharks[sharkRow][sharkCol] == Config.EMPTY) {
				sharkAge = randGen.nextInt(sharksBreed + 1);
				sharks[sharkRow][sharkCol] = sharkAge;
				++numSharksPlaced;
				if (numSharksTried < Config.MAX_PLACE_ATTEMPTS) {
					numSharksTried = 0;
				}
			}
		}
		return numSharksPlaced;
	}

	/**
	 * This counts the number of fish or the number of sharks depending on the array
	 * passed in.
	 * 
	 * @param fishOrSharks
	 *            Either an array containing the ages of all the fish or an array
	 *            containing the ages of all the sharks.
	 * @return The number of fish or number of sharks, depending on the array passed
	 *         in.
	 */
	public static int countCreatures(int[][] fishOrSharks) {
		int numCreatures = 0;
		for (int i = 0; i < fishOrSharks.length; ++i) {
			for (int j = 0; j < fishOrSharks[i].length; ++j) {
				if (fishOrSharks[i][j] != Config.EMPTY) {
					++numCreatures;
				}
			}
		}
		return numCreatures;
	}

	/**
	 * This returns a list of the coordinates (row,col) of positions around the row,
	 * col parameters that do not contain a fish or shark. The positions that are
	 * considered are directly above, below, left and right of row, col and IN THAT
	 * ORDER. Where 0,0 is the upper left corner when fish and sharks arrays are
	 * printed out. Remember that creatures moving off one side of the array appear
	 * on the opposite side. For example, those moving left off the array appear on
	 * the right side and those moving down off the array appear at the top.
	 * 
	 * @param fish
	 *            A non-Config.EMPTY value indicates the age of the fish occupying
	 *            the location.
	 * @param sharks
	 *            A non-Config.EMPTY value indicates the age of the shark occupying
	 *            the location.
	 * @param row
	 *            The row of a creature trying to move.
	 * @param col
	 *            The column of a creature trying to move.
	 * @return An ArrayList containing 0 to 4, 2-element arrays with row,col
	 *         coordinates of unoccupied locations. In each coordinate array the 0
	 *         index is the row, the 1 index is the column.
	 */
	public static ArrayList<int[]> unoccupiedPositions(int[][] fish, int[][] sharks, int row, int col) {
		ArrayList<int[]> unoccupied = new ArrayList<>();
		int rightC;
		int leftC;
		int topR;
		int botR;
		if (row == 0) {
			topR = fish.length - 1;
			botR = row + 1;
		} else if (row == fish.length - 1) {
			topR = row - 1;
			botR = 0;
		} else {
			topR = row - 1;
			botR = row + 1;
		}

		if (col == 0) {
			leftC = fish[row].length - 1;
			rightC = col + 1;
		} else if (col == fish[row].length - 1) {
			rightC = 0;
			leftC = col - 1;
		} else {
			rightC = col + 1;
			leftC = col - 1;
		}

		// above, below, left, and right
		// 1 2 3 4
		if (fish[topR][col] == Config.EMPTY && sharks[topR][col] == Config.EMPTY) {
			unoccupied.add(new int[] { topR, col });
		}
		if (fish[botR][col] == Config.EMPTY && sharks[botR][col] == Config.EMPTY) {
			unoccupied.add(new int[] { botR, col });
		}
		if (fish[row][leftC] == Config.EMPTY && sharks[row][leftC] == Config.EMPTY) {
			unoccupied.add(new int[] { row, leftC });
		}
		if (fish[row][rightC] == Config.EMPTY && sharks[row][rightC] == Config.EMPTY) {
			unoccupied.add(new int[] { row, rightC });
		}

		return unoccupied;
	}

	/**
	 * This randomly selects, with the Random number generator passed as a
	 * parameter, one of elements (array of int) in the neighbors list. If the size
	 * of neighbors is 0 (empty) then null is returned. If neighbors contains 1
	 * element then that element is returned. The randGen parameter is only used to
	 * select 1 element from a neighbors list containing more than 1 element. If
	 * neighbors or randGen is null then an error message is printed to System.err
	 * and null is returned.
	 * 
	 * @param neighbors
	 *            A list of potential neighbors to choose from.
	 * @param randGen
	 *            The random number generator used throughout the simulation.
	 * @return A int[] containing the coordinates of a creatures move or null as
	 *         specified above.
	 */
	public static int[] chooseMove(ArrayList<int[]> neighbors, Random randGen) {
		try {
			int numElements = neighbors.size();
			boolean nullGen = (randGen == null);
			if (neighbors == null || nullGen) {
				throw new NullPointerException("chooseMove neighbors invalid array or randGen: null");
			} else if (numElements == 1) {
				return neighbors.get(0);
			} else {
				int randIndex = randGen.nextInt(numElements);
				return neighbors.get(randIndex);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	/**
	 * This attempts to move each fish each chronon.
	 * 
	 * This is a key method with a number of parameters. Check that the parameters
	 * are valid prior to writing the code to move a fish. The parameters are
	 * checked in the order they appear in the parameter list. If any of the array
	 * parameters are null or not at least 1 element in size then a helpful error
	 * message is printed out and -1 is returned. An example message for an invalid
	 * fish array is "fishSwimAndBreed Invalid fish array: Null or not at least 1 in
	 * each dimension.". Testing will not check the content of the message but will
	 * check whether the correct number is returned for the situation. Passing this
	 * test means we know fish[0] exists and so won't cause a runtime error and also
	 * that fish[0].length is the width. For this project it is safe to assume
	 * rectangular arrays (arrays where all the rows are the same length). If
	 * fishBreed is less than zero a helpful error message is printed out and -2 is
	 * returned. If randGen is null then a helpful error message is printed out and
	 * -3 is returned.
	 * 
	 *
	 * Algorithm: for each fish that has not moved this chronon get the available
	 * unoccupied positions for the fish to move (call unoccupiedPositions) choose a
	 * move from those positions (call chooseMove) Based on the move chosen, either
	 * the fish stays (call aFishStays) fish moves (call aFishMoves) or fish moves
	 * and breeds (call aFishMovesAndBreeds)
	 * 
	 * 
	 * @param fish
	 *            The array containing all the ages of all the fish.
	 * @param sharks
	 *            The array containing all the ages of all the sharks.
	 * @param fishMove
	 *            The array containing the indicator of whether each fish moved this
	 *            chronon.
	 * @param fishBreed
	 *            The age in chronon that a fish must be to breed.
	 * @param randGen
	 *            The instance of the Random number generator.
	 * @return -1, -2, -3 for invalid parameters as specified above. After
	 *         attempting to move all fish 0 is returned indicating success.
	 */
	public static int fishSwimAndBreed(int[][] fish, int[][] sharks, boolean[][] fishMove, int fishBreed,
			Random randGen) {

		try {
			int i;
			int j;
			// minimum of all array columns and rows; checks for any null array.
			int min = Math.min(Math.min(fishMove.length, fishMove[0].length),
					(Math.min(Math.min(fish.length, fish[0].length), Math.min(sharks.length, sharks[0].length))));

			if (min == 0) {
				throw new NullPointerException(
						"fishSwimAndBreed Invalid fish array: " + "Null or not at least 1 in each dimension.");
			}
			if (randGen == null) {
				throw new IllegalArgumentException("randGen must not be null");
			}
			if (fishBreed < 0) {
				throw new Exception("fishBreed must be non-negative");
			}
			ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
			int[] newMove = new int[2];
			for (i = 0; i < fish.length; ++i) {
				for (j = 0; j < fish[i].length; ++j) {
					Arrays.fill(newMove, 0);
					// if there is a fish, and if it hasn't moved then try to move it to an
					// unoccupied position
					if (!fishMove[i][j] && fish[i][j] >= 0) {
						possibleMoves = unoccupiedPositions(fish, sharks, i, j);

						if (possibleMoves.isEmpty()) {
							aFishStays(fish, fishMove, i, j);
						}

						else {
							newMove = chooseMove(possibleMoves, randGen);
							if (fish[i][j] >= fishBreed) {
								aFishMovesAndBreeds(fish, fishMove, i, j, newMove[0], newMove[1]);
							} else {
								aFishMoves(fish, fishMove, i, j, newMove[0], newMove[1]);
							}

						}
					}
					possibleMoves.clear();
				}
			}

			return 0;
		} catch (NullPointerException excpt) {
			System.out.print(excpt.getMessage());
			return -1;
		} catch (IllegalArgumentException excpt2) {
			System.out.print(excpt2.getMessage());
			return -3;
		} catch (Exception excpt1) {
			System.out.print(excpt1.getMessage());
			return -2;
		} catch (Throwable thrwObj) {
			System.out.print("Exception: program terminated");
			return -4;
		}
	}

	/**
	 * This returns a list of the coordinates (row,col) of positions around the row,
	 * col parameters that contain a fish. The positions that are considered are
	 * directly above, below, left and right of row, col and IN THAT ORDER. Where
	 * 0,0 is the upper left corner when fish array is printed out. Remember that
	 * sharks moving off one side of the array appear on the opposite side. For
	 * example, those moving left off the array appear on the right side and those
	 * moving down off the array appear at the top.
	 * 
	 * @param fish
	 *            A non-Config.EMPTY value indicates the age of the fish occupying a
	 *            location.
	 * @param row
	 *            The row of a hungry shark.
	 * @param col
	 *            The column of a hungry shark.
	 * @return An ArrayList containing 0 to 4, 2-element arrays with row,col
	 *         coordinates of fish locations. In each coordinate array the 0 index
	 *         is the row, the 1 index is the column.
	 */
	public static ArrayList<int[]> fishPositions(int[][] fish, int row, int col) {
		ArrayList<int[]> fishPositions = new ArrayList<>();
		int rightC;
		int leftC;
		int topR;
		int botR;
		if (row == 0) {
			topR = fish.length - 1;
			botR = row + 1;
		} else if (row == fish.length - 1) {
			topR = row - 1;
			botR = 0;
		} else {
			topR = row - 1;
			botR = row + 1;
		}

		if (col == 0) {
			leftC = fish[row].length - 1;
			rightC = col + 1;
		} else if (col == fish[row].length - 1) {
			rightC = 0;
			leftC = col - 1;
		} else {
			rightC = col + 1;
			leftC = col - 1;
		}

		if (fish[topR][col] != Config.EMPTY) {
			fishPositions.add(new int[] { topR, col });
		}
		if (fish[botR][col] != Config.EMPTY) {
			fishPositions.add(new int[] { botR, col });
		}
		if (fish[row][leftC] != Config.EMPTY) {
			fishPositions.add(new int[] { row, leftC });
		}
		if (fish[row][rightC] != Config.EMPTY) {
			fishPositions.add(new int[] { row, rightC });
		}

		return fishPositions;
	}

	/**
	 * This attempts to move each shark each chronon.
	 *
	 * This is a key method with a number of parameters. Check that the parameters
	 * are valid prior to writing the code to move a shark. The parameters are
	 * checked in the order they appear in the parameter list. If any of the array
	 * parameters are null or not at least 1 element in size then a helpful error
	 * message is printed out and -1 is returned. An example message for an invalid
	 * fish array is "sharksHuntAndBreed Invalid fish array: Null or not at least 1
	 * in each dimension.". Testing will not check the content of the message but
	 * will check whether the correct number is returned for the situation. Passing
	 * this test means we know fish[0] exists and so won't cause a runtime error and
	 * also that fish[0].length is the width. For this project it is safe to assume
	 * rectangular arrays (arrays where all the rows are the same length). If
	 * sharksBreed or sharksStarve are less than zero a helpful error message is
	 * printed out and -2 is returned. If randGen is null then a helpful error
	 * message is printed out and -3 is returned.
	 * 
	 * Algorithm to move a shark: for each shark that has not moved this chronon
	 * check to see if the shark has starved, if so call sharkStarves otherwise get
	 * the available positions of neighboring fish (call fishPositions) if there are
	 * no neighboring fish to eat then determine available positions (call
	 * unoccupiedPositions) choose a move (call chooseMove) and based on the move
	 * chosen call sharkStays, sharkMoves or sharkMovesAndBreeds appropriately,
	 * using the sharkBreed parameter to see if a shark breeds. else if there are
	 * neighboring fish then choose the move (call chooseMove), eat the fish (call
	 * sharkEatsFish or sharkEatsFishAndBreeds) appropriately. return 0, meaning
	 * success.
	 * 
	 * @param fish
	 *            The array containing all the ages of all the fish.
	 * @param sharks
	 *            The array containing all the ages of all the sharks.
	 * @param fishMove
	 *            The array containing the indicator of whether each fish moved this
	 *            chronon.
	 * @param sharksMove
	 *            The array containing the indicator of whether each shark moved
	 *            this chronon.
	 * @param sharksBreed
	 *            The age the sharks must be in order to breed.
	 * @param starve
	 *            The array containing the time in chronon since the sharks last
	 *            ate.
	 * @param sharksStarve
	 *            The time in chronon since the sharks last ate that results in them
	 *            starving to death.
	 * @param randGen
	 *            The instance of the Random number generator.
	 * @return -1, -2, -3 for invalid parameters as specified above. After
	 *         attempting to move all sharks 0 is returned indicating success.
	 */
	public static int sharksHuntAndBreed(int[][] fish, int[][] sharks, boolean[][] fishMove, boolean[][] sharksMove,
			int sharksBreed, int[][] starve, int sharksStarve, Random randGen) {
		try {
			// minimum of all array columns and rows; checks for any null array.
			int min = Math.min(
					Math.min(Math.min(fishMove.length, fishMove[0].length),
							(Math.min(Math.min(fish.length, fish[0].length),
									Math.min(sharks.length, sharks[0].length)))),
					(Math.min(Math.min(sharksMove.length, sharksMove[0].length),
							Math.min(starve.length, starve[0].length))));

			if (min == 0) {
				throw new NullPointerException(
						"sharksHuntAndBreed Invalid fish array: " + "Null or not at least 1 in each dimension.");
			}
			if (randGen == null) {
				throw new IllegalArgumentException("randGen must not be null");
			}
			if (sharksBreed < 0) {
				throw new Exception("fishBreed must be non-negative");
			}
			/*
			 * if a shark is there, hasn't moved, and isn't going to starve in this chronon
			 * then try to find any fish near it, otherwise try to find an unoccupied
			 * position to move to.
			 */
			ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
			ArrayList<int[]> possibleFish = new ArrayList<int[]>();
			int[] newMove = new int[2];
			for (int i = 0; i < sharks.length; ++i) {
				for (int j = 0; j < sharks[i].length; ++j) {
					Arrays.fill(newMove, 0);

					if (!sharksMove[i][j] && sharks[i][j] >= 0 && !fishMove[i][j]) {

						if (starve[i][j] >= sharksStarve) {
							sharkStarves(sharks, sharksMove, starve, i, j);
						}

						else {
							possibleFish = fishPositions(fish, i, j);

							if (!possibleFish.isEmpty()) {
								newMove = chooseMove(possibleFish, randGen);
								if (sharks[i][j] >= sharksBreed) {
									sharkEatsFishAndBreeds(sharks, sharksMove, starve, fish, fishMove, i, j, newMove[0],
											newMove[1]);
								} else {
									sharkEatsFish(sharks, sharksMove, starve, fish, fishMove, i, j, newMove[0],
											newMove[1]);
								}

							}

							else {
								possibleMoves = unoccupiedPositions(fish, sharks, i, j);

								if (possibleMoves.isEmpty()) {
									sharkStays(sharks, sharksMove, starve, i, j);
								}

								else {
									newMove = chooseMove(possibleMoves, randGen);
									if (sharks[i][j] >= sharksBreed) {
										sharkMovesAndBreeds(sharks, sharksMove, starve, i, j, newMove[0], newMove[1]);
									} else {
										sharkMoves(sharks, sharksMove, starve, i, j, newMove[0], newMove[1]);
									}

								}
							}
						}
					}
					possibleMoves.clear();
					possibleFish.clear();
				}
			}

			return 0;
		} catch (NullPointerException excpt) {
			System.out.print(excpt.getMessage());
			return -1;
		} catch (IllegalArgumentException excpt2) {
			System.out.print(excpt2.getMessage());
			return -3;
		} catch (Exception excpt1) {
			System.out.print(excpt1.getMessage());
			return -2;
		} catch (Throwable thrwObj) {
			System.out.print("Exception: program terminated");
			return -4;
		}

	}

	/**
	 * This looks up the specified paramName in this Config.SIM_PARAMS array,
	 * ignoring case. If found then the array index is returned.
	 * 
	 * @param paramName
	 *            The parameter name to look for, ignoring case.
	 * @return The index of the parameter name if found, otherwise returns -1.
	 */
	public static int indexForParam(String paramName) {
		for (int i = 0; i < Config.SIM_PARAMS.length; i++) {
			if (paramName.equalsIgnoreCase(Config.SIM_PARAMS[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Writes the simulationParameters to the file named filename. The format of the
	 * file is the name of the parameter and value on one line separated by =. The
	 * order of the lines does not matter. Algorithm: Open the file named filename
	 * for writing. Any IOExceptions should be handled with a throws clause and not
	 * a try-catch block. For each of the simulation parameters whose names are
	 * found in Config.SIM_PARAMS Write out the name of the parameter, =, the
	 * parameter value and then newline. Close the file.
	 * 
	 * Example contents of file: seed=233 ocean_width=20 ocean_height=10
	 * starting_fish=100 starting_sharks=10 fish_breed=3 sharks_breed=10
	 * sharks_starve=4
	 * 
	 * @param simulationParameters
	 *            The values of the parameters to write out.
	 * @param filename
	 *            The name of the file to write the parameters to.
	 */
	public static void saveSimulationParameters(int[] simulationParameters, String filename) throws IOException {
		FileOutputStream fileOutStream = new FileOutputStream(filename);
		PrintWriter outStream = new PrintWriter(fileOutStream);
		for (int i = 0; i < simulationParameters.length; ++i) {
			int parameterInt = simulationParameters[i];
			String parameter = Config.SIM_PARAMS[i];
			outStream.println("" + parameter + "=" + parameterInt);

		}
		outStream.flush();
		outStream.close();
		fileOutStream.close();

	}

	/**
	 * This loads the simulation parameters from the file named filename. The names
	 * of the parameters are in the Config.SIM_PARAMS array and the array returned
	 * from this method is a parallel array containing the parameter values. The
	 * name corresponds to the value with the same index. Algorithm: Try to open
	 * filename for reading. If the FileNotFoundException is thrown print the
	 * message printing out the filename without < > and return null;
	 * 
	 * File not found: <filename>
	 * 
	 * Read lines from the file as long as each line contains "=". As soon as a line
	 * does not contain "=" then stop reading from the file. The order of the lines
	 * in the file is not significant. In a line the part before "=" is the name and
	 * the part after is the value. The separate method you wrote in P7 is helpful
	 * here. Find the index of the name within Config.SIM_PARAMS (call
	 * indexForParam). If the index is found then convert the value into an int and
	 * store in the corresponding index in the array of int that will be returned
	 * from this method. If the index is not found then print out the message
	 * followed by the entire line without the < >.
	 * 
	 * Unrecognized: <line>
	 * 
	 * @param filename
	 *            The name of the from which to read simulation parameters.
	 * @return The array of parameters.
	 */
	public static int[] loadSimulationParameters(String filename) {
		int check;
		int[] params = new int[Config.SIM_PARAMS.length];
		int equalsFound;
		int parameterValue;
		try {
			FileInputStream fileInStream = new FileInputStream(filename);
			Scanner fileInScanner = new Scanner(fileInStream);
			for (int i = 0; i < Config.SIM_PARAMS.length; ++i) {
				if (fileInScanner.hasNext()) {
					String nextFileLine = fileInScanner.next();
					equalsFound = nextFileLine.indexOf("=");
					if (equalsFound == -1) {
						break;
					}
					check = 0;
					String parameterString = nextFileLine.substring(0, equalsFound);
					for (i = 0; i < Config.SIM_PARAMS.length; ++i) {
						if (Config.SIM_PARAMS[i].equals(parameterString)) {
							parameterValue = Integer.parseInt(nextFileLine.substring(equalsFound + 1));
							params[i] = parameterValue;
							check = 1;
							break;
						}
					}
					if (check == 0) {
						System.out.print("Unrecognized: " + parameterString);
					}

				} else {
					break;
				}
			}
			fileInScanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename);
			return null;
		}

		return params;
	}

	/**
	 * This writes the simulation parameters and the chart of the simulation to a
	 * file. If simulationParameters is null or history is null then print an error
	 * message and leave the method before any output. If filename cannot be written
	 * to then this method should throw an IOException. *
	 * 
	 * Parameters are written first, 1 per line in the file. Use an = to separate
	 * the name from the value. Then write a blank line and then the Population
	 * Chart. Example file contents are: seed=111 ocean_width=5 ocean_height=2
	 * starting_fish=6 starting_sharks=2 fish_breed=3 sharks_breed=3 sharks_starve=3
	 * 
	 * Population Chart Numbers of fish(.) and sharks(O) in units of 1. F 6,S 2
	 * 1)OO.... F 4,S 2 2)OO.. F 2,S 4 3)..OO F 1,S 4 4).OOO F 0,S 4 5)OOOO
	 * 
	 * Looking at one line in detail F 6,S 2 1)OO.... ^^^^^^ 6 fish (the larger of
	 * sharks or fish is in the background) ^^ 2 sharks ^^^^^ chronon 1 ^^^^ the
	 * number of sharks ^^^^ the number of fish
	 * 
	 * The unit size is determined by dividing the maximum possible number of a
	 * creature (oceanWidth * oceanHeight) by Config.POPULATION_CHART_WIDTH. Then
	 * iterate through the history printing out the number of fish and sharks.
	 * PrintWriter has a printf method that is helpful for formatting.
	 * printf("F%3d", 5) prints "F 5", a 5 right justified in a field of size 3.
	 * 
	 * @param simulationParameters
	 *            The array of simulation parameter values.
	 * @param history
	 *            The ArrayList containing the number of fish and number of sharks
	 *            at each chronon.
	 * @param oceanWidth
	 *            The width of the ocean.
	 * @param oceanHeight
	 *            The height of the ocean.
	 * @param filename
	 *            The name of the file to write the parameters and chart to.
	 */
	public static void savePopulationChart(int[] simulationParameters, ArrayList<int[]> history, int oceanWidth,
			int oceanHeight, String filename) throws IOException {
		if (simulationParameters == null || history == null) {
			System.err.println("Simulation Parameters or History is null");
			return;
		}

		FileOutputStream fileOutStream = new FileOutputStream(filename);
		PrintWriter outStream = new PrintWriter(fileOutStream);

		for (int i = 0; i < Config.SIM_PARAMS.length; ++i) {
			outStream.println("" + Config.SIM_PARAMS[i] + "=" + simulationParameters[i]);
		}
		outStream.println();
		double unitSize = ((oceanWidth * oceanHeight) / Config.POPULATION_CHART_WIDTH);
		outStream.println("Population Chart\nNumbers of fish(" + Config.FISH_MARK + ") and sharks(" + Config.SHARK_MARK
				+ ") in units of " + (int)unitSize + ".");

		for (int[] currentHistory : history) {
			int chronon = currentHistory[Config.HISTORY_CHRONON_INDEX];
			int numberFish = currentHistory[Config.HISTORY_NUM_FISH_INDEX];
			int numberSharks = currentHistory[Config.HISTORY_NUM_SHARKS_INDEX];
			double fishUnitD = Math.ceil(numberFish / unitSize);
			double sharkUnitD = Math.ceil(numberSharks/ unitSize);
			int fishUnit = (int) fishUnitD;
			int sharkUnit = (int) sharkUnitD;
			outStream.printf("F%3d,S%3d%5d)", numberFish, numberSharks, chronon);
			if (fishUnit < sharkUnit) {
				int formatWidth = (Config.POPULATION_CHART_WIDTH - sharkUnit);
				String format = "%-" + formatWidth + "s";
				for (int j = 0; j < fishUnit; ++j) {
					outStream.print(Config.FISH_MARK);
				}
				for (int k = fishUnit; k < sharkUnit; ++k) {
					outStream.print(Config.SHARK_MARK);
				}
				outStream.printf(format, " ");

			} else {
				int formatWidth = (Config.POPULATION_CHART_WIDTH - fishUnit);
				String format = "%-" + formatWidth + "s";
				for (int j = 0; j < sharkUnit; ++j) {
					outStream.print(Config.SHARK_MARK);
				}
				for (int k = sharkUnit; k < fishUnit; ++k) {
					outStream.print(Config.FISH_MARK);
				}
				outStream.printf(format, " ");
			}
			outStream.println();
		}

		outStream.close();
	}
}
