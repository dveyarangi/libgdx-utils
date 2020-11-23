package game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Dummy solid color background drawable for UI elements
 */
public class BackgroundColor implements Drawable {
	

	private static Texture DEFAULT_TEXTURE = new Texture(Gdx.files.internal("images/white_512x512.png"));
	private TextureRegion textureRegion;
	private Color color;

	public BackgroundColor(Color color)
	{
		this(color.r, color.g, color.b, color.a);
	}
	
	public BackgroundColor(float r, float g, float b, float a) 
	{
		this(new TextureRegion(DEFAULT_TEXTURE), r, g, b, a);
	}

	public BackgroundColor(TextureRegion textureRegion, float r, float g, float b, float a) 
	{
		this.textureRegion = textureRegion;
		this.color = new Color();
		this.setColor(r, g, b, a);
	}

	public void setColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
	}
	
	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		batch.setColor(color);
		batch.draw(textureRegion, x, y, width, height);
	}

	@Override public float getLeftWidth() { return 0; }
	@Override public void setLeftWidth(float leftWidth) { }
	@Override public float getRightWidth() { return 0; }
	@Override public void setRightWidth(float rightWidth) { }
	@Override public float getTopHeight() { return 0; }
	@Override public void setTopHeight(float topHeight) { }
	@Override public float getBottomHeight() { return 0; }
	@Override public void setBottomHeight(float bottomHeight) { }
	@Override public float getMinWidth() { return 0; }
	@Override public void setMinWidth(float minWidth) { }
	@Override public float getMinHeight() { return 0; }
	@Override public void setMinHeight(float minHeight) { }

}