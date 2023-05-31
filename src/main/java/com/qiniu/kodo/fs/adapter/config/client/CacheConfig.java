package com.qiniu.kodo.fs.adapter.config.client;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.config.IQiniuConfiguration;

public class CacheConfig extends AConfigBase {
    public final boolean enable;
    public final int maxCapacity;

    public CacheConfig(IQiniuConfiguration conf, String namespace) {
        super(conf, namespace);
        this.enable = enable();
        this.maxCapacity = maxCapacity();
    }

    private boolean enable() {
        return conf.getBoolean(namespace + ".enable", true);
    }

    private int maxCapacity() {
        return conf.getInt(namespace + ".maxCapacity", 100);
    }
}
