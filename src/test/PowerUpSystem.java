package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.awt.Point;


//CLASS: PowerUpSystem :--------------------------------------------------------
class PowerUpSystem {
	
// MEMBER VARIABLES
	// PowerUp spawn probability out of 100
	public static final int spawnThresh = 20;
	
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
	public HashMap<Point, PowerUp> blk_powers;
	public HashMap<Point, PowerUp> red_powers;
	 
	// PowerUpSelector
	private PowerUpSelector pSelector;
	 

// MEMBER FUNCTIONS
	// Constructor
	public PowerUpSystem() 
	{
		blk_powers = new HashMap<Point, PowerUp>();
		red_powers = new HashMap<Point, PowerUp>();
		
		pSelector = new PowerUpSelector();
	}
	
	
	public PowerUp getRandomPowerUp(int type)
	{
		return pSelector.randomPowerUp(type);
	}
	 
	
	public void listPowerUp(Point p, int piece, PowerUp pUp) 
	{
		if(piece == CheckersData.BLACK || piece == CheckersData.BLACK_KING ) {
			blk_powers.put(p, pUp);
		}
		else if(piece == CheckersData.RED || piece == CheckersData.RED_KING ) {
			red_powers.put(p, pUp);
		}
	}
	
	
	public void movePowerUp(Point p, int piece, Point q)
	{
		PowerUp pUp;
		
		if(piece == CheckersData.BLACK || piece == CheckersData.BLACK_KING ) {
			pUp = blk_powers.remove(p);
			blk_powers.put(q, pUp);
		}
		else if(piece == CheckersData.RED || piece == CheckersData.RED_KING ) {
			pUp = red_powers.remove(p);
			red_powers.put(q, pUp);
		}
	}
	
	
	public void removePowerUp(Point p, int piece) 
	{
		if(piece == CheckersData.BLACK || piece == CheckersData.BLACK_KING ) {
			blk_powers.remove(p);
		}
		else if(piece == CheckersData.RED || piece == CheckersData.RED_KING ) {
			red_powers.remove(p);
		}
	}
	
	
	// Execute a power from the user to the target.
	public CheckersData usePowerUp(CheckersData board, Point user, Point target)
	{
		int piece = board.pieceAt(user.y, user.x);
		 
		if(piece == CheckersData.BLACK || piece == CheckersData.BLACK_KING ) {
			return blk_powers.get(user).execute(board, user, target);
		}
		else if(piece == CheckersData.RED || piece == CheckersData.RED_KING ) {
			return red_powers.get(user).execute(board, user, target);
		}
		return board;
	}
	
	
	// Roll. If true, spawn a power-up on a random empty tile on the board.
	public CheckersData spawnPowerUp(CheckersData board)
	{
		Random rand = new Random();
		if (rand.nextInt(100) <= spawnThresh) {
			int tries = 20;
			do {
				int row = rand.nextInt(CheckersData.HEIGHT-1);
				int col = rand.nextInt(CheckersData.WIDTH-1);
				if(row % 2 == col % 2 && board.pieceAt(row, col) == CheckersData.EMPTY){
					board.setPieceAt(row, col, pSelector.randomType()*10);
					System.out.println("Spawned PowerUp at [" + row + "]["+col+"]");
					break;
				}

				tries--;
			} while( tries > 0);

		}
		return board;
	}
	 
} // END CLASS :----------------------------------------------------------------



// CLASS: PowerUpSelector :-----------------------------------------------------
class PowerUpSelector {
    
	ArrayList<PowerUp> types = new ArrayList<PowerUp>();
	ArrayList<PowerUp> weapons = new ArrayList<PowerUp>();
	ArrayList<PowerUp> buffs = new ArrayList<PowerUp>();
	ArrayList<PowerUp> hexes = new ArrayList<PowerUp>();

    int totalSum = 100;
    Random rand = new Random();

    
    PowerUpSelector() 
    {
    	types.add(new Weapon(100));
    	weapons.add(new Laser(60));
    	weapons.add(new Bomb(30));
    	weapons.add(new AirStrike(10));
    }

    
    public int randomType() {
        int index = rand.nextInt(totalSum);
        int sum = 0;
        int i=0;
        
        while(sum < index ) {
            sum = sum + types.get(i++).getProbability();
       }
       return types.get(Math.max(0,i-1)).id;
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
