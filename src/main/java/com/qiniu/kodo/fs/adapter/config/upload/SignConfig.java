package com.qiniu.kodo.fs.adapter.config.upload;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.config.IQiniuConfiguration;

public class SignConfig extends AConfigBase {
    public final int expires;

    public SignConfig(IQiniuConfiguration conf, String namespace) {
        super(conf, namespace);
        this.expires = expires();
    }

    /**
     * 下载签名过期时间
     */
    private int expires() {
        return conf.getInt(namespace + ".expires", 7 * 24 * 3600);
    }
}
