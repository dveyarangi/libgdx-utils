package game.systems.rendering;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.world.Level;

public class TileMultiSpriteComponent  implements IRenderingComponent
{
	TileMultiSpriteDef def;
	List <TileSpriteComponent> sprites = new ArrayList <> ();

	@Override
	public void init(Entity entity, IComponentDef<?> def, Level level)
	{
		this.def = (TileMultiSpriteDef) def;
		
		for(TileSpriteDef spriteDef : this.def.defs)
		{
			TileSpriteComponent tileSprite = level.getEngine().createComponent(TileSpriteComponent.class);
			
			tileSprite.init(entity, spriteDef, level);
			
			sprites.add(tileSprite);
			
		}
	}

	@Override
	public void render(Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void reset()
	{
		this.def = null;
		this.sprites.clear();
	}

	int [] cid = new int [0];

	@Override
	public int[] cid() { return cid; }

}
