package game.systems.rendering;

import com.badlogic.ashley.core.ComponentType;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import game.resources.ResourceFactory;
import game.systems.IComponentDef;
import game.world.Level;
import game.world.saves.Props;
import game.world.saves.Savable;

public class SpriteTextureComponent extends SpriteComponent implements Savable <SpriteTextureDef>
{
	static { ComponentType.registerFor(SpriteComponent.class, SpriteTextureComponent.class); }

	@Override
	public void init( Entity entity, IComponentDef def, Level level )
	{
		SpriteTextureDef tdef = (SpriteTextureDef) def;

		TextureRegion origRegion = ResourceFactory.getTextureRegion(tdef.textureName.getName());


		this.region.setRegion(origRegion);
		if( tdef.color != null )
			this.color.set(tdef.color);

		super.init(entity, tdef, level);
	}
	@Override
	public Class<SpriteTextureDef> getDefClass() { return SpriteTextureDef.class; }
	@Override
	public void save(SpriteTextureDef def, Props props)
	{
		super.save(def, props);
	}
	@Override
	public void load(SpriteTextureDef def, Props props)
	{
		super.load(def, props);
	}
	@Override
	public Drawable createDrawable()
	{
		return new TextureRegionDrawable(this.getRegion());
	}

}
