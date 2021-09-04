package game.systems.rendering;

import game.resources.TextureName;

public class SpriteTextureDef extends SpriteDef <SpriteTextureComponent>
{
	public TextureName textureName;


	public SpriteTextureDef(String textureName)
	{
		this(new TextureName(textureName), 0, 0, 0, 1, 1);
	}

	public SpriteTextureDef(TextureName textureName, float xOffset, float yOffset, float zOffset, float w, float h)
	{
		super(xOffset, yOffset, zOffset, w, h);
		this.textureName = textureName;
	}

	@Override
	public Class<SpriteTextureComponent> getComponentClass() { return SpriteTextureComponent.class; }

	/*	@Override
	public void initComponent( SpriteComponent component, Entity entity, Level level )
	{
		//ComponentType.registerFor(IRenderingComponent.class, component.getClass());
		//ResourceFactory factory = level.getModules().getGameFactory();
		component.init( entity, this, level );


		component.origRegion = ResourceFactory.getTextureRegion(this.textureName.getName());
	}*/

}
