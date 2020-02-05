package net.onima.onimafaction.commands.fastplant;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.commands.ArgumentExecutor;
import net.onima.onimafaction.commands.fastplant.arguments.FastPlantCheckArgument;

public class FastPlantExecutor extends ArgumentExecutor {

	public FastPlantExecutor() {
		super("fastplant", OnimaPerm.ONIMAFACTION_FASTPLANT_COMMAND);
		
		addArgument(new FastPlantCheckArgument());
	}


}
