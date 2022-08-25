package com.github.justfelipe.enderchest.listeners;

import com.github.justfelipe.enderchest.EnderChestPlugin;
import com.github.justfelipe.enderchest.model.EnderChest;
import com.github.justfelipe.enderchest.utils.InventorySerialize;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EnderChestListeners implements Listener {

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        EnderChestPlugin.getInstance().getEnderChestManager().load(event.getPlayer());
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        EnderChestPlugin.getInstance().getEnderChestManager().unload(event.getPlayer());
    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction().name().toLowerCase().contains("right_click_block")) {

            if (event.getClickedBlock().getType().name().toLowerCase().contains("ender_chest")) {

                event.setCancelled(true);

                Player player = event.getPlayer();

                EnderChest enderChest = EnderChestPlugin.getInstance().getEnderChestManager().find(player.getName());

                if (enderChest == null) {
                    player.sendMessage("§cVocê não está em nosso banco de dados, relogue e tente novamente.");
                    return;
                }

                if (enderChest.getDelay() > System.currentTimeMillis()) {
                    player.sendMessage("§cAguarde alguns segundos para abrir o seu Baú do Fim novamente.");
                    return;
                }

                if (enderChest.getEnderChest().getSize() != EnderChestPlugin.getInstance().getEnderChestManager().getEnderChestRows(player)) {

                    Inventory oldEnderChest = enderChest.getEnderChest();
                    Inventory newEnderChest = EnderChestPlugin.getInstance().getEnderChestManager().createEnderChest(player);

                    Arrays.stream(oldEnderChest.getContents())
                            .filter(Objects::nonNull)
                            .forEach(content -> {

                                if (newEnderChest.firstEmpty() != -1)
                                    newEnderChest.addItem(content);
                                else
                                    player.getWorld().dropItemNaturally(player.getLocation(), content);
                            });

                    enderChest.setEnderChest(newEnderChest);
                }

                player.openInventory(enderChest.getEnderChest());
            }
        }
    }

    @EventHandler
    void onClose(InventoryCloseEvent event) {

        Inventory inventory = event.getInventory();

        if (inventory.getName().startsWith("Baú do Fim de ")) {

            Player player = (Player) event.getPlayer();

            String owner = inventory.getName().split("de ")[1];

            EnderChest enderChest = EnderChestPlugin.getInstance().getEnderChestManager().find(owner);

            player.getWorld().playSound(player.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);

            if (enderChest == null)
                Bukkit.getScheduler().runTaskAsynchronously(EnderChestPlugin.getInstance(), () ->
                        EnderChestPlugin.getInstance().getEnderChestDAO().insertOrUpdate(owner, InventorySerialize.toJsonObject(inventory).toString()));
            else
                enderChest.setDelay(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));
        }
    }
}
