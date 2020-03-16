package game.systems.hud;

public interface UIComponent
{
	public int[] getActiveOverlays();

	public boolean isHovered();

	public int[] getHoverOverlays();

	public void setIsHovered( float x, float y );

	public void toggleOverlay( int factionOid );
}
