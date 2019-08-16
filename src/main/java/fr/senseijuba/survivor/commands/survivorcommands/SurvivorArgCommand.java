package fr.senseijuba.survivor.commands.survivorcommands;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class SurvivorArgCommand {
    protected String name;
    protected String def;
    protected String use;
    protected String[] aliases;
    protected boolean hidden;
    protected int minArgs;

    public SurvivorArgCommand(String name, String def, String use, boolean hidden, int minArgs, String... aliases)
    {
        this.name = name;
        this.def = def;
        this.use = use;
        this.hidden = hidden;
        this.aliases = aliases;
        this.minArgs = minArgs;
    }

    public String getName()
    {
        return name;
    }

    public String getDef()
    {
        return def;
    }

    public String getUse()
    {
        return use;
    }

    public int getMinArgs()
    {
        return minArgs;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public List<String> getAliases()
    {
        List<String> aliases = new ArrayList<>();

        for(String s : this.aliases)
            aliases.add(s);

        return aliases;
    }

    public boolean doAliasesContainsIgnoreCase(String s)
    {
        for(String a : getAliases())
            if(a.equalsIgnoreCase(s))
                return true;

        return false;
    }

    public List<String> getPossibleArgs(Player executer, String[] args)
    {
        return new ArrayList<>();
    }

    public boolean isExecutableFrom(String s)
    {
        return getName().equalsIgnoreCase(s) || doAliasesContainsIgnoreCase(s);
    }

    public abstract void execute(Player p, String[] args);
}
