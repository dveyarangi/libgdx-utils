package game.world;

import com.badlogic.ashley.core.Entity;

public interface IPickProvider
{

	Entity pick( float x, float y, float pickRadius );

}
