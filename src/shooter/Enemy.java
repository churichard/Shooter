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

	/* Constructor - gets the enemy sprite */
	public Enemy(Game game, String ref, String name, int x, int y, int hp){
		super(game.getSprite(ref), x, y);

		this.game = game;
		this.name = name;
		HP = hp;
	}

	/* Generates the enemy x position */
	public int generateEnemyX(){
		do{
			x = randomGenerator.nextInt(game.getDisplayWidth()+1);
		}while(x > game.getDisplayWidth()-this.getSprite().getWidth());

		return x;
	}
	
	/* Reduces the enemy hp */
	public void reduceHP(int atk){
		HP -= atk;
		if (HP <= 0)
			game.removeEntity(this);
		else
			game.registerHit(this);
	}
	
	/* Returns the name of the enemy */
	public String getName(){
		return name;
	}

	/* This enemy has collided with another entity */
	public void collidedWith(Entity other){
		//the enemy collision is handled elsewhere
	}
}