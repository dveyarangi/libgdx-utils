package game.world.saves;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SavedEntity {
	@Getter String prefab;
	@Getter HashMap <String, String> props;
}
