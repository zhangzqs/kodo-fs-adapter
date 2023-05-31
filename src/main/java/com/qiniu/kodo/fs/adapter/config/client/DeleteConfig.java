package com.qiniu.kodo.fs.adapter.config.client;

import com.qiniu.kodo.fs.adapter.config.IQiniuConfiguration;
import com.qiniu.kodo.fs.adapter.config.client.base.ListAndBatchBaseConfig;

public class DeleteConfig extends ListAndBatchBaseConfig {
    public DeleteConfig(IQiniuConfiguration conf, String namespace) {
        super(conf, namespace);
    }
}
