package shooter;

public class Explosion extends Entity{
	//delta class
	protected Delta delta = new Delta();
	//the time that the explosion was created
	protected long timeAtCreation;
	//explosion or enemy_hit
	private String name;
	
	/*
	 * Creates a new instance of Explosion
	 */
	public Explosion(Game game, String ref, String name, int x, int y){
		super(game.getSprite(ref), x, y);
		
		timeAtCreation = delta.getTime();
		this.name = name;
	}
	
	/*
	 * Checks to see if the explosion should continue to be drawn (if it has lasted for less than a set amount of time)
	 */
	public boolean continueDrawing(){
		int t = 500;
		if (name.equals("enemy_hit"))
			t = 100;
		return !(delta.getTime() - timeAtCreation >= t);
	}
	
	/*
	 * Returns the name of the explosion
	 */
	public String getName(){
		return name;
	}
	
	/*
	 * Handles collisions
	 */
	public void collidedWith(Entity other) {
		//do nothing on collision
	}
}