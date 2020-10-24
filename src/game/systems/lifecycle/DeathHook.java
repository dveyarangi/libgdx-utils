package game.systems.lifecycle;

import com.badlogic.ashley.core.Entity;

public interface DeathHook {
	void entityDead(Entity entity);
}
