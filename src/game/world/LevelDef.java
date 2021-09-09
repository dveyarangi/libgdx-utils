package game.world;

import java.util.ArrayList;
import java.util.List;

import game.systems.EntityPrefab;
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

	@Getter @Setter private float centerX, centerY;

	@Getter private int width;
	@Getter private int height;

	private LevelInitialSettings initialSettings;

	// private IBackground background = new DummyBackground();

	private ArrayList<SystemDef<?>> systems = new ArrayList<> ();

	private ArrayList<EntityPrefab> entities = new ArrayList<>();

	public void setWidth( final int width )
	{
		this.width = width;
	}

	public void setHeight( final int height )
	{
		this.height = height;
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

	public ArrayList<EntityPrefab> getEntityDefs()
	{
		return entities;
	}

	public void addEntity(EntityPrefab entityDef)
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
