package shooter;

import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Sprite{
	//the texture that stores the images for this sprite
	private Texture texture;

	//the width of the sprite
	private int width;
	//the height of the sprite
	private int height;
	
	/* Constructor - creates the sprite texture */
	public Sprite(String name){
		try {
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("/res/"+name+".png"));
			width = (int)(texture.getImageWidth()/1.3);
			height = (int)(texture.getImageHeight()/1.3);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/* Returns the width of the image */
	public int getWidth(){
		return width;
	}
	
	/* Returns the height of the image */
	public int getHeight(){
		return height;
	}
	
	/* Sets the width of the image */
	public void setWidth(int w){
		width = w;
	}
	
	/* Sets the height of the iamge */
	public void setHeight(int h){
		height = h;
	}
	
	/* Returns the texture */
	public Texture getTexture(){
		return texture;
	}
}