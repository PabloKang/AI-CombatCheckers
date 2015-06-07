package test;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

class CheckersCanvas extends Canvas implements ActionListener, MouseListener {

	// This canvas displays a 160-by-160 checkerboard pattern with
	// a 2-pixel black border.  It is assumed that the size of the
	// canvas is set to exactly 164-by-164 pixels.  This class does
	// the work of letting the users play checkers, and it displays
	// the checkerboard.

	JButton resignButton;   // Current player can resign by clicking this button.
	JButton newGameButton;  // This button starts a new game.  It is enabled only
	//     when the current game has ended.

	JLabel message;   // A label for displaying messages to the user.

	CheckersData board;  // The data for the checkers board is kept here.
	//    This board is also responsible for generating
	//    lists of legal moves.

	public static AI firstAI;
	public static AI secondAI;
	
	public static int turnNumber = 0;

	boolean gameInProgress; // Is a game currently in progress?

	/* The next three variables are valid only when the game is in progress. */

	int currentPlayer;      // Whose turn is it now?  The possible values
	//    are CheckersData.RED and CheckersData.BLACK.
	int selectedRow, selectedCol;  // If the current player has selected a piece to
	//     move, these give the row and column
	//     containing that piece.  If no piece is
	//     yet selected, then selectedRow is -1.
	CheckersMove[] legalMoves;  // An array containing the legal moves for the
	//   current player.

	int winner;
	
	public CheckersCanvas() {
		// Constructor.  Create the buttons and lable.  Listen for mouse
		// clicks and for clicks on the buttons.  Create the board and
		// start the first game.
		setBackground(Color.black);
		addMouseListener(this);
		setFont(new  Font("Serif", Font.BOLD, 14));
		resignButton = new JButton("Resign");
		resignButton.addActionListener(this);
		newGameButton = new JButton("New Game");
		newGameButton.addActionListener(this);
		message = new JLabel("", JLabel.CENTER);
		board = new CheckersData();
		doNewAIvsAIGame();
	}

	public void outputResult(String output, int ai) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output, true)));
			switch(ai) {
			case 1:
				switch(winner) {
				case 0:
					out.print("Draw ");
					break;
				case 1:
					out.print("AI ");
					break;
				case 2:
					out.print("Random ");
					break;
				}
				break;
			case 2:
				switch(winner) {
				case 0:
					out.print("Draw ");
					break;
				case 1:
					out.print("Random ");
					break;
				case 2:
					out.print("AI ");
					break;
				}
				break;
			case 0:
				switch(winner) {
				case 0:
					out.print("Draw ");
					break;
				case 1:
					out.print("Red ");
					break;
				case 2:
					out.print("Black ");
					break;
				}
			}
			out.println(turnNumber);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void actionPerformed(ActionEvent evt) {
		// Respond to user's click on one of the two buttons.
		Object src = evt.getSource();
		if (src == newGameButton)
			doNewGame();
		else if (src == resignButton)
			doResign();
			
	}

	void trainAI() {
		for(int i = 0; i < 500; i++) {
			System.out.print("Red vs Random | i = ");
			System.out.println(i);
			while(gameInProgress) {
				turnNumber++;
				if(currentPlayer == firstAI.player)
					doMakeMoveAIvsAI(firstAI.makeMove(board));
				else
					doMakeMoveAIvsAI(secondAI.makeRandomMove(board));
			}
			outputResult(System.getenv("APPDATA") + "\\Combat Checkers\\RedvsRand.txt", 1);
			doNewGame();
			if(i%2 == 0)
				currentPlayer = CheckersData.BLACK;
		}
		for(int i = 0; i < 500; i++) {
			System.out.print("Random vs Black | i = ");
			System.out.println(i);
			while(gameInProgress) {
				turnNumber++;
				if(currentPlayer == firstAI.player)
					doMakeMoveAIvsAI(firstAI.makeRandomMove(board));
				else
					doMakeMoveAIvsAI(secondAI.makeMove(board));
			}
			outputResult(System.getenv("APPDATA") + "\\Combat Checkers\\RandvsBlack.txt", 2);
			doNewGame();
			if(i%2 == 0)
				currentPlayer = CheckersData.BLACK;
		}
		for(int i = 0; i < 100; i++) {
			System.out.print("Red vs Black | i = ");
			System.out.println(i);
			while(gameInProgress) {
				turnNumber++;
				if(currentPlayer == firstAI.player)
					doMakeMoveAIvsAI(firstAI.makeMove(board));
				else
					doMakeMoveAIvsAI(secondAI.makeMove(board));
			}
			outputResult(System.getenv("APPDATA") + "\\Combat Checkers\\RedvsBlack.txt", 0);
			doNewGame();
			if(i%2 == 0)
				currentPlayer = CheckersData.BLACK;
		}
		System.out.println("done training");
	}
	
	void doNewGame() {
		// Begin a new game.
		if (gameInProgress) {
			// This should not be possible, but it doens't 
			// hurt to check.
			message.setText("Finish the current game first!");
			return;
		}
		turnNumber = 0;
		board.setUpGame();   // Set up the pieces.
		currentPlayer = CheckersData.RED;   // RED moves first.
		legalMoves = board.getLegalMoves(CheckersData.RED);  // Get RED's legal moves.
		selectedRow = -1;   // RED has not yet selected a piece to move.
		message.setText("Red:  Make your move.");
		gameInProgress = true;
		newGameButton.setEnabled(false);
		resignButton.setEnabled(true);
		repaint();
	} // end doNewGame()

	void doNewAIGame() {
		if (gameInProgress == true) {
			// This should not be possible, but it doens't 
			// hurt to check.
			message.setText("Finish the current game first!");
			return;
		}
		turnNumber = 0;
		board.setUpGame();
		currentPlayer = CheckersData.RED;
		firstAI = new AI(CheckersData.BLACK, currentPlayer, System.getenv("APPDATA") + "\\Combat Checkers\\text.txt");
		legalMoves = board.getLegalMoves(CheckersData.RED);  // Get RED's legal moves.
		selectedRow = -1;   // RED has not yet selected a piece to move.
		message.setText("Red:  Make your move.");
		gameInProgress = true;
		newGameButton.setEnabled(false);
		resignButton.setEnabled(true);
		repaint();
	}

	void doNewAIvsAIGame() {
		if (gameInProgress == true) {
			message.setText("Finish the current game first!");
			return;
		}
		turnNumber = 0;
		board.setUpGame();
		currentPlayer = CheckersData.RED;
		firstAI = new AI(currentPlayer, CheckersData.BLACK, System.getenv("APPDATA") + "\\Combat Checkers\\text.txt");
		secondAI = new AI(CheckersData.BLACK, currentPlayer, System.getenv("APPDATA") + "\\Combat Checkers\\text2.txt");
		legalMoves = board.getLegalMoves(CheckersData.RED);
		selectedRow = -1;
		message.setText("Red: Make your move.");
		gameInProgress = true;
		newGameButton.setEnabled(false);
		resignButton.setEnabled(true);
		repaint();
	}
	


	void doResign() {
		// Current player resigns.  Game ends.  Opponent wins.
		if (gameInProgress == false) {
			message.setText("There is no game in progress!");
			return;
		}
		if (currentPlayer == CheckersData.RED) 
			gameOver("RED resigns.  BLACK wins.");         
		else
			gameOver("BLACK resigns.  RED wins.");
	}

	void doResignAI() {
		if (gameInProgress == false) {
			message.setText("There is no game in progress!");
			return;
		}
		if (currentPlayer == CheckersData.RED) {
			gameOver("RED resigns.  BLACK wins.");
			if(currentPlayer == firstAI.player)
				firstAI.lostGame();
			else
				firstAI.wonGame();
		}
		else {
			gameOver("BLACK resigns.  RED wins.");
			if(currentPlayer == firstAI.player)
				firstAI.lostGame();
			else
				firstAI.wonGame();
		}
	}

	void doResignAIvsAI() {
		if(currentPlayer == CheckersData.RED)
			gameOver("RED resigns. BLACK wins.");
		else
			gameOver("BLACK resigns. RED wins.");
		
		if(currentPlayer == firstAI.player) {
			firstAI.lostGame();
			secondAI.wonGame();
			winner = 2;
		}
		else {
			secondAI.lostGame();
			firstAI.wonGame();
			winner = 1;
		}
		gameInProgress = false;
	}

	void gameOver(String str) {
		// The game ends.  The parameter, str, is displayed as a message
		// to the user.  The states of the buttons are adjusted so playes
		// can start a new game.
		message.setText(str);
		newGameButton.setEnabled(true);
		resignButton.setEnabled(false);
		gameInProgress = false;
	}
	
	boolean drawDetection(CheckersMove move) {
		ArrayList<CheckersMove> p1 = new ArrayList<CheckersMove>();
		ArrayList<CheckersMove> p2 = new ArrayList<CheckersMove>();
		CheckersData copy = new CheckersData();
		copy.setUpGame(board.getBoardCopy());
		copy.makeMove(move);
		if(currentPlayer == firstAI.player) {
			p1.add(move);
			for(int i = 1; i < 8; i++) {
				CheckersMove m;
				if(i%2 == 0) {
					m = firstAI.makeMove(copy);
					if(m == null)
						return false;
					p1.add(m);
				}
				else {
					m = secondAI.makeMove(copy);
					if(m == null)
						return false;
					p2.add(m);
				}
				copy.makeMove(m);
			}
		}
		else {
			p2.add(move);
			for(int i = 1; i < 8; i++) {
				CheckersMove m;
				if(i%2 == 0) {
					m = secondAI.makeMove(copy);
					if(m == null)
						return false;
					p2.add(m);
				}
				else {
					m = firstAI.makeMove(copy);
					if(m == null)
						return false;
					p1.add(m);
				}
				copy.makeMove(m);
			}
		}
		int p1move = 0;
		int p2move = 0;
		if(p1.get(0).isEqual(p1.get(1)))
			p1move++;
		if(p1.get(0).isEqual(p1.get(2)))
			p1move++;
		if(p1.get(0).isEqual(p1.get(3)))
			p1move++;
		if(p1.get(1).isEqual(p1.get(2)))
			p1move++;
		if(p1.get(1).isEqual(p1.get(3)))
			p1move++;
		if(p1.get(2).isEqual(p1.get(3)))
			p1move++;
		if(p2.get(0).isEqual(p2.get(1)))
			p2move++;
		if(p2.get(0).isEqual(p2.get(2)))
			p2move++;
		if(p2.get(0).isEqual(p2.get(3)))
			p2move++;
		if(p2.get(1).isEqual(p2.get(2)))
			p2move++;
		if(p2.get(1).isEqual(p2.get(3)))
			p2move++;
		if(p2.get(2).isEqual(p2.get(3)))
			p2move++;
//		System.out.print("p1move: ");
//		System.out.print(p1move);
//		System.out.print(" | p2move: ");
//		System.out.println(p2move);
		if(p1move >= 2 && p2move >= 2)
			return true;
		else return false;
		
	}
	
	void doClickSquare(int row, int col) {
		// This is called by mousePressed() when a player clicks on the
		// square in the specified row and col.  It has already been checked
		// that a game is, in fact, in progress.
		
		/* If the player clicked on one of the pieces that the player
		can move, mark this row and col as selected and return.  (This
		might change a previous selection.)  Reset the message, in
		case it was previously displaying an error message. */

		for (int i = 0; i < legalMoves.length; i++)
		if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
			selectedRow = row;
			selectedCol = col;
			if (currentPlayer == CheckersData.RED)
				message.setText("RED:  Make your move.");
			else
				message.setText("BLACK:  Make your move.");
			repaint();
			return;
		}

		/* If no piece has been selected to be moved, the user must first
		select a piece.  Show an error message and return. */

		if (selectedRow < 0) {
			message.setText("Click the piece you want to move.");
			return;
		}
		
		/* If the user clicked on a square where the selected piece can be
		legally moved, then make the move and return. */

		for (int i = 0; i < legalMoves.length; i++)
		if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
				&& legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
			doMakeMove(legalMoves[i]);
			return;
		}
		
		/* If we get to this point, there is a piece selected, and the square where
		the user just clicked is not one where that piece can be legally moved.
		Show an error message. */

		message.setText("Click the square you want to move to.");

	}  // end doClickSquare()


	void doMakeMove(CheckersMove move) {
		// This is called when the current player has chosen the specified move.  Make the move, and then either end or continue the game appropriately.	


		board.makeMove(move);
		
		/* If the move was a jump, it's possible that the player has another
		jump.  Check for legal jumps starting from the square that the player
		just moved to.  If there are any, the player must jump.  The same
		player continues moving.
	*/
		
		if (move.isJump()) {
			
			legalMoves = board.getLegalJumpsFrom(currentPlayer,move.toRow,move.toCol);
			if (legalMoves != null) {
				if (currentPlayer == CheckersData.RED)
					message.setText("RED:  You must continue jumping.");
				else
					message.setText("BLACK:  You must continue jumping.");
				selectedRow = move.toRow;  // Since only one piece can be moved, select it.
				selectedCol = move.toCol;
				repaint();
				return;
			}
		}
		
		/* The current player's turn is ended, so change to the other player.
		Get that player's legal moves.  If the player has no legal moves,
		then the game ends. */
		
		if (currentPlayer == CheckersData.RED) {
			currentPlayer = CheckersData.BLACK;
			legalMoves = board.getLegalMoves(currentPlayer);
			if (legalMoves == null) {
				gameOver("BLACK has no moves.  RED wins.");
				return;
			}
			else if (legalMoves[0].isJump())
				message.setText("BLACK:  Make your move.  You must jump.");
			else
				message.setText("BLACK:  Make your move.");
		}
		else {
			currentPlayer = CheckersData.RED;
			legalMoves = board.getLegalMoves(currentPlayer);
			if (legalMoves == null) {
				gameOver("RED has no moves.  BLACK wins.");
				return;
			}
			else if (legalMoves[0].isJump())
				message.setText("RED:  Make your move.  You must jump.");
			else
				message.setText("RED:  Make your move.");
		}
		

		
		/* Set selectedRow = -1 to record that the player has not yet selected
		a piece to move. */
		
		selectedRow = -1;
		
		/* As a courtesy to the user, if all legal moves use the same piece, then
		select that piece automatically so the use won't have to click on it
		to select it. */
		
		if (legalMoves != null) {
			boolean sameStartSquare = true;
			for (int i = 1; i < legalMoves.length; i++)
			if (legalMoves[i].fromRow != legalMoves[0].fromRow
					|| legalMoves[i].fromCol != legalMoves[0].fromCol) {
				sameStartSquare = false;
				break;
			}
			if (sameStartSquare) {
				selectedRow = legalMoves[0].fromRow;
				selectedCol = legalMoves[0].fromCol;
			}
		}
		
		/* Make sure the board is redrawn in its new state. */
		
		repaint();
		
	}  // end doMakeMove();

	void doMakeMoveAIvsAI(CheckersMove move) {
		if(move == null) {
			doResignAIvsAI();
			return;
		}
		if(turnNumber >= 50) {
			if(drawDetection(move)) {
				gameOver("Game is a draw.");
				firstAI.drawGame();
				secondAI.drawGame();
				gameInProgress = false;
				winner = 0;
				return;
			}
		}
		board.makeMove(move);
//		try {
//			System.out.println("Here");
//			TimeUnit.SECONDS.sleep(1);
//			repaint();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if(move.isJump()) {
			legalMoves = board.getLegalJumpsFrom(currentPlayer,move.toRow, move.toCol);
			if(legalMoves != null) {
				if(currentPlayer == CheckersData.RED)
					message.setText("RED: You must continue jumping.");
				else
					message.setText("BLACK: You must continue jumping.");
				//selectedRow = move.toRow;
				//selectedCol = move.toCol;
				CheckersData copy = new CheckersData();
				copy.setUpGame(board.getBoardCopy());
				repaint();
				return;
			}
		}
		
		if(currentPlayer == CheckersData.RED) {
			currentPlayer = CheckersData.BLACK;
			legalMoves = board.getLegalMoves(currentPlayer);
			if(legalMoves == null) {
				gameOver("BLACK has no moves. RED wins.");
				secondAI.lostGame();
				firstAI.wonGame();
				winner = 1;
//				repaint();
				return;
			}
			else if(legalMoves[0].isJump())
				message.setText("BLACK: Make your move. You must jump.");
			else 
				message.setText("BLACK: Make your move.");
//			CheckersData copy = new CheckersData();
//			copy.setUpGame(board.getBoardCopy());
//			doMakeMoveAIvsAI(secondAI.makeMove(copy));
		}
		else {
			currentPlayer = CheckersData.RED;
			legalMoves = board.getLegalMoves(currentPlayer);
			if(legalMoves == null) {
				gameOver("RED has no moves. BLACK wins.");
				firstAI.lostGame();
				secondAI.wonGame();
				winner = 2;
//				repaint();
				return;
			}
			else if(legalMoves[0].isJump()) 
				message.setText("RED: Make your move. You must jump.");
			else
				message.setText("RED: Make your move.");
//			CheckersData copy = new CheckersData();
//			copy.setUpGame(board.getBoardCopy());
//			doMakeMoveAIvsAI(firstAI.makeMove(copy));
		}
		/*selectedRow = -1;
		if(legalMoves != null) {
			boolean sameStartSquare = true;
			for(int i = 1; i < legalMoves.length; i++)
				if(legalMoves[i].fromRow != legalMoves[0].fromRow
						|| legalMoves[i].fromCol != legalMoves[0].fromCol) {
					sameStartSquare = false;
					break;
				}
			if(sameStartSquare) {
				selectedRow = legalMoves[0].fromRow;
				selectedCol = legalMoves[0].fromCol;
			}
		}*/
		repaint();
		
	}


	public void update(Graphics g) {
		// The paint method completely redraws the canvas, so don't erase
		// before calling paint().
		paint(g);
	}


	public void paint(Graphics g) {
		// Draw  checkerboard pattern in gray and lightGray.  Draw the
		// checkers.  If a game is in progress, highlite the legal moves.
		
		/* Draw a two-pixel black border around the edges of the canvas. */
		
		g.setColor(Color.black);
		g.drawRect(0,0,getSize().width-1,getSize().height-1);
		g.drawRect(1,1,getSize().width-3,getSize().height-3);
		
		/* Draw the squares of the checkerboard and the checkers. */
		
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if ( row % 2 == col % 2 )
				g.setColor(Color.lightGray);
				else
				g.setColor(Color.gray);
				g.fillRect(2 + col*20, 2 + row*20, 20, 20);
				switch (board.pieceAt(row,col)) {
				case CheckersData.RED:
					g.setColor(Color.red);
					g.fillOval(4 + col*20, 4 + row*20, 16, 16);
					break;
				case CheckersData.BLACK:
					g.setColor(Color.black);
					g.fillOval(4 + col*20, 4 + row*20, 16, 16);
					break;
				case CheckersData.RED_KING:
					g.setColor(Color.red);
					g.fillOval(4 + col*20, 4 + row*20, 16, 16);
					g.setColor(Color.white);
					g.drawString("K", 7 + col*20, 16 + row*20);
					break;
				case CheckersData.BLACK_KING:
					g.setColor(Color.black);
					g.fillOval(4 + col*20, 4 + row*20, 16, 16);
					g.setColor(Color.white);
					g.drawString("K", 7 + col*20, 16 + row*20);
					break;
				}
			}
		}

		/* If a game is in progress, hilite the legal moves.   Note that legalMoves
		is never null while a game is in progress. */      
		
		if (gameInProgress) {
			// First, draw a cyan border around the pieces that can be moved.
			g.setColor(Color.cyan);
			for (int i = 0; i < legalMoves.length; i++) {
				g.drawRect(2 + legalMoves[i].fromCol*20, 2 + legalMoves[i].fromRow*20, 19, 19);
			}
			// If a piece is selected for moving (i.e. if selectedRow >= 0), then
			// draw a 2-pixel white border around that piece and draw green borders 
			// around each square that that piece can be moved to.
			if (selectedRow >= 0) {
				g.setColor(Color.white);
				g.drawRect(2 + selectedCol*20, 2 + selectedRow*20, 19, 19);
				g.drawRect(3 + selectedCol*20, 3 + selectedRow*20, 17, 17);
				g.setColor(Color.green);
				for (int i = 0; i < legalMoves.length; i++) {
					if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow)
					g.drawRect(2 + legalMoves[i].toCol*20, 2 + legalMoves[i].toRow*20, 19, 19);
				}
			}
		}
	}  // end paint()


	public Dimension getPreferredSize() {
		// Specify desired size for this component.  Note:
		// the size MUST be 164 by 164.
		return new Dimension(164, 164);
	}


	public Dimension getMinimumSize() {
		return new Dimension(164, 164);
	}


	public void mousePressed(MouseEvent evt) {
		// Respond to a user click on the board.  If no game is
		// in progress, show an error message.  Otherwise, find
		// the row and column that the user clicked and call
		// doClickSquare() to handle it.
		if (gameInProgress == false)
		message.setText("Click \"New Game\" to start a new game.");
		else {
			int col = (evt.getX() - 2) / 20;
			int row = (evt.getY() - 2) / 20;
			if (col >= 0 && col < 8 && row >= 0 && row < 8)
			doClickSquare(row,col);
		}
	}


	public void mouseReleased(MouseEvent evt) { }
	public void mouseClicked(MouseEvent evt) { }
	public void mouseEntered(MouseEvent evt) { }
	public void mouseExited(MouseEvent evt) { }


}  // end class SimpleCheckerboardCanvas