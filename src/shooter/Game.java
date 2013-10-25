/*
 * Programmed by Richard Chu
 */
package shooter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

public class Game {
	/* Frame rendering */
	private int fps = 60; //frames per second
	private int displayWidth = 800; //width of display
	private int displayHeight = 600; //length of display

	/* Font */
	private java.awt.Font awtFont;
	private UnicodeFont font;

	/* Delta */
	private int delta; //delta
	private int bulletDelta; //time since the last bullet
	private int enemyDelta; //time since the last enemy

	/* Classes */
	private Player player; //player class

	/* Screen sprites */
	private Sprite background;
	private Sprite titleScreen;
	private Sprite instructScreen;
	private Sprite creditsScreen;

	/* Sound */
	private Audio shootEffect; //shoot sfx
	private Audio enemyExplosionEffect; //enemy explosion sfx
	private Audio playerExplosionEffect; //player explosion sfx
	private Audio powerupGetEffect; //obtain powerup sfx
	private Audio playerHitEffect; //player hit sfx

	/* ArrayLists */
	private ArrayList<Bullet> bullet = new ArrayList<Bullet>(); //Bullet ArrayList
	private ArrayList<Bullet> enemy_bullet = new ArrayList<Bullet>(); //Enemy Bullet ArrayList
	private ArrayList<Enemy> enemy = new ArrayList<Enemy>(); //Enemy ArrayList
	private ArrayList<Powerup> powerup = new ArrayList<Powerup>(); //Powerup ArrayList
	private ArrayList<Explosion> explosion = new ArrayList<Explosion>(); //Explosion ArrayList

	/* Player initial position */
	private int initPlayerX = displayWidth/2-30;
	private int initPlayerY = 502;

	/* Bullet */
	//Shooting speed
	private int shootingSpeed = 150; //can shoot a bullet every 150 ms
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

	/* Cash */
	private int cash = 0;
	private int totalCash = 0;

	/* Game over */
	//true if the player should stop being drawn
	private boolean stopDrawingPlayer = false;
	//true if the game is over
	private boolean gameOver = false;

	/* Start the game */
	public void start(){
		//initialize stuff
		initGL();
		init();

		//main game loop
		while(true){
			//display the title screen
			showTitleScreen();

			while (!gameOver){
				updateDelta();
				render();
				pollInput();
				update();
				updateDisplay();
			}
			//temp code for game overs
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/* Displays the title screen */
	@SuppressWarnings("unchecked")
	private void showTitleScreen(){
		//reset game parameters
		//resets all keys to not being pressed
		wKeyDown = false;
		aKeyDown = false;
		sKeyDown = false;
		dKeyDown = false;
		mouseDown = false;
		//reset mouse
		Mouse.destroy();
		try {
			Mouse.create();
		} catch (LWJGLException e1) {
			e1.printStackTrace();
		}
		//resets the player's hp
		player.setHP(1000);
		//resets the player's position
		player.setX(initPlayerX);
		player.setY(initPlayerY);
		//the game is not over
		gameOver = false;
		stopDrawingPlayer = false;
		//clears the entity arraylists
		enemy.clear();
		bullet.clear();
		enemy_bullet.clear();
		powerup.clear();
		explosion.clear();
		//cash and totalCash are 0
		cash = 0;
		totalCash = 0;
		//doubleshot is false
		doubleShot = false;
		//setup fonts
		awtFont = new java.awt.Font("/res/ConsolaMono.ttf", java.awt.Font.BOLD, 20);
		font = new UnicodeFont(awtFont);
		font.getEffects().add(new ColorEffect(java.awt.Color.white));
		font.addAsciiGlyphs();
		try{
			font.loadGlyphs();
		} catch (SlickException e){
			e.printStackTrace();
		}

		//if the play button is not clicked
		while(!Mouse.isButtonDown(0) || !(Mouse.getX() >= 249 && Mouse.getX() <= 539
				&& Mouse.getY() <= (Display.getHeight()-215) && Mouse.getY() >= (Display.getHeight()-284))){
			//draw title screen
			drawScreen(titleScreen);

			if (Mouse.isButtonDown(0) && Mouse.getX() >= 249 && Mouse.getX() <= 539
					&& Mouse.getY() <= (Display.getHeight()-304) && Mouse.getY() >= (Display.getHeight()-373)){
				showInstructScreen();
			}

			if (Mouse.isButtonDown(0) && Mouse.getX() >= 249 && Mouse.getX() <= 539
					&& Mouse.getY() <= (Display.getHeight()-393) && Mouse.getY() >= (Display.getHeight()-462)){
				showCreditsScreen();
			}
			if (Mouse.isButtonDown(0) && Mouse.getX() >= 249 && Mouse.getX() <= 539
					&& Mouse.getY() <= (Display.getHeight()-482) && Mouse.getY() >= (Display.getHeight()-551)){
				Display.destroy();
				AL.destroy();
				System.exit(0);
			}

			//update display
			updateDisplay();
		}

		//reset beginning time
		Delta.setBeginningTime(Delta.getTime());
	}

	/* Displays the instructions screen */
	private void showInstructScreen(){
		while (!Mouse.isButtonDown(0) || !(Mouse.getX() >= 643 && Mouse.getX() <= 758
				&& Mouse.getY() <= (Display.getHeight()-494) && Mouse.getY() >= (Display.getHeight()-562))){
			//draw instructions screen
			drawScreen(instructScreen);

			//update display
			updateDisplay();
		}
	}

	/* Displays the credits screen */
	private void showCreditsScreen(){
		while (!Mouse.isButtonDown(0) || !(Mouse.getX() >= 643 && Mouse.getX() <= 758
				&& Mouse.getY() <= (Display.getHeight()-494) && Mouse.getY() >= (Display.getHeight()-562))){
			//draw credits screen
			drawScreen(creditsScreen);

			//update display
			updateDisplay();
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
		//creates the player
		player = new Player(this, "player", initPlayerX, initPlayerY, 1000);

		//initialize the background sprite
		background = getSprite("background");
		background.setWidth(background.getTexture().getImageWidth());
		background.setHeight(background.getTexture().getImageHeight());

		//initialize the title screen sprite
		titleScreen = getSprite("title_screen");
		titleScreen.setWidth(titleScreen.getTexture().getImageWidth());
		titleScreen.setHeight(titleScreen.getTexture().getImageHeight());

		//initialize the instructions screen sprite
		instructScreen = getSprite("instructions_screen");
		instructScreen.setWidth(instructScreen.getTexture().getImageWidth());
		instructScreen.setHeight(instructScreen.getTexture().getImageHeight());

		//initialize the credits screen sprite
		creditsScreen = getSprite("credits_screen");
		creditsScreen.setWidth(creditsScreen.getTexture().getImageWidth());
		creditsScreen.setHeight(creditsScreen.getTexture().getImageHeight());

		//initialize sound
		try {
			//shoot sfx
			shootEffect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/shoot.wav"));
			//enemy explosion sfx
			enemyExplosionEffect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/enemy_explosion.wav"));
			//player explosion sfx
			playerExplosionEffect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/player_explosion.wav"));
			//obtain powerup sfx
			powerupGetEffect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/powerup_get.wav"));
			//player hit sfx
			playerHitEffect = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/player_hit.wav"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		//setup fonts
		awtFont = new java.awt.Font("/res/ConsolaMono.ttf", java.awt.Font.BOLD, 60);
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
		if (Mouse.isButtonDown(0) && bulletDelta > shootingSpeed){
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
			AL.destroy();
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
			//play the shoot sound
			shootEffect.playAsSoundEffect(1.0f, 0.5f, false);

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
			if (enemyGenerate >= 0.3 || Delta.getDelta("beginning") <= 3000)
				enemy.add(new Enemy(this, "green_box", 0, 0, 300, Delta.getDelta("beginning")));
			else if (enemyGenerate < 0.3)
				enemy.add(new Enemy(this, "red_box", 0, 0, 400, Delta.getDelta("beginning")));
			Enemy lastEnemy = enemy.get(enemy.size()-1);

			lastEnemy.setX(lastEnemy.generateEnemyX());
			lastEnemy.setY(-lastEnemy.getSprite().getHeight());
			Delta.setLastEnemy(Delta.getTime());
		}

		//checks to see if enemy bullets should be generated
		for (int i = 0; i < enemy.size(); i++){
			if (enemy.get(i).getName().equals("red_box") && enemy.get(i).getTimeSinceLastBullet() >= 700){
				enemy_bullet.add(new Bullet(this, "enemy_bullet", 0, 0, 100));
				enemy.get(i).setLastBulletTime(Delta.getDelta("beginning"));

				Bullet lastEnemyBullet = enemy_bullet.get(enemy_bullet.size()-1);
				Sprite enemySprite = enemy.get(i).getSprite();
				Sprite enemyBulletSprite = lastEnemyBullet.getSprite();

				lastEnemyBullet.setX(enemy.get(i).getX()+enemySprite.getWidth()/2-enemyBulletSprite.getWidth()/2);
				lastEnemyBullet.setY(enemy.get(i).getY()+enemySprite.getHeight()/2-enemyBulletSprite.getHeight()/2);
			}
		}

		//checks for collisions
		for (int i = 0; i < enemy.size(); i++){
			Entity entity1 = enemy.get(i);

			//if a bullet collides with an enemy
			for (int j = 0; j < bullet.size(); j++){
				Entity entity2 = bullet.get(j);

				if (entity1.collidesWith(entity2)){
					entity1.collidedWith(entity2);
					entity2.collidedWith(entity1);
				}
			}

			Entity entity2 = player;

			//if the player collides with an enemy
			if (entity1.collidesWith(entity2)){
				entity1.collidedWith(entity2);
				entity2.collidedWith(entity1);
			}
		}
		Entity entity1 = player;
		//if an enemy bullet collides with the player
		for (int i = 0; i < enemy_bullet.size(); i++){
			Entity entity2 = enemy_bullet.get(i);

			if (entity1.collidesWith(entity2)){
				entity1.collidedWith(entity2);
				entity2.collidedWith(entity1);
			}
		}
		//if a powerup collides with the player
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

	/* Renders the background, text, and all of the sprites */
	@SuppressWarnings("unchecked")
	private void render(){
		//clear the screen and depth buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//draws the background
		drawScreen(background);

		//drawing player
		if (!stopDrawingPlayer)
			drawEntity(player);
		//drawing bullets
		drawListEntity(bullet);
		//drawing enemy bullets
		drawListEntity(enemy_bullet);
		//drawing enemies
		drawListEntity(enemy);
		//drawing powerups
		drawListEntity(powerup);
		//drawing explosions
		drawListEntity(explosion);
		
		//draw health
		font.drawString(10, 10, "Health: " + player.getHP());
		//draw cash
		font.drawString(10, 40, "Cash: $" + cash);
		
		if (stopDrawingPlayer){
			//setup fonts
			awtFont = new java.awt.Font("/res/Judson.ttf", java.awt.Font.BOLD, 80);
			font = new UnicodeFont(awtFont);
			font.getEffects().add(new ColorEffect(java.awt.Color.white));
			font.addAsciiGlyphs();
			try{
				font.loadGlyphs();
			} catch (SlickException e){
				e.printStackTrace();
			}
			//draw Game Over
			font.drawString(displayWidth/2-font.getWidth("Game Over!")/2,
					displayHeight/2-font.getHeight("Game Over!")/2, "Game Over!");
		}
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
					if (((Bullet)ent).getName().equals("bullet")){
						ent.setX(ent.getX()+((Bullet) ent).getXChange());
						ent.setY(ent.getY()+((Bullet) ent).getYChange());
					}
					if (((Bullet)ent).getName().equals("enemy_bullet")){
						ent.setY(ent.getY()+delta/2);
					}
				}
				else if (ent instanceof Enemy){
					if (((Enemy)ent).getName().equals("green_box")){
						ent.setY(ent.getY()+delta/3);
					}
					if (((Enemy)ent).getName().equals("red_box")){
						ent.setY(ent.getY()+delta/4);
					}
				}
				else if (ent instanceof Explosion){
					if (((Explosion) ent).getName().equals("player_hit")){
						ent.setX(((Explosion)ent).getEntity().getX());
						ent.setY(((Explosion)ent).getEntity().getY());
					}
					else if (((Explosion) ent).getName().equals("enemy_hit")){
						ent.setX(((Explosion)ent).getEntity().getX()-5);
						ent.setY(((Explosion)ent).getEntity().getY()-5);
					}
				}
				else if (ent instanceof Powerup){
					ent.setY(ent.getY()+delta/4);
				}
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

	/* Draws a screen */
	private void drawScreen(Sprite spr){
		Texture tex = spr.getTexture();
		Color.white.bind();
		tex.bind();

		glBegin(GL_QUADS);
		glTexCoord2f(0,tex.getHeight());
		glVertex2f(0, spr.getHeight());
		glTexCoord2f(tex.getWidth(),tex.getHeight());
		glVertex2f(spr.getWidth(), spr.getHeight());
		glTexCoord2f(tex.getWidth(),0);
		glVertex2f(spr.getWidth(), 0);
		glTexCoord2f(0,0);
		glVertex2f(0, 0);
		glEnd();
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
		enemyInterval = randomGenerator.nextInt(2901)+300;

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
		if (powerupCheck <= 0.1 && !doubleShot)
			powerup.add(new Powerup(this, "powerup_doubleshot", "doubleshot", x, y));
		else if (powerupCheck <= 0.1)
			powerup.add(new Powerup(this, "powerup_explosion", "explosion", x, y));
	}

	/* Registers a hit */
	public void registerHit(Entity entity){
		if (entity instanceof Enemy){
			explosion.add(new Explosion(this, "enemy_hit", "enemy_hit", entity.getX()-5, entity.getY()-5, entity));
		}
		else if (entity instanceof Player){
			playerHitEffect.playAsSoundEffect(1.0f, 2.0f, false);
			explosion.add(new Explosion(this, "player_hit", "player_hit", entity.getX(), entity.getY(), entity));
		}
	}

	/* Removes an entity */
	public void removeEntity(Entity entity){
		if (entity instanceof Bullet){
			if (((Bullet) entity).getName().equals("bullet")){
				bullet.remove(entity);
			}
			else if (((Bullet) entity).getName().equals("enemy_bullet")){
				enemy_bullet.remove(entity);
			}
		}
		else if (entity instanceof Enemy){
			int enemyX = entity.getX();
			int enemyY = entity.getY();
			enemy.remove(entity);

			//play enemy explosion sfx
			enemyExplosionEffect.playAsSoundEffect(1.0f, 1.0f, false);

			explosion.add(new Explosion(this, "enemy_explosion", "explosion", enemyX-5, enemyY-5, entity));
			powerupCheck(enemyX, enemyY);
			if (!stopDrawingPlayer && ((Enemy) entity).getName().equals("green_box"))
				cash += 10;
			else if (!stopDrawingPlayer && ((Enemy) entity).getName().equals("red_box"))
				cash += 30;
		}
		else if (entity instanceof Powerup){
			//play obtain powerup sfx
			powerupGetEffect.playAsSoundEffect(1.0f, 2.0f, false);

			powerup.remove(entity);
			if (((Powerup)entity).getName() == "doubleshot"){
				doubleShot = true;
			}
			else if (((Powerup)entity).getName() == "explosion"){
				for (int i = 0; i < enemy.size(); i++){
					explosion.add(new Explosion(this, "enemy_explosion", "explosion", enemy.get(i).getX()-5, enemy.get(i).getY()-5, entity));
				}
				for (int i = 0; i < enemy.size(); i++){
					if (enemy.get(i).getName().equals("green_box"))
						cash += 10;
					else if (enemy.get(i).getName().equals("red_box"))
						cash += 30;
				}
				//play enemy explosion sfx
				enemyExplosionEffect.playAsSoundEffect(1.0f, 1.0f, false);
				enemy.clear();
			}
		}
		else if (entity instanceof Player){
			//play player explosion sfx
			playerExplosionEffect.playAsSoundEffect(1.0f, 1.0f, false);

			explosion.add(new Explosion(this, "player_explosion", "explosion", player.getX()-5, player.getY()-5, entity));
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