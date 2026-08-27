package cz.example.chatitems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Interní příkaz "/citems item <id>" a "/citems inv <id>", který se spouští
 * kliknutím na zprávu v chatu. Otevře hráči GUI s daným itemem/inventářem
 * vycentrovaným uprostřed.
 */
public class ViewCommand implements CommandExecutor {

    private final ChatItemsPlugin plugin;

    public ViewCommand(ChatItemsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player) || args.length != 2) {
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "item" -> openItemView(player, id);
            case "inv" -> openInventoryView(player, id);
            default -> {
            }
        }

        return true;
    }

    private void openItemView(Player player, int id) {
        ItemStack item = plugin.getViewRegistry().getItem(id);
        if (item == null) {
            player.sendMessage(Component.text("Tento náhled itemu už není dostupný.", NamedTextColor.RED));
            return;
        }

        // 3 řádky (27 slotů), item uprostřed = slot 13
        Inventory gui = Bukkit.createInventory(new ChatItemsViewHolder(), 27,
                Component.text("Zobrazený item"));
        gui.setItem(13, item.clone());
        player.openInventory(gui);
    }

    private void openInventoryView(Player player, int id) {
        ItemStack[] contents = plugin.getViewRegistry().getInventory(id);
        if (contents == null) {
            player.sendMessage(Component.text("Tento náhled inventáře už není dostupný.", NamedTextColor.RED));
            return;
        }

        // 6 řádků (54 slotů). contents: 0-8 hotbar, 9-35 hlavní inventář (3 řádky).
        // Vykreslíme to jako normální inventář (3 řádky + hotbar) vycentrované -
        // řádek 0 a řádek 5 zůstanou prázdné jako odsazení.
        Inventory gui = Bukkit.createInventory(new ChatItemsViewHolder(), 54,
                Component.text("Zobrazený inventář"));

        int guiSlot = 9; // start na 2. řádku (index 1), řádek 0 = odsazení

        for (int i = 9; i <= 35; i++) {
            ItemStack item = (i < contents.length) ? contents[i] : null;
            if (item != null) {
                gui.setItem(guiSlot, item.clone());
            }
            guiSlot++;
        }

        for (int i = 0; i <= 8; i++) {
            ItemStack item = (i < contents.length) ? contents[i] : null;
            if (item != null) {
                gui.setItem(guiSlot, item.clone());
            }
            guiSlot++;
        }

        player.openInventory(gui);
    }
}
