package net.onima.onimafaction.commands.lives;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.commands.ArgumentExecutor;
import net.onima.onimafaction.commands.lives.arguments.LivesCheckArgument;
import net.onima.onimafaction.commands.lives.arguments.LivesSendArgument;
import net.onima.onimafaction.commands.lives.arguments.staff.LivesGiveArgument;
import net.onima.onimafaction.commands.lives.arguments.staff.LivesRemoveArgument;
import net.onima.onimafaction.commands.lives.arguments.staff.LivesSetArgument;

public class LiveExecutor extends ArgumentExecutor {

	public LiveExecutor() {
		super("lives", OnimaPerm.ONIMAFACTION_LIVES_COMMAND);
		
		addArgument(new LivesCheckArgument());
		addArgument(new LivesGiveArgument());
		addArgument(new LivesRemoveArgument());
		addArgument(new LivesSendArgument());
		addArgument(new LivesSetArgument());
	}


}
