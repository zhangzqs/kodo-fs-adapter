package com.qiniu.kodo.fs.adapter.config.download;

import com.qiniu.kodo.fs.adapter.config.AConfigBase;
import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class SignConfig extends AConfigBase {
    public final boolean enable;
    public final int expires;

    public SignConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.enable = enable();
        this.expires = expires();
    }


    /**
     * 下载文件是否使用签名
     */
    private boolean enable() {
        return conf.getBoolean(namespace + ".enable", true);
    }

    /**
     * 下载签名过期时间
     */
    private int expires() {
        return conf.getInt(namespace + ".expires", 3 * 60);
    }

}
