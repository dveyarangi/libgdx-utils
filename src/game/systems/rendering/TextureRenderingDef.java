package game.systems.rendering;


public class TextureRenderingDef extends RendererDef<SpriteComponent>
{
	public String textureName;
	
	public float ox = Float.NaN;
	public float oy = Float.NaN;
	public float w = Float.NaN;
	public float h = Float.NaN;

	public TextureRenderingDef( String textureName )
	{
		super(SpriteComponent.class);
		this.textureName = textureName;
	}


	
}
