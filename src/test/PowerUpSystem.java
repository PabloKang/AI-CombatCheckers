package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.awt.Point;


//CLASS: PowerUpSystem :--------------------------------------------------------
class PowerUpSystem {
	
// MEMBER VARIABLES
	// PowerUp types
	public static final int 
		WEAPON = 1,
		BUFF = 2,
		HEX = 3;
	 
	 // Weapons
	public static final int 
		LASER = 1,
		BOMB = 2,
		AIR_STRIKE = 3;
	 
	// Buffs
	//	 public static final int 

	 
	// Hexes
	//	 public static final int 
	 
	// PowerUp look-up tables
	public HashMap<Point, PowerUp> p1_powers;
	public HashMap<Point, PowerUp> p2_powers;
	 
	// PowerUpSelector
	private PowerUpSelector pSelector;
	 

// MEMBER FUNCTIONS
	// Constructor
	public PowerUpSystem() 
	{
		p1_powers = new HashMap<Point, PowerUp>();
		p2_powers = new HashMap<Point, PowerUp>();
		
		pSelector = new PowerUpSelector();
	}
	
	public PowerUp getRandomPowerUp(int type)
	{
		return pSelector.randomPowerUp(type);
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
	ArrayList<PowerUp> buffs = new ArrayList<PowerUp>();
	ArrayList<PowerUp> hexes = new ArrayList<PowerUp>();

    int totalSum = 100;
    Random rand = new Random();

    PowerUpSelector() 
    {
    	weapons.add(new Laser(60));
    	weapons.add(new Bomb(30));
    	weapons.add(new AirStrike(10));
    }

    public PowerUp randomPowerUp(int type) {

        int index = rand.nextInt(totalSum);
        int sum = 0;
        int i=0;
        
        if(type == PowerUpSystem.WEAPON) {
            while(sum < index ) {
                sum = sum + weapons.get(i++).getProbability();
           }
           return weapons.get(Math.max(0,i-1));
        }
        else if(type == PowerUpSystem.BUFF) {
            while(sum < index ) {
                sum = sum + buffs.get(i++).getProbability();
           }
           return buffs.get(Math.max(0,i-1));
        }
        else if(type == PowerUpSystem.HEX) {
            while(sum < index ) {
                sum = sum + hexes.get(i++).getProbability();
           }
           return hexes.get(Math.max(0,i-1));
        }
        else {
        	return null;
        }
    }
    
} // END CLASS :----------------------------------------------------------------
