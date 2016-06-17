package net.thecobix.stats.cmd;

import java.io.IOException;
import java.util.ArrayList;

import net.thecobix.stats.client.Client;


public class CommandManager
{
  public static ArrayList<Command> commands = new ArrayList();
  
  public CommandManager()
  {
    addCommands();
  }
  
  public void addCommands()
  {
    commands.clear();
    commands.add(new CommandStop());
    commands.add(new CommandKeepalive());
  }
  
  public static String cmdPrefix = "COMMAND:";
  
  public void runCommands(String s, Client receiver)
  {
    if ((!s.contains(cmdPrefix)) || (!s.startsWith(cmdPrefix))) {
      return;
    }
    boolean commandResolved = false;
    String readString = s.trim().substring(cmdPrefix.length()).trim();
    boolean hasArgs = readString.trim().contains(" ");
    String commandName = hasArgs ? readString.split(" ")[0] : readString.trim();
    String[] args = hasArgs ? readString.substring(commandName.length()).trim().split(" ") : new String[0];
    for (Command command : commands) {
      if (command.getCommand().trim().equalsIgnoreCase(commandName.trim()))
      {
        command.runCommand(readString, args, receiver);
        commandResolved = true;
        break;
      }
    }
    if (!commandResolved) {
    	try {
			receiver.send("MSG: unknown command");
		} catch (IOException e) {
			
		}
    }
  }
  
  public void unloadAll() {
	  commands.clear();
  }
  
}
