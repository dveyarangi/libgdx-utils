package game.systems.rendering;


public class TextureRenderingDef extends RendererDef<SpriteRenderingComponent>
{
	public String textureName;

	public TextureRenderingDef( String textureName )
	{
		super(SpriteRenderingComponent.class);
		this.textureName = textureName;
	}

	
}
