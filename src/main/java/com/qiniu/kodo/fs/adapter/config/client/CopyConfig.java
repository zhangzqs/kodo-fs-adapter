package com.qiniu.kodo.fs.adapter.config.client;

import com.qiniu.kodo.fs.adapter.config.IQiniuConfiguration;
import com.qiniu.kodo.fs.adapter.config.client.base.ListAndBatchBaseConfig;

public class CopyConfig extends ListAndBatchBaseConfig {
    public CopyConfig(IQiniuConfiguration conf, String namespace) {
        super(conf, namespace);
    }
}
