package game.world.saves;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SavedEntity {
	@Getter String id;
	@Getter HashMap <String, String> props;
}
