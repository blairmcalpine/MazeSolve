public class Mouse {
	/** List of Global Variables
	 * rowLoc - the current row location of the mouse
	 * colLoc - the current column location of the mouse
	 * newRow - the row location of the mouse once the cheese has been found
	 * newCol - the column location of the mouse once the cheese has been found
	 */
	private static int rowLoc, colLoc, newRow, newCol;
	/** Mouse method
	 * Constructor method to set the value of rowLoc and colLoc via recursion
	 * 
	 * @param maze - array used to store values of the maze <type String[][]>
	 */
	public Mouse (String[][] maze) {
		findMouse(0,0,maze);
	}
	/**findMouse method
	 * This procedural method uses recursion to locate the mouse in the maze
	 * 
	 * @param maze - array used to store values of the maze <type String[][]>
	 * @param row - current row location in the maze
	 * @param col - current column location in the maze
	 * 
	 * @return void
	 */
	private static void findMouse (int row, int col, String[][] maze) {
		if (row < maze.length) {
			if (col < maze[row].length) {
				if (mouseIsHere(row, col, maze)) {
					rowLoc = row;
					colLoc = col;
				}
				else 
					findMouse(row,col+1, maze);
			}
			else {
				findMouse(row+1,0, maze);
			}
		}
	}
	/**mouseIsHere method
	 * This functional method uses determines whether or not the mouse is at the location that it is given
	 * 
	 * @param maze - array used to store values of the maze <type String[][]>
	 * @param row - current row location in the maze
	 * @param col - current column location in the maze
	 * 
	 * @return boolean
	 */
	private static boolean mouseIsHere (int row, int col, String[][] maze) {
		if (maze[row][col].equals("M")) // checks if current location in the maze is the mouse
			return true;
		return false;
	}
	/**getRow method
	 * This functional method is an accessor for the main class, giving it the current row value of the mouse
	 * 
	 * @return int
	 */
	public int getRow () {
		return rowLoc;
	}
	/**getCol method
	 * This functional method is an accessor for the main class, giving it the current column value of the mouse
	 * 
	 * @return int
	 */
	public int getCol () {
		return colLoc;
	}
	/**moveDown method
	 * This functional method is a mutator for the main class that moves the mouse down one unit
	 * 
	 * @param row - current row value of the mouse
	 * 
	 * @return int
	 */
	public int moveDown (int row) {
		return row+1;
	}
	/**moveUp method
	 * This functional method is a mutator for the main class that moves the mouse up one unit
	 * 
	 * @param row - current row value of the mouse
	 * 
	 * @return int
	 */
	public int moveUp (int row) {
		return row-1;
	}
	/**moveLeft method
	 * This functional method is a mutator for the main class that moves the mouse left one unit
	 * 
	 * @param col - current column value of the mouse
	 * 
	 * @return int
	 */
	public int moveLeft (int col) {
		return col-1;
	}
	/**moveRight method
	 * This functional method is a mutator for the main class that moves the mouse right one unit
	 * 
	 * @param col - current column value of the mouse
	 * 
	 * @return int
	 */
	public int moveRight (int col){
		return col+1;
	}
	/**saveNewMouseRow method
	 * This procedural method saves the value of the new mouse row location
	 * 
	 * @param row - current row value of the mouse
	 * 
	 * @return void
	 */
	public void saveNewMouseRow (int row) {
		newRow = row;
	}
	/**saveNewMouseCol method
	 * This procedural method saves the value of the new mouse column location
	 * 
	 * @param col - current column value of the mouse
	 * 
	 * @return void
	 */
	public void saveNewMouseCol (int col) {
		newCol = col;
	}
	/**moveMouse method
	 * This procedural method moves the mouse to its new location, where the cheese used to be
	 * 
	 * @return void
	 */
	public void moveMouse () {
		rowLoc = newRow;
		colLoc = newCol;
	}
}
