package game.util.rendering;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;

import lombok.Getter;
import yarangi.arrays.FloatArrayWindow;
import yarangi.arrays.ShortArrayWindow;

/**
 * Helps with creation of large tile meshes, automatically instantiating required number of 
 * mesh parts and providing convenience filling vertex and index buffers
 * 
 * @author Fima
 */
public class TileMultiMesh 
{
	private int w, h;
	private int verticesPerTile;
	private int indicesPerTile;
	private VertexAttributes vattr;
	/**
	 * Vertex component number
	 */
	private int vertexCompCount = 0;
	
	private int maxTilesPerMesh;

	private int componentsPerTile;

	@Getter List <Mesh> meshes = new ArrayList <> ();

	/**
	 * 
	 * @param w tilemap width
	 * @param h timemap height
	 * @param verticesPerTile number of vertices used to create one tile
	 * @param trianglesPerTile number of triangles used to create one tile
	 * @param attrs mesh vertex attributes
	 */
	public TileMultiMesh(int w, int h, int verticesPerTile, int trianglesPerTile, VertexAttributes attrs)
	{
		this.w = w;
		this.h = h;
		this.verticesPerTile = verticesPerTile;
		this.indicesPerTile = trianglesPerTile * 3;
		
		this.vattr = attrs;
		
		for(int idx = 0; idx < vattr.size(); idx ++)
			this.vertexCompCount += vattr.get(idx).numComponents;
		
		// vertex component in one tile (sum of components of all tile vertices)
		this.componentsPerTile = verticesPerTile*vertexCompCount;
	
		// defining max tiles for mesh:
		this.maxTilesPerMesh = 256*256/verticesPerTile;
		

	}
	
	
	public void fillTiles(TileFill fill)
	{
		int remainingTiles = w * h;
		int tilesPerMesh = Math.min(maxTilesPerMesh, w*h) / 2;

		/////////////////////////////
		// preallocating buffers for first mesh
		// (next meshes are allocated inside tile loop)
		int vertexBufferSize = tilesPerMesh * componentsPerTile;
		float [] vertexBuffer = new float[vertexBufferSize];
		
		int indexBufferSize = tilesPerMesh * indicesPerTile;
		short [] indexBuffer = new short [ indexBufferSize ];
		
		int tileIdx = 0; // tile index
		int iidx = 0; // current index buffer pointer
		int vidx = 0; // current  vertex buffer pointer
		
		/////////////////////////////
		// iterating over tiles
		for(int y = 0; y < h; y ++)
			for(int x = 0; x < w; x ++)
			{
				/////////////////////////////
				// fill current tile
				FloatArrayWindow vertexBufferView = new FloatArrayWindow(vertexBuffer, vidx, componentsPerTile);
				ShortArrayWindow indexBufferView = new ShortArrayWindow(indexBuffer, iidx, indicesPerTile);
				
				fill.fillTile(x, y, tileIdx, vertexBufferView, indexBufferView);
				
				tileIdx ++;
				vidx += componentsPerTile;
				iidx += indicesPerTile;
				
				/////////////////////////////
				// instantiating and swapping mesh if full or last
				if(vidx >= vertexBufferSize || (y == h-1 && x == w-1))
				{
					// create mesh and store it
					Mesh mesh = new Mesh( true, tilesPerMesh*verticesPerTile, indexBufferSize, vattr);
					mesh.setIndices(indexBuffer);
					mesh.setVertices(vertexBuffer);

					meshes.add(mesh);

				
					remainingTiles -= maxTilesPerMesh;
					
					// create components for next mesh, if have remaining tiles: 
					if( remainingTiles > 0)
					{
						tilesPerMesh = Math.min(maxTilesPerMesh, remainingTiles);
						vertexBufferSize = tilesPerMesh*componentsPerTile;

						vertexBuffer = new float[vertexBufferSize]; 
						
						indexBufferSize = tilesPerMesh * indicesPerTile;
						indexBuffer = new short [ indexBufferSize ];
					}
					
					// reset buffer indices:
					vidx = 0;
					iidx = 0;
					tileIdx = 0;
				}

			}
	}

	public static interface TileFill
	{
		public void fillTile(int x, int y, int tileIdx, FloatArrayWindow vertexBuffer, ShortArrayWindow indexBuffer);
	}
}
