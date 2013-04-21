package shooter;

public class Bullet extends Entity{
	//game class
	private Game game;
	//attack value
	private int atk;

	/*
	 * Constructor - gets the bullet sprite
	 */
	public Bullet(Game game, String ref, int x, int y, int attack){
		super(game.getSprite(ref), x, y);

		this.game = game;
		atk = attack;
	}
	
	/*
	 * Returns the atk value
	 */
	public int getATK(){
		return atk;
	}

	/*
	 * This bullet has collided with another entity
	 */
	public void collidedWith(Entity other){
		if (other instanceof Enemy){
			((Enemy) other).reduceHP(atk);
			game.removeEntity(this);
		}
	}
}