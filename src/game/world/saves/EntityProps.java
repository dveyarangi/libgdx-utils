package game.world.saves;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;


public class EntityProps
{
	HashMap <String,String> props;

	public EntityProps()
	{
		this(new HashMap <> ());
	}

	public EntityProps(HashMap<String, String> props)
	{
		this.props = props;
	}
	public void put(String prop, String val)
	{
		props.put(prop, val);
	}

	public void put(String prop, int val) {
		props.put(prop, String.valueOf(val));
	}
	public void put(String prop, float val) {
		props.put(prop, String.valueOf(val));
	}

	public void put(String prop, Color color)
	{
		props.put(prop, color.toString());
	}
	public void put(String prop, boolean value)
	{
		props.put(prop, String.valueOf(value));
	}

	public boolean containsKey(String prop) { return props.containsKey(prop); }

	public String get(String prop) { return props.get(prop); }
	public int getInt(String prop)
	{
		if(!containsKey(prop))
			throw new IllegalArgumentException("Required property " + prop + " is missing.");
		return Integer.parseInt(get(prop));
	}

	public float getFloat(String prop)
	{
		if(!containsKey(prop))
			throw new IllegalArgumentException("Required property " + prop + " is missing.");
		return Float.parseFloat(get(prop));
	}

	public float getFloat(String prop, float defval)
	{
		return containsKey(prop) ? Float.parseFloat(get(prop)) : defval;
	}


	public int get(String prop, int defval)
	{
		return containsKey(prop) ? Integer.parseInt(get(prop)) : defval;
	}

	public <E> E get(String prop, E defval)
	{
		return containsKey(prop) ? (E)get(prop) : defval;
	}

	public float get(String prop, float defval)
	{
		return containsKey(prop) ? Float.parseFloat(get(prop)) : defval;
	}
	public boolean get(String prop, boolean defval)
	{
		return containsKey(prop) ? Boolean.parseBoolean(get(prop)) : defval;
	}

	public String get(String prop, String defval)
	{
		return containsKey(prop) ? get(prop) : defval;
	}
	public boolean hasProp(String prop)
	{
		return containsKey(prop);
	}
	public Color getColor(String prop, Color color)
	{
		return containsKey(prop) ? Color.valueOf(get(prop)) : color;
	}

}
