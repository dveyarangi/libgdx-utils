package game.systems.rendering;

import game.resources.TextureName;
import game.util.rendering.HAlign;
import game.util.rendering.VAlign;

public class SpriteTextureDef extends SpriteDef
{
	public TextureName textureName;
	

	public SpriteTextureDef(String textureName)
	{
		this(new TextureName(textureName),0, 0, 0, 1, 1, false, false, HAlign.CENTER, VAlign.CENTER);
	}
	public SpriteTextureDef(TextureName textureName, float priority, float ox, float oy, float w, float h, boolean xFlip, boolean yFlip, HAlign hAlign, VAlign vAlign)
	{
		super(priority, ox, oy, w, h, xFlip, yFlip, hAlign, vAlign);
		this.textureName = textureName;
	}


	
}
