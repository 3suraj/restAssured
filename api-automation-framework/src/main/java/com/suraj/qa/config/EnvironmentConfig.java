package com.suraj.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EnvironmentConfig {

    private static final Properties props = new Properties();

    static {
        // -Denv=qa  or  -Denv=uat  on the command line
        String env = System.getProperty("env", "qa");
        String path = "/config/" + env + "/application.properties";
        try (InputStream is = EnvironmentConfig.class.getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("Config not found: " + path);
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config for env: " + env, e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String baseUrl()  { return get("base.url"); }
    public static String env()      { return get("env"); }
    public static long   slaMs()    { return Long.parseLong(get("sla.response.ms")); }
}
