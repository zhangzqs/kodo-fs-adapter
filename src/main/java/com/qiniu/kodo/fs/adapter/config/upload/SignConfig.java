package com.qiniu.kodo.fs.adapter.config.upload;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class SignConfig extends AConfigBase {
    public final int expires;

    public SignConfig(IKVConfiguration conf, String namespace) {
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
