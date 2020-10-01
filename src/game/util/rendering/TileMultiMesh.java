package game.util.rendering;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

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
	private float [] vertexBufferUpdate;

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
		
		this.vertexBufferUpdate = new float [componentsPerTile];

	}
	
	
	public void fillTiles(TileFill fill)
	{
		int remainingTiles = w * h;
		int tilesPerMesh = Math.min(maxTilesPerMesh, w*h);

		/////////////////////////////
		// preallocating buffers for first mesh
		// (next meshes are allocated inside tile loop)
		int vertexBufferSize = tilesPerMesh * componentsPerTile;
		float [] vertexBuffer = new float[vertexBufferSize];
		
		int indexBufferSize = tilesPerMesh * indicesPerTile;
		short [] indexBuffer = new short [ indexBufferSize ];
		
		/////////////////////////////
		// iterating over tiles
		for(int y = 0; y < h; y ++)
			for(int x = 0; x < w; x ++)
			{
				int globalTileIdx = y * w + x;
				// tile index
				int tileIdx = globalTileIdx % maxTilesPerMesh;
				// current index buffer pointer
				int vidx = tileIdx * componentsPerTile;
				// current  vertex buffer pointer
				int iidx = tileIdx * indicesPerTile;
				
				/////////////////////////////
				// fill current tile
				FloatArrayWindow vertexBufferView = new FloatArrayWindow(vertexBuffer, vidx, componentsPerTile);
				ShortArrayWindow indexBufferView = new ShortArrayWindow(indexBuffer, iidx, indicesPerTile);
				TriangleIndicesWindow trianglesView = new TriangleIndicesWindow(indexBufferView);
				fill.fillTile(x, y, tileIdx, vertexBufferView, trianglesView);	
				
				
				/////////////////////////////
				// instantiating and swapping mesh if full or last
				if((tileIdx+1) * componentsPerTile >= vertexBufferSize || (y == h-1 && x == w-1))
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
	

	public void updateTile(int x, int y, TileUpdate update)
	{
		int globalTileIdx = y * w + x;
		
		int meshIdx = globalTileIdx / maxTilesPerMesh;
		// tile index
		int tileIdx = globalTileIdx % maxTilesPerMesh;
		// current index buffer pointer
		int vidx = tileIdx * componentsPerTile;
		
		Mesh mesh = meshes.get(meshIdx);
		
		/////////////////////////////
		// update tile
		
		update.updateVertexBuffer(x, y, vertexBufferUpdate);

		mesh.updateVertices(vidx, vertexBufferUpdate);

	}

	public static interface TileFill
	{
		public void fillTile(int x, int y, int tileIdx, FloatArrayWindow vertexBuffer, TriangleIndicesWindow triangles);
	}
	
	public static interface TileUpdate
	{
		public void updateVertexBuffer(int x, int y, float [] vertexBufferUpdate);
	}
	
	public static class VertexWindow
	{
		FloatArrayWindow vertexBuffer;
	}
	
	public static class TriangleIndicesWindow 
	{
		ShortArrayWindow indexBuffer;
		public TriangleIndicesWindow(ShortArrayWindow indexBuffer) { this.indexBuffer = indexBuffer; }
		int iidx = 0;
		public void addTriangle(int i1, int i2, int i3)
		{
			indexBuffer.set(iidx++, (short)i1);
			indexBuffer.set(iidx++, (short)i2);
			indexBuffer.set(iidx++, (short)i3);
		}
	}
	
	public Model buildModel(String modelName, Material material)
	{
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		
		int meshIdx = 0;
		for(Mesh mesh : getMeshes())
		{
			MeshPart part = new MeshPart(modelName + "_" + (meshIdx++), mesh, 0, mesh.getNumIndices(), GL20.GL_TRIANGLES);
			modelBuilder.part(part, material);
		}
		
		Model model = modelBuilder.end();
		
		return model;
	}
}
