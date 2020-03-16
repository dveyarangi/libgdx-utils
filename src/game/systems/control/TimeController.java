package game.systems.control;

/**
 * Provides game time modifier coefficient;
 *
 * @author Fima TODO: this one and all related code should be teared from
 *         GameInputProcessor as optional module
 */
public class TimeController
{
	float currentModifier;
	float targetModifier;

	public TimeController()
	{
		this.currentModifier = this.targetModifier = 1;
	}

	public float getModifier()
	{
		return currentModifier;
	}

	public void setTarget( final float f )
	{
		this.targetModifier = f;
		if( targetModifier > 4 )
		{
			targetModifier = 4;
		}
		else if( targetModifier <= 0.25 )
		{
			targetModifier = 0.25f;
		}

	}

	public float getTargetModifier()
	{
		return targetModifier;
	}

	public void update( final float delta )
	{
		currentModifier += delta * ( targetModifier - currentModifier ) / 2;
	}
}
