package game.systems.rendering;

import com.badlogic.gdx.graphics.Texture;

public class TextureID
{
	public static int genid( Texture texture )
	{
		return texture.hashCode()+100; 
		// allocate 100 ids for some purposes; see EntityRenderingContext for contexts 
	}
}
