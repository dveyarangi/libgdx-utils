package game.systems.rendering;

import java.util.ArrayList;
import java.util.List;

public class TileMultiSpriteDef  extends RendererDef<TileMultiSpriteComponent>
{
	public List <TileSpriteDef> defs = new ArrayList <> ();

	public TileMultiSpriteDef()
	{
		super();
	}

	@Override
	public Class<TileMultiSpriteComponent> getComponentClass() { return TileMultiSpriteComponent.class; }

}
