package cz.example.chatitems;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;
import java.util.Set;

public class ChatListener implements Listener {

    // Sem klidně přidej / uber aliasy, na které má plugin reagovat.
    private static final Set<String> ITEM_TAGS = Set.of("[i]", "[item]");
    private static final Set<String> INV_TAGS = Set.of("[inv]", "[inventory]");

    private final ChatItemsPlugin plugin;

    public ChatListener(ChatItemsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncChatEvent event) {
        String plain = PlainTextComponentSerializer.plainText()
                .serialize(event.message())
                .trim();
        String lower = plain.toLowerCase(Locale.ROOT);

        // Chat event běží na async threadu, práci s inventářem a broadcastem
        // proto přehazujeme zpět na hlavní vlákno serveru.
        if (ITEM_TAGS.contains(lower)) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> shareHeldItem(event.getPlayer()));
            return;
        }

        if (INV_TAGS.contains(lower)) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(plugin, () -> shareInventory(event.getPlayer()));
        }
    }

    private void shareHeldItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            player.sendMessage(Component.text("Nic v ruce nedržíš.", NamedTextColor.RED));
            return;
        }

        int id = plugin.getViewRegistry().storeItem(item);

        Component clickPart = Component.text("[Klikni pro zobrazení]", NamedTextColor.GREEN)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.runCommand("/citems item " + id))
                .hoverEvent(item.asHoverEvent());

        Component message = Component.text()
                .append(Component.text(player.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" ukazuje item ", NamedTextColor.GRAY))
                .append(itemNameComponent(item))
                .append(Component.text(" ", NamedTextColor.GRAY))
                .append(clickPart)
                .build();

        Bukkit.broadcast(message);
    }

    private void shareInventory(Player player) {
        // getStorageContents() = hotbar (0-8) + hlavní inventář (9-35), bez armoru a offhandu.
        ItemStack[] contents = player.getInventory().getStorageContents();
        int id = plugin.getViewRegistry().storeInventory(contents);

        Component clickPart = Component.text("[Klikni pro zobrazení]", NamedTextColor.GREEN)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.runCommand("/citems inv " + id));

        Component message = Component.text()
                .append(Component.text(player.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" ukazuje svůj inventář ", NamedTextColor.GRAY))
                .append(clickPart)
                .build();

        Bukkit.broadcast(message);
    }

    private Component itemNameComponent(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        Component baseName;

        if (meta != null && meta.hasDisplayName()) {
            baseName = meta.displayName();
        } else {
            String material = item.getType().name().toLowerCase(Locale.ROOT).replace('_', ' ');
            baseName = Component.text(material);
        }

        return Component.text()
                .append(Component.text("[", NamedTextColor.DARK_AQUA))
                .append(baseName.colorIfAbsent(NamedTextColor.AQUA))
                .append(Component.text(" x" + item.getAmount() + "]", NamedTextColor.DARK_AQUA))
                .build();
    }
}
