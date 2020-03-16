package game.screen;

import game.systems.rendering.Renderer;

/**
 * Cross-screen rendering overlay for screen transition smoothing and stuff.
 * 
 * @author Fima
 *
 */
public interface IGameOverlay
{
	public void setFadeIn( AbstractScreen screen );

	public void setFadeOut( AbstractScreen screen );

	public void updateOverlay( float delta );

	public void drawOverlay( Renderer renderer );

	public boolean isTotallyFaded();

}
