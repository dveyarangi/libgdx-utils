package game.world.environment.resources;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Multibin resource stack with dynamic capacity and resource amount.
 * 
 * @author dveyarangi
 *
 */
public class Port
{
	/**
	 * @deprecated Test only, not really infinite
	 * @return
	 */
	@Deprecated
	public static Port createEndlessPort()
	{
		Port port = new Port();
		for( Resource.Type type : Resource.Type.values() )
		{
			port.capacity.put(type, Float.MAX_VALUE);
			port.stock.put(type, new Resource(type, Float.MAX_VALUE));
		}

		return port;
	}

	public static Port createEmptyPort()
	{
		Port port = new Port();
		for( Resource.Type type : Resource.Type.values() )
		{
			port.capacity.put(type, 0f);
			port.stock.put(type, new Resource(type, 0));
		}

		return port;
	}

	private final Map<Resource.Type, Float> pendingProvision = new HashMap<Resource.Type, Float>();
	private final Map<Resource.Type, Float> capacity = new HashMap<Resource.Type, Float>();
	private final Map<Resource.Type, Resource> stock = new HashMap<Resource.Type, Resource>();

	private float transferRate = 1000;

	public Port()
	{
		for( Resource.Type type : Resource.Type.values() )
			pendingProvision.put(type, 0f);
	}

	public void setTransferRate( float rate )
	{
		this.transferRate = rate;
	}

	// void put(Resource resource) { stock.put( resource.type, resource ); }
	public Resource get( Resource.Type type )
	{
		return stock.get(type);
	}

	public void setCapacity( Resource.Type type, float amount, float maxAmount )
	{
		capacity.put(type, maxAmount);
		stock.put(type, new Resource(type, amount));
	}

	public float getCapacity( Resource.Type type )
	{
		return capacity.get(type);
	}

	public float use( Resource.Type type, float amount )
	{
		Resource resource = get(type);
		if( resource == null )
			return 0;

		Resource consumedResource = resource.consume(amount, false);
		return consumedResource == null ? 0 : consumedResource.getAmount();
	}

	public float getTransferRate()
	{
		return transferRate;
	}

	public float getSaturation( Resource.Type type )
	{
		return get(type).getAmount() / getCapacity(type);
	}

	public float getRequiredResource( Resource.Type type )
	{
		float requestedResource = capacity.get(type) - stock.get(type).getAmount()
				- pendingProvision.get(type);

		if( requestedResource <= 0 )
			return 0;

		return requestedResource;

	}

	/**
	 * Marks that specified amount of specifier resource is on its way Called
	 * when a carrier is allocated
	 * 
	 * @param type
	 * @param amount
	 */
	public void pendResourceProvision( Resource.Type type, float amount )
	{
		pendingProvision.put(type, pendingProvision.get(type) + amount);
	}

	/**
	 * Unmarks that specified amount of specifier resource is on its way Called
	 * when carrier task is cancelled (most probably, the carrier is dead)
	 * 
	 * @param type
	 * @param amount
	 */
	public void unpendResorceProvision( Resource.Type type, float amount )
	{
		float resultingAmount = pendingProvision.get(type) - amount;
		if( resultingAmount < 0 )
			resultingAmount = 0;
		pendingProvision.put(type, resultingAmount);
	}

	/**
	 * Adds the resource to the storage port
	 * 
	 * @param resource
	 * @return amount of resource that could not be added due to port being
	 *         full.
	 */
	public float provisionResource( Resource resource )
	{

		Resource typestock = stock.get(resource.type);
		float provisionedAmount = Math.min(
				capacity.get(resource.type) - typestock.getAmount(),
				resource.getAmount());

		typestock.supply(provisionedAmount);

		float pendingAmount = pendingProvision.get(resource.type);
		float newPendingAmount = pendingAmount - provisionedAmount;
		if( newPendingAmount < 0 )
			newPendingAmount = 0;

		pendingProvision.put(resource.type, newPendingAmount);

		return provisionedAmount;
	}

}
