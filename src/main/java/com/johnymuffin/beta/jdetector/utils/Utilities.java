package com.johnymuffin.beta.jdetector.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utilities {
    private static final Logger LOGGER = Logger.getLogger(Utilities.class.getName());

    /**
     * Validates whether a given IP address is a public IP.
     *
     * @param ip The IP address to validate.
     * @return true if the IP is a valid public IP, false if it is not.
     */
    public static boolean isValidPublicIp(String ip) {
        //Credit: h.j.k (https://codereview.stackexchange.com/questions/65071/test-if-given-ip-is-a-public-one)

        if(ip == null || ip.isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }

        if(!isValidIPv4Address(ip)) {
            LOGGER.log(Level.WARNING, "Invalid IP address: " + ip);
            return false;
        }

        if(ip.equals("255.255.255.255")) {
            // Broadcast address is a special case
            return false;
        }

        Inet4Address address;
        try {
            address = (Inet4Address) InetAddress.getByName(ip);
        } catch (UnknownHostException exception) {
            // Log this exception along with the IP that caused it
            LOGGER.log(Level.WARNING, "Failed to resolve IP address: " + ip, exception);
            return false;
        } catch (ClassCastException exception) {
            // This will catch the case where the IP address is IPv6 instead of IPv4
            LOGGER.log(Level.SEVERE, "IP address is not IPv4: " + ip, exception);
            return false;
        }

        // Check if the IP is not any of the private/local types
        return !(address.isSiteLocalAddress() ||
                address.isAnyLocalAddress() ||
                address.isLinkLocalAddress() ||
                address.isLoopbackAddress() ||
                address.isMulticastAddress());
    }

    /**
     * Validates whether a given string is a valid IPv4 address.
     *
     * @param ip The string to validate.
     * @return true if the string is a valid IPv4 address, false otherwise.
     */
    public static boolean isValidIPv4Address(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        for (String segment : parts) {
            int i;
            try {
                i = Integer.parseInt(segment);
            } catch (NumberFormatException nfe) {
                return false;
            }

            if (i < 0 || i > 255) {
                return false;
            }
        }

        return true;
    }
}
