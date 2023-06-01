package com.qiniu.kodo.fs.adapter.config;

import com.qiniu.kodo.fs.adapter.util.IKVConfiguration;

public class LoggerConfig extends AConfigBase {
    public final String level;

    public LoggerConfig(IKVConfiguration conf, String namespace) {
        super(conf, namespace);
        this.level = level();
    }


    public String level() {
        return conf.get(namespace + ".level", "INFO");
    }
}
