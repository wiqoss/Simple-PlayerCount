package com.uniquesplayer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class UniquesPlayer extends JavaPlugin implements Listener {
    
    private Set<UUID> uniquePlayers;
    private File dataFile;
    private FileConfiguration dataConfig;
    private WebServer webServer;
    private AtomicBoolean needsSave = new AtomicBoolean(false);
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        uniquePlayers = ConcurrentHashMap.newKeySet();
        loadData();
        
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("uniquesplayer").setExecutor(new UniquesPlayerCommand(this));
        
        int port = getConfig().getInt("web-server.port", 19069);
        webServer = new WebServer(this, port);
        webServer.start();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (needsSave.compareAndSet(true, false)) {
                    saveDataSync();
                }
            }
        }.runTaskTimerAsynchronously(this, 600L, 600L);
        
        getLogger().info("UniquesPlayer enabled! Web server on port " + port);
    }
    
    @Override
    public void onDisable() {
        if (webServer != null) {
            webServer.stop();
        }
        if (needsSave.get()) {
            saveDataSync();
        }
        getLogger().info("UniquesPlayer disabled!");
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (uniquePlayers.add(uuid)) {
            needsSave.set(true);
        }
    }
    
    public int getUniquePlayerCount() {
        return uniquePlayers.size();
    }
    
    public Set<UUID> getUniquePlayers() {
        return Collections.unmodifiableSet(uniquePlayers);
    }
    
    private void loadData() {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Could not create data.yml!");
            }
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        if (dataConfig.contains("unique-players")) {
            for (String uuidString : dataConfig.getStringList("unique-players")) {
                try {
                    uniquePlayers.add(UUID.fromString(uuidString));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        
        getLogger().info("Loaded " + uniquePlayers.size() + " unique players.");
    }
    
    private void saveDataSync() {
        dataConfig.set("unique-players", uniquePlayers.stream()
                .map(UUID::toString)
                .toList());
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getLogger().severe("Could not save data.yml!");
        }
    }
}
