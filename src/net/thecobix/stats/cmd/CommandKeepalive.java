package net.thecobix.stats.cmd;

import net.thecobix.stats.client.Client;

public class CommandKeepalive extends Command {

	public CommandKeepalive() {
		super("keepalive");
	}

	@Override
	public void runCommand(String cmd, String[] args, Client receiver) {
		receiver.now = System.currentTimeMillis();
		receiver.ping = (int) (System.currentTimeMillis() - receiver.pStart);
		receiver.sended = false;
	}

	@Override
	public String getDescription() {
		return "Keeps connection alive";
	}

	@Override
	public String getSyntax() {
		return "keepalive";
	}

}
