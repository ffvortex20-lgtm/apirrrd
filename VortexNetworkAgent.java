package com.vortex.hub.network;

import android.content.Context;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VortexNetworkAgent {

    private static VortexNetworkAgent instance;
    private final Context context;
    private final VortexRuleManager ruleManager;
    private final ScheduledExecutorService scheduler;
    private static final String LOCAL_CONFIG_FILE = "localconfig.json";
    private static final String HOSTS_CACHE_FILE = "hosts_cache.json";

    private VortexNetworkAgent(Context context) {
        this.context = context.getApplicationContext();
        this.ruleManager = new VortexRuleManager();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static synchronized VortexNetworkAgent getInstance(Context context) {
        if (instance == null) {
            instance = new VortexNetworkAgent(context);
        }
        return instance;
    }

    public void start(long refreshIntervalMinutes) {
        VortexLogger.i("Iniciando Agente de Rede Vortex Hub...");
        
        // Carrega configurações do Cache Local no Startup
        loadFromCache();

        // Agende atualizações periódicas remota do GitHub
        scheduler.scheduleAtFixedRate(this::syncWithGithub, 0, refreshIntervalMinutes, TimeUnit.MINUTES);
    }

    private void loadFromCache() {
        try {
            String cachedHosts = VortexConfigDownloader.readFromCache(context, HOSTS_CACHE_FILE);
            if (cachedHosts != null) {
                VortexJsonParser.HostsConfig hostsConfig = VortexJsonParser.parseHostsConfig(cachedHosts);
                ruleManager.updateRules(hostsConfig.enabled, hostsConfig.blockedHosts);
                VortexLogger.i("Configurações locais carregadas do cache.");
            }
        } catch (Exception e) {
            VortexLogger.e("Erro ao processar cache local.", e);
        }
    }

    private void syncWithGithub() {
        try {
            VortexLogger.i("Buscando atualizações no GitHub...");
            
            // 1. Ler localconfig.json local / remoto
            String localConfigJson = VortexConfigDownloader.readFromCache(context, LOCAL_CONFIG_FILE);
            if (localConfigJson == null) {
                // Fallback para baixar a versão inicial
                localConfigJson = VortexConfigDownloader.fetchHttpsString("https://raw.githubusercontent.com/ffvortex20-lgtm/apirrrd/main/localconfig.json");
                VortexConfigDownloader.saveToCache(context, LOCAL_CONFIG_FILE, localConfigJson);
            }

            VortexJsonParser.LocalConfig localConfig = VortexJsonParser.parseLocalConfig(localConfigJson);
            
            if (!localConfig.enabled) {
                VortexLogger.w("Módulo desativado via localconfig.json.");
                ruleManager.updateRules(false, null);
                return;
            }

            // 2. Baixar hosts.json atualizado
            String remoteHostsJson = VortexConfigDownloader.fetchHttpsString(localConfig.configUrl);
            VortexJsonParser.HostsConfig hostsConfig = VortexJsonParser.parseHostsConfig(remoteHostsJson);

            // 3. Validação concluída: Atualiza cache e memória
            VortexConfigDownloader.saveToCache(context, HOSTS_CACHE_FILE, remoteHostsJson);
            ruleManager.updateRules(hostsConfig.enabled, hostsConfig.blockedHosts);

        } catch (Exception e) {
            VortexLogger.e("Falha na sincronização. Mantendo últimas regras válidas.", e);
        }
    }

    public boolean isHostBlocked(String host) {
        return ruleManager.shouldBlockHost(host);
    }

    public void stop() {
        scheduler.shutdown();
        VortexLogger.i("Agente de Rede Finalizado.");
    }
}
