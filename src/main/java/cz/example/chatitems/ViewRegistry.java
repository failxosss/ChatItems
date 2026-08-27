package cz.example.chatitems;

import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dočasné úložiště "snapshotů" itemů a inventářů, na které odkazují
 * klikací zprávy v chatu. Drží se jen v paměti (přežije restart? Ne),
 * a nejstarší záznamy se automaticky mažou, aby se plugin nezacpal.
 */
public class ViewRegistry {

    private static final int MAX_ENTRIES = 500;

    private final AtomicInteger counter = new AtomicInteger(0);

    private final Map<Integer, ItemStack> items = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, ItemStack> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    private final Map<Integer, ItemStack[]> inventories = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, ItemStack[]> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    public int storeItem(ItemStack item) {
        int id = counter.incrementAndGet();
        items.put(id, item.clone());
        return id;
    }

    public int storeInventory(ItemStack[] contents) {
        int id = counter.incrementAndGet();
        ItemStack[] snapshot = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            snapshot[i] = contents[i] == null ? null : contents[i].clone();
        }
        inventories.put(id, snapshot);
        return id;
    }

    public ItemStack getItem(int id) {
        return items.get(id);
    }

    public ItemStack[] getInventory(int id) {
        return inventories.get(id);
    }
}
