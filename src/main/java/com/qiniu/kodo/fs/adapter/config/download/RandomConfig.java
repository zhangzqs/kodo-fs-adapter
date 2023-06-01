package com.qiniu.kodo.fs.adapter.config.download;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class RandomConfig extends AConfigBase {
    public final boolean enable;
    public final int blockSize;
    public final int maxBlocks;

    public RandomConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.enable = enable();
        this.blockSize = blockSize();
        this.maxBlocks = maxBlocks();
    }

    private boolean enable() {
        return conf.getBoolean(namespace + ".enable", false);
    }

    private int blockSize() {
        return conf.getInt(namespace + ".blockSize", 64 * 1024);
    }

    private int maxBlocks() {
        return conf.getInt(namespace + ".maxBlocks", 100);
    }
}
