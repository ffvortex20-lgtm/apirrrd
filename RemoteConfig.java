import org.json.JSONObject;

public final class RemoteConfig {

    public String verAddr;
    public boolean skipResourceDownload;
    public double fakeVersion;

    public static RemoteConfig fromJson(String json) throws Exception {
        JSONObject obj = new JSONObject(json);

        RemoteConfig config = new RemoteConfig();

        config.verAddr = obj.getString("verAddr");
        config.skipResourceDownload =
                obj.getBoolean("skipResourceDownload");
        config.fakeVersion =
                obj.getDouble("fakeVersion");

        return config;
    }
}
