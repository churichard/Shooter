/*
 * Programmed by Richard Chu
 * Project started on July 24, 2012
 */
package shooter;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;

public class Game {
	/* Frame rendering */
	private int fps = 60; //frames per second
	private int displayWidth = 800; //width of display
	private int displayHeight = 600; //length of display

	/* Font */
	private UnicodeFont font;

	/* Delta */
	private int delta; //delta
	private int bulletDelta; //time since the last bullet
	private int enemyDelta; //time since the last enemy

	/* Classes */
	private Player player; //player class

	/* Background sprite */
	private Sprite background;

	/* ArrayLists */
	private ArrayList<Bullet> bullet = new ArrayList<Bullet>(); //Bullet ArrayList
	private ArrayList<Enemy> enemy = new ArrayList<Enemy>(); //Enemy ArrayList
	private ArrayList<Powerup> powerup = new ArrayList<Powerup>(); //Powerup ArrayList
	private ArrayList<Explosion> explosion = new ArrayList<Explosion>(); //Explosion ArrayList

	/* Bullet offsets */
	//First bullet offset
	private int BULLET_X_OFFSET = (int)(-57/1.3);
	private int BULLET_Y_OFFSET = (int)(-85/1.3);
	//Second bullet offset
	private int BULLET2_X_OFFSET = (int)(-7/1.3);
	private int BULLET2_Y_OFFSET = (int)(-85/1.3);

	/* Key presses */
	//is the w key down or not
	private boolean wKeyDown = false;
	//is the s key down or not
	private boolean sKeyDown = false;
	//is the a key down or not
	private boolean aKeyDown = false;
	//is the d key down or not
	private boolean dKeyDown = false;
	//is the left mouse button down or not
	private boolean mouseDown = false;

	/* Random checking */
	private Random randomGenerator = new Random(); //Random number generator
	//Number between 100 and 2000 that determines the number of milliseconds between each enemy
	private int enemyInterval;

	/* Powerups */
	private boolean doubleShot = false;

	/* Score */
	private int score = 0;

	/* Game over */
	//true if the player should stop being drawn
	private boolean stopDrawingPlayer = false;
	//true if the game is over
	private boolean gameOver = false;

	/* Start the game */
	public void start(){
		initGL();
		init();

		while(true){
			if (!gameOver){
				updateDelta();
				render();
				pollInput();
				update();
				updateDisplay();
			}
			else{
				System.out.println("Game Over!!");
				try{
					Thread.sleep(1000);
				}catch(Exception e){}
				Display.destroy();
				System.exit(0);
			}
		}
	}

	/* Updates delta values */
	private void updateDelta(){
		delta = Delta.getDelta("frame");
		bulletDelta = Delta.getDelta("bullet");
		enemyDelta = Delta.getDelta("enemy");
	}

	/* Initialize the GL display */
	private void initGL(){
		try {
			Display.setDisplayMode(new DisplayMode(displayWidth,displayHeight));
			Display.setVSyncEnabled(true);
			Display.setTitle("Shooter");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		//init OpenGL
		glEnable(GL_TEXTURE_2D);

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		//enable alpha blending
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glViewport(0,0,displayWidth,displayHeight);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
	}

	/* Initialize resources */
	@SuppressWarnings("unchecked")
	private void init(){
		player = new Player(this, "player", displayWidth/2-30, 502);

		//initialize the background sprite
		background = getSprite("background");
		background.setWidth(background.getTexture().getImageWidth());
		background.setHeight(background.getTexture().getImageHeight());

		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		//setup fonts
		java.awt.Font awtFont = new java.awt.Font("/res/ConsolaMono.ttf", java.awt.Font.BOLD, 18);
		font = new UnicodeFont(awtFont);
		font.getEffects().add(new ColorEffect(java.awt.Color.white));
		font.addAsciiGlyphs();
		try{
			font.loadGlyphs();
		} catch (SlickException e){
			e.printStackTrace();
		}
	}

	/* Polls for input */
	private void pollInput(){
		//check to see if the w key is down
		if (Keyboard.isKeyDown(Keyboard.KEY_W)){
			wKeyDown = true;
		}
		else
			wKeyDown = false;
		//check to see if the a key is down
		if (Keyboard.isKeyDown(Keyboard.KEY_A)){
			aKeyDown = true;
		}
		else
			aKeyDown = false;
		//check to see if the s key is down
		if (Keyboard.isKeyDown(Keyboard.KEY_S)){
			sKeyDown = true;
		}
		else
			sKeyDown = false;
		//check to see if the d key is down
		if (Keyboard.isKeyDown(Keyboard.KEY_D)){
			dKeyDown = true;
		}
		else
			dKeyDown = false;
		//check to see if the left mouse button is down and the time since the last bullet is at least 100 milliseconds
		if (Mouse.isButtonDown(0) && bulletDelta > 100){
			mouseDown = true;
		}
		else
			mouseDown = false;
	}

	/* Checks to see if the display is active or if it has been closed and updates it */
	private void updateDisplay(){
		if (!Display.isActive()){
			wKeyDown = false;
			aKeyDown = false;
			sKeyDown = false;
			dKeyDown = false;
			mouseDown = false;
		}

		Display.update();
		Display.sync(fps);

		if (Display.isCloseRequested()){
			Display.destroy();
			System.exit(0);
		}
	}

	/* Checks to see if a key is pressed */
	private void update(){
		//checks to see if the w key is down
		if (wKeyDown){
			player.setY((int)(player.getY()-0.5*delta));
		}
		//checks to see if the a key is down
		if (aKeyDown){
			player.setX((int)(player.getX()-0.5*delta));
		}
		//checks to see if the s key is down
		if (sKeyDown){
			player.setY((int)(player.getY()+0.5*delta));
		}
		//checks to see if the d key is down
		if (dKeyDown){
			player.setX((int)(player.getX()+0.5*delta));
		}
		
		//checks to make sure the player is within the bounds
		checkBounds();
		
		//checks to see if the left mouse button is clicked
		if (mouseDown){
			bullet.add(new Bullet(this, "bullet", 0, 0, 100));
			Bullet lastBullet = bullet.get(bullet.size()-1);
			Sprite playerSprite = player.getSprite();
			Sprite bulletSprite = lastBullet.getSprite();
			
			//sets the x and y coordinates of the bullet
			lastBullet.setX(player.getX()+playerSprite.getWidth()-bulletSprite.getWidth()/2+BULLET_X_OFFSET);
			lastBullet.setY(player.getY()+playerSprite.getHeight()-bulletSprite.getHeight()/2+BULLET_Y_OFFSET);
			
			//set the angle of the shot
			double xChange = Mouse.getX() - lastBullet.getX();
			double yChange = (Display.getHeight()-Mouse.getY()) - lastBullet.getY();
			double magnitude = Math.sqrt(xChange*xChange+yChange*yChange);
			xChange = xChange/magnitude * 20;
			yChange = yChange/magnitude * 20;
			lastBullet.setXChange((int) xChange);
			lastBullet.setYChange((int) yChange);
			
			//if the doubleshot powerup is activated
			if (doubleShot){
				bullet.add(new Bullet(this, "bullet", 0, 0, 100));
				lastBullet = bullet.get(bullet.size()-1);

				lastBullet.setX(player.getX()+playerSprite.getWidth()-bulletSprite.getWidth()/2+BULLET2_X_OFFSET);
				lastBullet.setY(player.getY()+playerSprite.getHeight()-bulletSprite.getHeight()/2+BULLET2_Y_OFFSET);
				
				//set the angle of the shot
				double doubleshotXChange = Mouse.getX() - lastBullet.getX();
				double doubleshotYChange = (Display.getHeight()-Mouse.getY()) - lastBullet.getY();
				double doubleshotMagnitude = Math.sqrt(xChange*xChange+yChange*yChange);
				doubleshotXChange = xChange/doubleshotMagnitude * 20;
				doubleshotYChange = yChange/doubleshotMagnitude * 20;
				lastBullet.setXChange((int) doubleshotXChange);
				lastBullet.setYChange((int) doubleshotYChange);
			}
			Delta.setLastBullet(Delta.getTime());
		}

		//checks to see if the time since the last enemy is greater than the enemy interval
		if (enemyDelta > generateEnemyInterval()){
			double enemyGenerate = randomGenerator.nextDouble();
			if (enemyGenerate >= 0.3)
				enemy.add(new Enemy(this, "green_box", "green_box", 0, 0, 300));
			else if (enemyGenerate < 0.3)
				enemy.add(new Enemy(this, "red_box", "red_box", 0, 0, 300));
			Enemy lastEnemy = enemy.get(enemy.size()-1);

			lastEnemy.setX(lastEnemy.generateEnemyX());
			lastEnemy.setY(-lastEnemy.getSprite().getHeight());
			Delta.setLastEnemy(Delta.getTime());
		}

		//checks for collisions
		for (int i = 0; i < enemy.size(); i++){
			Entity entity1 = enemy.get(i);
			for (int j = 0; j < bullet.size(); j++){
				Entity entity2 = bullet.get(j);

				if (entity1.collidesWith(entity2)){
					entity1.collidedWith(entity2);
					entity2.collidedWith(entity1);
				}
			}
			Entity entity2 = player;
			if (entity1.collidesWith(entity2)){
				entity1.collidedWith(entity2);
				entity2.collidedWith(entity1);
			}
		}
		Entity entity1 = player;
		for (int i = 0; i < powerup.size(); i++){
			Entity entity2 = powerup.get(i);

			if (entity1.collidesWith(entity2)){
				entity1.collidedWith(entity2);
				entity2.collidedWith(entity1);
			}
		}
	}

	/* Checks to see if the player and bullets are within the bounds */
	private void checkBounds() {
		//checks to see if playerX is to the left of the left side of the display
		if (player.getX() < 0)
			player.setX(0);
		//checks to see if playerX is greater than the width of the display
		if (player.getX() > Display.getWidth()-player.getSprite().getWidth())
			player.setX(Display.getWidth()-player.getSprite().getWidth());
		//checks to see if playerY is lower than the bottom of the display
		if (player.getY() < 0)
			player.setY(0);
		//checks to see if playerY is greater than the height of the display
		if (player.getY() > Display.getHeight()-player.getSprite().getHeight())
			player.setY(Display.getHeight()-player.getSprite().getHeight());
	}

	/* Renders the background, the score, and all of the sprites */
	private void render(){
		//clear the screen and depth buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//draw background
		Texture backTex = background.getTexture();
		Color.white.bind();
		backTex.bind();

		glBegin(GL_QUADS);
		glTexCoord2f(0,backTex.getHeight());
		glVertex2f(0, 0);
		glTexCoord2f(backTex.getWidth(),backTex.getHeight());
		glVertex2f(background.getWidth(), 0);
		glTexCoord2f(backTex.getWidth(),0);
		glVertex2f(background.getWidth(), background.getHeight());
		glTexCoord2f(0,0);
		glVertex2f(0, background.getHeight());
		glEnd();

		//draw score
		font.drawString(10, 10, "Score: " + score);

		//drawing player
		if (!stopDrawingPlayer)
			drawEntity(player);
		//drawing bullets
		drawListEntity(bullet);
		//drawing enemies
		drawListEntity(enemy);
		//drawing powerups
		drawListEntity(powerup);
		//drawing explosions
		drawListEntity(explosion);
	}

	/* Draws an entity */
	private void drawEntity(Entity ent){
		Sprite entSprite = ent.getSprite();

		Color.white.bind();
		entSprite.getTexture().bind();

		//draw entity
		glBegin(GL_QUADS);
		glTexCoord2f(0,0);
		glVertex2d(ent.getX(),ent.getY());
		glTexCoord2f(1,0);
		glVertex2d(ent.getX()+entSprite.getWidth(),ent.getY());
		glTexCoord2f(1,1);
		glVertex2d(ent.getX()+entSprite.getWidth(),ent.getY()+entSprite.getHeight());
		glTexCoord2f(0,1);
		glVertex2d(ent.getX(),ent.getY()+entSprite.getHeight());
		glEnd();
	}

	/* Draws all of the entities in a list */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void drawListEntity(ArrayList list) {
		ArrayList listRemove = new ArrayList();
		for (int i = 0; i < list.size(); i++){
			Entity ent = (Entity) list.get(i);

			drawEntity(ent);

			//if the entity is still in the screen, update its position
			if (ent.continueDrawing()){
				if (ent instanceof Bullet){
					ent.setX(ent.getX()+((Bullet) ent).getXChange());
					ent.setY(ent.getY()+((Bullet) ent).getYChange());
					//ent.setY(ent.getY()-delta);
				}
				else if (ent instanceof Enemy && ((Enemy)ent).getName().equals("green_box"))
					ent.setY(ent.getY()+delta/3);
				else if (ent instanceof Enemy && ((Enemy)ent).getName().equals("red_box"))
					ent.setY(ent.getY()+delta/2);
				else if (ent instanceof Powerup)
					ent.setY(ent.getY()+delta/4);
			}
			//else if the entity is outside of the screen, remove it
			else{
				listRemove.add(list.get(i));
			}

			//if the player is dead, notify the player of death
			if (stopDrawingPlayer){
				notifyDeath();
			}
		}
		for (int i = 0; i < listRemove.size(); i++){
			list.remove(listRemove.get(i));
		}
	}

	/* Returns the display width */
	public int getDisplayWidth(){
		return displayWidth;
	}

	/* Returns the display height */
	public int getDisplayHeight(){
		return displayHeight;
	}

	/* Randomly generates a number between 100 and 3000 to determine the number of milliseconds between each enemy */
	public int generateEnemyInterval(){
		enemyInterval = randomGenerator.nextInt(2901)+100;

		return enemyInterval;
	}

	/* Creates a sprite that displays an image */
	public Sprite getSprite(String name){
		return new Sprite(name);
	}

	/* Randomly checks to see if a powerup will drop from an enemy */
	public void powerupCheck(int x, int y){
		//checks to see if a powerup will drop
		double powerupCheck = randomGenerator.nextDouble();
		if (powerupCheck <= 0.15){
			double powerupGenerate = randomGenerator.nextDouble();
			boolean powerupCreated = false;

			while (!powerupCreated){
				if (powerupGenerate < 0.7 && !doubleShot){
					powerup.add(new Powerup(this, "powerup_doubleshot", "doubleshot", x, y));
					powerupCreated = true;
				}
				else if (powerupGenerate >= 0.7){
					powerup.add(new Powerup(this, "powerup_explosion", "explosion", x, y));
					powerupCreated = true;
				}
				else{
					powerupGenerate = randomGenerator.nextDouble();
				}
			}
		}
	}

	/* Registers a hit */
	public void registerHit(Entity entity){
		if (entity instanceof Enemy){
			explosion.add(new Explosion(this, "enemy_hit", "enemy_hit", entity.getX()-5, entity.getY()-5));
		}
	}

	/* Removes an entity */
	public void removeEntity(Entity entity){
		if (entity instanceof Bullet){
			bullet.remove(entity);
		}
		else if (entity instanceof Enemy){
			int enemyX = entity.getX();
			int enemyY = entity.getY();
			enemy.remove(entity);
			explosion.add(new Explosion(this, "enemy_explosion", "explosion", enemyX-5, enemyY-5));
			powerupCheck(enemyX, enemyY);
			if (!stopDrawingPlayer && ((Enemy) entity).getName().equals("green_box"))
				score += 10;
			else if (!stopDrawingPlayer && ((Enemy) entity).getName().equals("red_box"))
				score += 20;
		}
		else if (entity instanceof Powerup){
			powerup.remove(entity);
			if (((Powerup)entity).getName() == "doubleshot"){
				doubleShot = true;
			}
			else if (((Powerup)entity).getName() == "explosion"){
				for (int i = 0; i < enemy.size(); i++){
					explosion.add(new Explosion(this, "enemy_explosion", "explosion", enemy.get(i).getX()-5, enemy.get(i).getY()-5));
				}
				for (int i = 0; i < enemy.size(); i++){
					if (enemy.get(i).getName().equals("green_box"))
						score += 10;
					else if (enemy.get(i).getName().equals("red_box"))
						score += 20;
				}
				enemy.clear();
			}
		}
		else if (entity instanceof Player){
			explosion.add(new Explosion(this, "player_explosion", "explosion", player.getX()-5, player.getY()-5));
			stopDrawingPlayer = true;
		}
	}

	/* Notifies the player that they died */
	public void notifyDeath(){
		gameOver = true;
	}

	/* Main method */
	public static void main(String [] args){
		new Game().start();
	}
}