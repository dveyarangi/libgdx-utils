package game.systems.rendering;


public class TextureRenderingDef extends RendererDef<SpriteComponent>
{
	public String textureName;

	public TextureRenderingDef( String textureName )
	{
		super(SpriteComponent.class);
		this.textureName = textureName;
	}

	
}
