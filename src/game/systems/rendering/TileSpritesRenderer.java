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
import game.systems.IComponentDef;
import game.systems.rendering.TileSpritesRendererDef.MeshDef;
import game.util.rendering.TileMultiMesh;
import game.util.rendering.TileMultiMesh.TileUpdate;
import game.world.Level;

public class TileSpritesRenderer extends EntitySystem implements EntityListener, IRenderingComponent
{
	//static { ComponentType.registerFor(IRenderingComponent.class, MeshRenderingComponent.class); }

	TileSpritesRendererDef rendererDef;
	Texture [] textures;

	ShaderProgram [] shaders;

	Camera cam;
	
	TileMultiMesh [] meshes;
	ObjectIntMap<String> meshIndices = new ObjectIntMap<> ();

	public TileSpritesRenderer()
	{
	}
	
	@Override
	public void init(Entity entity, IComponentDef<?> def, Level level)
	{
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void addedToEngine( Engine engine )
	{
		super.addedToEngine(engine);
		engine.addEntityListener(this);
//		ResourceFactory factory = level.getModules().getGameFactory();
		int meshNum = rendererDef.meshes.length;
		
		textures = new Texture[meshNum];
		shaders = new ShaderProgram[meshNum];
		meshes = new TileMultiMesh[meshNum];
		for(int idx = 0; idx < meshNum; idx ++)
		{
			MeshDef meshDef = rendererDef.meshes[idx];
			shaders[idx] = ResourceFactory.getShader(meshDef.shaderName);
			TextureAtlas atlas = ResourceFactory.getTextureAtlas(meshDef.textureName);
			textures[idx] = atlas.getTextures().iterator().next();
			meshIndices.put(meshDef.textureName, idx);
			//textures[idx].setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			//textures[idx].setFilter(TextureFilter.MipMap, TextureFilter.MipMap);
		}
		//meshTexture = ResourceFactory.getTexture(Resources.TEXTURE_GRASS_MASK);
		
		
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
		
		for(int idx = 0; idx < textures.length; idx ++)
		{
			MeshDef meshDef = rendererDef.meshes[idx];
			int w = rendererDef.width*meshDef.unitsPerTile;
			int h = rendererDef.height*meshDef.unitsPerTile;
			int dx = meshDef.unitsPerTile/2;
			int dy = meshDef.unitsPerTile/2;
			meshes[idx] = new TileMultiMesh(w, h, verticesPerTile, trianglesPerTile, vattr);
			meshes[idx].fillTiles((x, y, tileIdx, vertexBuffer, triangles) -> {
				
				
				int vidx = 0;
				vertexBuffer.set(vidx++, x+dx);
				vertexBuffer.set(vidx++, y+dy);
				vertexBuffer.set(vidx++, -y);//heightmap[xx][yy];
				vertexBuffer.set(vidx++, 0);
				vertexBuffer.set(vidx++, 0);
	
				// color
				vertexBuffer.set(vidx++, 1);
				vertexBuffer.set(vidx++, 1);
				vertexBuffer.set(vidx++, 1);
				vertexBuffer.set(vidx++, 1);					
	
				int c00 = verticesPerTile*tileIdx+0;
				int c10 = verticesPerTile*tileIdx+1;
				int c01 = verticesPerTile*tileIdx+2;
				int c11 = verticesPerTile*tileIdx+3;
	
				triangles.addTriangle(c00, c01, c10);
				triangles.addTriangle(c10, c01, c11);
			});
		}
	}

	
	public void updateTile(int tx, int ty, float ex, float ey, float ez, boolean clear, float [] buffer, TileSpriteComponent sprite)
	{
		float x = ex - sprite.dx;
		float y = ey - sprite.dy;
		float z = ez;
		//z = -ty;
		int vidx = 0;
		buffer[vidx++] = x;
		buffer[vidx++] = y+sprite.height;
		buffer[vidx++] = z;
		buffer[vidx++] = clear ? 0 : sprite.region.getU();
		buffer[vidx++] = clear ? 0 : sprite.region.getV();
		buffer[vidx++] = 0;
		buffer[vidx++] = 0;
		buffer[vidx++] = 1;

		// color
		buffer[vidx++] = sprite.def.color.r;
		buffer[vidx++] = sprite.def.color.g;
		buffer[vidx++] = sprite.def.color.b;
		buffer[vidx++] = clear ? 0 : 1;					

		buffer[vidx++] = x;
		buffer[vidx++] = y;
		buffer[vidx++] = z;
		buffer[vidx++] = clear ? 0 : sprite.region.getU();
		buffer[vidx++] = clear ? 0 : sprite.region.getV2();
		buffer[vidx++] = 0;
		buffer[vidx++] = 0;
		buffer[vidx++] = 1;

		// color
		buffer[vidx++] = sprite.def.color.r;
		buffer[vidx++] = sprite.def.color.g;
		buffer[vidx++] = sprite.def.color.b;
		buffer[vidx++] = clear ? 0 : 1;	
		
		buffer[vidx++] = x+sprite.width;
		buffer[vidx++] = y+sprite.height;
		buffer[vidx++] = z;
		buffer[vidx++] = clear ? 0 : sprite.region.getU2();
		buffer[vidx++] = clear ? 0 : sprite.region.getV();
		buffer[vidx++] = 0;
		buffer[vidx++] = 0;
		buffer[vidx++] = 1;

		// color
		buffer[vidx++] = sprite.def.color.r;
		buffer[vidx++] = sprite.def.color.g;
		buffer[vidx++] = sprite.def.color.b;
		buffer[vidx++] = clear ? 0 : 1;
		
		buffer[vidx++] = x+sprite.width;
		buffer[vidx++] = y;
		buffer[vidx++] = z;
		buffer[vidx++] = clear ? 0 : sprite.region.getU2();
		buffer[vidx++] = clear ? 0 : sprite.region.getV2();
		buffer[vidx++] = 0;
		buffer[vidx++] = 0;
		buffer[vidx++] = 1;

		// color
		buffer[vidx++] = sprite.def.color.r;
		buffer[vidx++] = sprite.def.color.g;
		buffer[vidx++] = sprite.def.color.b;
		buffer[vidx++] = clear ? 0 : 1;	
	}
	

	@Override
	public void entityAdded(Entity entity)
	{
		TileSpriteComponent tileSprite = entity.getComponent( TileSpriteComponent.class );
		if( tileSprite != null)
		{			
			updateSprite(tileSprite);
		}
		
		TileMultiSpriteComponent tileMultiSprite = entity.getComponent( TileMultiSpriteComponent.class );
		if( tileMultiSprite != null )
		{
			for(int i = 0; i < tileMultiSprite.sprites.size(); i ++)
			{
				TileSpriteComponent sprite = tileMultiSprite.sprites.get(i);
				updateSprite( sprite);
			}
		}
	}


	public void updateSprite(TileSpriteComponent tileSprite)
	{
		tileSprite.renderer = this;
		
		//final SpatialComponent spatial = entity.getComponent( SpatialComponent.class );
		
		int meshIndex = meshIndices.get(tileSprite.def.atlas.getName(), -1);
		if( meshIndex < 0)
			throw new IllegalArgumentException("No mesh for defined for atlas " + tileSprite.def.atlas.getName());
		
		TileMultiMesh multimesh = meshes[meshIndex];
		MeshDef meshDef = rendererDef.meshes[meshIndex];
		
		multimesh.updateTile(meshDef.getUnitsPerTile()*tileSprite.def.tx, meshDef.getUnitsPerTile()*tileSprite.def.ty, new TileUpdate() {
			
			@Override
			public void updateVertexBuffer(int tx, int ty, float[] vertexBufferUpdate)
			{
				updateTile(tx, ty, tileSprite.def.x, tileSprite.def.y, tileSprite.def.priority, false, vertexBufferUpdate, tileSprite);
			}
		});	
	}

	public void entityUpdated(TileSpriteComponent tileSprite)
	{

		if( tileSprite == null)
			return; // ignore
		
		
		int meshIndex = meshIndices.get(tileSprite.def.atlas.getName(), 0);
		TileMultiMesh multimesh = meshes[meshIndex];
		multimesh.updateTile(tileSprite.def.tx, tileSprite.def.ty, new TileUpdate() {
			
			@Override
			public void updateVertexBuffer(int tx, int ty, float[] vertexBufferUpdate)
			{
				updateTile(tx, ty, tileSprite.def.x, tileSprite.def.y, tileSprite.def.priority, false, vertexBufferUpdate, tileSprite);
			}
		});
		
	}
	@Override
	public void update( float deltaTime )
	{
	}

	@Override
	public void entityRemoved(Entity entity)
	{
		TileSpriteComponent tileSprite = entity.getComponent( TileSpriteComponent.class );
		if( tileSprite != null)
		{
			removeSprite(tileSprite);
		}
		
		TileMultiSpriteComponent tileMultiSprite = entity.getComponent( TileMultiSpriteComponent.class );
		if( tileMultiSprite != null )
		{
			for(int i = 0; i < tileMultiSprite.sprites.size(); i ++)
			{
				TileSpriteComponent sprite = tileMultiSprite.sprites.get(i);
				removeSprite(sprite);
			}
		}

	}


	public void removeSprite(TileSpriteComponent tileSprite)
	{
		int meshIndex = meshIndices.get(tileSprite.def.atlas.getName(), 0);
		TileMultiMesh multimesh = meshes[meshIndex];
		multimesh.updateTile(tileSprite.def.tx, tileSprite.def.ty, new TileUpdate() {
			
			@Override
			public void updateVertexBuffer(int tx, int ty, float[] vertexBufferUpdate)
			{
				updateTile(tx, ty, 0,0, 200, true, vertexBufferUpdate, tileSprite);
			}
		});	
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

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		
	}

	int [] cid = new int [] {};
	@Override
	public int[] cid() { return cid; }

	@Override
	public void render(Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime)
	{
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
		//TextureRenderingContext ctx = (TextureRenderingContext)context;
		//ctx.getTexture().bind();
		for(int tidx = 0; tidx < textures.length; tidx ++)
		{
			textures[tidx].bind();
	
			ShaderProgram shader = shaders[tidx];
			shader.begin();
	
			shader.setUniformMatrix("u_projTrans", cam.combined);
			//		Color color1 = ceilingWallMesh.color1;
			//		Color color2 = Color.BLACK;
			//		bicoloringShader.setUniformf("u_maskingColor", color1.r, color1.g, color1.b, 0.2f);
			//		bicoloringShader.setUniformf("v_maskingColor", color2.r, color2.g, color2.b, 1f);
			TileMultiMesh multimesh = meshes[tidx];
	
			for(int midx = 0; midx < multimesh.getMeshes().size(); midx ++)
				multimesh.getMeshes().get(midx).render( shader, GL20.GL_TRIANGLES );
	
			shader.end();
	}	
	}

}
