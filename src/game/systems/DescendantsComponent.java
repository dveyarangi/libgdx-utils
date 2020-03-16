package game.systems;

import java.util.ArrayList;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class DescendantsComponent  extends ArrayList <Entity> implements Component, Poolable
{

	public static ComponentMapper<DescendantsComponent> MAPPER = ComponentMapper.getFor(DescendantsComponent.class);

	public static DescendantsComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	public DescendantsComponent()
	{
		super();
	}

/*	public List<Entity> descendands()
	{
		return descendands;
	}*/

	@Override
	public void reset()
	{
		this.clear();
	}
	
	private static final long serialVersionUID = 5578187795397781118L;

}
