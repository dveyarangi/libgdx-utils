package game.systems.rendering;

import game.resources.TextureName;

public class SpriteTextureDef extends SpriteDef
{
	public TextureName textureName;
	

	public SpriteTextureDef(String textureName)
	{
		this(new TextureName(textureName),0, 0, 0, 1, 1, false, false);
	}
	public SpriteTextureDef(TextureName textureName, float priority, float ox, float oy, float w, float h, boolean xFlip, boolean yFlip)
	{
		super(priority, ox, oy, w, h, xFlip, yFlip);
		this.textureName = textureName;
	}


	
}
