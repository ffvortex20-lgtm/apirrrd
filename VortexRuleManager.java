package com.vortex.hub.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VortexRuleManager {

    private final Set<String> blockedHosts = Collections.synchronizedSet(new HashSet<>());
    private boolean isEnabled = false;

    public void updateRules(boolean enabled, List<String> hosts) {
        this.isEnabled = enabled;
        this.blockedHosts.clear();
        if (hosts != null) {
            for (String host : hosts) {
                this.blockedHosts.add(host.toLowerCase());
            }
        }
        VortexLogger.i("Regras atualizadas. Total de hosts bloqueados: " + blockedHosts.size() + " | Status: " + isEnabled);
    }

    public boolean shouldBlockHost(String targetHost) {
        if (!isEnabled || targetHost == null || targetHost.isEmpty()) {
            return false;
        }

        String hostLower = targetHost.toLowerCase();
        
        // Verificação exata e por subdomínio
        for (String blocked : blockedHosts) {
            if (hostLower.equals(blocked) || hostLower.endsWith("." + blocked)) {
                VortexLogger.w("Conexão Bloqueada para: " + targetHost);
                return true;
            }
        }
        return false;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Set<String> getActiveRules() {
        return Collections.unmodifiableSet(blockedHosts);
    }
}
