package game.systems.rendering;

import com.badlogic.ashley.core.ComponentType;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import game.systems.IComponentDef;
import game.systems.spatial.GenericSpatialComponent;
import game.systems.spatial.SpatialComponent;
import game.systems.spatial.SpatialListener;
import game.world.Level;

public class TileMultiSpriteComponent implements ISpriteComponent, SpatialListener
{
	static
	{   // map as rendering component:
		ComponentType.registerFor(ISpriteComponent.class, TileMultiSpriteComponent.class);
	}	
	
	//TileMultiSpriteDef def;
	Array <TileSpriteComponent> sprites = new Array <> ();

	@Override
	public void init(Entity entity, IComponentDef<?> cdef, Level level)
	{
		TileMultiSpriteDef def = (TileMultiSpriteDef) cdef;
		
		for(int idx = 0; idx < def.defs.size(); idx ++)
		{
			TileSpriteDef spriteDef = def.defs.get(idx);
			TileSpriteComponent tileSprite = level.getEngine().createComponent(TileSpriteComponent.class);
			
			tileSprite.init(entity, spriteDef, level);
			
			sprites.add(tileSprite);
			
		}
		
		SpatialComponent spatial = entity.getComponent(SpatialComponent.class);
		spatial.addListener(this);
	}

	@Override
	public void render(Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void reset()
	{
		this.sprites.clear();
	}

	int [] cid = new int [0];

	@Override
	public int[] cid() { return cid; }

	@Override
	public void scale(float size)
	{
		for(int idx = 0; idx < sprites.size; idx ++)
			sprites.get(idx).scale(size);
	}

	public void setColor(Color color)
	{
		for(int idx = 0; idx < sprites.size; idx ++)
			sprites.get(idx).setColor(color);
	}
	
	@Override
	public void spatialChanged(GenericSpatialComponent component)
	{
		scale(component.r());
	}

	@Override
	public Drawable createDrawable()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
