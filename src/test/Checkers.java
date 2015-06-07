package test;

/*
   This applet lets two uses play checkers against each other.
   Red always starts the game.  If a player can jump an opponent's
   piece, then the player must jump.  When a player can make no more
   moves, the game ends.
   
   This file defines four classes: the main applet class, Checkers;
   CheckersCanvas, CheckersMove, and CheckersData.
   (This is not very good style; the other classes really should be
   nested classes inside the Checkers class.)
*/

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class Checkers extends JPanel {

   /* The main applet class only lays out the applet.  The work of
      the game is all done in the CheckersCanvas object.   Note that
      the Buttons and Label used in the applet are defined as 
      instance variables in the CheckersCanvas class.  The applet
      class gives them their visual appearance and sets their
      size and positions.*/

   public static void main (String[] args) {
      JFrame window = new JFrame("Combat Checkers");
      Checkers content = new Checkers();
      window.setContentPane(content);
      window.pack();
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation((screensize.width - window.getWidth())/2, (screensize.height - window.getHeight())/2);
      window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      window.addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing (java.awt.event.WindowEvent evt) {
            if(CheckersCanvas.firstAI != null) CheckersCanvas.firstAI.dumpToFile();
            if(CheckersCanvas.secondAI != null) CheckersCanvas.secondAI.dumpToFile();
         }
      });

      window.setVisible(true);
   }

   public static class Applet extends JApplet {
      public void init () {
         setContentPane(new Checkers());
      }
   }

   public Checkers () {
	   setLayout(new GridBagLayout());
	      setPreferredSize(new Dimension(500, 400));

	      GridBagConstraints c = new GridBagConstraints();
	   
	      setBackground(new Color(0, 102, 0));
	      CheckersCanvas board = new CheckersCanvas();

	      c.gridx = 0;
	      c.gridy = 0;
	      c.gridwidth = 1;
	      c.gridheight = 4;
	      c.insets = new Insets(5, 5, 5, 5);
	      c.weightx = 1;
	      c.weighty = 1;
	      c.fill = GridBagConstraints.BOTH;
	      add(board, c);

	      c.gridx = 1;
	      c.gridy = 0;
	      c.gridwidth = 1;
	      c.gridheight = 1;
	      c.weightx = 0;
	      c.weighty = 0;
	      c.fill = GridBagConstraints.HORIZONTAL;
	      board.newGameButton.setBackground(new Color(238, 238, 238));
	      add(board.newGameButton, c);

	      c.gridx = 1;
	      c.gridy = 1;
	      c.gridwidth = 1;
	      c.gridheight = 1;
	      c.weightx = 0;
	      c.weighty = 0;
	      c.fill = GridBagConstraints.HORIZONTAL;
	      board.resignButton.setBackground(new Color(238, 238, 238));
	      add(board.resignButton, c);

	      c.gridx = 0;
	      c.gridy = 4;
	      c.gridwidth = 2;
	      c.gridheight = 1;
	      c.insets = new Insets(5, 0, 0, 0);
	      c.weightx = 1;
	      c.weighty = 0;
	      c.fill = GridBagConstraints.BOTH;
	      board.message.setOpaque(true);
	      board.message.setBackground(new Color(238, 238, 238));
	      board.message.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
	      board.message.setBorder(new EmptyBorder(5, 5, 5, 5));
	      add(board.message, c);
   }
} // end class Checkers