package game.util.textures;

import game.util.textures.TileQuery.Treshold;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class TileNeighbourhood
{
	boolean n;
	boolean ne;
	boolean e;
	boolean se;
	boolean s;
	boolean sw;
	boolean w;
	boolean nw;
	
	
	public static TileNeighbourhood at(int x, int y, float [][] map, float treshold)
	{
		return at(x, y, new Treshold(map, treshold));
	}
	
	public static TileNeighbourhood at(int x, int y, TileQuery query)
	{
		return new TileNeighbourhood(
				query.is(x, y+1),
				query.is(x+1, y+1),
				query.is(x+1, y),
				query.is(x+1, y-1),
				query.is(x, y-1),
				query.is(x-1, y-1),
				query.is(x-1, y),
				query.is(x-1, y+1));
	}
	
	public boolean anyE() { return e || se || ne; }
	public boolean anyW() { return w || sw || nw; }
	public boolean anyS() { return s || se || sw; }
	public boolean anyN() { return n || ne || nw; }
	
	public boolean at(int x, int y) {
		return x < 0 ? y < 0 ? sw :
			           y == 0 ? w :
			           y > 0 ? nw : false
			 : x == 0 ? y < 0 ? s :
				        y > 0 ? n : false
		     : x > 0 ? y < 0 ? se :
		    	       y == 0 ? e :
		    	       y > 0 ? ne : false : false;
	}
}


