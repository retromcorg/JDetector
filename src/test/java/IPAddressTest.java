import com.johnymuffin.beta.jdetector.utils.Utilities;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IPAddressTest {

    @Test
    void testValidPublicIp() {
        // List of test IP addresses
        String[] ips = {
                "8.8.8.8",            // Public Google DNS
                "10.1.0.0",           // Private IP
                "172.16.0.1",         // Private IP
                "192.168.1.1",        // Private IP
                "169.254.0.1",        // Link local address
                "127.0.0.1",          // Loopback address
                "224.0.0.1",          // Multicast address
                "255.255.255.255",    // Broadcast address (special case)
                "2001:db8::1",        // IPv6 (should fail in this IPv4 only method)
                "172.217.17.78"       // Public IP (Google)
        };
        // Expected results for whether the IPs are public
        Boolean[] expected = {
                true,   // Public
                false,  // Private
                false,  // Private
                false,  // Private
                false,  // Link local
                false,  // Loopback
                false,  // Multicast
                false,  // Broadcast
                false,  // IPv6 (not handled)
                true    // Public
        };

        // Test each IP address and compare with expected results
        for (int i = 0; i < ips.length; i++) {
            boolean actual = Utilities.isValidPublicIp(ips[i]);
            assertEquals(expected[i], actual, "Test failed for IP: " + ips[i]);
        }
    }

    @Test
    void testValidIPv4Address() {
        // List of test IP addresses
        String[] ips = {
                "192.168.1.1",        // Valid IPv4
                "255.255.255.255",    // Valid IPv4 (Broadcast address)
                "10.0.0.256",         // Invalid IPv4 (256 is out of range)
                "172.16.500.1",       // Invalid IPv4 (500 is out of range)
                "1.2.3",              // Invalid IPv4 (not enough segments)
                "1.2.3.4.5",          // Invalid IPv4 (too many segments)
                "123.456.78.90",      // Invalid IPv4 (456 is out of range)
                "192.168.0.1a",       // Invalid IPv4 (contains letters)
                "2001:db8::1",        // Invalid IPv4 (IPv6 address)
                ""                    // Invalid IPv4 (empty string)
        };
        // Expected results for whether the IPs are valid IPv4
        Boolean[] expected = {
                true,   // Valid
                true,   // Valid
                false,  // Invalid
                false,  // Invalid
                false,  // Invalid
                false,  // Invalid
                false,  // Invalid
                false,  // Invalid
                false,  // Invalid
                false   // Invalid
        };

        // Test each IP address and compare with expected results
        for (int i = 0; i < ips.length; i++) {
            boolean actual = Utilities.isValidIPv4Address(ips[i]);
            assertEquals(expected[i], actual, "Test failed for IP: " + ips[i]);
        }
    }
}
