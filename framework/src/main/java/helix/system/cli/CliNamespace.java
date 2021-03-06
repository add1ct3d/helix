package helix.system.cli;

import helix.system.cli.commands.Exit;
import helix.system.cli.commands.Help;
import helix.system.cli.commands.Namespace;

import java.util.HashMap;
import java.util.Map;

public abstract class CliNamespace
{
    private Map<String, CliCommand> commands;

    /**
     * Creates new CliNamespace instance
     * **/
    public CliNamespace()
    {
        commands = new HashMap<>();
        registerCommand(new Exit(), new Help(), new Namespace());
    }

    /**
     * Registeres one or more commands with this namespace
     * @param cliCommands List of CLI commands to register
     * **/
    protected void registerCommand(CliCommand ...cliCommands)
    {
        for(CliCommand cliCommand : cliCommands) commands.put(cliCommand.command(), cliCommand);
    }

    public abstract String description();
    public abstract String name();

    /**
     * Getter for all registered commands
     * @return Registered commands
     * **/
    public Map<String, CliCommand> commands()
    {
        return commands;
    }
}
