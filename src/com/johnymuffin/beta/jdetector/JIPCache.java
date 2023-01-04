package com.johnymuffin.beta.jdetector;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public class JIPCache {
    private JDetector plugin;
    private JSONObject ipCacheJSON;
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
                ipCacheJSON = new JSONObject();
                file.write(ipCacheJSON.toJSONString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isNew = true;
        }

        try {
            plugin.logger(Level.INFO, "Reading ipCache.json file");
            JSONParser parser = new JSONParser();
            ipCacheJSON = (JSONObject) parser.parse(new FileReader(cacheFile));
        } catch (ParseException e) {
            plugin.logger(Level.WARNING, "ipCache.json file is corrupt, resetting file: " + e + " : " + e.getMessage());
            ipCacheJSON = new JSONObject();
        } catch (Exception e) {
            plugin.logger(Level.WARNING, "ipCache.json file is corrupt, changing to memory only mode.");
            memoryOnly = true;
            ipCacheJSON = new JSONObject();
        }
        saveData();

    }

    public void saveIPData(String ip, boolean vpn) {
        JSONObject ipData = new JSONObject();
        ipData.put("vpn", vpn);
        ipData.put("lastChecked", (System.currentTimeMillis()/1000L));
        ipCacheJSON.put(ip, ipData);
        this.saveData();
    }

    public boolean isIPSaved(String ip) {
        return ipCacheJSON.containsKey(ip);
    }

    public boolean isVPN(String ip) {
        return Boolean.valueOf(String.valueOf(((JSONObject) ipCacheJSON.get(ip)).get("vpn")));
    }

    public long getLastChecked(String ip) {
        return Long.valueOf(String.valueOf(((JSONObject) ipCacheJSON.get(ip)).get("lastChecked")));
    }

    public void saveData() {
        saveJsonArray();
    }

    private void saveJsonArray() {
        if (memoryOnly) {
            return;
        }
        try (FileWriter file = new FileWriter(cacheFile)) {
            plugin.logger(Level.INFO, "Saving ipCache.json");
            file.write(ipCacheJSON.toJSONString());
            file.flush();
        } catch (IOException e) {
            plugin.logger(Level.WARNING, "Error saving ipCache.json: " + e + " : " + e.getMessage());
        }
    }

}
