package com.qiniu.kodo.fs.adapter.config.client.base;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class ListAndBatchBaseConfig extends AConfigBase {
    public final ListProducerConfig listProducer;
    public final BatchConsumerConfig batchConsumer;

    public ListAndBatchBaseConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.listProducer = listProducer();
        this.batchConsumer = batchConsumer();
    }

    protected ListProducerConfig listProducer() {
        return new ListProducerConfig(conf, namespace + ".producer");
    }

    protected BatchConsumerConfig batchConsumer() {
        return new BatchConsumerConfig(conf, namespace + ".consumer");
    }

}
