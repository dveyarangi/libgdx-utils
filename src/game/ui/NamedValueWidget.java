package game.ui;

import java.util.function.BiConsumer;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.kotcrab.vis.ui.widget.VisLabel;


public class NamedValueWidget <V>
{
		
	public String name;
	public String nullText;
	public Label label = new VisLabel();
	BiConsumer<V, StringBuilder> format = (val, builder) -> builder.append(val);

	private StringBuilder string = new StringBuilder();

	public NamedValueWidget(String name, Label label, BiConsumer<V, StringBuilder> format, String nullText, Table table)
	{
		this.name = name;
		this.label = label;
		this.format = format;
		this.nullText = nullText;
		this.init(table);
	}
	public NamedValueWidget(String name, BiConsumer<V, StringBuilder> format, String nullText, Table table)
	{
		this(name, new VisLabel(), format, nullText, table);
	}
	
	public NamedValueWidget(String name, String nullText, Table table)
	{
		this(name, (val, builder) -> builder.append(val), nullText, table);
	}
	
	public NamedValueWidget(String name, Table table)
	{
		this(name, "", table);
	}
	
	private void init(Table table)
	{
		table.row();//.padTop(5).padBottom(5);
		table.add(new VisLabel(name + ": ")).padLeft(10).align(Align.left).minWidth(100).maxWidth(100);
		table.add(label).align(Align.left).minWidth(100).maxWidth(100);
	}
	
	public void update(V value)
	{
		string.setLength(0);
		if(value != null)
		{
			format.accept(value, string);
			label.setText(string);
		}
		else
			label.setText(nullText);	
	}

}