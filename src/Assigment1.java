/*=================================================================================================================================
The Maze Solver
Blair McAlpine
October/November 2019
Java 1.8.0
===================================================================================================================================
Problem Definition – Given a text file representing a maze, find the shortest solution to said maze 
Input – 'layout.txt' a text file containing single characters representing either a path, wall, cheese, mouse, or exit in the maze
Output – The solution to the maze, either from the start to the cheese, the cheese to the exit, or both
Process – Using recursion, find each possible pathway to the desired location in the maze. Once all have been found, compare them 
and find the shortest one
===================================================================================================================================
*/ 
import java.io.*;
import java.util.*;
public class Assigment1 {
	/** List of Global Variables
	 * maze - the String array containing the contents of the maze <type String[][]>
	 * 
	 * prevMousePosRow - The old row location of the mouse before moving to the cheese, needed for many calculations including output
	 * 
	 * prevMousePosCol - The old column location of the mouse before moving to the cheese, needed for many calculations including output
	 * 
	 * solutionList - The array in which all of the possible solutions are saved, saved as numbers from
	 * 1 to 4 to represent each direction <type int[][]>
	 * 
	 * firstSolution - The solutionList value of the first solution, initialized to 0 as the first 
	 * solution will be saved to solutionList[0] <type int>
	 *
	 * finalSolution - The solutionList value of the final solution <type String>
	 * 
	 * solutionCount - The total amount of solutions to each step of the maze
	 * 
	 * cheeseFound - A boolean to identify whether or not the mouse has found the cheese
	 */
	public static String[][] maze = new String[8][12];
	static int prevMousePosRow, prevMousePosCol, firstSolution = 0, finalSolution, solutionCount = 0;
	static int[][] solutionList = new int[1000][1000];
	static boolean cheeseFound = false;
	/**main method:
	 * This procedural method is automatically called and to organizes the calling of other methods in the same class
	 * 
	 * List of Local Variables
	 * br - an object used to get access to the BufferedReader class, which reads the file
	 * 
	 * outputMaze - the array of characters that is manipulated for program output, separated from 
	 * the main maze to avoid calculation errors <type String[][]>
	 * 
	 * mouse - an object used to get access to the mouse class, which is used to gain
	 * information on the location and movement of the mouse.
	 * 
	 * totalCheeseSolutions - the total amount of possible solutions to the cheese <type int>
	 *
	 * bestCheeseSolution - the array value of the shortest way to get to the cheese <type int>
	 * 
	 * shortestCheesePath - the path length of the shortest solution to the cheese <type int>
	 * 
	 * totalExitSolutions - the total amount of possible solutions from the cheese to the exit <type int>
	 *
	 * bestExitSolution - the array value of the shortest way to get from the cheese to the exit <type int>
	 * 
	 * shortestExitPath - the path length of the shortest solution from the cheese to the exit <type int>
	 * @param args <type String[]>
	 * @throws Exception - needs to have this general throw to combine FileNotFoundException and IOException into one
	 * @return void
	 */
	public static void main(String[] args) throws Exception {
		// Initializes all required information -> Maze, outputMaze, mouse and br object
		BufferedReader br = new BufferedReader(new FileReader ("C:\\Users\\blair\\Documents\\Assignment1\\mazefile2.txt"));
		String[][]outputMaze = new String[8][12];
		fileRead(0,0, br);
		outputMaze = fillOutputMaze(outputMaze);
		Mouse mouse = new Mouse(maze);
		// Calls the pathFind method (the main recursive method) for the first step of the solution, going from the mouse to the cheese
		pathFind(mouse.getRow(), mouse.getCol(), mouse.getRow(), mouse.getCol(), 0, mouse);
		// Calls the fillEmptySolutions method which fills in any duplicate moves between solutions (ex. if the first moves for 2 solutions are up, up, left)
		fillEmptySolutions();
		// Creates 3 variables to save the information about the solutions - the best one, the length of the best one, and the total number of solutions
		int totalCheeseSolutions = finalSolution - firstSolution;
		int bestCheeseSolution = findShortest(firstSolution, finalSolution-1, false);
		int shortestCheesePath = findShortest(firstSolution, finalSolution-1, true);
		// Calls the pathFindReset method which resets all of the necessary variables that were changed in the first pathFind call, to prepare it for the second step
		pathFindReset(mouse);
		// Calls the pathFind method (the main recursive method) for the second step of the solution, going from the cheese to the exit
		pathFind(mouse.getRow(), mouse.getCol(), mouse.getRow(), mouse.getCol(), 0, mouse);
		// Calls the fillEmptySolutions method which fills in any duplicate moves between solutions (ex. if the first moves for 2 solutions are up, up, left)
		fillEmptySolutions();
		// Creates 3 variables to save the information about the solutions - the best one, the length of the best one, and the total number of solutions
		int totalExitSolutions = finalSolution - firstSolution;
		int bestExitSolution = findShortest(firstSolution, finalSolution-1, false);
		int shortestExitPath = findShortest(firstSolution, finalSolution-1, true);
		// Calls the bestTotalSolution method to combine the two best solutions to create the best solution for the entire maze
		bestTotalSolution(bestCheeseSolution,bestExitSolution,shortestCheesePath);
		// Calls the output method, which relays the information about the various solutionsto the user 
		output(bestCheeseSolution,bestExitSolution,shortestCheesePath,shortestExitPath,totalCheeseSolutions,totalExitSolutions, outputMaze, mouse);
		// Termination line, to confirm the program has ended
		line();
		System.out.println("Program Terminated.");
	}
	/**input method:
	 * This functional method gets the user input in the form of an integer
	 * 
	 * List of local variables
	 * sc - object to access the Scanner class
	 * 
	 * @return int
	 */
	public static int input () {
		Scanner sc = new Scanner (System.in);
		return sc.nextInt();
	}
	/**fileRead method:
	 * This procedural recursive method reads the layout.txt file and uses its contents to fill the maze array 
	 * 
	 * @param row - array row value that is changed as the method recurses <type int>
	 * @param col - array column value that is changed as the method recurses <type int>
	 * @param br - object to access the bufferedReader class, cannot be local otherwise it will reset
	 * each time it recurses <type int>
	 * @throws IOException for the BufferedReader
	 * @return void
	 */
	public static void fileRead (int row, int col, BufferedReader br) throws IOException {
		if (row < maze.length) { // base case, checks if the row value is inside the maze, otherwise the file is done reading
			if (col < maze[row].length) { // checks if column value is inside the maze, otherwise it will move to the next line
				maze [row][col] = String.valueOf((char)br.read());
				br.read();
				fileRead(row,col+1,br); // calls a new fileRead method, telling it to move 1 column to the right
			}
			else {
				br.readLine();
				fileRead(row+1,0,br); // calls a new fileRead method, telling it to move 1 row down and start in the 1st column
			}
		}
	}
	/**CanMoveLeft method:
	 * This functional method checks if the values it is given are within the array.
	 * If it is, it returns the value of the validPath method.
	 * Otherwise, it returns false.
	 * 
	 * @param row - value of the row for the array <type int>
	 * @param col - value of the column for the array <type int>
	 * @return boolean
	 */
	public static boolean CanMoveLeft (int row, int col) {
		if (col-1 >= 0) { // Checks if the column it is given would still be in the array if moved 1 unit left
			return validPath(row,col-1);
		}
		return false;
	}
	/**CanMoveRight method:
	 * This functional method checks if the values it is given are within the array.
	 * If it is, it returns the value of the validPath method.
	 * Otherwise, it returns false.
	 * 
	 * @param row - value of the row for the array <type int>
	 * @param col - value of the column for the array <type int>
	 * @return boolean
	 */
	public static boolean CanMoveRight (int row, int col) {
		if (col+1 < maze[0].length) { // Checks if the column it is given would still be in the array if moved 1 unit right
			return validPath(row,col+1);
		}
		return false;
	}
	/**CanMoveUp method:
	 * This functional method checks if the values it is given are within the array.
	 * If it is, it returns the value of the validPath method.
	 * Otherwise, it returns false.
	 * 
	 * @param row - value of the row for the array <type int>
	 * @param col - value of the column for the array <type int>
	 * @return boolean
	 */
	public static boolean CanMoveUp (int row, int col) {
		if (row-1 >= 0) { // Checks if the row it is given would still be in the array if moved 1 unit up
			return validPath(row-1,col);
		}
		return false;
	}
	/**CanMoveDown method:
	 * This functional method checks if the values it is given are within the array.
	 * If it is, it returns the value of the validPath method.
	 * Otherwise, it returns false.
	 * 
	 * @param row - value of the row for the array <type int>
	 * @param col - value of the column for the array <type int>
	 * @return boolean
	 */
	public static boolean CanMoveDown (int row, int col) {
		if (row+1 < maze.length) { // Checks if the row it is given would still be in the array if moved 1 unit down
			return validPath(row+1,col);
		}
		return false;
	}
	/**validPath method:
	 * This functional method checks if the array at the values it is given is not a wall.
	 * If it is not a wall, it returns true
	 * Otherwise, it returns false.
	 * 
	 * @param row - value of the row for the array <type int>
	 * @param col - value of the column for the array <type int>
	 * @return boolean
	 */
	public static boolean validPath (int row, int col) {
		if (!maze[row][col].equals("B")&&!maze[row][col].equals("/")) // selection to determine if the maze location isn't a wall (b) or a previously traveled path (/)
			return true;
		return false;
	}
	/**findCheese method:
	 * This functional method checks if the array at the values it is given is the location of the cheese
	 * If it is the cheese, it saves the location of cheese and old mouse location and then returns true
	 * Otherwise, it returns false.
	 * 
	 * @param row - value of the row for the array <type int>
	 * @param col - value of the column for the array <type int>
	 * @param mouse - object used to access the Mouse class <type Mouse>
	 * @return boolean
	 */
	public static boolean findCheese (int row, int col, Mouse mouse) {
		if (maze[row][col].equals("C")) { // Checks if the array values it is given are the cheese
			if (solutionCount == 0) { // Checks if this is the first solution to the cheese
				prevMousePosRow = mouse.getRow();
				prevMousePosCol = mouse.getCol();
				mouse.saveNewMouseRow(row);
				mouse.saveNewMouseCol(col);
			}
			solutionCount++;
			return true;
		}
		return false;
	}
	/**findExit method:
	 * This functional method checks if the array at the value it is given is the exit.
	 * It returns true if it is the exit location
	 * Otherwise it returns false
	 * 
	 * @param row - value of the row for the array <type int>
	 * @param col - value of the column for the array <type int>
	 * @return boolean
	 */
	public static boolean findExit (int row, int col) {
		if (maze[row][col].equals("X")) { // Checks if the array value is the exit location
			solutionCount++;
			return true;
		}
		return false;
	}
	/**pathFind method:
	 * This functional method uses recursion to test all possible paths in the maze. Once it has found the desired goal,
	 * either the cheese or the exit, it returns true and saves the move direction of each move in the solution to the 
	 * solutionList array. It also sets the current location to another character, to prevent looping around the same path.
	 * If it hits a dead end, it returns false and pops back all the way until anther potential path is found.
	 * 
	 * List of Local Variables
	 * goalFound - Determines whether or not the current location is part of an eventual solution
	 * 
	 * @param mouseRow - value of the row for the array <type int>
	 * @param mouseCol - value of the column for the array <type int>
	 * @param prevPosRow - Previous mouse row location, used to prevent the mouse from doubling back the way it came <type int>
	 * @param prevPosCol - Previous mouse column location, used to prevent the mouse from doubling back the way it came <type int>
	 * @param pathLength - Value that identifies the current path position, for example if it is the first
	 * move in the solution, pathLength will be 1 <type int>
	 * @param mouse - object to access the Mouse class, which is used in moving the mouse and getting the current location <type Mouse>
	 * @return boolean
	 */
	public static boolean pathFind (int mouseRow, int mouseCol, int prevPosRow, int prevPosCol, int pathLength, Mouse mouse) throws Exception{
		if (!maze[mouseRow][mouseCol].equals("X")&&!maze[mouseRow][mouseCol].equals("C")) // confirms that the current location isn't an important location 
			maze[mouseRow][mouseCol]="/"; // sets the current location to a temporary other character, to prevent going back where it came from
		boolean goalFound = false;
		if (CanMoveDown(mouseRow,mouseCol)&&mouse.moveDown(mouseRow)!=prevPosRow) { // calls the CanMoveDown method to check if moving down is a valid choice
			if (goalFound(mouse.moveDown(mouseRow),mouseCol, mouse) || pathFind (mouse.moveDown(mouseRow), mouseCol, mouseRow, mouseCol, pathLength+1, mouse)) {
			// checks if the current location is the goal, if not, call the pathFind method again to continue the search in the current direction
				solutionList[solutionCount-1][pathLength] = 4;
				goalFound = true;
			}
		}
		if (CanMoveUp(mouseRow,mouseCol)&&mouse.moveUp(mouseRow)!=prevPosRow) { // calls the CanMoveUp method to check if moving up is a valid choice
			if (goalFound(mouse.moveUp(mouseRow),mouseCol, mouse) || pathFind (mouse.moveUp(mouseRow), mouseCol, mouseRow, mouseCol, pathLength+1, mouse)) {
			// checks if the current location is the goal, if not, call the pathFind method again to continue the search in the current direction
				solutionList[solutionCount-1][pathLength] = 2;
				goalFound = true;
			}
		}
		if (CanMoveLeft(mouseRow,mouseCol)&&mouse.moveLeft(mouseCol)!=prevPosCol) { // calls the CanMoveLeft method to check if moving left is a valid choice
			if (goalFound(mouseRow,mouse.moveLeft(mouseCol), mouse) || pathFind (mouseRow, mouse.moveLeft(mouseCol), mouseRow, mouseCol, pathLength+1, mouse)) {
			// checks if the current location is the goal, if not, call the pathFind method again to continue the search in the current direction
				solutionList[solutionCount-1][pathLength] = 1;
				goalFound = true;
			}
		}
		if (CanMoveRight(mouseRow,mouseCol)&&mouse.moveRight(mouseCol)!=prevPosCol) { // calls the CanMoveRight method to check if moving right is a valid choice
			if (goalFound(mouseRow,mouse.moveRight(mouseCol), mouse) || pathFind (mouseRow, mouse.moveRight(mouseCol), mouseRow, mouseCol, pathLength+1, mouse)) {
			// checks if the current location is the goal, if not, call the pathFind method again to continue the search in the current direction	
				solutionList[solutionCount-1][pathLength] = 3;
				goalFound = true;
			}
		}
		if (!maze[mouseRow][mouseCol].equals("X")&&!maze[mouseRow][mouseCol].equals("C")) // confirms that the current location isn't a possible goal
			maze[mouseRow][mouseCol] = "."; // sets the current location back to a regular path
		return goalFound;
	}
	/**goalFound method:
	 * This functional method checks if the array at the value it is given is the desired goal
	 * If the cheese has already been found, it will return the value of the findExit method
	 * Otherwise it will return the value of the findCheese method
	 * 
	 * @param row - value of the row for the array <type int>
	 * @param col - value of the column for the array <type int>
	 * @param mouse - object created to access the Mouse class
	 * @return boolean
	 */
	public static boolean goalFound (int row, int col, Mouse mouse) {
		if (cheeseFound) // checks if the cheese has already been found
			return findExit(row, col);
		else
			return findCheese(row, col, mouse);
	}
	/**pathFindReset method:
	 * This procedural method resets all of the necessary variables to correctly run through the maze for a second time
	 * 
	 * @param mouse - object created to access the Mouse class
	 * @return void
	 */
	public static void pathFindReset (Mouse mouse) {
		solutionCount++;
		firstSolution = solutionCount;
		cheeseFound = true;
		mouse.moveMouse();
		maze[prevMousePosRow][prevMousePosCol] = ".";
	}
	/**fillEmptySolutions method:
	 * This procedural method fills missing steps to solutions by comparing them to the next solution
	 * If there are 2 or more solutions that have the same first or last few moves, the solution will only be 
	 * saved to one of them. This solution duplicates the moves to any solutions that are missing them
	 * 
	 * List of local variables
	 * solutionFilled - value to determine whether or not the blank moves are filled, to prevent filling all
	 * values with the previous solution's values
	 * 
	 * @return void
	 */
	public static void fillEmptySolutions () {
		finalSolution = solutionCount;
		boolean solutionFilled = false;
		for (int i = finalSolution;i>=firstSolution;i--) { // counted loop to traverse the entire solutionList array, one row at a time
			solutionFilled = false;
			for (int j = 0; j<1000 ; j++) { // counted loop to test all values of a specific solution in the solutionList array
				if (solutionFilled == false) { // makes sure the filling process isn't finished
					if (solutionList[i][j] == 0) // checks if the current solution is empty
						solutionList[i][j] = solutionList[i+1][j]; // sets the value of the current solution to the next solution
					else 
						solutionFilled = true;;
				}
			}
		}
	}
	/**findShortest method:
	 * This functional method finds the shortest possible solution inside the section it is given
	 * It returns either the location of the shortest solution, or the actual length of the shortest solution, depending on the value of wantLength
	 * 
	 * List of local variables
	 * shortestMoves - the length of the shortest possible solution
	 * shortestSolution - the location of the shortest possible solution
	 * moveCount - a temporary storage for the length of the current solution being tested
	 * 
	 * @param firstSolution - the location of the first solution wanting to be tested <type int>
	 * @param finalSolution - the location of the final solution wanting to be tested <type int>
	 * @param wantLength - value to determine what wants to be returned  <type boolean>
	 * 
	 * @return int
	 */
	public static int findShortest (int firstSolution, int finalSolution, boolean wantLength) {
		int shortestMoves = -1;
		int shortestSolution = -1;
		for (int i = firstSolution; i<= finalSolution; i++) { // counted loop to check every solution in the range
			int moveCount = 0;
			for (int j = 0; j < 1000; j++) { // counted loop to check every value in the solution
				if (solutionList[i][j] != 0) // checks if the current value is a move and not empty
					moveCount++;
			}
			if (shortestSolution == -1 || moveCount < shortestMoves) { // checks if the solution that was just tested is the shortest so far
				shortestSolution = i;
				shortestMoves = moveCount;
			}
		}
		if (wantLength)
			return shortestMoves;
		return shortestSolution;		
	}
	/**bestTotalSolution method:
	 * This procedural combines the best solution to the cheese and best solution from the cheese to the exit into one total solution
	 * 
	 * @param bestCheeseSolution - the location of the best solution to the cheese in the solutionList array <type int>
	 * @param bestExitSolution - the location of the best solution from the cheese to the exit in the solutionList array <type int>
	 * @param shortestCheesePath - the length of the best solution from the cheese <type int>
	 * 
	 * @return void
	 */
	public static void bestTotalSolution (int bestCheeseSolution, int bestExitSolution, int shortestCheesePath) {
		for (int i = 0; i < 1000; i++) // counted loop to save the first half of the total solution to the solutionList array
			solutionList[finalSolution+1][i] = solutionList[bestCheeseSolution][i];
		for (int i = 0; i < 1000-shortestCheesePath; i++) // counted loop to save the second half of the total solution to the solutionList
			solutionList[finalSolution+1][i+shortestCheesePath] = solutionList[bestExitSolution][i];
	}
	/**fillOutputMaze method:
	 * This functional method duplicates the maze array into the outputMaze array
	 * 
	 * @param outputMaze - string array used for output of the maze <type String[][]>
	 * 
	 * @return String[][]
	 */
	public static String[][] fillOutputMaze (String[][] outputMaze) {
		for (int i = 0; i<8; i++) {
			for (int j = 0; j<12; j++) {
					outputMaze[i][j] = maze[i][j];
			}
		}
		return outputMaze;
	}
	/**line method:
	 * This procedural method outputs a line to seperate information
	 * 
	 * @return void
	 */
	public static void line() {
		System.out.println("__________________________________________________________________");
		System.out.println();
	}
	/**mainOutput method:
	 * This procedural method prints out the main menu of the maze
	 * 
	 * @param outputMaze - Array that is manipulated for output <type String[][]>
	 * @param totalCheeseSolutions - number of ways to get from the start to the cheese <type int>
	 * @param totalExitSolutions - number of ways to get from the cheese to the exit <type int>
	 * 
	 * @return void
	 */
	public static void mainOutput (String[][] outputMaze, int totalCheeseSolutions, int totalExitSolutions) {
		for (int i = 0; i<8; i++) { // counted loop to print each row of the maze
			for (int j = 0; j<12; j++) { // counted loop to print each column of the maze
				System.out.print(outputMaze[i][j]);
			}
			if (i == 0) // checks if the first row of the maze is being printed
				System.out.print("   There are "+totalCheeseSolutions+" way(s) to get to the cheese,");
			else if (i == 1) // checks if the second row of the maze is being printed
				System.out.print("   "+totalExitSolutions+" way(s) to get from the cheese to the exit,");
			else if (i == 2) // checks if the third row of the maze is being printed
				System.out.print("   and "+totalExitSolutions*totalCheeseSolutions+" total way(s) to navigate the entire maze.");
			else if (i == 4) // checks if the fifth row of the maze is being printed
				System.out.print("   Enter a number to see the:");
			else if (i == 5) // checks if the sixth row of the maze is being printed
				System.out.print("   (1) Shortest path from the start to the cheese");
			else if (i == 6) // checks if the seventh row of the maze is being printed
				System.out.print("   (2) Shortest path from the cheese to the exit");
			else if (i == 7) // checks if the eighth row of the maze is being printed
				System.out.print("   (3) Shortest path through the entire maze");
			System.out.println();
		}
		System.out.println("               Or enter -1 to exit the program.");
	}
	/**startToCheese method:
	 * This procedural method prints out the 'start to cheese' solution of the menu
	 * 
	 * @param row - current row value in the maze array <type int>
	 * @param col - current column value in the maze array <type int>
	 * @param bestCheeseSolution - location of the best solution to the cheese in the solutionList array <type int>
	 * @param shortestCheesePath - length of the best solution to the cheese <type int>
	 * @param outputMaze - maze used for output <type String[][]>
	 * @param path - current move location in the best solution to the cheese <type int>
	 * 
	 * @return void
	 */
	public static void startToCheese(int row, int col, int bestCheeseSolution, int shortestCheesePath, String[][] outputMaze, int path) {
		if (!outputMaze[row][col].equals("C")) { // checks that the current location isn't the cheese (base case)
			if (!outputMaze[row][col].equals("M")&&!outputMaze[row][col].equals("X"))  // checks that the location isn't an important location
				outputMaze[row][col] = "~";
			if (solutionList[bestCheeseSolution][path] == 1) // checks if the value of the best solution is 1 (left)
				startToCheese(row, col-1, bestCheeseSolution, shortestCheesePath, outputMaze, path+1);
			else if (solutionList[bestCheeseSolution][path] == 2) // checks if the value of the best solution is 2 (up)
				startToCheese(row-1, col, bestCheeseSolution, shortestCheesePath, outputMaze, path+1);
			else if (solutionList[bestCheeseSolution][path] == 3) // checks if the value of the best solution is 3 (right)
				startToCheese(row, col+1, bestCheeseSolution, shortestCheesePath, outputMaze, path+1);
			else if (solutionList[bestCheeseSolution][path] == 4) // checks if the value of the best solution is 4 (down)
				startToCheese(row+1, col, bestCheeseSolution, shortestCheesePath, outputMaze, path+1);
		}
		else // else statement to know if the recursion portion is complete
			for (int i = 0; i<8; i++) { // counted loop to print every row in the array
				for (int j = 0; j<12; j++) { // counted loop to print every column in the array
					System.out.print(outputMaze[i][j]);
				}
				if (i==0) // checks if the first row of the maze is being printed
					System.out.print("   This is the shortest path from the start to the cheese.");
				else if (i==2) // checks if the third row of the maze is being printed
					System.out.print("   It is a total of "+shortestCheesePath+" moves.");
				else if (i==4) // checks if the fifth row of the maze is being printed
					System.out.print("   '~' Represents the path of the solution.");
				else if (i==6) // checks if the seventh row of the maze is being printed
					System.out.print("   Enter 0 to go back to the main menu.");
				System.out.println();
			}	
	}
	/**cheeseToExit method:
	 * This procedural method prints out the 'cheese to exit' solution of the menu
	 * 
	 * @param row - current row value in the maze array <type int>
	 * @param col - current column value in the maze array <type int>
	 * @param bestExitSolution - location of the best solution to the exit in the solutionList array <type int>
	 * @param shortestExitPath - length of the best solution to the exit <type int>
	 * @param outputMaze - maze used for output <type String[][]>
	 * @param path - current move location in the best solution to the exit <type int>
	 * 
	 * @return void
	 */
	public static void cheeseToExit(int row, int col, int bestExitSolution, int shortestExitPath, String[][] outputMaze, int path) {
		if (!outputMaze[row][col].equals("X")) { // checks to see if the current location is the exit
			if (!outputMaze[row][col].equals("M")&&!outputMaze[row][col].equals("C")) // makes sure the current location isn't important
				outputMaze[row][col] = "~"; // 
			if (solutionList[bestExitSolution][path] == 1) // checks if the value of the best solution is 1 (left)
				cheeseToExit(row, col-1, bestExitSolution, shortestExitPath, outputMaze, path+1);
			else if (solutionList[bestExitSolution][path] == 2) // checks if the value of the best solution is 2 (up)
				cheeseToExit(row-1, col, bestExitSolution, shortestExitPath, outputMaze, path+1);
			else if (solutionList[bestExitSolution][path] == 3) // checks if the value of the best solution is 3 (right)
				cheeseToExit(row, col+1, bestExitSolution, shortestExitPath, outputMaze, path+1);
			else if (solutionList[bestExitSolution][path] == 4) // checks if the value of the best solution is 4 (down)
				cheeseToExit(row+1, col, bestExitSolution, shortestExitPath, outputMaze, path+1);
		}  
		else // else statement to know if the recursion portion is complete
			for (int i = 0; i<8; i++) { // counted loop to print every row in the array
				for (int j = 0; j<12; j++) { // counted loop to print every column in the array
					System.out.print(outputMaze[i][j]);
				}
				if (i==0) // checks if the first row of the maze is being printed
					System.out.print("   This is the shortest path from the cheese to the exit.");
				else if (i==2) // checks if the third row of the maze is being printed
					System.out.print("   It is a total of "+shortestExitPath+" moves.");
				else if (i==4) // checks if the fifth row of the maze is being printed
					System.out.print("   '~' Represents the path of the solution.");
				else if (i==6) // checks if the seventh row of the maze is being printed
					System.out.print("   Enter 0 to go back to the main menu.");
				System.out.println();
			}	
	}
	/**startToExit method:
	 * This procedural method prints out the 'start to exit' solution of the menu
	 * 
	 * @param row - current row value in the maze array <type int>
	 * @param col - current column value in the maze array <type int>
	 * @param bestTotalSolution - location of the best solution to the exit in the solutionList array <type int>
	 * @param shortestTotalPath - length of the best solution to the exit <type int>
	 * @param outputMaze - maze used for output <type String[][]>
	 * @param path - current move location in the best solution to the exit <type int>
	 * 
	 * @return void
	 */
	public static void startToExit(int row, int col, int bestTotalSolution, int shortestTotalPath, String[][] outputMaze, int path) {
		if (!outputMaze[row][col].equals("X")) { // checks if the current location is the exit
			if (!outputMaze[row][col].equals("M")&&!outputMaze[row][col].equals("C")) // makes sure the current location isn't important
				outputMaze[row][col] = "~";
			if (solutionList[bestTotalSolution][path] == 1) // checks if the value of the best solution is 1 (left)
				startToExit(row, col-1, bestTotalSolution, shortestTotalPath, outputMaze, path+1);
			else if (solutionList[bestTotalSolution][path] == 2) // checks if the value of the best solution is 2 (up)
				startToExit(row-1, col, bestTotalSolution, shortestTotalPath, outputMaze, path+1);
			else if (solutionList[bestTotalSolution][path] == 3) // checks if the value of the best solution is 3 (right)
				startToExit(row, col+1, bestTotalSolution, shortestTotalPath, outputMaze, path+1);
			else if (solutionList[bestTotalSolution][path] == 4) // checks if the value of the best solution is 4 (down)
				startToExit(row+1, col, bestTotalSolution, shortestTotalPath, outputMaze, path+1);
		}
		else // else statement to know if the recursion portion is complete
			for (int i = 0; i<8; i++) { // counted loop to print every row in the array
				for (int j = 0; j<12; j++) { // counted loop to print every column in the array
					System.out.print(outputMaze[i][j]);
				}
				if (i==0) // checks if the first row of the maze is being printed
					System.out.print("   This is the shortest path from the start to the exit.");
				else if (i==2) // checks if the third row of the maze is being printed
					System.out.print("   It is a total of "+shortestTotalPath+" moves.");
				else if (i==4) // checks if the fifth row of the maze is being printed
					System.out.print("   '~' Represents the path of the solution.");
				else if (i==6) // checks if the seventh row of the maze is being printed
					System.out.print("   Enter 0 to go back to the main menu.");
				System.out.println();
			}	
	}
	/**resetMaze method:
	 * This functional method removes the solution path from the outputMaze, to reset it to the original.
	 * 
	 * @param outputMaze - maze used for output <type String[][]>
	 * 
	 * @return String[][]
	 */
	public static String[][] resetMaze (String[][] outputMaze) {
		for (int i = 0; i<8; i++) {
			for (int j = 0; j<12; j++) {
				if (outputMaze[i][j].equals("~"))
					outputMaze[i][j] = ".";
			}
		}
		return outputMaze;
	}
	/**fill method:
	 * This procedural method 'clears' the console to prepare the next output.
	 * 
	 * @return void
	 */
	public static void fill () {
		for (int i = 0; i< 10; i++)
			System.out.println();
	}
	/**startToExit method:
	 * This procedural method prints out the 'start to exit' solution of the menu
	 * 
	 * List of local variables
	 * input - user's choice on what to output
	 * 
	 * @param bestCheeseSolution - location of the best solution to the cheese in the solutionList array <type int>
	 * @param shortestCheesePath - length of the best solution to the cheese <type int>
	 * @param bestExitSolution - location of the best solution to the exit in the solutionList array <type int>
	 * @param shortestExitPath - length of the best solution to the exit <type int>
	 * @param totalCheeseSolutions - number of possible solutions to the cheese <type int>
	 * @param totalExitSolutions - number of possible solutions to the exit <type int>
	 * @param outputMaze - maze used for output <type String[][]>
	 * @param mouse - object used to access the mouse class <type Mouse>
	 * 
	 * @return void
	 */
	public static void output (int bestCheeseSolution, int bestExitSolution, int shortestCheesePath, int shortestExitPath, int totalCheeseSolutions, int totalExitSolutions, String[][]outputMaze, Mouse mouse) {
		int input = 0;
		while (input != -1 ) { // enters loop of input until user enters -1
			fill();
			line();
			if (input == 1) // checks if the user enters 1
				startToCheese(prevMousePosRow, prevMousePosCol, bestCheeseSolution, shortestCheesePath, outputMaze, 0);
			else if (input == 2) // checks if the user enters 2
				cheeseToExit(mouse.getRow(), mouse.getCol(), bestExitSolution, shortestExitPath, outputMaze, 0);
			else if (input == 3) // checks if the user enters 3
				startToExit(prevMousePosRow, prevMousePosCol, solutionCount+1, shortestExitPath+shortestCheesePath, outputMaze, 0);
			else
				mainOutput (outputMaze, totalCheeseSolutions, totalExitSolutions);
			line();
			System.out.print("Enter a number here: ");
			input = input(); // calls input method for user input
			outputMaze = resetMaze(outputMaze); // resets maze
		}
	}
}