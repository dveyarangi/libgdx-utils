package game.world.environment.nav;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PivotComponent implements Component, Poolable
{
	private Vector2 target = new Vector2();
	private float angle;

	public Vector2 target() { return target; }

	public float angle() { return angle; }

	@Override
	public void reset()
	{
		target.set(0,0);
		angle = 0;
	}
}
