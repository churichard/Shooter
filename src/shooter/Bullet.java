package shooter;

public class Bullet extends Entity{
	//game class
	private Game game;
	//attack value
	private int atk;
	//x change
	private int xChange;
	//y change
	private int yChange;

	/* Constructor - gets the bullet sprite */
	public Bullet(Game game, String ref, int x, int y, int attack){
		super(game.getSprite(ref), x, y);

		this.game = game;
		atk = attack;
	}
	
	/* Returns the atk value */
	public int getATK(){
		return atk;
	}
	
	/* Returns the x change */
	public int getXChange(){
		return xChange;
	}
	
	/* Returns the y change */
	public int getYChange(){
		return yChange;
	}
	
	/* Sets the x change */
	public void setXChange(int change){
		xChange = change;
	}
	
	/* Sets the y change */
	public void setYChange(int change){
		yChange = change;
	}

	/* This bullet has collided with another entity */
	public void collidedWith(Entity other){
		if (other instanceof Enemy){
			((Enemy) other).reduceHP(atk);
			game.removeEntity(this);
		}
	}
}