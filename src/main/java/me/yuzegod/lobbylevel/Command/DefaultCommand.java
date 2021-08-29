package me.yuzegod.lobbylevel.Command;

import org.bukkit.command.*;

public class DefaultCommand
{
    private CommandSender sender;
    private Command command;
    private String label;
    private String[] args;
    
    public DefaultCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
    }
    
    public CommandSender getSender() {
        return this.sender;
    }
    
    public Command getCommand() {
        return this.command;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public String[] getArgs() {
        return this.args;
    }
}
