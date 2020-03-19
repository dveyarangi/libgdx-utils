package game.systems.rendering;


public class RegionRenderingDef extends RendererDef<SpriteComponent>
{
	public String atlasName;
	public String regionName;

	public RegionRenderingDef( String atlasName, String regionName )
	{
		super( SpriteComponent.class );
		this.atlasName = atlasName;
		this.regionName = regionName;
	}
}
