package game.systems.rendering;

import game.resources.TextureName;
import game.util.rendering.HAlign;
import game.util.rendering.VAlign;

public class SpriteTextureDef extends SpriteDef
{
	public TextureName textureName;
	

	public SpriteTextureDef(TextureName textureName, float priority, float ox, float oy, float w, float h, boolean xFlip, boolean yFlip, HAlign hAlign, VAlign vAlign)
	{
		super(priority, ox, oy, w, h, xFlip, yFlip, hAlign, vAlign);
		this.textureName = textureName;
	}


	
}
