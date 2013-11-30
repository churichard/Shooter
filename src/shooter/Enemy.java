package shooter;

import java.util.Random;

public class Enemy extends Entity{
	//random generator
	private Random randomGenerator = new Random();
	//enemy hp
	private int HP;
	//time the enemy was created
	private long lastBulletTime;
	//the damage the enemy deals when the player hits it
	private int atk;

	/* Constructor - gets the enemy sprite */
	public Enemy(Game game, String ref, int x, int y, int HP, long lastBulletTime, int atk){
		super(game, game.getSprite(ref), ref, x, y);

		this.HP = HP;
		this.lastBulletTime = lastBulletTime;
		this.atk = atk;
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
	
	/* Returns the atk value of the enemy */
	public int getATK(){
		return atk;
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