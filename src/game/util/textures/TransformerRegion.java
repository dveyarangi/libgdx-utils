package game.util.textures;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransformerRegion extends TextureRegion
{
	public TransformerRegion(Texture texture, int x, int y, int width, int height) 
	{
		super(texture, x, y, width, height);
	}

}
