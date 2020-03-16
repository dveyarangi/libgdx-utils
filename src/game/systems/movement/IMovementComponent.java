package game.systems.movement;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public interface IMovementComponent extends Component, Poolable
{

	float getMaxSpeed();

}
