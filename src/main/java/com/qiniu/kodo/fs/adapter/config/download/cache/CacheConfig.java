package com.qiniu.kodo.fs.adapter.config.download.cache;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.config.IQiniuConfiguration;

public class CacheConfig extends AConfigBase {
    public final DiskCacheConfig disk;
    public final MemoryCacheConfig memory;

    public CacheConfig(IQiniuConfiguration conf, String namespace) {
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
