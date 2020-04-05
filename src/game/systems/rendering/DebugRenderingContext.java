package game.systems.rendering;

import game.resources.ResourceFactory;

/**
 * Defines parameters for a texture context.
 *
 * @author Fima
 */
public class DebugRenderingContext implements IRenderingContext
{
	private int contextId;

	private IRenderer renderer;

	public DebugRenderingContext(int contextId)
	{
		this.contextId = contextId;
	}

	@Override
	public void init( ResourceFactory factory, IRenderer renderer )
	{
		this.renderer = renderer;
	}

	@Override
	public void begin()
	{
	}

	@Override
	public void end()
	{
	}

	@Override
	public int id()
	{
		return contextId;
	}

}
