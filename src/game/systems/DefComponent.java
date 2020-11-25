package game.systems;

import lombok.Getter;

/**
 * Stateless self-defining component
 */
public class DefComponent implements IDefComponent 
{
	@Getter private Class<?> componentClass;
	
	protected DefComponent()
	{
		this.componentClass = this.getClass();
	}

	@Override
	public void reset() { }
}
