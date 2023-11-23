package game.systems.rendering;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ObjectIntMap;

import game.resources.ResourceFactory;
import game.systems.rendering.TileSpritesRendererDef.MeshDef;
import game.systems.spatial.ISpatialComponent;
import game.systems.tiles.ITile;
import game.systems.tiles.TileSystem;
import game.util.rendering.TileMultiMesh;
import game.util.rendering.TileMultiMesh.TileUpdate;


/**
 * Renders multiple quad sprites located on rectangular grid,
 * each grid tile containing one and only one sprite.
 *
 * @author fimar
 */
public class TileSpritesRenderer extends EntitySystem implements EntityListener, IRenderingContext
{
	//static { ComponentType.registerFor(IRenderingComponent.class, MeshRenderingComponent.class); }

	TileSystem tilemap;

	TileSpritesRendererDef rendererDef;

	TileSpritesGrid [] grids;

	ObjectIntMap<String> meshIndices = new ObjectIntMap<> ();

	Camera cam;

	public TileSpritesRenderer(TileSpritesRendererDef rendererDef)
	{
		this.rendererDef = rendererDef;
	}

	@Override
	public void init(ResourceFactory factory, IRenderer renderer)
	{
		this.cam = renderer.camera();
	}

	@Override
	public void addedToEngine( Engine engine )
	{
		super.addedToEngine(engine);
		engine.addEntityListener(this);

		// looking up for TileSystem to allow binding tile sprites to tile coordinates
		for(EntitySystem system : engine.getSystems())
			if(system instanceof TileSystem)
				this.tilemap = (TileSystem) system;

		//		ResourceFactory factory = level.getModules().getGameFactory();
		int meshNum = rendererDef.meshes.length;


		grids = new TileSpritesGrid[meshNum];


		//interp = Interpolation.getInstance(Interpolation.Type.BICUBIC, heightLowressArr);
		//float [][] heightmap = interp.resize(2*w+1, 2*h+1);
		VertexAttributes vattr = new VertexAttributes(
				new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
				VertexAttribute.TexCoords(0),
				VertexAttribute.Normal(),
				VertexAttribute.ColorUnpacked()
				);

		int verticesPerTile = 4;
		int trianglesPerTile = 2;

		for(int idx = 0; idx < meshNum; idx ++)
		{
			MeshDef meshDef = rendererDef.meshes[idx];
			int w = rendererDef.width*meshDef.unitsPerTile;
			int h = rendererDef.height*meshDef.unitsPerTile;
			int dx = meshDef.unitsPerTile/2;
			int dy = meshDef.unitsPerTile/2;


			TileMultiMesh mesh = new TileMultiMesh(w, h, verticesPerTile, trianglesPerTile, vattr);
			mesh.fillTiles((x, y, tileIdx, vertexBuffer, triangles) -> {


				int vidx = 0;
				for(int i = -1; i == 1; i += 2)
					for(int j = -1; j == 1; j += 2)
					{
						vertexBuffer.set(vidx++, x+i*dx);
						vertexBuffer.set(vidx++, y+j*dy);
						vertexBuffer.set(vidx++, -200);//heightmap[xx][yy];
						vertexBuffer.set(vidx++, (i+1)/2);
						vertexBuffer.set(vidx++, (j+1)/2);
						vertexBuffer.set(vidx++, 0);
						vertexBuffer.set(vidx++, 0);
						vertexBuffer.set(vidx++, 1);
		
						// color
						vertexBuffer.set(vidx++, 0);
						vertexBuffer.set(vidx++, 0);
						vertexBuffer.set(vidx++, 0);
						vertexBuffer.set(vidx++, 1);
					}
				int c00 = verticesPerTile*tileIdx+0;
				int c10 = verticesPerTile*tileIdx+1;
				int c01 = verticesPerTile*tileIdx+2;
				int c11 = verticesPerTile*tileIdx+3;

				triangles.addTriangle(c00, c01, c10);
				triangles.addTriangle(c10, c01, c11);
			});


			ShaderProgram shader = ResourceFactory.getShader(meshDef.shaderName);
			TextureAtlas atlas = ResourceFactory.getTextureAtlas(meshDef.textureName);
			Texture texture = atlas.getTextures().iterator().next();
			meshIndices.put(meshDef.textureName, idx);

			boolean isOn = true;

			float opacity = 1;

			TileSpritesGrid grid = new TileSpritesGrid(mesh, texture, shader, isOn, opacity);

			grids[idx] = grid;

		}
	}


	/**
	 *
	 * @param ex entity x
	 * @param ey entity y
	 * @param ez entity rendering priority
	 * @param opacity
	 * @param buffer output
	 * @param sprite
	 */
	public void updateTileGeometry(float ex, float ey, float ez, float opacity,  float [] buffer, TileSpriteComponent sprite)
	{
		float x = ex - sprite.dx;
		float y = ey - sprite.dy;
		float z = ez;
		//z = -ty;
		int vidx = 0;
		buffer[vidx++] = x;
		buffer[vidx++] = y+sprite.sizeCoef*sprite.height;
		buffer[vidx++] = z;
		buffer[vidx++] = opacity <= 0 ? 0 : sprite.region.getU();
		buffer[vidx++] = opacity <= 0 ? 0 : sprite.region.getV();
		buffer[vidx++] = 0;
		buffer[vidx++] = 0;
		buffer[vidx++] = 1;

		// color
		buffer[vidx++] = sprite.color.r;
		buffer[vidx++] = sprite.color.g;
		buffer[vidx++] = sprite.color.b;
		buffer[vidx++] = opacity;

		buffer[vidx++] = x;
		buffer[vidx++] = y;
		buffer[vidx++] = z;
		buffer[vidx++] = opacity <= 0 ? 0 : sprite.region.getU();
		buffer[vidx++] = opacity <= 0 ? 0 : sprite.region.getV2();
		buffer[vidx++] = 0;
		buffer[vidx++] = 0;
		buffer[vidx++] = 1;

		// color
		buffer[vidx++] = sprite.color.r;
		buffer[vidx++] = sprite.color.g;
		buffer[vidx++] = sprite.color.b;
		buffer[vidx++] = opacity;

		buffer[vidx++] = x+sprite.sizeCoef*sprite.width;
		buffer[vidx++] = y+sprite.sizeCoef*sprite.height;
		buffer[vidx++] = z;
		buffer[vidx++] = opacity <= 0 ? 0 : sprite.region.getU2();
		buffer[vidx++] = opacity <= 0 ? 0 : sprite.region.getV();
		buffer[vidx++] = 0;
		buffer[vidx++] = 0;
		buffer[vidx++] = 1;

		// color
		buffer[vidx++] = sprite.color.r;
		buffer[vidx++] = sprite.color.g;
		buffer[vidx++] = sprite.color.b;
		buffer[vidx++] = opacity;

		buffer[vidx++] = x+sprite.sizeCoef*sprite.width;
		buffer[vidx++] = y;
		buffer[vidx++] = z;
		buffer[vidx++] = opacity <= 0 ? 0 : sprite.region.getU2();
		buffer[vidx++] = opacity <= 0 ? 0 : sprite.region.getV2();
		buffer[vidx++] = 0;
		buffer[vidx++] = 0;
		buffer[vidx++] = 1;

		// color
		buffer[vidx++] = sprite.color.r;
		buffer[vidx++] = sprite.color.g;
		buffer[vidx++] = sprite.color.b;
		buffer[vidx++] = opacity;
	}


	@Override
	public void entityAdded(Entity entity)
	{
		ISpriteComponent tileSprite = entity.getComponent( ISpriteComponent.class );
		ISpatialComponent spatial = entity.getComponent(ISpatialComponent.class);
		if( tileSprite instanceof TileSpriteComponent)
		{
			updateSprite((TileSpriteComponent)tileSprite, spatial);
		}
		else
		if( tileSprite instanceof TileMultiSpriteComponent)
		{
			TileMultiSpriteComponent tileMultiSprite = (TileMultiSpriteComponent)tileSprite;
			for(int i = 0; i < tileMultiSprite.sprites.size; i ++)
			{
				TileSpriteComponent sprite =tileMultiSprite.sprites.get(i);
				updateSprite( sprite, spatial );
			}
		}
	}


	private void updateSprite(TileSpriteComponent tileSprite, ISpatialComponent spatial)
	{

		int meshIndex = meshIndices.get(tileSprite.atlasName, -1);
		if( meshIndex < 0)
			throw new IllegalArgumentException("No mesh for defined for atlas " + tileSprite.atlasName);

		TileMultiMesh multimesh = grids[meshIndex].mesh;
		MeshDef meshDef = rendererDef.meshes[meshIndex];

		float x = spatial.x();
		float y = spatial.y();
		ITile tile = tilemap.getTileAt(x, y);

		tileSprite.update(x, y, spatial.inv(), tile);
		//if( tile.getX() == 140 && tile.getY() == 135)
		//	System.out.print("");
		update.setSprite(tileSprite, grids[meshIndex].opacity);
		multimesh.updateTile(meshDef.getUnitsPerTile()*tile.getX(), 
							 meshDef.getUnitsPerTile()*tile.getY(), update);
	}


	private class TileSpriteUpdate implements TileUpdate
	{

		private TileSpriteComponent tileSprite;
		private float opacity;
		public void setSprite(TileSpriteComponent tileSprite, float opacity)
		{
			this.tileSprite = tileSprite;
			this.opacity = opacity;
		}

		@Override
		public void updateVertexBuffer(int tx, int ty, float[] vertexBufferUpdate)
		{
			updateTileGeometry(tileSprite.x, tileSprite.y, tileSprite.priority, opacity, vertexBufferUpdate, tileSprite);
		}

	}

	TileSpriteUpdate update = new TileSpriteUpdate();


	private class TileSpriteRemove implements TileUpdate
	{
		private TileSpriteComponent tileSprite;
		void setSprite(TileSpriteComponent tileSprite)
		{
			this.tileSprite = tileSprite;
		}
		@Override
		public void updateVertexBuffer(int tx, int ty, float[] vertexBufferUpdate)
		{
			updateTileGeometry(tx,ty, -200, 0, vertexBufferUpdate, tileSprite);
		}
	}

	TileSpriteRemove remove = new TileSpriteRemove();

	void entityUpdated(TileSpriteComponent tileSprite)
	{

		if( tileSprite == null)
			return; // ignore

		if( tilemap.getTileAt(tileSprite.tx, tileSprite.ty) == null )
			return; // ignore TODO: this is caused by early calls to entityUpdated
		// from the TileSpriteCompo


		int meshIndex = meshIndices.get(tileSprite.atlasName, 0);
		//System.out.println("updated: " + meshIndex + " " + tileSprite.atlasName);
		TileMultiMesh multimesh = grids[meshIndex].mesh;
		float opacity = grids[meshIndex].opacity;
		update.setSprite(tileSprite, opacity);
		multimesh.updateTile(tileSprite.tx, tileSprite.ty, update);

	}
	@Override
	public void update( float deltaTime )
	{
	}

	@Override
	public void entityRemoved(Entity entity)
	{
		ISpriteComponent tileSprite = entity.getComponent( ISpriteComponent.class );
		ISpatialComponent spatial = entity.getComponent(ISpatialComponent.class);
		if( tileSprite instanceof TileSpriteComponent)
		{
			removeSprite((TileSpriteComponent)tileSprite, spatial);
		}
		else
		if( tileSprite instanceof TileMultiSpriteComponent)
		{
			TileMultiSpriteComponent tileMultiSprite = (TileMultiSpriteComponent)tileSprite;
			for(int i = 0; i < tileMultiSprite.sprites.size; i ++)
			{
				TileSpriteComponent sprite =tileMultiSprite.sprites.get(i);
				removeSprite( sprite, spatial );
			}
		}		
	}


	public void removeSprite(TileSpriteComponent tileSprite, ISpatialComponent spatial)
	{
		int meshIndex = meshIndices.get(tileSprite.atlasName, 0);
		//System.out.println("removed: " + meshIndex + " " + tileSprite.atlasName);
		TileMultiMesh multimesh = grids[meshIndex].mesh;
		ITile tile = tilemap.getTileAt(spatial.x(), spatial.y());
		remove.setSprite(tileSprite);
		multimesh.updateTile(tile.getX(), tile.getY(), remove);
	}

	/*public void render( Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime )
	{
		//TextureRenderingContext ctx = (TextureRenderingContext)context;
		//ctx.getTexture().bind();
		meshTexture.bind();

		shader.begin();

		shader.setUniformMatrix("u_projTrans", cam.combined);
		//		Color color1 = ceilingWallMesh.color1;
		//		Color color2 = Color.BLACK;
		//		bicoloringShader.setUniformf("u_maskingColor", color1.r, color1.g, color1.b, 0.2f);
		//		bicoloringShader.setUniformf("v_maskingColor", color2.r, color2.g, color2.b, 1f);

		for(int idx = 0; idx < meshes.size(); idx ++)
			meshes.get(idx).render( shader, GL20.GL_TRIANGLES );

		shader.end();

		//return false;
	}

	//float [] buffer = new float[256*256];

	/*public void render(ModelBatch batch, Environment environment )
	{

		batch.begin(cam);

		batch.render(terrainModel, environment);
		batch.end();

	}*/

	@Override
	public void removedFromEngine( Engine engine )
	{
		engine.removeEntityListener(this);
		super.removedFromEngine(engine);
	}



	@Override public int id() { return EntityRenderingSystem.POST_RENDERING; }




	@Override
	public void begin()
	{
		//TextureRenderingContext ctx = (TextureRenderingContext)context;
		//ctx.getTexture().bind();
		for(int tidx = 0; tidx < grids.length; tidx ++)
		{
			TileSpritesGrid grid = grids[tidx];
			if( ! grid.isOn )
				continue;

			MeshDef meshDef = rendererDef.meshes[tidx];
			if( meshDef.isBlended() || grid.opacity < 1)
			{
				Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
				Gdx.gl.glEnable(GL20.GL_BLEND);
			}
			else
			{
				Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
				Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
				Gdx.gl.glDisable(GL20.GL_BLEND);
			}


			grid.texture.bind();

			ShaderProgram shader = grid.shader;
			shader.bind();


			if( meshDef.context != null)
				meshDef.context.updateShader(shader);


			shader.setUniformMatrix("u_projTrans", cam.combined);
			//		Color color1 = ceilingWallMesh.color1;
			//		Color color2 = Color.BLACK;
			//		bicoloringShader.setUniformf("u_maskingColor", color1.r, color1.g, color1.b, 0.2f);
			//		bicoloringShader.setUniformf("v_maskingColor", color2.r, color2.g, color2.b, 1f);
			TileMultiMesh multimesh = grid.mesh;

			for(int midx = 0; midx < multimesh.getMeshes().size(); midx ++)
				multimesh.getMeshes().get(midx).render( shader, GL20.GL_TRIANGLES );

		}
	}


	@Override
	public void end()
	{
		// TODO Auto-generated method stub

	}


	@Override
	public boolean isEntityless() { return true; }

	public void toggleMeshVisibility(String meshName)
	{
		int idx = meshIndices.get(meshName, -1);
		assert idx >= 0;

		grids[idx].isOn = !grids[idx].isOn;
	}
	public void setOpacity(String meshName, float opacity)
	{
		int idx = meshIndices.get(meshName, -1);
		assert idx >= 0;
		grids[idx].opacity = opacity;
		for(int x = 0; x < rendererDef.width; x ++)
			for(int y = 0; y < rendererDef.height; y ++)
				grids[idx].mesh.updateTile(x, y, new TileUpdate() {

					@Override
					public void updateVertexBuffer(int x, int y, float[] vertexBufferUpdate)
					{
						vertexBufferUpdate[8] = opacity;
					}

				});

	}




	@Override
	public String toString() { return "tile sprites"; }

/*	@Override
	public void entityChanged(Entity entity)
	{
		TileSpriteComponent tileSprite = entity.getComponent( TileSpriteComponent.class );
		ISpatialComponent spatial = entity.getComponent(ISpatialComponent.class);
		if( tileSprite != null)
		{
			updateSprite(tileSprite, spatial);
		}

		TileMultiSpriteComponent tileMultiSprite = entity.getComponent( TileMultiSpriteComponent.class );
		if( tileMultiSprite != null )
		{
			for(int i = 0; i < tileMultiSprite.sprites.size; i ++)
			{
				TileSpriteComponent sprite = tileMultiSprite.sprites.get(i);
				updateSprite( sprite, spatial );
			}
		}	
	}*/
}
