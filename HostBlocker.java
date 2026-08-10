package com.vortex.hub;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public final class HostBlocker {

    private static final Set<String> blockedHosts = new HashSet<>();

    private HostBlocker() {}

    public static void setBlockedHosts(Set<String> hosts) {
        synchronized (blockedHosts) {
            blockedHosts.clear();

            for (String host : hosts) {
                if (host == null) continue;

                String normalized = host
                        .trim()
                        .toLowerCase();

                if (!normalized.isEmpty()) {
                    blockedHosts.add(normalized);
                }
            }
        }
    }

    public static boolean isBlocked(String host) {
        if (host == null) return false;

        String normalized = host
                .trim()
                .toLowerCase();

        synchronized (blockedHosts) {
            if (blockedHosts.contains(normalized)) {
                return true;
            }

            // Também bloqueia subdomínios.
            for (String blocked : blockedHosts) {
                if (normalized.endsWith("." + blocked)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isBlockedUrl(String url) {
        try {
            URI uri = URI.create(url);
            return isBlocked(uri.getHost());
        } catch (Exception e) {
            return false;
        }
    }
}
