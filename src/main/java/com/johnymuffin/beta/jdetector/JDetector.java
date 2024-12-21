package com.johnymuffin.beta.jdetector;

import com.johnymuffin.beta.jdetector.utils.BetaEvolutionsUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDetector extends JavaPlugin {

    //Basic Plugin Info
    private static JDetector plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;

    //IPCache
    private JIPCache JIPCache;

    private JSettings jSettings;

    private HashMap<String, BetaEvolutionsUtils.VerificationResults> betaEVOVerificationResults = new HashMap<>();


    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());

        this.JIPCache = new JIPCache(plugin);
        this.jSettings = new JSettings(new File(this.getDataFolder(), "config.yml"));

        JListener jListener = new JListener(this);
        Bukkit.getPluginManager().registerEvents(jListener, this);
    }

    @Override
    public void onDisable() {
        log.info("[" + pluginName + "] Is Disabling, Version: " + pdf.getVersion());
        JIPCache.saveData();
    }

    public JSettings getjSettings() {
        return jSettings;
    }

    public HashMap<String, BetaEvolutionsUtils.VerificationResults> getBetaEVOVerificationResults() {
        return betaEVOVerificationResults;
    }

    public com.johnymuffin.beta.jdetector.JIPCache getJIPCache() {
        return JIPCache;
    }

    public void logger(Level level, String message) {
        Bukkit.getLogger().log(level, "[" + pluginName + "] " + message);
    }
}
