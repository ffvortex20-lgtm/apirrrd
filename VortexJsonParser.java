package com.vortex.hub.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VortexJsonParser {

    public static class LocalConfig {
        public String version;
        public boolean enabled;
        public String configUrl;
    }

    public static class HostsConfig {
        public boolean enabled;
        public List<String> blockedHosts = new ArrayList<>();
    }

    public static LocalConfig parseLocalConfig(String jsonStr) throws JSONException {
        JSONObject obj = new JSONObject(jsonStr);
        LocalConfig config = new LocalConfig();
        config.version = obj.optString("version", "1.0");
        config.enabled = obj.optBoolean("enabled", false);
        config.configUrl = obj.optString("configUrl", "");

        if (config.configUrl.isEmpty() || !config.configUrl.startsWith("https://")) {
            throw new JSONException("URL de configuração inválida ou não HTTPS.");
        }
        return config;
    }

    public static HostsConfig parseHostsConfig(String jsonStr) throws JSONException {
        JSONObject obj = new JSONObject(jsonStr);
        HostsConfig config = new HostsConfig();
        config.enabled = obj.optBoolean("enabled", false);

        JSONArray arr = obj.optJSONArray("blockedHosts");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                String host = arr.getString(i).trim().toLowerCase();
                if (!host.isEmpty()) {
                    config.blockedHosts.add(host);
                }
            }
        }
        return config;
    }
}
