package game.systems.rendering;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import game.resources.ResourceFactory;
import game.systems.IComponentDef;
import game.systems.spatial.SpatialComponent;
import game.util.rendering.TileMultiMesh;
import game.util.rendering.TileMultiMesh.TileUpdate;
import game.world.Level;

public class TileSpritesRenderer extends EntitySystem implements EntityListener, IRenderingComponent
{
	//static { ComponentType.registerFor(IRenderingComponent.class, MeshRenderingComponent.class); }

	TileSpritesRendererDef meshDef;
	Texture meshTexture;

	ShaderProgram shader;

	Camera cam;
	
	
	TileMultiMesh multimesh;

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
		shader = ResourceFactory.getShader(meshDef.shaderName);
		
		//meshTexture = ResourceFactory.getTexture(Resources.TEXTURE_GRASS_MASK);
		meshTexture = meshDef.texture; 
		meshTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		meshTexture.setFilter(TextureFilter.MipMap, TextureFilter.MipMap);
		
		int w = meshDef.width;
		int h = meshDef.height;
		
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
		
		multimesh = new TileMultiMesh(w, h, verticesPerTile, trianglesPerTile, vattr);
		multimesh.fillTiles((x, y, tileIdx, vertexBuffer, triangles) -> {
				
			
			int vidx = 0;
			vertexBuffer.set(vidx++, x+0.5f);
			vertexBuffer.set(vidx++, y+0.5f);
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
	
	public void updateTile(int tx, int ty, float ex, float ey, boolean clear, float [] buffer, TileSpriteComponent sprite)
	{
		float x = ex - sprite.dx;
		float y = ey - sprite.dy;
		float z = -10f - (float)(meshDef.height-ty)/meshDef.height;
		//z = -ty;
		int vidx = 0;
		buffer[vidx++] = x;
		buffer[vidx++] = y+sprite.def.h;
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
		
		buffer[vidx++] = x+sprite.def.w;
		buffer[vidx++] = y+sprite.def.h;
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
		
		buffer[vidx++] = x+sprite.def.w;
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
		if( tileSprite == null)
			return; // ignore
			
		final SpatialComponent spatial = entity.getComponent( SpatialComponent.class );
		
		
		multimesh.updateTile(tileSprite.def.tx, tileSprite.def.ty, new TileUpdate() {
			
			@Override
			public void updateVertexBuffer(int tx, int ty, float[] vertexBufferUpdate)
			{
				updateTile(tx, ty, spatial.x(), spatial.y(), false, vertexBufferUpdate, tileSprite);
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
		if( tileSprite == null)
			return; // ignore
		multimesh.updateTile(tileSprite.def.tx, tileSprite.def.ty, new TileUpdate() {
			
			@Override
			public void updateVertexBuffer(int tx, int ty, float[] vertexBufferUpdate)
			{
				updateTile(tx, ty, 0,0, true, vertexBufferUpdate, tileSprite);
			}
		});	}


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
		meshTexture.bind();

		shader.begin();

		shader.setUniformMatrix("u_projTrans", cam.combined);
		//		Color color1 = ceilingWallMesh.color1;
		//		Color color2 = Color.BLACK;
		//		bicoloringShader.setUniformf("u_maskingColor", color1.r, color1.g, color1.b, 0.2f);
		//		bicoloringShader.setUniformf("v_maskingColor", color2.r, color2.g, color2.b, 1f);

		for(int idx = 0; idx < multimesh.getMeshes().size(); idx ++)
			multimesh.getMeshes().get(idx).render( shader, GL20.GL_TRIANGLES );

		shader.end();
	}

}
