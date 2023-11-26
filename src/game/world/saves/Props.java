package game.world.saves;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Pool;

import lombok.Getter;


public class Props
{
	@Getter private OrderedMap<String,String> props;
	
	private static Pool<Props> pool = new Pool<Props> () {
		@Override protected Props newObject() { return new Props(); }
	};
	
	public static Props get() { return pool.obtain(); }
	
	public void free() { pool.free(this); }

	private Props()
	{
		this(new OrderedMap <> ());
	}

	public Props(OrderedMap<String, String> props)
	{
		this.props = props;
	}
	public Props put(String prop, String val)
	{
		props.put(prop, val);
		return this;
	}

	public Props put(String prop, int val) {
		props.put(prop, String.valueOf(val));
		return this;
	}
	public Props put(String prop, float val) {
		props.put(prop, String.valueOf(val));
		return this;
	}

	public Props put(String prop, Color color)
	{
		props.put(prop, color.toString());
		return this;
	}
	public Props put(String prop, boolean value)
	{
		props.put(prop, String.valueOf(value));
		return this;
	}

	public boolean containsKey(String prop) { return props.containsKey(prop); }

	public String get(String prop) { return props.get(prop); }
	public int getInt(String prop)
	{
		if(!containsKey(prop))
			throw new IllegalArgumentException("Required property " + prop + " is missing.");
		return Integer.parseInt(get(prop));
	}
	public int getInt(String prop, int defval)
	{
		return containsKey(prop) ? Integer.parseInt(get(prop)) : defval;
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

	@Override
	public String toString()
	{
		return props.toString();
	}
}
