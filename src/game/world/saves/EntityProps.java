package game.world.saves;

import java.util.HashMap;

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

	public float get(String prop, float defval)
	{
		return props.containsKey(prop) ? Float.parseFloat(props.get(prop)) : defval;
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
