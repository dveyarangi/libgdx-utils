package game.world.environment.resources;

/**
 * Represents a game resource. Resource supplying and consumption should be
 * performed through either {@link Port} or {@link GatheringTask} interfaces.
 * 
 * @author dveyarangi
 *
 */
public class Resource
{
	public static enum Type
	{
		MATTER, ENERGY
	}

	public Type type;

	public float amount;

	Resource( Type type, float amount )
	{
		if( amount < 0 )
			throw new IllegalArgumentException("Cannot create negative resource.");
		this.type = type;
		this.amount = amount;
	}

	public Type getType()
	{
		return type;
	}

	public float getAmount()
	{
		return amount;
	}

	/**
	 * If nothing was consumed, null is returned
	 * 
	 * @param amount
	 *            amount to consume
	 * @param consumeOnFailure
	 *            either we should still consume when to enough resource
	 *            available
	 * @return
	 */
	public Resource consume( float consumedAmount, boolean consumeOnFailure )
	{
		float consumed;
		if( this.amount >= consumedAmount )
			consumed = consumedAmount;
		else if( consumeOnFailure )
			consumed = this.amount;
		else
			consumed = 0;

		this.amount -= consumed;
		if( amount < 0 )
			amount = 0;

		return consumed >= 0 ? new Resource(getType(), consumed) : null;
	}

	/**
	 * Creation of resources is by {@link GatheringTask} only.
	 * 
	 * @param suppliedAmount
	 */
	void supply( float suppliedAmount )
	{
		if( amount < 0 )
			throw new IllegalArgumentException("Cannot supply negative resource.");
		this.amount += suppliedAmount;
	}

	@Override
	public String toString()
	{
		return new StringBuilder()
				.append("type:").append(type).append(" amount:").append(amount)
				.toString();
	}
}
