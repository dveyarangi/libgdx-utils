package game.systems.rendering;

import com.badlogic.ashley.core.ComponentType;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import game.debug.Debug;
import game.systems.IComponentDef;
import game.world.Level;
import game.world.saves.Props;
import game.world.saves.Savable;

public class SpriteRegionComponent extends SpriteComponent implements Savable <SpriteRegionDef>
{
	static { ComponentType.registerFor(SpriteComponent.class, SpriteRegionComponent.class); }

	@Override
	public void init( Entity entity, IComponentDef def, Level level )
	{
		SpriteRegionDef tdef = (SpriteRegionDef) def;
		TextureAtlas atlas = tdef.atlas;
		TextureRegion origRegion;
		if( tdef.regionName == null)
		{
			origRegion = atlas.getRegions().get(0);
			Debug.warn("RegionRenderingDef does not specify regionName");
		}
		else
			origRegion = atlas.findRegion(tdef.regionName);

		this.region.setRegion(origRegion);

		super.init(entity, tdef, level);
	}

	@Override
	public Class<SpriteRegionDef> getDefClass() { return SpriteRegionDef.class; }
	@Override
	public void save(SpriteRegionDef def, Props props)
	{
		super.save(def, props);
	}
	@Override
	public void load(SpriteRegionDef def, Props props)
	{
		super.load(def, props);
	}

	@Override
	public Drawable createDrawable()
	{
		return new TextureRegionDrawable(this.getRegion());
	}
}
