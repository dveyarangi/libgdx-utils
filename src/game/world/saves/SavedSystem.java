package game.world.saves;

import com.badlogic.gdx.utils.Array;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SavedSystem
{
	@Getter String name;
	@Getter Array <Props> props;
}
