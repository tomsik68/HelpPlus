package sk.tomsik68.helpplus.commands;

import org.bukkit.ChatColor;

import sk.tomsik68.helpplus.CommandInfo;

public class ShortCommandFormatter implements CommandFormatter {

    @Override
    public String[] format(CommandInfo ci) {
        String[] result = new String[1];
        StringBuilder sb = new StringBuilder();
        sb = sb.append(ChatColor.BLUE)
                .append('/')
                .append(ci.getName())
                .append(" - ")
                .append(ChatColor.GREEN)
                .append(ci.getDescription());
        result[0] = sb.toString();
        return result;
    }

}
