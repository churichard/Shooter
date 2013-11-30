package shooter;

public class Powerup extends Entity{
	
	/* Constructor - gets the powerup sprite */
	public Powerup(Game game, String ref, int x, int y) {
		super(game, game.getSprite(ref), ref, x, y);
	}

	/* The powerup collided with the player */
	public void collidedWith(Entity other){
		if (other instanceof Player){
			game.removeEntity(this);
		}
	}
}