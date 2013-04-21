package shooter;

public class Player extends Entity{
	//game class
	private Game game;

	/*
	 * Constructor - sets the player coordinates and gets the player sprite
	 */
	public Player(Game game, String ref, int x, int y){
		super(game.getSprite(ref), x, y);

		this.game = game;
	}

	/*
	 * This player has collided with another entity
	 */
	public void collidedWith(Entity other){
		if (other instanceof Enemy){
			((Enemy) other).reduceHP(500);
			game.removeEntity(this);
		}
	}
}