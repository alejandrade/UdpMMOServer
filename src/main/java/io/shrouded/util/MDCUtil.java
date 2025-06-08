package io.shrouded.util;

import org.slf4j.MDC;

import java.net.InetSocketAddress;

public class MDCUtil {
    public static void put(String key, String value) {
        if (key != null && value != null) {
            MDC.put(key, value);
        }
    }

    public static void remove(String key) {
        if (key != null) {
            MDC.remove(key);
        }
    }

    public static void clear() {
        MDC.clear();
    }

    public static void addRequestId(String requestId) {
        put("requestId", requestId);
    }

    public static void addClient(InetSocketAddress address) {
        if (address == null) return;
        put("client.ip", address.getAddress() != null ? address.getAddress().getHostAddress() : null);
        put("client.port", String.valueOf(address.getPort()));
    }
}
