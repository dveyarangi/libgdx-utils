package game.world.saves;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class EntityProps
{
	@Getter HashMap <String, String> props = new HashMap <> ();


	public void put(String prop, int val) {
		props.put(prop, String.valueOf(val));
	}
	public void put(String prop, float val) {
		props.put(prop, String.valueOf(val));
	}
	public void put(String prop, String val) {
		props.put(prop, val);
	}
	public void put(String prop, Color color)
	{
		props.put(prop, color.toString());
	}
	public void put(String prop, boolean value)
	{
		this.props.put(prop, String.valueOf(value));
	}

	public int get(String prop)
	{
		if(!props.containsKey(prop))
			throw new IllegalArgumentException("Required property " + prop + " is missing.");
		return Integer.parseInt(props.get(prop));
	}
	public float getFloat(String prop)
	{
		if(!props.containsKey(prop))
			throw new IllegalArgumentException("Required property " + prop + " is missing.");
		return Float.parseFloat(props.get(prop));
	}



	public int get(String prop, int defval)
	{
		return props.containsKey(prop) ? Integer.parseInt(props.get(prop)) : defval;
	}

	public <E> E get(String prop, E defval)
	{
		return props.containsKey(prop) ? (E)props.get(prop) : defval;
	}

	public float get(String prop, float defval)
	{
		return props.containsKey(prop) ? Float.parseFloat(props.get(prop)) : defval;
	}
	public boolean get(String prop, boolean defval)
	{
		return props.containsKey(prop) ? Boolean.parseBoolean(props.get(prop)) : defval;
	}

	public String get(String prop, String defval)
	{
		return props.containsKey(prop) ? props.get(prop) : defval;
	}
	public boolean hasProp(String prop)
	{
		return props.containsKey(prop);
	}

}
