package game.systems;

import com.badlogic.ashley.core.EntitySystem;

import game.systems.rendering.IRenderingContext;
import game.world.Level;
import game.world.saves.SavableSystem;
import game.world.saves.SavedSystem;
import lombok.Getter;

public class SystemDef <S extends EntitySystem>
{
	protected Class <S> systemClass;

	@Getter protected S system;

	

	protected IRenderingContext renderer;

	private SavedSystem loadedData;

	public SystemDef()
	{
	}
	public SystemDef( Class<S> systemClass)
	{
		this.systemClass = systemClass;
	}

	public SystemDef( S system )
	{
		this.system = system;
	}

	public SystemDef( S system, IRenderingContext renderer )
	{
		this.system = system;
		this.renderer = renderer;
	}
	
	public SystemDef( S system, SavedSystem loadedData)
	{
		this.system = system;
		this.loadedData = loadedData;
	}

	@SuppressWarnings("deprecation")
	public S createSystem()
	{
		if(system != null) return system;
		try
		{
			system = systemClass.newInstance();
		} 
		catch( InstantiationException | IllegalAccessException e )
		{
			throw new IllegalArgumentException("Entity system of type " + systemClass + " not found.");
		} 

		return system;
	}

	public void initSystem( Level level, S system )
	{
		if(!(system instanceof SavableSystem))
			return;
		
		SavableSystem savableSystem = (SavableSystem) system;
		if(this.loadedData != null) 
			savableSystem.load(level, this.loadedData);
	}

	public IRenderingContext createRenderer() { return renderer; }

}
