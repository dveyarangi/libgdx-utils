package game.util;

import java.util.Iterator;

public class CyclicBuffer<E> implements Iterable<E>
{
	private Object[] buffer;

	private int idx;

	private CyclicSingletonIterator singletonIterator;

	private class CyclicSingletonIterator implements Iterator<E>
	{

		private int iidx;

		@Override
		public boolean hasNext()
		{
			return iidx < buffer.length;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public E next()
		{
			if( iidx >= buffer.length )
				throw new IllegalStateException("No more elements in buffer");
			return (E) buffer[iidx++];
		}

		public void reset()
		{
			iidx = 0;// (idx+1)%buffer.length;
		}
	}

	public CyclicBuffer( int size )
	{
		this.buffer = new Object[size];
		this.idx = 0;

		this.singletonIterator = new CyclicSingletonIterator();
	}

	public void add( E object )
	{
		buffer[idx++] = object;
		if( idx >= buffer.length )
			idx = 0;
	}

	@SuppressWarnings( "unchecked" )
	public E get( int oidx )
	{
		return (E) buffer[oidx];
	}

	public void clear()
	{
		for( idx = 0; idx < buffer.length; idx++ )
		{
			buffer[idx] = null;
		}

		idx = 0;
	}

	@Override
	public Iterator<E> iterator()
	{
		singletonIterator.reset();
		return singletonIterator;
	}
}
