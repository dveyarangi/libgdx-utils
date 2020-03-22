package game.world;

import java.util.ArrayList;
import java.util.List;

import game.systems.EntityDef;
import game.systems.SystemDef;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines a game level.
 *
 * This class can be interchanged with json representation for saving/loading.
 *
 *
 * @author Fima
 *
 */
public class LevelDef
{
	private String name;
	
	@Getter @Setter private int centerX, centerY;

	@Getter private int width, halfWidth;
	@Getter private int height, halfHeight;

	private LevelInitialSettings initialSettings;

	// private IBackground background = new DummyBackground();

	private List<SystemDef<?>> systems = new ArrayList<> ();

	private List<EntityDef> entities = new ArrayList<>();

	public void setWidth( final int width )
	{
		this.width = width;
		halfWidth = width / 2;
	}

	public void setHeight( final int height )
	{
		this.height = height;
		halfHeight = height / 2;
	}

	// public IBackground getBackgroundDef() { return background; }
	// public void setBackgroundDef( final IBackground background ) {
	// this.background = background; }
	List<SystemDef<?>> getSystemDefs()
	{
		return systems;
	}
	
	public void addSystem(SystemDef <?> systemDef)
	{
		systems.add(systemDef);
	}

	public List<EntityDef> getEntityDefs()
	{
		return entities;
	}
	
	public void addEntity(EntityDef entityDef)
	{
		entities.add(entityDef);
	}

	// public Map <String, AnimationHandle> getAnimations() { return animations;
	// }

	public String getName()
	{
		return name;
	}

	public LevelInitialSettings getInitialSettings()
	{
		return initialSettings;
	}

	public void setInitialSettings( final LevelInitialSettings settings )
	{
		this.initialSettings = settings;
	}

}
