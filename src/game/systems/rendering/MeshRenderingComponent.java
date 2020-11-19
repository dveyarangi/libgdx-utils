package game.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import game.resources.ResourceFactory;
import game.systems.IComponentDef;
import game.util.MeshDef;
import game.world.Level;

public class MeshRenderingComponent implements IRenderingComponent
{
	//static { ComponentType.registerFor(IRenderingComponent.class, MeshRenderingComponent.class); }

	private int [] cid;

	MeshDef terrainTriangles;

	Mesh mesh;

	Texture meshTexture;

	ShaderProgram shader;

	public Camera cam;

	public MeshRenderingComponent()
	{
		cid = new int[1];
	}

	@Override
	public void init( Entity entity, IComponentDef<?> def, Level level )
	{
		this.cam = level.getModules().getCameraProvider().getCamera();
		
		MeshRenderingDef meshDef = (MeshRenderingDef) def;
		//ResourceFactory factory = level.getModules().getGameFactory();
		meshTexture = ResourceFactory.getTexture(meshDef.textureName);
		shader = ResourceFactory.getShader(meshDef.shaderName);

		terrainTriangles = meshDef.triangles;


		this.cid[0] = TextureID.genid( meshTexture );
		VertexAttributes vattr = new VertexAttributes(
				new VertexAttribute( Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE ),
				new VertexAttribute( Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0" )/*,
				new VertexAttribute( Usage.ColorPacked, 1, ShaderProgram.COLOR_ATTRIBUTE)*/
				);

		// filling vertex buffer:
		int verticesNum = terrainTriangles.points.size;
		int vertexBufferSize = vattr.size() * verticesNum;

		float [] vertexBuffer = new float [vertexBufferSize];
		int pIdx = 0;
		for(int vIdx = 0; vIdx < vertexBufferSize; )
		{
			float x = terrainTriangles.points.get(pIdx ++);
			float y = terrainTriangles.points.get(pIdx ++);
			vertexBuffer[vIdx++] = x;
			vertexBuffer[vIdx++] = y;
			vertexBuffer[vIdx++] = (x+meshDef.width/2)/meshDef.width;
			vertexBuffer[vIdx++] = (y+meshDef.height/2)/meshDef.height;
		}

		// filling triangle index buffer:
		int indexBufferSize = terrainTriangles.triangles.size;
		short [] indexBuffer = new short [ indexBufferSize ];
		for(int iIdx = 0; iIdx < indexBufferSize; iIdx ++)
		{
			indexBuffer[iIdx] = terrainTriangles.triangles.get(iIdx);
		}

		// creating mesh:
		mesh = new Mesh( true, verticesNum, indexBufferSize, vattr);
		mesh.setIndices(indexBuffer);
		mesh.setVertices(vertexBuffer);

	}
	@Override
	public void reset()
	{
		cid[0] = IRenderingContext.INVALID_ID;
	}

	@Override public int [] cid() { return cid; }

	@Override
	public void render( Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime )
	{
		meshTexture.bind();

		shader.begin();

		shader.setUniformMatrix("u_projTrans", cam.combined);
		//		Color color1 = ceilingWallMesh.color1;
		//		Color color2 = Color.BLACK;
		//		bicoloringShader.setUniformf("u_maskingColor", color1.r, color1.g, color1.b, 0.2f);
		//		bicoloringShader.setUniformf("v_maskingColor", color2.r, color2.g, color2.b, 1f);

		mesh.render( shader, GL20.GL_TRIANGLES );

		shader.end();

		//return false;
	}


}
