package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.awt.Point;


//CLASS: PowerUpSystem :--------------------------------------------------------
class PowerUpSystem {
	
// MEMBER VARIABLES
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
	 
	 // PowerUpSelector
	 private PowerUpSelector pSelector;
	 

// MEMBER FUNCTIONS
	 // Constructor
	 public PowerUpSystem() 
	 {
		 p1_powers = new HashMap<Point, Integer>();
		 p2_powers = new HashMap<Point, Integer>();
		 
		 pSelector = new PowerUpSelector();
	 }
	 
	 public PowerUp getRandomPowerUp()
	 {
		 return pSelector.getRandom();
	 }
	 
//	 // Roll. If true, spawn a power-up on a random empty tile on the board.
//	 public CheckersData spawnRoll(CheckersData board)
//	 {
//		 return board;
//	 }
//	 
//	 // Execute a power from the user to the target.
//	 public CheckersData usePower(CheckersData board, Point user, Point target)
//	 {
//		 return board;
//	 }
	 
	 
	 
} // END CLASS :----------------------------------------------------------------



// CLASS: PowerUpSelector :-----------------------------------------------------
class PowerUpSelector {
    
	ArrayList<PowerUp> weapons = new ArrayList<PowerUp>();

    int totalSum = 0;
    Random rand = new Random();

    PowerUpSelector() 
    {
    	weapons.add(new Laser(60));
    	weapons.add(new Bomb(30));
    	weapons.add(new AirStrike(10));
    	
        for(PowerUp weapon : weapons) {
            totalSum = totalSum + weapon.getProbability();
        }
    }

    public PowerUp getRandom() {

        int index = rand.nextInt(totalSum);
        int sum = 0;
        int i=0;
        while(sum < index ) {
             sum = sum + weapons.get(i++).getProbability();
        }
        return weapons.get(Math.max(0,i-1));
    }
    
} // END CLASS :----------------------------------------------------------------
