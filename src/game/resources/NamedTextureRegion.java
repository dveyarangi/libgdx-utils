package game.resources;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import lombok.Getter;

public class NamedTextureRegion extends TextureRegion
{

	@Getter private TextureRegionName name;

	public NamedTextureRegion(TextureRegionName regionName, TextureRegion region)
	{
		this.name = regionName;
		this.setRegion(region);
	}
}
