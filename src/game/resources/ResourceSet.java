package game.resources;

import java.util.Map;

public interface ResourceSet
{
	/**
	 * Allows to use enum interfaces in configuration files 
	 * @return
	 */
	public Map<Class<?>, Class <? extends Enum<?>>> getCfgEnums();
	
}
