package shooter;

public class Player extends Entity{
	//player hp
	private int HP;

	/* Constructor - sets the player coordinates and gets the player sprite */
	public Player(Game game, String ref, int x, int y, int HP){
		super(game, game.getSprite(ref), ref, x, y);
		
		this.HP = HP;
	}
	
	/* Reduces the player HP */
	public void reduceHP(int atk){
		HP -= atk;
		if (HP <= 0){
			HP = 0;
			game.removeEntity(this);
		}
		else
			game.registerHit(this);
	}
	
	/* Sets the player HP */
	public void setHP(int hp){
		HP = hp;
	}
	
	/* Returns the current player HP */
	public int getHP(){
		return HP;
	}

	/* This player has collided with another entity */
	public void collidedWith(Entity other){
		if (other instanceof Enemy){
			this.reduceHP(((Enemy) other).getATK());
			if (!((Enemy) other).getName().equals("boss"))
				((Enemy) other).reduceHP(500);
		}
	}
}