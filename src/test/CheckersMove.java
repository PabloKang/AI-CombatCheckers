package test;

class CheckersMove {
	// A CheckersMove object represents a move in the game of Checkers.
	// It holds the row and column of the piece that is to be moved
	// and the row and column of the square to which it is to be moved.
	// (This class makes no guarantee that the move is legal.)
	int fromRow, fromCol;  // Position of piece to be moved.
	int toRow, toCol;      // Square it is to move to.
	boolean isPowerMove;
	CheckersMove(int r1, int c1, int r2, int c2, boolean power) {
		// Constructor.  Just set the values of the instance variables.
		fromRow = r1;
		fromCol = c1;
		toRow = r2;
		toCol = c2;
		isPowerMove = power;
	}
	boolean isPower() {
		return isPowerMove;
	}
	boolean isJump() {
		// Test whether this move is a jump.  It is assumed that
		// the move is legal.  In a jump, the piece moves two
		// rows.  (In a regular move, it only moves one row.)
		return (fromRow - toRow == 2 || fromRow - toRow == -2);
	}
	boolean isEqual(CheckersMove m) {
		return (fromRow == m.fromRow && fromCol == m.fromCol
				&& toRow == m.toRow && toCol == m.toCol);
	}
}  // end class CheckersMove.