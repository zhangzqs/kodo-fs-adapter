package com.qiniu.kodo.fs.adapter.config.client;

import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;
import com.qiniu.kodo.fs.adapter.config.client.base.ListAndBatchBaseConfig;

public class DeleteConfig extends ListAndBatchBaseConfig {
    public DeleteConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
    }
}
