package cz.example.chatitems;

import org.bukkit.plugin.java.JavaPlugin;

public final class ChatItemsPlugin extends JavaPlugin {

    private final ViewRegistry viewRegistry = new ViewRegistry();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new ViewGuiListener(), this);
        getCommand("citems").setExecutor(new ViewCommand(this));
        getLogger().info("ChatItems plugin zapnut. Triggery: [i] [item] [inv] [inventory]");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatItems plugin vypnut.");
    }

    public ViewRegistry getViewRegistry() {
        return viewRegistry;
    }
}
