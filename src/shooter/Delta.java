package shooter;

import org.lwjgl.Sys;

public class Delta {
	private static long lastFrame;
	private static long lastBullet;
	private static long lastEnemy;
	private static long beginningTime = getTime();
	
	/* Gets the time in milliseconds */
	public static long getTime(){
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/* Finds the change in time since the last frame/enemy/bullet */
	public static int getDelta(String name){
		long lastDelta = lastFrame;
		long time = getTime();
		if (name.equals("frame")){
			lastDelta = lastFrame;
			lastFrame = time;
		}
		else if (name.equals("enemy")){
			lastDelta = lastEnemy;
		}
		else if (name.equals("bullet")){
			lastDelta = lastBullet;
		}
		else if (name.equals("beginning")){
			lastDelta = beginningTime;
		}
		
		int delta = (int) (time - lastDelta);
		return delta;
	}
	
	/* Sets lastBullet */
	public static void setLastBullet(long lastBullet){
		Delta.lastBullet = lastBullet;
	}
	
	/* Sets lastEnemy */
	public static void setLastEnemy(long lastEnemy){
		Delta.lastEnemy = lastEnemy;
	}
}