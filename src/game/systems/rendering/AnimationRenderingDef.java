package game.systems.rendering;

import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimationRenderingDef extends RendererDef<AnimationRenderingComponent>
{
	public String atlasName;
	public float timeModifier;

	public Animation animation;

	public AnimationRenderingDef(  String atlasName, float timeModifier )
	{
		super();
		this.atlasName = atlasName;
		this.timeModifier = timeModifier;
	}

	@Override
	public Class<AnimationRenderingComponent> getComponentClass() { return AnimationRenderingComponent.class; }

}
