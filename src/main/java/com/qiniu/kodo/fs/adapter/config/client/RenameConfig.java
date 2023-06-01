package com.qiniu.kodo.fs.adapter.config.client;

import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;
import com.qiniu.kodo.fs.adapter.config.client.base.ListAndBatchBaseConfig;

public class RenameConfig extends ListAndBatchBaseConfig {
    public RenameConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
    }
}
