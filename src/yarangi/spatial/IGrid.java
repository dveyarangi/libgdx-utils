package yarangi.spatial;

public interface IGrid <C>
{
	public int getGridWidth();
	public int getGridHeight();
	public float getMinX();
	public float getMaxX();
	public float getMinY();
	public float getMaxY();
	public int getCellSize();
	public boolean isEmptyAtCoord(float x, float y);

	public void setModificationListener(IGridListener<C> listener);
}
