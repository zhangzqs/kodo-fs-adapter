package com.qiniu.kodo.fs.adapter.config.client;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;
import com.qiniu.kodo.fs.adapter.config.client.base.ListProducerConfig;

public class ClientConfig extends AConfigBase {
    public final int nThread;
    public final CacheConfig cache;
    public final ListProducerConfig list;
    public final CopyConfig copy;
    public final DeleteConfig delete;
    public final RenameConfig rename;

    public ClientConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.cache = cache();
        this.list = list();
        this.copy = copy();
        this.delete = delete();
        this.rename = rename();
        this.nThread = nThread();
    }

    private int nThread() {
        return conf.getInt(namespace + ".nThread", 16);
    }

    private CacheConfig cache() {
        return new CacheConfig(conf, namespace + ".cache");
    }

    private ListProducerConfig list() {
        return new ListProducerConfig(conf, namespace + ".list");
    }

    private CopyConfig copy() {
        return new CopyConfig(conf, namespace + ".copy");
    }

    private DeleteConfig delete() {
        return new DeleteConfig(conf, namespace + ".delete");
    }

    private RenameConfig rename() {
        return new RenameConfig(conf, namespace + ".rename");
    }
}
