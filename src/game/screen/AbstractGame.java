package game.screen;

import com.badlogic.gdx.Game;

import game.resources.ResourceFactory;

public abstract class AbstractGame extends Game
{

	protected IGameOverlay overlay;

	ResourceFactory resourceFactory;

	public AbstractGame()
	{

	}

	/**
	 *  @return Default overlay renderer for transition between game screens
	 */
	public <E extends IGameOverlay> E getTransitionOverlay() { return null; };

	@Override
	public abstract void create();

	public ResourceFactory getResourceFactory() { return resourceFactory; }
}
