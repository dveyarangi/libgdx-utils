package yarangi.spatial;

import java.util.List;

import yarangi.Zen;
import yarangi.math.FastMath;
import yarangi.math.IVector2D;
import yarangi.math.Vector2D;

public class CircleArea implements Area
{
	
	private final Vector2D center;
	
	private double radius;
	
	private int passId;
	
	
	public CircleArea(double cx, double cy, double radius) 
	{
		this.center = Vector2D.R(cx, cy);
		this.radius = radius;
	}

	@Override
	public double getOrientation() { return 0;  }

	@Override
	public void setOrientation(double a) { }

	@Override
	public IVector2D getAnchor() { return center; }

	@Override
	public void translate(double dx, double dy) { center.add( dx, dy ); }
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void move(double x, double y) {
		center.setxy( x, y );
	}
	
	@Override
	public void fitTo(double radius) { this.radius = radius; }

	@Override
	public double getMaxRadius() { return radius; }

	@Override
	public void iterate(int cellsize, IChunkConsumer consumer)
	{
		// TODO: optimize for small radius/cellsize
		double radiusSquare = radius * radius;
		double sx = FastMath.toGrid( center.x(), cellsize );
		double sy = FastMath.toGrid( center.y(), cellsize );
		
		double cd, ce;
		for(double d = 0; d <= radius; d += cellsize)
		{
			cd = FastMath.toGrid( d, cellsize );
			ce = FastMath.toGrid(Math.sqrt( radiusSquare - d*d ), cellsize);
			if(ce < cd)
				break;
			
			consumer.consume( new CellChunk(this, sx+ce, sy+cd, cellsize));
			consumer.consume( new CellChunk(this, sx+cd, sy+ce, cellsize));
			consumer.consume( new CellChunk(this, sx+ce, sy-cd, cellsize));
			consumer.consume( new CellChunk(this, sx+cd, sy-ce, cellsize));
			consumer.consume( new CellChunk(this, sx-ce, sy-cd, cellsize));
			consumer.consume( new CellChunk(this, sx-cd, sy-ce, cellsize));
			consumer.consume( new CellChunk(this, sx-ce, sy+cd, cellsize));
			consumer.consume( new CellChunk(this, sx-cd, sy+ce, cellsize));
		}
	}

	@Override
	public List<IVector2D> getDarkEdge(Vector2D from)
	{
		return null;
	}

	@Override
	public Area clone()
	{
		return new CircleArea( center.x(), center.y(), radius );
	}

	@Override
	public int getPassId() { return passId; }

	@Override
	public void setPassId(int id) {	this.passId = id; }

	@Override
	public boolean overlaps(AABB area)
	{
		return Zen.notSupported( );
	}

	@Override
	public boolean contains(double x, double y)
	{
		return (center.x()-x)*(center.x()-x) + (center.y()-y)*(center.y()-y) < radius*radius;
	}
}
