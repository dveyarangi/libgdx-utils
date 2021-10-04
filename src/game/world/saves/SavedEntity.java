package game.world.saves;

import com.badlogic.gdx.utils.ObjectMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SavedEntity {
	@Getter String id;
	@Getter ObjectMap <String, String> props;
}
