package shooter;

import java.util.Random;

public class Enemy extends Entity{
	//game class
	private Game game;
	//random generator
	private Random randomGenerator = new Random();
	//enemy name
	private String name;
	//enemy hp
	private int HP;
	//time the enemy was created
	private long lastBulletTime;

	/* Constructor - gets the enemy sprite */
	public Enemy(Game game, String ref, int x, int y, int HP, long lastBulletTime){
		super(game.getSprite(ref), x, y);

		this.game = game;
		this.name = ref;
		this.HP = HP;
		this.lastBulletTime = lastBulletTime;
	}

	/* Generates the enemy x position */
	public int generateEnemyX(){
		do{
			x = randomGenerator.nextInt(game.getDisplayWidth()+1);
		}while(x > game.getDisplayWidth()-this.getSprite().getWidth());

		return x;
	}
	
	/* Reduces the enemy HP */
	public void reduceHP(int atk){
		HP -= atk;
		if (HP <= 0){
			HP = 0;
			game.removeEntity(this);
		}
		else
			game.registerHit(this);
	}
	
	/* Returns the name of the enemy */
	public String getName(){
		return name;
	}
	
	/* Returns the amount of time that has elapsed since the last bullet fired */
	public long getTimeSinceLastBullet(){
		long time = Delta.getDelta("beginning");
		long timeSinceLastBullet = time - lastBulletTime;
		return timeSinceLastBullet;
	}
	
	/* Sets the time that the last bullet was shot at */
	public void setLastBulletTime(long lastBulletTime){
		this.lastBulletTime = lastBulletTime;
	}

	/* This enemy has collided with another entity */
	public void collidedWith(Entity other){
		//the enemy collision is handled elsewhere
	}
}