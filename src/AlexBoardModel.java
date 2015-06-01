
/*
 * Our model for the Checkers Gameboard
 * 
 */

public class AlexBoardModel {

	private char board[][];
	
	
	public AlexBoardModel() { // Constructor for the Gameboard
		board = new char[8][8];

		// Fill in the White Pieces and Black Pieces
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 3; j++) {
				if(j % 2 == 0) {
					if(i % 2 == 1) 
						board[j][i] = 'w';
					else
						board[j][i] = '\0';
				}
				else {
					if(i % 2 == 0) 
						board[j][i] = 'w';
					else
						board[j][i] = '\0';
				}
			}
			for(int j = 7; j >= 5 ; j--) {
				if(j % 2 == 1) {
					if(i % 2 == 0) 
						board[j][i] = 'b';
					else
						board[j][i] = '\0';
				}
				else {
					if(i % 2 == 1)
						board[j][i] = 'b';
					else
						board[j][i] = '\0';
				}
			}
		}

	} // end of BoardModel()
	
	public void printBoard() {
		System.out.println("---------------------------------");
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				System.out.print("| ");
				System.out.print(board[i][j] + " ");
			}
			System.out.println('|');
			System.out.println("---------------------------------");
		}
	} // end of printBoard()
	
	public boolean makeMove(int startx, int starty, int endx, int endy) {
		if(board[starty][startx] == '\0')
			return false;

		if(board[endy][endx] != '\0') 
			return false;

		board[endy][endx] = board[starty][startx];
		board[starty][startx] = '\0';
		return true;
	}
	
}







