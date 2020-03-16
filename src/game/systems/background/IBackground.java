package game.systems.background;

public interface IBackground
{

	void draw();

	void update( float delta );

	void init( float width, float height );

	void destroy();

	void resize( int width, int height );
}
