package game.systems.control;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.world.Transient;
import lombok.Getter;

@Transient
public class PickComponent implements Component, Poolable, Comparable <PickComponent>
{
	PickDef def;
	
	@Getter Entity entity;
	
	@Override
	public void reset()
	{
		this.def = null;
		this.entity = null;
	}

	@Override
	public int compareTo(PickComponent that)
	{
		return that.def.getPriority() - this.def.getPriority();
	}

}
