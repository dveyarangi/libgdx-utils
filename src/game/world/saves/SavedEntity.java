package game.world.saves;

import com.badlogic.gdx.utils.OrderedMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SavedEntity {

	@Getter OrderedMap <String, String> props;


}
