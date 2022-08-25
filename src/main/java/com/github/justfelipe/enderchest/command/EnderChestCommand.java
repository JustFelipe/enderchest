package com.github.justfelipe.enderchest.command;

import com.github.justfelipe.enderchest.EnderChestPlugin;
import com.github.justfelipe.enderchest.model.EnderChest;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;

public class EnderChestCommand {

    @Command(
            name = "enderchest",
            aliases = {"ec"},
            target = CommandTarget.PLAYER,
            async = true
    )
    public void enderChestCommand(Context<Player> context, @Optional OfflinePlayer target) {

        Player player = context.getSender();

        if (target == null) {

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
            return;
        }

        if (!player.hasPermission("enderchest.admin")) {
            player.sendMessage("§cVocê não tem permissão para executar este comando.");
            return;
        }

        EnderChest enderChest = EnderChestPlugin.getInstance().getEnderChestManager().find(target.getName());

        if (target.isOnline()) {

            if (!EnderChestPlugin.getInstance().getEnderChestDAO().enderChestExists(target.getName())) {
                player.sendMessage("§cEste jogador não existe em nosso banco de dados.");
                return;
            }

            player.openInventory(enderChest.getEnderChest());
            return;
        }

        if (!EnderChestPlugin.getInstance().getEnderChestDAO().enderChestExists(target.getName())) {
            player.sendMessage("§cEste jogador não existe em nosso banco de dados.");
            return;
        }

        player.openInventory(EnderChestPlugin.getInstance().getEnderChestDAO().getEnderChest(target.getName()));
    }
}