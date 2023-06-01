package com.qiniu.kodo.fs.adapter.config.download.cache;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class CacheConfig extends AConfigBase {
    public final DiskCacheConfig disk;
    public final MemoryCacheConfig memory;

    public CacheConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.disk = disk();
        this.memory = memory();
    }

    private DiskCacheConfig disk() {
        return new DiskCacheConfig(conf, namespace + ".disk");
    }

    private MemoryCacheConfig memory() {
        return new MemoryCacheConfig(conf, namespace + ".memory");
    }

}
