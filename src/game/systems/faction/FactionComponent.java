package game.systems.faction;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool.Poolable;

public class FactionComponent implements Component, Poolable
{
	public static ComponentMapper<FactionComponent> MAPPER = ComponentMapper.getFor(FactionComponent.class);

	public static FactionComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	public int factionId;
	public AFaction faction;

	public int id()
	{
		return factionId;
	}

	public Color color()
	{
		return faction.getDef().getColor();
	}

	@Override
	public void reset()
	{
		faction = null;
	}

}
