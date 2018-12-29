
/* This file includes java source code to run Wa-Tor which

* is a population dynamics simulation devised by Alexander Keewatin Dewdney[1] and 
* presented in the December 1984 issue of Scientific American in a 5-page article 
* entitled "Computer Recreations: Sharks and fish wage an ecological war on the toroidal 
* planet Wa-Tor".
//*

//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title:           (Test Wa-Tor)
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

/**
 * This file contains testing methods for the WaTor project.
 * These methods are intended to serve several objectives:
 * 1) provide an example of a way to incrementally test your code
 * 2) provide example method calls for the WaTor methods
 * 3) provide examples of creating, accessing and modifying arrays
 *    
 * Toward these objectives, the expectation is that part of the 
 * grade for the WaTor project is to write some tests and
 * write header comments summarizing the tests that have been written. 
 * Specific places are noted with TODO but add any other comments 
 * you feel would be useful.
 * 
 * Some of the provided comments within this file explain
 * Java code as they are intended to help you learn Java.  However,
 * your comments and comments in professional code, should
 * summarize the purpose of the code, not explain the meaning
 * of the specific Java constructs.
 *    
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.io.*;
import java.util.Scanner;

/**
 * This class contains a few methods for testing methods in the WaTor class as
 * they are developed. These methods are all private as they are only intended
 * for use within this class.
 * 
 * @author Jim Williams
 * @author Luke Sprangers
 *
 */
public class TestWaTor {

	/**
	 * This is the main method that runs the various tests. Uncomment the tests when
	 * you are ready for them to run.
	 * 
	 * @param args
	 *            (unused)
	 */
	public static void main(String[] args) {

		// milestone 1
		// testClearMoves();
		// testEmptyArray();
		// testCountCreatures(); passed 4/8

		// The best way to test the following is probably to compare
		// output with the examples.
		// testShowFishAndSharks(); //passed 4/8
		// testplaceFish(); // passed
		// placeSharks(); // passed with placeFish

		// milestone 2
		// testUnoccupiedPositions(); //passed 4/9
		// testChooseMove(); //passed 4/10
		// testFishPositions(); //passed 4/10
		// testFishSwimAndBreed(); //passed 4/12
		// testSharksHuntAndBreed(); //passed 4/12
		// by comparing output to examples.

		// milestone 3
		// testSaveSimulationParameters(); //passed
		testSavePopulationChart(); // passed
	}

	/**
	 * Compares the lists to see if they are the same size and contain the same
	 * elements.
	 * 
	 * @param list1
	 *            One list of coordinates.
	 * @param list2
	 *            Another list of coordinates
	 * @return Whether the lists contain the same coordinates or not.
	 */
	private static boolean matchingArrayLists(ArrayList<int[]> list1, ArrayList<int[]> list2) {
		boolean result = true;
		if (list1.size() != list2.size()) {
			System.err.println("list1 size: " + list1.size() + " list2 size:" + list2.size() + " should be the same");
			result = false;
			return result;
		}
		for (int i = 0; i < list1.size(); i++) {
			int[] move1 = list1.get(i);
			int[] move2 = list2.get(i);
			if (move1[0] == move2[0] && move1[1] == move2[1]) {
				// ok
			} else {
				result = false;
				System.err.println("list1(" + i + "):" + Arrays.toString(move1) + " doesn't match in list2: "
						+ Arrays.toString(move2));

				break;
			}
		}
		return result;
	}

	/**
	 * This method tests the WaTor method unoccupiedPositions by creating a 3x3 game
	 * board with a fish in location 1,1 and no sharks. It then calls unoccupied
	 * Positions and expects it to return 4 coordinate values up, down, left, and
	 * right of 1,1. It reruns the unoccupiedPositions method 2 more times with that
	 * board, and then creates a new board and repeats the entire process giving a
	 * final pass/fail message with which ones failed.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testUnoccupiedPositions() {
		boolean error = false;

		int[][] fish = new int[][] { { -1, -1, -1 }, { -1, 0, -1 }, { -1, -1, -1 } };
		int[][] sharks = new int[][] { { -1, -1, -1 }, { -1, -1, -1 }, { -1, -1, -1 } };

		ArrayList<int[]> positions = WaTor.unoccupiedPositions(fish, sharks, 1, 1);
		ArrayList<int[]> expected = new ArrayList<>();
		expected.add(new int[] { 0, 1 });
		expected.add(new int[] { 2, 1 });
		expected.add(new int[] { 1, 0 });
		expected.add(new int[] { 1, 2 });
		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testUnoccupiedPositions 1 :");
		}

		positions = WaTor.unoccupiedPositions(fish, sharks, 0, 1);
		expected = new ArrayList<>();
		expected.add(new int[] { 2, 1 });
		expected.add(new int[] { 0, 0 });
		expected.add(new int[] { 0, 2 });
		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testUnoccupiedPositions 2 :");
		}

		positions = WaTor.unoccupiedPositions(fish, sharks, 0, 0);
		expected = new ArrayList<>();
		expected.add(new int[] { 2, 0 });
		expected.add(new int[] { 1, 0 });
		expected.add(new int[] { 0, 2 });
		expected.add(new int[] { 0, 1 });
		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testUnoccupiedPositions 3 :");
		}

		fish = new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
		sharks = new int[][] { { -1, -1, -1 }, { -1, -1, -1 }, { -1, -1, -1 } };

		positions = WaTor.unoccupiedPositions(fish, sharks, 1, 1);
		expected = new ArrayList<>();

		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testUnoccupiedPositions 4 :");
		}

		positions = WaTor.unoccupiedPositions(fish, sharks, 0, 1);
		expected = new ArrayList<>();

		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testUnoccupiedPositions 5 :");
		}

		positions = WaTor.unoccupiedPositions(fish, sharks, 0, 0);
		expected = new ArrayList<>();

		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testUnoccupiedPositions 6 :");
		}
		fish = new int[][] { { 0, 0, 0 }, { -1, 0, 0 }, { 0, 0, 0 } };
		sharks = new int[][] { { -1, -1, -1 }, { -1, -1, -1 }, { -1, -1, -1 } };

		positions = WaTor.unoccupiedPositions(fish, sharks, 1, 1);
		expected = new ArrayList<>();
		expected.add(new int[] { 1, 0 });

		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testUnoccupiedPositions 7 :");
		}

		positions = WaTor.unoccupiedPositions(fish, sharks, 0, 1);
		expected = new ArrayList<>();
		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testUnoccupiedPositions 8 :");
		}

		positions = WaTor.unoccupiedPositions(fish, sharks, 0, 0);
		expected = new ArrayList<>();
		expected.add(new int[] { 1, 0 });
		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testUnoccupiedPositions 9 :");
		}

		if (error) {
			System.err.println("testUnoccupiedPositions failed");
		} else {
			System.out.println("testUnoccupiedPositions passed");
		}
	}

	/**
	 * This runs some tests on the fishPositions method. 1. This testing method
	 * creates a 3x3 grid and places fish on various spots. After placing the fish
	 * it then calls WaTor.fishPositions and compares the results from there to the
	 * ones it created and then tells you if the fishPositions method returned the
	 * correct results or not. It does this process 3 times.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testFishPositions() {
		boolean error = false;
		// fish @ (0,1) , (1,1) , (1,2), (2,0)
		// 0 1 2
		int[][] fish = new int[][] { { -1, 1, 2 }, // 0
				{ -1, 0, 3 }, // 1
				{ -1, 1, -1 } }; // 2

		ArrayList<int[]> positions = WaTor.fishPositions(fish, 1, 1);
		ArrayList<int[]> expected = new ArrayList<>();
		expected.add(new int[] { 0, 1 });
		expected.add(new int[] { 2, 1 });
		expected.add(new int[] { 1, 2 });
		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testFishPositions 1 :");
		}

		positions = WaTor.fishPositions(fish, 0, 1);
		expected = new ArrayList<>();
		expected.add(new int[] { 2, 1 });
		expected.add(new int[] { 1, 1 });
		expected.add(new int[] { 0, 2 });

		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testFishPositions 2 :");
		}

		// @ (1,0) , (2,1) , (0,1)
		// 0 1 2
		fish = new int[][] { { 0, 0, 0 }, // 0
				{ 2, -1, -1 }, // 1
				{ -1, 3, -1 } }; // 2
		expected = new ArrayList<>();
		positions = WaTor.fishPositions(fish, 1, 1);
		expected.add(new int[] { 0, 1 });
		expected.add(new int[] { 2, 1 });
		expected.add(new int[] { 1, 0 });

		if (!matchingArrayLists(expected, positions)) {
			error = true;
			System.err.println("testFishPositions 3 :");
		}

		if (error) {
			System.err.println("testFishPositions failed");
		} else {
			System.out.println("testFishPositions passed");
		}
	}

	/**
	 * This runs some tests on the chooseMove method. 1. This method test goes
	 * through the choose move method to determine if it returns null correctly. 2.
	 * Then it goes through to make sure that choose moves works for 1-4 moves. 3.
	 * Finally it lists if all of your tests have passed or not.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testChooseMove() {
		boolean error = false;
		Random randGen = new Random();
		randGen.setSeed(456);

		ArrayList<int[]> input = new ArrayList<>();
		int[] expected = null;
		int[] result = WaTor.chooseMove(input, randGen);
		if (result != expected) {
			error = true;
			System.err.println("testChooseMove 0: result not null");
		}

		input.clear();
		int[] oneMove = new int[] { 0, 1 };
		input.add(oneMove);
		expected = oneMove;
		result = WaTor.chooseMove(input, randGen);
		if (result != expected) {
			error = true;
			System.err.println("testChooseMove 1: result not " + Arrays.toString(oneMove));
		}

		input.clear();
		int[] move1 = new int[] { 0, 1 };
		int[] move2 = new int[] { 1, 0 };
		input.add(move1);
		input.add(move2);
		int move1Count = 0;
		int move2Count = 0;
		int numTrials = 1000;
		for (int i = 0; i < numTrials; i++) {
			result = WaTor.chooseMove(input, randGen);
			if (result == move1)
				move1Count++;
			else if (result == move2)
				move2Count++;
		}
		if (move1Count != 513 || move2Count != 487) {
			error = true;
			System.err.println(
					"testChooseMove 2: expected 513,487 move1Count=" + move1Count + " move2Count=" + move2Count);
		}

		input.clear();
		move1 = new int[] { 0, 1 };
		move2 = new int[] { 1, 0 };
		int[] move3 = new int[] { 2, 1 };
		input.add(move1);
		input.add(move2);
		input.add(move3);
		move1Count = 0;
		move2Count = 0;
		int move3Count = 0;
		numTrials = 1000;
		for (int i = 0; i < numTrials; i++) {
			result = WaTor.chooseMove(input, randGen);
			if (result == move1)
				move1Count++;
			else if (result == move2)
				move2Count++;
			else if (result == move3)
				move3Count++;
		}
		if (move1Count != 325 || move2Count != 341 || move3Count != 334) {
			error = true;
			System.err.println("testChooseMove 3: expected 325,341,334 move1Count=" + move1Count + " move2Count="
					+ move2Count + " move3Count=" + move3Count);
		}

		input.clear();
		move1 = new int[] { 0, 1 };
		move2 = new int[] { 1, 0 };
		move3 = new int[] { 2, 1 };
		int[] move4 = new int[] { 1, 2 };
		input.add(move1);
		input.add(move2);
		input.add(move3);
		input.add(move4);
		move1Count = 0;
		move2Count = 0;
		move3Count = 0;
		int move4Count = 0;
		numTrials = 1000;
		for (int i = 0; i < numTrials; i++) {
			result = WaTor.chooseMove(input, randGen);
			if (result == move1)
				move1Count++;
			else if (result == move2)
				move2Count++;
			else if (result == move3)
				move3Count++;
			else if (result == move4)
				move4Count++;
		}
		if (move1Count != 273 || move2Count != 231 || move3Count != 234 || move4Count != 262) {
			error = true;
			System.err.println("testChooseMove 4: expected 325,341,334,262 move1Count=" + move1Count + " move2Count="
					+ move2Count + " move3Count=" + move3Count + " move4Count=" + move4Count);
		}

		if (error) {
			System.err.println("testChooseMove failed");
		} else {
			System.out.println("testChooseMove passed");
		}
	}

	/**
	 * This runs some tests on the clearMoves method. 1.This test method creates a
	 * boolean 2d grid where all (x,y) coordinates are true 2. It executes the WaTor
	 * clear moves method 3. It then goes through each coordinate, and if one is
	 * true then clear moves method has failed and it prints an error message,
	 * otherwise it prints that it has passed.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testClearMoves() {
		boolean error = false;
		boolean[][] moves = new boolean[4][9];
		for (int row = 0; row < moves.length; row++) {
			for (int col = 0; col < moves[row].length; col++) {
				moves[row][col] = true;
			}
		}
		WaTor.clearMoves(moves);
		for (int row = 0; row < moves.length; row++) {
			for (int col = 0; col < moves[row].length; col++) {
				if (moves[row][col]) {
					System.err.println("testClearMoves 0: move " + row + "," + col + " not false");
					error = true;
					break;
				}
			}
		}
		if (error) {
			System.err.println("testClearMoves failed");
		} else {
			System.out.println("testClearMoves passed");
		}
	}

	/**
	 * This runs some tests on the clearMoves method. 1.This test method creates a
	 * boolean 2d grid where all (x,y) coordinates are not empty 2. It executes the
	 * WaTor empty array method 3. It then goes through each coordinate, and if one
	 * is not empty then empty arrays method has failed and it prints an error
	 * message, otherwise it prints that it has passed.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testEmptyArray() {
		boolean error = false;
		int[][] moves = new int[100][99];
		for (int row = 0; row < moves.length; row++) {
			for (int col = 0; col < moves[row].length; col++) {
				moves[row][col] = Config.EMPTY + 2; // make sure not EMPTY
			}
		}
		WaTor.emptyArray(moves);
		for (int row = 0; row < moves.length; row++) {
			for (int col = 0; col < moves[row].length; col++) {
				if (moves[row][col] != Config.EMPTY) {
					System.err.println("testEmptyArray 0: move " + row + "," + col + " not EMPTY");
					error = true;
					break;
				}
			}
		}
		if (error) {
			System.err.println("testEmptyArray failed");
		} else {
			System.out.println("testEmptyArray passed");
		}
	}

	/**
	 * This runs some tests on the countFish method. 1. This method tests the
	 * WaTor.countCreatures method by creating a 7x3 grid and placing various fish
	 * onto it, then calls countCreatures and compares the returned value with what
	 * is expected.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testCountCreatures() {
		boolean error = false;

		int[][] fish = new int[7][3];
		WaTor.emptyArray(fish);
		fish[0][0] = 1;
		fish[6][2] = 2;
		fish[0][2] = 3;
		fish[6][0] = 4;
		fish[3][1] = 5;
		int result = WaTor.countCreatures(fish);
		if (result != 5) {
			System.err.println("testCountCreatures 0: expected 5 found " + result);
			error = true;
		}

		if (error) {
			System.err.println("testCountCreatures failed");
		} else {
			System.out.println("testCountCreatures passed");
		}
	}

	/**
	 * This runs some tests on the showFishAndSharks method. 1. This method tests
	 * the WaTor.showFishAndSharks method by creating a 7x7 grid and placing various
	 * fish/sharks onto it, calls showFishAndSharks, and compares the returned value
	 * with what is expected.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testShowFishAndSharks() {
		int[][] fish = new int[7][7];
		int[][] shark = new int[7][7];
		for (int row = 0; row < fish.length; row++) {
			for (int col = 0; col < fish[row].length; col++) {
				fish[row][col] = Config.EMPTY; // make sure not EMPTY
				shark[row][col] = Config.EMPTY;
			}
		}
		fish[2][2] = 1;
		fish[0][4] = 1;
		fish[2][2] = 1;
		fish[1][1] = 3;
		fish[6][1] = 6;
		fish[3][5] = 6;
		fish[5][3] = 6;
		fish[6][6] = 4;
		fish[1][1] = 6;
		shark[4][1] = 2;
		shark[1][2] = 4;
		shark[5][2] = 1;
		shark[5][0] = 1;
		shark[3][6] = 1;
		shark[2][5] = 1;
		shark[0][3] = 1;
		shark[5][6] = 1;
		shark[0][0] = 1;
		WaTor.showFishAndSharks(1, fish, shark);

	}

	/**
	 * This runs some tests on the placeFish method. 1. This method tests the
	 * WaTor.placeFish method by creating a x by y grid and placing various
	 * fish/sharks onto it, calls placeFish with different parameters, and prints
	 * the returned values. It does this 3 times.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testplaceFish() {
		// should be 153 fish and 1 shark
		int x = 10;
		int y = 40;
		int[][] fish = new int[x][y];
		int[][] sharks = new int[x][y];
		int startingFish = 200;
		int fishBreed = 5;
		int startingSharks = 5;
		int sharksBreed = 10;
		int sharksStarve = 10;
		Random randGen = new Random(934);

		for (int row = 0; row < fish.length; row++) {
			for (int col = 0; col < fish[row].length; col++) {
				fish[row][col] = Config.EMPTY; // make sure not EMPTY
				sharks[row][col] = Config.EMPTY;
			}
		}

		WaTor.placeFish(fish, startingFish, fishBreed, randGen);
		WaTor.placeSharks(fish, sharks, startingSharks, sharksBreed, randGen);
		WaTor.showFishAndSharks(1, fish, sharks);

		x = 2;
		y = 5;
		int[][] fish1 = new int[x][y];
		int[][] sharks1 = new int[x][y];
		startingFish = 6;
		fishBreed = 3;
		startingSharks = 1;
		sharksBreed = 10;
		sharksStarve = 2;
		randGen = new Random(123);

		for (int row = 0; row < fish1.length; row++) {
			for (int col = 0; col < fish1[row].length; col++) {
				fish1[row][col] = Config.EMPTY; // make sure not EMPTY
				sharks1[row][col] = Config.EMPTY;
			}
		}
		System.out.println();
		// should be 6 fish, 1 shark
		WaTor.placeFish(fish1, startingFish, fishBreed, randGen);
		WaTor.placeSharks(fish1, sharks1, startingSharks, sharksBreed, randGen);
		WaTor.showFishAndSharks(1, fish1, sharks1);

		x = 20;
		y = 20;
		int[][] fish2 = new int[x][y];
		int[][] sharks2 = new int[x][y];
		startingFish = 1;
		fishBreed = 2;
		startingSharks = 1;
		sharksBreed = 10;
		sharksStarve = 11;
		randGen = new Random(234);

		for (int row = 0; row < fish2.length; row++) {
			for (int col = 0; col < fish2[row].length; col++) {
				fish2[row][col] = Config.EMPTY; // make sure not EMPTY
				sharks2[row][col] = Config.EMPTY;
			}
		}
		System.out.println();
		// should be 1 fish, 1 shark
		WaTor.placeFish(fish2, startingFish, fishBreed, randGen);
		WaTor.placeSharks(fish2, sharks2, startingSharks, sharksBreed, randGen);
		WaTor.showFishAndSharks(1, fish2, sharks2);

	}

	/**
	 * This runs some tests on the FishSwimAndBreed method. 1. This method tests the
	 * WaTor.FishSwimAndBreed method by creating a 3x3 grid and placing various
	 * fish/sharks onto it, calls FishSwimAndBreed with different parameters, and
	 * compares the returned value with what is expected. It does this twice.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testFishSwimAndBreed() {
		boolean error = false;
		Random randGen = new Random();
		randGen.setSeed(456);
		int x = 3;
		int y = 3;
		int[][] fish = new int[x][y];
		int[][] sharks = new int[x][y];
		boolean[][] fishMove = new boolean[x][y];
		int fishBreed = 3;

		ArrayList<int[]> input = new ArrayList<>();
		int[] expected = null;
		int result = WaTor.fishSwimAndBreed(fish, sharks, fishMove, fishBreed, randGen);
		if (result != 0) {
			error = true;
			System.err.println("testChooseMove 0: result not 0");
		}

		input.clear();
		int[] oneMove = new int[] { 0, 1 };

		input.add(oneMove);
		expected = oneMove;
		result = WaTor.fishSwimAndBreed(fish, sharks, fishMove, fishBreed, randGen);
		if (result != 0) {
			error = true;
			System.err.println("testChooseMove 1: result not " + Arrays.toString(oneMove));
			WaTor.showFishAndSharks(1, fish, sharks);
		}

		if (error) {
			System.err.println("testChooseMove failed");
		} else {
			System.out.println("testChooseMove passed");
		}
	}

	/**
	 * This runs some tests on the SharksHuntAndBreed method. 1. This method tests
	 * the WaTor.SharksHuntAndBreed method by creating a 7x4 grid and placing
	 * various fish/sharks onto it, calls SharksHuntAndBreed with different
	 * parameters, and compares the returned value with what is expected. It does
	 * this twice.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testSharksHuntAndBreed() {
		int sharksStarve = 7;
		int sharksBreed = 4;
		Random randGen = new Random(1);
		boolean error = false;
		int x = 3;
		int[][] starve = new int[x][x];
		int[][] fish = new int[x][x];
		int[][] fish1 = null;
		int[][] shark = new int[x][x];
		boolean[][] fishMove = new boolean[x][x];
		boolean[][] sharkMove = new boolean[x][x];
		for (int row = 0; row < fish.length; row++) {
			for (int col = 0; col < fish[row].length; col++) {
				fish[row][col] = Config.EMPTY; // make sure not EMPTY
				shark[row][col] = Config.EMPTY;
				fishMove[row][col] = false;
			}
		}

		fish[0][1] = 1;
		fish[2][0] = 1;
		// fish[6][1] = 6;
		// fish[3][5] = 6;
		// fish[5][3] = 6;
		// fish[6][6] = 4;
		// fishMove[0][4] = true;

		fishMove[0][1] = true;
		fishMove[2][0] = true;
		// fishMove[6][1] = true;
		// fishMove[3][5] = true;
		// fishMove[5][3] = true;
		// fishMove[6][6] = true;
		// shark[4][1] = 2;
		// shark[0][2] = 6;
		shark[1][1] = 3;
		// shark[1][2] = 1;
		sharkMove[1][1] = true;
		// sharkMove[1][2] = true;
		// sharkMove[0][2] = true;
		// starve[0][2] = 7;
		starve[1][1] = 1;
		// starve[1][2] = 2;
		// shark[5][2] = 1;
		// shark[5][0] = 1;
		// shark[3][6] = 1;
		// shark[2][5] = 1;
		// shark[2][0] = 1;
		// shark[5][6] = 1;
		// shark[0][0] = 1;
		WaTor.showFishAndSharks(6, fish, shark);
		System.out.println(
				WaTor.sharksHuntAndBreed(fish, shark, fishMove, sharkMove, sharksBreed, starve, sharksStarve, randGen));
		// System.out.println(WaTor.fishSwimAndBreed(fish1, shark, fishMove, 2,
		// randGen));
		for (int row = 0; row < fish.length; row++) {
			for (int col = 0; col < fish[row].length; col++) {
				System.out.print(fish[row][col] + " ");
			}
			System.out.println();
		}
		System.out.println();
		for (int row = 0; row < shark.length; row++) {
			for (int col = 0; col < shark[row].length; col++) {
				System.out.print(shark[row][col] + " ");
			}
			System.out.println();
		}
		System.out.println();
		WaTor.showFishAndSharks(6, fish, shark);
	}

	/**
	 * This tests the saveSimulationParameters method in WaTor by creating two
	 * unique, and different sized, arrays which are passed into the method.
	 * Afterwards the task is checked by comparing output to expected output.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testSaveSimulationParameters() {
		int[] simulationParameters = new int[Config.SIM_PARAMS.length];
		int[] simulationParameters2 = new int[Config.SIM_PARAMS.length - 1];
		int[] simulationParameters11 = new int[Config.SIM_PARAMS.length];
		int[] simulationParameters21 = new int[Config.SIM_PARAMS.length - 1];
		for (int i = 0; i < simulationParameters.length; ++i) {
			simulationParameters[i] = 68;
		}
		for (int i = 0; i < simulationParameters2.length; ++i) {
			simulationParameters2[i] = 69;
		}
		// String fileName = "WaTor saveSimulationParameters.rtf";
		String fileName = "fileName";
		String fileName2 = "fileName2";
		try {
			WaTor.saveSimulationParameters(simulationParameters, fileName); // passed
			WaTor.saveSimulationParameters(simulationParameters2, fileName2); // passed
			for (int i = 0; i < simulationParameters.length; ++i) {
				System.out.println("Entered simulation Parameters 1: " + simulationParameters[i]);
			}
			System.out.println("\n");
			for (int i = 0; i < simulationParameters2.length; ++i) {
				System.out.println("Entered simulation Parameters 2: " + simulationParameters2[i]);
			}

			System.out.println("\n");
			simulationParameters11 = WaTor.loadSimulationParameters(fileName);
			simulationParameters21 = WaTor.loadSimulationParameters(fileName2);

			for (int i = 0; i < simulationParameters11.length; ++i) {
				System.out.println("Loaded simulation Parameters 1: " + simulationParameters11[i]);
			}
			System.out.println("\n");
			for (int i = 0; i < simulationParameters21.length; ++i) {
				System.out.println("Loaded simulation Parameters 2: " + simulationParameters21[i]);
			}
			System.out.println("\n");
			System.out.println("fileName size: " + simulationParameters.length + " fileName2 size: "
					+ simulationParameters2.length);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		System.out.println("Done");
	}

	/**
	 * This tests the savePopulationChart method in WaTor by creating the needed
	 * parameters which are passed into the method. Afterwards the task is checked
	 * by comparing output to expected output, and by using debugging practices in
	 * the method to print out various results.
	 * 
	 * @author Luke Sprangers
	 */
	private static void testSavePopulationChart() {

		try {

			ArrayList<int[]> history = new ArrayList<int[]>();
			String fileName = "saveChart";
			int[] simulationParameters = new int[Config.SIM_PARAMS.length];

			for (int i = 0; i < simulationParameters.length; ++i) {
				simulationParameters[i] = 30;
			}
			simulationParameters[0] = 34;
			simulationParameters[3] = 56;
			simulationParameters[6] = 100;

			for (int j = 0; j < 50; ++j) {
				int[] currentHistory = new int[3];
				currentHistory[0] = j + 1;
				currentHistory[1] = 12;
				currentHistory[2] = 30;
				history.add(currentHistory);
			}
			int[] newHistory1 = new int[3];
			newHistory1[0] = 1;
			newHistory1[1] = 4;
			newHistory1[2] = 23;
			history.set(0, newHistory1);
			int[] newHistory2 = new int[3];
			newHistory2[0] = 50;
			newHistory2[1] = 16;
			newHistory2[2] = 34;
			history.set(49, newHistory2);
			int[] newHistory3 = new int[3];
			newHistory3[0] = 26;
			newHistory3[1] = 38;
			newHistory3[2] = 7;
			history.set(25, newHistory3);
			int[] newHistory4 = new int[3];
			newHistory4[0] = 37;
			newHistory4[1] = 10;
			newHistory4[2] = 1;
			history.set(36, newHistory4);
			WaTor.savePopulationChart(simulationParameters, history, 10, 20, fileName);

		} catch (IOException e) {
			System.out.println("Error to save pop chart");
		}
	}

}
