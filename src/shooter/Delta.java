package shooter;

import org.lwjgl.Sys;

public class Delta {
	private long lastFrame;
	private long lastBullet;
	private long lastEnemy;
	
	/*
	 * Gets the time in milliseconds
	 */
	public long getTime(){
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/*
	 * Finds the change in time since the last frame
	 */
	public int getDelta(){
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;

		return delta;
	}
	
	/*
	 * Finds the change in time since the last enemy appeared
	 */
	public int getEnemyDelta(){
		long time = getTime();
		int enemyDelta = (int)(time - lastEnemy);

		return enemyDelta;
	}
	
	/*
	 * Finds the change in time since the last bullet fired
	 */
	public int getBulletDelta(){
		long time = getTime();
		int bulletDelta = (int)(time - lastBullet);

		return bulletDelta;
	}
	
	/*
	 * Sets lastBullet
	 */
	public void setLastBullet(long lastBullet){
		this.lastBullet = lastBullet;
	}
	
	/*
	 * Sets lastEnemy
	 */
	public void setLastEnemy(long lastEnemy){
		this.lastEnemy = lastEnemy;
	}
}