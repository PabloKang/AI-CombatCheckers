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
     doNewAIGame();
  }
  

  public void actionPerformed(ActionEvent evt) {
        // Respond to user's click on one of the two buttons.
     Object src = evt.getSource();
     if (src == newGameButton)
        doNewGame();
     else if (src == resignButton)
        doResignAI();
  }
  

  void doNewGame() {
        // Begin a new game.
     if (gameInProgress == true) {
            // This should not be possible, but it doens't 
            // hurt to check.
        message.setText("Finish the current game first!");
        return;
     }
     board.setUpGame();   // Set up the pieces.
     currentPlayer = CheckersData.RED;   // RED moves first.
     legalMoves = board.getLegalMoves(CheckersData.RED);  // Get RED's legal moves.
     selectedRow = -1;   // RED has not yet selected a piece to move.
     message.setText("Red:  Make your move.");
     gameInProgress = true;
     newGameButton.setEnabled(false);
     resignButton.setEnabled(true);
     repaint();
  }
  
  void doNewAIGame() {
	  if (gameInProgress == true) {
          // This should not be possible, but it doens't 
          // hurt to check.
	      message.setText("Finish the current game first!");
	      return;
	  }
	  board.setUpGame();
	  currentPlayer = CheckersData.RED;
	  firstAI = new AI(CheckersData.BLACK, currentPlayer, "C:\\dev\\CS175---Combat-Checkers\\text.txt");
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
	  board.setUpGame();
	  currentPlayer = CheckersData.RED;
	  firstAI = new AI(currentPlayer, CheckersData.BLACK, "C:\\dev\\CS175---Combat-Checkers\\text.txt");
	  secondAI = new AI(CheckersData.BLACK, currentPlayer, "C:\\dev\\CS175---Combat-Checkers\\text2.txt");
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
         // This is called when the current player has chosen the specified
         // move.  Make the move, and then either end or continue the game
         // appropriately.

         
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
           if(currentPlayer == firstAI.player) {
	       		CheckersData copy = new CheckersData();
	       		copy.setUpGame(board.getBoardCopy());
	       		doMakeMove(firstAI.makeMove(copy));
       	   }
           repaint();
           return;
        }
     }
     
     /* The current player's turn is ended, so change to the other player.
        Get that player's legal moves.  If the player has no legal moves,
        then the game ends. */
     
     if(currentPlayer == firstAI.player) {
	     if (currentPlayer == CheckersData.RED) {
	        currentPlayer = CheckersData.BLACK;
	        legalMoves = board.getLegalMoves(currentPlayer);
	        if (legalMoves == null) {
	           firstAI.wonGame();
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
	           firstAI.wonGame();
	           gameOver("RED has no moves.  BLACK wins.");
	           return;
	        }
	        else if (legalMoves[0].isJump())
	           message.setText("RED:  Make your move.  You must jump.");
	        else
	           message.setText("RED:  Make your move.");
	     }
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
	     repaint();
     }
     else {
    	 if (currentPlayer == CheckersData.RED) {
 	        currentPlayer = CheckersData.BLACK;
 	        legalMoves = board.getLegalMoves(currentPlayer);
 	        if (legalMoves == null) {
 	           firstAI.lostGame();
 	           gameOver("BLACK has no moves.  RED wins.");
 	          System.out.println("null case");
 	          return;
 	        }
 	        else if (legalMoves[0].isJump()) {
 	           message.setText("BLACK:  Make your move.  You must jump.");
 	           System.out.println("isJump Case");
 	        }
 	        else {
 	           message.setText("BLACK:  Make your move.");
 	          System.out.println("final case");
 	        }
 	     }
 	     else {
 	        currentPlayer = CheckersData.RED;
 	        legalMoves = board.getLegalMoves(currentPlayer);
 	        if (legalMoves == null) {
 	           firstAI.lostGame();
 	           gameOver("RED has no moves.  BLACK wins.");
 	           return;
 	        }
 	        else if (legalMoves[0].isJump())
 	           message.setText("RED:  Make your move.  You must jump.");
 	        else
 	           message.setText("RED:  Make your move.");
 	     }
    	 repaint();
    	 CheckersData copy = new CheckersData();
    	 copy.setUpGame(board.getBoardCopy());
    	 doMakeMove(firstAI.makeMove(copy));
      }
     
     /* Set selectedRow = -1 to record that the player has not yet selected
         a piece to move. */
     
//     selectedRow = -1;
//     
//     /* As a courtesy to the user, if all legal moves use the same piece, then
//        select that piece automatically so the use won't have to click on it
//        to select it. */
//     
//     if (legalMoves != null) {
//        boolean sameStartSquare = true;
//        for (int i = 1; i < legalMoves.length; i++)
//           if (legalMoves[i].fromRow != legalMoves[0].fromRow
//                                || legalMoves[i].fromCol != legalMoves[0].fromCol) {
//               sameStartSquare = false;
//               break;
//           }
//        if (sameStartSquare) {
//           selectedRow = legalMoves[0].fromRow;
//           selectedCol = legalMoves[0].fromCol;
//        }
//     }
//     
//     /* Make sure the board is redrawn in its new state. */
//     
//     repaint();
     
  }  // end doMakeMove();
  
  void doMakeMoveAIvsAI(CheckersMove move) {
	  
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