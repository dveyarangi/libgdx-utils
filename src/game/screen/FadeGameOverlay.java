package game.screen;

import game.systems.rendering.Renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class FadeGameOverlay implements IGameOverlay
{

	private AbstractScreen screen;

	private static float INTERVAL = 0.5f;
	private float inTime;
	private static float OUTERVAL = 0.5f;
	private float ouTime;
	private float fadePercentage;

	private boolean doFade;

	protected boolean inFade = false;
	public boolean outFade = false;

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
			float alpha = outFade ? 0 : 1 - getFadePercentage();
			Gdx.gl.glEnable(GL20.GL_BLEND);
			renderer.shaper.begin(ShapeType.Filled);
			renderer.shaper.setColor(0, 0, 0, fadePercentage);
			renderer.shaper.rect(0, 0, screen.HUD_WIDTH, screen.HUD_HEIGHT);
			renderer.shaper.end();
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
