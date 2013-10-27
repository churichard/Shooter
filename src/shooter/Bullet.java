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
	//name of the bullet
	private String name;

	/* Constructor - gets the bullet sprite */
	public Bullet(Game game, String ref, int x, int y, int attack){
		super(game.getSprite(ref), x, y);

		this.game = game;
		name = ref;
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
	
	/* Returns the name of the bullet */
	public String getName(){
		return name;
	}

	/* This bullet has collided with another entity */
	public void collidedWith(Entity other){
		if ((name.equals("bullet") || name.equals("laser")) && other instanceof Enemy){
			((Enemy) other).reduceHP(atk);
			game.removeEntity(this);
		}
		else if ((name.equals("enemy_bullet") || name.equals("boss_bullet")) && other instanceof Player){
			((Player) other).reduceHP(atk);
			game.removeEntity(this);
		}
	}
}