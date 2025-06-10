package io.shrouded.util;

import org.slf4j.MDC;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MDCUtil {

    private static final AtomicInteger threadCounter = new AtomicInteger(1);

    public static ThreadFactory mdcThreadFactory(String namePrefix) {
        return runnable -> {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            String threadName = namePrefix + "-" + threadCounter.getAndIncrement();

            return new Thread(() -> {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                } else {
                    MDC.clear();
                }

                try {
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            }, threadName);
        };
    }


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

    public static void addSessionId() {
        put("sessionId", UUID.randomUUID().toString());
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
