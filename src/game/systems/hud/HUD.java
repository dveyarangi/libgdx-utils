package game.systems.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import game.debug.Debug;
import game.systems.control.GameInputProcessor;
import lombok.Getter;

public abstract class HUD
{
	protected Stage stage;
	
	protected Table table;
	
	@Getter private int screenWidth, screenHeight;

	protected GameInputProcessor input;

	public HUD()
	{
		this.stage = new Stage(new ScreenViewport());

	}
	
	public void init(GameInputProcessor gameInputProcessor) 
	{
		this.input = gameInputProcessor;
		
		this.table = new Table();
		
		table.setFillParent(true);
		stage.addActor(table);
		table.setDebug(Debug.DEBUG_UI);
	}

	
	public void render()
	{
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	
	public void resize(int width, int height)
	{
		this.screenWidth = width;
		this.screenHeight = height;

		stage.getViewport().update(width, height, true);
	}

	public InputProcessor getInputProcessor() { return stage; } 

	public void dispose() 
	{
		stage.dispose();
	}
}
