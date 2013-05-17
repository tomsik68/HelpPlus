package sk.tomsik68.helpplus.findcommands;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.VanillaCommand;

import com.google.common.util.concurrent.FakeTimeLimiter;

import sk.tomsik68.helpplus.CommandInfo;
import sk.tomsik68.helpplus.CompatibilityChecker;
import sk.tomsik68.helpplus.FakePlayer;
import sk.tomsik68.helpplus.HelpPlus;

public class CommandMapCommandProvider implements CommandProvider {
    static {
        CommandProviders.registerProvider(new CommandMapCommandProvider());
    }

    @Override
    public Map<String, CommandInfo> getCommands(Server server) {
        HashMap<String, CommandInfo> result = new HashMap<String, CommandInfo>();
        @SuppressWarnings("unchecked")
        Map<String, Command> commands = new HashMap<String, Command>((HashMap<String, Command>) ReflectionUtils.get(ReflectionUtils.get(server, "commandMap"), "knownCommands"));
        @SuppressWarnings("unchecked")
        Set<VanillaCommand> vanillaCommands = new HashSet<VanillaCommand>((Collection<? extends VanillaCommand>) ReflectionUtils.get(ReflectionUtils.get(server, "commandMap"), "fallbackCommands"));

        for (VanillaCommand vc : vanillaCommands) {
            result.put(vc.getName(), new CommandInfo(vc.getName(), vc.getUsage(), vc.getDescription(), vc.getAliases().toArray(new String[0]), vc.getPermission(), "<bukkit>"));
        }

        for (Entry<String, Command> entry : commands.entrySet()) {
            if (entry.getValue() instanceof PluginCommand) {
                result.put(entry.getValue().getName(), new CommandInfo((PluginCommand) entry.getValue()));
            } else {
                result.put(entry.getKey(), new CommandInfo(entry.getKey(), entry.getValue().getUsage().replaceAll("<command>", entry.getValue().getName()), entry.getValue().getDescription(), entry.getValue().getAliases().toArray(new String[0]), entry.getValue().getPermission(), "<unknown>"));
            }
            if (entry.getValue().getPermission() == null || entry.getValue().getPermission().length() == 0 || entry.getValue().getPermission().equalsIgnoreCase("null")) {
                result.get(entry.getKey()).permission = resolvePermission(entry.getKey());
            }
        }
        return result;
    }

    private String resolvePermission(final String commandName) {
        try {
            FakePlayer fakie = new FakePlayer();
            Bukkit.dispatchCommand(fakie, "/" + commandName);
            StringBuilder sb = new StringBuilder();
            for (String p : fakie.getPermissionsUsed()) {
                sb = sb.append(p).append(';');
            }
            if (sb.length() > 0)
                sb = sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (Exception e) {
            // could not resolve permission
        }
        return "";
    }

    @Override
    public boolean isFunctional(Server server) {
        try {
            CompatibilityChecker.performCheck();
            return true;
        } catch (Exception e) {
            HelpPlus.log.info("Incompatible CraftBukkit. ");
            HelpPlus.log.info("Reason: ");
            e.printStackTrace();
        }
        return false;
    }
}
