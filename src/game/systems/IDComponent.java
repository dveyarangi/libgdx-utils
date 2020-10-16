package game.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class IDComponent implements Component, Poolable
{
	String id;
	public String toString() { return id; }

	@Override
	public void reset()
	{
		id = null;
	}
}
