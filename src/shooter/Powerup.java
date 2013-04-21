package shooter;

public class Powerup extends Entity{
	//game class
	private Game game;
	//name of powerup
	private String name;

	/*
	 * Constructor - gets the powerup sprite
	 */
	public Powerup(Game game, String ref, String name, int x, int y) {
		super(game.getSprite(ref), x, y);

		this.game = game;
		this.name = name;
	}
	
	/*
	 * Returns the name of the powerup
	 */
	public String getName(){
		return name;
	}

	/*
	 * The powerup collided with the player
	 */
	public void collidedWith(Entity other){
		if (other instanceof Player){
			game.removeEntity(this);
		}
	}
}