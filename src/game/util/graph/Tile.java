package game.util.graph;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Polygon;

public class Tile
{
	public final Polygon polygon;

	public final Map <String, Object> attributes = new HashMap <> ();

	public static final String ATT_TILE_AREA = "tile-area";

	public Tile( Polygon polygon)
	{
		this.polygon = polygon;

		attributes.put( ATT_TILE_AREA, polygon.area() );
	}

	public Object getAttribute( String attName )
	{
		return attributes.get(attName);
	}

	public void setAttribute( String attName, Object value )
	{
		attributes.put(attName, value);
	}
}
