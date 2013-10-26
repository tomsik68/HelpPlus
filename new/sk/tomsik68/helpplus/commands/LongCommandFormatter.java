package sk.tomsik68.helpplus.commands;

import org.bukkit.ChatColor;

import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.HelpPlus;

public class LongCommandFormatter implements CommandFormatter {
    private final ChatColor colorA, colorC;
    private final boolean showPlugin;

    public LongCommandFormatter() {
        colorA = HelpPlus.config.getColorA();
        colorC = HelpPlus.config.getColorC();
        showPlugin = HelpPlus.config.isShowPlugin();
    }

    @Override
    public String[] format(CommandInfo ci) {
        String pl = ci.getPlugin();
        if (!showPlugin)
            pl = "<unknown>";
        String[] result = HelpPlus.messages.getFormattedMessage("help.command.long", ci.getName(), ci.getDescription(), ci.getUsgae(), ci.getPermission(), pl, ci.getAliases(), colorA.toString(), colorC.toString()).split("/n");
        return result;
    }

}
