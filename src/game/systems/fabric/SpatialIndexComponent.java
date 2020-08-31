package game.systems.fabric;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.systems.spatial.ISpatialComponent;
import lombok.Getter;
import yarangi.spatial.AABB;
import yarangi.spatial.ISpatialObject;

public class SpatialIndexComponent implements ISpatialObject, Component, Poolable
{

	@Getter Entity entity;
	
	//@Getter SpatialIndexDef def;
	
	@Getter AABB area = AABB.createFromCenter(0, 0, 0, 0, 0);

	@Getter public boolean isStatic;
	
	public void init(Entity entity)
	{
		this.entity = entity;
		
		ISpatialComponent spatial = ISpatialComponent.get(entity);
		
		area.move(spatial.x(), spatial.y());
		area.fitTo(spatial.r());
	}

	@Override
	public void reset()
	{
		this.entity = null;
	}
	
}