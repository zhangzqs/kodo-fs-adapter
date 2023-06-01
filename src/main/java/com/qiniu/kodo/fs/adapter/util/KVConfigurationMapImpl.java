package com.qiniu.kodo.fs.adapter.util;

import java.util.Map;

public class KVConfigurationMapImpl implements IKVConfiguration {
    private final Map<String, String> map;

    public KVConfigurationMapImpl(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String get(String key, String defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }
}
