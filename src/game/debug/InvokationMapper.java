package game.debug;

import java.util.HashMap;
import java.util.Map;

/**
 * Helps to determine object allocation cause, TODO: use by either javaagent or
 * annotation
 */
public class InvokationMapper

{

	protected Map<String, Integer> rootsCount = new HashMap<String, Integer>();

	public InvokationMapper()
	{

	}

	/**
	 * Marks object allocation event.
	 * 
	 * @param depth
	 *            - depth of method invoker to trace
	 */
	public boolean record( final int depth )
	{
		String root = new Exception().getStackTrace()[depth].toString();
		if( rootsCount.containsKey(root) )
		{
			int pval = rootsCount.get(root);
			rootsCount.put(root, pval + 1);
		} else
		{
			rootsCount.put(root, 1);
		}

		return true;
	}

	/**
	 * Prints allocation distribution.
	 */
	public void print()
	{
		for( Map.Entry<String, Integer> entry : rootsCount.entrySet() )
		{
			System.out.println(entry.getKey() + " >>> " + entry.getValue());

		}
	}

	public String getInvoker()
	{
		String invoker = new Exception().getStackTrace()[2].getClassName();
		return invoker;
	}
}
