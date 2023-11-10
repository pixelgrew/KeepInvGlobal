package me.itskronx11.keepinvglobal;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class KeepInvGlobal extends JavaPlugin {
    private ActiveAnnouncement currentAnnouncement;
    private FileConfiguration last;
    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        File last = new File(this.getDataFolder(), "last.yml");
        if (!last.exists()) {
            try {
                last.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        this.last = YamlConfiguration.loadConfiguration(last);

        long end = this.last.getLong("last-announcement");
        if (end != 0) {
            this.setCurrentAnnouncement(new ActiveAnnouncement(this, end));
        }

        this.getCommand("keepinv").setExecutor(new KeepInvCommand(this));
        this.getCommand("keepinvstatus").setExecutor(new KeepInvStatusCommand(this));
    }
    public ActiveAnnouncement getCurrentAnnouncement() {
        return this.currentAnnouncement;
    }
    public void setCurrentAnnouncement(ActiveAnnouncement currentAnnouncement) {
        this.currentAnnouncement = currentAnnouncement;
        this.last.set("last-announcement", currentAnnouncement == null ? 0 : currentAnnouncement.getEnd());
        try {
            this.last.save(new File(this.getDataFolder(), "last.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (currentAnnouncement != null)
            currentAnnouncement.start0();
    }
}
