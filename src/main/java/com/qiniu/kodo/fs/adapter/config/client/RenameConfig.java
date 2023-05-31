package com.qiniu.kodo.fs.adapter.config.client;

import com.qiniu.kodo.fs.adapter.config.IQiniuConfiguration;
import com.qiniu.kodo.fs.adapter.config.client.base.ListAndBatchBaseConfig;

public class RenameConfig extends ListAndBatchBaseConfig {
    public RenameConfig(IQiniuConfiguration conf, String namespace) {
        super(conf, namespace);
    }
}
