package sk.tomsik68.helpplus.findcommands;

import java.util.List;

import sk.tomsik68.helpplus.CommandInfo;

public interface CommandProvider {
    public List<CommandInfo> getCommands();
}
