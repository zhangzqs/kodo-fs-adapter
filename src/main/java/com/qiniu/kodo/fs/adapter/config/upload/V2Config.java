package com.qiniu.kodo.fs.adapter.config.upload;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class V2Config extends AConfigBase {
    public final boolean enable;
    public final int blockSize;

    public V2Config(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.enable = enable();
        this.blockSize = blockSize();
    }

    public boolean enable() {
        return conf.getBoolean(namespace + ".enable", true);
    }

    public int blockSize() {
        return conf.getInt(namespace + ".blockSize", 32 * 1024 * 1024);
    }
}
