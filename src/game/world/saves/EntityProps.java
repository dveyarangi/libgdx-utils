package game.world.saves;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class EntityProps extends HashMap <String,String>
{

	private static final long serialVersionUID = -2612984146255094863L;

	public EntityProps(HashMap<String, String> props)
	{
		super(props);
	}
	public void put(String prop, int val) {
		put(prop, String.valueOf(val));
	}
	public void put(String prop, float val) {
		put(prop, String.valueOf(val));
	}

	public void put(String prop, Color color)
	{
		put(prop, color.toString());
	}
	public void put(String prop, boolean value)
	{
		this.put(prop, String.valueOf(value));
	}

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

}
