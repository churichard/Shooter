package shooter;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import java.awt.Color;

public class SimpleFont {
	private UnicodeFont font;
	
	// Constructor - constructs a new SimpleFont
	@SuppressWarnings("unchecked")
	public SimpleFont(String res, String style, int size, Color color) throws SlickException {
		if (style.equals("bold")){
			this.font = new UnicodeFont(res, size, true, false);
		}
		else if (style.equals("italic")){
			this.font = new UnicodeFont(res, size, false, true);
		}
		else if (style.equals("both")){
			this.font = new UnicodeFont(res, size, true, true);
		}
		else if (style.equals("none")){
			this.font = new UnicodeFont(res, size, false, false);
		}
		else {
			this.font = new UnicodeFont(res, size, false, false);
		}
		this.font.getEffects().add(new ColorEffect(color));
		this.font.addAsciiGlyphs();
		this.font.loadGlyphs();
	}
	
	// Constructor when there is no color specified
	public SimpleFont(String res, String style, int size) throws SlickException {
		this(res, style, size, Color.white);
	}
	
	// Draws the string
	public void drawString(int x, int y, String text) {
		get().drawString(x, y, text);
	}
	
	// Gets the width of the string
	public int getWidth(String s) {
		return get().getWidth(s);
	}
	
	// Gets the height of the string
	public int getHeight(String s) {
		return get().getHeight(s);
	}
	
	// Gets the UnicodeFont
	public UnicodeFont get() {
		return font;
	}
}