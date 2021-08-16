package game.resources;

import java.util.List;

import game.systems.EntityDef;
import lombok.Getter;

public class EntityDefGroup
{
	@Getter private String name;
	@Getter private List <EntityDef> entities;
}
