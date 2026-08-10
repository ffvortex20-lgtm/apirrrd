package com.vortex.hub.network;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class VortexConfigDownloader {

    public static String fetchHttpsString(String urlString) throws Exception {
        URL url = new URL(urlString);
        if (!url.getProtocol().equalsIgnoreCase("https")) {
            throw new SecurityException("Apenas conexões HTTPS são permitidas.");
        }

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpsURLConnection.HTTP_OK) {
            throw new Exception("Falha HTTP: Código " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        conn.disconnect();
        return sb.toString();
    }

    public static void saveToCache(Context context, String fileName, String content) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
            writer.close();
            VortexLogger.i("Cache salvo com sucesso: " + fileName);
        } catch (Exception e) {
            VortexLogger.e("Erro ao salvar cache: " + fileName, e);
        }
    }

    public static String readFromCache(Context context, String fileName) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            if (!file.exists()) return null;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        } catch (Exception e) {
            VortexLogger.e("Erro ao ler cache: " + fileName, e);
            return null;
        }
    }
}
