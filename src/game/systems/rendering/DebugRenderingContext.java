package game.systems.rendering;

import game.resources.ResourceFactory;
import lombok.Getter;

/**
 * Defines parameters for a texture context.
 * TODO: not in use
 *
 * @author Fima
 */
public class DebugRenderingContext implements IRenderingContext
{
	private int contextId;

	@Getter private IRenderer renderer;

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
	
	@Override public String toString() { return "debug rendering context"; }

}
