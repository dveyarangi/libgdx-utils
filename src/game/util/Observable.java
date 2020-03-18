package game.util;

import java.util.ArrayList;
import java.util.List;




public class Observable <L>
{
	
	public static interface Dispatcher <L>
	{
		public void dispatch(L l, Object ... args);
	}

	
	private List <L> listeners = new ArrayList <> ();
	private Dispatcher<L> method;
	
	public Observable(Dispatcher <L> method)
	{
		this.method = method;
	}
	
	public void addListener(L l) { listeners.add(l); }
	public void removeListener(L l ) { listeners.remove(l); }
	
	public void fire(Object ... args)
	{
		for(int idx = 0; idx < listeners.size(); idx ++)
			method.dispatch(listeners.get(idx), args);
	}
}
