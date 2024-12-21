//package com.johnymuffin.beta.jdetector.utils;
//
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Scanner;
//
///**
// * Utility class to interact with the IPQualityScore API to check IP quality and fraud score.
// */
//public class IPQualityScoreUtil {
//    private static final String API_URL = "https://ipqualityscore.com/api/json/ip/{key}/{ip}?strictness={strictness}";
//
//    private String[] apiKeys; // Array to hold multiple API keys for rate limiting purposes
//    private int currentKeyIndex = 0; // Index to manage the rotation of API keys
//
//    /**
//     * Constructor for IPQualityScoreUtil class.
//     *
//     * @param apiKeys Array of API keys.
//     * @throws IllegalArgumentException If the apiKeys array is null or empty.
//     */
//    public IPQualityScoreUtil(String[] apiKeys) {
//        if(apiKeys == null || apiKeys.length == 0) throw new IllegalArgumentException("API keys must be provided");
//        this.apiKeys = apiKeys;
//    }
//
//    /**
//     * Retrieves and rotates the API key to be used for API requests.
//     *
//     * @return The next API key to use.
//     */
//    private String getApiKey() {
//        return apiKeys[currentKeyIndex++ % apiKeys.length]; // Rotate the API key if multiple are provided
//    }
//
//    /**
//     * Checks the IP address for its quality and fraud likelihood.
//     *
//     * @param ip The IP address to check.
//     * @param strictness The strictness level for the fraud checks.
//     * @return A Response object containing the results of the IP check.
//     */
//    public Response checkIP(String ip, int strictness) {
//        try {
//            String urlStr = API_URL.replace("{key}", getApiKey()).replace("{ip}", ip).replace("{strictness}", Integer.toString(strictness));
//            URL url = new URL(urlStr);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.connect();
//
//            if (conn.getResponseCode() != 200) {
//                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
//            }
//
//            Scanner sc = new Scanner(url.openStream());
//            StringBuilder jsonStr = new StringBuilder();
//            while (sc.hasNext()) {
//                jsonStr.append(sc.nextLine());
//            }
//            sc.close();
//
//            JSONParser parser = new JSONParser();
//            JSONObject json = (JSONObject) parser.parse(jsonStr.toString());
//            return new Response(json);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static void main(String[] args) {
//        String ips = "94.254.130.7\n" +
//                "136.23.0.98\n" +
//                "31.223.96.239\n" +
//                "192.0.13.91\n" +
//                "83.24.140.211\n" +
//                "207.177.171.37\n" +
//                "185.186.152.226\n" +
//                "5.14.146.79\n" +
//                "109.252.100.172\n" +
//                "116.96.47.239\n" +
//                "212.253.194.193\n" +
//                "84.159.181.168\n" +
//                "31.134.69.101";
//
//        String[] ipsArray = ips.split("\n");
//
//        IPQualityScoreUtil ipQualityScoreUtil = new IPQualityScoreUtil(new String[]{"C3M41xbd2SOJRIRFnpOrtmlsGA0MV5BP"});
//
//        for (String ip : ipsArray) {
//            System.out.println("=====================================");
//            System.out.println("Checking IP: " + ip);
//            System.out.println(ipQualityScoreUtil.checkIP(ip, 1));
//            System.out.println("=====================================");
//        }
//
//    }
//
//    /**
//     * Inner class to encapsulate the response from the IPQualityScore API.
//     */
//    public class Response {
//        private final boolean success;
//        private final String message;
//        private final int fraudScore;
//        private final String countryCode;
//        private final String region;
//        private final String city;
//        private final String isp;
//        private final long asn;
//        private final String organization;
//        private final boolean isCrawler;
//        private final String timezone;
//        private final boolean mobile;
//        private final String host;
//        private final boolean proxy;
//        private final boolean vpn;
//        private final boolean tor;
//        private final boolean activeVPN;
//        private final boolean activeTor;
//        private final boolean recentAbuse;
//        private final boolean botStatus;
//        private final String zipCode;
//        private final Double latitude;
//        private final Double longitude;
//        private final String requestID;
//
//        /**
//         * Constructs a new Response object using data from a JSON object.
//         *
//         * @param json The JSON object containing the API response data.
//         */
//        public Response(JSONObject json) {
//            this.success = (boolean) json.get("success");
//            this.message = (String) json.get("message");
//            this.fraudScore = ((Long) json.get("fraud_score")).intValue();
//            this.countryCode = (String) json.get("country_code");
//            this.region = (String) json.get("region");
//            this.city = (String) json.get("city");
//            this.isp = (String) json.get("ISP");
//            this.asn = (long) json.get("ASN");
//            this.organization = (String) json.get("organization");
//            this.isCrawler = (boolean) json.get("is_crawler");
//            this.timezone = (String) json.get("timezone");
//            this.mobile = (boolean) json.get("mobile");
//            this.host = (String) json.get("host");
//            this.proxy = (boolean) json.get("proxy");
//            this.vpn = (boolean) json.get("vpn");
//            this.tor = (boolean) json.get("tor");
//            this.activeVPN = (boolean) json.get("active_vpn");
//            this.activeTor = (boolean) json.get("active_tor");
//            this.recentAbuse = (boolean) json.get("recent_abuse");
//            this.botStatus = (boolean) json.get("bot_status");
//            this.zipCode = String.valueOf(json.get("zip_code"));
//            this.latitude = Double.valueOf(String.valueOf(json.get("latitude")));
//            this.longitude = Double.valueOf(String.valueOf(json.get("longitude")));
//            this.requestID = (String) json.get("request_id");
//        }
//
//        public boolean isSuccess() {
//            return success;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        public int getFraudScore() {
//            return fraudScore;
//        }
//
//        public String getCountryCode() {
//            return countryCode;
//        }
//
//        public String getRegion() {
//            return region;
//        }
//
//        public String getCity() {
//            return city;
//        }
//
//        public String getIsp() {
//            return isp;
//        }
//
//        public long getAsn() {
//            return asn;
//        }
//
//        public String getOrganization() {
//            return organization;
//        }
//
//        public boolean isCrawler() {
//            return isCrawler;
//        }
//
//        public String getTimezone() {
//            return timezone;
//        }
//
//        public boolean isMobile() {
//            return mobile;
//        }
//
//        public String getHost() {
//            return host;
//        }
//
//        public boolean isProxy() {
//            return proxy;
//        }
//
//        public boolean isVpn() {
//            return vpn;
//        }
//
//        public boolean isTor() {
//            return tor;
//        }
//
//        public boolean isActiveVPN() {
//            return activeVPN;
//        }
//
//        public boolean isActiveTor() {
//            return activeTor;
//        }
//
//        public boolean isRecentAbuse() {
//            return recentAbuse;
//        }
//
//        public boolean isBotStatus() {
//            return botStatus;
//        }
//
//        public String getZipCode() {
//            return zipCode;
//        }
//
//        public Double getLatitude() {
//            return latitude;
//        }
//
//        public Double getLongitude() {
//            return longitude;
//        }
//
//        public String getRequestID() {
//            return requestID;
//        }
//
//        @Override
//        public String toString() {
//            return "Response{" +
//                    "success=" + success +
//                    ", message='" + message + '\'' +
//                    ", fraudScore=" + fraudScore +
//                    ", countryCode='" + countryCode + '\'' +
//                    ", region='" + region + '\'' +
//                    ", city='" + city + '\'' +
//                    ", isp='" + isp + '\'' +
//                    ", asn=" + asn +
//                    ", organization='" + organization + '\'' +
//                    ", isCrawler=" + isCrawler +
//                    ", timezone='" + timezone + '\'' +
//                    ", mobile=" + mobile +
//                    ", host='" + host + '\'' +
//                    ", proxy=" + proxy +
//                    ", vpn=" + vpn +
//                    ", tor=" + tor +
//                    ", activeVPN=" + activeVPN +
//                    ", activeTor=" + activeTor +
//                    ", recentAbuse=" + recentAbuse +
//                    ", botStatus=" + botStatus +
//                    ", zipCode='" + zipCode + '\'' +
//                    ", latitude='" + latitude + '\'' +
//                    ", longitude='" + longitude + '\'' +
//                    ", requestID='" + requestID + '\'' +
//                    '}';
//        }
//    }
//
//}
