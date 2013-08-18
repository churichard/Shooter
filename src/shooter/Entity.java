package shooter;

import java.awt.Rectangle;
import org.lwjgl.opengl.Display;

public abstract class Entity {
	//sprite class
	private Sprite sprite;
	
	//the x location of the entity
	protected int x;
	//the y location of the entity
	protected int y;
	
	//the rectangle used for this entity
	private Rectangle entity1 = new Rectangle();
	//the rectangle used for other entities
	private Rectangle entity2 = new Rectangle();
	
	/* Constructor */
	public Entity(Sprite sprite, int x, int y){
		this.sprite = sprite;
		this.x = x;
		this.y = y;
	}
	
	/* Returns x */
	public int getX(){
		return x;
	}
	
	/* Sets x */
	public void setX(int x){
		this.x = x;
	}
	
	/* Returns y */
	public int getY(){
		return y;
	}
	
	/* Sets y */
	public void setY(int y){
		this.y = y;
	}
	
	/* Returns the entity sprite */
	public Sprite getSprite(){
		return sprite;
	}
	
	/* Checks to see if the entity should continue to be drawn */
	public boolean continueDrawing(){
		return x >= -getSprite().getWidth() && x <= Display.getWidth() && y >= -getSprite().getHeight() && y <= Display.getHeight();
	}
	
	/* Checks to see if this entity has collided with another entity */
	public boolean collidesWith(Entity other){
		entity1.setBounds(x, y, sprite.getWidth(), sprite.getHeight());
		entity2.setBounds(other.x, other.y, other.sprite.getWidth(), other.sprite.getHeight());
		
		return entity1.intersects(entity2);
	}
	
	/* This entity has collided with another entity */
	public abstract void collidedWith(Entity other);
}