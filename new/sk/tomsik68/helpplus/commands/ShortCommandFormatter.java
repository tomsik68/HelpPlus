package sk.tomsik68.helpplus.commands;

import org.bukkit.ChatColor;

import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.HelpPlus;

public class ShortCommandFormatter implements CommandFormatter {
    private final ChatColor colorA, colorC;

    public ShortCommandFormatter() {
        colorA = HelpPlus.config.getColorA();
        colorC = HelpPlus.config.getColorC();
    }

    @Override
    public String[] format(CommandInfo ci) {
        String[] result = new String[1];
        StringBuilder sb = new StringBuilder();
        sb = sb.append(colorA).append('/').append(ci.getName()).append(" - ").append(colorC).append(ci.getDescription());
        result[0] = sb.toString();
        return result;
    }

}
