package game.util.arrays;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilteringIterator <T> implements Iterator <T>
{
	Iterator<T> iterator;
	Filter<T> filter;
	
	T next;
	
	public FilteringIterator(Iterator<T> iterator, Filter <T> filter)
	{
		this.iterator = iterator;
		this.filter = filter;
	}
	@Override
	public boolean hasNext()
	{
		if(next != null) // if next is set, just return true 
			return true;
		
		if(!iterator.hasNext())
			return false;
		
		// otherwise find that next:
		while(!filter.accept(next = iterator.next())) ;
		
		return next != null;
	}

	@Override
	public T next()
	{
		if(!hasNext())
			throw new NoSuchElementException();
		T prevNext = next;
		next = null;
		return prevNext;
	}
	
}
