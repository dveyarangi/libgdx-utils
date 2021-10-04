package yarangi.spatial;

import java.util.Collection;

public interface IGridListener <K>
{
//	public void tilesModified(final Collection <K> tiles);
	public void tilesModified(K tile);
	
}
