package sk.tomsik68.helpplus;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ListingRunnable implements Runnable {
    private final CommandSender sender;
    private static final File listingFile = new File(new File("plugins", "HelpPlus"), "listing.txt");

    public ListingRunnable(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void run() {
        try {
            if (listingFile.exists())
                listingFile.delete();
            listingFile.createNewFile();
            List<CommandInfo> commands = HelpPlus.getInstance().getAllCommands();
            List<String> permissions = new ArrayList<String>();
            PrintWriter pw = new PrintWriter(new FileWriter(listingFile));
            pw.println("=====Commands=====");
            pw.println("======================================================================");
            for (CommandInfo command : commands) {
                pw.println("Name: " + command.name);
                pw.println("Description: " + command.description);
                pw.println("Usage: " + command.usgae);
                pw.println("Aliases: " + command.aliases);
                pw.println("Permission needed: " + command.permission);
                pw.println("Owning plugin: " + command.plugin);
                pw.println("======================================================================");
                if (command.permission != null && command.permission.length() > 0 && !command.permission.equalsIgnoreCase("null"))
                    permissions.add(command.permission);
            }
            pw.println("=====Permission Nodes=====");
            for (String node : permissions) {
                pw.println("* \"" + node + "\"");
            }
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "[HelpPlus] There was an error while listing commands/permissions.");
            return;
        }
        sender.sendMessage(HelpPlus.getInstance().def1 + "[HelpPlus] Command & permission Listing is done! [saved as \"" + listingFile.getAbsolutePath() + "\"]");
    }

}
