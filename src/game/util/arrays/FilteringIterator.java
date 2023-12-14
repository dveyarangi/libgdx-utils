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
		
		// otherwise find that next:
		while(iterator.hasNext() && !filter.accept(next = iterator.next())) ;
		
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
