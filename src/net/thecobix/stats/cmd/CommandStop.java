package net.thecobix.stats.cmd;

import net.thecobix.stats.client.Client;

public class CommandStop extends Command {

	public CommandStop() {
		super("stop");
	}

	@Override
	public void runCommand(String cmd, String[] args, Client receiver) {
		receiver.disconnect();
	}

	@Override
	public String getDescription() {
		return "Server terminates";
	}

	@Override
	public String getSyntax() {
		return "stop";
	}

}
