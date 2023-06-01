package com.qiniu.kodo.fs.adapter.util;

public interface IKVConfiguration {
    String get(String key, String defaultValue);

    default String get(String key) {
        return get(key, null);
    }
    default String[] getStrings(String key, String... defaultValue) {
        String value = get(key);
        if (value == null) return defaultValue;
        return value.split(",");
    }
    default int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }
    default boolean getBoolean(String key, boolean defaultValue) {
        return get(key, defaultValue ? "true" : "false").equals("true");
    }
}
