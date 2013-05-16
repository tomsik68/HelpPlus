package sk.tomsik68.helpplus.commands;

import sk.tomsik68.helpplus.CommandInfo;

public interface CommandFormatter {
    public String[] format(CommandInfo ci);
}
