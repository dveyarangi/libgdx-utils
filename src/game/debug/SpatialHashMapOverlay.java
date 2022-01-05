package game.debug;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ObjectSet;

import game.systems.rendering.IRenderer;
import yarangi.spatial.AABB;
import yarangi.spatial.ISpatialObject;
import yarangi.spatial.SpatialHashMap;


/**
 * Renders entity index
 *
 * @author dveyarangi
 *
 */
public class SpatialHashMapOverlay <O extends ISpatialObject> extends WorldOverlay
{

	private final SpatialHashMap <O> map;

	public SpatialHashMapOverlay(final SpatialHashMap <O> map)
	{
		this.map = map;
	}

	public void draw(final IRenderer rend)
	{
		ShapeRenderer renderer = rend.shaper();
		GL20 gl = Gdx.gl;
		gl.glEnable(GL20.GL_BLEND);

		int cellX, cellY;
		float cellsize = (float)map.getCellSize();
		float halfCellSize = cellsize / 2.f;
		
		Camera camera = cameraProvider.getCamera();


		// lower left screen corner in world coordinates
		float screenMinX = camera.position.x - camera.viewportWidth / 2 * cameraProvider.zoom();
		float screenMinY = camera.position.y - camera.viewportHeight / 2 * cameraProvider.zoom();
		// higher right screen corner in world coordinates
		float screenMaxX = camera.position.x + camera.viewportWidth / 2 * cameraProvider.zoom();
		float screenMaxY = camera.position.y + camera.viewportHeight / 2 * cameraProvider.zoom();
		float minx = -map.getWidth()/2-halfCellSize;
		float maxx = map.getWidth()/2-halfCellSize;
		float miny = -map.getHeight()/2-halfCellSize;
		float maxy = map.getHeight()/2-halfCellSize;
		renderer.setColor(0f, 0f, 0.4f, 0.5f);
		renderer.begin( ShapeType.Line );
		for(float y = miny; y <= maxy; y += map.getCellSize())
		{
			renderer.line( minx, y, maxx, y);
		}

		for(float x = minx; x <= maxx; x += map.getCellSize())
		{
			renderer.line( x, miny,x, maxy);

		}

		renderer.end();

		ObjectSet <O> bucket = null;
		for(float y = miny; y <= maxy; y += map.getCellSize())
		{
			cellY = map.toGridIndex( y );
			for(float x = minx; x < maxx; x += map.getCellSize())
			{

				cellX = map.toGridIndex( x );
				
				bucket = map.getBucket(cellX, cellY);

				if(bucket != null && bucket.size != 0)
				{
					boolean isReal = false;
					for(ISpatialObject o : bucket)
					{
						if(o.getArea().overlaps( AABB.createFromEdges(x, y, x+cellsize, y+cellsize, 0) ))
						{
							isReal = true;
							break;
						}
					}
					if(isReal)
					{
						renderer.setColor(0.8f, 0.6f, 0.8f, 0.2f);
						renderer.begin( ShapeType.Filled );
						renderer.rect( x, y, cellsize, cellsize);
						renderer.end();
					}
					else
					{
						renderer.setColor(0.1f, 0.5f, 0.1f, 0.2f);
						renderer.begin( ShapeType.Filled );
						renderer.rect( x, y, cellsize, cellsize);
						renderer.end();
					}
				}
			}
		}
	}


}
