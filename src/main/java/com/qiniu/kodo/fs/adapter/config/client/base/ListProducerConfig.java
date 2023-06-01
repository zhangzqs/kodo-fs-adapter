package com.qiniu.kodo.fs.adapter.config.client.base;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class ListProducerConfig extends AConfigBase {
    public final boolean useListV2;
    public final int singleRequestLimit;
    public final int bufferSize;
    public final int offerTimeout;

    public ListProducerConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.useListV2 = useListV2();
        this.singleRequestLimit = singleRequestLimit();
        this.bufferSize = bufferSize();
        this.offerTimeout = offerTimeout();
    }

    protected boolean useListV2() {
        return conf.getBoolean(namespace + ".useListV2", false);
    }

    protected int singleRequestLimit() {
        return conf.getInt(namespace + ".singleRequestLimit", 500);
    }

    protected int bufferSize() {
        return conf.getInt(namespace + ".bufferSize", 1000);
    }

    protected int offerTimeout() {
        return conf.getInt(namespace + ".offerTimeout", 10);
    }
}
