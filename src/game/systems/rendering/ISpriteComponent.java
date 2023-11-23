package game.systems.rendering;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public interface ISpriteComponent extends IRenderingComponent
{

	public abstract void scale(float size);
	
	/**
	 * Create an icon of this sprite
	 * @return
	 */
	public Drawable createDrawable();
}
