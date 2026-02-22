package com.uniquesplayer;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UniquesPlayerCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playersCount = String.valueOf(UniquesPlayer.getPlugin(UniquesPlayer.class).getUniquePlayerCount());

        // Create message
        TextComponent countMessage = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD +
                "Unique Players: " + ChatColor.WHITE + playersCount);

        // Set hover event
        countMessage.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text(String.join(", ", getUniquePlayersString()))
        ));

        // Send it
        sender.spigot().sendMessage(countMessage);
        return true;
    }

    /**
     * Unique players' nicknames
     * @return A list with unique players
     */
    private List<String> getUniquePlayersString() {
        Set<UUID> uniquePlayers = UniquesPlayer.getPlugin(UniquesPlayer.class).getUniquePlayers();
        return uniquePlayers.stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).toList();
    }

    // TODO: It's better to not touch the weight Bukkit::getOfflinePlayer method and just storage the nicknames, isn't it?
    //  Also switching online-mode to false usually creates new UUIDs
}
