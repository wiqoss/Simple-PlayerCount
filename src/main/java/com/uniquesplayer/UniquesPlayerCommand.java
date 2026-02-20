package com.uniquesplayer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UniquesPlayerCommand implements CommandExecutor {
    
    private final UniquesPlayer plugin;
    
    public UniquesPlayerCommand(UniquesPlayer plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int count = plugin.getUniquePlayerCount();
        
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Unique Players: " + ChatColor.WHITE + count);
        
        return true;
    }
}
