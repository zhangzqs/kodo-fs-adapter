package com.qiniu.kodo.fs.adapter.config.client;

import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;
import com.qiniu.kodo.fs.adapter.config.client.base.ListAndBatchBaseConfig;

public class CopyConfig extends ListAndBatchBaseConfig {
    public CopyConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
    }
}
