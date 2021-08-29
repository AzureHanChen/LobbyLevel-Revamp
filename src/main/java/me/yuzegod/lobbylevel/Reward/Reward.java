package me.yuzegod.lobbylevel.Reward;

import java.util.*;

public class Reward
{
    private int level;
    private String name;
    private List<String> description;
    private List<String> commands;
    
    public Reward(final int level, final String name, final List<String> description, final List<String> commands) {
        this.level = level;
        this.name = name;
        this.description = description;
        this.commands = commands;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getDescription() {
        return this.description;
    }
    
    public List<String> getCommands() {
        return this.commands;
    }
}
