package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import game.config.GraphicOptions;
import game.systems.rendering.Renderer;

public class FadeGameOverlay implements IGameOverlay
{

	private GraphicOptions options;

	private AbstractScreen screen;

	public static float INTERVAL = 0.5f;
	private float inTime;
	public static float OUTERVAL = 0.5f;
	private float ouTime;
	private float fadePercentage;

	private boolean doFade;

	protected boolean inFade = false;
	public boolean outFade = false;

	private int screenWidth, screenHeight;
	
	FadeGameOverlay()
	{
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();
	}

	@Override
	public void setFadeIn( AbstractScreen screen )
	{
		this.screen = screen;
		this.inTime = 0;
		this.outFade = false;
	}

	@Override
	public void setFadeOut( AbstractScreen screen )
	{
		this.screen = screen;
		this.outFade = true;
		this.ouTime = 0;
	}

	@Override
	public void updateOverlay( float delta )
	{

		doFade = inFade = false;

		if( inTime < INTERVAL )
		{
			inFade = true;
			inTime += delta;
			fadePercentage = Math.max(0f, 1f - inTime / INTERVAL);
			if( fadePercentage > 0 )
				doFade = true;
		}
		else if( outFade )
		{
			outFade = true;
			ouTime += delta;
			fadePercentage = Math.min(ouTime / OUTERVAL, 1f);
			doFade = true;
			if( ouTime > INTERVAL )
			{
				outFade = false;
				inFade = false;
			}
		}

	}

	@Override
	public void drawOverlay( Renderer renderer )
	{
		if( doFade )
		{
			float alpha = outFade ? getFadePercentage() : 1 - getFadePercentage();
			//System.out.println(alpha);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			renderer.shaper().begin(ShapeType.Filled);
			renderer.shaper().setColor(0.1f, 0.1f, 0.1f, alpha);
			renderer.shaper().rect(0, 0, screenWidth, screenHeight);
			renderer.shaper().end();
		}
	}

	public float getFadePercentage()
	{
		return fadePercentage;
	}

	@Override
	public boolean isTotallyFaded()
	{
		return ouTime >= INTERVAL;
	}

}
