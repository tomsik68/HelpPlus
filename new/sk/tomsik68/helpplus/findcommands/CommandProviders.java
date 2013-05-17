package sk.tomsik68.helpplus.findcommands;

import java.util.ArrayList;
import java.util.List;

public class CommandProviders {
    private static final ArrayList<CommandProvider> providers = new ArrayList<CommandProvider>();

    public static void registerProvider(CommandProvider p) {
        providers.add(p);
    }

    public static List<CommandProvider> getProviders() {
        return providers;
    }
}
