package com.johnymuffin.beta.jdetector;

import com.johnymuffin.beta.jdetector.utils.BetaEvolutionsUtils;
import com.projectposeidon.johnymuffin.ConnectionPause;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;

public class JListener implements Listener {
    private JDetector jDetector;

    private BetaEvolutionsUtils betaEvolutionsUtils;

    public JListener(JDetector jDetector) {
        this.jDetector = jDetector;
        this.betaEvolutionsUtils = new BetaEvolutionsUtils(false); //Disable debug for now
    }

    @EventHandler
    public void onPlayerPreLogin(final PlayerPreLoginEvent event) {
        String playerName = event.getName();
        String playerIP = event.getAddress().getHostAddress();

        // Beta Evolutions Check
        ConnectionPause betaEVOConnectionPause = event.addConnectionPause(this.jDetector, "BetaEVO");
        Bukkit.getScheduler().scheduleAsyncDelayedTask(this.jDetector, () -> {
            final BetaEvolutionsUtils.VerificationResults verificationResult = betaEvolutionsUtils.verifyUser(playerName, playerIP);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.jDetector, () -> {
                jDetector.getBetaEVOVerificationResults().put(playerName + "-" + playerIP, verificationResult);
                betaEVOConnectionPause.removeConnectionPause();
            });
        });

        // IPHub Check

        //Check if IP lookup has occurred in the last week
        boolean checkIP = true;

        if (jDetector.getJIPCache().isIPSaved(playerIP)) {
            if (jDetector.getJIPCache().getLastChecked(playerIP) + 604800 > (System.currentTimeMillis()/1000L)) {
                checkIP = false;
            }
        }

        if(!checkIP) {
            jDetector.logger(Level.INFO, "IPHub check skipped for " + playerName + " as IP has been checked in the last week.");
            return;
        }

        ConnectionPause ipHubConnectionPause = event.addConnectionPause(this.jDetector, "IPHub");
        int timeout = 5000;
        String Xkey = this.jDetector.getjSettings().getString("settings.api.key");
        Bukkit.getScheduler().scheduleAsyncDelayedTask(this.jDetector, () -> {
            try {
                String endpointURL = this.jDetector.getjSettings().getConfigString("settings.api.url");
                endpointURL = endpointURL.replace("{player_ip}", playerIP);
                URL myURL = new URL(endpointURL);
                HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-Key", Xkey);
                connection.connect();

                // Check response code
                int responseCode = connection.getResponseCode();

                if (responseCode != 200) {
                    jDetector.logger(Level.WARNING, "IPHub returned a response code of " + responseCode + " for " + playerIP);
                    ipHubConnectionPause.removeConnectionPause();
                    return;
                }

                JSONObject response = null;

                // Read response
                InputStream is = connection.getInputStream();
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    String jsonText = readAll(rd);
                    JSONParser parser = new JSONParser();
                    response = (JSONObject) parser.parse(jsonText);
                } catch (Exception exception) {
                    jDetector.logger(Level.WARNING, "An exception occurred while reading the response from IPHub for " + playerIP);
                    exception.printStackTrace();
                    ipHubConnectionPause.removeConnectionPause();
                } finally {
                    is.close();
                }

                JSONObject finalResponse = response;
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.jDetector, () -> {
                    ipHubConnectionPause.removeConnectionPause();
                    boolean isLikelyProxy = false;
                    if(Integer.parseInt(finalResponse.get("block").toString()) >= 1) {
                        isLikelyProxy = true;
                    }

                    // Save IP information
                    jDetector.getJIPCache().saveIPData(playerIP, isLikelyProxy);
                });


            } catch (Exception exception) {
                ipHubConnectionPause.removeConnectionPause();
                this.jDetector.logger(Level.WARNING, "Error while checking IPHub: ");
                exception.printStackTrace();
            }
        });


    }

    @EventHandler(priority = Event.Priority.Lowest)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        //Check if player is actually allowed to join already
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        String playerIP = event.getAddress().getHostAddress();
        BetaEvolutionsUtils.VerificationResults verificationResult = jDetector.getBetaEVOVerificationResults().get(event.getPlayer().getName() + "-" + playerIP);
        if (verificationResult.getSuccessful() >= 1) {
            this.jDetector.logger(Level.INFO, "Player " + event.getPlayer().getName() + " has been verified by Beta Evolutions and has bypassed VPN checks.");
            return;
        }

        //Check if users is utilizing a VPN
        if (!this.jDetector.getJIPCache().isIPSaved(playerIP)) {
            this.jDetector.logger(Level.WARNING, "Player " + event.getPlayer().getName() + " doesn't have an IP saved in the cache. Likely, we have exceeded our API limit. They will be allowed to join.");
            return;
        }

        if (this.jDetector.getJIPCache().isVPN(playerIP)) {
            this.jDetector.logger(Level.WARNING, "Player " + event.getPlayer().getName() + " has been detected as using a VPN. They will be kicked.");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "VPN detected. Try connecting with Beta Evolutions, bit.ly/BetaEVO");
            return;
        }

        this.jDetector.logger(Level.INFO, "Player " + event.getPlayer().getName() + " has been verified as not using a VPN.");


    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1)
            sb.append((char) cp);
        return sb.toString();
    }


}
