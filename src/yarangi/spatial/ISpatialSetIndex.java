package yarangi.spatial;

import java.util.Set;

public interface ISpatialSetIndex<O> extends ISpatialIndex<O>
{
	
	public Set <O> keySet();

}