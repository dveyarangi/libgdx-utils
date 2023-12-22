package game.world;

import com.badlogic.ashley.core.EntitySystem;

import lombok.Getter;

public abstract class Chronometer extends EntitySystem
{
	@Getter protected boolean isPaused = false;
	public abstract float getGameTime();

	public abstract float toGameTime(float seconds);
	
	public abstract float toSystemTime(float gameSeconds);

	public abstract float getTimeSpeed();
	public abstract void setTimeSpeed(float timeSpeed);

	public void togglePause()
	{
		isPaused = !isPaused;
	}
}
