package com.qiniu.kodo.fs.adapter.config.client.base;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class BatchConsumerConfig extends AConfigBase {

    public final int bufferSize;
    public final int count;
    public final int singleBatchRequestLimit;
    public final int pollTimeout;

    public BatchConsumerConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.bufferSize = bufferSize();
        this.count = count();
        this.singleBatchRequestLimit = singleBatchRequestLimit();
        this.pollTimeout = pollTimeout();
    }

    protected int bufferSize() {
        return conf.getInt(namespace + ".bufferSize", 1000);
    }

    protected int count() {
        return conf.getInt(namespace + ".count", 4);
    }

    protected int singleBatchRequestLimit() {
        return conf.getInt(namespace + ".singleBatchRequestLimit", 200);
    }

    protected int pollTimeout() {
        return conf.getInt(namespace + ".pollTimeout", 10);
    }

}
