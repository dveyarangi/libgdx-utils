package game.resources;

public class AnimationHandle
{
	private String atlasName;
	private final String regionGroupName;

	public AnimationHandle( final String atlasName, final String regionGroupName )
	{
		this.atlasName = atlasName;
		this.regionGroupName = regionGroupName;
	}

	public String getAtlas()
	{
		return atlasName;
	}

	public String getRegionName()
	{
		return regionGroupName;
	}

	@Override
	public int hashCode()
	{
		return atlasName.hashCode() ^ regionGroupName.hashCode();
	}

	@Override
	public boolean equals( final Object object )
	{
		AnimationHandle that = (AnimationHandle) object;
		return regionGroupName.equals(that.regionGroupName)
				&& atlasName.equals(that.atlasName);
	}

	@Override
	public String toString()
	{
		return atlasName + ":" + regionGroupName;
	}
}
