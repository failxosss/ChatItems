package cz.example.chatitems;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Zamyká GUI okna ChatItems na "jen ke čtení" - hráč si nemůže itemy
 * odebrat, přemístit ani do okna nic vložit.
 */
public class ViewGuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof ChatItemsViewHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof ChatItemsViewHolder) {
            event.setCancelled(true);
        }
    }
}
