package test;


import java.util.HashMap;
import java.awt.Point;


class PowerUpSystem {
	
	// MEMBER VARIABLES :-----------------------------------------------------------------
	// PowerUp Types
	 public static final int 
		WEAPON = 10,
		BUFF = 20,
		HEX = 30;
	 
	 // Weapons
	 public static final int 
		LASER = 10,
		BOMB = 20,
		AIR_STRIKE = 30;
	 
	 // Buffs
//	 public static final int 

	 
	 // Hexes
//	 public static final int 
	 
	 // PowerUp look-up tables
	 public HashMap<Point, Integer> p1_powers;
	 public HashMap<Point, Integer> p2_powers;
	 

	 // MEMBER FUNCTIONS :-----------------------------------------------------------------
	 // CONSTRUCTOR
	 public PowerUpSystem() 
	 {
		 p1_powers = new HashMap<Point, Integer>();
		 p2_powers = new HashMap<Point, Integer>();
	 }
	 
	 // Roll. If true, spawn a power-up on a random empty tile on the board.
	 public CheckersData spawnRoll(CheckersData board)
	 {
		 return board;
	 }
	 
	 // Execute a power from the user to the target.
	 public CheckersData usePower(CheckersData board, Point user, Point target)
	 {
		 return board;
	 }
}
