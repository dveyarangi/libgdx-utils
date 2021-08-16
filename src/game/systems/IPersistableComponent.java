package game.systems;

import java.util.Map;

public interface IPersistableComponent 
{
	public void write(Map <String, String> dict);
	public void read(Map <String, String> dict);
}
