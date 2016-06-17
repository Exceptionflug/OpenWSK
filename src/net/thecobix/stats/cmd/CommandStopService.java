package net.thecobix.stats.cmd;

import org.bukkit.Bukkit;

import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.stats.client.Client;

public class CommandStopService extends Command {

    public CommandStopService()
    {
        super("stopservice");
    }

    @Override
    public void runCommand(String cmd, String[] args, Client receiver)
    {
        OpenWSK.theStatsClient.disconnect();
        Bukkit.getPluginManager().disablePlugin(OpenWSK.getPluginInstance());
    }

    @Override
    public String getDescription()
    {
        return "Reloads the current wsk instance";
    }

    @Override
    public String getSyntax()
    {
        return "COMMAND: stopservice";
    }

}
