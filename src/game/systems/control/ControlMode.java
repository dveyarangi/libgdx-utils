package game.systems.control;

public abstract class ControlMode implements IControlMode 
{
	
	public ControlModes modes;
	
	public ControlMode(ControlModes modes)
	{
		this.modes = modes;
	}
	
	public void modeActivated(Object parameter)
	{
		//this.switchToMode = null;
	}
	
	public void modeDeactivated()
	{
		
	}

	@Override
	public void switchToMode(String modeName, Object parameter)
	{
		modes.switchToMode(modeName, parameter);
	}
}
