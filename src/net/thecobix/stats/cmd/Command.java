package net.thecobix.stats.cmd;

import net.thecobix.stats.client.Client;

public abstract class Command
{
  private String command;
  
  public Command(String command)
  {
    this.command = command;
  }
  
  public abstract void runCommand(String cmd, String[] args, Client receiver);
  
  public abstract String getDescription();
  
  public abstract String getSyntax();
  
  public String getCommand()
  {
    return this.command;
  }
}
