package be.isach.ultracosmetics.manager;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.treasurechests.TreasureChest;
import be.isach.ultracosmetics.cosmetics.treasurechests.TreasureChestDesign;
import be.isach.ultracosmetics.util.Cuboid;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Sacha on 11/11/15.
 */
public class TreasureChestManager implements Listener {

    private static Random random = new Random();

    private static void openTreasureChest(Player player) {
        String designPath = getRandomDesign();
        player.closeInventory();
        new TreasureChest(player.getUniqueId(), new TreasureChestDesign(designPath));
    }

    private static String getRandomDesign() {
        Set<String> set = Core.config.getConfigurationSection("TreasureChests.Designs").getKeys(false);
        List<String> list = new ArrayList<>();
        list.addAll(set);
        return list.get(random.nextInt(set.size()));
    }

    public static void tryOpenChest(Player player) {
        if (Core.getCustomPlayer(player).getKeys() > 0) {
            Cuboid c = new Cuboid(player.getLocation().add(-2, 0, -2), player.getLocation().add(2, 1, 2));
            if (!c.isEmpty()) {
                player.sendMessage(MessageManager.getMessage("Chest-Not-Enough-Space"));
                return;
            }
            for (Entity ent : player.getNearbyEntities(5, 5, 5)) {
                if (ent instanceof Player && Core.getCustomPlayer((Player) ent).currentTreasureChest != null) {
                    player.closeInventory();
                    player.sendMessage(MessageManager.getMessage("Too-Close-To-Other-Chest"));
                    return;
                }
            }
            if (!player.isOnGround()) {
                player.sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-On-Ground"));
                return;
            }
            Core.getCustomPlayer(player).removeKey();
            openTreasureChest(player);
        } else {
            player.closeInventory();
            Core.getCustomPlayer(player).openKeyPurchaseMenu();
        }
    }

    @EventHandler
    public void openChest(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()
                && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Treasure-Chests"))) {
            if (!Core.vaultLoaded && Core.getCustomPlayer((Player) event.getWhoClicked()).getKeys() == 0) {
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ANVIL_LAND, 0.2f, 1.2f);
                return;
            }
            Player player = (Player) event.getWhoClicked();
            tryOpenChest(player);
        }
    }

    @EventHandler
    public void buyKeyOpenInv(InventoryClickEvent event) {
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()
                && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Treasure-Keys"))) {
            if (!Core.vaultLoaded && Core.getCustomPlayer((Player) event.getWhoClicked()).getKeys() == 0) {
                ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ANVIL_LAND, 0.2f, 1.2f);
                return;
            }
            event.getWhoClicked().closeInventory();
            Core.getCustomPlayer((Player) event.getWhoClicked()).openKeyPurchaseMenu();
        }
    }

    @EventHandler
    public void buyKeyConfirm(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equalsIgnoreCase(MessageManager.getMessage("Buy-Treasure-Key"))) return;
        event.setCancelled(true);
        if (event.getCurrentItem() != null
                && event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasDisplayName()) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Purchase"))) {
                if (Core.getCustomPlayer((Player) event.getWhoClicked()).getBalance() >= (int) SettingsManager.getConfig().get("TreasureChests.Key-Price")) {
                    Core.economy.withdrawPlayer((Player) event.getWhoClicked(), (int) SettingsManager.getConfig().get("TreasureChests.Key-Price"));
                    Core.getCustomPlayer((Player) event.getWhoClicked()).addKey();
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Successful-Purchase"));
                    event.getWhoClicked().closeInventory();
                    MainMenuManager.openMenu((Player) event.getWhoClicked());
                } else {
                    event.getWhoClicked().sendMessage(MessageManager.getMessage("Not-Enough-Money"));
                    event.getWhoClicked().closeInventory();
                    return;
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(MessageManager.getMessage("Cancel"))) {
                event.getWhoClicked().closeInventory();
                MainMenuManager.openMenu((Player) event.getWhoClicked());
            }
        }
    }

}
