package game.debug;

public class InvokationTimer
{
	private long lastMeasureTime;
	private int count;
	private long measureInterval;

	private float average = 1;
	private String tag;


	public InvokationTimer(String tag)
	{
		this.tag = tag;
		measureInterval = 1000;
		lastMeasureTime = System.currentTimeMillis();
		count = 0;
		average = 1;
	}

	public void measure()
	{
		long now = System.currentTimeMillis();
		if(now - lastMeasureTime < measureInterval)
		{
			count ++;
			return;
		}


		average = average*0.5f + count*0.5f;
		System.out.println(tag + ": average calls per second: " + average);

		lastMeasureTime = now;
		count = 0;

	}
}
