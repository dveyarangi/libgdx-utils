package game.util;

import java.util.Iterator;

import com.badlogic.gdx.utils.Pool;

public class ReiterableLinkedList<E> implements Iterable<E>, Iterator<E>
{

	public static class Element
	{
		Object object;
		Element nextElement;
		Element prevElement;
	}

	protected static Pool<Element> pool = new Pool<Element>() {
		@Override
		protected Element newObject()
		{
			return new Element();
		}
	};

	protected Element firstElement;
	protected Element lastElement;

	int size;

	private Element currElement;
	private Element prevElement;

	public void addLast( E e )
	{
		Element element = pool.obtain();
		element.object = e;
		if( lastElement == null )
		{
			firstElement = lastElement = element;
			element.nextElement = null;
			element.prevElement = null;
		}
		else
		{
			element.prevElement = lastElement;
			lastElement.nextElement = element;
			element.nextElement = null;
			lastElement = element;
		}

		size++;
	}

	private Element removeElement( Element element )
	{
		Element prevElement = element.prevElement;
		Element nextElement = element.nextElement;
		if( prevElement != null )
			prevElement.nextElement = nextElement;
		if( nextElement != null )
			nextElement.prevElement = prevElement;
		if( element == lastElement )
			lastElement = prevElement;
		if( element == firstElement )
			firstElement = nextElement;

		pool.free(element);

		size--;

		currElement = nextElement;

		return nextElement;
	}

	@Override
	public ReiterableLinkedList<E> iterator()
	{
		return this;
	}

	public void reset()
	{
		currElement = firstElement;
	}

	@Override
	public boolean hasNext()
	{
		return currElement != null;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public E next()
	{
		E object = (E) currElement.object;
		prevElement = currElement;
		currElement = currElement.nextElement;
		return object;
	}

	@Override
	public void remove()
	{
		removeElement(prevElement);
	}

	public void remove( E object )
	{
		for( Element curr = firstElement; curr != null; curr = curr.nextElement )
		{
			if( curr.object == object )
			{
				removeElement(curr);
				break;
			}
		}
	}

	public int size()
	{
		return size;
	}

	public boolean isEmpty()
	{
		return size == 0;
	}

	@SuppressWarnings( "unchecked" )
	public E getFirst()
	{
		return (E) firstElement.object;
	}

	public void clear()
	{
		// pool.clear();
		this.reset();
		while( this.hasNext() )
			this.remove();
		firstElement = lastElement = null;
		size = 0;
	}

}
