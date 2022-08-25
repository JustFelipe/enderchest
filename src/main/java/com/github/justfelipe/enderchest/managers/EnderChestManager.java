package com.github.justfelipe.enderchest.managers;

import com.github.justfelipe.enderchest.EnderChestPlugin;
import com.github.justfelipe.enderchest.model.EnderChest;
import com.github.justfelipe.enderchest.utils.InventorySerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

@RequiredArgsConstructor
public class EnderChestManager {

    @Getter private final List<EnderChest> enderChestList;

    public void load(Player player) {

        enderChestList.add(
                EnderChest.builder()
                .owner(player.getName())
                .enderChest(EnderChestPlugin.getInstance().getEnderChestDAO().enderChestExists(player.getName()) ?
                        EnderChestPlugin.getInstance().getEnderChestDAO().getEnderChest(player.getName()) : createEnderChest(player))
                .build()
        );
    }

    public void unload(Player player) {

        EnderChest enderChest = find(player.getName());

        enderChestList.remove(enderChest);

        if (enderChest != null)
            EnderChestPlugin.getInstance().getEnderChestDAO().insertOrUpdate(player.getName(), InventorySerialize.toJsonObject(enderChest.getEnderChest()).toString());
    }

    public EnderChest find(String owner) {
        return enderChestList.stream()
                .filter(enderChest -> enderChest.getOwner().equalsIgnoreCase(owner))
                .findFirst()
                .orElse(null);
    }

    public int getEnderChestRows(Player player) {

        if (player.hasPermission("enderchest.6")) return 9*6;

        if (player.hasPermission("enderchest.5")) return 9*5;

        if (player.hasPermission("enderchest.4")) return 9*4;

        return 9*3;
    }

    public Inventory createEnderChest(Player player) {
        return Bukkit.createInventory(null, getEnderChestRows(player), "Baú do Fim de " + player.getName());
    }
}