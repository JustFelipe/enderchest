package com.github.justfelipe.enderchest;

import com.github.justfelipe.enderchest.command.EnderChestCommand;
import com.github.justfelipe.enderchest.dao.EnderChestDAO;
import com.github.justfelipe.enderchest.listeners.EnderChestListeners;
import com.github.justfelipe.enderchest.managers.EnderChestManager;
import com.github.justfelipe.enderchest.utils.InventorySerialize;
import com.google.common.collect.Lists;
import lombok.Getter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class EnderChestPlugin extends JavaPlugin {

    @Getter private static EnderChestPlugin instance;

    private EnderChestDAO enderChestDAO;

    private EnderChestManager enderChestManager;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {

        try {

            enderChestDAO = new EnderChestDAO(
                    getConfig().getString("MySQL.url"),
                    getConfig().getString("MySQL.username"),
                    getConfig().getString("MySQL.password")
            );

            enderChestManager = new EnderChestManager(
                    Lists.newArrayList()
            );

            BukkitFrame bukkitFrame = new BukkitFrame(this);
            bukkitFrame.registerCommands(new EnderChestCommand());

            Bukkit.getPluginManager().registerEvents(new EnderChestListeners(), this);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            getLogger().severe("Ocorreu um erro na inicialização do plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

        enderChestManager.getEnderChestList()
                .forEach(enderChest -> enderChestDAO.insertOrUpdate(enderChest.getOwner(), InventorySerialize.toJsonObject(enderChest.getEnderChest()).toString()));

        enderChestDAO.shutdown();
    }
}