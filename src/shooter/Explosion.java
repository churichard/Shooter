package shooter;

public class Explosion extends Entity{
	//the time that the explosion was created
	protected long timeAtCreation;
	//explosion or enemy_hit
	private String name;
	//entity that the explosion is associated with
	private Entity ent;
	
	/* Creates a new instance of Explosion */
	public Explosion(Game game, String ref, String name, int x, int y, Entity ent){
		super(game.getSprite(ref), x, y);
		
		timeAtCreation = Delta.getTime();
		this.ent = ent;
		this.name = name;
	}
	
	/* Checks to see if the explosion should continue to be drawn (if it has lasted for less than a set amount of time) */
	public boolean continueDrawing(){
		int t = 500;
		if (name.equals("enemy_hit") || name.equals("player_hit"))
			t = 100;
		return !(Delta.getTime() - timeAtCreation >= t);
	}
	
	/* Returns the entity that the explosion is for */
	public Entity getEntity(){
		return ent;
	}
	
	/* Returns the name of the explosion */
	public String getName(){
		return name;
	}
	
	/* Handles collisions */
	public void collidedWith(Entity other) {
		//do nothing on collision
	}
}