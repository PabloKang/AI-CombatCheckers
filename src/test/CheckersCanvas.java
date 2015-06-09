package test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.*;

import javafx.application.Application;
import javafx.util.Pair;


class CheckersCanvas extends Canvas implements ActionListener, MouseListener {


	JButton resignButton;
	JButton newGameButton;

	JLabel message;

	CheckersData board;

	private HashMap<String, BufferedImage> images;

	private int boardLength;
	private double xOffset;
	private double yOffset;
	
	public static AI firstAI;
	public static AI secondAI;
	public static AI firstCombatAI;
	public static AI secondCombatAI;
	public static int turnNumber = 0;
	boolean gameInProgress;

	boolean vsAI = false;
	boolean combatMode = false;

	int currentPlayer;
	int selectedRow, selectedCol;
	CheckersMove[] legalMoves;
	int winner;
	
	double[] w1 = {0.15, 0.4, 0.20, 0.25}; // weight vectors // { MoveDifferenceHeuristic, PieceDifferenceHeuristic,
	double[] w2 = {0.15, 0.7, 0.05, 0.1};					 //  Distance Heuristic, ProtectedPiecesHeuristic     }
	double[] w3 = {0.25, 0.4, 0.15, 0.2};
	double[] w4 = {0.2, 0.5, 0.1, 0.2};
	
	public CheckersCanvas() {
		setBackground(Color.black);
		addMouseListener(this);

		setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		resignButton = new JButton("Resign");
		resignButton.addActionListener(this);

		newGameButton = new JButton("New Game");
		newGameButton.addActionListener(this);

		message = new JLabel("", JLabel.CENTER);
		board = new CheckersData();

		images = new HashMap<>();
		try {
			images.put("piece_black", ImageIO.read(new File(".\\images\\piece_black.png")));
			images.put("piece_red", ImageIO.read(new File(".\\images\\piece_red.png")));
			images.put("king_black", ImageIO.read(new File(".\\images\\king_black.png")));
			images.put("king_red", ImageIO.read(new File(".\\images\\king_red.png")));
			images.put("weapon_open", ImageIO.read(new File(".\\images\\weapon_open.png")));
			images.put("weapon_taken", ImageIO.read(new File(".\\images\\weapon_taken.png")));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		setupGame();
	}
	
	public void setupGame() {
		String[] modes = {"Combat", "Normal"};
		String mode = (String) JOptionPane.showInputDialog(null, "Mode:", "Setup", JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);

		combatMode = (mode.equals("Combat"));

		String[] choices = {"AI vs AI", "AI vs Player", "Player vs AI", "Player vs Player"};
		String choice = (String) JOptionPane.showInputDialog(null, "Game type:", "Setup", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

		if (choice != null) {
			switch (choice) {
				case "AI vs AI":
					vsAI = false;
					doNewAIvsAIGame();
					break;
				case "AI vs Player":
					vsAI = true;
					doNewAIGame(CheckersData.RED);
					break;
				case "Player vs AI":
					vsAI = true;
					doNewAIGame(CheckersData.BLACK);
					break;
				default:
					vsAI = false;
					doNewGame();
			}
		} else {
			System.exit(0);
		}
	}

	
	
	public void actionPerformed(ActionEvent evt) {
		// Respond to user's click on one of the two buttons.
		Object src = evt.getSource();
		if (src == newGameButton)
			setupGame();
		else if (src == resignButton)
			doResign();
			
	}

	
	
	void doNewGame() {
		if (gameInProgress) {
			message.setText("Finish the current game first!");
			return;
		}
		turnNumber = 0;
		board.setUpGame();
		currentPlayer = CheckersData.RED;
		legalMoves = board.getLegalMoves(CheckersData.RED); 
		selectedRow = -1; 
		message.setText("Red:  Make your move.");
		gameInProgress = true;
		newGameButton.setEnabled(false);
		resignButton.setEnabled(true);
		repaint();
	} // end doNewGame()

	void doNewAIGame(int aiPlayer) {
		if (gameInProgress) {
			message.setText("Finish the current game first!");
			return;
		}
		turnNumber = 0;
		board.setUpGame();
		currentPlayer = CheckersData.RED;
		int opponent;
		if(aiPlayer == currentPlayer)
			opponent = CheckersData.BLACK;
		else
			opponent = currentPlayer;
		firstAI = new AI(aiPlayer, opponent, ".\\data\\text.txt", false, w1, w2);
		legalMoves = board.getLegalMoves(CheckersData.RED);  // Get RED's legal moves.
		selectedRow = -1;   // RED has not yet selected a piece to move.
		message.setText("Red:  Make your move.");
		gameInProgress = true;
		newGameButton.setEnabled(false);
		resignButton.setEnabled(true);
		repaint();
		if(firstAI.player == currentPlayer) {
			CheckersData copy = new CheckersData();
			copy.setUpGame(board.getBoardCopy());
			doMakeMoveAI(firstAI.makeMove(copy));
		}
	}

	void doNewAIvsAIGame() {
		if (gameInProgress) {
			message.setText("Finish the current game first!");
			return;
		}
		turnNumber = 0;
		board.setUpGame();
		currentPlayer = CheckersData.RED;

		firstAI = new AI(currentPlayer, CheckersData.BLACK, ".\\data\\text.txt", false, w1, w2);
		secondAI = new AI(CheckersData.BLACK, currentPlayer, ".\\data\\text2.txt", false, w1, w2);
		legalMoves = board.getLegalMoves(CheckersData.RED);
		selectedRow = -1;
		message.setText("Red: Make your move.");
		gameInProgress = true;
		newGameButton.setEnabled(false);
		resignButton.setEnabled(true);
		repaint();
		startAIvsAIGame();
	}
	


	void doResign() {
		// Current player resigns.  Game ends.  Opponent wins.
		if (!gameInProgress) {
			message.setText("There is no game in progress!");
			return;
		}
		if (currentPlayer == CheckersData.RED) 
			gameOver("RED resigns.  BLACK wins.");         
		else
			gameOver("BLACK resigns.  RED wins.");
	}

	void doResignAI() {
		if (!gameInProgress) {
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
		gameInProgress = false;
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
	
	
	void doClickSquare(int row, int col) {

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


		if (selectedRow < 0) {
			message.setText("Click the piece you want to move.");
			return;
		}
		

		for (int i = 0; i < legalMoves.length; i++)
		if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
				&& legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
			if(!vsAI)
				doMakeMove(legalMoves[i]);
			else
				doMakeMoveAI(legalMoves[i]);
			return;
		}
		
		message.setText("Click the square you want to move to.");

	}  // end doClickSquare()


	void doMakeMove(CheckersMove move) {

		// This is called when the current player has chosen the specified
		// move.  Make the move, and then either end or continue the game
		// appropriately.
		turnNumber++;
		board.makeMove(move);
		
		// Check if PowerUp needs to spawn
		if (combatMode)
			board = board.powerUpSys.spawnPowerUp(board);
		
		/* If the move was a jump, it's possible that the player has another
		jump.  Check for legal jumps starting from the square that the player
		just moved to.  If there are any, the player must jump.  The same
		player continues moving.
		 */
		
		if (move.isJump()) {
			
			legalMoves = board.getLegalJumpsFrom(currentPlayer, move.toRow, move.toCol);
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
		

		
		
		selectedRow = -1;
		
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
		
	}  // end doMakeMove();

	void doMakeMoveAI(CheckersMove move) {
		turnNumber++;
		
		// Check if PowerUp needs to spawn
		if (combatMode)
			board = board.powerUpSys.spawnPowerUp(board);
		
		if(currentPlayer == firstAI.player && move == null) {
			doResignAI();
			return;
		}

		board.makeMove(move);
		repaint();
		
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
				if(firstAI.player == currentPlayer) {
					CheckersData copy = new CheckersData();
					copy.setUpGame(board.getBoardCopy());
					doMakeMoveAI(firstAI.makeMove(copy));
				}
				return;
			}
		}
		
		
		if (currentPlayer == CheckersData.RED) {
			currentPlayer = CheckersData.BLACK;
			legalMoves = board.getLegalMoves(currentPlayer);
			if (legalMoves == null) {
				gameOver("BLACK has no moves.  RED wins.");
				if(firstAI.player == currentPlayer)
					firstAI.lostGame();
				else firstAI.wonGame();
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
				if(firstAI.player == currentPlayer)
					firstAI.lostGame();
				else firstAI.wonGame();
				return;
			}
			else if (legalMoves[0].isJump())
				message.setText("RED:  Make your move.  You must jump.");
			else
				message.setText("RED:  Make your move.");
		}
		
		selectedRow = -1;
		
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
		
		if(firstAI.player == currentPlayer) {
			CheckersData copy = new CheckersData();
			copy.setUpGame(board.getBoardCopy());
			doMakeMoveAI(firstAI.makeMove(copy));
		}
		
		repaint();
	} // end doMakeMoveAI()
	
	void startAIvsAIGame() {

		while(gameInProgress) {
			repaint();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(firstAI.player == currentPlayer) 
				doMakeMoveAI(firstAI.makeMove(board));
			else
				doMakeMoveAI(secondAI.makeMove(board));
		}
		String msg = "AI vs AI game over. Winner : ";
		switch(winner) {
		case 0:
			msg += "Draw";
			break;
		case 1:
			msg += "Red";
			break;
		case 2:
			msg += "Black";
			break;
		}
		message.setText(msg);
		repaint();
	}
	
	void doMakeMoveAIvsAI(CheckersMove move) {
		turnNumber++;
		
		// Check if PowerUp needs to spawn
		if (combatMode)
			board = board.powerUpSys.spawnPowerUp(board);
		
		System.out.println("Game still happening");
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
				return;
			}
			else if(legalMoves[0].isJump())
				message.setText("BLACK: Make your move. You must jump.");
			else 
				message.setText("BLACK: Make your move.");
		}
		else {
			currentPlayer = CheckersData.RED;
			legalMoves = board.getLegalMoves(currentPlayer);
			if(legalMoves == null) {
				gameOver("RED has no moves. BLACK wins.");
				firstAI.lostGame();
				secondAI.wonGame();
				winner = 2;
				return;
			}
			else if(legalMoves[0].isJump()) 
				message.setText("RED: Make your move. You must jump.");
			else
				message.setText("RED: Make your move.");
		}
		repaint();
		
	}


	public void update(Graphics g) {
		// The paint method completely redraws the canvas, so don't erase
		// before calling paint().
		paint(g);
	}


	public void paint(Graphics g) {
		boardLength = (getWidth() <= getHeight()) ? getWidth() : getHeight();
		xOffset = (double) (getWidth() - boardLength) / 2;
		yOffset = (double) (getHeight() - boardLength) / 2;

		BufferedImage scene = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = scene.createGraphics();
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g2D.setColor(new Color(0, 102, 0));
		g2D.fillRect(0, 0, getWidth(), getHeight());

		drawCheckerboardBase(g2D, Color.black);

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if (row % 2 == col % 2) {
					drawCheckerboardSquare(g2D, new Color(187, 187, 187), col, row);
				} else {
					drawCheckerboardSquare(g2D, new Color(102, 102, 102), col, row);
				}

				drawCheckersPiece(g2D, board.pieceAt(row, col), col, row);
				if (CheckersData.parsePowerType(board.pieceAt(row, col)) > 0) {
					drawPowerUp(g2D, board.pieceAt(row, col), col, row);
				}
			}
		}

		if (gameInProgress) {
			for (int i = 0; i < legalMoves.length; i++) {
				drawHighlight(g2D, new Color(0, 238, 238), legalMoves[i].fromCol, legalMoves[i].fromRow);
			}

			if (selectedRow >= 0) {
				drawHighlight(g2D, Color.white, selectedCol, selectedRow);
				for (int i = 0; i < legalMoves.length; i++) {
					if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow)
						drawHighlight(g2D, new Color(0, 238, 0), legalMoves[i].toCol, legalMoves[i].toRow);
				}
			}
		}

		g.drawImage(scene, 0, 0, this);
	}

	private void drawCheckerboardBase(Graphics2D g, Color c) {
		g.setColor(c);
		g.fill(new Rectangle2D.Double(xOffset, yOffset, boardLength, boardLength));
	}

	private void drawCheckerboardSquare(Graphics2D g, Color c, int col, int row) {
		double padding = (double) boardLength * 0.01;
		if (padding < 2) padding = 2;

		double length = ((double) boardLength - 2 * padding) / 8;
		double x = padding + length * col;
		double y = padding + length * row;

		g.setColor(c);
		g.fill(new Rectangle2D.Double(x + xOffset, y + yOffset, length, length));
	}

	private void drawCheckersPiece(Graphics2D g, int code, int col, int row) {
		double boardPadding = (double) boardLength * 0.01;
		if (boardPadding < 2) boardPadding = 2;

		double squareLength = ((double) boardLength - 2 * boardPadding) / 8;

		double x = boardPadding + squareLength * col;
		double y = boardPadding + squareLength * row;
		AffineTransform at = new AffineTransform();
		at.translate(x + xOffset, y + yOffset);

		BufferedImage image;

		switch (CheckersData.parsePiece(code)) {
			case CheckersData.RED:
				image = images.get("piece_red");
				at.scale(squareLength / image.getWidth(), squareLength / image.getHeight());
				g.drawImage(image, at, this);
				break;
			case CheckersData.BLACK:
				image = images.get("piece_black");
				at.scale(squareLength / image.getWidth(), squareLength / image.getHeight());
				g.drawImage(image, at, this);
				break;
			case CheckersData.RED_KING:
				image = images.get("king_red");
				at.scale(squareLength / image.getWidth(), squareLength / image.getHeight());
				g.drawImage(image, at, this);
				break;
			case CheckersData.BLACK_KING:
				image = images.get("king_black");
				at.scale(squareLength / image.getWidth(), squareLength / image.getHeight());
				g.drawImage(image, at, this);
				break;
		}
	}

	private void drawPowerUp(Graphics2D g, int code, int col, int row) {
		if (CheckersData.parsePowerType(code) > 0) {
			double boardPadding = (double) boardLength * 0.01;
			if (boardPadding < 2) {
				boardPadding = 2;
			}

			double squareLength = ((double) boardLength - 2 * boardPadding) / 8;

			double x = boardPadding + squareLength * col;
			double y = boardPadding + squareLength * row;
			AffineTransform at = new AffineTransform();
			at.translate(x + xOffset, y + yOffset);

			BufferedImage image;

			String powerType = "none";
			String powerStatus = "open";

			switch (CheckersData.parsePowerType(code)) {
				case 1:
					powerType = "weapon";
					break;
				case 2:
					powerType = "buff";
					break;
				case 3:
					powerType = "hex";
					break;
			}

			if (!powerType.equals("none")) {
				if (CheckersData.parsePiece(code) > 0) {
					powerStatus = "taken";
				}

				image = images.get(powerType + "_" + powerStatus);
				at.scale(squareLength / image.getWidth(), squareLength / image.getHeight());
				g.drawImage(image, at, this);
			}
		}
	}

	private void drawHighlight(Graphics2D g, Color c, int col, int row) {
		double padding = (double) boardLength * 0.01;
		if (padding < 2) padding = 2;

		double length = ((double) boardLength - 2 * padding) / 8;
		double x = padding + length * col;
		double y = padding + length * row;

		double weight = length * 0.04;
		if (weight < 1) weight = 1;

		g.setColor(c);
		g.setStroke(new BasicStroke((float) weight));
		g.draw(new Rectangle2D.Double(x + xOffset, y + yOffset, length - 1, length - 1));
	}

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
		if (!gameInProgress)
			message.setText("Click \"New Game\" to start a new game.");
		else {
			double padding = (double) boardLength * 0.01;
			double squareLength = ((double) boardLength - padding * 2) / 8;

			int col = (int) ((evt.getX() - 2 - xOffset) / squareLength);
			int row = (int) ((evt.getY() - 2 - yOffset) / squareLength);

			if (col >= 0 && col < 8 && row >= 0 && row < 8)
				doClickSquare(row,col);
		}
	}


	public void mouseReleased(MouseEvent evt) { }
	public void mouseClicked(MouseEvent evt) { }
	public void mouseEntered(MouseEvent evt) { }
	public void mouseExited(MouseEvent evt) { }
	
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
	private void trainAI() {
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

		if(p1move >= 2 && p2move >= 2)
			return true;
		else return false;
		
	}
	

}  // end class SimpleCheckerboardCanvas