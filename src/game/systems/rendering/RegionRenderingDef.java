package game.systems.rendering;


public class RegionRenderingDef extends RendererDef<SpriteRenderingComponent>
{
	public String atlasName;
	public String regionName;

	public RegionRenderingDef( String atlasName, String regionName )
	{
		super( SpriteRenderingComponent.class );
		this.atlasName = atlasName;
		this.regionName = regionName;
	}
}
