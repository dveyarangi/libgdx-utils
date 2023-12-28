package game.systems.fabric;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.systems.spatial.ISpatialComponent;
import game.world.Transient;
import lombok.Getter;
import yarangi.spatial.AABB;
import yarangi.spatial.ISpatialObject;

/**
 * Entities having this component will be managed by SpatialFabric
 *
 * @author Fima
 */
@Transient
public class SpatialIndexComponent implements ISpatialObject, Component, Poolable
{
	private static ComponentMapper<SpatialIndexComponent> mapper =	
	ComponentMapper.getFor(SpatialIndexComponent.class);
	public static SpatialIndexComponent get(Entity entity) { return mapper.get(entity); }

	@Getter Entity entity;

	//@Getter SpatialIndexDef def;

	@Getter AABB oldArea = AABB.createFromCenter(0, 0, 1, 1, 0);
	@Getter AABB area = AABB.createFromCenter(0, 0, 1, 1, 0);

	@Getter public boolean isStatic = true;
	
	@Getter CategorySet categories;

	public void init(Entity entity)
	{
		this.entity = entity;

		update();
	}

	// TODO: when updating, SpatialFabric translates the area to it's coordinate system
	// this is redundant and can cause mirky bugs
	void update()
	{
		ISpatialComponent spatial = ISpatialComponent.get(entity);
		oldArea.paste(area);
		area.move(spatial.x(), spatial.y());
		area.fitTo(spatial.r());
	}
	@Override
	public void reset()
	{
		this.entity = null;
	}

}
