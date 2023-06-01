package com.qiniu.kodo.fs.adapter.config.download.cache;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class MemoryCacheConfig extends AConfigBase {
    public final boolean enable;
    public final int blocks;

    public MemoryCacheConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.blocks = blocks();
        this.enable = enable();
    }

    private boolean enable() {
        return conf.getBoolean(namespace + ".enable", true);
    }

    /**
     * 读取文件时内存LRU缓冲区的最大块数量
     */
    private int blocks() {
        return conf.getInt(namespace + ".blocks", 25);
    }

}
