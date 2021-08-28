package game.systems.control;

import java.util.Map;
import java.util.TreeMap;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import game.debug.Debug;
import game.systems.order.ControlComponent;
import game.systems.rendering.IRenderer;
import game.world.Level;
import lombok.Getter;
import lombok.Setter;

/**
 * Managed differen control modes and propagates input signals to the current control mode
 *
 *
 * @author fimar
 */
public class ControlModes// extends EntitySystem
{

	private Map <String, IControlMode> controlModes;
	private String currModeName;
	private IControlMode currControlMode;

	@Getter @Setter private IEntityFilter pickFilter;
	boolean controlModeChanged = false;

	public ControlModes()
	{
		controlModes = new TreeMap<>();
	}


	public void addMode( String name, IControlMode mode )
	{
		controlModes.put(name, mode);
	}

	public void init( Level level )
	{

		if( !controlModes.isEmpty() )
		{
			switchToMode(controlModes.keySet().iterator().next(), null);

			Family family = Family.one( ControlComponent.class ).get();
			for(IControlMode mode : controlModes.values())
				level.getEngine().addEntityListener( family, mode );
		}
	}

	//@Override
	public void update( float delta )
	{
		if( !controlModes.isEmpty() )
			currControlMode.update(delta);
	}

	public void setWorldPointer( Vector2 worldPos, float zoom )
	{
		if( !controlModes.isEmpty() )
			currControlMode.setWorldPointer(worldPos, zoom);
	}

	public void objectPicked( Entity pickedObject )
	{
		if( !controlModes.isEmpty() )
			currControlMode.objectPicked(pickedObject);
	}

	public void switchToMode(String modeName, Object parameter)
	{

		if(modeName.equals(currModeName))
			return;
		IControlMode newMode = controlModes.get(modeName);
		if( newMode == null )
			throw new IllegalArgumentException("Unknown mode " + modeName);

		controlModeChanged = true;

		if(currControlMode != null)
			currControlMode.modeDeactivated();

		currControlMode = newMode;

		Debug.log("New control mode: " + currControlMode);

		newMode.modeActivated(parameter);

		currModeName = modeName;
	}

	public void keyDown( int keycode )
	{
		if( controlModes.isEmpty() )
			return;

		currControlMode.keyDown(keycode);

		if( controlModeChanged )
		{
			currControlMode.keyDown(keycode);
			controlModeChanged = false;
		}
	}

	public void keyUp( int keycode )
	{
		if( controlModes.isEmpty() )
			return;

		currControlMode.keyUp(keycode);

		if( controlModeChanged )
		{
			currControlMode.keyUp(keycode);
			controlModeChanged = false;
		}
	}

	public void keyTyped( char keycode )
	{
		if( controlModes.isEmpty() )
			return;

		currControlMode.keyTyped(keycode);

		if( controlModeChanged )
		{
			currControlMode.keyTyped(keycode);
			controlModeChanged = false;
		}
	}
	public boolean touchDown( float worldX, float worldY, float scale, Entity pickedObject, int button )
	{
		if( controlModes.isEmpty() )
			return false;
		boolean propagate = currControlMode.touchDown(worldX, worldY, scale, pickedObject, button);
		if(controlModeChanged)
		{
			propagate = currControlMode.touchDown(worldX, worldY, scale, pickedObject, button);
			controlModeChanged = false;
		}
		return propagate;
	}

	public boolean touchUp( float worldX, float worldY, float scale, Entity pickedObject, int button )
	{
		if( controlModes.isEmpty() )
			return false;

		boolean propagate = currControlMode.touchUp(worldX, worldY, scale, pickedObject, button);
		if(controlModeChanged)
		{
			propagate = currControlMode.touchUp(worldX, worldY, scale, pickedObject, button);
			controlModeChanged = false;
		}
		return propagate;
	}

	public void untouch()
	{
		if( !controlModes.isEmpty() )
			currControlMode.untouch();
	}

	public boolean drag( float worldX, float worldY, float scale, Entity pickedObject, int button)
	{
		if( !controlModes.isEmpty() )
			return currControlMode.drag(worldX, worldY, scale, pickedObject, button );

		return false;
	}

	public void render( IRenderer renderer )
	{
		if( !controlModes.isEmpty() )
			currControlMode.render(renderer);
	}

	public IControl control() {
		if( !controlModes.isEmpty() )
			return currControlMode.control();

		return null;
	}


}
