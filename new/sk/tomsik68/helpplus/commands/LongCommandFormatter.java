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

        String[] result = new String[6];
        result[0] = colorA + "Command: " + colorC + " /" + ci.getName();
        result[1] = colorA + "Description: " + colorC + " /" + ci.getDescription();
        result[2] = colorA + "Usage: " + colorC + " /" + ci.getUsgae();
        result[3] = colorA + "Permission: " + colorC + ci.getPermission();
        if (showPlugin)
            result[4] = colorA + "Plugin: " + colorC + ci.getPlugin();
        else
            result[4] = colorA + "Plugin: <unknown>";
        result[5] = colorA + "Aliases: " + colorC + ci.getAliases();
        return result;
    }

}
