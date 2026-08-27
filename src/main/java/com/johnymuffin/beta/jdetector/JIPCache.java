package com.johnymuffin.beta.jdetector;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public class JIPCache {
    private JDetector plugin;
    private JsonObject ipCacheJSON;
    private File cacheFile;
    private boolean memoryOnly = false;

    public JIPCache(JDetector plugin) {
        this.plugin = plugin;
        cacheFile = new File(plugin.getDataFolder() + File.separator + "cache" + File.separator + "ipCache.json");
        boolean isNew = false;
        if (!cacheFile.exists()) {
            cacheFile.getParentFile().mkdirs();
            try {
                FileWriter file = new FileWriter(cacheFile);
                plugin.logger(Level.INFO, "Generating ipCache.json file");
                ipCacheJSON = new JsonObject();
                file.write(ipCacheJSON.toString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isNew = true;
        }

        try {
            plugin.logger(Level.INFO, "Reading ipCache.json file");
            ipCacheJSON = (JsonObject) JsonParser.parseReader(new FileReader(cacheFile));
        } catch (JsonParseException e) {
            plugin.logger(Level.WARNING, "ipCache.json file is corrupt, resetting file: " + e + " : " + e.getMessage());
            ipCacheJSON = new JsonObject();
        } catch (Exception e) {
            plugin.logger(Level.WARNING, "ipCache.json file is corrupt, changing to memory only mode.");
            memoryOnly = true;
            ipCacheJSON = new JsonObject();
        }
    }

    public synchronized void saveIPData(String ip, boolean vpn) {
        JsonObject ipData = new JsonObject();
        ipData.addProperty("vpn", vpn);
        ipData.addProperty("lastChecked", (System.currentTimeMillis()/1000L));
        ipCacheJSON.add(ip, ipData);
    }

    public synchronized boolean isIPSaved(String ip) {
        return ipCacheJSON.has(ip);
    }

    public synchronized boolean isVPN(String ip) {
        return ipCacheJSON.get(ip).getAsJsonObject().get("vpn").getAsBoolean();
    }

    public synchronized long getLastChecked(String ip) {
        return ipCacheJSON.get(ip).getAsJsonObject().get("lastChecked").getAsLong();
    }

    public synchronized void saveData() {
        if (memoryOnly) {
            return;
        }
        try (FileWriter file = new FileWriter(cacheFile)) {
            plugin.logger(Level.INFO, "Saving ipCache.json");
            file.write(ipCacheJSON.toString());
            file.flush();
        } catch (IOException e) {
            plugin.logger(Level.WARNING, "Error saving ipCache.json: " + e + " : " + e.getMessage());
        }
    }
}
