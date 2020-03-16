package game.systems.rendering;

import com.badlogic.gdx.graphics.Texture;

public class TextureID
{
	public static int genid( Texture texture )
	{
		return texture.hashCode();
	}
}
