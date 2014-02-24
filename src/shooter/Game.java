/*
 * Programmed by Richard Chu
 * Thanks for looking at my code! :)
 */
package shooter;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

public class Game {
	/* Frame rendering */
	private int fps = 60; // frames per second
	private int displayWidth = 800; // width of display
	private int displayHeight = 600; // length of display
	
	/* Font */
	private SimpleFont defaultFont;
	private SimpleFont scoreFont;
	private SimpleFont congratsFont;
	private SimpleFont gameOverFont;
	
	/* Delta */
	private int bulletDelta; // time since the last bullet
	private int enemyDelta; // time since the last enemy
	
	/* Classes */
	private Player player; // player class
	
	/* Table of sprites */
	private HashMap<String, Sprite> sprite = new HashMap<String, Sprite>();
	
	/* Table of sounds */
	private HashMap<String, Audio> sound = new HashMap<String, Audio>();
	
	/* ArrayLists */
	private ArrayList<Bullet> bullet = new ArrayList<Bullet>(); // Bullet ArrayList
	private ArrayList<Bullet> enemy_bullet = new ArrayList<Bullet>(); // Enemy Bullet ArrayList
	private ArrayList<Enemy> enemy = new ArrayList<Enemy>(); // Enemy ArrayList
	private ArrayList<Powerup> powerup = new ArrayList<Powerup>(); // Powerup ArrayList
	private ArrayList<Explosion> explosion = new ArrayList<Explosion>(); // Explosion ArrayList
	
	/* Boss */
	private boolean bossSpawned;
	private int bossMovement;
	private int bossExplosionNum;
	
	/* Player initial position */
	private int initPlayerX = displayWidth / 2 - 30;
	private int initPlayerY = 502;
	
	/* Bullet */
	// Bullet
	private int bulletShootingSpeed = 150; // can shoot a bullet every 150 ms
	// Doubleshot
	private int doubleShotShootingSpeed = 270; // can shoot a doubleshot every 270 ms
	// First bullet offset
	private int BULLET_X_OFFSET = (int) (-57 / 1.3);
	private int BULLET_Y_OFFSET = (int) (-85 / 1.3);
	// Second bullet offset
	private int BULLET2_X_OFFSET = (int) (-7 / 1.3);
	private int BULLET2_Y_OFFSET = (int) (-85 / 1.3);
	
	/* Key presses */
	// is the w key down or not
	private boolean wKeyDown;
	// is the s key down or not
	private boolean sKeyDown;
	// is the a key down or not
	private boolean aKeyDown;
	// is the d key down or not
	private boolean dKeyDown;
	// is the left mouse button down or not
	private boolean mouseDown;
	
	/* Random checking */
	private Random randomGenerator = new Random(); // Random number generator
	// Number between 100 and 2000 that determines the number of milliseconds between each enemy
	private int enemyInterval;
	
	/* Weapons */
	private boolean bulletShot;
	private boolean doubleShot;
	private boolean laserShot;
	
	/* Cash */
	private int cash;
	private int totalCash;
	
	/* Game over */
	// true if the player should stop being drawn
	private boolean stopDrawingPlayer;
	// true if the game has been won
	private boolean gameWon;
	// true if the game is over
	private boolean gameOver;
	
	/* Start the game */
	public void start() {
		// initialize stuff
		initGL();
		init();
		
		// main game loop
		while (true) {
			// display the title screen
			showTitleScreen();
			
			while (!gameOver) {
				updateDelta();
				render();
				pollInput();
				update();
				updateDisplay();
			}
			// Game Over
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/* Displays the title screen */
	private void showTitleScreen() {
		resetParameters();
		
		// if the play button is not clicked
		while (!Mouse.isButtonDown(0)
				|| !(Mouse.getX() >= 249 && Mouse.getX() <= 539 && Mouse.getY() <= (displayHeight - 215) && Mouse.getY() >= (displayHeight - 284))) {
			// draw title screen
			drawScreen(sprite.get("titleScreen"));
			
			if (Mouse.isButtonDown(0) && Mouse.getX() >= 249 && Mouse.getX() <= 539
					&& Mouse.getY() <= (displayHeight - 304) && Mouse.getY() >= (displayHeight - 373)) {
				showInstructScreen();
			}
			
			if (Mouse.isButtonDown(0) && Mouse.getX() >= 249 && Mouse.getX() <= 539
					&& Mouse.getY() <= (displayHeight - 393) && Mouse.getY() >= (displayHeight - 462)) {
				showCreditsScreen();
			}
			if (Mouse.isButtonDown(0) && Mouse.getX() >= 249 && Mouse.getX() <= 539
					&& Mouse.getY() <= (displayHeight - 482) && Mouse.getY() >= (displayHeight - 551)) {
				Display.destroy();
				AL.destroy();
				System.exit(0);
			}
			
			// update display
			updateDisplay();
		}
		
		// reset beginning time
		Delta.setBeginningTime(Delta.getTime());
	}
	
	private void resetParameters() {
		// Reset game parameters
		// resets all keys to not being pressed
		wKeyDown = false;
		aKeyDown = false;
		sKeyDown = false;
		dKeyDown = false;
		mouseDown = false;
		// reset mouse
		Mouse.destroy();
		try {
			Mouse.create();
		} catch (LWJGLException e1) {
			e1.printStackTrace();
		}
		// resets the player's hp
		player.setHP(1000);
		// resets the player's position
		player.setX(initPlayerX);
		player.setY(initPlayerY);
		// the game is not over
		gameOver = false;
		stopDrawingPlayer = false;
		gameWon = false;
		// clears the entity arraylists
		enemy.clear();
		bullet.clear();
		enemy_bullet.clear();
		powerup.clear();
		explosion.clear();
		// cash and totalCash are 0
		cash = 0;
		totalCash = 0;
		// bullet is activated, upgraded weapons are not activated
		bulletShot = true;
		doubleShot = false;
		laserShot = false;
		// boss
		bossSpawned = false;
		bossMovement = 3;
		bossExplosionNum = 0;
	}
	
	/* Displays the instructions screen */
	private void showInstructScreen() {
		while (!Mouse.isButtonDown(0)
				|| !(Mouse.getX() >= 643 && Mouse.getX() <= 758 && Mouse.getY() <= (displayHeight - 494) && Mouse.getY() >= (displayHeight - 562))) {
			// draw instructions screen
			drawScreen(sprite.get("instructScreen"));
			
			// update display
			updateDisplay();
		}
	}
	
	/* Displays the credits screen */
	private void showCreditsScreen() {
		while (!Mouse.isButtonDown(0)
				|| !(Mouse.getX() >= 643 && Mouse.getX() <= 758 && Mouse.getY() <= (displayHeight - 494) && Mouse.getY() >= (displayHeight - 562))) {
			// draw credits screen
			drawScreen(sprite.get("creditsScreen"));
			
			// update display
			updateDisplay();
		}
	}
	
	/* Displays the shop screen */
	private void showShopScreen() {
		while (!Mouse.isButtonDown(0)
				|| !(Mouse.getX() >= 643 && Mouse.getX() <= 758 && Mouse.getY() <= (displayHeight - 494) && Mouse.getY() >= (displayHeight - 562))) {
			// draw shop screen
			drawScreen(sprite.get("shopScreen"));
			
			while (Mouse.next()) {
				if (!laserShot && cash >= 500 && !Mouse.getEventButtonState() && Mouse.getEventButton() == 0
						&& Mouse.getX() >= 278 && Mouse.getX() <= 382
						&& Mouse.getY() <= (displayHeight - 207) && Mouse.getY() >= (displayHeight - 259)) {
					bulletShot = false;
					doubleShot = false;
					laserShot = true;
					cash -= 500;
					sound.get("shopBuyEffect").playAsSoundEffect(1.0f, 1.0f, false);
				}
				
				if (!doubleShot && cash >= 1000 && !Mouse.getEventButtonState()
						&& Mouse.getEventButton() == 0 && Mouse.getX() >= 278 && Mouse.getX() <= 382
						&& Mouse.getY() <= (displayHeight - 319) && Mouse.getY() >= (displayHeight - 371)) {
					bulletShot = false;
					doubleShot = true;
					laserShot = false;
					cash -= 1000;
					sound.get("shopBuyEffect").playAsSoundEffect(1.0f, 1.0f, false);
				}
				
				if (player.getHP() < 1000 && cash >= 100 && !Mouse.getEventButtonState()
						&& Mouse.getEventButton() == 0 && Mouse.getX() >= 680 && Mouse.getX() <= 784
						&& Mouse.getY() <= (displayHeight - 207) && Mouse.getY() >= (displayHeight - 259)) {
					player.setHP(player.getHP() + 100);
					if (player.getHP() > 1000)
						player.setHP(1000);
					cash -= 100;
					sound.get("shopBuyEffect").playAsSoundEffect(1.0f, 1.0f, false);
				}
			}
			
			// draw health
			defaultFont.drawString(13, displayHeight - 70, "Health: " + player.getHP() + "/1000");
			// draw cash
			defaultFont.drawString(13, displayHeight - 40, "Cash: $" + cash);
			
			// update display
			updateDisplay();
		}
	}
	
	/* Updates delta values */
	private void updateDelta() {
		bulletDelta = Delta.getDelta("bullet");
		enemyDelta = Delta.getDelta("enemy");
	}
	
	/* Initialize the GL display */
	private void initGL() {
		try {
			Display.setDisplayMode(new DisplayMode(displayWidth, displayHeight));
			Display.setVSyncEnabled(true);
			Display.setTitle("Shooter");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// init OpenGL
		glEnable(GL_TEXTURE_2D);
		
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// enable alpha blending
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glViewport(0, 0, displayWidth, displayHeight);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, displayWidth, displayHeight, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
	}
	
	/* Initialize resources */
	private void init() {
		// Creates the player
		player = new Player(this, "player", initPlayerX, initPlayerY, 1000);
		
		// Initializes screens
		sprite.put("background", new Sprite("screens/background"));
		sprite.put("titleScreen", new Sprite("screens/title_screen"));
		sprite.put("instructScreen", new Sprite("screens/instructions_screen"));
		sprite.put("creditsScreen", new Sprite("screens/credits_screen"));
		sprite.put("shopScreen", new Sprite("screens/shop_screen"));
		
		// Initialize actors
		sprite.put("playerSprite", new Sprite("actors/player"));
		sprite.put("greenBoxSprite", new Sprite("actors/green_box"));
		sprite.put("redBoxSprite", new Sprite("actors/red_box"));
		sprite.put("bossSprite", new Sprite("actors/boss"));
		
		// Initialize effects
		sprite.put("bulletSprite", new Sprite("effects/bullet"));
		sprite.put("laserSprite", new Sprite("effects/laser"));
		sprite.put("playerHitSprite", new Sprite("effects/player_hit"));
		sprite.put("playerExplosionSprite", new Sprite("effects/player_explosion"));
		sprite.put("enemyBulletSprite", new Sprite("effects/enemy_bullet"));
		sprite.put("enemyHitSprite", new Sprite("effects/enemy_hit"));
		sprite.put("enemyExplosionSprite", new Sprite("effects/enemy_explosion"));
		sprite.put("bossBulletSprite", new Sprite("effects/boss_bullet"));
		sprite.put("bossHitSprite", new Sprite("effects/boss_hit"));
		sprite.put("bossExplosionSprite", new Sprite("effects/boss_explosion"));
		
		// Initialize pickups
		sprite.put("powerupExplosionSprite", new Sprite("pickups/powerup_explosion"));
		
		// Initialize sounds
		try {
			sound.put("shootEffect", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/sound/shoot.wav")));
			sound.put("enemyExplosionEffect", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/sound/enemy_explosion.wav")));
			sound.put("playerExplosionEffect", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/sound/player_explosion.wav")));
			sound.put("powerupGetEffect", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/sound/powerup_get.wav")));
			sound.put("playerHitEffect", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/sound/player_hit.wav")));
			sound.put("shopBuyEffect", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/sound/shop_buy.wav")));
			sound.put("laserEffect", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/sound/laser.wav")));
			sound.put("bossExplosionEffect", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/sound/boss_explosion.wav")));
			sound.put("bossExplosionEffect2", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/sound/boss_explosion2.wav")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		try {
			setupFonts();
		} catch (SlickException se) {
			se.printStackTrace();
		}
	}
	
	private void setupFonts() throws SlickException {
		// setup fonts
		defaultFont = new SimpleFont("res/fonts/ConsolaMono.ttf", "bold", 20, Color.white);
		congratsFont = new SimpleFont("res/fonts/ConsolaMono.ttf", "bold", 25, Color.white);
		scoreFont = new SimpleFont("res/fonts/ConsolaMono.ttf", "bold", 40, Color.white);
		gameOverFont = new SimpleFont("res/fonts/ConsolaMono.ttf", "bold", 80, Color.white);
	}
	
	/* Polls for input */
	private void pollInput() {
		// check to see if the w key is down
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			wKeyDown = true;
		} else
			wKeyDown = false;
		// check to see if the a key is down
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			aKeyDown = true;
		} else
			aKeyDown = false;
		// check to see if the s key is down
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			sKeyDown = true;
		} else
			sKeyDown = false;
		// check to see if the d key is down
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			dKeyDown = true;
		} else
			dKeyDown = false;
		// check to see if the left mouse button is down
		if (Mouse.isButtonDown(0)) {
			mouseDown = true;
		} else
			mouseDown = false;
		// check to see if the F button is pressed and shows the shop screen if so
		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
			showShopScreen();
		}
		// check to see if the esc button is pressed; if so, go to title screen
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			showTitleScreen();
		}
	}
	
	/*
	 * Checks to see if the display is active or if it has been closed and updates it
	 */
	private void updateDisplay() {
		if (!Display.isActive()) {
			wKeyDown = false;
			aKeyDown = false;
			sKeyDown = false;
			dKeyDown = false;
			mouseDown = false;
		}
		
		Display.update();
		Display.sync(fps);
		
		if (Display.isCloseRequested()) {
			Display.destroy();
			AL.destroy();
			System.exit(0);
		}
	}
	
	/* Checks to see if a key is pressed */
	private void update() {
		// checks to see if the w key is down
		if (wKeyDown) {
			player.setY((int) (player.getY() - 8));
		}
		// checks to see if the a key is down
		if (aKeyDown) {
			player.setX((int) (player.getX() - 8));
		}
		// checks to see if the s key is down
		if (sKeyDown) {
			player.setY((int) (player.getY() + 8));
		}
		// checks to see if the d key is down
		if (dKeyDown) {
			player.setX((int) (player.getX() + 8));
		}
		
		// checks to make sure the player is within the bounds
		checkBounds();
		
		// checks to see if the left mouse button is clicked
		if (mouseDown) {
			// if doubleshot is activated
			if (doubleShot && bulletDelta > doubleShotShootingSpeed) {
				// plays the bullet shooting sound
				sound.get("shootEffect").playAsSoundEffect(1.0f, 0.5f, false);
				
				// adds the first bullet
				bullet.add(new Bullet(this, "bullet", 0, 0, 100));
				Bullet lastBullet = bullet.get(bullet.size() - 1);
				
				// sets the x and y coordinates of the bullet
				lastBullet.setX(player.getX() + sprite.get("playerSprite").getWidth()
						- sprite.get("bulletSprite").getWidth() / 2 + BULLET_X_OFFSET);
				lastBullet.setY(player.getY() + sprite.get("playerSprite").getHeight()
						- sprite.get("bulletSprite").getHeight() / 2 + BULLET_Y_OFFSET);
				
				// set the angle of the shot
				double xChange = Mouse.getX() - lastBullet.getX();
				double yChange = (displayHeight - Mouse.getY()) - lastBullet.getY();
				double magnitude = Math.sqrt(xChange * xChange + yChange * yChange);
				xChange = xChange / magnitude * 20;
				yChange = yChange / magnitude * 20;
				lastBullet.setXChange((int) xChange);
				lastBullet.setYChange((int) yChange);
				
				// adds the second bullet
				bullet.add(new Bullet(this, "bullet", 0, 0, 100));
				lastBullet = bullet.get(bullet.size() - 1);
				
				lastBullet.setX(player.getX() + sprite.get("playerSprite").getWidth()
						- sprite.get("bulletSprite").getWidth() / 2 + BULLET2_X_OFFSET);
				lastBullet.setY(player.getY() + sprite.get("playerSprite").getHeight()
						- sprite.get("bulletSprite").getHeight() / 2 + BULLET2_Y_OFFSET);
				
				// set the angle of the shot
				double doubleshotXChange = Mouse.getX() - lastBullet.getX();
				double doubleshotYChange = (displayHeight - Mouse.getY()) - lastBullet.getY();
				double doubleshotMagnitude = Math.sqrt(xChange * xChange + yChange * yChange);
				doubleshotXChange = xChange / doubleshotMagnitude * 20;
				doubleshotYChange = yChange / doubleshotMagnitude * 20;
				lastBullet.setXChange((int) doubleshotXChange);
				lastBullet.setYChange((int) doubleshotYChange);
				
				Delta.setLastBullet(Delta.getTime());
			}
			// if a bullet is shot and the time since the last bullet is at least shootingSpeed
			else if (bulletShot && bulletDelta > bulletShootingSpeed) {
				// plays the bullet shooting sound
				sound.get("shootEffect").playAsSoundEffect(1.0f, 0.5f, false);
				
				// adds the bullet
				bullet.add(new Bullet(this, "bullet", 0, 0, 100));
				Bullet lastBullet = bullet.get(bullet.size() - 1);
				
				// sets the x and y coordinates of the bullet
				lastBullet.setX(player.getX() + sprite.get("playerSprite").getWidth()
						- sprite.get("bulletSprite").getWidth() / 2 + BULLET_X_OFFSET);
				lastBullet.setY(player.getY() + sprite.get("playerSprite").getHeight()
						- sprite.get("bulletSprite").getHeight() / 2 + BULLET_Y_OFFSET);
				
				// set the angle of the shot
				double xChange = Mouse.getX() - lastBullet.getX();
				double yChange = (displayHeight - Mouse.getY()) - lastBullet.getY();
				double magnitude = Math.sqrt(xChange * xChange + yChange * yChange);
				xChange = xChange / magnitude * 20;
				yChange = yChange / magnitude * 20;
				lastBullet.setXChange((int) xChange);
				lastBullet.setYChange((int) yChange);
				
				Delta.setLastBullet(Delta.getTime());
			}
			// if a laser is shot
			else if (laserShot) {
				if (Delta.getTime() % 6 == 0) {
					// plays the laser shooting sound
					sound.get("laserEffect").playAsSoundEffect(1.0f, 0.5f, false);
				}
				
				// adds the laser
				bullet.add(new Bullet(this, "laser", 0, 0, 20));
				Bullet lastBullet = bullet.get(bullet.size() - 1);
				
				// sets the x and y coordinates of the laser
				lastBullet.setX(player.getX() + sprite.get("playerSprite").getWidth()
						- sprite.get("laserSprite").getWidth() / 2 + BULLET_X_OFFSET);
				lastBullet.setY(player.getY() + sprite.get("playerSprite").getHeight()
						- sprite.get("laserSprite").getHeight() + BULLET_Y_OFFSET);
			}
		}
		
		// Checks to see if the time since the last enemy is greater than the enemy interval
		if (!bossSpawned && enemyDelta > generateEnemyInterval()) {
			double enemyGenerate = randomGenerator.nextDouble();
			if (enemyGenerate >= 0.3 || Delta.getDelta("beginning") <= 10000)
				enemy.add(new Enemy(this, "green_box", 0, 0, 300, Delta.getDelta("beginning"), 300));
			else if (enemyGenerate < 0.3)
				enemy.add(new Enemy(this, "red_box", 0, 0, 400, Delta.getDelta("beginning"), 400));
			Enemy lastEnemy = enemy.get(enemy.size() - 1);
			
			lastEnemy.setX(lastEnemy.generateEnemyX());
			lastEnemy.setY(-lastEnemy.getSprite().getHeight());
			Delta.setLastEnemy(Delta.getTime());
		}
		
		if (bossExplosionNum == 0) {
			// Checks to see if enemy bullets should be generated
			for (int i = 0; i < enemy.size(); i++) {
				if (enemy.get(i).getName().equals("red_box") && enemy.get(i).getTimeSinceLastBullet() >= 700) {
					enemy_bullet.add(new Bullet(this, "enemy_bullet", 0, 0, 100));
					enemy.get(i).setLastBulletTime(Delta.getDelta("beginning"));
					
					Bullet lastEnemyBullet = enemy_bullet.get(enemy_bullet.size() - 1);
					Sprite enemySprite = enemy.get(i).getSprite();
					Sprite enemyBulletSprite = lastEnemyBullet.getSprite();
					
					lastEnemyBullet.setX(enemy.get(i).getX() + enemySprite.getWidth() / 2
							- enemyBulletSprite.getWidth() / 2);
					lastEnemyBullet.setY(enemy.get(i).getY() + enemySprite.getHeight() / 2
							- enemyBulletSprite.getHeight() / 2);
				}
				
				if (enemy.get(i).getName().equals("boss") && enemy.get(i).getTimeSinceLastBullet() >= 1000) {
					enemy_bullet.add(new Bullet(this, "boss_bullet", 0, 0, 100));
					enemy.get(i).setLastBulletTime(Delta.getDelta("beginning"));
					
					Bullet lastEnemyBullet = enemy_bullet.get(enemy_bullet.size() - 1);
					Sprite enemySprite = enemy.get(i).getSprite();
					Sprite enemyBulletSprite = lastEnemyBullet.getSprite();
					
					lastEnemyBullet.setX(enemy.get(i).getX() + enemySprite.getWidth() / 2
							- enemyBulletSprite.getWidth() / 2);
					lastEnemyBullet.setY(enemy.get(i).getY() + enemySprite.getHeight() / 2
							- enemyBulletSprite.getHeight() / 2);
					
					// set the angle of the shot
					double xChange = player.getX() - lastEnemyBullet.getX();
					double yChange = player.getY() - lastEnemyBullet.getY();
					double magnitude = Math.sqrt(xChange * xChange + yChange * yChange);
					xChange = xChange / magnitude * 15;
					yChange = yChange / magnitude * 15;
					lastEnemyBullet.setXChange((int) xChange);
					lastEnemyBullet.setYChange((int) yChange);
					
					enemy_bullet.add(new Bullet(this, "boss_bullet", 0, 0, 100));
					enemy.get(i).setLastBulletTime(Delta.getDelta("beginning"));
					
					lastEnemyBullet = enemy_bullet.get(enemy_bullet.size() - 1);
					enemySprite = enemy.get(i).getSprite();
					enemyBulletSprite = lastEnemyBullet.getSprite();
					
					lastEnemyBullet.setX(enemy.get(i).getX() + enemySprite.getWidth() / 2
							- enemyBulletSprite.getWidth() / 2);
					lastEnemyBullet.setY(enemy.get(i).getY() + enemySprite.getHeight() / 2
							- enemyBulletSprite.getHeight() / 2);
					
					// set the angle of the shot
					xChange = player.getX() - lastEnemyBullet.getX() - 100;
					yChange = player.getY() - lastEnemyBullet.getY() - 100;
					magnitude = Math.sqrt(xChange * xChange + yChange * yChange);
					xChange = xChange / magnitude * 15;
					yChange = yChange / magnitude * 15;
					lastEnemyBullet.setXChange((int) xChange);
					lastEnemyBullet.setYChange((int) yChange);
					
					enemy_bullet.add(new Bullet(this, "boss_bullet", 0, 0, 100));
					enemy.get(i).setLastBulletTime(Delta.getDelta("beginning"));
					
					lastEnemyBullet = enemy_bullet.get(enemy_bullet.size() - 1);
					enemySprite = enemy.get(i).getSprite();
					enemyBulletSprite = lastEnemyBullet.getSprite();
					
					lastEnemyBullet.setX(enemy.get(i).getX() + enemySprite.getWidth() / 2
							- enemyBulletSprite.getWidth() / 2);
					lastEnemyBullet.setY(enemy.get(i).getY() + enemySprite.getHeight() / 2
							- enemyBulletSprite.getHeight() / 2);
					
					// set the angle of the shot
					xChange = player.getX() - lastEnemyBullet.getX() + 100;
					yChange = player.getY() - lastEnemyBullet.getY() + 100;
					magnitude = Math.sqrt(xChange * xChange + yChange * yChange);
					xChange = xChange / magnitude * 15;
					yChange = yChange / magnitude * 15;
					lastEnemyBullet.setXChange((int) xChange);
					lastEnemyBullet.setYChange((int) yChange);
				}
			}
		}
		
		// spawns the boss if 60 seconds has passed
		if (Delta.getDelta("beginning") >= 60000 && !bossSpawned) {
			bossSpawned = true;
			enemy.add(new Enemy(this, "boss", 0, 0, 10000, Delta.getDelta("beginning"), 500));
			
			Enemy boss = enemy.get(enemy.size() - 1);
			boss.setX(displayWidth / 2 - boss.getSprite().getWidth() / 2);
			boss.setY(-boss.getSprite().getHeight());
		}
		
		// checks for collisions
		if (bossExplosionNum == 0) {
			for (int i = 0; i < enemy.size(); i++) {
				Entity entity1 = enemy.get(i);
				
				// if a bullet collides with an enemy
				for (int j = 0; j < bullet.size(); j++) {
					Entity entity2 = bullet.get(j);
					
					if (entity1.collidesWith(entity2)) {
						entity1.collidedWith(entity2);
						entity2.collidedWith(entity1);
					}
				}
				
				Entity entity2 = player;
				
				// if the player collides with an enemy
				if (entity1.collidesWith(entity2)) {
					entity1.collidedWith(entity2);
					entity2.collidedWith(entity1);
				}
			}
			Entity entity1 = player;
			// if an enemy bullet collides with the player
			for (int i = 0; i < enemy_bullet.size(); i++) {
				Entity entity2 = enemy_bullet.get(i);
				
				if (entity1.collidesWith(entity2)) {
					entity1.collidedWith(entity2);
					entity2.collidedWith(entity1);
				}
			}
			// if a powerup collides with the player
			for (int i = 0; i < powerup.size(); i++) {
				Entity entity2 = powerup.get(i);
				
				if (entity1.collidesWith(entity2)) {
					entity1.collidedWith(entity2);
					entity2.collidedWith(entity1);
				}
			}
		}
		
		// if the player has defeated the boss
		if (gameWon && bossSpawned) {
			// draw Congratulations
			String congrats = "Congratulations! You were able to vanquish";
			String congrats2 = "all of your evil thoughts and achieve nirvana!";
			congratsFont.drawString(displayWidth / 2 - congratsFont.getWidth(congrats) / 2, displayHeight / 2
					- congratsFont.getHeight(congrats) / 2 - 50, congrats);
			congratsFont.drawString(displayWidth / 2 - congratsFont.getWidth(congrats2) / 2, displayHeight
					/ 2 - congratsFont.getHeight(congrats2) / 2 - 15, congrats2);
			
			// draw the score
			scoreFont.drawString(displayWidth / 2 - scoreFont.getWidth("Score: " + totalCash) / 2, displayHeight
					/ 2
					+ scoreFont.getHeight("Game Over!")
					/ 2
					+ scoreFont.getHeight("Score: " + totalCash)
					/ 2 + 10 - 50, "Score: " + totalCash);
		}
	}
	
	/* Checks to see if the player and bullets are within the bounds */
	private void checkBounds() {
		// checks to see if playerX is to the left of the left side of the display
		if (player.getX() < 0)
			player.setX(0);
		// checks to see if playerX is greater than the width of the display
		if (player.getX() > displayWidth - sprite.get("playerSprite").getWidth())
			player.setX(displayWidth - sprite.get("playerSprite").getWidth());
		// checks to see if playerY is lower than the bottom of the display
		if (player.getY() < 0)
			player.setY(0);
		// checks to see if playerY is greater than the height of the display
		if (player.getY() > displayHeight - sprite.get("playerSprite").getHeight())
			player.setY(displayHeight - sprite.get("playerSprite").getHeight());
	}
	
	/* Renders the background, text, and all of the sprites */
	private void render() {
		// clear the screen and depth buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// draws the background
		drawScreen(sprite.get("background"));
		
		// drawing player
		if (!stopDrawingPlayer)
			drawEntity(player);
		// drawing bullets
		drawListEntity(bullet);
		// drawing enemy bullets
		drawListEntity(enemy_bullet);
		// drawing enemies
		drawListEntity(enemy);
		// drawing powerups
		drawListEntity(powerup);
		// drawing explosions
		drawListEntity(explosion);
		
		// draw health
		defaultFont.drawString(10, 10, "Health: " + player.getHP() + "/1000");
		// draw cash
		defaultFont.drawString(10, 40, "Cash: $" + cash);
		// draw shop prompt
		defaultFont.drawString(displayWidth - 280, displayHeight - 40, "Press F to enter shop");
		
		if (stopDrawingPlayer) {
			// draw Game Over
			gameOverFont.drawString(displayWidth / 2 - gameOverFont.getWidth("Game Over!") / 2, displayHeight
					/ 2 - gameOverFont.getHeight("Game Over!") / 2 - 50, "Game Over!");
			
			// draw the score
			scoreFont.drawString(displayWidth / 2 - scoreFont.getWidth("Score: " + totalCash) / 2, displayHeight
					/ 2
					+ scoreFont.getHeight("Game Over!")
					/ 2
					+ scoreFont.getHeight("Score: " + totalCash)
					/ 2 + 10 - 50, "Score: " + totalCash);
		}
	}
	
	/* Draws an entity */
	private void drawEntity(Entity ent) {
		Sprite entSprite = getSprite(ent.getName());
		
		glBindTexture(GL_TEXTURE_2D, entSprite.getTexture().getTextureID());
		
		// draw entity
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0, 0);
			glVertex2d(ent.getX(), ent.getY());
			glTexCoord2f(1, 0);
			glVertex2d(ent.getX() + entSprite.getWidth(), ent.getY());
			glTexCoord2f(1, 1);
			glVertex2d(ent.getX() + entSprite.getWidth(), ent.getY() + entSprite.getHeight());
			glTexCoord2f(0, 1);
			glVertex2d(ent.getX(), ent.getY() + entSprite.getHeight());
		}
		glEnd();
		
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	/* Draws all of the entities in a list */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void drawListEntity(ArrayList list) {
		ArrayList listRemove = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Entity ent = (Entity) list.get(i);
			
			drawEntity(ent);
			
			// if the entity is still in the screen, update its position
			if (ent.continueDrawing()) {
				if (ent instanceof Bullet) {
					if (((Bullet) ent).getName().equals("bullet")) {
						ent.setX(ent.getX() + ((Bullet) ent).getXChange());
						ent.setY(ent.getY() + ((Bullet) ent).getYChange());
					}
					if (((Bullet) ent).getName().equals("enemy_bullet")) {
						ent.setY(ent.getY() + 8);
					}
					if (((Bullet) ent).getName().equals("laser")) {
						listRemove.add(list.get(i));
					}
					if (((Bullet) ent).getName().equals("boss_bullet")) {
						ent.setX(ent.getX() + ((Bullet) ent).getXChange());
						ent.setY(ent.getY() + ((Bullet) ent).getYChange());
					}
				} else if (ent instanceof Enemy) {
					if (((Enemy) ent).getName().equals("green_box")) {
						ent.setY(ent.getY() + 5);
					}
					if (((Enemy) ent).getName().equals("red_box")) {
						ent.setY(ent.getY() + 3);
					}
					if (((Enemy) ent).getName().equals("boss") && bossExplosionNum == 0) {
						if (ent.getX() <= 0)
							bossMovement = 3;
						if (ent.getX() >= displayWidth - ((Enemy) ent).getSprite().getWidth())
							bossMovement = -3;
						if (ent.getY() <= 30)
							ent.setY(ent.getY() + 1);
						else
							ent.setX(ent.getX() + bossMovement);
					}
				} else if (ent instanceof Explosion) {
					if (((Explosion) ent).getName().equals("player_hit")) {
						ent.setX(((Explosion) ent).getEntity().getX());
						ent.setY(((Explosion) ent).getEntity().getY());
					} else if (((Explosion) ent).getName().equals("enemy_hit")) {
						ent.setX(((Explosion) ent).getEntity().getX() - 5);
						ent.setY(((Explosion) ent).getEntity().getY() - 5);
					}
				} else if (ent instanceof Powerup) {
					ent.setY(ent.getY() + 4);
				}
			}
			// else if the entity is outside of the screen, remove it
			else {
				listRemove.add(list.get(i));
				
				// handles the boss explosions
				handleBossExplosions(list, listRemove, i);
			}
			
			// if the player is dead, notify the player of death
			if (stopDrawingPlayer || gameWon) {
				notifyGameOver();
			}
		}
		for (int i = 0; i < listRemove.size(); i++) {
			list.remove(listRemove.get(i));
		}
		listRemove.clear();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void handleBossExplosions(ArrayList list, ArrayList listRemove, int i) {
		if (bossExplosionNum >= 1
				&& ((Entity) list.get(i)) instanceof Explosion
				&& (((Explosion) list.get(i)).getName().equals("enemy_explosion") || ((Explosion) list.get(i)).getName().equals("boss_explosion"))) {
			Entity entity = ((Explosion) list.get(i)).getEntity();
			int enemyX = entity.getX();
			int enemyY = entity.getY();
			Sprite bossSprite = ((Enemy) entity).getSprite();
			
			if (bossExplosionNum == 1) {
				sound.get("bossExplosionEffect").playAsSoundEffect(1.0f, 1.0f, false);
				explosion.add(new Explosion(this, "enemy_explosion", enemyX, enemyY, entity));
				Explosion lastExplosion = explosion.get(explosion.size() - 1);
				lastExplosion.setX(enemyX + bossSprite.getWidth() - lastExplosion.getSprite().getWidth());
				
				bossExplosionNum = 2;
			} else if (bossExplosionNum == 2) {
				sound.get("bossExplosionEffect").playAsSoundEffect(1.0f, 1.0f, false);
				explosion.add(new Explosion(this, "enemy_explosion", enemyX, enemyY, entity));
				Explosion lastExplosion = explosion.get(explosion.size() - 1);
				lastExplosion.setY(enemyY + bossSprite.getHeight() - lastExplosion.getSprite().getHeight());
				
				bossExplosionNum = 3;
			} else if (bossExplosionNum == 3) {
				sound.get("bossExplosionEffect").playAsSoundEffect(1.0f, 1.0f, false);
				explosion.add(new Explosion(this, "enemy_explosion", enemyX, enemyY, entity));
				Explosion lastExplosion = explosion.get(explosion.size() - 1);
				lastExplosion.setX(enemyX + bossSprite.getWidth() - lastExplosion.getSprite().getWidth());
				lastExplosion.setY(enemyY + bossSprite.getHeight() - lastExplosion.getSprite().getHeight());
				
				bossExplosionNum = 4;
			} else if (bossExplosionNum == 4) {
				// large boss explosion
				sound.get("bossExplosionEffect2").playAsSoundEffect(1.0f, 1.0f, false);
				explosion.add(new Explosion(this, "boss_explosion", enemyX, enemyY, entity));
				Explosion lastExplosion = explosion.get(explosion.size() - 1);
				lastExplosion.setX(enemyX + entity.getSprite().getWidth() / 2
						- lastExplosion.getSprite().getWidth() / 2 + 45);
				lastExplosion.setY(enemyY + entity.getSprite().getHeight() / 2
						- lastExplosion.getSprite().getWidth() / 2 + 30);
				
				bossExplosionNum = 5;
			} else if (bossExplosionNum == 5) {
				listRemove.add(entity);
				gameWon = true;
			}
		}
	}
	
	/* Draws a screen */
	private void drawScreen(Sprite spr) {
		Texture tex = spr.getTexture();
		
		tex.bind();
		
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0, tex.getHeight());
			glVertex2f(0, spr.getHeight());
			glTexCoord2f(tex.getWidth(), tex.getHeight());
			glVertex2f(spr.getWidth(), spr.getHeight());
			glTexCoord2f(tex.getWidth(), 0);
			glVertex2f(spr.getWidth(), 0);
			glTexCoord2f(0, 0);
			glVertex2f(0, 0);
		}
		glEnd();
	}
	
	/* Returns the display width */
	public int getDisplayWidth() {
		return displayWidth;
	}
	
	/* Returns the display height */
	public int getDisplayHeight() {
		return displayHeight;
	}
	
	/* Returns the sprite */
	public Sprite getSprite(String ref) {
		if (ref.equals("boss")) {
			return sprite.get("bossSprite");
		} else if (ref.equals("boss_bullet")) {
			return sprite.get("bossBulletSprite");
		} else if (ref.equals("boss_explosion")) {
			return sprite.get("bossExplosionSprite");
		} else if (ref.equals("boss_hit")) {
			return sprite.get("bossHitSprite");
		} else if (ref.equals("bullet")) {
			return sprite.get("bulletSprite");
		} else if (ref.equals("enemy_bullet")) {
			return sprite.get("enemyBulletSprite");
		} else if (ref.equals("enemy_explosion")) {
			return sprite.get("enemyExplosionSprite");
		} else if (ref.equals("enemy_hit")) {
			return sprite.get("enemyHitSprite");
		} else if (ref.equals("green_box")) {
			return sprite.get("greenBoxSprite");
		} else if (ref.equals("laser")) {
			return sprite.get("laserSprite");
		} else if (ref.equals("player")) {
			return sprite.get("playerSprite");
		} else if (ref.equals("player_explosion")) {
			return sprite.get("playerExplosionSprite");
		} else if (ref.equals("player_hit")) {
			return sprite.get("playerHitSprite");
		} else if (ref.equals("powerup_explosion")) {
			return sprite.get("powerupExplosionSprite");
		} else if (ref.equals("red_box")) {
			return sprite.get("redBoxSprite");
		} else {
			System.out.println("fatal error");
			System.out.println(ref);
			return null;
		}
	}
	
	/*
	 * Randomly generates a number between 100 and 3000 to determine the number of milliseconds between each enemy
	 */
	public int generateEnemyInterval() {
		enemyInterval = randomGenerator.nextInt(2901) + 300;
		
		return enemyInterval;
	}
	
	/* Randomly checks to see if a powerup will drop from an enemy */
	public void powerupCheck(int x, int y) {
		// checks to see if a powerup will drop
		double powerupCheck = randomGenerator.nextDouble();
		if (powerupCheck <= 0.07)
			powerup.add(new Powerup(this, "powerup_explosion", x, y));
	}
	
	/* Registers a hit */
	public void registerHit(Entity entity) {
		if (entity instanceof Enemy && !((Enemy) entity).getName().equals("boss")) {
			explosion.add(new Explosion(this, "enemy_hit", entity.getX() - 5, entity.getY() - 5, entity));
		} else if (entity instanceof Player) {
			sound.get("playerHitEffect").playAsSoundEffect(1.0f, 2.0f, false);
			explosion.add(new Explosion(this, "player_hit", entity.getX(), entity.getY(), entity));
		} else if (entity instanceof Enemy && ((Enemy) entity).getName().equals("boss")) {
			explosion.add(new Explosion(this, "boss_hit", entity.getX() - 5, entity.getY() - 5, entity));
		}
	}
	
	/* Removes an entity */
	public void removeEntity(Entity entity) {
		if (entity instanceof Bullet) {
			if (((Bullet) entity).getName().equals("bullet")) {
				bullet.remove(entity);
			} else if (((Bullet) entity).getName().equals("enemy_bullet")
					|| ((Bullet) entity).getName().equals("boss_bullet")) {
				enemy_bullet.remove(entity);
			}
		} else if (entity instanceof Enemy) {
			int enemyX = entity.getX();
			int enemyY = entity.getY();
			
			if (!((Enemy) entity).getName().equals("boss")) {
				enemy.remove(entity);
				// play enemy explosion sfx
				sound.get("enemyExplosionEffect").playAsSoundEffect(1.0f, 1.0f, false);
				
				// add enemy explosion
				explosion.add(new Explosion(this, "enemy_explosion", enemyX - 5, enemyY - 5, entity));
				powerupCheck(enemyX, enemyY);
			} else if (((Enemy) entity).getName().equals("boss") && bossExplosionNum == 0) {
				bossExplosionNum = 1;
				
				sound.get("bossExplosionEffect").playAsSoundEffect(1.0f, 1.0f, false);
				explosion.add(new Explosion(this, "enemy_explosion", enemyX, enemyY, entity));
			}
			
			// add cash and points to score
			if (!stopDrawingPlayer && ((Enemy) entity).getName().equals("green_box")) {
				cash += 30;
				totalCash += 30;
			} else if (!stopDrawingPlayer && ((Enemy) entity).getName().equals("red_box")) {
				cash += 50;
				totalCash += 50;
			} else if (!stopDrawingPlayer && ((Enemy) entity).getName().equals("boss")) {
				totalCash += 500;
			}
		} else if (entity instanceof Powerup) {
			// play obtain powerup sfx
			sound.get("powerupGetEffect").playAsSoundEffect(1.0f, 1.0f, false);
			
			Enemy boss = null;
			
			powerup.remove(entity);
			if (((Powerup) entity).getName() == "powerup_explosion") {
				for (int i = 0; i < enemy.size(); i++) {
					if (!enemy.get(i).getName().equals("boss")) {
						explosion.add(new Explosion(this, "enemy_explosion", enemy.get(i).getX() - 5,
								enemy.get(i).getY() - 5, entity));
					} else {
						boss = enemy.get(i);
					}
				}
				for (int i = 0; i < enemy.size(); i++) {
					if (enemy.get(i).getName().equals("green_box")) {
						cash += 30;
						totalCash += 30;
					} else if (enemy.get(i).getName().equals("red_box")) {
						cash += 50;
						totalCash += 50;
					}
				}
				// play enemy explosion sfx
				sound.get("enemyExplosionEffect").playAsSoundEffect(1.0f, 1.0f, false);
				enemy.clear();
				
				if (boss != null)
					enemy.add(boss);
			}
		} else if (entity instanceof Player) {
			// play player explosion sfx
			sound.get("playerExplosionEffect").playAsSoundEffect(1.0f, 1.0f, false);
			
			explosion.add(new Explosion(this, "player_explosion", player.getX() - 5, player.getY() - 5,
					entity));
			stopDrawingPlayer = true;
		}
	}
	
	/* Notifies the player that the game is over */
	public void notifyGameOver() {
		gameOver = true;
	}
	
	/* Main method */
	public static void main(String[] args) {
		new Game().start();
	}
}