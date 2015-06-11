package test;

import java.awt.Point;
import java.util.Vector;

class CheckersData {

	// An object of this class holds data about a game of checkers.
	// It knows what kind of piece is on each sqaure of the checkerboard.
	// Note that RED moves "up" the board (i.e. row number decreases)
	// while BLACK moves "down" the board (i.e. row number increases).
	// Methods are provided to return lists of available legal moves.
	
	/*  The following constants represent the possible contents of a square
	on the board.  The constants RED and BLACK also represent players
	in the game.
*/

	private int RED_MEN;
	private int RED_KINGS;
	private int BLACK_MEN;
	private int BLACK_KINGS;
	
	public static final int
	EMPTY = 0,
	RED = 1,
	RED_KING = 2,
	BLACK = 3,
	BLACK_KING = 4,
	FREE_WEAPON = 10,
	FREE_BUFF = 20,
	FREE_HEX = 30;
	
	public static final int
	HEIGHT = 8,
	WIDTH = 8;
	
	public static PowerUpSystem powerUpSys;

	private int[][] board;  // board[r][c] is the contents of row r, column c.  


	public CheckersData() {
		// Constructor.  Create the board and set it up for a new game.
		board = new int[HEIGHT][WIDTH];
		RED_MEN = 12;
		BLACK_MEN = 12;
		RED_KINGS = 0;
		BLACK_KINGS = 0;
		powerUpSys = new PowerUpSystem();
		setUpGame();
	}
	
	public void recount() {
		RED_MEN = 0;
		BLACK_MEN = 0;
		RED_KINGS = 0;
		BLACK_KINGS = 0;
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++)
				switch(board[i][j]%10) {
				case RED:
					RED_MEN++;
					break;
				case BLACK:
					BLACK_MEN++;
					break;
				case RED_KING:
					RED_KINGS++;
					break;
				case BLACK_KING:
					BLACK_KINGS++;
					break;
				}
	}

	public String hash() {
		String h = "";
		for(int row = 0; row < 8; row++) {
			for(int col = 0; col < 8; col++) {
				switch(board[row][col]) {
				case RED:
					h += "1";
					break;
				case RED_KING:
					h += "2";
					break;
				case BLACK:
					h += "3";
					break;
				case BLACK_KING:
					h += "4";
					break;
				case 10:
					h += "10";
					break;
				case 11:
					h += "11";
					break;
				case 12:
					h += "12";
					break;
				case 13:
					h += "13";
					break;
				case 14:
					h += "14";
					break;
				case 20:
					h += "20";
					break;
				case 21:
					h += "21";
					break;
				case 22:
					h += "22";
					break;
				case 23:
					h += "23";
					break;
				case 24:
					h += "24";
					break;
				case 30:
					h += "30";
					break;
				case 31:
					h += "31";
					break;
				case 32:
					h += "32";
					break;
				case 33:
					h += "33";
					break;
				case 34:
					h += "34";
					break;
				default:
					h += "_";
					break;
				}
			}
		}
		return h;	 
	}

	public void setUpGame() {
		// Set up the board with checkers in position for the beginning
		// of a game.  Note that checkers can only be found in squares
		// that satisfy  row % 2 == col % 2.  At the start of the game,
		// all such squares in the first three rows contain black squares
		// and all such squares in the last three rows contain red squares.
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if ( row % 2 == col % 2 ) {
					if (row < 3)
						board[row][col] = BLACK;
					else if (row > 4)
						board[row][col] = RED;
					else
						board[row][col] = EMPTY;
				}
				else {
					board[row][col] = EMPTY;
				}
			}
		}
	}  // end setUpGame()


	public void setUpGame(int[][] b) {
		BLACK_MEN = 0;
		RED_MEN = 0;
		for(int row = 0; row < 8; row++) {
			for(int col = 0; col < 8; col++) {
				board[row][col] = b[row][col];
				switch(board[row][col] % 10) {
				case BLACK:
					BLACK_MEN++;
					break;
				case RED:
					RED_MEN++;
					break;
				case RED_KING:
					RED_KINGS++;
					break;
				case BLACK_KING:
					BLACK_KINGS++;
					break;
				}
			}
		}
	} // end setUpGame(int[][] b)

	public int[][] getBoardCopy() {
		int[][] b = new int[8][8];
		for(int row = 0; row < 8; row++) {
			for(int col = 0; col < 8; col++) {
				b[row][col] = board[row][col];
			}
		}
		return b;
	} // end getBoardCopy

	public int pieceAt(int row, int col) {
		// Return the contents of the square in the specified row and column.
		return board[row][col];
	}


	public void setPieceAt(int row, int col, int piece) {
		// Set the contents of the square in the specified row and column.
		// piece must be one of the constants EMPTY, RED, BLACK, RED_KING,
		// BLACK_KING.
		board[row][col] = piece;
	}

	
	public void removePieceAt(Point p) {
		removePieceAt(p.y, p.x);
	}
	public void removePieceAt(int row, int col) {
		int jumped = board[row][col];
		board[row][col] = EMPTY;
		
		switch(jumped) {
		case 1:
			RED_MEN--;
			break;
		case 2:
			RED_KINGS--;
			break;
		case 3:
			BLACK_MEN--;
			break;
		case 4:
			BLACK_KINGS--;
			break;
		}
	}
	
	
	public static int parsePiece(int code)
	{
		return code % 10;
	}
	
	
	public static int parsePowerType(int code)
	{
		return (code / 10) % 10;
	}
	
	
	public static int parsePowerUp(int code)
	{
		return code / 100;
	}
	

	public void makeMove(CheckersMove move) {
		// Make the specified move.  It is assumed that move
		// is non-null and that the move it represents is legal.
		makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol, move.isPowerMove);
	}


	public void makeMove(int fromRow, int fromCol, int toRow, int toCol, boolean power) {
		// Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
		// assumed that this move is legal.  If the move is a jump, the
		// jumped piece is removed from the board.  If a piece moves
		// the last row on the opponent's side of the board, the 
		// piece becomes a king.
		
		
		if(power) {
			int piece = board[fromRow][fromCol] % 10;
			PowerUp p = null;

			switch (piece) {
				case RED:
				case RED_KING:
					p = powerUpSys.red_powers.get(new Point(fromRow, fromCol));
					break;
				case BLACK:
				case BLACK_KING:
					p = powerUpSys.blk_powers.get(new Point(fromRow, fromCol));
					break;
			}
			p.execute(board, new Point(toRow, toCol));
			recount();
			
		}
		else {
			int target = board[toRow][toCol];	// Value of space a piece moved to
			int powerPiece = board[fromRow][fromCol]/10;
			
			if(target == 0)
				board[toRow][toCol] = board[fromRow][fromCol];
			else
				board[toRow][toCol] = board[fromRow][fromCol] % 10 + target;
				board[fromRow][fromCol] = EMPTY;
			
			// CAPTURING PIECES WITH JUMPS
			if (fromRow - toRow == 2 || fromRow - toRow == -2) {
				removePieceAt((fromRow + toRow) / 2, (fromCol + toCol) / 2);			
			}
			
			// KINGING REDS
			if (toRow == 0 && board[toRow][toCol]%10 == RED) {
				if(target == 0)
					board[toRow][toCol] = RED_KING + (10*powerPiece);
				else
					board[toRow][toCol] = RED_KING + target;
				RED_KINGS++;
				RED_MEN--;
			}
			// KINGING BLACKS
			if (toRow == 7 && board[toRow][toCol]%10 == BLACK) {
				if(target == 0)
					board[toRow][toCol] = BLACK_KING + (10*powerPiece);
				else
					board[toRow][toCol] = BLACK_KING + target;
				BLACK_KINGS++;
				BLACK_MEN--;
			}
			// TARGET LOCATION HAD POWER-UP
			if (target != 0) {
				int piece = parsePiece(board[toRow][toCol]);
				int pType = parsePowerType(target);
				
				// Generate random PowerUp of pType
				powerUpSys.listPowerUp(new Point(toCol,toRow), piece, powerUpSys.getRandomPowerUp(pType));
			}
		}
	}


	public CheckersMove[] getLegalMoves(int player) {
		// Return an array containing all the legal CheckersMoves
		// for the specified player on the current board.  If the player
		// has no legal moves, null is returned.  The value of player
		// should be one of the constants RED or BLACK; if not, null
		// is returned.  If the returned value is non-null, it consists
		// entirely of jump moves or entirely of regular moves, since
		// if the player can jump, only jumps are legal moves.

		if (player != RED && player != BLACK)
			return null;

		int playerKing;  // The constant representing a King belonging to player.
		if (player == RED)
			playerKing = RED_KING;
		else
			playerKing = BLACK_KING;

		Vector<CheckersMove> moves = new Vector<CheckersMove>();  // Moves will be stored in this vector.
		
		/*  First, check for any possible jumps.  Look at each square on the board.
		If that square contains one of the player's pieces, look at a possible
		jump in each of the four directions from that square.  If there is 
		a legal jump in that direction, put it in the moves vector.
	*/

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if (board[row][col]%10 == player || board[row][col]%10 == playerKing) {
					if (canJump(player, row, col, row+1, col+1, row+2, col+2))
						moves.addElement(new CheckersMove(row, col, row+2, col+2, false));
					if (canJump(player, row, col, row-1, col+1, row-2, col+2))
						moves.addElement(new CheckersMove(row, col, row-2, col+2, false));
					if (canJump(player, row, col, row+1, col-1, row+2, col-2))
						moves.addElement(new CheckersMove(row, col, row+2, col-2, false));
					if (canJump(player, row, col, row-1, col-1, row-2, col-2))
						moves.addElement(new CheckersMove(row, col, row-2, col-2, false));
				}
			}
		}
		
		/*  If any jump moves were found, then the user must jump, so we don't 
		add any regular moves.  However, if no jumps were found, check for
		any legal regualar moves.  Look at each square on the board.
		If that square contains one of the player's pieces, look at a possible
		move in each of the four directions from that square.  If there is 
		a legal move in that direction, put it in the moves vector.
	*/

		
		if (moves.size() == 0) {
			int powerMan = 10 + player;
			int powerKing = 10 + playerKing;
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if (board[row][col] == player || board[row][col] == playerKing) {
						if (canMove(player,row,col,row+1,col+1))
							moves.addElement(new CheckersMove(row,col,row+1,col+1, false));
						if (canMove(player,row,col,row-1,col+1))
							moves.addElement(new CheckersMove(row,col,row-1,col+1, false));
						if (canMove(player,row,col,row+1,col-1))
							moves.addElement(new CheckersMove(row,col,row+1,col-1, false));
						if (canMove(player,row,col,row-1,col-1))
							moves.addElement(new CheckersMove(row,col,row-1,col-1, false));
					}
					else if(board[row][col] == powerMan || board[row][col] == powerKing) {
						moves.addElement(new CheckersMove(row, col, row, col, true));
					}
				}
			}
		}
		
		/* If no legal moves have been found, return null.  Otherwise, create
	an array just big enough to hold all the legal moves, copy the
	legal moves from the vector into the array, and return the array. */
		
		if (moves.size() == 0)
			return null;
		else {
			CheckersMove[] moveArray = new CheckersMove[moves.size()];
			for (int i = 0; i < moves.size(); i++)
				moveArray[i] = (CheckersMove)moves.elementAt(i);
			return moveArray;
		}

	}  // end getLegalMoves


	public CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
		// Return a list of the legal jumps that the specified player can
		// make starting from the specified row and column.  If no such
		// jumps are possible, null is returned.  The logic is similar
		// to the logic of the getLegalMoves() method.
		if (player != RED && player != BLACK)
			return null;
		int playerKing;  // The constant representing a King belonging to player.
		if (player == RED)
			playerKing = RED_KING;
		else
			playerKing = BLACK_KING;
		Vector<CheckersMove> moves = new Vector<CheckersMove>();  // The legal jumps will be stored in this vector.
		if (board[row][col]%10 == player || board[row][col]%10 == playerKing) {
			if (canJump(player, row, col, row+1, col+1, row+2, col+2))
				moves.addElement(new CheckersMove(row, col, row+2, col+2, false));
			if (canJump(player, row, col, row-1, col+1, row-2, col+2))
				moves.addElement(new CheckersMove(row, col, row-2, col+2, false));
			if (canJump(player, row, col, row+1, col-1, row+2, col-2))
				moves.addElement(new CheckersMove(row, col, row+2, col-2, false));
			if (canJump(player, row, col, row-1, col-1, row-2, col-2))
				moves.addElement(new CheckersMove(row, col, row-2, col-2, false));
		}
		if (moves.size() == 0)
			return null;
		else {
			CheckersMove[] moveArray = new CheckersMove[moves.size()];
			for (int i = 0; i < moves.size(); i++)
				moveArray[i] = (CheckersMove)moves.elementAt(i);
			return moveArray;
		}
	}  // end getLegalJumpsFrom()


	private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {
		// This is called by the two previous methods to check whether the
		// player can legally jump from (r1,c1) to (r3,c3).  It is assumed
		// that the player has a piece at (r1,c1), that (r3,c3) is a position
		// that is 2 rows and 2 columns distant from (r1,c1) and that 
		// (r2,c2) is the square between (r1,c1) and (r3,c3).
		
		if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
			return false;  // (r3,c3) is off the board.
		
		if (board[r3][c3]%10 != EMPTY)
			return false;  // (r3,c3) already contains a piece.
		
		if (player == RED) {
			if (board[r1][c1]%10 == RED && r3 > r1)
				return false;  // Regular red piece can only move  up.
			if (board[r2][c2]%10 != BLACK && board[r2][c2]%10 != BLACK_KING)
				return false;  // There is no black piece to jump.
			return true;  // The jump is legal.
		}
		else {
			if (board[r1][c1]%10 == BLACK && r3 < r1)
				return false;  // Regular black piece can only move downn.
			if (board[r2][c2]%10 != RED && board[r2][c2]%10 != RED_KING)
				return false;  // There is no red piece to jump.
			return true;  // The jump is legal.
		}

	}  // end canJump()


	private boolean canMove(int player, int r1, int c1, int r2, int c2) {
		// This is called by the getLegalMoves() method to determine whether
		// the player can legally move from (r1,c1) to (r2,c2).  It is
		// assumed that (r1,r2) contains one of the player's pieces and
		// that (r2,c2) is a neighboring square.
		int playerKing;
		if (player == RED)
			playerKing = RED_KING;
		else
			playerKing = BLACK_KING;
		
		
		if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
			return false;  // (r2,c2) is off the board.
		
		if (board[r2][c2]%10 != EMPTY)
			return false;  // (r2,c2) already contains a piece.

		if (player == RED) {
			if (board[r1][c1]%10 == RED && r2 > r1)
				return false;  // Regualr red piece can only move down.
			return true;  // The move is legal.
		}
		else {
			if (board[r1][c1]%10 == BLACK && r2 < r1)
				return false;  // Regular black piece can only move up.
			return true;  // The move is legal.
		}
		
	}  // end canMove()


	public int numRedMen() {
		return RED_MEN;
	}

	public int numRedKings() {
		return RED_KINGS;
	}

	public int numBlackMen() {
		return BLACK_MEN;
	}

	public int numBlackKings() {
		return BLACK_KINGS;
	}

} // end class CheckersData
