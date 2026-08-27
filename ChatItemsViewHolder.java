package cz.example.chatitems;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Značkovací holder - podle něj poznáme, že otevřené GUI patří ChatItems
 * (slouží jen k zobrazení, ne k reálné manipulaci s itemy).
 */
public class ChatItemsViewHolder implements InventoryHolder {

    @Override
    public Inventory getInventory() {
        return null;
    }
}
