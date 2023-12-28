package game.systems.fabric;

import game.systems.sensor.SensorCategory;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CategorySet
{
	int mask = 0;
	
	public CategorySet(int ... categories)
	{
        for (int category : categories)
        	set(category);
	}
	public CategorySet(SensorCategory ... categories)
	{
		for (SensorCategory category : categories)
        	set(category.ordinal());
	}
	
	public void set(int category)
	{
    	assert category < 32;
		mask |= 1 << category;
	}
	
	public boolean has(int category)
	{
		return 0 != (mask & (1 << category));  
	}
	
    public boolean matchAny(int filterMask) {
        return (mask & filterMask) != 0;
    }
}
